package aara.technologies.rewarddragon.model

data class NotificationModel(
    val activity: String,
    val created_at: String,
    val from_user_id: Int,
    val heading: String,
    val id: Int,
    val is_read: Boolean,
    val redirectional_code: String,
    val to_user_id: Int,
    val updated_at: String,
    var is_selected: Boolean
)