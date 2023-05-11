package aara.technologies.rewarddragon.model

import com.google.gson.annotations.SerializedName

data class GiftVoucher(
    @SerializedName("name")
    val category_name: String,
    @SerializedName("id")
    val id: String,
)

