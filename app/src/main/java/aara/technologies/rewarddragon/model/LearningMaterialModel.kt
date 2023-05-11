package aara.technologies.rewarddragon.model

data class LearningMaterialModel(
    val created_at: String,
    val id: Int,
    val learning_site_url: String,
    val status: Int,
    val updated_at: String,
    val image_data: String,
    val title: String
)