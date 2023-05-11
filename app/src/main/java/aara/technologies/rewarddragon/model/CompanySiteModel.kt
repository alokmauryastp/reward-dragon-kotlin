package aara.technologies.rewarddragon.model

data class CompanySiteModel(
    val created_at: String,
    val id: Int,
    val site_url: String,
    val status: Int,
    val updated_at: String,
    val image_data: String,
    val organization_id:String,
    val title: String
)