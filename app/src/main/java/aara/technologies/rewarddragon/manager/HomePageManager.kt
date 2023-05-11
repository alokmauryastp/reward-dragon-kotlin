package aara.technologies.rewarddragon.manager


import aara.technologies.rewarddragon.Dashboard
import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.activities.MyLeaderboard
import aara.technologies.rewarddragon.activities.*
import aara.technologies.rewarddragon.adapter.LeaderBoardAdapter
import aara.technologies.rewarddragon.adapter.VoucherAdapter3
import aara.technologies.rewarddragon.dashboardBinding
import aara.technologies.rewarddragon.databinding.ActivityHomepageBinding
import aara.technologies.rewarddragon.model.JoshModel
import aara.technologies.rewarddragon.model.LeaderBoardModel
import aara.technologies.rewarddragon.model.VoucherModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant
import aara.technologies.rewarddragon.utils.Constant.daysToGo
import aara.technologies.rewarddragon.utils.Constant.getMoodText
import aara.technologies.rewarddragon.utils.Constant.loadAvatarImage
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.OnBonusDialogDismissInterface
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class HomePageManager : Fragment(), VoucherAdapter3.RedeemReward, View.OnClickListener {

    var binding: ActivityHomepageBinding? = null
    var dialog: CustomLoader? = null
    private lateinit var ctx: Context
    var welcomeBonusStatus: Boolean = false
    var sharedPrefManager: SharedPrefManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityHomepageBinding.inflate(
            inflater
        )
        ctx = inflater.context

        sharedPrefManager = SharedPrefManager.getInstance(requireContext())
        return binding!!.root
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.userNameTxt.text =
            "Hello, " + SharedPrefManager.getInstance(requireContext())!!.user.firstName

        dialog = CustomLoader(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)

        binding!!.daysToGo.text = daysToGo()

        binding!!.viewAll.setOnClickListener(this)
        binding!!.teamPerformance.setOnClickListener(this)
        binding!!.challengeTv.setOnClickListener(this)
        binding!!.reviewChallenge.setOnClickListener(this)
        binding!!.addCampaign.setOnClickListener(this)
        binding!!.reviewCampaign.setOnClickListener(this)
        binding!!.myJoshLayout.setOnClickListener(this)
        binding!!.teamMoodLayout.setOnClickListener(this)
        binding!!.myReward.setOnClickListener(this)
        binding!!.gameTime.setOnClickListener(this)


        binding!!.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)


        /*    Constant.loginWelcomeObject?.let { showWelcomeBonusDialog(it) }
            if (welcomeBonusStatus) {
                val handler = Handler()
                handler.postDelayed({
                    Constant.loginRewardObject?.let { showBonusDialog(it) }
                }, 5000)
            } else {
                Constant.loginRewardObject?.let { showBonusDialog(it) }
            }*/

        binding!!.teamWellBeing.setOnClickListener {
            startActivity(Intent(context, TeamWellBeing::class.java))

        }

        binding!!.myWellbeing.setOnClickListener {
            (context as Dashboard).addFragment(MyWellBeing())
            dashboardBinding!!.bottomNav.selectedItemId = R.id.wellbeing
        }

        binding!!.recycler.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        joshTodayForManager
        homepageVoucherList()
        getTeamLeaderBoardData()
        getManagerKpiData()
        getWinLevelPoints()
        getWellBeingList()

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
                            homepageVoucherList()
                        }
                    }
                })
        } else {
            Toast.makeText(context, "Voucher not available!", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(JSONException::class)
    private fun showBonusDialog(obj2: JSONObject) {
        Log.i(TAG, "onResponse: $obj2")
        if (obj2.length() > 0) {
            val points = obj2.getString("reward_points")
            val message = obj2.getString("reward_message")

            if (points.toInt() > 0) {
                var alert = Constant.AlertDialog2(
                    Dashboard.context,
                    R.style.ThemeDialogCustom,
                    points,
                    message, listener = object : OnBonusDialogDismissInterface {
                        override fun onDismiss(boolean: Boolean) {
                            Constant.loginRewardObject = null
                        }
                    })
                alert.show()
            }

            Log.i(TAG, "onResponse: reward_points $points reward_message $message")

        } else {
            Log.i(TAG, "onResponse: reward_points null")

        }
    }


    @Throws(JSONException::class)
    private fun showWelcomeBonusDialog(obj2: JSONObject) {
        Log.i(TAG, "onResponse: $obj2")

        if (obj2.length() > 0) {
            val points = obj2.getString("reward_points")
            val message = obj2.getString("reward_message")

            if (points.toInt() > 0) {
                welcomeBonusStatus = true
                var alert = Constant.AlertDialog2(
                    Dashboard.context,
                    R.style.ThemeDialogCustom,
                    points,
                    message, listener = object : OnBonusDialogDismissInterface {
                        override fun onDismiss(boolean: Boolean) {
                            Constant.loginRewardObject = null
                        }
                    })
                alert.show()
            }

            Log.i(TAG, "onResponse: reward_points $points reward_message $message")

        } else {
            Log.i(TAG, "onResponse: reward_points null")

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
                                binding!!.recycler.adapter =
                                    VoucherAdapter3(list, requireContext(), this@HomePageManager)
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


    private fun getWinLevelPoints() {
        val map: HashMap<String, String> = hashMapOf()
        map["employee_id"] = SharedPrefManager(requireContext()).user.id.toString()
        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.getWinLevelPoints(map)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.code() == 200) {
                    assert(response.body() != null)
                    Log.e("getWinLevelPoints", response.body().toString())
                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {
                            binding!!.level.text = jsonObject.getString("win_level")
                            binding!!.point.text = jsonObject.getString("points_won")
                            //  binding!!.pointBalance.text = jsonObject.getString("points_won")

                            binding!!.pointBalance.text =
                                Constant.format((jsonObject.getString("points_won")).toLong())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Log.e("getWinLevelPoints t", t.message!!)
            }
        })
    }

    private fun openDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setIcon(R.mipmap.logo)
        builder.setTitle("Update Profile")
        builder.setCancelable(false)
        builder.setMessage("Your profile details not updated.\nPlease update your complete profile.")
        builder.setPositiveButton(
            "OK"
        ) { _: DialogInterface?, _: Int ->
            (context as Dashboard).addFragment(
                EditProfile()
            )
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun getTeamLeaderBoardData() {
//        {  manager_id=25,  team_id=5}
        dialog!!.show()
        val map: HashMap<String, Any> = hashMapOf()
        map["group_all"] = "top_10"
        map["select_name_all"] = "select_name_all"
        map["time_period_all"] = "time_period_all"
        map["manager_id"] = SharedPrefManager.getInstance(requireContext())!!.user.id.toString()
        map["team_id"] = SharedPrefManager.getInstance(requireContext())!!.user.teamId.toString()
        println(map)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
//        val call = services.getTeamLeaderBoard(map)
        val call = services.getTeamLeaderboardFilter(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getTeamLeaderBoardData")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        val turnsType = object : TypeToken<ArrayList<LeaderBoardModel>>() {}.type
                        val list: ArrayList<LeaderBoardModel> = Gson().fromJson(
                            obj.getJSONArray("team_points_data").toString(),
                            turnsType
                        )
                        println(list.size)
                        binding!!.recyclerView.adapter =
                            LeaderBoardAdapter(
                                if (list.size > 5) list.subList(0, 5) else list,
                                Dashboard.context
                            )
                    }
                }
                dialog!!.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialog!!.dismiss()
                println(t.message)
            }
        })
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(view: View) {
        when (view.id) {
            R.id.view_all -> {
                (context as Dashboard?)!!.addFragment(MyLeaderboard())
                dashboardBinding!!.bottomNav.selectedItemId = R.id.leaderboard
            }
            R.id.team_performance -> startActivity(
                Intent(
                    requireContext(),
                    TeamPerformance::class.java
                )
            )
            R.id.challengeTv -> {
                startActivity(
                    Intent(
                        requireContext(),
                        TeamChallenges::class.java
                    ).putExtra("isOpen", true)
                )
            }
            R.id.review_challenge -> {
                startActivity(
                    Intent(
                        requireContext(),
                        TeamChallenges::class.java
                    ).putExtra("isOpen", false)
                )
            }
            R.id.add_campaign -> {
                startActivity(
                    Intent(
                        requireContext(),
                        TeamCampaign::class.java
                    ).putExtra("isOpen", true)
                )
            }
            R.id.review_campaign -> {
                startActivity(
                    Intent(
                        requireContext(),
                        TeamCampaign::class.java
                    ).putExtra("isOpen", false)
                )
            }
            R.id.my_josh_layout -> startActivity(
                Intent(
                    requireContext(),
                    MyJoshForToday::class.java
                )
            )
            R.id.team_mood_layout -> startActivity(Intent(requireContext(), TeamJosh::class.java))
            R.id.my_reward -> startActivity(Intent(requireContext(), MyReward::class.java))
            R.id.gameTime -> startActivity(Intent(requireContext(), MyGameTime::class.java))
        }
    }

    private val TAG = "HomePageManager"
    private val joshTodayForManager: Unit
        get() {
            val services = RetrofitInstance().getInstance().create(
                DataServices::class.java
            )
            Log.i(TAG, "teamId: ${SharedPrefManager(requireContext()).user.teamId}")
            val call = services.getJoshTodayForManager(
                Objects.requireNonNull(
                    SharedPrefManager(requireContext()).user.id!!
                ),
                Objects.requireNonNull(
                    SharedPrefManager(requireContext()).user.teamId!!.toInt()
                )
            )
            call.enqueue(object : Callback<JsonObject?> {
                override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                    if (response.code() == 200) {
                        assert(response.body() != null)
                        Log.e("getJoshTodayForManager", response.body().toString())
                        try {
                            val jsonObject = JSONObject(Gson().toJson(response.body()))
                            val resCode = jsonObject.getInt("response_code")
                            if (resCode == 200) {
                                val data = jsonObject.getJSONObject("data")
                                val list = Gson().fromJson(data.toString(), JoshModel::class.java)
                                println("list.emoji_point")
                                println(list.emoji_point)
                                if (!data.isNull("emoji_point")) {
                                    println("list.emoji_point")
                                    println(list.emoji_point)
                                    when (list.emoji_point) {
                                        5 -> binding!!.myJosh.setImageResource(R.drawable.happy)
                                        4 -> binding!!.myJosh.setImageResource(R.drawable.smile)
                                        3 -> binding!!.myJosh.setImageResource(R.drawable.okay)
                                        2 -> binding!!.myJosh.setImageResource(R.drawable.amber)
                                        1 -> binding!!.myJosh.setImageResource(R.drawable.angry)
                                        else -> {}
                                    }
                                    binding!!.myMoodTv.text = getMoodText(list.emoji_point)
                                }
                                when (jsonObject.getInt("team_mood")) {
                                    5 -> binding!!.teamMood.setImageResource(R.drawable.happy)
                                    4 -> binding!!.teamMood.setImageResource(R.drawable.smile)
                                    3 -> binding!!.teamMood.setImageResource(R.drawable.okay)
                                    2 -> binding!!.teamMood.setImageResource(R.drawable.amber)
                                    1 -> binding!!.teamMood.setImageResource(R.drawable.angry)
                                    else -> {}
                                }

                                binding!!.teamMoodTv.text =
                                    getMoodText(jsonObject.getInt("team_mood"))
                            }
                        } catch (e: Exception) {
                            println("getJoshTodayForManager e")
                            println(e.message)
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                    Log.e("getJoshToday t", t.message!!)
                }
            })
        }

    private fun getManagerKpiData() {
        val map: HashMap<String, String> = hashMapOf()
        map["manager_id"] = SharedPrefManager.getInstance(requireContext())!!.user.id.toString()
        map["team_id"] = SharedPrefManager.getInstance(requireContext())!!.user.teamId.toString()
        println(map)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getManagerKpiData(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getManagerKpiData")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        binding!!.kpiMet.text = obj.getString("manager_kpi_met_data")
                        binding!!.kpiWip.text = obj.getString("total_kpi_wip")
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        checkUserLogin()
        getTeamWellBeingList()
        loadAvatarImage(requireContext(), binding!!.imageView)
        if (SharedPrefManager(requireContext()).user.teamId == null) {
            Log.e("TAG", "onResume: Team id not update")
            openDialog()
        } else {
            Log.e("TAG", "onResume: Team id updated")
        }
    }


    private fun checkUserLogin() {
        dialog!!.show()
        val hashMap: HashMap<String, String> = hashMapOf()
        hashMap["user_profile_id"] =
            SharedPrefManager.getInstance(requireContext())!!.user.id.toString()
        hashMap["device_id"] = Constant.androidId
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.userLoginCheck(hashMap)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                println("checkUserLogin")
                println(response.code())
                println(response.body())

                // Configure sign-in to request the user's ID, email address, and basic
                // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()
                // Build a GoogleSignInClient with the options specified by gso.

                if (response.code() == 401) {
                    val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
                    mGoogleSignInClient.signOut()
                    SharedPrefManager(requireContext()).logout()
                    startActivity(Intent(activity, LoginActivity::class.java))
                    Toast.makeText(context, "User Logout Successfully...", Toast.LENGTH_SHORT)
                        .show()
                    activity!!.finish()
                }

                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val status = obj.getInt("status")
                    if (status == 1) {
                        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
                        mGoogleSignInClient.signOut()
                        SharedPrefManager.getInstance(requireContext())!!.logout()
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        Toast.makeText(context, "User Logout Successfully...", Toast.LENGTH_SHORT)
                            .show()
                        activity!!.finish()

                    }
                }
                dialog!!.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
                dialog!!.dismiss()
            }
        })
    }


    private fun getWellBeingList() {
        val map: HashMap<String, String> = hashMapOf()
        map["time_period_all"] = "today"
        map["user_profile_id"] =
            SharedPrefManager.getInstance(requireContext())!!.user.id.toString()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.wellBeingList(map)
        println(map)
        call.enqueue(object : Callback<JsonObject> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getWellBeingList")
                println(response.code())
                println(response.body())

                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        val progress = obj.getInt("wellbeing_percent")
                        binding!!.circularMe.setProgress(progress.toFloat())
                        binding!!.percentMe.text = "$progress %"
                        binding!!.percentMe.animate()
                        if (progress < 50) {
                            binding!!.circularMe.progressColor =
                                ContextCompat.getColor(ctx, android.R.color.holo_orange_light)
                            binding!!.circularMe.progressBackgroundColor =
                                ContextCompat.getColor(ctx, android.R.color.holo_orange_light)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
            }
        })
    }

    private fun getTeamWellBeingList() {
        dialog?.show()
        val map: HashMap<String, String> = hashMapOf()
        map["time_period_all"] = "all"
        map["team_id"] = SharedPrefManager.getInstance(requireContext())!!.user.teamId.toString()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.teamWellBeingList(map)
        call.enqueue(object : Callback<JsonObject> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getTeamWellBeingList")
                println(map)
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        val progress = obj.getInt("wellbeing_percent")
                        binding!!.circularTeam.setProgress(progress.toFloat())
                        binding!!.percentTeam.text = "$progress %"
                        binding!!.percentTeam.animate()
                        if (progress > 50) {

                            //   binding!!.circularTeam.progressColor = R.color.progress_color
                            //  binding!!.circularTeam.progressBackgroundColor = R.color.progress_color

                            binding!!.circularTeam.progressColor =
                                ContextCompat.getColor(Dashboard.context, R.color.progress_color)
                            binding!!.circularTeam.progressBackgroundColor =
                                ContextCompat.getColor(Dashboard.context, R.color.progress_color)

                        }
                    }
                }
                dialog?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialog?.dismiss()
                println(t.message)
            }
        })
    }


}