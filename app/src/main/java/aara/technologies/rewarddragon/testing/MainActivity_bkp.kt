package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.Dashboard
import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.adapter.VoucherAdapter3
import aara.technologies.rewarddragon.databinding.ActivityMainBinding
import aara.technologies.rewarddragon.databinding.CouponDetailsLayoutBinding
import aara.technologies.rewarddragon.manager.EditProfile
import aara.technologies.rewarddragon.model.JoshModel
import aara.technologies.rewarddragon.model.VoucherModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant
import aara.technologies.rewarddragon.utils.Constant.androidId
import aara.technologies.rewarddragon.utils.Constant.daysToGo
import aara.technologies.rewarddragon.utils.Constant.loadAvatarImage
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class MainActivity_bkp : Fragment(), VoucherAdapter3.RedeemReward {
    private lateinit var paramsOld: LinearLayout.LayoutParams
    private lateinit var paramsNew: LinearLayout.LayoutParams
    var binding: ActivityMainBinding? = null
    var alertDialog: AlertDialog? = null
    lateinit var progressDialog: CustomLoader


    var mGoogleSignInClient: GoogleSignInClient? = null
    var sharedPrefManager: SharedPrefManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefManager = SharedPrefManager(requireContext())
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels

        paramsNew = LinearLayout.LayoutParams(width / 4, 130)
        paramsOld = LinearLayout.LayoutParams(width / 6, 100)

        Log.i(TAG, "onCreateView: width  $width")

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityMainBinding.inflate(
            layoutInflater
        )

        return binding!!.root
    }

    private fun setIntent(i: Int): Intent {
        val intent = Intent(requireContext(), MyJoshForToday::class.java)
        intent.putExtra("emoji", i)
        return intent
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        progressDialog = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        binding!!.performance.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    MyPerformance::class.java
                )
            )
        }
        binding!!.daysToGo.text = daysToGo()
        binding!!.myReward.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    MyReward::class.java
                )
            )
        }
        binding!!.myJoshForToday.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    MyJoshForToday::class.java
                )
            )
        }
        binding!!.myWellbeing.setOnClickListener {
            Log.i(TAG, "onViewCreated: " + "working")
            (context as Dashboard?)!!.addFragment(MyWellBeing())
            //  bottomView?.selectedItemId = Dashboard.wellbeingView!!.id
        }

        binding!!.latestChallenges.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    MyLatestChallenge::class.java
                )
            )
        }
        binding!!.gameTime.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    MyGameTime::class.java
                )
            )
        }
        binding!!.image5.setOnClickListener { view1: View ->
            startActivity(setIntent(5))
            emojiEffect(binding!!.image5.tag.toString())
        }
        binding!!.image4.setOnClickListener { view1: View ->
            startActivity(setIntent(4))
            emojiEffect(binding!!.image4.tag.toString())
        }
        binding!!.image3.setOnClickListener { view1: View ->
            startActivity(setIntent(3))
            emojiEffect(binding!!.image3.tag.toString())
        }
        binding!!.image2.setOnClickListener { view1: View ->
            startActivity(setIntent(2))
            emojiEffect(binding!!.image2.tag.toString())
        }
        binding!!.image1.setOnClickListener { view1: View ->
            startActivity(setIntent(1))
            emojiEffect(binding!!.image1.tag.toString())
        }

        paramsOld.rightMargin = 5
        paramsOld.leftMargin = 5
        paramsOld.topMargin = 5
        paramsNew.rightMargin = 5
        paramsNew.leftMargin = 5
        paramsNew.topMargin = 5
        binding!!.image1.layoutParams = paramsOld
        binding!!.image2.layoutParams = paramsOld
        binding!!.image3.layoutParams = paramsOld
        binding!!.image4.layoutParams = paramsOld
        binding!!.image5.layoutParams = paramsOld
        binding!!.userNameTxt.text = "Hello, " + sharedPrefManager!!.user.firstName
//        binding!!.time.text = getTime()

        binding!!.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)



        joshToday
        winLevelPoints
        customerKpiData
        wellBeingList
        loadAvatarImage(requireContext(), binding!!.imageView)
        homepageVoucherList()
        getRewardPoints()
    }


    override fun onResume() {
        super.onResume()
        checkUserLogin()
        if (sharedPrefManager!!.user.teamId == null) {
            Log.e("TAG", "onResume: Team id not update")
            openDialog()
        } else {
            Log.e("TAG", "onResume: Team id updated")
        }
    }

    private fun homepageVoucherList() {

        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.homepageVoucherList()
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                //   println("homepageVoucherList")
                // println(response.code())
                // println(response.body())
                Log.i(TAG, "onResponse: ${response.body()}")
                if (response.code() == 200) {
                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {
                            val turnsType = object : TypeToken<ArrayList<VoucherModel>>() {}.type
                            val list: ArrayList<VoucherModel> = Gson().fromJson(
                                jsonObject.getJSONArray("data").toString(),
                                turnsType
                            )
                            println(list.size)
                            if (list.size == 0) {
//                                binding!!.notFound.visibility = View.VISIBLE
                            } else {
//                                binding!!.notFound.visibility = View.GONE
                                binding!!.recyclerView.adapter =
                                    VoucherAdapter3(list, requireContext(), this@MainActivity_bkp)
                            }
                        }
                    } catch (e: Exception) {
                        println("productVoucherList e")
                        println(e.message)
                        e.printStackTrace()
                    }
                }
//                dialog.dismiss()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
//                dialog.dismiss()
                println(t.message)
            }
        })
    }

    private fun openDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setIcon(R.mipmap.logo)
        builder.setTitle("Update Profile")
        builder.setCancelable(false)
        builder.setMessage("Your profile details not updated.\nPlease update your complete profile.")
        builder.setPositiveButton("OK") { _: DialogInterface?, _: Int ->
            (context as Dashboard?)!!.addFragment(
                EditProfile()
            )
        }
        val dialog = builder.create()
        dialog.show()
    }

    private val TAG = "MainActivity_test"
    private fun checkUserLogin() {
        val map: HashMap<String, String> = HashMap()
        map["user_profile_id"] = sharedPrefManager!!.user.id.toString()
        map["device_id"] = androidId
        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.userLoginCheck(map)
        Log.i(TAG, "checkUserLogin: " + Gson().toJson(call.request()))
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.e("check", "onResponse: " + response.code())
                //   Log.i(TAG, "onResponse: " + Gson().toJson(response.body()))
                if (response.code() == 401) {
                    mGoogleSignInClient!!.signOut()
                    sharedPrefManager!!.logout()
                    startActivity(Intent(activity, LoginActivity::class.java))
                    Toast.makeText(context, "User Logout Successfully...", Toast.LENGTH_SHORT)
                        .show()
                    activity!!.finish()
                }
                try {
                    Log.e("check", "onResponse: " + response.body())
                    val jsonObject = JSONObject(Gson().toJson(response.body()))
                    val status = jsonObject.getInt("status")
                    if (status == 1) {
                        mGoogleSignInClient!!.signOut()
                        sharedPrefManager!!.logout()
                        startActivity(Intent(activity, LoginActivity::class.java))
                        Toast.makeText(
                            context,
                            "User Logout Successfully...",
                            Toast.LENGTH_SHORT
                        ).show()
                        activity!!.finish()
                    }
                } catch (e: Exception) {
                    Log.e("TAG", "onResponse: " + e.message)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("tag", "onFailure: " + t.message)
            }
        })
    }

    //                    Log.e("getJoshToday", Objects.requireNonNull(new SharedPref!!.getUser().getId()).toString());
    private val joshToday: Unit
        get() {
            val services = RetrofitInstance().getInstance().create(
                DataServices::class.java
            )
            val call = services.getJoshToday(
                Objects.requireNonNull(sharedPrefManager!!.user.id).toString(),
                Objects.requireNonNull(sharedPrefManager!!.user.managerId.toString())
            )
            call.enqueue(object : Callback<JsonObject?> {
                @SuppressLint("SimpleDateFormat")
                override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                    if (response.code() == 200) {
                        assert(response.body() != null)
                        //                    Log.e("getJoshToday", Objects.requireNonNull(new SharedPref!!.getUser().getId()).toString());
                        Log.e("getJoshToday", response.body().toString())
                        try {
                            val jsonObject = JSONObject(Gson().toJson(response.body()))
                            val resCode = jsonObject.getInt("response_code")
                            if (resCode == 200) {
                                val data = jsonObject.getJSONObject("data")
                                val list = Gson().fromJson(data.toString(), JoshModel::class.java)
                                if (!data.isNull("emoji_point")) {
                                    binding!!.image1.isClickable = false
                                    binding!!.image2.isClickable = false
                                    binding!!.image3.isClickable = false
                                    binding!!.image4.isClickable = false
                                    binding!!.image5.isClickable = false
                                }
                                if (list != null) {

                                    val sdf = SimpleDateFormat(
                                        "yyyy-MM-dd'T'HH:mm:ss",
                                        Locale.getDefault()
                                    )


                                    var created_at = data.optString("created_at")
                                    if (!created_at.isNullOrEmpty()) {
                                        val createdAt =
                                            sdf.parse(data.getString("created_at").substring(0, 19))
                                        val newFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a")
                                        val newDate = newFormat.format(createdAt!!)
                                        binding!!.time.text = newDate
                                    }
                                    when (list.emoji_point) {
                                        5 -> {
                                            emojiEffect("image5")
                                            binding!!.text5.visibility = View.VISIBLE
                                        }
                                        4 -> {
                                            emojiEffect("image4")
                                            binding!!.text4.visibility = View.VISIBLE
                                        }
                                        3 -> {
                                            emojiEffect("image3")
                                            binding!!.text3.visibility = View.VISIBLE
                                        }
                                        2 -> {
                                            emojiEffect("image2")
                                            binding!!.text2.visibility = View.VISIBLE
                                        }
                                        1 -> {
                                            emojiEffect("image1")
                                            binding!!.text1.visibility = View.VISIBLE
                                        }
                                        else -> {

                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                    Log.e("getJoshToday t", t.message!!)
                }
            })
        }

    private val winLevelPoints: Unit
        get() {
            val map: HashMap<String, String> = HashMap()
            map["employee_id"] = sharedPrefManager!!.user.id.toString()
            val services = RetrofitInstance().getInstance().create(
                DataServices::class.java
            )
            val call = services.getWinLevelPoints(map)
            call.enqueue(object : Callback<JsonObject?> {
                override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                    if (response.code() == 200) {
                        assert(response.body() != null)
                        //                    Log.e("getJoshToday", Objects.requireNonNull(new SharedPref!!.getUser().getId()).toString());
                        Log.e("getWinLevelPoints", response.body().toString())
                        try {
                            val jsonObject = JSONObject(Gson().toJson(response.body()))
                            val resCode = jsonObject.getInt("response_code")
                            if (resCode == 200) {
                                binding!!.level.text = jsonObject.getString("win_level")
                                binding!!.point.text = jsonObject.getString("points_won")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                    Log.e("getJoshToday t", t.message!!)
                }
            })
        }


    private fun getRewardPoints() {
        val map: HashMap<String, Any> = hashMapOf()
        map["user_profile_id"] = sharedPrefManager!!.user.id.toString()
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
                            binding!!.pointBalance.text =
                                Constant.format((earnPoints - usedPoints).toLong())

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
            }
        })
    }

    private val customerKpiData: Unit
        get() {
            val map: HashMap<String, String> = HashMap()
            map["employee_id"] = sharedPrefManager!!.user.id.toString()
            val services = RetrofitInstance().getInstance().create(
                DataServices::class.java
            )
            val call = services.getCustomerKpiData(map)
            call.enqueue(object : Callback<JsonObject?> {
                override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                    if (response.code() == 200) {
                        assert(response.body() != null)
                        //                    Log.e("getJoshToday", Objects.requireNonNull(new SharedPref!!.getUser().getId()).toString());
                        Log.e("getCustomerKpiData", response.body().toString())
                        try {
                            val jsonObject = JSONObject(Gson().toJson(response.body()))
                            val resCode = jsonObject.getInt("response_code")
                            if (resCode == 200) {
                                binding!!.kpiMet.text = jsonObject.getString("total_kpi_met")
                                binding!!.kpiWip.text = jsonObject.getString("total_kpi_wip")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                    Log.e("getJoshToday t", t.message!!)
                }
            })
        }

    private val wellBeingList: Unit
        get() {
            val map: HashMap<String, String> = HashMap()
            map["user_profile_id"] = sharedPrefManager!!.user.id.toString()
            map["time_period_all"] = "today"
            val services = RetrofitInstance().getInstance().create(
                DataServices::class.java
            )
            val call = services.wellBeingList(map)
            call.enqueue(object : Callback<JsonObject> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    Log.e("getWellBeingList", response.code().toString() + "")
                    if (response.code() == 200) {
//                    Log.e("getJoshToday", Objects.requireNonNull(new SharedPref!!.getUser().getId()).toString());
                        Log.e("getWellBeingList", response.body().toString())
                        try {
                            val jsonObject = JSONObject(Gson().toJson(response.body()))
                            val resCode = jsonObject.getInt("response_code")
                            if (resCode == 200) {
                                binding!!.wellBeingPercent.progress =
                                    jsonObject.getInt("wellbeing_percent")
                                binding!!.percent.text =
                                    jsonObject.getString("wellbeing_percent") + " %"
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("getWellBeingList t", t.message!!)
                }
            })
        }

    fun emojiEffect(tag: String) {
        paramsOld.rightMargin = 5
        paramsOld.leftMargin = 5
        paramsOld.topMargin = 5
        paramsNew.rightMargin = 5
        paramsNew.leftMargin = 5
        paramsNew.topMargin = 5
        binding!!.image1.layoutParams = if (tag == "image1") paramsNew else paramsOld
        binding!!.image2.layoutParams = if (tag == "image2") paramsNew else paramsOld
        binding!!.image3.layoutParams = if (tag == "image3") paramsNew else paramsOld
        binding!!.image4.layoutParams = if (tag == "image4") paramsNew else paramsOld
        binding!!.image5.layoutParams = if (tag == "image5") paramsNew else paramsOld
    }

    override fun onPause() {
        super.onPause()
        if (alertDialog != null) {
            alertDialog!!.dismiss()
        }
    }

    override fun onRedeem(model: VoucherModel) {
        if (model.vouchers.size > 1) {
            Log.i(TAG, "openDialog: ${model.vouchers.size}")
            openDialog(model)
        } else {
            Toast.makeText(context, "Voucher not available!", Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun openDialog(model: VoucherModel) {
        var amount: Double = 0.0
        var discountAmount: Double = 0.0
        val binding = CouponDetailsLayoutBinding.inflate(LayoutInflater.from(context))
        val dialog = android.app.AlertDialog.Builder(context).create()
        binding.toolbarTitle.text = model.name
        binding.brand.text = model.brand
        binding.validity.text = model.validity
        binding.description.text = model.description

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));


        binding.claimReward.setOnClickListener {


            var qty: String? = binding.qtySpinner.selectedItem.toString()
            Log.i(TAG, "openDialog: ${model.vouchers.size}")
            claimReward(
                model.product_voucher_id,
                model.vouchers[1].voucherAmountId,
                amount,
                qty!!.toInt(),
                dialog
            )

        }

        var items: ArrayList<String> = ArrayList()


        for (i in 1..model.vouchers[1].quantity!!) {
            //   print(i);
            items.add(i.toString())
        }

        // Log.i(TAG, "openDialog: " + items[1])


        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item2, items)
        adapter.setDropDownViewResource(R.layout.selectable_dialog_items)
        binding.qtySpinner.adapter = adapter

        binding.qtySpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Toast.makeText(context, items[position], Toast.LENGTH_SHORT).show()
                amount = model.vouchers[1].amount!!.toDouble() * items[position].toDouble()
                binding.price.text = "\u20b9 ${amount.toString()}"

                discountAmount =
                    model.vouchers[1].redeemValue!!.toDouble() * items[position].toDouble()

                binding.discountPrice.text =
                    Html.fromHtml("<strike> ₹ $discountAmount </strike>") /*=" <strike> \u20b9 $discountAmount </strike>"*/


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }


        /*     binding.qtySpinner.maxValue = items.size;
             binding.qtySpinner.minValue = 1;
             binding.qtySpinner.displayedValues = items.toTypedArray();*/


        Glide.with(requireContext())
            .load(model.image)
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

    private fun claimReward(
        product_voucher_id: Int?,
        voucher_amount_id: Int?,
        redeem_value: Double,
        quantity: Int?,
        dialog: android.app.AlertDialog
    ) {
        this.progressDialog.show()


        val hashmap: HashMap<String, String> = hashMapOf()
        hashmap["user_profile_id"] = sharedPrefManager!!.user.id.toString()
        hashmap["product_voucher_id"] = product_voucher_id.toString()
        hashmap["voucher_amount_id"] = voucher_amount_id.toString()
        hashmap["redeem_value"] = redeem_value.toString()
        hashmap["quantity"] = quantity.toString()
        hashmap["unique_code"] = sharedPrefManager!!.user.uniqueCode.toString()


        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.claimReward(hashmap)
        //   Log.i(TAG, "claimReward:req " + Gson().toJson(call.request()))
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.code() == 200) {
                    try {
                        Log.i(TAG, "onResponse: " + Gson().toJson(response.body()))
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        //  val jsonObj = jsonObject.getJSONObject("message")
                        //   Log.i(TAG, "onResponse:claimReward ${jsonObj.getString("message")}")
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {

                            Toast.makeText(
                                context,
                                jsonObject.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()

                            dialog.dismiss()


                        }
                    } catch (e: java.lang.Exception) {
                        Log.i(TAG, "onResponse: " + e.message)
                    }


                } else {

                    Toast.makeText(
                        context,
                        response.message(),
                        Toast.LENGTH_SHORT
                    ).show()

                }

                progressDialog.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(
                    context,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()

            }

        })

    }
}