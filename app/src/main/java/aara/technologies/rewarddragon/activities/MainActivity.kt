package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.Dashboard
import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.adapter.VoucherAdapter4
import aara.technologies.rewarddragon.dashboardBinding
import aara.technologies.rewarddragon.databinding.ActivityMainBinding
import aara.technologies.rewarddragon.manager.EditProfile
import aara.technologies.rewarddragon.model.VoucherModel
import aara.technologies.rewarddragon.model.Vouchers
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.testing.BaseFragment
import aara.technologies.rewarddragon.testing.Model.JoshRes
import aara.technologies.rewarddragon.testing.Repository.Repository
import aara.technologies.rewarddragon.testing.Resource
import aara.technologies.rewarddragon.testing.ViewModel.MainActivityViewModel
import aara.technologies.rewarddragon.utils.Constant
import aara.technologies.rewarddragon.utils.Constant.androidId
import aara.technologies.rewarddragon.utils.Constant.daysToGo
import aara.technologies.rewarddragon.utils.Constant.loadAvatarImage
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : BaseFragment<MainActivityViewModel, ActivityMainBinding, Repository>(),
    VoucherAdapter4.RedeemReward {
    private lateinit var paramsOld: LinearLayout.LayoutParams
    private lateinit var paramsNew: LinearLayout.LayoutParams
    var alertDialog: AlertDialog? = null
    lateinit var progressDialog: CustomLoader


    var mGoogleSignInClient: GoogleSignInClient? = null
    lateinit var sharedPrefManager: SharedPrefManager
    private val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefManager = SharedPrefManager(requireContext())
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels

        paramsNew = LinearLayout.LayoutParams(width / 4, 130)
        paramsOld = LinearLayout.LayoutParams(width / 6, 100)

        // Log.i(TAG, "onCreateView: width  $width")

    }

    override fun getViewModel() = MainActivityViewModel::class.java
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ActivityMainBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        Repository(remoteDataSource.buildApi(DataServices::class.java))


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        viewModel.getJosh(
            Objects.requireNonNull(sharedPrefManager.user.id).toString(),
            Objects.requireNonNull(sharedPrefManager.user.managerId.toString())
        )

        viewModel.joshResponse.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    getMoodData(it)
                }

                is Resource.Failure -> {

                    Log.i(TAG, "onActivityCreated: $it")

                }

                else -> Log.i(TAG, "onActivityCreated:else  ")
            }

        }

        val map: HashMap<String, String> = HashMap()
        map["employee_id"] = sharedPrefManager.user.id.toString()
        viewModel.getWinLevel(map)

        viewModel.response.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    var response = it.value

                    Log.e(TAG, "onActivityCreated winlevel $response")

                    if (response.responseCode == 200) {
                        try {
                            binding.level.text = response.winLevel.toString()
                            binding.point.text = response.pointsWon.toString()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                }

                is Resource.Failure -> {

                    Log.i(TAG, "onActivityCreated: $it")

                }

                else -> Log.i(TAG, "onActivityCreated:else  ")
            }

        }

        viewModel.getKpiData(map)
        viewModel.kpiData.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    var response = it.value
                    Log.i(TAG, "kpiData: $response")

                    try {
                        if (response.responseCode == 200) {
                            binding.kpiMet.text = response.totalKpiMet.toString()
                            binding.kpiWip.text = response.totalKpiWip.toString()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                is Resource.Failure -> {

                    Log.i(TAG, "kpiData: $it")

                }

                else -> Log.i(TAG, "kpiData:else  ")

            }
        }

        val map2: HashMap<String, String> = HashMap()
        map2["user_profile_id"] = sharedPrefManager.user.id.toString()
        map2["time_period_all"] = "today"
        viewModel.getWellbeingList(map2)


        viewModel.wellBeingData.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    var response = it.value

                    Log.e(TAG, "wellBeingData " + response.toString())

                    if (response.responseCode == 200) {
                        try {

                            binding.wellBeingPercent.progress =
                                response.wellbeingPercent.toInt()
                            binding.percent.text =
                                response.wellbeingPercent.toString() + " %"

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                }

                is Resource.Failure -> {

                    Log.i(TAG, "wellBeingData: $it")

                }

                else -> Log.i(TAG, "wellBeingData:else  ")
            }
        }


        viewModel.getVoucherList()

        viewModel.voucherData.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    var response = it.value

                    // Log.e(TAG, "voucherData " + response.toString())

                    if (response.response_code == 200) {
                        try {

                            /*   val turnsType =
                                   object : TypeToken<ArrayList<VoucherModel>>() {}.type
                               val list: ArrayList<VoucherModel> = Gson().fromJson(
                                   jsonObject.getJSONArray("data").toString(),
                                   turnsType
                               )*/

                            val list = response.data
                            val newList: ArrayList<VoucherModel> = arrayListOf()
                            for (i in 0 until list.size) {
                                val data = list[i]

                                val vouchers: ArrayList<Vouchers> = arrayListOf()
                                for (j in 0 until data.vouchers.size) {
                                    val vou = data.vouchers[j]
                                    vouchers.add(
                                        Vouchers(
                                            vou.id,
                                            vou.productVoucherId,
                                            vou.voucherAmountId,
                                            vou.redeemValue,
                                            vou.amount,
                                            vou.quantity, vou.createdAt, vou.updatedAt
                                        )
                                    )
                                }
                                newList.add(
                                    VoucherModel(
                                        data.id,
                                        data.productVoucherId,
                                        data.name,
                                        data.redeemMode,
                                        data.summary,
                                        data.description,
                                        data.image,
                                        data.categories,
                                        data.brandExternalUrl,
                                        data.brand,
                                        data.howToRedeem,
                                        data.importantInformation,
                                        data.validity,
                                        "",
                                        data.createdAt,
                                        data.updatedAt,
                                        data.is_added_wishlist,
                                        vouchers
                                    )
                                )

                            }

                            println(list.size)
                            if (list.isEmpty()) {
//                                binding!!.notFound.visibility = View.VISIBLE
                            } else {

//                                binding!!.notFound.visibility = View.GONE
                                binding.recyclerView.adapter =
                                    VoucherAdapter4(
                                        newList,
                                        requireContext(),
                                        this@MainActivity
                                    )
                            }


                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                }

                is Resource.Failure -> {

                    Log.i(TAG, "voucherData: $it")

                }

                else -> Log.i(TAG, "voucherData:else  ")
            }

        }

        val map3: HashMap<String, String> = HashMap()
        map3["user_profile_id"] = sharedPrefManager.user.id.toString()

        viewModel.getRewardPoints(map3)

        viewModel.rewardData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    var response = it.value

                    Log.e(TAG, "rewardData $response")

                    if (response.responseCode == 200) {
                        try {
                            val earnPoints = response.earnedPoint
                            val usedPoints = response.pointUsed

                            var pointBalance = (earnPoints - usedPoints).toLong()

                            binding.pointBalance.text =
                                Constant.format(pointBalance)

                            sharedPrefManager.setString(
                                SharedPrefManager.KEY_POINT_BALANCE,
                                pointBalance.toString()
                            )

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                }

                is Resource.Failure -> {

                    Log.i(TAG, "rewardData: $it")

                }

                else -> Log.i(TAG, "rewardData:else  ")
            }

        }


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        progressDialog = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        binding.performance.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    MyPerformance::class.java
                )
            )
        }
        binding.daysToGo.text = daysToGo()
        binding.myReward.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    MyReward::class.java
                )
            )
        }
        binding.myJoshForToday.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    MyJoshForToday::class.java
                )
            )
        }
        binding.myWellbeing.setOnClickListener {
            Log.i(TAG, "onViewCreated: " + "working")
//            (context as Dashboard?)!!.addFragment(MyWellBeing())
            dashboardBinding!!.bottomNav.selectedItemId = R.id.wellbeing
            //  bottomView?.selectedItemId = Dashboard.wellbeingView!!.id
        }

        binding.latestChallenges.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    MyLatestChallenge::class.java
                )
            )
        }

        binding.gameTime.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    MyGameTime::class.java
                )
            )
        }
        binding.image5.setOnClickListener { view1: View ->
            startActivity(setIntent(5))
            emojiEffect(binding.image5.tag.toString())
        }
        binding.image4.setOnClickListener { view1: View ->
            startActivity(setIntent(4))
            emojiEffect(binding.image4.tag.toString())
        }
        binding.image3.setOnClickListener { view1: View ->
            startActivity(setIntent(3))
            emojiEffect(binding.image3.tag.toString())
        }
        binding.image2.setOnClickListener { view1: View ->
            startActivity(setIntent(2))
            emojiEffect(binding.image2.tag.toString())
        }
        binding.image1.setOnClickListener { view1: View ->
            startActivity(setIntent(1))
            emojiEffect(binding.image1.tag.toString())
        }

        paramsOld.rightMargin = 5
        paramsOld.leftMargin = 5
        paramsOld.topMargin = 5
        paramsNew.rightMargin = 5
        paramsNew.leftMargin = 5
        paramsNew.topMargin = 5
        binding.image1.layoutParams = paramsOld
        binding.image2.layoutParams = paramsOld
        binding.image3.layoutParams = paramsOld
        binding.image4.layoutParams = paramsOld
        binding.image5.layoutParams = paramsOld
        binding.userNameTxt.text = "Hello, " + sharedPrefManager.user.firstName
//        binding!!.time.text = getTime()

        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        //  joshToday
        // winLevelPoints
        // customerKpiData
        //  wellBeingList
        loadAvatarImage(requireContext(), binding.imageView)
        //  homepageVoucherList()
        //  getRewardPoints()
    }

    private fun getMoodData(it: Resource.Success<JoshRes>) {

        Log.i(TAG, "getMoodData: $it")
        try {
            val jsonObject = it.value
            val resCode = it.value.responseCode
            if (resCode == 200) {
                val data = jsonObject.data
                //                            val list = Gson().fromJson(data.toString(), JoshModel::class.java)
                if (data?.emojiPoint != null) {
                    binding.image1.isClickable = false
                    binding.image2.isClickable = false
                    binding.image3.isClickable = false
                    binding.image4.isClickable = false
                    binding.image5.isClickable = false
                }


                val sdf = SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss",
                    Locale.getDefault()
                )
                var created_at = data?.createdAt
                if (!created_at.isNullOrEmpty()) {
                    val createdAt =
                        sdf.parse(data?.createdAt?.substring(0, 19))
                    val newFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a")
                    val newDate = newFormat.format(createdAt!!)
                    binding.time.text = newDate
                }
                when (data?.emojiPoint) {
                    5 -> {
                        emojiEffect("image5")
                        binding.text5.visibility = View.VISIBLE
                    }
                    4 -> {
                        emojiEffect("image4")
                        binding.text4.visibility = View.VISIBLE
                    }
                    3 -> {
                        emojiEffect("image3")
                        binding.text3.visibility = View.VISIBLE
                    }
                    2 -> {
                        emojiEffect("image2")
                        binding.text2.visibility = View.VISIBLE
                    }
                    1 -> {
                        emojiEffect("image1")
                        binding.text1.visibility = View.VISIBLE
                    }
                    else -> {

                    }
                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIntent(i: Int): Intent {
        val intent = Intent(requireContext(), MyJoshForToday::class.java)
        intent.putExtra("emoji", i)
        return intent
    }

    override fun onResume() {
        super.onResume()
        checkUserLogin()
        if (sharedPrefManager.user.teamId == null) {
            Log.e("TAG", "onResume: Team id not update")
            openDialog()
        } else {
            Log.e("TAG", "onResume: Team id updated")
        }
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

    private fun checkUserLogin() {
        val map: HashMap<String, String> = HashMap()
        map["user_profile_id"] = sharedPrefManager.user.id.toString()
        map["device_id"] = androidId
        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.userLoginCheck(map)
        Log.i(TAG, "checkUserLogin: " + Gson().toJson(call.request()))
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.e(
                    TAG,
                    "onResponse: checkUserLogin code " + response.code() + " res " + response.body()
                )
                //   Log.i(TAG, "onResponse: " + Gson().toJson(response.body()))

                if (response.code() == 401) {
                    mGoogleSignInClient!!.signOut()
                    sharedPrefManager.logout()
                    startActivity(Intent(context, LoginActivity::class.java))
                    Toast.makeText(context, "User Logout Successfully...", Toast.LENGTH_SHORT)
                        .show()
                    activity!!.finish()
                }
                try {
                    Log.e("check", "onResponse: " + response.body())
                    if (response.body() != null) {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val status = jsonObject.getInt("status")
                        if (status == 1) {
                            mGoogleSignInClient!!.signOut()
                            sharedPrefManager.logout()
                            startActivity(Intent(activity, LoginActivity::class.java))
                            Toast.makeText(
                                context,
                                "User Logout Successfully...",
                                Toast.LENGTH_SHORT
                            ).show()
                            activity!!.finish()
                        }
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

    fun emojiEffect(tag: String) {
        paramsOld.rightMargin = 5
        paramsOld.leftMargin = 5
        paramsOld.topMargin = 5
        paramsNew.rightMargin = 5
        paramsNew.leftMargin = 5
        paramsNew.topMargin = 5
        binding.image1.layoutParams = if (tag == "image1") paramsNew else paramsOld
        binding.image2.layoutParams = if (tag == "image2") paramsNew else paramsOld
        binding.image3.layoutParams = if (tag == "image3") paramsNew else paramsOld
        binding.image4.layoutParams = if (tag == "image4") paramsNew else paramsOld
        binding.image5.layoutParams = if (tag == "image5") paramsNew else paramsOld
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
            Constant.showCouponDialog(
                model,
                requireActivity(),
                object : Constant.RedeemRewardCallback {
                    override fun onRedeemed(str: Boolean) {
                        if (str) {
                            val map3: HashMap<String, String> = HashMap()
                            map3["user_profile_id"] = sharedPrefManager.user.id.toString()
                            viewModel.getRewardPoints(map3)
                        }
                    }

                })
        } else {
            Toast.makeText(context, "Voucher not available!", Toast.LENGTH_SHORT).show()
        }
    }


}