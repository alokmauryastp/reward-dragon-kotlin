package aara.technologies.rewarddragon.model

data class TeamLeaderboardModel(
    val created_at: String,
    val earned_point: Int,
    val first_name: String,
    val id: Int,
    val last_name: String,
    val manager_id: Int,
    val point_balance: Int,
    val point_used: Int,
    val updated_at: String,
    val user_profile_id: Int
)