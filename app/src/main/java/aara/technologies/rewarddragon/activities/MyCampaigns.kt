package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.adapter.MyCampaignAdapter
import aara.technologies.rewarddragon.databinding.ActivityMyCampaignsBinding
import aara.technologies.rewarddragon.model.CampaignModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant.getTime
import aara.technologies.rewarddragon.utils.Constant.loadAvatarImage
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.SharedPrefManager
import aara.technologies.rewarddragon.utils.SharedPrefManager.Companion.KEY_COMPANY_IMAGE
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCampaigns : AppCompatActivity() {

    var binding: ActivityMyCampaignsBinding? = null
    var dialog: CustomLoader? = null
    lateinit var context: Context
    
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCampaignsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        context =this
        binding!!.toolbar.toolbarTitle.text = SharedPrefManager.getInstance(context)!!.user.companyName
        com.bumptech.glide.Glide.with(context).load(SharedPrefManager.getInstance(context)!!.getString(KEY_COMPANY_IMAGE)!!).into(binding!!.toolbar.companyLogo)
        binding!!.toolbar.back.setOnClickListener { finish() }
        dialog = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)
        dialog!!.setCancelable(false)

        
        binding!!.userNameTxt.text =
            SharedPrefManager.getInstance(context)!!.user.firstName + " " + SharedPrefManager.getInstance(
                context
            )!!.user.lastName
        binding!!.designation.text =
            SharedPrefManager.getInstance(context)!!.user.designation + ", " + SharedPrefManager.getInstance(
                context
            )!!.user.teamName


        binding!!.latestChallenges.setOnClickListener {
            startActivity(Intent(applicationContext,MyLatestChallenge::class.java))
        }
        binding!!.myGameTime.setOnClickListener {
            startActivity(Intent(context,MyGameTime::class.java))
        }

        binding!!.recyclerView.layoutManager = LinearLayoutManager(context)

        binding!!.time.text = "Data last refreshed on ${getTime()}"

        getCampaignList()

        loadAvatarImage(context, binding!!.imageView)






    }

    private fun  getCampaignPoint(){
        dialog!!.show()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getCampaignPoint(SharedPrefManager.getInstance(context)!!.user.id.toString())
        call.enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println()
                println(response.code())
                println(response.body())
                if (response.code()==200){
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode==200){
                        println(resCode)

//                        binding!!.played.text =  obj.getString("campaign_played_count")
//                        binding!!.won.text =  obj.getString("campaign_won_count")
//                        binding!!.bonusPoints.text = if (!obj.isNull("campaign_bonus_point_count")) obj.getString("campaign_bonus_point_count") else "0"

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

    private  val TAG = "MyCampaigns"
    private fun getCampaignList() {
        dialog!!.show()
        val map: HashMap<String, String> = HashMap()
        map["unique_code"] = SharedPrefManager.getInstance(context)!!.user.uniqueCode.toString()
        map["team_id"] = SharedPrefManager.getInstance(context)!!.user.teamId.toString()
        println(map)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val result = services.getCampaignList(map)

        Log.i(TAG, "getCampaignList: ${Gson().toJson(result.request())}")
        result.enqueue(object : Callback<JsonObject> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getCampaignList")
                println(response.body())
                println(response.code())
                if (response.code() == 200) {
                    val jsonObject = JSONObject(Gson().toJson(response.body()))
                    val responseCode: Int = jsonObject.getInt("response_code")
                    if (responseCode == 200) {
                        val jsonArray = JSONArray(
                            Gson().toJson(
                                response.body()?.getAsJsonArray("team_campaign_lists")
                            )
                        )
                        val turnsType = object : TypeToken<ArrayList<CampaignModel>>() {}.type
                        val list: ArrayList<CampaignModel> =
                            Gson().fromJson(jsonArray.toString(), turnsType)
                        println(list.size)
                        binding!!.count.text = "Running "+ list.size.toString()
                        if (list.size == 0) {
                            binding!!.notFound.visibility = View.VISIBLE
                        } else {
                            binding!!.notFound.visibility = View.GONE
                            binding!!.recyclerView.adapter = MyCampaignAdapter(list, context)
                        }


                    } else {

                        binding!!.notFound.visibility = View.VISIBLE
                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG)
                            .show()
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


}