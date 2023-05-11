package aara.technologies.rewarddragon.testing.Model


import com.google.gson.annotations.SerializedName

data class CheckUserLoginRes(
    @SerializedName("message")
    val message: String,
    @SerializedName("response_code")
    val responseCode: Int,
    @SerializedName("status")
    val status: Int
)