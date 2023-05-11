package aara.technologies.rewarddragon.model


import com.google.gson.annotations.SerializedName

data class UnreadNotificationCount(
    @SerializedName("message")
    val message: String,
    @SerializedName("response_code")
    val responseCode: Int,
    @SerializedName("unread_notification_count")
    val unreadNotificationCount: Int
)