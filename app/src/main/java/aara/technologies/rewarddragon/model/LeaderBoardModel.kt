package aara.technologies.rewarddragon.model

data class LeaderBoardModel(
    val email: String,
    val employee_name: String,
    val earned_point: Int,
    val user_profile_id: Int,
    val user_image: String
)