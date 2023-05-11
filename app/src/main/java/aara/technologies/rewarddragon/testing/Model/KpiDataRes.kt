package aara.technologies.rewarddragon.testing.Model


import com.google.gson.annotations.SerializedName

data class KpiDataRes(
    @SerializedName("message")
    val message: String,
    @SerializedName("response_code")
    val responseCode: Int,
    @SerializedName("total_kpi_met")
    val totalKpiMet: Int,
    @SerializedName("total_kpi_wip")
    val totalKpiWip: Int
)