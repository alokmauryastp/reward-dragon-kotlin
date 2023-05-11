package aara.technologies.rewarddragon.manager

import aara.technologies.rewarddragon.activities.TeamChallengeBottomSheet
import aara.technologies.rewarddragon.adapter.TeamChallengesAdapter
import aara.technologies.rewarddragon.adapter.TeamChallengesAdapter.ClickListener
import aara.technologies.rewarddragon.databinding.ActivityTeamChallengesBinding
import aara.technologies.rewarddragon.model.ChallengeData
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant.convert12to24format
import aara.technologies.rewarddragon.utils.Constant.getCurrentDate
import aara.technologies.rewarddragon.utils.Constant.getCurrentTime
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.OnRefresh
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeamChallenges : AppCompatActivity(), OnRefresh {

    var binding: ActivityTeamChallengesBinding? = null
    val recyclerView: RecyclerView? = null
    val adapter: TeamChallengesAdapter? = null
    var customLoader: CustomLoader? = null
    private val TAG = "TeamChallenges"
    lateinit var context: Context

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTeamChallengesBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        context = this

        binding!!.toolbar.toolbarTitle.text = SharedPrefManager.getInstance(context)!!.user.companyName
                com.bumptech.glide.Glide.with(context).load(SharedPrefManager.getInstance(context)!!.getString(SharedPrefManager.KEY_COMPANY_IMAGE)!!).into(binding!!.toolbar.companyLogo)
        binding!!.toolbar.back.setOnClickListener { finish() }
        

        binding!!.userNameTxt.text =
            SharedPrefManager.getInstance(context)!!.user.firstName + " " + SharedPrefManager.getInstance(
                context
            )!!.user.lastName
        binding!!.designation.text =
            SharedPrefManager.getInstance(context)!!.user.designation + ", " + SharedPrefManager.getInstance(
                context
            )!!.user.teamName
        customLoader = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)

        binding!!.recyclerview.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        binding!!.recyclerview.adapter = adapter
        
        binding?.createChallenge?.setOnClickListener {
            TeamChallengeBottomSheet(this).show(supportFragmentManager, TAG)
        }

//        val isOpen = arguments?.getBoolean("isOpen")
        val isOpen = intent?.getBooleanExtra("isOpen",false)
        if (isOpen == true) {
            TeamChallengeBottomSheet(this).show(supportFragmentManager, TAG)
        }
      /*  println("convert12to24format")
        println(convert12to24format("09:15:01 PM"))
        Log.i(TAG, "onViewCreated: ")*/
    }

    override fun onResume() {
        super.onResume()
        getTeamChallenges()
        Log.i(TAG, "onResume: ")
    }

    private fun getTeamChallenges() {
        customLoader?.show()
        val map: HashMap<String, Any> = HashMap()
        map["user_id"] = SharedPrefManager.getInstance(context)!!.user.id!!
        map["team_id"] = SharedPrefManager.getInstance(context)!!.user.teamId!!.toInt()
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = dataServices.getTeamChallenges(map)
        call.enqueue(object : Callback<JsonObject> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                println("getTeamChallenges")
                println(map)
                println(response.code())
                println(response.body())
                customLoader?.dismiss()
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val jsonArray = obj.getJSONArray("challenge_data")
                    val turnsType = object : TypeToken<List<ChallengeData>>() {}.type
                    val list: ArrayList<ChallengeData> =
                        Gson().fromJson(jsonArray.toString(), turnsType)
                    binding!!.count.text = "Challenges " + obj.getString("total_challenges_running")
                    val adapter = TeamChallengesAdapter(
                        list, context,
                        object : ClickListener {
                            override fun onClickListener(
                                challenge_id: Int,
                                broadcast_id: Int
                            ) {
                                sendBroadcastStatus(challenge_id, broadcast_id)
                            }
                        }, this@TeamChallenges
                    )
                    binding!!.recyclerview.adapter = adapter

                    if (list.size == 0) {
                        binding!!.notFound.visibility = View.VISIBLE
                    } else {
                        binding!!.notFound.visibility = View.GONE
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                //  Log.i("teamChallengeListRes",t.)
                customLoader?.dismiss()
            }
        })
    }

    fun sendBroadcastStatus(challenge_id: Int, broadcast_id: Int) {
        customLoader?.show()
        val map: HashMap<String, String> = HashMap()
        map["user_id"] = SharedPrefManager.getInstance(context)!!.user.id.toString()
        map["challenge_id"] = challenge_id.toString()
        map["broadcast_id"] = broadcast_id.toString()
        map["manager_updated_date"] = getCurrentDate()
        map["manager_updated_time"] = getCurrentTime()
        println("sendBroadCastStatus $map")
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = dataServices.updateBroadCastStatus(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("sendBroadcastStatus")
                println(response.code())
                println(response.body())
                val obj: JsonObject? = response.body()
                Toast.makeText(context, "" + obj?.get("message"), Toast.LENGTH_SHORT).show()
                getTeamChallenges()
                customLoader?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(context, "" + t.message, Toast.LENGTH_SHORT).show()
                customLoader?.dismiss()
            }
        })
    }

    override fun refresh() {

        getTeamChallenges()

    }
}

