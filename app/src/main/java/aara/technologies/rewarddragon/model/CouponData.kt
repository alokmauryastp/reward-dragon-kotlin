package aara.technologies.rewarddragon.model


import com.google.gson.annotations.SerializedName

 data class CouponData(

    @SerializedName("my_coupons") var myCoupons: ArrayList<CouponDataItem> = arrayListOf(),
    @SerializedName("message") var message: String? = null,
    @SerializedName("response_code") var responseCode: Int? = null
){
     data class CouponDataItem(
         @SerializedName("coupon_name") var couponName: String? = null,
         @SerializedName("coupon_validity") var couponValidity: String? = null,
         @SerializedName("coupon_description") var couponDescription: String? = null,
         @SerializedName("coupon_image") var couponImage: String? = null,
         @SerializedName("coupon_code") var couponCode: String? = null
     )
 }

