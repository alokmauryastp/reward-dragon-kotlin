package aara.technologies.rewarddragon.manager

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.adapter.TeamLeaderBoardAdapter
import aara.technologies.rewarddragon.databinding.ActivityTeamLeadershipBinding
import aara.technologies.rewarddragon.databinding.TeamLeaderboardFilterBinding
import aara.technologies.rewarddragon.model.EmployeeModel
import aara.technologies.rewarddragon.model.TeamLeaderboardModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant.getGroup
import aara.technologies.rewarddragon.utils.Constant.getTime
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

class TeamLeaderboard : Fragment() {

    var binding: ActivityTeamLeadershipBinding? = null
    var dialog: CustomLoader? = null
    val map: HashMap<String, Any> = hashMapOf()
    var page = 1
    var limit = 1
    var userList: ArrayList<TeamLeaderboardModel> = arrayListOf()
    var selectedGroup = 0
    var selectedName = 0
    var selectedTime = 0

    // on below line we are creating
    // a variable for bar data
    lateinit var barData: BarData

    // on below line we are creating a
    // variable for bar data set
    lateinit var barDataSet1: BarDataSet

    // on below line we are creating array list for bar data
    lateinit var barEntriesList1: ArrayList<BarEntry>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityTeamLeadershipBinding.inflate(inflater)
        return binding!!.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = CustomLoader(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)

        binding!!.time.text = "Data last refreshed on ${getTime()}"

        binding!!.userNameTxt.text =
            SharedPrefManager.getInstance(requireContext())!!.user.firstName + " " + SharedPrefManager.getInstance(
                requireContext()
            )!!.user.lastName

        binding!!.designation.text =
            SharedPrefManager.getInstance(requireContext())!!.user.designation + ", " + SharedPrefManager.getInstance(
                requireContext()
            )!!.user.teamName


        binding!!.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)


        binding!!.filter.setOnClickListener {
            teamEmployeeList()
        }


        getTeam()


//        binding!!.nested.setOnScrollChangeListener(object :
//            NestedScrollView.OnScrollChangeListener {
//            override fun onScrollChange(
//                v: NestedScrollView?,
//                scrollX: Int,
//                scrollY: Int,
//                oldScrollX: Int,
//                oldScrollY: Int
//            ) {
//                // on scroll change we are checking when users scroll as bottom.
//                if (scrollY == v!!.getChildAt(0).measuredHeight - v.measuredHeight) {
//                    // in this method we are incrementing page number,
//                    // making progress bar visible and calling get data method.
//                    page++
//                    dialog!!.show()
//                    getTeamLeaderboardFilter()
//                }
//            }
//        })




    }

    private fun getTeam(){
        map["group_all"] = "group_all"
        map["select_name_all"] = "select_name_all"
        map["time_period_all"] = "time_period_all"
        getTeamLeaderboardFilter()
    }

    private fun openDialog(list: ArrayList<EmployeeModel>) {
        val binding = TeamLeaderboardFilterBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context).create()

        binding.apply.setOnClickListener{
            getTeamLeaderboardFilter()
            dialog.dismiss()
        }


        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item1,
            arrayOf("Time Period", "Today", "Yesterday", "WTD", "MTD", "YTD", "ALL")
        ) //setting the country_array to spinner
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.timePeriod.adapter = adapter
        binding.timePeriod.setSelection(selectedTime)
        binding.timePeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val time = p0?.selectedItem.toString()
                map["time_period_all"] = getTime(time)
                println(map)
                limit = 1
                page = 1
                userList.clear()
                selectedTime =p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        val adapter1 = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item1,
            arrayOf("Group", "Top 10", "Bottom 10", "ALL")
        ) //setting the country_array to spinner
        adapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.groupBtn.adapter = adapter1
        binding.groupBtn.setSelection(selectedGroup)
        binding.groupBtn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val group = p0?.selectedItem.toString()
                map["group_all"] = getGroup(group)
                println(map)
                limit = 1
                page = 1
                userList.clear()
                selectedGroup = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        val items: ArrayList<String> = ArrayList()
        list.add(0, EmployeeModel(0, 0, "", "All", ""))
        for (item in list) {
            items.add(item.user__first_name + " " + item.user__last_name)
        }

        val adapter3 = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item, items
        ) //setting the country_array to spinner
        adapter3.setDropDownViewResource(R.layout.simple_spinner_dropdown_item2)
        binding.spinner.adapter = adapter3
        binding.spinner.setSelection(selectedName)
        binding.spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    p2: Int,
                    p3: Long
                ) {
                    selectedName = p2
                    if (p2 == 0) {
                        map["select_name_all"] = "select_name_all"
//                                map["page_no"] = "1"
                        binding.groupBtn.visibility = View.VISIBLE
                    } else {
                        binding.groupBtn.visibility = View.GONE
                        map["select_name_all"] = list[p2].id.toString()
                    }
                    limit = 1
                    page = 1
                    userList.clear()
                    println(map)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

        binding.close.setOnClickListener { dialog.dismiss() }
        dialog.setView(binding.root)
        dialog.show()
    }

    private fun teamEmployeeList() {
        dialog?.show()
        val map1: HashMap<String, String> = hashMapOf()
        map1["manager_id"] = SharedPrefManager.getInstance(requireContext())!!.user.id.toString()
        println(map1)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.teamEmployeeList(map1)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
//                println("teamEmployeeList")
//                println(response.code())
//                println(response.body())
                val jsonArray =
                    JSONArray(Gson().toJson(response.body()?.getAsJsonArray("employees")))
                val turnsType = object : TypeToken<List<EmployeeModel>>() {}.type
                val list: ArrayList<EmployeeModel> =
                    Gson().fromJson(jsonArray.toString(), turnsType)
                openDialog(list)
                dialog?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialog?.dismiss()
                println(t.message)
            }
        })
    }

    private fun getTeamLeaderboardFilter() {
//        if (page > limit) {
//            // checking if the page number is greater than limit.
//            // displaying toast message in this case when page>limit.
//            Toast.makeText(requireContext(), "That's all the data..", Toast.LENGTH_SHORT).show()
//            // hiding our progress bar.
//            dialog!!.dismiss()
//            return
//        }
        dialog!!.show()
        map["manager_id"] = SharedPrefManager.getInstance(requireContext())!!.user.id.toString()
        map["team_id"] = SharedPrefManager.getInstance(requireContext())!!.user.teamId!!.toInt()
//        map["page_no"] = pageNo.toString()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getTeamLeaderboardFilter(map)
        println("pageNo")
//        println(pageNo)
        println(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getTeamLeaderboardFilter")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")

                    if (!obj.isNull("total_no_pages")) {
                        limit = obj.getInt("total_no_pages")
                    }

                    if (resCode == 200) {
                        val turnsType =
                            object : TypeToken<java.util.ArrayList<TeamLeaderboardModel>>() {}.type
                        userList.addAll(
                            Gson().fromJson(
                                obj.getJSONArray("team_points_data").toString(), turnsType
                            )
                        )
                        println(userList.size)
                        if (userList.size == 0) {
                            binding!!.notFound.visibility = View.VISIBLE
                            binding!!.pieChart.visibility = View.GONE
                            binding!!.barChart.visibility = View.GONE
                        } else {


                            binding!!.barChart.fitScreen()
                            binding!!.barChart.data?.clearValues()
                            binding!!.barChart.data?.setDrawValues(true)
                            binding!!.barChart.xAxis.valueFormatter = null
                            binding!!.barChart.notifyDataSetChanged()
                            binding!!.barChart.clear()
                            binding!!.barChart.invalidate()


                            binding!!.notFound.visibility = View.GONE
                            binding!!.pieChart.visibility = View.VISIBLE
                            binding!!.barChart.visibility = View.VISIBLE
                            binding!!.recyclerView.adapter =
                                TeamLeaderBoardAdapter(userList, requireContext())

                            // on below line we are disabling our legend for pie chart
                            binding!!.pieChart.legend.isEnabled = false
                            binding!!.pieChart.setEntryLabelColor(Color.WHITE)
                            binding!!.pieChart.setEntryLabelTextSize(7f)

                            // on below line we are creating array list and
                            // adding data to it to display in pie chart
                            val entries: ArrayList<PieEntry> = ArrayList()

                            for (model in userList) {
                                entries.add(
                                    PieEntry(
                                        model.earned_point.toFloat(), model.first_name
                                    )
                                )
                            }

                            // on below line we are setting pie data set
                            val dataSet = PieDataSet(entries, "Mobile OS")

                            // on below line we are setting slice for pie
                            dataSet.sliceSpace = 1f
                            dataSet.iconsOffset = MPPointF(0f, 40f)
                            dataSet.selectionShift = 5f

                            // add a lot of colors to list
                            val colors: ArrayList<Int> = ArrayList()
                            colors.add(ContextCompat.getColor(requireContext(),R.color.green))
                            colors.add(ContextCompat.getColor(requireContext(),R.color.green))
                            colors.add(ContextCompat.getColor(requireContext(),R.color.green))

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

                            for (i in 0 until userList.size) {
                                val model = userList[i]
                                labels.add(model.first_name)
                                barEntriesList1.add(
                                    BarEntry(
                                        i.toFloat(),
                                        model.earned_point.toFloat()
                                    )
                                )
                            }

                            // on below line we are initializing our bar data set
                            barDataSet1 = BarDataSet(barEntriesList1, "Team Leaderboard")

                            // on below line we are initializing our bar data
                            barData = BarData(barDataSet1)
                            barData.barWidth = .4f

                            // on below line we are setting data to our bar chart
                            binding!!.barChart.data = barData
                            binding!!.barChart.axisLeft.textColor =
                                ContextCompat.getColor(requireContext(),R.color.white)
                            binding!!.barChart.axisRight.textColor =
                                ContextCompat.getColor(requireContext(),R.color.white)
                            binding!!.barChart.legend.textColor = ContextCompat.getColor(requireContext(),R.color.white)

                            // on below line we are setting colors for our bar chart text
                            barDataSet1.valueTextColor = Color.WHITE

                            // on below line we are setting color for our bar data set
                            barDataSet1.color = ContextCompat.getColor(requireContext(),R.color.green)

                            // on below line we are setting text size
                            barDataSet1.valueTextSize = 10f

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
                            xAxis.textColor = ContextCompat.getColor(requireContext(),R.color.white)


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
                            binding!!.barChart.setVisibleXRangeMaximum(5f)

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
                } else {
                    binding!!.notFound.visibility = View.VISIBLE
                }
                dialog!!.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialog!!.dismiss()

            }
        })
    }
}