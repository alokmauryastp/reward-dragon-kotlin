package aara.technologies.rewarddragon.manager

import aara.technologies.rewarddragon.activities.TeamCampaignBottomSheet
import aara.technologies.rewarddragon.adapter.TeamCampaignAdapter
import aara.technologies.rewarddragon.databinding.ActivityTeamCampaignBinding
import aara.technologies.rewarddragon.model.CampaignData
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant.loadAvatarImage
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.OnRefresh
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeamCampaign : AppCompatActivity(), View.OnClickListener, OnRefresh {
    var binding: ActivityTeamCampaignBinding? = null
    private val TAG = "TeamCampaign"
    var customLoader: CustomLoader? = null
    lateinit var context: Context

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamCampaignBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        context = this
        binding!!.toolbar.toolbarTitle.text =
            SharedPrefManager.getInstance(context)!!.user.companyName
        com.bumptech.glide.Glide.with(context).load(
            SharedPrefManager.getInstance(context)!!
                .getString(aara.technologies.rewarddragon.utils.SharedPrefManager.Companion.KEY_COMPANY_IMAGE)!!
        ).into(binding!!.toolbar.companyLogo)
        binding!!.toolbar.back.setOnClickListener { finish() }
        binding!!.recyclerview.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        customLoader = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)

        binding!!.userNameTxt.text =
            SharedPrefManager.getInstance(context)!!.user.firstName + " " + SharedPrefManager.getInstance(
                context
            )!!.user.lastName
        binding!!.designation.text =
            SharedPrefManager.getInstance(context)!!.user.designation + ", " + SharedPrefManager.getInstance(
                context
            )!!.user.teamName

        binding!!.addCampaignBtn.setOnClickListener(this)

        binding!!.addCampaignBtn.setOnClickListener {
            TeamCampaignBottomSheet(this).show(supportFragmentManager, TAG)
        }

//        val isOpen = arguments?.getBoolean("isOpen")

        val isOpen = intent?.getBooleanExtra("isOpen", false)
        println("isOpen")
        println(isOpen)
        if (isOpen == true) {
            TeamCampaignBottomSheet(this).show(supportFragmentManager, TAG)
        }


        loadAvatarImage(context, binding!!.imageView)
    }


    override fun onClick(view: View) {
        when (view.id) {
            //  R.id.addCampaignBtn -> binding!!.addcampaignLayout.visibility = View.VISIBLE
            /*   R.id.broadcastBtn -> {
                   Toast.makeText(context, "Campaign Broadcast Successful !", Toast.LENGTH_LONG).show()*/
            //   binding!!.msgTv.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        getTeamCampaign()
    }

    fun getTeamCampaign() {
        customLoader?.show()
        val map: HashMap<String, String> = HashMap()
        map["user_id"] = SharedPrefManager.getInstance(context)!!.user.id.toString()
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = dataServices.getTeamCampaign(map)

        Log.i(TAG, "getTeamCampaign:req ${Gson().toJson(call.request())}")
        call.enqueue(object : Callback<JsonObject> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                println("getTeamCampaign")
                println(response.code())
                println(response.body())
                customLoader?.dismiss()
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val jsonArray = obj.getJSONArray("campaign_data")
                    val turnsType = object : TypeToken<List<CampaignData>>() {}.type
                    val list: ArrayList<CampaignData> = Gson().fromJson(jsonArray.toString(), turnsType)
                    binding!!.count.text = "Campaigns " + obj.getString("total_campaigns_running")
                    val adapter = TeamCampaignAdapter(
                        list, context,
                        object : TeamCampaignAdapter.ClickListener {
                            override fun onClickListener(
                                challenge_id: Int,
                                broadcast_id: Int
                            ) {
                                sendBroadcastStatus(challenge_id, broadcast_id)
                            }
                        }, this@TeamCampaign
                    )
                    binding!!.recyclerview.adapter = adapter
                    if (list.isEmpty()) {
                        binding!!.notFound.visibility = View.VISIBLE
                    } else {
                        binding!!.notFound.visibility = View.GONE
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.i("teamCampaignListRes", t.message.toString())
                customLoader?.dismiss()
            }
        })
    }

    fun sendBroadcastStatus(challenge_id: Int, broadcast_id: Int) {
        customLoader?.show()
        val map: HashMap<String, String> = HashMap()
        map["user_id"] = SharedPrefManager.getInstance(context)!!.user.id.toString()
        map["campaign_id"] = challenge_id.toString()
        map["broadcast_id"] = broadcast_id.toString()
        println("sendBroadCastStatus $map")
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = dataServices.updateCampaignBroadCastStatus(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj: JsonObject? = response.body()
                    Toast.makeText(context, "" + obj?.get("message"), Toast.LENGTH_SHORT).show()
                    getTeamCampaign()
                }
                customLoader?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(context, "" + t.message, Toast.LENGTH_SHORT).show()
                customLoader?.dismiss()
            }
        })
    }

    override fun refresh() {
        getTeamCampaign()
    }
}