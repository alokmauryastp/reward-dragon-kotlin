package aara.technologies.rewarddragon.model

import com.google.gson.annotations.SerializedName


data class User(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("unique_code") var uniqueCode: String? = null,
    @SerializedName("first_name") var firstName: String? = null,
    @SerializedName("last_name") var lastName: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("mobile_no") var mobileNo: String? = null,
    @SerializedName("role_id") var roleId: Int? = null,
    @SerializedName("role_name") var roleName: String? = null,
    @SerializedName("company_name") var companyName: String? = null,
    @SerializedName("organization_code") var organizationCode: String? = null,
    @SerializedName("gender") var gender: String? = null,
    @SerializedName("designation") var designation: String? = null,
    @SerializedName("base_location") var baseLocation: String? = null,
    @SerializedName("team_id") var teamId: String? = null,
    @SerializedName("team_name") var teamName: String? = null,
    @SerializedName("manager_id") var managerId: String? = null,
    @SerializedName("manager_name") var managerName: String? = null,
    @SerializedName("default_language") var defaultLanguage: String? = null,
    @SerializedName("member_since") var memberSince: String? = null,
    @SerializedName("last_active_on") var lastActiveOn: String? = null,
    @SerializedName("is_verified_by_admin") var isVerifiedByAdmin: Boolean? = null,
    @SerializedName("firebase_token") var firebaseToken: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("token") var token: String? = null,
    @SerializedName("avatar_image") var avatarImage: String? = null,
    @SerializedName("user_profile_updated_at") var userProfileUpdatedAt: String? = null,
    @SerializedName("organization") var organization: Organization? = Organization()

    )

