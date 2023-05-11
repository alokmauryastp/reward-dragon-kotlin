package aara.technologies.rewarddragon.model

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class Organization(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("organization_name") var organizationName: String? = null,
    @SerializedName("landline_country_code") var landlineCountryCode: String? = null,
    @SerializedName("landline_state_code") var landlineStateCode: String? = null,
    @SerializedName("landline_number") var landlineNumber: String? = null,
    @SerializedName("mobile_country_code") var mobileCountryCode: String? = null,
    @SerializedName("organization_code") var organizationCode: String? = null,
    @SerializedName("unique_code") var uniqueCode: String? = null,
    @SerializedName("mobile_number") var mobileNumber: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("country_id") var countryId: Int? = null,
    @SerializedName("state_id") var stateId: Int? = null,
    @SerializedName("city_id") var cityId: Int? = null,
    @SerializedName("pincode") var pincode: String? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("company_image") var organization: String? = null,
    @SerializedName("approved_status") var approvedStatus: Int? = null,
    @SerializedName("json_field") var jsonField: JSONObject? = null,
    @SerializedName("sponsor_name") var sponsorName: String? = null,
    @SerializedName("sponsor_email_id") var sponsorEmailId: String? = null

)

