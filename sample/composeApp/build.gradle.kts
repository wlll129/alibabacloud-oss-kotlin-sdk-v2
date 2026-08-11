import android.databinding.tool.ext.capitalizeUS
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
}

kotlin {
    jvmToolchain(17)

    androidTarget()
//    jvm()
//    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
//    wasmJs {
//        browser {
//            commonWebpackConfig {
//                outputFileName = "composeApp.js"
//            }
//        }
//        binaries.executable()
//    }
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "composeApp"
            isStatic = true
        }
    }

    listOf(
        ohosArm64(),   // 真机 arm64
//        ohosX64()      // 模拟器/开发机 x64
    ).forEach { ohosTarget ->
        ohosTarget.binaries.sharedLib {
            baseName = "kn"
            // Release 链接阶段的 DevirtualizationAnalysis 对这个 sample 的内存占用过高，容易在 OOM 后失败。
            if (buildType == NativeBuildType.RELEASE) {
                optimized = false
            }
            export(libs.compose.multiplatform.export)
            linkerOpts("-lz")
            // 渲染模式
            // 背景：当 libkn.so 为旧编译产物时，其 DT_NEEDED 可能缺少以下库（正确构建时
            // NativeTasksConfiguration.kt 已通过 -l 选项将它们写入 DT_NEEDED）。
            // 在 build.gradle.kts 中统一补全，避免在 CMakeLists.txt 中硬编码。
            val rendererBackend = rootProject.findProperty("rendererBackend")?.toString() ?: "fusion-renderer"
            if (rendererBackend == "fusion-renderer") {
                linkerOpts(
                    "-lnative_drawing",    // OH_Drawing_*（字体、绘制）
                    "-limage_source",       // OH_ImageSourceNative_*（图像解码）
                    "-lpixelmap",           // OH_PixelMap_*
                    "-lpixelmap_ndk.z",     // OH_PixelMapNdk_*
                    "-lnative_window",      // OH_NativeWindow_*
                    "-lace_napi.z",         // N-API
                    "-lhilog_ndk.z",        // HiLog 日志
                    "-lhitrace_ndk.z",      // HiTrace 性能追踪
                    "-luv",                 // libuv 事件循环
                    "-lunwind",             // 栈展开
                    "-licu",               // ICU 文本处理
                )
            }
        }
        ohosTarget.compilations.getByName("main") {
//            val resource by cinterops.creating {
//                defFile(file("src/ohosMain/cinterop/resource.def"))
//                includeDirs(file("src/ohosMain/cinterop/include"))
//            }
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(libs.kotlinx.io.core)
            implementation(project(":oss-sdk"))
        }

        androidMain.dependencies {
            implementation(libs.androidx.activityCompose)
        }

//        jvmMain.dependencies {
//            implementation(compose.desktop.currentOs)
//        }
//
//        wasmJsMain.dependencies {
//            implementation(libs.ktor.client.js)
//        }

        val ohosArm64Main by getting {
            dependencies {
                api(libs.compose.multiplatform.export)
            }
        }
    }
}

android {
    namespace = "sample.app"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        targetSdk = 36

        applicationId = "sample.app.androidApp"
        versionCode = 1
        versionName = "1.0.0"
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "sample"
            packageVersion = "1.0.0"
        }
    }
}

arrayOf("debug", "release").forEach { type ->
    tasks.register<Copy>("publish${type.capitalizeUS()}BinariesToHarmonyApp") {
        group = "harmony" // 归类到harmony任务组
        dependsOn("link${type.capitalizeUS()}SharedOhosArm64")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        into(rootProject.project("sample").file("harmonyApp"))
        from("build/bin/ohosArm64/${type}Shared/libkn_api.h") {
            into("entry/src/main/cpp/include/arm64-v8a/")
        }
        from(project.file("build/bin/ohosArm64/${type}Shared/libkn.so")) {
            into("entry/libs/arm64-v8a/")
        }
//        from("build/bin/ohosX64/${type}Shared/libkn_api.h") {
//            into("entry/src/main/cpp/include/x86_64/")
//        }
//        from(project.file("build/bin/ohosX64/${type}Shared/libkn.so")) {
//            into("entry/libs/x86_64/")
//        }
        val composeResourcePackage = "${rootProject.name}.${project.name.lowercase()}.generated.resources"
        from("src/commonMain/composeResources") {
            into("entry/src/main/resources/rawfile/composeResources/$composeResourcePackage/")
        }
    }
}
