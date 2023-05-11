package aara.technologies.rewarddragon.model

data class KpiPerformanceModel(
    val kpi_name: String,
    val total_actual: String,
    val total_gap: String,
    val total_target: String,
    val type: String
)