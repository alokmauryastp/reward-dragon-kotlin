package aara.technologies.rewarddragon.model

import com.google.gson.annotations.SerializedName

data class VoucherModel(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("product_voucher_id") var product_voucher_id: Int? = null,
    @SerializedName("name") val name: String,
    @SerializedName("redeem_mode") val redeem_mode: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("description") var description: String,
    @SerializedName("image") var image: String,
    @SerializedName("categories") var categories: String,
    @SerializedName("brand_external_url") var brand_external_url: String,
    @SerializedName("brand") var brand: String,
    @SerializedName("how_to_redeem") var how_to_redeem: String,
    @SerializedName("important_information") var important_information: String,
    @SerializedName("validity") var validity: String,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("is_added_wishlist") var is_added_wishlist: Int,


    @SerializedName("vouchers") var vouchers: List<Vouchers>

)

class Vouchers(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("product_voucher_id") var productVoucherId: Int? = null,
    @SerializedName("voucher_amount_id") var voucherAmountId: Int? = null,
    @SerializedName("redeem_value") var redeemValue: String? = null,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("quantity") var quantity: Int? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null
)

