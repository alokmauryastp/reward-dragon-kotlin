package aara.technologies.rewarddragon.testing.Model


import com.google.gson.annotations.SerializedName

data class HomePageVoucherRes(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("response_code")
    val response_code: Int
) {
    data class Data(
        @SerializedName("brand")
        val brand: String,
        @SerializedName("brand_external_url")
        val brandExternalUrl: String,
        @SerializedName("categories")
        val categories: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("how_to_redeem")
        val howToRedeem: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("image")
        val image: String,
        @SerializedName("important_information")
        val importantInformation: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("product_voucher_id")
        val productVoucherId: Int,
        @SerializedName("redeem_mode")
        val redeemMode: String,
        @SerializedName("summary")
        val summary: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("validity")
        val validity: String,
        @SerializedName("vouchers")
        val vouchers: List<Voucher>,
        @SerializedName("is_added_wishlist") var is_added_wishlist: Int,

        )

    data class Voucher(
        @SerializedName("amount")
        val amount: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("product_voucher_id")
        val productVoucherId: Int,
        @SerializedName("quantity")
        val quantity: Int,
        @SerializedName("redeem_value")
        val redeemValue: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("voucher_amount_id")
        val voucherAmountId: Int
    )
}