package aara.technologies.rewarddragon.model

data class KpiModel(
    val created_at: String,
    val id: Int,
    val industry_work_type_id: Int,
    val name: String,
    val kpi_type:String,
    val kpi_unit:String,
    val updated_at: String,
    var rule: String = "",
    var point: String = "",
    var isChecked: Boolean,
    val is_time: Int
)