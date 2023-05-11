package aara.technologies.rewarddragon.model

import com.google.gson.annotations.SerializedName

class RoleModel {
    @SerializedName("role"          ) var role         : ArrayList<Role> = arrayListOf()
    @SerializedName("message"       ) var message      : String?         = null
    @SerializedName("response_code" ) var responseCode : Int?            = null
}


data class Role (

    @SerializedName("id"         ) var id        : Int?    = null,
    @SerializedName("created_at" ) var createdAt : String? = null,
    @SerializedName("updated_at" ) var updatedAt : String? = null,
    @SerializedName("role_name"  ) var roleName  : String? = null,
    @SerializedName("status"     ) var status    : Int?    = null

)