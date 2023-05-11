package aara.technologies.rewarddragon.testing.Model


import com.google.gson.annotations.SerializedName

data class GetWellBeingListRes(
    @SerializedName("heart_points_count")
    val heartPointsCount: Int,
    @SerializedName("learning_hours")
    val learningHours: Int,
    @SerializedName("meditation_hours")
    val meditationHours: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("response_code")
    val responseCode: Int,
    @SerializedName("steps_count")
    val stepsCount: Int,
    @SerializedName("wellbeing_percent")
    val wellbeingPercent: Double
)