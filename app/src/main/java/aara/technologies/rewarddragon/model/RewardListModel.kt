package aara.technologies.rewarddragon.model

import com.google.gson.annotations.SerializedName

data class RewardListModel(
    @SerializedName("email")
    val email: String,

    @SerializedName("user_profile_id")
    var employee_id: Int,

    @SerializedName("employee_name")
    val employee_name: String,

    @SerializedName("user_image")
    val user_image: String,

    @SerializedName("earned_point")
    val earned_point: Int
)