package aara.technologies.rewarddragon.model

data class TeamModel(
    val created_at: String,
    val id: Int,
    val manager_id: Int,
    val manager_name: String,
    val organization_id: Int,
    val team_name: String,
    val updated_at: String
)