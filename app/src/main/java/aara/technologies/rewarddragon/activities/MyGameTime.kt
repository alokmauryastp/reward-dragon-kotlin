package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.adapter.GameAdapter
import aara.technologies.rewarddragon.adapter.GameCategoryAdapter
import aara.technologies.rewarddragon.databinding.ActivityMyGameTimeBinding
import aara.technologies.rewarddragon.model.GameCategoryModel
import aara.technologies.rewarddragon.model.GameModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.*
import aara.technologies.rewarddragon.utils.Constant.gameClickable
import aara.technologies.rewarddragon.utils.Constant.loadAvatarImage
import aara.technologies.rewarddragon.utils.Constant.onRefresh
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MyGameTime : AppCompatActivity(), OnRefresh, OnGameRefresh {

    var binding: ActivityMyGameTimeBinding? = null
    var customLoader: CustomLoader? = null
    var points = 0
    private var company = ""
    lateinit var context: Context

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyGameTimeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        context = this
        customLoader = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)

        binding!!.toolbar.toolbarTitle.text =
            SharedPrefManager.getInstance(context)!!.user.companyName
        com.bumptech.glide.Glide.with(context).load(
            SharedPrefManager.getInstance(context)!!
                .getString(aara.technologies.rewarddragon.utils.SharedPrefManager.Companion.KEY_COMPANY_IMAGE)!!
        ).into(binding!!.toolbar.companyLogo)
        binding!!.toolbar.back.setOnClickListener { finish() }


        onRefresh = this

        binding!!.userNameTxt.text =
            SharedPrefManager.getInstance(context)!!.user.firstName + " " + SharedPrefManager.getInstance(
                context
            )!!.user.lastName

        binding!!.designation.text =
            SharedPrefManager.getInstance(context)!!.user.designation

        company = SharedPrefManager.getInstance(context)!!.user.companyName.toString()

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

        binding!!.gameCategoryRecycler.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding!!.recyclerView.layoutManager =
            GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
        getWinLevelPoints()
        getGamePoint()
        gameCategoryList()
        println("on create view my game time")
        nextAvailabilityTime()
        loadAvatarImage(context, binding!!.imageView)

    }

    private val TAG = "MyGameTime"


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 454 && resultCode == Activity.RESULT_OK) {
            var point = data?.getStringExtra("reward_points")
            var msg = data?.getStringExtra("reward_message")
            if (point != null && msg != null) {
                Log.i(TAG, "onActivityResult: point $point msg $msg")

                if (point.toInt() > 0) {
                    val alert = Constant.AlertDialog2(
                        this,
                        R.style.ThemeDialogCustom,
                        point,
                        msg,
                        object : OnBonusDialogDismissInterface {
                            override fun onDismiss(b: Boolean) {}
                        })
                    alert.show()

                }
            } else {
                Log.i(TAG, "onActivityResult: null data")
            }
        } else {
            Log.i(TAG, "onActivityResult: $data")
        }
    }

    override fun onResume() {
        super.onResume()
        println("on resume my game time")
        nextAvailabilityTime()
    }

    private fun nextAvailabilityTime() {
        val map: HashMap<String, String> = hashMapOf()
        map["user_profile_id"] = SharedPrefManager(context).user.id.toString()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.nextAvailabilityTime(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("nextAvailabilityTime")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    if (obj.isNull("Data")) {
                      //  binding!!.gameCounter.visibility = View.GONE
                        gameClickable = true

                    } else {

                        binding!!.gameCounter.visibility = View.VISIBLE

                        gameClickable = false

                        val time = obj.getJSONObject("Data").getString("next_availability_time")

                        printDifferenceDateForHours(time)

                    }

                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
            }
        })
    }

    private fun printDifferenceDateForHours(time: String) {
        // 2022-08-06T19:04:36

        val currentTime = Calendar.getInstance().time
//        val endDateDay = "06/08/2022 21:00:00"
//        val endDateDay = "2022-08-06T19:04:36"
        val format1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val endDate = format1.parse(time)

        //milliseconds
        val different = endDate.time - currentTime.time
        object : CountDownTimer(different, 1000) {

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

                binding!!.timer.text = "$hour : $minute : $seconds"

            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                binding!!.timer.text = "00 : 00 : 00"
                gameClickable = true
            }
        }.start()
    }

    private fun getWinLevelPoints() {
        val map: HashMap<String, String> = hashMapOf()
        map["employee_id"] = SharedPrefManager(context).user.id.toString()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
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
                            points = jsonObject.getInt("points_won")
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

    private fun getGamePoint() {
        val map: HashMap<String, Any> = hashMapOf()
        map["employee_id"] = SharedPrefManager(context).user.id.toString()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getGamePoint(map)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                println("getGamePoint")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {

                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {

                            binding!!.gamesPlayed.text = jsonObject.getString("total_played_games")
                            binding!!.gamePoint.text = jsonObject.getString("total_bonus")

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Log.e("getGamePoint t", t.message!!)
            }
        })
    }

    private fun gameCategoryList() {
        customLoader?.show()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call =
            services.gameCategoryList()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("gameCategoryList")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        val turnsType = object : TypeToken<ArrayList<GameCategoryModel>>() {}.type
                        val list: ArrayList<GameCategoryModel> = Gson().fromJson(
                            obj.getJSONArray("game_categories").toString(),
                            turnsType
                        )
                        println(list.size)
                        binding!!.gameCategoryRecycler.adapter =
                            GameCategoryAdapter(list, context, this@MyGameTime)
                    } else {
//                        binding!!.notFound.visibility = View.VISIBLE
                        Toast.makeText(
                            context,
                            obj.getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                customLoader?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
                customLoader?.dismiss()
            }
        })
    }

    private fun getGameList(gameCategoryId: Int) {
        customLoader?.show()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call =
            services.getGameList(
                SharedPrefManager.getInstance(context)!!.user.uniqueCode.toString(),
                gameCategoryId
            )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getGameList")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {

                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        val turnsType = object : TypeToken<ArrayList<GameModel>>() {}.type
                        val list: ArrayList<GameModel> = Gson().fromJson(
                            obj.getJSONArray("game_name_data").toString(),
                            turnsType
                        )
                        println(list.size)
                        binding!!.recyclerView.adapter =
                            GameAdapter(list, this@MyGameTime, this@MyGameTime)
                        binding!!.notFound.visibility = View.GONE
                    } else {
                        binding!!.recyclerView.adapter = null
                        binding!!.notFound.visibility = View.VISIBLE
//                        Toast.makeText(
//                            context,
//                            obj.getString("message"),
//                            Toast.LENGTH_LONG
//                        ).show()
                    }
                }
                customLoader?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
                customLoader?.dismiss()
            }
        })
    }

    override fun refresh() {
        nextAvailabilityTime()
    }

    override fun gameRefresh(gameCategoryId: Int) {
        getGameList(gameCategoryId)
    }


}