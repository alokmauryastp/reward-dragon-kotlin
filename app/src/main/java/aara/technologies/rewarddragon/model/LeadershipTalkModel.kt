package aara.technologies.rewarddragon.model

import com.google.gson.annotations.SerializedName

data class LeadershipTalkModel(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("organization_id") var organizationId: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("video_url") var videoUrl: String? = null,
    @SerializedName("status") var status: Int? = null

)
