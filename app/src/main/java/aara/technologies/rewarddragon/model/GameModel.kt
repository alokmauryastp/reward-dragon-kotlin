package aara.technologies.rewarddragon.model

data class GameModel(
    val created_at: String,
    val game_time: String,
    val game_type: String,
    val game_url: String,
    val id: Int,
    val logo: String,
    val organization_id: Int,
    val start_time: String,
    val title: String,
    val updated_at: String,
    val points: Int,
    val purpose: String,
    val benefits: String,
)