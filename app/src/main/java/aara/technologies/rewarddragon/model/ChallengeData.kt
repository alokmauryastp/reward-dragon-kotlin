package aara.technologies.rewarddragon.model

import com.google.gson.annotations.SerializedName


data class ChallengeData(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("manager_id") var managerId: Int? = null,
    @SerializedName("customer_accepted_id") var customerAcceptedId: String? = null,
    @SerializedName("challenge_purpose_id") var challengePurposeId: Int? = null,
    @SerializedName("challenge_name") var challengeName: String? = null,
    @SerializedName("start_time") var startTime: String? = null,
    @SerializedName("end_time") var endTime: String? = null,
    @SerializedName("activity_details") var activityDetails: String? = null,
    @SerializedName("bonus_point") var bonusPoint: Any ,
    @SerializedName("is_broadcasted") var isBroadcasted: Int? = null,
    @SerializedName("is_accepted") var isAccepted: Int? = null,
    @SerializedName("is_completed_by_customer") var isCompletedByCustomer: Int? = null,
    @SerializedName("is_completed_by_manager") var isCompletedByManager: Int? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("purpose_name") var purposeName: String? = null,
    @SerializedName("kpi_name") var kpiName: String? = null,
    @SerializedName("participation_percent") var participationPercent: Any ,
    @SerializedName("win_percent") var winPercent: Any
)
