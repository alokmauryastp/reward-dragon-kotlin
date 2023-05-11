package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.adapter.SpeedometerAdapter
import aara.technologies.rewarddragon.databinding.ActivityMyPerformanceBinding
import aara.technologies.rewarddragon.model.KpiPerformanceData
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant
import aara.technologies.rewarddragon.utils.Constant.getTime
import aara.technologies.rewarddragon.utils.Constant.loadAvatarImage
import aara.technologies.rewarddragon.utils.SharedPrefManager
import aara.technologies.rewarddragon.utils.getDateInterface
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MyPerformance : AppCompatActivity() {

    var binding: ActivityMyPerformanceBinding? = null
    lateinit var context: Context

    private val TAG = "MyPerformance"
    var dialog2: Constant.AlertDialog3? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPerformanceBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        context = this

        binding!!.toolbar.toolbarTitle.text =
            SharedPrefManager.getInstance(context)!!.user.companyName
        //  Log.i(TAG, "onCreate: image "+SharedPrefManager.getInstance(context)!!.getString(aara.technologies.rewarddragon.utils.SharedPrefManager.Companion.KEY_COMPANY_IMAGE))
        com.bumptech.glide.Glide.with(context).load(
            SharedPrefManager.getInstance(context)!!
                .getString(aara.technologies.rewarddragon.utils.SharedPrefManager.Companion.KEY_COMPANY_IMAGE)!!
        ).into(binding!!.toolbar.companyLogo)
        binding!!.toolbar.back.setOnClickListener { finish() }

        binding!!.userNameTxt.text =
            SharedPrefManager.getInstance(context)!!.user.firstName + " " + SharedPrefManager.getInstance(
                context
            )!!.user.lastName
        binding!!.designation.text = SharedPrefManager.getInstance(context)!!.user.designation
        binding!!.mobile.text = "+91 " + SharedPrefManager.getInstance(context)!!.user.mobileNo


        binding!!.myCampaigns.setOnClickListener {
            startActivity(Intent(applicationContext, MyCampaigns::class.java))
        }

        binding!!.time.text = "Data last refreshed on ${getTime()}"
        binding!!.time1.text = "Targets reset date : ${getTime()}"

        dialog2 = Constant.AlertDialog3(this@MyPerformance, 0, listener = object :
            getDateInterface {

            override fun getData(date: String, buttonClick: String) {
                Log.i(TAG, "getData: date $date  buttonclick $buttonClick")
                if (buttonClick == "DATE") {
                    //  getKpiPerformanceData(date, "")
                } else if (buttonClick == "CLEAR") {
                    //  getKpiPerformanceData("", "all")
                }

            }
        })

        binding?.filter?.setOnClickListener {
            // dialog2?.show()

            /* val now = Calendar.getInstance()
             val tpd = TimePickerDialog.newInstance(
                 { view: RadialPickerLayout?, hourOfDay: Int, minute: Int, hourOfDayEnd: Int, minuteEnd: Int ->


                     Log.i(TAG, "datePicker: from $hourOfDay:$minute to: $hourOfDayEnd:$minuteEnd")
                 },
                 now[Calendar.HOUR_OF_DAY],
                 now[Calendar.MINUTE],
                 false
             )*/


            val now = Calendar.getInstance()
            val dpd: DatePickerDialog = DatePickerDialog.newInstance(
                { view, year, monthOfYear, dayOfMonth, yearEnd, monthOfYearEnd, dayOfMonthEnd ->

                    Log.i(
                        TAG,
                        "datePicker: from $dayOfMonth:${monthOfYear + 1}:$year to: ${dayOfMonthEnd + 1}:$monthOfYearEnd:$yearEnd "
                    )
                    view.isAutoHighlight = true

                    var fromDate = "$year-${monthOfYear + 1}-$dayOfMonth"
                    var toDate = "$yearEnd-${monthOfYearEnd + 1}-$dayOfMonthEnd"

                    getKpiPerformanceData(fromDate, toDate, "")


                },
                now[Calendar.YEAR],
                now[Calendar.MONTH],
                now[Calendar.DAY_OF_MONTH]
            )
            dpd.show(fragmentManager, "Datepickerdialog")
            dpd.accentColor = R.color.black_app
            dpd.isThemeDark = true


            //  tpd.show((this).fragmentManager, "Timepickerdialog")
        }


        binding!!.latestChallenges.setOnClickListener {
            startActivity(Intent(applicationContext, MyLatestChallenge::class.java))
        }

        binding!!.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        getWinLevelPoints()
        getKpiPerformanceData("", "", "all")
        getCustomerKpiData()
        loadAvatarImage(context, binding!!.imageView)

    }

    private fun getWinLevelPoints() {
        val map: HashMap<String, String> = hashMapOf()
        map["employee_id"] = SharedPrefManager(context).user.id.toString()
        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.getWinLevelPoints(map)
        call.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("SetTextI18n")
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

    private fun getCustomerKpiData() {
        val map: HashMap<String, String> = hashMapOf()
        map["employee_id"] = SharedPrefManager(context).user.id.toString()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getCustomerKpiData(map)
        call.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.code() == 200) {
                    assert(response.body() != null)
                    Log.e("getCustomerKpiData", response.body().toString())
                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {
                            binding!!.kpiMet.text = (jsonObject.getString("total_kpi_met"))
                            binding!!.kpiWip.text = (jsonObject.getString("total_kpi_wip"))
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

    private fun getKpiPerformanceData(fromdate: String, toDate: String, all: String) {
        val map: HashMap<String, Any> = hashMapOf()
        map["employee_id"] = SharedPrefManager(context).user.id.toString()
        map["unique_code"] = SharedPrefManager(context).user.uniqueCode.toString()
        map["all"] = all
        map["from_date"] = fromdate
        map["to_date"] = toDate
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getKpiPerformanceData(map)
        Log.i(TAG, "getKpiPerformanceData:req ${call.request()}")
        call.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.i(
                    TAG,
                    "getKpiPerformanceData: " + Gson().toJson(response.body()) + "   code " + response.code()
                )
                if (response.code() == 200) {
                    assert(response.body() != null)
                    Log.e("getKpiPerformanceData", map.toString())
                    Log.e("getKpiPerformanceData", response.body().toString())
                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {
                            val overAll = jsonObject.getDouble("total_kpi_percent_data")
                            println(overAll)
                            println(overAll.toInt())
                            binding!!.circular.setProgress(overAll.toFloat())
                            binding!!.circular.animate()
                            binding!!.percent.text = overAll.toInt().toString() + "%"
                            if (overAll.toInt() > 50) {
                                binding!!.circular.progressColor =
                                    ContextCompat.getColor(context, R.color.progress_color)
                                binding!!.circular.progressBackgroundColor =
                                    ContextCompat.getColor(context, R.color.progress_color)
                            }
                            val turnsType = object : TypeToken<ArrayList<KpiPerformanceData>>() {}.type

                            val list: ArrayList<KpiPerformanceData> = Gson().fromJson(
                                jsonObject.getJSONArray("kpi_percent_data").toString(), turnsType
                            )
                            if (list.isEmpty()) {
                                binding!!.recyclerView.visibility = View.GONE
                                binding!!.recyclerView.adapter = null
                                Toast.makeText(context, "no data found", Toast.LENGTH_SHORT).show()
                            } else {
                                binding!!.recyclerView.visibility = View.VISIBLE
                                binding!!.recyclerView.adapter = SpeedometerAdapter(list, context)
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
}