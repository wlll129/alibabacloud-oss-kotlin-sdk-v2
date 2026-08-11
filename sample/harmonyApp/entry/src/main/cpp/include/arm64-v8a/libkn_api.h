#ifndef KONAN_LIBKN_H
#define KONAN_LIBKN_H
#ifdef __cplusplus
extern "C" {
#endif
#ifdef __cplusplus
typedef bool            libkn_KBoolean;
#else
typedef _Bool           libkn_KBoolean;
#endif
typedef unsigned short     libkn_KChar;
typedef signed char        libkn_KByte;
typedef short              libkn_KShort;
typedef int                libkn_KInt;
typedef long long          libkn_KLong;
typedef unsigned char      libkn_KUByte;
typedef unsigned short     libkn_KUShort;
typedef unsigned int       libkn_KUInt;
typedef unsigned long long libkn_KULong;
typedef float              libkn_KFloat;
typedef double             libkn_KDouble;
typedef float __attribute__ ((__vector_size__ (16))) libkn_KVector128;
typedef void*              libkn_KNativePtr;
struct libkn_KType;
typedef struct libkn_KType libkn_KType;

typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_kotlin_Byte;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_kotlin_Short;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_kotlin_Int;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_kotlin_Long;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_kotlin_Float;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_kotlin_Double;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_kotlin_Char;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_kotlin_Boolean;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_kotlin_Unit;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_kotlin_UByte;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_kotlin_UShort;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_kotlin_UInt;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_kotlin_ULong;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_kotlin_Function1;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_kotlinx_coroutines_CoroutineDispatcher;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_sample_app_PickedFile;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_kotlin_coroutines_SuspendFunction0;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_sample_app_Service;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_com_aliyun_kotlin_sdk_service_oss2_OSSClient;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_sample_app_Data;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_sample_app_Data_Image;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_kotlin_ByteArray;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_kotlin_Any;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_sample_app_Data_Text;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_sample_app_Data_Other;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_sample_app_PresignType;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_sample_app_PresignType_PUT;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_sample_app_PresignType_GET;
typedef struct {
  libkn_KNativePtr pinned;
} libkn_kref_sample_app_PresignType_HEAD;

extern libkn_KInt kn_get_render_backend_id();
extern void InitJsRenderNodeContext(void* env, void* nodeConstructor, void* statusModifyConstructor, libkn_KDouble ratio, libkn_KBoolean fixed);
extern void androidx_compose_ui_arkui_ArkUIViewController_aboutToAppear(void* controllerRef);
extern void androidx_compose_ui_arkui_ArkUIViewController_aboutToDisappear(void* controllerRef);
extern void androidx_compose_ui_arkui_ArkUIViewController_cancelSyncRefresh(void* controllerRef, libkn_KInt refreshId);
extern void androidx_compose_ui_arkui_ArkUIViewController_dispatchHoverEvent(void* controllerRef);
extern void androidx_compose_ui_arkui_ArkUIViewController_dispatchMouseEvent(void* controllerRef);
extern libkn_KBoolean androidx_compose_ui_arkui_ArkUIViewController_dispatchTouchEvent(void* controllerRef, void* nativeTouchEvent, libkn_KBoolean ignoreInteropView);
extern void AndroidxComposeUiArkuiArkUiViewControllerDraw(void* controllerRef, void* canvas);
extern libkn_KInt androidx_compose_ui_arkui_ArkUIViewController_findNodeIdAt(void* controllerRef, libkn_KFloat x, libkn_KFloat y);
extern const char* androidx_compose_ui_arkui_ArkUIViewController_getId(void* controllerRef);
extern void* AndroidxComposeUiArkuiArkUiViewControllerGetJsNode(void* controllerRef);
extern libkn_KInt androidx_compose_ui_arkui_ArkUIViewController_getRendererTypeId();
extern void* androidx_compose_ui_arkui_ArkUIViewController_getXComponentRender(void* controllerRef);
extern void androidx_compose_ui_arkui_ArkUIViewController_initFusionRendererNode(void* controllerRef, libkn_KBoolean enableCApi, void* rootContent, void* frameMgr);
extern void androidx_compose_ui_arkui_ArkUIViewController_keyboardWillHide(void* controllerRef);
extern void androidx_compose_ui_arkui_ArkUIViewController_keyboardWillShow(void* controllerRef, libkn_KFloat keyboardHeight);
extern libkn_KBoolean androidx_compose_ui_arkui_ArkUIViewController_onBackPress(void* controllerRef);
extern void androidx_compose_ui_arkui_ArkUIViewController_onConfigurationUpdate(void* controllerRef);
extern void androidx_compose_ui_arkui_ArkUIViewController_onFinalize(void* controllerRef);
extern void androidx_compose_ui_arkui_ArkUIViewController_onFocusEvent(void* controllerRef);
extern void androidx_compose_ui_arkui_ArkUIViewController_onFrame(void* controllerRef, libkn_KLong timestamp, libkn_KLong targetTimestamp);
extern void AndroidXComposeUIArkUIArkUIViewControllerOnIdle(void* controllerRef, libkn_KLong timeLeft);
extern void androidx_compose_ui_arkui_ArkUIViewController_onKeyEvent(void* controllerRef);
extern void androidx_compose_ui_arkui_ArkUIViewController_onPageHide(void* controllerRef);
extern void androidx_compose_ui_arkui_ArkUIViewController_onPageShow(void* controllerRef);
extern void androidx_compose_ui_arkui_ArkUIViewController_onSurfaceChanged(void* controllerRef, libkn_KInt width, libkn_KInt height);
extern void androidx_compose_ui_arkui_ArkUIViewController_onSurfaceCreated(void* controllerRef, void* xcomponentPtr, libkn_KInt width, libkn_KInt height);
extern void androidx_compose_ui_arkui_ArkUIViewController_onSurfaceDestroyed(void* controllerRef);
extern void androidx_compose_ui_arkui_ArkUIViewController_onSurfaceHide(void* controllerRef);
extern void androidx_compose_ui_arkui_ArkUIViewController_onSurfaceShow(void* controllerRef);
extern libkn_KInt androidx_compose_ui_arkui_ArkUIViewController_requestSyncRefresh(void* controllerRef);
extern const char* androidx_compose_ui_arkui_ArkUIViewController_sendMessage(void* controllerRef, const char* type, const char* message);
extern void androidx_compose_ui_arkui_ArkUIViewController_setContext(void* controllerRef, void* context);
extern void androidx_compose_ui_arkui_ArkUIViewController_setEnv(void* controllerRef, void* env);
extern void androidx_compose_ui_arkui_ArkUIViewController_setId(void* controllerRef, const char* id);
extern void androidx_compose_ui_arkui_ArkUIViewController_setLocaleAndStringProvider(void* controllerRef, void* provider);
extern void androidx_compose_ui_arkui_ArkUIViewController_setMessenger(void* controllerRef, void* messenger);
extern void androidx_compose_ui_arkui_ArkUIViewController_setRendererBackendId(void* controllerRef, libkn_KInt backendId);
extern void androidx_compose_ui_arkui_ArkUIViewController_setRootView(void* controllerRef, void* backRootView, void* foreRootView, void* touchableRootView);
extern void androidx_compose_ui_arkui_ArkUIViewController_setUIContext(void* controllerRef, void* uiContext);
extern void androidx_compose_ui_arkui_ArkUIViewController_setXComponentRender(void* controllerRef, void* render);
extern void androidx_compose_ui_platform_LowMemoryMonitor_notifyLowMemoryFromHost(libkn_KInt rawLevel);
extern libkn_KFloat androidx_compose_ui_arkui_ArkUIViewController_consumeOuterScroll(void* controllerRef, libkn_KFloat offsetVp, libkn_KInt sourceInt);
extern void androidx_compose_ui_arkui_ArkUIViewController_setParentScrollBridge(void* controllerRef, void* scroller);
extern void androidx_compose_ui_arkui_init(void* env, void* exports);
extern void* MainArkUIViewController(void* env);

typedef struct {
  /* Service functions. */
  void (*DisposeStablePointer)(libkn_KNativePtr ptr);
  void (*DisposeString)(const char* string);
  libkn_KBoolean (*IsInstance)(libkn_KNativePtr ref, const libkn_KType* type);
  libkn_kref_kotlin_Byte (*createNullableByte)(libkn_KByte);
  libkn_KByte (*getNonNullValueOfByte)(libkn_kref_kotlin_Byte);
  libkn_kref_kotlin_Short (*createNullableShort)(libkn_KShort);
  libkn_KShort (*getNonNullValueOfShort)(libkn_kref_kotlin_Short);
  libkn_kref_kotlin_Int (*createNullableInt)(libkn_KInt);
  libkn_KInt (*getNonNullValueOfInt)(libkn_kref_kotlin_Int);
  libkn_kref_kotlin_Long (*createNullableLong)(libkn_KLong);
  libkn_KLong (*getNonNullValueOfLong)(libkn_kref_kotlin_Long);
  libkn_kref_kotlin_Float (*createNullableFloat)(libkn_KFloat);
  libkn_KFloat (*getNonNullValueOfFloat)(libkn_kref_kotlin_Float);
  libkn_kref_kotlin_Double (*createNullableDouble)(libkn_KDouble);
  libkn_KDouble (*getNonNullValueOfDouble)(libkn_kref_kotlin_Double);
  libkn_kref_kotlin_Char (*createNullableChar)(libkn_KChar);
  libkn_KChar (*getNonNullValueOfChar)(libkn_kref_kotlin_Char);
  libkn_kref_kotlin_Boolean (*createNullableBoolean)(libkn_KBoolean);
  libkn_KBoolean (*getNonNullValueOfBoolean)(libkn_kref_kotlin_Boolean);
  libkn_kref_kotlin_Unit (*createNullableUnit)(void);
  libkn_kref_kotlin_UByte (*createNullableUByte)(libkn_KUByte);
  libkn_KUByte (*getNonNullValueOfUByte)(libkn_kref_kotlin_UByte);
  libkn_kref_kotlin_UShort (*createNullableUShort)(libkn_KUShort);
  libkn_KUShort (*getNonNullValueOfUShort)(libkn_kref_kotlin_UShort);
  libkn_kref_kotlin_UInt (*createNullableUInt)(libkn_KUInt);
  libkn_KUInt (*getNonNullValueOfUInt)(libkn_kref_kotlin_UInt);
  libkn_kref_kotlin_ULong (*createNullableULong)(libkn_KULong);
  libkn_KULong (*getNonNullValueOfULong)(libkn_kref_kotlin_ULong);

  /* User functions. */
  struct {
    struct {
      struct {
        struct {
          struct {
            struct {
              struct {
                libkn_KInt (*kn_get_render_backend_id_)();
              } internal;
            } ohos;
          } compose;
        } jetbrains;
      } org;
      struct {
        struct {
          struct {
            struct {
              struct {
                void (*InitJsRenderNodeContext_)(void* env, void* nodeConstructor, void* statusModifyConstructor, libkn_KDouble ratio, libkn_KBoolean fixed);
                void (*_Export_ArkUIViewController_aboutToAppear)(void* controllerRef);
                void (*_Export_ArkUIViewController_aboutToDisappear)(void* controllerRef);
                void (*_Export_ArkUIViewController_cancelSyncRefresh)(void* controllerRef, libkn_KInt refreshId);
                void (*_Export_ArkUIViewController_dispatchHoverEvent)(void* controllerRef);
                void (*_Export_ArkUIViewController_dispatchMouseEvent)(void* controllerRef);
                libkn_KBoolean (*_Export_ArkUIViewController_dispatchTouchEvent)(void* controllerRef, void* nativeTouchEvent, libkn_KBoolean ignoreInteropView);
                void (*_Export_ArkUIViewController_draw)(void* controllerRef, void* canvas);
                libkn_KInt (*_Export_ArkUIViewController_findNodeIdAt)(void* controllerRef, libkn_KFloat x, libkn_KFloat y);
                const char* (*_Export_ArkUIViewController_getId)(void* controllerRef);
                void* (*_Export_ArkUIViewController_getJsNode)(void* controllerRef);
                libkn_KInt (*_Export_ArkUIViewController_getRendererTypeId)();
                void* (*_Export_ArkUIViewController_getXComponentRender)(void* controllerRef);
                void (*_Export_ArkUIViewController_initFusionRendererNode)(void* controllerRef, libkn_KBoolean enableCApi, void* rootContent, void* frameMgr);
                void (*_Export_ArkUIViewController_keyboardWillHide)(void* controllerRef);
                void (*_Export_ArkUIViewController_keyboardWillShow)(void* controllerRef, libkn_KFloat keyboardHeight);
                libkn_KBoolean (*_Export_ArkUIViewController_onBackPress)(void* controllerRef);
                void (*_Export_ArkUIViewController_onConfigurationUpdate)(void* controllerRef);
                void (*_Export_ArkUIViewController_onFinalize)(void* controllerRef);
                void (*_Export_ArkUIViewController_onFocusEvent)(void* controllerRef);
                void (*_Export_ArkUIViewController_onFrame)(void* controllerRef, libkn_KLong timestamp, libkn_KLong targetTimestamp);
                void (*_Export_ArkUIViewController_onIdle)(void* controllerRef, libkn_KLong timeLeft);
                void (*_Export_ArkUIViewController_onKeyEvent)(void* controllerRef);
                void (*_Export_ArkUIViewController_onPageHide)(void* controllerRef);
                void (*_Export_ArkUIViewController_onPageShow)(void* controllerRef);
                void (*_Export_ArkUIViewController_onSurfaceChanged)(void* controllerRef, libkn_KInt width, libkn_KInt height);
                void (*_Export_ArkUIViewController_onSurfaceCreated)(void* controllerRef, void* xcomponentPtr, libkn_KInt width, libkn_KInt height);
                void (*_Export_ArkUIViewController_onSurfaceDestroyed)(void* controllerRef);
                void (*_Export_ArkUIViewController_onSurfaceHide)(void* controllerRef);
                void (*_Export_ArkUIViewController_onSurfaceShow)(void* controllerRef);
                libkn_KInt (*_Export_ArkUIViewController_requestSyncRefresh)(void* controllerRef);
                const char* (*_Export_ArkUIViewController_sendMessage)(void* controllerRef, const char* type, const char* message);
                void (*_Export_ArkUIViewController_setContext)(void* controllerRef, void* context);
                void (*_Export_ArkUIViewController_setEnv)(void* controllerRef, void* env);
                void (*_Export_ArkUIViewController_setId)(void* controllerRef, const char* id);
                void (*_Export_ArkUIViewController_setLocaleAndStringProvider)(void* controllerRef, void* provider);
                void (*_Export_ArkUIViewController_setMessenger)(void* controllerRef, void* messenger);
                void (*_Export_ArkUIViewController_setRendererBackendId)(void* controllerRef, libkn_KInt backendId);
                void (*_Export_ArkUIViewController_setRootView)(void* controllerRef, void* backRootView, void* foreRootView, void* touchableRootView);
                void (*_Export_ArkUIViewController_setUIContext)(void* controllerRef, void* uiContext);
                void (*_Export_ArkUIViewController_setXComponentRender)(void* controllerRef, void* render);
                void (*_Export_LowMemoryMonitor_notifyLowMemoryFromHost)(libkn_KInt rawLevel);
                libkn_KLong (*getCurrentTimeNanos)();
                libkn_KFloat (*_Export_ArkUIViewController_consumeOuterScroll)(void* controllerRef, libkn_KFloat offsetVp, libkn_KInt sourceInt);
                void (*_Export_ArkUIViewController_setParentScrollBridge)(void* controllerRef, void* scroller);
                void (*_Export_ArkUIViewInitializer_init)(void* env, void* exports);
              } arkui;
            } ui;
          } export_;
        } compose;
      } androidx;
      struct {
        struct {
          struct {
            libkn_KType* (*_type)(void);
            libkn_kref_sample_app_PickedFile (*PickedFile)(const char* name, libkn_kref_kotlin_coroutines_SuspendFunction0 reader);
            const char* (*get_name)(libkn_kref_sample_app_PickedFile thiz);
          } PickedFile;
          struct {
            libkn_KType* (*_type)(void);
            libkn_kref_sample_app_Service (*Service)(libkn_kref_com_aliyun_kotlin_sdk_service_oss2_OSSClient client);
            libkn_kref_com_aliyun_kotlin_sdk_service_oss2_OSSClient (*get_client)(libkn_kref_sample_app_Service thiz);
            void (*set_client)(libkn_kref_sample_app_Service thiz, libkn_kref_com_aliyun_kotlin_sdk_service_oss2_OSSClient set);
          } Service;
          struct {
            struct {
              libkn_KType* (*_type)(void);
              libkn_kref_sample_app_Data_Image (*Image)(libkn_kref_kotlin_ByteArray bytes);
              libkn_kref_kotlin_ByteArray (*get_bytes)(libkn_kref_sample_app_Data_Image thiz);
              libkn_kref_kotlin_ByteArray (*component1)(libkn_kref_sample_app_Data_Image thiz);
              libkn_kref_sample_app_Data_Image (*copy)(libkn_kref_sample_app_Data_Image thiz, libkn_kref_kotlin_ByteArray bytes);
              libkn_KBoolean (*equals)(libkn_kref_sample_app_Data_Image thiz, libkn_kref_kotlin_Any other);
              libkn_KInt (*hashCode)(libkn_kref_sample_app_Data_Image thiz);
              const char* (*toString)(libkn_kref_sample_app_Data_Image thiz);
            } Image;
            struct {
              libkn_KType* (*_type)(void);
              libkn_kref_sample_app_Data_Text (*Text)(const char* bytes);
              const char* (*get_bytes)(libkn_kref_sample_app_Data_Text thiz);
              const char* (*component1)(libkn_kref_sample_app_Data_Text thiz);
              libkn_kref_sample_app_Data_Text (*copy)(libkn_kref_sample_app_Data_Text thiz, const char* bytes);
              libkn_KBoolean (*equals)(libkn_kref_sample_app_Data_Text thiz, libkn_kref_kotlin_Any other);
              libkn_KInt (*hashCode)(libkn_kref_sample_app_Data_Text thiz);
              const char* (*toString)(libkn_kref_sample_app_Data_Text thiz);
            } Text;
            struct {
              libkn_KType* (*_type)(void);
              libkn_kref_sample_app_Data_Other (*Other)(libkn_kref_kotlin_Any bytes);
              libkn_kref_kotlin_Any (*get_bytes)(libkn_kref_sample_app_Data_Other thiz);
              libkn_kref_kotlin_Any (*component1)(libkn_kref_sample_app_Data_Other thiz);
              libkn_kref_sample_app_Data_Other (*copy)(libkn_kref_sample_app_Data_Other thiz, libkn_kref_kotlin_Any bytes);
              libkn_KBoolean (*equals)(libkn_kref_sample_app_Data_Other thiz, libkn_kref_kotlin_Any other);
              libkn_KInt (*hashCode)(libkn_kref_sample_app_Data_Other thiz);
              const char* (*toString)(libkn_kref_sample_app_Data_Other thiz);
            } Other;
            libkn_KType* (*_type)(void);
            libkn_kref_sample_app_Data (*Data)();
          } Data;
          struct {
            struct {
              libkn_kref_sample_app_PresignType (*get)(); /* enum entry for PUT. */
            } PUT;
            struct {
              libkn_kref_sample_app_PresignType (*get)(); /* enum entry for GET. */
            } GET;
            struct {
              libkn_kref_sample_app_PresignType (*get)(); /* enum entry for HEAD. */
            } HEAD;
            libkn_KType* (*_type)(void);
          } PresignType;
          void (*FilePicker)(libkn_kref_kotlin_Function1 onFileSelected);
          libkn_KInt (*sample_app_Data$stableprop_getter)();
          libkn_KInt (*sample_app_Data_Image$stableprop_getter)();
          libkn_KInt (*sample_app_Data_Other$stableprop_getter)();
          libkn_KInt (*sample_app_Data_Text$stableprop_getter)();
          libkn_KInt (*sample_app_PickedFile$stableprop_getter)();
          libkn_KInt (*sample_app_Service$stableprop_getter)();
          void* (*MainArkUIViewController_)(void* env);
          libkn_KInt (*sample_app_Data$stableprop_getter_)();
          libkn_KInt (*sample_app_Data_Image$stableprop_getter_)();
          libkn_KInt (*sample_app_Data_Other$stableprop_getter_)();
          libkn_KInt (*sample_app_Data_Text$stableprop_getter_)();
          libkn_KInt (*sample_app_PickedFile$stableprop_getter_)();
          libkn_KInt (*sample_app_Service$stableprop_getter_)();
          libkn_kref_kotlinx_coroutines_CoroutineDispatcher (*get_ioDispatcher)();
          libkn_KInt (*sample_app_Data$stableprop_getter__)();
          libkn_KInt (*sample_app_Data_Image$stableprop_getter__)();
          libkn_KInt (*sample_app_Data_Other$stableprop_getter__)();
          libkn_KInt (*sample_app_Data_Text$stableprop_getter__)();
          libkn_KInt (*sample_app_PickedFile$stableprop_getter__)();
          libkn_KInt (*sample_app_Service$stableprop_getter__)();
          void (*App)();
          libkn_KInt (*sample_app_Data$stableprop_getter___)();
          libkn_KInt (*sample_app_Data_Image$stableprop_getter___)();
          libkn_KInt (*sample_app_Data_Other$stableprop_getter___)();
          libkn_KInt (*sample_app_Data_Text$stableprop_getter___)();
          libkn_KInt (*sample_app_PickedFile$stableprop_getter___)();
          libkn_KInt (*sample_app_Service$stableprop_getter___)();
          libkn_KInt (*sample_app_Data$stableprop_getter____)();
          libkn_KInt (*sample_app_Data_Image$stableprop_getter____)();
          libkn_KInt (*sample_app_Data_Other$stableprop_getter____)();
          libkn_KInt (*sample_app_Data_Text$stableprop_getter____)();
          libkn_KInt (*sample_app_PickedFile$stableprop_getter____)();
          libkn_KInt (*sample_app_Service$stableprop_getter____)();
          libkn_KInt (*sample_app_Data$stableprop_getter_____)();
          libkn_KInt (*sample_app_Data_Image$stableprop_getter_____)();
          libkn_KInt (*sample_app_Data_Other$stableprop_getter_____)();
          libkn_KInt (*sample_app_Data_Text$stableprop_getter_____)();
          libkn_KInt (*sample_app_PickedFile$stableprop_getter_____)();
          libkn_KInt (*sample_app_Service$stableprop_getter_____)();
          libkn_KInt (*sample_app_Data$stableprop_getter______)();
          libkn_KInt (*sample_app_Data_Image$stableprop_getter______)();
          libkn_KInt (*sample_app_Data_Other$stableprop_getter______)();
          libkn_KInt (*sample_app_Data_Text$stableprop_getter______)();
          libkn_KInt (*sample_app_PickedFile$stableprop_getter______)();
          libkn_KInt (*sample_app_Service$stableprop_getter______)();
        } app;
      } sample;
    } root;
  } kotlin;
} libkn_ExportedSymbols;
extern libkn_ExportedSymbols* libkn_symbols(void);
#ifdef __cplusplus
}  /* extern "C" */
#endif
#endif  /* KONAN_LIBKN_H */
