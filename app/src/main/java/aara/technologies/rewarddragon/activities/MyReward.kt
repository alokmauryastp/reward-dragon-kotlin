package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.adapter.VoucherAdapter
import aara.technologies.rewarddragon.databinding.ActivityMyRewardBinding
import aara.technologies.rewarddragon.databinding.RewardLayoutFilterBinding
import aara.technologies.rewarddragon.model.GiftBrandList
import aara.technologies.rewarddragon.model.GiftVoucher
import aara.technologies.rewarddragon.model.VoucherModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant
import aara.technologies.rewarddragon.utils.Constant.loadAvatarImage
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.slider.LabelFormatter.LABEL_VISIBLE
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class MyReward : AppCompatActivity(), VoucherAdapter.RedeemReward {

    private var filterDialog: AlertDialog? = null
    private lateinit var filterersBinding: RewardLayoutFilterBinding
    var binding: ActivityMyRewardBinding? = null
    lateinit var dialog: CustomLoader
    val hashmap: HashMap<String, Any> = hashMapOf()
    lateinit var context: Context
    var page = 1
    var limit = 1
    val list2: ArrayList<VoucherModel> = arrayListOf()
    var giftVoucherCategory: ArrayList<GiftVoucher> = arrayListOf()
    var selectedCategory = 0
    var selectedbrand = 0
    private val TAG = "MyReward"
    var pointsToCurrencyRes: PointsToCurrencyRes? = null

    private lateinit var sharedPrefManager: SharedPrefManager

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRewardBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        context = this

        sharedPrefManager = SharedPrefManager(context)

        filterersBinding = RewardLayoutFilterBinding.inflate(LayoutInflater.from(context))
        filterDialog = AlertDialog.Builder(context).create()

        binding!!.toolbar.toolbarTitle.text =
            sharedPrefManager.user.companyName
        com.bumptech.glide.Glide.with(context).load(
            sharedPrefManager
                .getString(SharedPrefManager.KEY_COMPANY_IMAGE)!!
        ).into(binding!!.toolbar.companyLogo)
        binding!!.toolbar.back.setOnClickListener { finish() }

        dialog = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)

        binding!!.recyclerView.layoutManager = GridLayoutManager(context, 3)

        binding!!.userNameTxt.text =
            sharedPrefManager.user.firstName + " " + SharedPrefManager.getInstance(
                context
            )!!.user.lastName
        binding!!.designation.text = sharedPrefManager.user.designation


        loadAvatarImage(context, binding!!.imageView)

        binding!!.filter.setOnClickListener {
            giftCategoryList()
        }
        // adding on scroll change listener method for our nested scroll view.
        // adding on scroll change listener method for our nested scroll view.
        binding!!.nested.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            // on scroll change we are checking when users scroll as bottom.
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                // in this method we are incrementing page number,
                // making progress bar visible and calling get data method.
                page++
                dialog.show()
                productVoucherList()
            }
        })


        getVoucher("", "", "")
        getRewardPoints()
    }


    override fun onStop() {

        if (filterDialog != null) {
            if (filterDialog!!.isShowing) {
                filterDialog!!.dismiss()
            } else {
                filterDialog = null
            }

        }

        super.onStop()
    }

    private fun getVoucher(category: String, brands: String, amount: String) {
        hashmap["categories"] = category
        hashmap["brands"] = brands
        hashmap["amount"] = amount
        productVoucherList()
    }

    private fun openDialog(
        listCategory: ArrayList<GiftVoucher>,
//        pointRangeList: ArrayList<PointRangeModel>
    ) {


        try {
            if (pointsToCurrencyRes != null) {
                var money = pointsToCurrencyRes!!.rupees

                if (money!! > 0) {
                    filterersBinding.rangeSlider.valueTo =
                        money!!.toFloat()

                    filterersBinding.rangeSlider.visibility = View.VISIBLE

                } else {
                    filterersBinding.rangeSlider.valueTo = 1F
                    filterersBinding.rangeSlider.visibility = View.GONE

                }

                filterersBinding.pointsToMoneyTxt.text =
                    "(${this.binding?.pointBalance?.text.toString()} points = ₹ $money)"
                filterersBinding.pointsBal.text =
                    "You have rewards worth ₹ $money available to claim"

            } else {
                Log.i(TAG, "openDialog:pointsToCurrencyRes nullll ")
            }
            addBrandSpinnerList(filterersBinding)

            listCategory.add(0, GiftVoucher("All", ""))
            val items1: java.util.ArrayList<String?> = java.util.ArrayList()

            for (item in listCategory) {
                items1.add(item.category_name)
            }
            val adapter1 = ArrayAdapter(
                context,
                R.layout.simple_spinner_item,
                items1
            ) //setting the country_array to spinner
            adapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            filterersBinding.giftCategory.adapter = adapter1
            filterersBinding.giftCategory.setSelection(selectedCategory)
            filterersBinding.giftCategory.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        p1: View?,
                        p2: Int,
                        p3: Long
                    ) {
                        limit = 1
                        page = 1
                        list2.clear()
                        if (p2 == 0) {
                            hashmap["categories"] = ""
                        } else {
                            hashmap["categories"] = listCategory[p2].category_name
                        }
                        selectedCategory = p2
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }


        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }


        filterersBinding.close.setOnClickListener { filterDialog?.dismiss() }


        filterersBinding.rangeSlider.addOnChangeListener { slider, value, fromUser ->
            if (value > 0) {
                slider.labelBehavior = LABEL_VISIBLE
            }
        }

        var giftCategory = ""
        var brandCategory = ""
        var amount = ""
        filterersBinding.apply.setOnClickListener {

            giftCategory = if (filterersBinding.giftCategory.selectedItem.toString() == "All") {
                ""
            } else {

                filterersBinding.giftCategory.selectedItem.toString().trim()
            }
            brandCategory = if (filterersBinding.brandCategory.selectedItem.toString() == "All") {
                ""
            } else {
                filterersBinding.brandCategory.selectedItem.toString().trim()
            }

            amount = if (filterersBinding.rangeSlider.value.roundToInt() == 0) {
                ""
            } else {
                filterersBinding.rangeSlider.value.roundToInt().toString()
            }


            getVoucher(
                giftCategory,
                brandCategory,
                amount
            )

            filterDialog?.dismiss()
        }
        filterDialog?.setView(filterersBinding.root)
        filterDialog?.show()
    }

    private fun convertPointsToMoney(
        points: String,
        unicode: String
    ) {
        var hashmap: HashMap<String, Any>? = hashMapOf()
        hashmap?.set("unique_code", unicode)
        hashmap?.set("points", points)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        services.convertMoneyToPoints(hashmap!!).enqueue(object : Callback<PointsToCurrencyRes> {
            override fun onResponse(
                call: Call<PointsToCurrencyRes>,
                response: Response<PointsToCurrencyRes>
            ) {

                Log.i(TAG, "onResponse: convertPointsToMoney: ${Gson().toJson(response.body())}  responsecode : ${response.code()}")

                pointsToCurrencyRes = response.body()
            }

            override fun onFailure(call: Call<PointsToCurrencyRes>, t: Throwable) {
                Log.i(TAG, "onFailure:money ${t.message}")
            }
        })
    }

    private fun addBrandSpinnerList(binding: RewardLayoutFilterBinding) {
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)

        services.giftBrandList().enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.i(TAG, "onResponse: brand ${response.message()}")
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val turnsType = object : TypeToken<ArrayList<GiftBrandList>>() {}.type
                    val list: ArrayList<GiftBrandList> =
                        Gson().fromJson(obj.getJSONArray("data").toString(), turnsType)
                    //   println(list.size)

                    list.add(0, GiftBrandList("", "", "All"))

                    val items1: java.util.ArrayList<String?> = java.util.ArrayList()

                    for (item in list) {
                        items1.add(item.name)
                    }

                    val adapter1 = ArrayAdapter(
                        context,
                        R.layout.simple_spinner_item,
                        items1
                    ) //setting the country_array to spinner
                    adapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                    binding.brandCategory.adapter = adapter1
                    binding.brandCategory.setSelection(selectedbrand)
                    binding.brandCategory.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                p0: AdapterView<*>?,
                                p1: View?,
                                p2: Int,
                                p3: Long
                            ) {
                                limit = 1
                                page = 1
                                list2.clear()


                                if (p2 == 0) {
                                    hashmap["brands"] = ""
                                } else {
                                    hashmap["brands"] = list[p2].name
                                }
                                selectedbrand = p2
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {
                            }
                        }

                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.i(TAG, "onFailure:brand ${t.message}")
            }
        })
    }


    data class PointsToCurrencyRes(

        @SerializedName("message") var message: String? = null,
        @SerializedName("rupees") var rupees: Double? = null,
        @SerializedName("currency_type") var currencyType: String? = null,
        @SerializedName("response_code") var responseCode: Int? = null

    )

    private fun giftCategoryList() {
        dialog.show()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        var call = services.giftCategoryList()

        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                try {
                    if (response.code() == 200) {
                        if (giftVoucherCategory != null) {
                            giftVoucherCategory.clear()
                        }
                        val obj = JSONObject(Gson().toJson(response.body()))
                        val turnsType = object : TypeToken<ArrayList<GiftVoucher>>() {}.type
                        giftVoucherCategory =
                            Gson().fromJson(obj.getJSONArray("data").toString(), turnsType)
                        openDialog(giftVoucherCategory)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                dialog.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
                dialog.dismiss()
            }
        })


    }


    private fun getRewardPoints() {
        dialog.show()
        val map: java.util.HashMap<String, Any> = hashMapOf()
        map["user_profile_id"] = sharedPrefManager.user.id.toString()
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
                            binding!!.pointEarned.text = earnPoints.toString()
                            binding!!.pointBalance.text = pointBalance.toString()
                            binding!!.pointUsed.text = usedPoints.toString()

                            sharedPrefManager!!.setString(
                                SharedPrefManager.KEY_POINT_BALANCE,
                                pointBalance.toString()
                            )

                            convertPointsToMoney(
                                pointBalance.toString(),
                                sharedPrefManager.user.uniqueCode!!
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

    private fun productVoucherList() {
        if (page > limit) {
            // checking if the page number is greater than limit.
            // displaying toast message in this case when page>limit.
            Toast.makeText(context, "That's all the data..", Toast.LENGTH_SHORT).show()
            // hiding our progress bar.
            dialog.dismiss()
            return
        }
        dialog.show()
        hashmap["user_profile_id"] = sharedPrefManager.user.id.toString()
        hashmap["page"] = page
        Log.i(TAG, "productVoucherList: $hashmap")
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.productVoucherList(hashmap)
       Log.i(TAG, "productVoucherList:req " + Gson().toJson(call.request()))
        call.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
//                println("productVoucherList")
//                println(hashmap)
//                println(response.code())
//                println(response.body())
                Log.i(
                    TAG,
                    "onResponse:productVoucherList code ${response.code()} body ${response.body()}"
                )
                if (response.code() == 200) {
                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {

                            if (!jsonObject.isNull("total_no_pages")) {
                                limit = jsonObject.getInt("total_no_pages")
                            }
                            val turnsType = object : TypeToken<ArrayList<VoucherModel>>() {}.type
                            val list3: ArrayList<VoucherModel> = Gson().fromJson(
                                jsonObject.getJSONArray("data").toString(),
                                turnsType
                            )

                            list2.addAll(list3)

                            //     println(list2.size)

                            binding!!.count.text = "(" + list2.size.toString() + ")"
                            if (list2.size == 0) {
                                binding!!.notFound.visibility = View.VISIBLE
                                binding!!.recyclerView.adapter = null

                            } else {
                                binding!!.notFound.visibility = View.GONE
                                var adapter = VoucherAdapter(list2, context, this@MyReward)
                                binding!!.recyclerView.adapter = adapter
                                adapter.notifyDataSetChanged()


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
                //println(t.message)

                Log.i(TAG, "onFailure: dssd " + t.message)
            }
        })
    }

    override fun onRedeem(model: VoucherModel) {

        if (model.vouchers.isNotEmpty()) {
            Log.i(TAG, "openDialog: ${model.vouchers.size}")
            //openDialog(model)
            Constant.showCouponDialog(
                model,
                this@MyReward,
                object : Constant.RedeemRewardCallback {
                    override fun onRedeemed(str: Boolean) {
                        if (str) {
                            val map3: java.util.HashMap<String, String> = java.util.HashMap()
                            map3["user_profile_id"] = sharedPrefManager.user.id.toString()
                            getRewardPoints()
                        }
                    }
                })

        } else {
            Toast.makeText(context, "Voucher not available!", Toast.LENGTH_SHORT).show()
        }

    }
}