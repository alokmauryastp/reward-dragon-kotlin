package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.Dashboard
import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.adapter.MyChallengeAdapter
import aara.technologies.rewarddragon.databinding.ActivityMyLatestChallengeBinding
import aara.technologies.rewarddragon.model.ChallengeModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant.loadAvatarImage
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.OnRefresh
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyLatestChallenge : AppCompatActivity(), OnRefresh {
    var binding: ActivityMyLatestChallengeBinding? = null
    var dialog: CustomLoader? = null
    lateinit var context: Context

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyLatestChallengeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        context = this
        binding!!.toolbar.toolbarTitle.text =
            SharedPrefManager.getInstance(context)!!.user.companyName
        com.bumptech.glide.Glide.with(context).load(
            SharedPrefManager.getInstance(context)!!
                .getString(SharedPrefManager.KEY_COMPANY_IMAGE)!!
        ).into(binding!!.toolbar.companyLogo)
        binding!!.toolbar.back.setOnClickListener { finish() }

        dialog = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)
        dialog!!.setCancelable(false)

        binding!!.recyclerView.layoutManager = LinearLayoutManager(context)

        binding!!.gameTime.setOnClickListener {
            startActivity(Intent(context, MyGameTime::class.java))
        }
        binding!!.myLeaderboard.setOnClickListener {
            finish()
            startActivity(Intent(context, Dashboard::class.java).putExtra("from", "MyLeaderboard"))
        }

        binding!!.userNameTxt.text =
            SharedPrefManager.getInstance(context)!!.user.firstName + " " + SharedPrefManager.getInstance(
                context
            )!!.user.lastName
        binding!!.designation.text =
            SharedPrefManager.getInstance(context)!!.user.designation + ", " + SharedPrefManager.getInstance(
                context
            )!!.user.teamName

        getChallengeList()
        getChallengePoint()
        getWinLevelPoints()

        loadAvatarImage(context, binding!!.imageView)

    }

    fun addFragment(fragment: Fragment?) {
        // Create the transaction
        val fts = supportFragmentManager.beginTransaction()
        // Replace the content of the container
        fts.replace(R.id.fragment_container_view, fragment!!)
        // Append this transaction to the backstack
        fts.addToBackStack("optional tag")
        // Commit the changes
        fts.commit()
    }

    private fun getWinLevelPoints() {
        val map: HashMap<String, String> = hashMapOf()
        map["employee_id"] = SharedPrefManager(context).user.id.toString()
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

    private val TAG = "MyLatestChallenge"
    private fun getChallengePoint() {
        dialog!!.show()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call =
            services.getChallengePoint(SharedPrefManager.getInstance(context)!!.user.id.toString())
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                Log.i(TAG, "getChallengePoint: ${response.body()}")

                if (response.code() == 200) {
                    try {

                        val obj = JSONObject(Gson().toJson(response.body()))
                        val resCode = obj.getInt("response_code")
                        if (resCode == 200) {
                            binding!!.played.text = obj.getInt("challenges_played_count").toString()
                            binding!!.won.text = obj.getInt("challenges_won_count").toString()
                            binding!!.bonusPoints.text =
                                if (!obj.isNull("challenge_bonus_point")) obj.getInt("challenge_bonus_point")
                                    .toString() else "0"
                        }
                    } catch (e: java.lang.Exception) {
                        Log.i(TAG, "onResponse: ${e.message}")
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

    private fun getChallengeList() {
        dialog!!.show()
        val map: HashMap<String, String> = HashMap()
        map["unique_code"] =
            SharedPrefManager.getInstance(context)!!.user.uniqueCode.toString()
        map["team_id"] = SharedPrefManager.getInstance(context)!!.user.teamId.toString()
        map["employee_id"] = SharedPrefManager.getInstance(context)!!.user.id.toString()
        println(map)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val result = services.getChallengeList(map)
        println(Gson().toJson(result.request()))
        result.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getChallengeList")
                println(response.code())
                println(response.body())

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
                        if (list.size == 0) {
                            binding!!.notFound.visibility = View.VISIBLE
                        } else {
                            binding!!.notFound.visibility = View.GONE
                            binding!!.recyclerView.adapter =
                                MyChallengeAdapter(list, context, this@MyLatestChallenge)
                        }
                    } else {
                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    binding!!.notFound.visibility = View.VISIBLE
                }
                dialog!!.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialog!!.dismiss()
                println(t.message)
            }
        })
    }

    override fun refresh() {
        getChallengeList()
        getChallengePoint()
        getWinLevelPoints()
    }

}