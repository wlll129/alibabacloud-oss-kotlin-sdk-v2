package com.aliyun.kotlin.sdk.service.oss2.extension.models

import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlElement
import com.aliyun.kotlin.sdk.service.oss2.serialization.xml.XmlRoot
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * The container in which the redundancy type conversion task is stored.
 */
@Serializable
@SerialName("BucketDataRedundancyTransition")
@XmlRoot
public data class BucketDataRedundancyTransition(
    /**
     * The ID of the redundancy type conversion task. The ID can be used to view and delete the redundancy type conversion task.
     */
    @XmlElement("TaskId") public var taskId: String? = null,

    /**
     * The time when the redundancy type change task was created.
     */
    @XmlElement("CreateTime") public var createTime: String? = null,

    /**
     * The time when the redundancy type change task was performed. This element is available when the task is in the Processing or Finished state.
     */
    @XmlElement("StartTime") public var startTime: String? = null,

    /**
     * The time when the redundancy type change task was finished. This element is available when the task is in the Finished state.
     */
    @XmlElement("EndTime") public var endTime: String? = null,

    /**
     * The state of the redundancy type change task. Valid values:
     * - Queueing
     * - Processing
     * - Finished
     */
    @XmlElement("Status") public var status: String? = null,

    /**
     * The estimated period of time that is required for the redundancy type change task. Unit: hours. This element is available when the task is in the Processing or Finished state.
     */
    @XmlElement("EstimatedRemainingTime") public var estimatedRemainingTime: String? = null,

    /**
     * The progress of the redundancy type change task in percentage. Valid values: 0 to 100. This element is available when the task is in the Processing or Finished state.
     */
    @XmlElement("ProcessPercentage") public var processPercentage: String? = null
) {
    public companion object {
        public operator fun invoke(builder: BucketDataRedundancyTransition.() -> Unit): BucketDataRedundancyTransition =
            BucketDataRedundancyTransition().apply(builder)
    }
}
