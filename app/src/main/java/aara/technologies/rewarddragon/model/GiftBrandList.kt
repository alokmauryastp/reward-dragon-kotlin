package aara.technologies.rewarddragon.model


import com.google.gson.annotations.SerializedName

data class GiftBrandList(
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)

