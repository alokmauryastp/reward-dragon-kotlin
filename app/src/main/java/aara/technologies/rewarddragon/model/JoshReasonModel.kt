package aara.technologies.rewarddragon.model

import com.google.gson.annotations.SerializedName


data class JoshReasonModel (

    @SerializedName("id"          ) var id         : Int?    = null,
    @SerializedName("created_at"  ) var createdAt  : String? = null,
    @SerializedName("updated_at"  ) var updatedAt  : String? = null,
    @SerializedName("reason_name" ) var reasonName : String? = null,
    @SerializedName("status"      ) var status     : Int?    = null

)