package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.adapter.MyWishlistAdapter
import aara.technologies.rewarddragon.adapter.VoucherAdapter2
import aara.technologies.rewarddragon.databinding.ActivityMyCouponsBinding
import aara.technologies.rewarddragon.databinding.ShowCouponBinding
import aara.technologies.rewarddragon.model.CouponData
import aara.technologies.rewarddragon.model.VoucherModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyCoupons : AppCompatActivity(), VoucherAdapter2.RedeemReward,
    MyWishlistAdapter.VoucherDetail, MyWishlistAdapter.onClickRemoveWishList {
    var binding: ActivityMyCouponsBinding? = null

    private val TAG = "MyCoupons"
    lateinit var dialog: CustomLoader
    lateinit var context: Context
    var sharedPrefManager: SharedPrefManager? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCouponsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        context = this

        sharedPrefManager = SharedPrefManager.getInstance(this)
        dialog = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)


        Constant.loadAvatarImage(context, binding!!.imageView)

/*        viewModel?.getRewardPoints(HashMap())


        Log.e(TAG, "getRewardPoints ${Gson().toJson( viewModel?.rewardData?.value)}")
        viewModel?.rewardData?.observeForever {

            Log.e(TAG, "getRewardPoints ${Gson().toJson(it)}")

            when (it) {


                is Resource.Success -> {
                    var response = it.value

                    Log.e(TAG, "getRewardPoints $response")

                    if (response.responseCode == 200) {

                    }

                }

                is Resource.Failure -> {

                    Log.i(TAG, "rewardData: $it")

                }

                else -> Log.i(TAG, "rewardData:else  ")
            }
        }*/

        binding!!.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding!!.toolbar.toolbarTitle.text =
            sharedPrefManager!!.user.companyName
        com.bumptech.glide.Glide.with(context).load(
            sharedPrefManager!!
                .getString(aara.technologies.rewarddragon.utils.SharedPrefManager.Companion.KEY_COMPANY_IMAGE)!!
        ).into(binding!!.toolbar.companyLogo)
        binding!!.toolbar.back.setOnClickListener { finish() }

        binding!!.userNameTxt.text =
            sharedPrefManager!!.user.firstName + " " + sharedPrefManager!!.user.lastName
        binding!!.designation.text =
            sharedPrefManager!!.user.designation + ", " + sharedPrefManager!!.user.teamName


        binding!!.couponRecycler.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        getRewardPoints()

        productVoucherList()

        productVoucherWishList()

        Log.i(TAG, "onCreate: ")
    }

    private fun productVoucherList() {
        dialog.show()
        var hashmap: HashMap<String, String> = hashMapOf();
        hashmap["user_profile_id"] = sharedPrefManager!!.user.id.toString()
        Log.i(TAG, "productVoucherList: $hashmap")
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.listMyCoupon(hashmap)
        call.enqueue(object : Callback<CouponData?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<CouponData?>, response: Response<CouponData?>) {
                var couponData: CouponData? = response.body()
                Log.i(TAG, "onResponse: couponRes ${Gson().toJson(couponData)}")
                if (response.code() == 200) {


                    if (couponData?.responseCode == 200) {


                        if (couponData.myCoupons.isNotEmpty()) {
                            binding!!.noData.visibility = View.GONE
                            binding!!.couponRecycler.visibility = View.VISIBLE

                            var adapter =
                                VoucherAdapter2(couponData.myCoupons, context, this@MyCoupons)
                            binding!!.couponRecycler.adapter = adapter
                            adapter.notifyDataSetChanged()
                        } else {
                            binding!!.noData.visibility = View.VISIBLE
                            binding!!.couponRecycler.visibility = View.GONE

                        }


                    } else {

                    }


                }
                dialog.dismiss()
            }

            override fun onFailure(call: Call<CouponData?>, t: Throwable) {
                dialog.dismiss()
                println(t.message)
            }
        })
    }

    private fun productVoucherWishList() {
        dialog.show()
        val hashmap: HashMap<String, Any> = hashMapOf()
        hashmap["user_profile_id"] = sharedPrefManager!!.user.id.toString()
        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.productVoucherWishList(hashmap)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                /*      println("productVoucherWishList")
                      println(hashmap)
                      println(response.code())
                      println(response.body())*/
                if (response.code() == 200) {
                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {
                            val turnsType = object : TypeToken<ArrayList<VoucherModel>>() {}.type
                            val list: ArrayList<VoucherModel> = Gson().fromJson(
                                jsonObject.getJSONArray("wish_list_data").toString(),
                                turnsType
                            )

                            Log.i(
                                TAG,
                                "onResponse list: ${jsonObject.getJSONArray("wish_list_data")}"
                            )
                            println(list.size)
                            if (list.size == 0) {
                                binding!!.notFound.visibility = View.VISIBLE
                            } else {
                                binding!!.notFound.visibility = View.GONE
                                binding!!.recyclerView.adapter =
                                    MyWishlistAdapter(list, context, this@MyCoupons, this@MyCoupons)
                            }
                        }
                    } catch (e: Exception) {
                        println("productVoucherList e")
                        println(e.message)
                        e.printStackTrace()
                    }
                }
                dialog.dismiss()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                dialog.dismiss()
                println(t.message)
            }
        })
    }

    override fun onRedeem(model: CouponData.CouponDataItem) {
        openDialog(model)
    }

    @SuppressLint("SetTextI18n")
    private fun openDialog(model: CouponData.CouponDataItem) {
        val binding = ShowCouponBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        binding.toolbarTitle.text = model.couponName
        if (model.couponValidity.isNullOrEmpty()) {
            binding.ll2.visibility = View.GONE
        } else {
            binding.validity.text = model.couponValidity
            binding.ll2.visibility = View.VISIBLE
        }
        binding.description.text = model.couponDescription
        binding.description.movementMethod = ScrollingMovementMethod()

        //  binding.couponText.text = model.couponCode?.trim()
        binding.couponText.text = model.couponCode?.trim()
        //   binding.couponText.movementMethod=ScrollingMovementMethod()
        //   binding.couponText.setHorizontallyScrolling(true)

        binding.claimReward.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", binding.couponText.text.toString().trim())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
        }


        Glide.with(context)
            .load(model.couponImage)
            .placeholder(R.mipmap.logo)
            .apply(RequestOptions().override(ViewGroup.LayoutParams.MATCH_PARENT, 400))
            .fitCenter()
            .error(R.mipmap.logo)
            .into(binding.image)
        dialog.setView(binding.root)
        dialog.setOnCancelListener {
        }
        dialog.show()
    }

    override fun showVoucher(
        model: VoucherModel,
        position: Int,
        arrayList: ArrayList<VoucherModel>,
        myWishlistAdapter: MyWishlistAdapter
    ) {
        if (model.vouchers.size > 1) {
            Log.i(TAG, "openDialog: ${model.vouchers.size}")
            // openDialog(model)

            Constant.showCouponDialog(
                model,
                this@MyCoupons,
                object : Constant.RedeemRewardCallback {
                    override fun onRedeemed(str: Boolean) {
                        if (str) {
                            getRewardPoints()
                            model.product_voucher_id?.let {
                                removeWishlist(
                                    it,
                                    position,
                                    arrayList,
                                    myWishlistAdapter
                                )
                            }
                        }

                    }

                })


        } else {
            Toast.makeText(context, "Voucher not available!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRewardPoints() {
        dialog.show()
        val map: java.util.HashMap<String, Any> = hashMapOf()
        map["user_profile_id"] = sharedPrefManager?.user?.id.toString()
        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.getRewardPoints(map)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.code() == 200) {
                    assert(response.body() != null)
                    Log.e("getRewardPoints", response.body().toString())
                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {

                            val earnPoints = jsonObject.getInt("earned_point")
                            val usedPoints = jsonObject.getInt("point_used")
                            var pointBalance = earnPoints - usedPoints
                            sharedPrefManager!!.setString(
                                SharedPrefManager.KEY_POINT_BALANCE,
                                pointBalance.toString()
                            )


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                dialog.dismiss()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                dialog.dismiss()
            }
        })
    }

    override fun onRemoveWishlist(
        productVoucherId: Int,
        position: Int,
        arrayList: ArrayList<VoucherModel>,
        myWishlistAdapter: MyWishlistAdapter
    ) {
        removeWishlist(productVoucherId, position, arrayList, myWishlistAdapter)
    }

    private fun removeWishlist(
        productVoucherId: Int,
        position: Int,
        arrayList: ArrayList<VoucherModel>,
        myWishlistAdapter: MyWishlistAdapter
    ) {
        dialog.show()
        val hashmap: HashMap<String, Any> = hashMapOf()
        hashmap["user_profile_id"] = SharedPrefManager(context).user.id.toString()
        hashmap["product_voucher_id"] = productVoucherId
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.removeWishlist(hashmap)
        call.enqueue(object : Callback<JsonObject> {
            @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged")
            override fun onResponse(
                call: retrofit2.Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                println("removeWishlist")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        arrayList.removeAt(position)
                        myWishlistAdapter.notifyDataSetChanged()
                        if (arrayList.size == 0) {
                            binding!!.notFound.visibility = View.VISIBLE
                        }
                        Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show()
                    }
                }
                dialog.dismiss()
            }

            override fun onFailure(call: retrofit2.Call<JsonObject>, t: Throwable) {
                dialog.dismiss()
                println(t.message)

            }
        })

    }

}