package aara.technologies.rewarddragon.testing.Model


import com.google.gson.annotations.SerializedName

data class RewardPointsRes(
    @SerializedName("earned_point")
    val earnedPoint: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("point_used")
    val pointUsed: Int,
    @SerializedName("response_code")
    val responseCode: Int
)