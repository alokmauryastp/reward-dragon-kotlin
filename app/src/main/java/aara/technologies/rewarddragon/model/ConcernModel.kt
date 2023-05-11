package aara.technologies.rewarddragon.model

import com.google.gson.annotations.SerializedName


data class ConcernModel(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("action_owner_id") var actionOwnerId: Int? = null,
    @SerializedName("comment") var comment: String? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("concern_category") var concernCategory: Int? = null,
    @SerializedName("user_profile") var userProfile: Int? = null,
    @SerializedName("ticket") var ticket: String? = null,
    @SerializedName("action_owner_name") var action_owner_name: String? = null


)