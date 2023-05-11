package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.adapter.TopMemberAdapter
import aara.technologies.rewarddragon.adapter.WellbeingAverageAdapter
import aara.technologies.rewarddragon.adapter.WellbeingTypeAdapter
import aara.technologies.rewarddragon.databinding.ActivityTeamWellbeingBinding
import aara.technologies.rewarddragon.model.TeamWellAverage
import aara.technologies.rewarddragon.model.TopMemberModel
import aara.technologies.rewarddragon.model.WellbeingTypeModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.SharedPrefManager
import aara.technologies.rewarddragon.utils.TypeRefresh
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeamWellBeing : AppCompatActivity(), TypeRefresh {

    var binding: ActivityTeamWellbeingBinding? = null
    val map: HashMap<String, String> = hashMapOf()
    var dialog: CustomLoader? = null
    lateinit var jsonObject: JSONObject
    private lateinit var arr: JSONArray
    var selected = 0
    var firstTime = true
    var sharedPrefManager: SharedPrefManager? = null

/*    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityTeamWellbeingBinding.inflate(
            layoutInflater
        )
        return binding!!.root
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamWellbeingBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        sharedPrefManager = SharedPrefManager.getInstance(this)
        dialog = CustomLoader(this@TeamWellBeing, android.R.style.Theme_Translucent_NoTitleBar)
        binding!!.toolbar.toolbarTitle.text =
            sharedPrefManager?.user?.companyName

        binding!!.recyclerView.layoutManager = LinearLayoutManager(this)

        binding!!.recyclerWellbeingType.layoutManager =
            GridLayoutManager(this, 4, RecyclerView.VERTICAL, false)

        binding!!.recyclerTopMember.layoutManager = LinearLayoutManager(this)

        if (firstTime) {
            firstTime = false
            getTeamWellBeingData()
        }
        teamWellbeingTopThreeList()


    }

    private fun getTeamWellBeingData() {
        val adapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_item1,
            arrayOf("ALL", "Today", "Yesterday", "WTD", "MTD", "YTD")
        ) //setting the country_array to spinner
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding!!.timePeriod.adapter = adapter
        binding!!.timePeriod.setSelection(selected)
        binding!!.timePeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                map["time_period_all"] = getTime(p0?.selectedItem.toString())
                println(map)
                getTeamWellBeingList()
                selected = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        if (dialog != null) {
            dialog!!.dismiss()
        }

    }

    override fun onResume() {
        super.onResume()
        if (!firstTime)
            getTeamWellBeingData()
    }

    fun getTime(time: String): String {
        when (time) {
            "Today" -> return "today"
            "Time Period" -> return "all"
            "Yesterday" -> return "yesterday"
            "WTD" -> return "wtd"
            "MTD" -> return "mtd"
            "YTD" -> return "ytd"
            "ALL" -> return "all"
            else -> {}
        }
        return " "
    }

    private fun getTeamWellBeingList() {
        dialog!!.show()
        map["team_id"] = SharedPrefManager.getInstance(this)!!.user.teamId.toString()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.teamWellBeingList(map)
        println(map)
        call.enqueue(object : Callback<JsonObject> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getTeamWellBeingList")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {

                        binding!!.learningHours.text = obj.getString("learning_hours")
                        binding!!.meditationTime.text = obj.getString("meditation_hours")
                        binding!!.wellBeingPercent.progress = obj.getInt("wellbeing_percent")
                        binding!!.stepsTaken.text = obj.getString("steps_count")
                        binding!!.percent.text = obj.getString("wellbeing_percent") + " %"
                        binding!!.heartPoints.text = obj.getString("heart_points_count")

                        val turnsType = object : TypeToken<ArrayList<TeamWellAverage>>() {}.type
                        val list: ArrayList<TeamWellAverage> = Gson().fromJson(
                            obj.getJSONArray("individual_team_wellbeing_percent_lists").toString(),
                            turnsType
                        )
                        println(list.size)
                        if (list.size == 0) {
//                                binding!!.notFound.visibility = View.VISIBLE
                        } else {
//                                binding!!.notFound.visibility = View.GONE
                            binding!!.recyclerView.adapter =
                                WellbeingAverageAdapter(list, this@TeamWellBeing)
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

    private fun teamWellbeingTopThreeList() {
        dialog!!.show()
        val hashmap: HashMap<String, Any> = hashMapOf()
        hashmap["team_id"] = SharedPrefManager(this).user.teamId.toString()
        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.teamWellbeingTopThreeList(hashmap)
        call.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                println("teamWellbeingTopThreeList")
                println(hashmap)
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    try {

                        jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {

                            val list: ArrayList<WellbeingTypeModel> = ArrayList()
                            list.add(
                                WellbeingTypeModel(
                                    ResourcesCompat.getDrawable(
                                        resources,
                                        R.drawable.ic_steps_taken,
                                        null
                                    )!!,
                                    "Body"
                                )
                            )
                            list.add(
                                WellbeingTypeModel(
                                    ResourcesCompat.getDrawable(
                                        resources,
                                        R.drawable.ic_meditation,
                                        null
                                    )!!,
                                    "Mind"
                                )
                            )
                            list.add(
                                WellbeingTypeModel(
                                    ResourcesCompat.getDrawable(
                                        resources,
                                        R.drawable.ic_soul,
                                        null
                                    )!!,
                                    "Soul"
                                )
                            )
                            list.add(
                                WellbeingTypeModel(
                                    ResourcesCompat.getDrawable(
                                        resources,
                                        R.drawable.ic_heart_point,
                                        null
                                    )!!,
                                    "Heart"
                                )
                            )
                            binding!!.recyclerWellbeingType.adapter =
                                WellbeingTypeAdapter(list, this@TeamWellBeing, this@TeamWellBeing)


                        }
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
                dialog!!.dismiss()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                dialog!!.dismiss()
                println(t.message)
            }
        })
    }


    override fun refresh(position: Int) {

        when (position) {
            0 -> {
                arr = jsonObject.getJSONArray("top_three_steps_lists")
            }
            1 -> {
                arr = jsonObject.getJSONArray("top_three_meditation_lists")
            }
            2 -> {
                arr = jsonObject.getJSONArray("top_three_learning_lists")
            }
            3 -> {
                arr = jsonObject.getJSONArray("top_three_heart_lists")
            }
        }

        val turnsType = object : TypeToken<ArrayList<TopMemberModel>>() {}.type
        val list: ArrayList<TopMemberModel> = Gson().fromJson(arr.toString(), turnsType)
        if (list.size == 0) {
            binding!!.recyclerTopMember.visibility = View.GONE
            binding!!.notFound.visibility = View.VISIBLE
        } else {
            binding!!.notFound.visibility = View.GONE
            binding!!.recyclerTopMember.visibility = View.VISIBLE
            binding!!.recyclerTopMember.adapter =
                TopMemberAdapter(list, this)
        }

    }


}