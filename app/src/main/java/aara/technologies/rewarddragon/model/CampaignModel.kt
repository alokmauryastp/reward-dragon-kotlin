package aara.technologies.rewarddragon.model

data class CampaignModel(
    val campaign_name: String,
    val contact_person: String,
    val created_at: String,
    val customer_accepted_id: Int,
    val end_date: String,
    val id: Int,
    val industry_work_type: Int,
    val is_accepted: Int,
    val is_broadcast: Int,
    val is_completed_by_customer: Int,
    val is_completed_by_manager: Int,
    val kpi_data: List<KpiData1>,
    val purpose_name: String,
    val start_date: String,
    val updated_at: String
)

data class KpiData1(
    val created_at: String,
    val id: Int,
    val kpi_id: Int,
    val point: String,
    val rule: String,
    val updated_at: String
)