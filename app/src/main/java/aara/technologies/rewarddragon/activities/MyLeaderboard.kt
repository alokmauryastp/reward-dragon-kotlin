package aara.technologies.rewarddragon.activities


import aara.technologies.rewarddragon.Dashboard
import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.adapter.RewardPointsAdapter
import aara.technologies.rewarddragon.databinding.ActivityMyLeaderboardBinding
import aara.technologies.rewarddragon.model.ChallengeModel
import aara.technologies.rewarddragon.model.RewardListModel
import aara.technologies.rewarddragon.model.WinTrendsModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant
import aara.technologies.rewarddragon.utils.Constant.getTime
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.SharedPrefManager
import aara.technologies.rewarddragon.utils.getDateInterface
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class MyLeaderboard : Fragment(), AdapterView.OnItemSelectedListener {

    var firstTime = true


    var binding: ActivityMyLeaderboardBinding? = null
    var dialog: CustomLoader? = null

    //    var rewardListModel: RewardListModel? = null
    var points = 0
    var userId = 0
    private var company = ""
    val map: HashMap<String, Any> = hashMapOf()

    // on below line we are creating array list for bar data
    lateinit var barEntriesList1: ArrayList<BarEntry>

    // on below line we are creating
    // a variable for bar data
    lateinit var barData: BarData

    // on below line we are creating a
    // variable for bar data set
    lateinit var barDataSet1: BarDataSet
    var sharedPrefManager:SharedPrefManager?=null

    var filterdialog2: Constant.AlertDialog3? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityMyLeaderboardBinding.inflate(
            layoutInflater
        )
        return binding!!.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefManager=SharedPrefManager.getInstance(requireContext())

        userId = sharedPrefManager!!.user.id!!.toInt()

        company = sharedPrefManager!!.user.companyName.toString()
        binding!!.share.setOnClickListener {

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Hey, I’ve Won $points Reward Points through Reward Dragon@$company and I am excited to see what’s next."
            )

            startActivity(Intent.createChooser(shareIntent, "Share Via"))
        }


        binding!!.teamRadioBtn.setOnClickListener {
            getEmployeeReward()
        }



        if (sharedPrefManager!!.user.roleId == 1) {
            //  Log.i(TAG, "onResponse: ")
            getCustomerRewardResource("", "all")
        } else {
            getManagerRewardResource("", "all")
        }


        filterdialog2 =
            Constant.AlertDialog3(requireContext(), 0, listener = object :
                getDateInterface {

                override fun getData(date: String, buttonClick: String) {


                    Log.i(TAG, "getData: date $date  buttonclick $buttonClick")
                    if (buttonClick == "DATE") {

                        if (sharedPrefManager!!.user.roleId == 1) {
                            Log.i(TAG, "onResponse: ")
                            getCustomerRewardResource(date, "")
                        } else {
                            getManagerRewardResource(date, "")
                        }
                    } else if (buttonClick == "CLEAR") {


                        if (sharedPrefManager!!.user.roleId == 1) {
                            Log.i(TAG, "onResponse: ")
                            getCustomerRewardResource("", "all")
                        } else {
                            getManagerRewardResource("", "all")
                        }
                    }

                }
            })


        binding!!.yourRadioBtn.setOnClickListener {

            binding!!.filter.visibility = View.VISIBLE

            if (sharedPrefManager!!.user.roleId == 1) {
                Log.i(TAG, "onResponse: ")
                getCustomerRewardResource("", "all")
            } else {
                getManagerRewardResource("", "all")
            }

        }
        dialog = CustomLoader(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)

        binding!!.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding?.filter?.setOnClickListener {
            filterdialog2?.show()
        }


        val adapter3 = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item_black,
            arrayOf("ALL", "Today", "Yesterday", "WTD", "MTD", "YTD")
        ) //setting the country_array to spinner
        adapter3.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding!!.timePeriod1.adapter = adapter3
        binding!!.timePeriod1.setSelection(0, false)
        binding!!.timePeriod1.onItemSelectedListener = this


        val adapterGroup = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item_black,
            arrayOf("Top 10", "Bottom 10")
        ) //setting the country_array to spinner
        adapterGroup.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding!!.groupSpinner.adapter = adapterGroup
        binding!!.groupSpinner.setSelection(0, false)
        binding!!.groupSpinner.onItemSelectedListener = this


        if (firstTime) {
            map["time_period_all"] = "time_period_all"
            map["group_all"] = "top_10"
            myRewardPointList()
            firstTime = false
        }


        binding!!.gameTime.setOnClickListener {
            startActivity(Intent(requireContext(), MyGameTime::class.java))
        }

        binding!!.challengeOfTheDay.setOnClickListener {
            startActivity(Intent(requireContext(), MyLatestChallenge::class.java))
        }

        getWinLevelPoints()
        nextAvailabilityTime()
        getChallengeList()
        getMotivationalMessage()
    }


    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when (p0) {
            binding?.timePeriod1 -> {
                println(p0!!.selectedItem.toString())
                map["time_period_all"] = getTime(p0.selectedItem.toString())
                if (!firstTime) {
                    myRewardPointList()
                }
            }

            binding?.groupSpinner -> {
                val group = p0?.selectedItem.toString()
                map["group_all"] = Constant.getGroup(group)
                // println(map)
                Log.i(TAG, "onItemSelected: $firstTime")
                if (!firstTime) {
                    myRewardPointList()
                }
            }
        }


    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }


    private fun getCustomerRewardResource(date: String, all: String) {
        dialog?.show()

        val hashmap: HashMap<String, Any> = hashMapOf()
        hashmap["user_profile_id"] = SharedPrefManager(requireContext()).user.id.toString()
        hashmap["all"] = all;
        hashmap["date"] = date;
        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.getCustomerRewardResource(hashmap)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(
                call: Call<JsonObject?>,
                response: Response<JsonObject?>
            ) {
                if (response.body() != null && response.code() == 200) {
                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {
                            getRewardData(jsonObject);
                        }
                    } catch (e: Exception) {
                        println(e.message)
                    }
                } else {
                    binding?.notFound?.visibility = View.VISIBLE
                    binding?.pointsLl?.visibility = View.GONE
                }
                dialog?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                dialog?.dismiss()
                println(t.message)
            }
        })
    }


    private fun getEmployeeReward() {
        dialog?.show()
        val hashmap: HashMap<String, Any> = hashMapOf()
        hashmap["team_id"] = SharedPrefManager(requireContext()).user.teamId.toString()
        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.getEmployeeReward(hashmap)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {

                Log.i(TAG, "getEmployeeReward: ${response.body()}")
                if (response.code() == 200) {
                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {
                            binding!!.filter.visibility = View.GONE
                            getRewardDataEmployee(jsonObject)
                            //  Log.i(TAG, "onResponse:getEmployeeReward ${jsonObject.getString("usage_rewards_point")}")
                        }
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
                dialog?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                dialog?.dismiss()
                println(t.message)
            }
        })
    }

    private fun getRewardData(jsonObject: JSONObject) {


        var usesRewards = jsonObject.getInt("usage_rewards_point")
        var challengeCampaignRewards = jsonObject.getInt("challenge_and_campaign_rewards_point")
        var gameRewards = jsonObject.getInt("game_rewards_point")
        var moodRewards = jsonObject.getInt("mood_rewards_point")
        var wellBeingRewards = jsonObject.getInt("wellbeing_rewards_point")
        var performanceLoyaltyRewards =
            jsonObject.getInt("milestone_rewards_point")
        var leaderboardRewards = jsonObject.getInt("leaderboard_rewards_point")
        var kpiRewards = jsonObject.getInt("kpi_rewards_point")
        var redemptionCashbackRewards =
            jsonObject.getInt("redemption_cashback_rewards_point")

        val non_app_rewards_point = jsonObject.getInt("non_app_rewards_point")

        Handler(Looper.getMainLooper()).post {
            var totalPoints = totalPoint(
                    usesRewards,
                    challengeCampaignRewards,
                    gameRewards,
                    moodRewards,
                    wellBeingRewards,
                    performanceLoyaltyRewards,
                    leaderboardRewards,
                    kpiRewards,
                    redemptionCashbackRewards,
                    non_app_rewards_point
                )

            //print("dattttttttttt usesRewards $usesRewards + challengeCampaignRewards $challengeCampaignRewards +gameRewards $gameRewards +moodRewards $moodRewards + wellBeingRewards $wellBeingRewards +performanceLoyaltyRewards $performanceLoyaltyRewards +leaderboardRewards $leaderboardRewards +kpiRewards $kpiRewards +redemptionCashbackRewards $redemptionCashbackRewards \n")

            Log.i(
                TAG,
                "totalpoints: $totalPoints   redemptionCashbackRewards  $redemptionCashbackRewards"
            )

            print("redemptionCashbackRewards")
            var point = ( (490195990).toDouble() * 100 )/ 491768814
            print(point)

            Log.i(
                TAG,
                "totalpoints: $totalPoints   redemptionCashbackRewards  $point"
            )


            if (totalPoints > 0) {
                binding?.notFound?.visibility = View.GONE
                binding?.pointsLl?.visibility = View.VISIBLE

                binding!!.totalPoints.progress = 100
                binding!!.totalPointsTv.text = totalPoints.toString()

                binding!!.usesRewards.progress = (usesRewards.toDouble() * 100 / totalPoints).toInt()

                Log.i(
                    TAG,
                    "nonAppRewards: $non_app_rewards_point "
                )

                binding!!.nonAppRewards.progress = (non_app_rewards_point.toDouble() * 100 / totalPoints).toInt()
                binding!!.nonAppRewardTv.text = non_app_rewards_point.toString()

                binding!!.challengeAndCampaignRewards.progress = (challengeCampaignRewards.toDouble() * 100 / totalPoints).toInt()
                binding!!.gameRewards.progress = (gameRewards.toDouble() * 100 / totalPoints).toInt()
                binding!!.moodRewards.progress = (moodRewards.toDouble() * 100 / totalPoints).toInt()
                binding!!.wellbeingRewards.progress = (wellBeingRewards.toDouble() * 100 / totalPoints).toInt()
                binding!!.performanceLoyaltyRewards.progress = (performanceLoyaltyRewards.toDouble() * 100 / totalPoints).toInt()
                binding!!.leaderboardRewards.progress = (leaderboardRewards.toDouble() * 100 / totalPoints).toInt()
                binding!!.kpiRewards.progress = (kpiRewards.toDouble() * 100 / totalPoints).toInt()
                binding!!.redemptionCashbackRewards.progress = (redemptionCashbackRewards.toDouble() * (100) / totalPoints).toInt()

                print("redemptionCashbackRewards")
                var point = redemptionCashbackRewards * 100 / totalPoints
                print(point)

                binding!!.usesRewardsTv.text = usesRewards.toString()
                binding!!.challengeAndCampaignRewardsTv.text = challengeCampaignRewards.toString()
                binding!!.gameRewardsTv.text = gameRewards.toString()
                binding!!.moodRewardsTv.text = moodRewards.toString()
                binding!!.wellbeingRewardsTv.text = wellBeingRewards.toString()
                binding!!.performanceLoyaltyRewardsTv.text = performanceLoyaltyRewards.toString()
                binding!!.leaderboardRewardsTv.text = leaderboardRewards.toString()
                binding!!.kpiRewardsTv.text = kpiRewards.toString()
                binding!!.redemptionCashbackRewardsTv.text = redemptionCashbackRewards.toString()

                binding!!.challengeAndCampaignLayout.visibility = View.VISIBLE
                binding!!.leaderboardLayout.visibility = View.VISIBLE
            } else {
                binding?.notFound?.visibility = View.VISIBLE
                binding?.pointsLl?.visibility = View.GONE
            }


        }

    }

    private fun totalPoint(
        usesRewards: Int,
        challengeCampaignRewards: Int,
        gameRewards: Int,
        moodRewards: Int,
        wellBeingRewards: Int,
        performanceLoyaltyRewards: Int,
        leaderboardRewards: Int,
        kpiRewards: Int,
        redemptionCashbackRewards: Int,
        nonappreward: Int,
    ) : Int {

        print("usesRewards")
        print(usesRewards)
        print("challengeCampaignRewards")
        print(challengeCampaignRewards)
        print("gameRewards")
        print(gameRewards)
        print("moodRewards")
        print(moodRewards)
        print("wellBeingRewards")
        print(wellBeingRewards)
        print("kpiRewards")
        print(kpiRewards)
        print("leaderboardRewards")
        print(leaderboardRewards)
        var totalPoints = nonappreward + redemptionCashbackRewards + usesRewards + challengeCampaignRewards + gameRewards + moodRewards + wellBeingRewards +performanceLoyaltyRewards + leaderboardRewards + kpiRewards
        print("totalPoints")
        print(totalPoints)

        return totalPoints
    }

    private fun getRewardDataEmployee(jsonObject: JSONObject) {

        var usesRewards = jsonObject.getInt("usage_rewards_point")
        var challengeCampaignRewards =
            jsonObject.getInt("challenge_and_campaign_rewards_point")
        var gameRewards = jsonObject.getInt("game_rewards_point")
        var moodRewards = jsonObject.getInt("mood_rewards_point")
        var wellBeingRewards = jsonObject.getInt("wellbeing_rewards_point")
        var performanceLoyaltyRewards =
            jsonObject.getInt("milestone_rewards_point")
        var leaderboardRewards = jsonObject.getInt("leaderboard_rewards_point")
        var kpiRewards = jsonObject.getInt("kpi_rewards_point")
        var redemptionCashbackRewards = jsonObject.getInt("redemption_cashback_rewards_point")

        var non_app_rewards_point = jsonObject.getInt("non_app_rewards_point")

        Handler(Looper.getMainLooper()).post {
            val totalPoints =
                non_app_rewards_point + usesRewards + challengeCampaignRewards + gameRewards + moodRewards + wellBeingRewards + performanceLoyaltyRewards + leaderboardRewards + kpiRewards + redemptionCashbackRewards

            if (totalPoints > 0) {
                binding?.notFound?.visibility = View.GONE
                binding?.pointsLl?.visibility = View.VISIBLE
//                Log.i(TAG, "getRewardDataEmployee:totalPoints $totalPoints")
                binding!!.totalPoints.progress = 100


                binding!!.nonAppRewards.progress =
                    (non_app_rewards_point.toDouble() * 100 / totalPoints).toInt()
                binding!!.nonAppRewardTv.text = non_app_rewards_point.toString()

                binding!!.usesRewards.progress = (usesRewards.toDouble() * 100 / totalPoints).toInt()
                binding!!.challengeAndCampaignRewards.progress =
                    (challengeCampaignRewards.toDouble() * 100 / totalPoints).toInt()
                binding!!.gameRewards.progress = (gameRewards.toDouble() * 100 / totalPoints).toInt()
                binding!!.moodRewards.progress = (moodRewards.toDouble() * 100 / totalPoints).toInt()
                binding!!.wellbeingRewards.progress =
                    (wellBeingRewards.toDouble() * 100 / totalPoints).toInt()
                binding!!.performanceLoyaltyRewards.progress =
                    (performanceLoyaltyRewards.toDouble() * 100 / totalPoints).toInt()
                binding!!.leaderboardRewards.progress =
                    (leaderboardRewards.toDouble() * 100 / totalPoints).toInt()
                binding!!.kpiRewards.progress = (kpiRewards.toDouble() * 100 / totalPoints).toInt()
                binding!!.redemptionCashbackRewards.progress =
                    (redemptionCashbackRewards.toDouble() * 100 / totalPoints).toInt()

                binding!!.totalPointsTv.text = totalPoints.toString()
                binding!!.usesRewardsTv.text = usesRewards.toString()
                binding!!.challengeAndCampaignRewardsTv.text =
                    challengeCampaignRewards.toString()
                binding!!.gameRewardsTv.text = gameRewards.toString()
                binding!!.moodRewardsTv.text = moodRewards.toString()
                binding!!.wellbeingRewardsTv.text = wellBeingRewards.toString()
                binding!!.performanceLoyaltyRewardsTv.text =
                    performanceLoyaltyRewards.toString()
                binding!!.leaderboardRewardsTv.text = leaderboardRewards.toString()
                binding!!.kpiRewardsTv.text = kpiRewards.toString()
                binding!!.redemptionCashbackRewardsTv.text =
                    redemptionCashbackRewards.toString()

                binding!!.challengeAndCampaignLayout.visibility = View.VISIBLE
                binding!!.leaderboardLayout.visibility = View.VISIBLE
            } else {
                binding?.notFound?.visibility = View.VISIBLE
                binding?.pointsLl?.visibility = View.GONE
            }
        }
    }

    private fun getManagerRewardResource(date: String, all: String) {
        dialog?.show()
        val hashmap: HashMap<String, Any> = hashMapOf()
        hashmap["user_profile_id"] = SharedPrefManager(requireContext()).user.id.toString()
        hashmap["all"] = all;
        hashmap["date"] = date;
        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.getManagerRewardResource(hashmap)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                println("getManagerRewardResource: " + response.body())
                println("roleId: " + sharedPrefManager!!.user.roleId)
                println(hashmap)
                println(response.code())
                // println(response.body())
                if (response.code() == 200) {
                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {


                            val usesRewards = jsonObject.getInt("usage_rewards_point")
                            val non_app_rewards_point = jsonObject.getInt("non_app_rewards_point")

//                            val challengeCampaignRewards = jsonObject.getInt("challenge_and_campaign_rewards_point")
                            val gameRewards = jsonObject.getInt("game_rewards_point")

                            val moodRewards = jsonObject.getInt("mood_rewards_point")

                            val wellBeingRewards = jsonObject.getInt("wellbeing_rewards_point")

                            val performanceLoyaltyRewards = jsonObject.getInt("milestone_rewards_point")

//                            val leaderboardRewards = jsonObject.getInt("leaderboard_rewards_point")

                            val redemptionCashbackRewards = jsonObject.getInt("redemption_cashback_rewards_point")

                            val kpi_rewards_point = jsonObject.getInt("kpi_rewards_point")

                            Handler(Looper.getMainLooper()).post(Runnable {

                                var totalPoints = non_app_rewards_point + usesRewards + gameRewards + moodRewards + wellBeingRewards + performanceLoyaltyRewards + redemptionCashbackRewards + kpi_rewards_point

                                Log.i(
                                    TAG,
                                    "onResponse:manager total $totalPoints   kpi reward "
                                )

                                if (totalPoints > 0) {

                                    binding?.notFound?.visibility = View.GONE
                                    binding?.pointsLl?.visibility = View.VISIBLE

                                    binding!!.totalPoints.progress = 100


                                    Log.i(
                                        TAG,
                                        "nonAppRewards: $non_app_rewards_point "
                                    )

                                    binding!!.nonAppRewards.progress =
                                        (non_app_rewards_point.toDouble() * 100 / totalPoints).toInt()
                                    binding!!.nonAppRewardTv.text = non_app_rewards_point.toString()


                                    binding!!.usesRewards.progress = (usesRewards.toDouble() * 100 / totalPoints).toInt()
//                            binding!!.challengeAndCampaignRewards.progress = challengeCampaignRewards*100/totalPoints
                                    binding!!.gameRewards.progress = (gameRewards.toDouble() * 100 / totalPoints).toInt()
                                    binding!!.moodRewards.progress = (moodRewards.toDouble() * 100 / totalPoints).toInt()
                                    binding!!.wellbeingRewards.progress =(wellBeingRewards.toDouble() * 100 / totalPoints).toInt()
                                    binding!!.kpiRewards.progress = (kpi_rewards_point.toDouble() * 100 / totalPoints).toInt()

                                    binding!!.performanceLoyaltyRewards.progress =
                                        (performanceLoyaltyRewards.toDouble() * 100 / totalPoints).toInt()
//                            binding!!.leaderboardRewards.progress = leaderboardRewards*100/totalPoints
                                    binding!!.redemptionCashbackRewards.progress =
                                        (redemptionCashbackRewards.toDouble() * 100 / totalPoints).toInt()

                                    binding!!.totalPointsTv.text = totalPoints.toString()
                                    binding!!.usesRewardsTv.text = usesRewards.toString()
//                            binding!!.challengeAndCampaignRewardsTv.text = challengeCampaignRewards.toString()
                                    binding!!.gameRewardsTv.text = gameRewards.toString()
                                    binding!!.moodRewardsTv.text = moodRewards.toString()
                                    binding!!.wellbeingRewardsTv.text = wellBeingRewards.toString()
                                    binding!!.kpiRewardsTv.text = kpi_rewards_point.toString()
                                    binding!!.performanceLoyaltyRewardsTv.text = performanceLoyaltyRewards.toString()
//                            binding!!.leaderboardRewardsTv.text = leaderboardRewards.toString()
                                    binding!!.redemptionCashbackRewardsTv.text = redemptionCashbackRewards.toString()


                                    binding!!.challengeAndCampaignLayout.visibility = View.GONE
                                    binding!!.leaderboardLayout.visibility = View.GONE

                                } else {
                                    binding?.notFound?.visibility = View.VISIBLE
                                    binding?.pointsLl?.visibility = View.GONE
                                }

                            })

                        }
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
                dialog?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                dialog?.dismiss()
                println(t.message)
            }
        })
    }

    private fun myLeaderboardWinTrends() {
        dialog!!.show()
        map["employee_id"] = sharedPrefManager!!.user.id.toString()
        println(map)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.myLeaderboardWinTrends(map)
        println(Gson().toJson(call.request()))
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("myLeaderboardWinTrends")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val jsonObject = JSONObject(Gson().toJson(response.body()))
                    val responseCode: Int = jsonObject.getInt("response_code")
                    if (responseCode == 200) {
                        val arr = jsonObject.getJSONArray("data")
                        val turnsType = object : TypeToken<List<WinTrendsModel>>() {}.type
                        val list: ArrayList<WinTrendsModel> =
                            Gson().fromJson(arr.toString(), turnsType)
                        if (list.size == 0) {
                            binding!!.charts.visibility = View.GONE
                            binding!!.notFound.visibility = View.VISIBLE
                        } else {
                            binding!!.charts.visibility = View.VISIBLE
                            binding!!.notFound.visibility = View.GONE

                            //Pie Chart


                            // on below line we are disabling our legend for pie chart
                            binding!!.pieChart.legend.isEnabled = false
                            binding!!.pieChart.setEntryLabelColor(Color.WHITE)
                            binding!!.pieChart.setCenterTextColor(Color.WHITE)
                            binding!!.pieChart.setEntryLabelTextSize(8f)
                            binding!!.pieChart.description.isEnabled = false

                            // on below line we are creating array list and
                            // adding data to it to display in pie chart
                            val entries: ArrayList<PieEntry> = ArrayList()

                            for (model in list) {
                                entries.add(
                                    PieEntry(
                                        model.game_bonus_point.toFloat(), model.game_name
                                    )
                                )
                            }

                            // on below line we are setting pie data set
                            val dataSet = PieDataSet(entries, "Mobile OS")

                            // on below line we are setting slice for pie
                            dataSet.sliceSpace = 3f
                            dataSet.iconsOffset = MPPointF(0f, 40f)
                            dataSet.selectionShift = 5f

                            // add a lot of colors to list
                            val colors: ArrayList<Int> = ArrayList()
                            colors.add(ContextCompat.getColor(requireContext(), R.color.green))
                            colors.add(ContextCompat.getColor(requireContext(), R.color.green))
                            colors.add(ContextCompat.getColor(requireContext(), R.color.green))

                            // on below line we are setting colors.
                            dataSet.colors = colors

                            val data = PieData(dataSet)
                            binding!!.pieChart.data = data
                            // on below line we are setting animation for our pie chart
                            binding!!.pieChart.animateY(1400, Easing.EaseInOutQuad)
                            binding!!.pieChart.invalidate()

                            // Bar Chart

                            barEntriesList1 = ArrayList()
//                            val teamJosh = j.getJSONObject("team_josh_percent")

                            val labels = ArrayList<String>()

                            for (i in 0 until list.size) {
                                val model = list[i]
                                labels.add(model.game_name)
                                barEntriesList1.add(
                                    BarEntry(
                                        i.toFloat(),
                                        model.game_bonus_point.toFloat(),
                                    )
                                )
                            }

                            // on below line we are initializing our bar data set
                            barDataSet1 = BarDataSet(barEntriesList1, "Game")

                            // on below line we are initializing our bar data
                            barData = BarData(barDataSet1)
                            barData.barWidth = .3f


                            // on below line we are setting data to our bar chart
                            binding!!.barChart.data = barData
                            binding!!.barChart.axisLeft.textColor =
                                ContextCompat.getColor(requireContext(), R.color.white)
                            binding!!.barChart.axisRight.textColor =
                                ContextCompat.getColor(requireContext(), R.color.white)
                            binding!!.barChart.legend.textColor =
                                ContextCompat.getColor(requireContext(), R.color.white)

                            // on below line we are setting colors for our bar chart text
                            barDataSet1.valueTextColor = Color.WHITE


                            // on below line we are setting color for our bar data set
                            barDataSet1.color =
                                ContextCompat.getColor(requireContext(), R.color.green)

                            // on below line we are setting text size
                            barDataSet1.valueTextSize = 8f

                            // below line is to set minimum
                            // axis to our chart.
//                            binding!!.barChart.xAxis.axisMinimum = 0f

                            // on below line setting x axis
                            val xAxis = binding!!.barChart.xAxis
                            xAxis.setAvoidFirstLastClipping(false)

                            val yAxis = binding!!.barChart.axisRight
                            yAxis.isEnabled = false

                            // below line is to set value formatter to our x-axis and
                            // we are adding our days to our x axis.
                            xAxis.valueFormatter = IndexAxisValueFormatter(
                                labels
                            )
                            xAxis.textColor =
                                ContextCompat.getColor(requireContext(), R.color.white)


                            // below line is to set center axis
                            // labels to our bar chart.
                            xAxis.setCenterAxisLabels(false)
                            // below line is to set position
                            // to our x-axis to bottom.
                            xAxis.position = XAxis.XAxisPosition.BOTTOM

                            // below line is to set granularity
                            // to our x axis labels.
                            xAxis.granularity = 1.0f
                            // below line is to enable
                            // granularity to our x axis.
                            xAxis.isGranularityEnabled = true

                            // below line is to make our
                            // bar chart as draggable.
                            binding!!.barChart.isDragEnabled = true

                            // below line is to make visible
                            // range for our bar chart.
                            binding!!.barChart.setVisibleXRangeMaximum(3f)

                            // below line is to
                            // animate our chart.
                            binding!!.barChart.animate()

                            // on below line we are enabling description as false
                            binding!!.barChart.description.isEnabled = false
//                            binding!!.barChart.groupBars(0f, 0.1f, 0.1f)
                            binding!!.barChart.animateY(1400, Easing.EaseInOutQuad)

                            binding!!.barChart.invalidate()


                        }


                    }
                }
                dialog?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
                dialog?.dismiss()
            }
        })

    }

    private fun getMotivationalMessage() {
        val map: HashMap<String, Any> = hashMapOf()
        map["employee_id"] = SharedPrefManager(requireContext()).user.id.toString()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getMotivationalMessage(map)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                println("getMotivationalMessage")
                println(response.code())
                if (response.code() == 200) {

                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {
                            binding!!.motivationalMsz.text =
                                jsonObject.getString("motivation_message")
//
//                            binding!!.gamesPlayed.text = jsonObject.getString("total_played_games")
//                            binding!!.gamePoint.text = jsonObject.getString("total_bonus")

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Log.e("getMotivation t", t.message!!)
            }
        })
    }

    private fun getChallengeList() {
        dialog!!.show()
        val map: HashMap<String, String> = HashMap()
        map["unique_code"] =
            sharedPrefManager!!.user.uniqueCode.toString()
        map["team_id"] = sharedPrefManager!!.user.teamId.toString()
        map["employee_id"] = sharedPrefManager!!.user.id.toString()
        println(map)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val result = services.getChallengeList(map)
        println(Gson().toJson(result.request()))
        result.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getChallengeList")
                println(response.code())
                if (response.code() == 200) {
                    val jsonObject = JSONObject(Gson().toJson(response.body()))
                    val responseCode: Int = jsonObject.getInt("response_code")
                    if (responseCode == 200) {
                        val jsonArray = JSONArray(
                            Gson().toJson(
                                response.body()?.getAsJsonArray("team_challenge_lists")
                            )
                        )
                        val turnsType = object : TypeToken<ArrayList<ChallengeModel>>() {}.type
                        val list: ArrayList<ChallengeModel> =
                            Gson().fromJson(jsonArray.toString(), turnsType)
                        println(list.size)
                        if (list.size > 0) {
                            val time = list[0].endTime.toString()
                            printDifferenceDateForHours(
                                time,
                                binding!!.challengeTimer,
                                "yyyy-MM-dd HH:mm:ss"
                            )
                        }
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

    private fun getWinLevelPoints() {
        val map: HashMap<String, String> = hashMapOf()
        map["employee_id"] = SharedPrefManager(requireContext()).user.id.toString()
        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.getWinLevelPoints(map)
        call.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.code() == 200) {
                    assert(response.body() != null)
                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {
                            points = jsonObject.getInt("points_won")
                            binding!!.level.text =
                                jsonObject.getString("win_level")
                            binding!!.point.text =
                                jsonObject.getString("points_won")


                            val earnPoints = jsonObject.getInt("points_won")


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

    private val TAG = "MyLeaderboard"

    private fun myRewardPointList() {
        dialog!!.show()
//        val map: HashMap<String, String> = hashMapOf()
        map["select_name_all"] = "select_name_all"
        map["manager_id"] = sharedPrefManager!!.user.id.toString()
        map["team_id"] = sharedPrefManager!!.user.teamId.toString()
        println(map)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getTeamLeaderboardFilter(map)
//        val call = services.myRewardPointList(map)

        Log.i(TAG, "myRewardPointList: req ${Gson().toJson(call.request())}")
        call.enqueue(object : Callback<JsonObject> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        val jsonArray =
                            JSONArray(
                                Gson().toJson(
                                    response.body()?.getAsJsonArray("team_points_data")
                                )
                            )
                        val turnsType = object : TypeToken<List<RewardListModel>>() {}.type
                        val list: ArrayList<RewardListModel> =
                            Gson().fromJson(jsonArray.toString(), turnsType)
//                        list.addAll(list)
//                        for (i in 0 until 3) {
//                            list.addAll(list)
//                        }
                        //   Log.i(TAG, "onResponse: $list")
                        if (list != null) {
                            when (list.size) {
                                0 -> {
                                    binding!!.recyclerView.adapter = null
                                    binding!!.noDataFound.visibility = View.VISIBLE
                                    binding!!.topThreeLayout.visibility = View.GONE
                                }
                                1 -> {
                                    binding!!.topThreeLayout.visibility = View.VISIBLE
                                    binding!!.user1.text =
                                        list[0].employee_name + "\n" + list[0].earned_point
                                    Glide
                                        .with(Dashboard.context)
                                        .load(list[0].user_image)
                                        .placeholder(R.mipmap.logo)
                                        .apply(
                                            RequestOptions().override(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                400
                                            )
                                        )
                                        .fitCenter()
                                        .into(binding!!.firstRank)
                                }
                                2 -> {
                                    binding!!.topThreeLayout.visibility = View.VISIBLE
                                    binding!!.user1.text =
                                        list[0].employee_name + "\n" + list[0].earned_point
                                    binding!!.user2.text =
                                        list[1].employee_name + "\n" + list[1].earned_point
                                    Glide
                                        .with(Dashboard.context)
                                        .load(list[0].user_image)
                                        .placeholder(R.mipmap.logo)
                                        .apply(
                                            RequestOptions().override(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                400
                                            )
                                        )
                                        .fitCenter()
                                        .into(binding!!.firstRank)
                                    Glide
                                        .with(Dashboard.context)
                                        .load(list[1].user_image)
                                        .placeholder(R.mipmap.logo)
                                        .apply(
                                            RequestOptions().override(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                400
                                            )
                                        )
                                        .fitCenter()
                                        .into(binding!!.secondRank)
                                }
                                3 -> {
                                    binding!!.topThreeLayout.visibility = View.VISIBLE
                                    binding!!.user1.text =
                                        list[0].employee_name + "\n" + list[0].earned_point
                                    binding!!.user2.text =
                                        list[1].employee_name + "\n" + list[1].earned_point
                                    binding!!.user3.text =
                                        list[2].employee_name + "\n" + list[2].earned_point
                                    Glide
                                        .with(Dashboard.context)
                                        .load(list[0].user_image)
                                        .placeholder(R.mipmap.logo)
                                        .apply(
                                            RequestOptions().override(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                400
                                            )
                                        )
                                        .fitCenter()
                                        .into(binding!!.firstRank)
                                    Glide
                                        .with(Dashboard.context)
                                        .load(list[1].user_image)
                                        .placeholder(R.mipmap.logo)
                                        .apply(
                                            RequestOptions().override(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                400
                                            )
                                        )
                                        .fitCenter()
                                        .into(binding!!.secondRank)
                                    Glide
                                        .with(Dashboard.context)
                                        .load(list[2].user_image)
                                        .placeholder(R.mipmap.logo)
                                        .apply(
                                            RequestOptions().override(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                400
                                            )
                                        )
                                        .fitCenter()
                                        .into(binding!!.thirdRank)
                                }
                                else -> {
                                    Glide
                                        .with(Dashboard.context)
                                        .load(list[0].user_image)
                                        .placeholder(R.mipmap.logo)
                                        .apply(
                                            RequestOptions().override(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                400
                                            )
                                        )
                                        .fitCenter()
                                        .into(binding!!.firstRank)
                                    Glide
                                        .with(Dashboard.context)
                                        .load(list[1].user_image)
                                        .placeholder(R.mipmap.logo)
                                        .apply(
                                            RequestOptions().override(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                400
                                            )
                                        )
                                        .fitCenter()
                                        .into(binding!!.secondRank)
                                    Glide
                                        .with(Dashboard.context)
                                        .load(list[2].user_image)
                                        .placeholder(R.mipmap.logo)
                                        .apply(
                                            RequestOptions().override(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                400
                                            )
                                        )
                                        .fitCenter()
                                        .into(binding!!.thirdRank)
                                    binding!!.topThreeLayout.visibility = View.VISIBLE
                                    binding!!.user1.text =
                                        list[0].employee_name + "\n" + list[0].earned_point
                                    binding!!.user2.text =
                                        list[1].employee_name + "\n" + list[1].earned_point
                                    binding!!.user3.text =
                                        list[2].employee_name + "\n" + list[2].earned_point
                                    binding!!.noDataFound.visibility = View.GONE

                                }
                            }
                            val adapter = RewardPointsAdapter(list, Dashboard.context)
                            binding!!.recyclerView.adapter = adapter
                        }
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

    private fun nextAvailabilityTime() {
        val map: HashMap<String, String> = hashMapOf()
        map["user_profile_id"] = SharedPrefManager(requireContext()).user.id.toString()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.nextAvailabilityTime(map)
        call.enqueue(object : Callback<JsonObject> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("nextAvailabilityTime")
                println(response.code())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    if (obj.isNull("Data")) {
                        binding!!.timer.text = "00 : 00 : 00"
//                        Constant.gameClickable = true

                    } else {

//                        binding!!.gameCounter.visibility = View.VISIBLE

//                        Constant.gameClickable = false

                        val time = obj.getJSONObject("Data").getString("next_availability_time")

                        printDifferenceDateForHours(time, binding!!.timer, "yyyy-MM-dd'T'HH:mm:ss")

                    }

                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun printDifferenceDateForHours(time: String, timer: TextView, pattern: String) {
        // 2022-08-06T19:04:36
        val currentTime = Calendar.getInstance().time
//        val endDateDay = "06/08/2022 21:00:00"
//        val endDateDay = "2022-08-06T19:04:36"
        val format1 = SimpleDateFormat(pattern, Locale.getDefault())
        var endDate: Date? = null

        endDate = if (pattern == "yyyy-MM-dd HH:mm:ss") {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val currentDate = sdf.format(Date())
            format1.parse("$currentDate $time")
        } else {
            format1.parse(time)
        }
        //milliseconds
        val different = endDate.time - currentTime.time
        var countDownTimer = object : CountDownTimer(different, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                var diff = millisUntilFinished
                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                val hoursInMilli = minutesInMilli * 60
                val daysInMilli = hoursInMilli * 24

//                val elapsedDays = diff / daysInMilli
                diff %= daysInMilli

                val elapsedHours = diff / hoursInMilli
                diff %= hoursInMilli

                val elapsedMinutes = diff / minutesInMilli
                diff %= minutesInMilli

                val elapsedSeconds = diff / secondsInMilli

                val hour = (if (elapsedHours > 9) elapsedHours else "0$elapsedHours")
                val minute = (if (elapsedMinutes > 9) elapsedMinutes else "0$elapsedMinutes")
                val seconds = if (elapsedSeconds > 9) elapsedSeconds else "0$elapsedSeconds"

                timer.text = "$hour : $minute : $seconds"

            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                timer.text = "00 : 00 : 00"
//                Constant.gameClickable = true
            }
        }.start()
    }


}