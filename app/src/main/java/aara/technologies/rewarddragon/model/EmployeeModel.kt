package aara.technologies.rewarddragon.model

data class EmployeeModel(
    val id: Int,
    val role__id: Int,
    val role__role_name: String,
    val user__first_name: String,
    val user__last_name: String
)