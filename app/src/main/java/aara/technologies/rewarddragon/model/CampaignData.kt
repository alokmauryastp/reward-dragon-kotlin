package aara.technologies.rewarddragon.model


data class CampaignData(

    val campaign_name: String,
    val created_at: String,
    val customer_accepted_id: Any,
    val end_date: String,
    val id: Int,
    val is_accepted: Int,
    val is_broadcast: Int,
    val is_completed_by_customer: Int,
    val is_completed_by_manager: Int,
    val kpi_data: List<KpiData>,
    val purpose_name: String,
    val start_date: String,
    val updated_at: String
)
