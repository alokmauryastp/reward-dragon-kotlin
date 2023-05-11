package aara.technologies.rewarddragon.testing.Model


import com.google.gson.annotations.SerializedName

data class WinLevelRes(
    @SerializedName("message")
    val message: String,
    @SerializedName("points_won")
    val pointsWon: Int,
    @SerializedName("response_code")
    val responseCode: Int,
    @SerializedName("win_level")
    val winLevel: Int
)