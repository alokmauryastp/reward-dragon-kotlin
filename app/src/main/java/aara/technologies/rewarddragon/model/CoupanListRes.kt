package aara.technologies.rewarddragon.model

import com.google.gson.annotations.SerializedName

data class CoupanListRes(
    val data: List<Datum>?,
    val totalNoPages: Int?,
    val productVouchersCount: Int?,
    val message: String?,
    val responseCode: Int?
) {

    data class Datum(

        @SerializedName("id") var id: Int? = null,
        @SerializedName("product_voucher_id") var productVoucherId: Int? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("redeem_mode") var redeemMode: String? = null,
        @SerializedName("summary") var summary: String? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("image") var image: String? = null,
        @SerializedName("categories") var categories: String? = null,
        @SerializedName("brand_external_url") var brandExternalUrl: String? = null,
        @SerializedName("brand") var brand: String? = null,
        @SerializedName("how_to_redeem") var howToRedeem: String? = null,
        @SerializedName("important_information") var importantInformation: String? = null,
        @SerializedName("validity") var validity: String? = null,
        @SerializedName("created_at") var createdAt: String? = null,
        @SerializedName("updated_at") var updatedAt: String? = null,
        @SerializedName("is_added_wishlist") var isAddedWishlist: Int? = null,
        @SerializedName("vouchers") var vouchers: ArrayList<Vouchers> = arrayListOf()

    )


    data class Voucher(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("product_voucher_id") var productVoucherId: Int? = null,
        @SerializedName("voucher_amount_id") var voucherAmountId: Int? = null,
        @SerializedName("redeem_value") var redeemValue: String? = null,
        @SerializedName("amount") var amount: String? = null,
        @SerializedName("quantity") var quantity: Int? = null,
        @SerializedName("created_at") var createdAt: String? = null,
        @SerializedName("updated_at") var updatedAt: String? = null

    )

}