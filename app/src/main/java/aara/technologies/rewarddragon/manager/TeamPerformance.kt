package aara.technologies.rewarddragon.manager

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.activities.LineChartXAxisValueFormatter
import aara.technologies.rewarddragon.adapter.KpiPerformanceAdapter
import aara.technologies.rewarddragon.databinding.ActivityTeamPerformanceBinding
import aara.technologies.rewarddragon.databinding.TeamPerformanceFilterBinding
import aara.technologies.rewarddragon.model.*
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant.getStringFormatted
import aara.technologies.rewarddragon.utils.Constant.getTime
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TeamPerformance : AppCompatActivity() {
    var binding: ActivityTeamPerformanceBinding? = null
    val map: HashMap<String, Any> = hashMapOf()
    var dialog: CustomLoader? = null
    var dialog1: CustomLoader? = null

    lateinit var context: Context
    var selectedKpi = 0
    var selectedName = 0
    var selectedTime = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamPerformanceBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        context = this

        binding!!.toolbar.toolbarTitle.text =
            SharedPrefManager.getInstance(context)!!.user.companyName
        com.bumptech.glide.Glide.with(context).load(
            SharedPrefManager.getInstance(context)!!
                .getString(aara.technologies.rewarddragon.utils.SharedPrefManager.Companion.KEY_COMPANY_IMAGE)!!
        ).into(binding!!.toolbar.companyLogo)
        binding!!.toolbar.back.setOnClickListener { finish() }

        dialog = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)
        dialog1 = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)

        binding!!.userNameTxt.text =
            SharedPrefManager.getInstance(context)!!.user.firstName + " " + SharedPrefManager.getInstance(
                context
            )!!.user.lastName

        binding!!.designation.text =
            SharedPrefManager.getInstance(context)!!.user.designation + ", " + SharedPrefManager.getInstance(
                context
            )!!.user.teamName


        binding!!.time.text = "Data last refreshed on ${getTime()}"

        binding!!.recyclerView.layoutManager = LinearLayoutManager(context)

        binding!!.filter.setOnClickListener { getKpiList() }

        getData()
//        performanceTrendChart()

    }

    private fun getData() {
        map["select_name"] = "all"
        map["select_time_period"] = "all"
        map["select_kpi"] = "all"
        getTeamPerformanceList()
    }

    private fun openDialog(kpiList: ArrayList<KpiListModel>, userList: ArrayList<EmployeeModel>) {
        val binding = TeamPerformanceFilterBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context).create()

        binding.apply.setOnClickListener {
            dialog.dismiss()
            getTeamPerformanceList()
        }

        val adapter = ArrayAdapter(
            context,
            R.layout.simple_spinner_item1,
            arrayOf("ALL", "Today", "Yesterday", "WTD", "MTD", "YTD")
        ) //setting the country_array to spinner
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.timePeriod.adapter = adapter
        binding.timePeriod.setSelection(selectedTime)
        binding.timePeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedTime = p2
                map["select_time_period"] = getTime(p0?.selectedItem.toString())
                println(map)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        // name Adapter


        val userItems: ArrayList<String> = ArrayList()
        userList.add(0, EmployeeModel(0, 0, "", "All", ""))
        for (item in userList) {
            userItems.add(item.user__first_name + " " + item.user__last_name)
        }

        val userAdapter = ArrayAdapter(
            context,
            R.layout.simple_spinner_item1, userItems
        ) //setting the country_array to spinner
        userAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item2)
        binding.selectName.adapter = userAdapter
        binding.selectName.setSelection(selectedName)
        binding.selectName.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    p2: Int,
                    p3: Long
                ) {
                    selectedName = p2
                    if (p2 == 0) {
                        map["select_name"] = "all"
                    } else {
                        map["select_name"] = userList[p2].id.toString()
                    }
                    println(map)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        //kpi spinner

        val kpiItems: ArrayList<String> = ArrayList()
        kpiList.add(0, KpiListModel("", 0, 0, "All", 0, ""))
        for (item in kpiList) {
            kpiItems.add(item.name)
        }

        val adapterKpi = ArrayAdapter(
            context,
            R.layout.simple_spinner_item1, kpiItems
        ) //setting the country_array to spinner
        adapterKpi.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.selectKpi.adapter = adapterKpi
        binding.selectKpi.setSelection(selectedKpi)
        binding.selectKpi.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    p2: Int,
                    p3: Long
                ) {
                    selectedKpi = p2
                    if (p2 == 0) {
                        map["select_kpi"] = "all"
                    } else {
                        map["select_kpi"] = kpiList[p2].id.toString()
                    }
                    println(map)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

        binding.close.setOnClickListener { dialog.dismiss() }
        dialog.setView(binding.root)
        dialog.show()
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

    private val TAG = "TeamPerformance"
    private fun setUpLineChart(list: List<PerformanceTrendChart>) {

        // on below line we are creating array list for Entry data

        val lineEntriesList: ArrayList<Entry> = ArrayList()

        val labels = ArrayList<String>()

        for (i in list.indices) {
            val model = list[i]
            labels.add(getStringFormatted(model.date))
            lineEntriesList.add(Entry((i).toFloat(), model.kpi_percent.toFloat()))
        }

        println("labels.size")
        println(labels.size)
        println("lineEntriesList.size")
        println(lineEntriesList.size)

        val lineDataSet = LineDataSet(lineEntriesList, null)
        lineDataSet.color = ContextCompat.getColor(context, R.color.green)
        lineDataSet.fillColor = ContextCompat.getColor(context, R.color.green)
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillAlpha = 100

//        lineDataSet.valueFormatter = DefaultValueFormatter(0)
        lineDataSet.valueFormatter = LineChartXAxisValueFormatter()

        lineDataSet.setGradientColor(
            ContextCompat.getColor(context, R.color.green),
            ContextCompat.getColor(context, R.color.green)
        )

        val lineData = LineData(lineDataSet)

        binding!!.lineChart.legend.isEnabled = false

        binding!!.lineChart.data = lineData

        binding!!.lineChart.data


        binding!!.lineChart.description.isEnabled = false

        val xLine = binding!!.lineChart.axisLeft
        xLine.labelCount = 3
        xLine.axisMaximum = 5f
        xLine.axisMinimum = 1f
        xLine.spaceMin = .5f

        xLine.axisMinimum = 0f
        xLine.axisMaximum = 100f
        xLine.labelCount = 10

        xLine.valueFormatter = LineChartXAxisValueFormatter()

//                          binding!!.lineChart.extraRightOffset = 25f

        val xAxisLine: XAxis = binding!!.lineChart.xAxis
        xAxisLine.valueFormatter = IndexAxisValueFormatter(labels)
        xAxisLine.textColor = ContextCompat.getColor(context, R.color.white)

        xAxisLine.setAvoidFirstLastClipping(false)  //always set to false

//        xAxisLine.labelRotationAngle = -60f

//                            xAxisLine.isGranularityEnabled = true
//                            xAxisLine.granularity = 7f


//                            xLine.valueFormatter = LineChartXAxisValueFormatter()
//                            xLine.valueFormatter = IAxisValueFormatter { value, axis -> value.roundToInt() }


        // below line is to set position
        // to our x-axis to bottom.
        xAxisLine.position = XAxis.XAxisPosition.BOTTOM
        xAxisLine.setCenterAxisLabels(false)

//                          binding!!.lineChart.setViewPortOffsets(0f,0f,0f,100f)
        binding!!.lineChart.extraBottomOffset = 0f

        val yLine = binding!!.lineChart.axisRight
        yLine.isEnabled = false
        println("running")


        // below line is to set granularity
        // to our x axis labels.
        xAxisLine.granularity = 1.0f
        // below line is to enable
        // granularity to our x axis.
        xAxisLine.isGranularityEnabled = true


        // on below line we are setting colors for our bar chart text
        lineDataSet.valueTextColor = Color.WHITE
        lineDataSet.lineWidth = 2f
        lineDataSet.circleColors
        lineDataSet.setCircleColor(ContextCompat.getColor(context, R.color.green_light))
        lineDataSet.circleHoleColor = ContextCompat.getColor(context, R.color.green)

        // on below line we are setting color for our bar data set
        lineDataSet.color = ContextCompat.getColor(context, R.color.green)

        binding!!.lineChart.axisLeft.textColor = ContextCompat.getColor(context, R.color.white)

        // below line is to make our
        // bar chart as draggable.
        binding!!.lineChart.isDragEnabled = true

        // below line is to make visible
        // range for our bar chart.
        binding!!.lineChart.setVisibleXRangeMaximum(5f)

        binding!!.lineChart.invalidate()

    }

    private fun getTeamPerformanceList() {
        dialog?.show()
        map["manager_email"] =
            SharedPrefManager.getInstance(context)!!.user.email.toString()
        map["team_id"] = SharedPrefManager.getInstance(context)!!.user.teamId!!.toInt()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getTeamPerformanceList(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println(map)
                println("getTeamPerformanceList")
                Log.i(TAG, "onResponse: ${response.body()}")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {

                    val obj = JSONObject(Gson().toJson(response.body()))
                    binding!!.teamMember.text = obj.getString("team_member_count")
                    binding!!.kpiMet.text = obj.getString("manager_kpi_met_data")
                    binding!!.kpiWip.text = obj.getString("kpi_wip")

                    val jsonArray = obj.getJSONArray("kpi_data")
                    val turnsType = object : TypeToken<List<KpiPerformanceModel>>() {}.type
                    val list: ArrayList<KpiPerformanceModel> =
                        Gson().fromJson(jsonArray.toString(), turnsType)
                    binding!!.recyclerView.adapter = KpiPerformanceAdapter(list, context)

                    val jsonArray1 = JSONArray(
                        Gson().toJson(
                            response.body()?.getAsJsonArray("kpi_trend_chart_data")
                        )
                    )
                    val list1: List<PerformanceTrendChart> = Gson().fromJson(
                        jsonArray1.toString(),
                        Array<PerformanceTrendChart>::class.java
                    ).toList()

                    Log.i(TAG, "setUpLineChart: $list1")

                    if (list1.isNotEmpty()) {
                        setUpLineChart(list1)
                    } else {
                        binding!!.lineChart.clear()

                    }

                    if (list.size > 0) {
                        binding!!.notFound.visibility = View.GONE
                    } else {
                        binding!!.notFound.visibility = View.VISIBLE
                    }

                } else {
                    binding!!.notFound.visibility = View.VISIBLE
                    binding!!.lineChart.clear()

                }
                dialog?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialog?.dismiss()
            }
        })
    }

    private fun getKpiList() {
        dialog?.show()
        val map1: HashMap<String, String> = hashMapOf()
        map1["unique_code"] = SharedPrefManager.getInstance(context)!!.user.uniqueCode.toString()
        println(map1)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getKpiList(map1)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getKpi list")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val jsonArray =
                        JSONArray(Gson().toJson(response.body()?.getAsJsonArray("kpi_data")))
                    val turnsType = object : TypeToken<List<KpiListModel>>() {}.type
                    val list: ArrayList<KpiListModel> =
                        Gson().fromJson(jsonArray.toString(), turnsType)

                    teamEmployeeList(list)

                }
                dialog?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialog?.dismiss()
            }
        })

    }

    private fun teamEmployeeList(kpiList: ArrayList<KpiListModel>) {
        dialog1?.show()
        val map1: HashMap<String, String> = hashMapOf()
        map1["manager_id"] = SharedPrefManager.getInstance(context)!!.user.id.toString()
        println(map1)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.teamEmployeeList(map1)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("teamEmployeeList")
                println(response.code())
                println(response.body())
                val jsonArray =
                    JSONArray(Gson().toJson(response.body()?.getAsJsonArray("employees")))
                val turnsType = object : TypeToken<List<EmployeeModel>>() {}.type
                val list: ArrayList<EmployeeModel> =
                    Gson().fromJson(jsonArray.toString(), turnsType)

                openDialog(kpiList, list)

                dialog1?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialog1?.dismiss()
                println(t.message)
            }
        })


    }

    private fun performanceTrendChart() {
        dialog1?.show()
        val map1: HashMap<String, Any> = hashMapOf()
        map1["team_id"] = SharedPrefManager.getInstance(context)!!.user.teamId.toString()
        map1["unique_code"] = SharedPrefManager.getInstance(context)!!.user.uniqueCode.toString()
        println(map1)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.performanceTrendChart(map1)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("performanceTrendChart")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {

                    val jsonArray = JSONArray(
                        Gson().toJson(
                            response.body()?.getAsJsonArray("performance_trend_chart")
                        )
                    )
                    val list: List<PerformanceTrendChart> = Gson().fromJson(
                        jsonArray.toString(),
                        Array<PerformanceTrendChart>::class.java
                    ).toList()
//                    setUpLineChart(list)
                }
                dialog1?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialog1?.dismiss()
                println(t.message)
            }
        })
    }


}