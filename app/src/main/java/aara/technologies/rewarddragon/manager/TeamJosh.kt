package aara.technologies.rewarddragon.manager

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.activities.LineChartXAxisValueFormatter
import aara.technologies.rewarddragon.activities.MyJoshForToday
import aara.technologies.rewarddragon.databinding.ActivityTeamJoshBinding
import aara.technologies.rewarddragon.model.JoshModel
import aara.technologies.rewarddragon.model.MoodScoreModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant.getMoodText
import aara.technologies.rewarddragon.utils.Constant.loadAvatarImage
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class TeamJosh : AppCompatActivity() {

    var binding: ActivityTeamJoshBinding? = null
    val map1: HashMap<String, Any> = hashMapOf()

    // on below line we are creating
    // variables for our bar chart
//    lateinit var barChart: BarChart

    // on below line we are creating
    // a variable for bar data
    lateinit var barData: BarData

    // on below line we are creating a
    // variable for bar data set
    lateinit var barDataSet1: BarDataSet
    lateinit var barDataSet2: BarDataSet
    lateinit var barDataSet0: BarDataSet

    // on below line we are creating array list for bar data
    lateinit var barEntriesList2: ArrayList<BarEntry>
    lateinit var barEntriesList3: ArrayList<BarEntry>
    lateinit var barEntriesList1: ArrayList<BarEntry>

    lateinit var dialog: CustomLoader
    lateinit var context: Context

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamJoshBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        context = this

        binding!!.toolbar.toolbarTitle.text = SharedPrefManager.getInstance(context)!!.user.companyName
                com.bumptech.glide.Glide.with(context).load(SharedPrefManager.getInstance(context)!!.getString(SharedPrefManager.KEY_COMPANY_IMAGE)!!).into(binding!!.toolbar.companyLogo)
        binding!!.toolbar.back.setOnClickListener { finish() }

        dialog = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)

        binding!!.userNameTxt.text = SharedPrefManager.getInstance(context)!!.user.firstName + " " + SharedPrefManager.getInstance( context )!!.user.lastName

        binding!!.designation.text = SharedPrefManager.getInstance(context)!!.user.designation + ", " + SharedPrefManager.getInstance( context )!!.user.teamName

        val adapter = ArrayAdapter( context, R.layout.simple_spinner_item1, arrayOf("Time Period", "Today", "Yesterday", "WTD", "MTD", "YTD", "ALL") ) //setting the country_array to spinner

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding!!.timePeriod.adapter = adapter
        binding!!.timePeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                map1["time_period"] = getTime(p0?.selectedItem.toString())
                println(map1)
//                getTeamMoodTodayFilter()

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding!!.moodToday.setOnClickListener {
            startActivity(Intent(context, MyJoshForToday::class.java))
        }

        getTeamMoodToday()

        loadAvatarImage(context, binding!!.imageView)

        joshTodayForManager

        reasonPieChartData()

    }

    private val joshTodayForManager : Unit
        get() {
            val services = RetrofitInstance().getInstance().create(
                DataServices::class.java
            )
            val call = services.getJoshTodayForManager( Objects.requireNonNull( SharedPrefManager(context).user.id!! ), Objects.requireNonNull( SharedPrefManager(context).user.teamId!!.toInt() ) )
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

    private fun getTeamMoodToday() {
        val map: HashMap<String, Any> = hashMapOf()
        map["manager_id"] = SharedPrefManager.getInstance(context)!!.user.id!!.toInt()
        map["team_id"] = SharedPrefManager.getInstance(context)!!.user.teamId!!.toInt()
        println(map)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getTeamMoodToday(map)
        call.enqueue(object : Callback<JsonObject> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getTeamMoodToday")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        val avgScore = obj.getInt("avg_score_today")
                        val respondToday = obj.getDouble("percent_respondent_today")
                        println("avgScore")
                        println(avgScore)

                        binding!!.avgScore.text = avgScore.toString()
                        binding!!.respondToday.text = "$respondToday %"

                        setupBarChart(obj)

                        val todayScore = obj.getJSONObject("today_scores")

                        binding!!.todayOne.text = todayScore.getInt("one").toString() + "%"
                        binding!!.todayTwo.text = todayScore.getInt("two").toString() + "%"
                        binding!!.todayThree.text = todayScore.getInt("three").toString() + "%"
                        binding!!.todayFour.text = todayScore.getInt("four").toString() + "%"
                        binding!!.todayFive.text = todayScore.getInt("five").toString() + "%"

                        val yesterdayScore = obj.getJSONObject("yesterday_scores")


                        binding!!.yesterdayOne.text = yesterdayScore.getInt("one").toString() + "%"
                        binding!!.yesterdayTwo.text = yesterdayScore.getInt("two").toString() + "%"
                        binding!!.yesterdayThree.text = yesterdayScore.getInt("three").toString() + "%"
                        binding!!.yesterdayFour.text = yesterdayScore.getInt("four").toString() + "%"
                        binding!!.yesterdayFive.text = yesterdayScore.getInt("five").toString() + "%"


                        val yesterdayKpi = obj.getJSONObject("yesterday_kpi_met_percent")


                        binding!!.kpiOne.text = yesterdayKpi.getInt("one").toString() + "%"
                        binding!!.kpiTwo.text = yesterdayKpi.getInt("two").toString() + "%"
                        binding!!.kpiThree.text = yesterdayKpi.getInt("three").toString() + "%"
                        binding!!.kpiFour.text = yesterdayKpi.getInt("four").toString() + "%"
                        binding!!.kpiFive.text = yesterdayKpi.getInt("five").toString() + "%"


                    } else {
                        Toast.makeText(
                            context,
                            obj.getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
            }
        })
    }

    private fun setupPieChart(list: ArrayList<MoodScoreModel>){


        // on below line we are disabling our legend for pie chart
        binding!!.pieChart.legend.isEnabled = true
        binding!!.pieChart.description.isEnabled = false
        binding!!.pieChart.setEntryLabelColor(Color.WHITE)
        binding!!.pieChart.setDrawEntryLabels(false)
        binding!!.pieChart.setEntryLabelTextSize(12f)


        // on below line we are creating array list and
        // adding data to it to display in pie chart
        val entries: ArrayList<PieEntry> = ArrayList()
        for(mood in list ){
            entries.add(PieEntry(mood.category_percent.toFloat(),mood.category_name))
        }

        // on below line we are setting pie data set
        val dataSet = PieDataSet(entries, "")

        // on below line we are setting slice for pie
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        dataSet.valueFormatter = LineChartXAxisValueFormatter()

        // add a lot of colors to list
        val colors: ArrayList<Int> = ArrayList()
        colors.add(ContextCompat.getColor(context, R.color.red))
        colors.add(ContextCompat.getColor(context, R.color.amber))
        colors.add(ContextCompat.getColor(context, R.color.yellow_app))
        colors.add(ContextCompat.getColor(context, R.color.blue_chart))
        colors.add(ContextCompat.getColor(context, R.color.green))


//        val le1 = LegendEntry("Mood Scores Today", Legend.LegendForm.SQUARE, 10f, 2f, null, ContextCompat.getColor(context, R.color.blue_chart))
//        val le2 = LegendEntry("Mood Scores Yesterday", Legend.LegendForm.SQUARE, 10f, 2f, null, ContextCompat.getColor(context, android.R.color.holo_orange_dark))
//        val le3 = LegendEntry("KPI Met % Yesterday", Legend.LegendForm.SQUARE, 10f, 2f, null, ContextCompat.getColor(context, R.color.green))

        val le = binding!!.pieChart.legend
        le.textColor = ContextCompat.getColor(context, R.color.white)
        le.orientation = Legend.LegendOrientation.VERTICAL

        binding!!.pieChart.extraBottomOffset = 20f

//        le.setCustom(arrayOf(le1, le2,le3))

        // on below line we are setting colors.

        dataSet.colors = colors

        val data = PieData(dataSet)
        binding!!.pieChart.data = data
        // on below line we are setting animation for our pie chart
        binding!!.pieChart.animateY(1400, Easing.EaseInOutQuad)
        binding!!.pieChart.invalidate()

    }

    private fun setupBarChart(jsonObject: JSONObject){

        // Bar Chart

        barEntriesList1 = ArrayList()
        barEntriesList2 = ArrayList()
        barEntriesList3 = ArrayList()


        val todayScore = jsonObject.getJSONObject("today_scores")
        val teamJosh = jsonObject.getJSONObject("yesterday_scores")
        val teamKPI = jsonObject.getJSONObject("yesterday_kpi_met_percent")


        // on below line we are adding data
        // to our bar entries list
        barEntriesList1.add(BarEntry(0f, todayScore.getInt("one").toFloat()))
        barEntriesList1.add(BarEntry(2f, todayScore.getInt("two").toFloat()))
        barEntriesList1.add(BarEntry(3f, todayScore.getInt("three").toFloat()))
        barEntriesList1.add(BarEntry(4f, todayScore.getInt("four").toFloat()))
        barEntriesList1.add(BarEntry(5f, todayScore.getInt("five").toFloat()))

        // on below line we are adding data
        // to our bar entries list
        barEntriesList2.add(BarEntry(0f, teamJosh.getDouble("one").toFloat()))
        barEntriesList2.add(BarEntry(2f, teamJosh.getDouble("two").toFloat()))
        barEntriesList2.add(BarEntry(3f, teamJosh.getDouble("three").toFloat()))
        barEntriesList2.add(BarEntry(4f, teamJosh.getDouble("four").toFloat()))
        barEntriesList2.add(BarEntry(5f, teamJosh.getDouble("five").toFloat()))

        // on below line we are adding data
        // to our bar entries list
        barEntriesList3.add(BarEntry(0f, teamKPI.getDouble("one").toFloat()))
        barEntriesList3.add(BarEntry(2f, teamKPI.getDouble("two").toFloat()))
        barEntriesList3.add(BarEntry(3f, teamKPI.getDouble("three").toFloat()))
        barEntriesList3.add(BarEntry(4f, teamKPI.getDouble("four").toFloat()))
        barEntriesList3.add(BarEntry(5f, teamKPI.getDouble("five").toFloat()))


        // on below line we are initializing our bar data set
        barDataSet0 = BarDataSet(barEntriesList1, "Mood Scores Today")
        barDataSet1 = BarDataSet(barEntriesList2, "Mood Scores Yesterday")
        barDataSet2 = BarDataSet(barEntriesList3, "KPI Met % Yesterday")


        // on below line we are initializing our bar data
        barData = BarData(barDataSet0,barDataSet1, barDataSet2)
        barData.barWidth = .29f

        // on below line we are setting data to our bar chart
        binding!!.barChart.data = barData
        binding!!.barChart.axisLeft.textColor = ContextCompat.getColor(context, R.color.white)
        binding!!.barChart.axisRight.textColor = ContextCompat.getColor(context, R.color.white)
        binding!!.barChart.legend.textColor = ContextCompat.getColor(context, R.color.white)

        val l1 = LegendEntry("Mood Scores Today", Legend.LegendForm.SQUARE, 10f, 2f, null, ContextCompat.getColor(context, R.color.blue_chart))
        val l2 = LegendEntry("Mood Scores Yesterday", Legend.LegendForm.SQUARE, 10f, 2f, null, ContextCompat.getColor(context, android.R.color.holo_orange_dark))
        val l3 = LegendEntry("KPI Met % Yesterday", Legend.LegendForm.SQUARE, 10f, 2f, null, ContextCompat.getColor(context, R.color.green))

        val l = binding!!.barChart.legend

        l.setCustom(arrayOf(l1, l2,l3))


        // on below line we are setting colors for our bar chart text
        barDataSet1.valueTextColor = Color.WHITE
        barDataSet2.valueTextColor = Color.WHITE
        barDataSet0.valueTextColor = Color.WHITE

        barDataSet1.valueFormatter = LineChartXAxisValueFormatter()
        barDataSet2.valueFormatter = LineChartXAxisValueFormatter()
        barDataSet0.valueFormatter = LineChartXAxisValueFormatter()

        // on below line we are setting color for our bar data set
        barDataSet1.color = ContextCompat.getColor(context, android.R.color.transparent)
        barDataSet1.barBorderColor = ContextCompat.getColor(context, android.R.color.holo_orange_dark)
        barDataSet1.barBorderWidth = 1f

        barDataSet2.color = ContextCompat.getColor(context, R.color.green)
        barDataSet2.barBorderWidth = 1f
        barDataSet2.barBorderColor = Color.WHITE

        barDataSet0.color = ContextCompat.getColor(context, android.R.color.transparent)
        barDataSet0.barBorderWidth = 1f
        barDataSet0.barBorderColor = ContextCompat.getColor(context, R.color.blue_chart)

        // on below line we are setting text size
        barDataSet1.valueTextSize = 10f
        barDataSet2.valueTextSize = 10f
        barDataSet0.valueTextSize = 10f

        // below line is to set minimum
        // axis to our chart.
//                    binding!!.barChart.xAxis.axisMinimum = 0f

        val yAxis = binding!!.barChart.axisLeft

        yAxis.axisMaximum = 100f
        yAxis.axisMinimum = 0f

        yAxis.valueFormatter = LineChartXAxisValueFormatter()

        // on below line setting x axis
        val xAxis = binding!!.barChart.xAxis

        xAxis.setAvoidFirstLastClipping(false)  //always set to false

        // below line is to set value formatter to our x-axis and
        // we are adding our days to our x axis.
        xAxis.valueFormatter = IndexAxisValueFormatter(
            arrayOf(
                "1",
                "2",
                "3",
                "4",
                "5"
            )
        )

        xAxis.textColor = ContextCompat.getColor(context, R.color.white)

        val yLine = binding!!.barChart.axisRight
        yLine.isEnabled = false

        // below line is to set center axis
        // labels to our bar chart.
        xAxis.setCenterAxisLabels(true)
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
        binding!!.barChart.groupBars(0f, 0.1f, 0.02f)
        binding!!.barChart.animateY(1400, Easing.EaseInOutQuad)

        binding!!.barChart.invalidate()

    }

    private fun getTeamMoodTodayFilter() {
        dialog.show()
        map1["team_id"] = SharedPrefManager.getInstance(context)!!.user.teamId!!.toInt()
        println(map1)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getTeamMoodTodayFilter(map1)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getTeamMoodTodayFilter")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {

                    val jsonObject = JSONObject(Gson().toJson(response.body()))



                }
                dialog.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialog.dismiss()
                println(t.message)
            }
        })

    }

    private fun reasonPieChartData() {
        dialog.show()
        val map: HashMap<String, Any> = hashMapOf()
        map["team_id"] = SharedPrefManager.getInstance(context)!!.user.teamId!!.toInt()
        map["unique_code"] = SharedPrefManager.getInstance(context)!!.user.uniqueCode.toString()
        println(map)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.reasonPieChartData(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("reasonPieChartData")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val jsonObject = JSONObject(Gson().toJson(response.body()))
                    val moodScorePercent = jsonObject.getJSONArray("mood_score_percent")
                    val turnsType = object : TypeToken<ArrayList<MoodScoreModel>>() {}.type
                    val list: ArrayList<MoodScoreModel> = Gson().fromJson( moodScorePercent.toString(), turnsType )
                    println(list.size)
                    setupPieChart(list)
                }
                dialog.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialog.dismiss()
                println(t.message)
            }
        })
    }

}