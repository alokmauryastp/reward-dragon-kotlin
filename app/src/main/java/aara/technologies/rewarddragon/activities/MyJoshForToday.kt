package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.databinding.ActivityMyJoshForTodayBinding
import aara.technologies.rewarddragon.model.JoshModel
import aara.technologies.rewarddragon.model.JoshReasonModel
import aara.technologies.rewarddragon.model.MoodalyticsModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant
import aara.technologies.rewarddragon.utils.Constant.getStringFormatted
import aara.technologies.rewarddragon.utils.Constant.loadAvatarImage
import aara.technologies.rewarddragon.utils.Constant.width
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.OnBonusDialogDismissInterface
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MyJoshForToday : AppCompatActivity() {

    var binding: ActivityMyJoshForTodayBinding? = null
    var point: Int = 0
    var reasonId: Int? = null
    var dialog: CustomLoader? = null
    val map: HashMap<String, Any> = hashMapOf()
    var clickable = false
    lateinit var context: Context

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyJoshForTodayBinding.inflate(layoutInflater)
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

        binding!!.userNameTxt.text =
            SharedPrefManager.getInstance(context)!!.user.firstName + " " + SharedPrefManager.getInstance(
                context
            )!!.user.lastName

        binding!!.designation.text = SharedPrefManager.getInstance(context)!!.user.designation

        val emoji = intent.getIntExtra("emoji", -1)
        println("isOpen")
        println(emoji)

        when (emoji) {
            5 -> {
                binding!!.lowMoodLayout.visibility = View.GONE
                emojiEffect("image5")

                if (SharedPrefManager.getInstance(context)!!.user.roleId == 1) {
                    submitCustomerJosh(5)
                } else {
                    submitManagerJosh(5)
                }
                binding!!.text5.visibility = View.VISIBLE
                map["reason_type"] = ""
            }
            4 -> {
                emojiEffect("image4")

                binding!!.lowMoodLayout.visibility = View.GONE
                if (SharedPrefManager.getInstance(context)!!.user.roleId == 1) {
                    submitCustomerJosh(4)
                } else {
                    submitManagerJosh(4)
                }
                map["reason_type"] = ""
                binding!!.text4.visibility = View.VISIBLE
            }
            3 -> {
                point = 3
                binding!!.lowMoodLayout.visibility = View.VISIBLE
                emojiEffect("image3")
                binding!!.text3.visibility = View.VISIBLE
            }
            2 -> {
                point = 2
                binding!!.lowMoodLayout.visibility = View.VISIBLE
                emojiEffect("image2")
                binding!!.text2.visibility = View.VISIBLE
            }
            1 -> {
                point = 1
                binding!!.lowMoodLayout.visibility = View.VISIBLE
                emojiEffect("image1")
                binding!!.text1.visibility = View.VISIBLE
            }
        }

        binding!!.image5.setOnClickListener { view1: View ->

            if (clickable) {
                binding!!.lowMoodLayout.visibility = View.GONE
                binding!!.text5.visibility = View.VISIBLE
                binding!!.text4.visibility = View.GONE
                binding!!.text3.visibility = View.GONE
                binding!!.text2.visibility = View.GONE
                binding!!.text1.visibility = View.GONE
                emojiEffect(view1.tag.toString())

                if (SharedPrefManager.getInstance(context)!!.user.roleId == 1) {
                    submitCustomerJosh(5)
                } else {
                    submitManagerJosh(5)
                }
                map["reason_type"] = ""
            }
        }

        binding!!.image4.setOnClickListener { view1: View ->
            if (clickable) {
                binding!!.text4.visibility = View.VISIBLE
                binding!!.text5.visibility = View.GONE
                binding!!.text3.visibility = View.GONE
                binding!!.text2.visibility = View.GONE
                binding!!.text1.visibility = View.GONE
                emojiEffect(view1.tag.toString())

                binding!!.lowMoodLayout.visibility = View.GONE
                if (SharedPrefManager.getInstance(context)!!.user.roleId == 1) {
                    submitCustomerJosh(4)
                } else {
                    submitManagerJosh(4)
                }
                map["reason_type"] = ""
            }
        }

        binding!!.image3.setOnClickListener { view1: View ->
            if (clickable) {
                point = 3
                binding!!.text3.visibility = View.VISIBLE
                binding!!.text5.visibility = View.GONE
                binding!!.text4.visibility = View.GONE
                binding!!.text2.visibility = View.GONE
                binding!!.text1.visibility = View.GONE
                binding!!.lowMoodLayout.visibility = View.VISIBLE
                emojiEffect(view1.tag.toString())
            }
        }

        binding!!.image2.setOnClickListener { view1: View ->
            if (clickable) {
                point = 2
                binding!!.text2.visibility = View.VISIBLE
                binding!!.text5.visibility = View.GONE
                binding!!.text4.visibility = View.GONE
                binding!!.text3.visibility = View.GONE
                binding!!.text1.visibility = View.GONE
                binding!!.lowMoodLayout.visibility = View.VISIBLE
                emojiEffect(view1.tag.toString())
            }
        }

        binding!!.image1.setOnClickListener { view1: View ->
            if (clickable) {
                point = 1
                binding!!.text1.visibility = View.VISIBLE
                binding!!.text5.visibility = View.GONE
                binding!!.text4.visibility = View.GONE
                binding!!.text3.visibility = View.GONE
                binding!!.text2.visibility = View.GONE
                binding!!.lowMoodLayout.visibility = View.VISIBLE
                emojiEffect(view1.tag.toString())
            }
        }

        binding!!.submit.setOnClickListener {
            if (validation()) {
                if (SharedPrefManager.getInstance(context)!!.user.roleId == 1) {
                    submitCustomerJosh(point)
                } else {
                    submitManagerJosh(point)
                }
            }
        }

        paramsOld.rightMargin = 5
        paramsOld.topMargin = 5
        paramsNew.rightMargin = 5
        paramsNew.topMargin = 5


        if (SharedPrefManager.getInstance(context)!!.user.roleId == 1) {
            getJoshReasonType()
            getJoshToday()
        } else {
            getJoshTodayForManager()
        }


        loadAvatarImage(context, binding!!.imageView)

    }


    private fun validation(): Boolean {
        val reason = binding!!.spinner.selectedItemPosition
        val description: String = binding!!.description.text.toString()
        if (reason == 0) {
            Toast.makeText(context, "Select Reason", Toast.LENGTH_LONG).show()
            return false
        }
        if (description.isEmpty()) {
            binding!!.description.requestFocus()
            binding!!.description.error = "Required"
            return false
        }
        return true
    }

    private var paramsNew = LinearLayout.LayoutParams(width / 7, 150)
    private var paramsOld = LinearLayout.LayoutParams(width / 9, 110)

    private fun emojiEffect(tag: String) {
        println("emojiEffect")
        println(tag)
        paramsOld.rightMargin = 5
        paramsOld.topMargin = 5
        paramsNew.rightMargin = 5
        paramsNew.topMargin = 5
        binding!!.image1.layoutParams = if (tag == "image1") paramsNew else paramsOld
        binding!!.image2.layoutParams = if (tag == "image2") paramsNew else paramsOld
        binding!!.image3.layoutParams = if (tag == "image3") paramsNew else paramsOld
        binding!!.image4.layoutParams = if (tag == "image4") paramsNew else paramsOld
        binding!!.image5.layoutParams = if (tag == "image5") paramsNew else paramsOld
    }

    // to burst fireworks
    private fun burstFireworks() {
//        println("$x burstFireworks $y")
        val lp: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(20, 20)
        // creating lottieAnimation view
        val cracker = LottieAnimationView(context)
        cracker.layoutParams = lp
        // initializing lottie view from json file
        cracker.setAnimation(R.raw.fireworks)
        // setting height and width of lottie file
        cracker.layoutParams.height = width + width
        cracker.layoutParams.width = width
        // setting positions of lottie file
        cracker.x = 20f
        cracker.y = 20f
        // adding lottieAnimationView to relative layout
        binding!!.ll2.addView(cracker)
        cracker.playAnimation()

    }

    private fun getJoshReasonType() {
        dialog!!.show()
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val result = dataServices.getJoshReasonType()
        result.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getJoshReasonType")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {

                    val jsonArray =
                        JSONArray(
                            Gson().toJson(
                                response.body()?.getAsJsonArray("customer_reason_type")
                            )
                        )
                    val turnsType = object : TypeToken<List<JoshReasonModel>>() {}.type
                    val list: ArrayList<JoshReasonModel> =
                        Gson().fromJson(jsonArray.toString(), turnsType)
                    val items: ArrayList<String?> = ArrayList()
                    items.add("Select Reason")
                    for (item in list) {
                        items.add(item.reasonName)
                    }
                    val adapter = ArrayAdapter(
                        context,
                        R.layout.simple_spinner_item, items
                    ) //setting the country_array to spinner
                    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                    binding!!.spinner.adapter = adapter
                    binding!!.spinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                p0: AdapterView<*>?,
                                p1: View?,
                                p2: Int,
                                p3: Long
                            ) {
                                reasonId = list[p2].id!!
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {
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

    private fun getJoshReasonTypeForManager() {
        dialog!!.show()
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val result = dataServices.getJoshReasonTypeForManager()
        result.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getJoshReasonTypeForManager")
                println(response.body())
                val jsonArray =
                    JSONArray(Gson().toJson(response.body()?.getAsJsonArray("Manager_reason_type")))
                val turnsType = object : TypeToken<List<JoshReasonModel>>() {}.type
                val list: ArrayList<JoshReasonModel> =
                    Gson().fromJson(jsonArray.toString(), turnsType)
                val items: ArrayList<String?> = ArrayList()
                items.add("Select Reason")
                for (item in list) {
                    items.add(item.reasonName)
                }
                list.add(0, JoshReasonModel(0, "", "", "", 0))
                val adapter = ArrayAdapter(
                    context,
                    R.layout.simple_spinner_item,
                    items
                ) //setting the country_array to spinner
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                binding!!.spinner.adapter = adapter

                binding!!.spinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            p2: Int,
                            p3: Long
                        ) {
                            reasonId = list[p2].id!!
                            println("reasonId")
                            println(reasonId)
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
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

    private fun submitCustomerJosh(point: Int) {
        dialog!!.show()
        map["user_profile"] = SharedPrefManager.getInstance(context)!!.user.id.toString()
        map["description"] = binding!!.description.text.toString()
        map["emoji_point"] = point
        map["manager_id"] = SharedPrefManager.getInstance(context)!!.user.teamId.toString()
        if (point != 4 && point != 5) {
            map["reason_type"] = reasonId!!.toInt()
        } else {
            map["reason_type"] = ""
        }
        println(map)
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = dataServices.submitCustomerJosh(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("submitCustomerJosh")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        getJoshToday()
                        binding!!.description.text.clear()
                        clickable = false
                        binding!!.lowMoodLayout.visibility = View.GONE

                        if (point != 5 && point != 4) {
                            showDialog(obj)
                        } else {
                            showBonusDialog(obj)
                        }


                    } else {
                        Toast.makeText(
                            context,
                            obj.getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                dialog!!.dismiss()

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialog!!.dismiss()
            }
        })
    }

    private val TAG = "MyJoshForToday"

    fun showBonusDialog(obj: JSONObject) {
        val obj2: JSONObject = obj.getJSONObject("reward_points_data")
        Log.i(TAG, "onResponse: $obj2")
        if (obj2.length() > 0) {
            val points = obj2.getString("reward_points")
            val message = obj2.getString("reward_message")

            if (points.toInt() > 0) {
                var alert = Constant.AlertDialog2(
                    this,
                    R.style.ThemeDialogCustom,
                    points,
                    message, listener = object : OnBonusDialogDismissInterface {
                        override fun onDismiss(boolean: Boolean) {
                        }
                    })
                alert.show()
            }


            Log.i(TAG, "onResponse: reward_points $points reward_message $message")

        } else {
            Log.i(TAG, "onResponse: reward_points null")

        }

        Log.i(TAG, "reward_points_data: $obj2")
    }

    private fun submitManagerJosh(point: Int) {
        dialog!!.show()
        map["manager"] = SharedPrefManager.getInstance(context)!!.user.id.toString()
        map["description"] = binding!!.description.text.toString()
        map["emoji_point"] = point
        if (point != 4 && point != 5) {
            map["reason_type"] = reasonId!!.toInt()
        } else {
            map["reason_type"] = ""
        }
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = dataServices.submitManagerJosh(map)
        Log.i(TAG, "submitManagerJosh:req " + Gson().toJson(call.request()))
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("submitManagerJosh")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        getJoshTodayForManager()
                        binding!!.description.text.clear()
                        clickable = false
                        binding!!.lowMoodLayout.visibility = View.GONE
                        if (point != 5 && point != 4) {
                            showDialog(obj)
                        } else {
                            showBonusDialog(obj)

                        }
                    } else {
                        Toast.makeText(
                            context,
                            obj.getString("msg"),
                            Toast.LENGTH_LONG
                        ).show()

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

    private fun showDialog(obj: JSONObject) {
        AlertDialog.Builder(context)
            .setIcon(R.mipmap.logo)
            .setTitle("Submitted")
            .setMessage("Thank you for sharing how you feel and why, give us time to find a way to help")
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ -> }
            .setOnDismissListener { showBonusDialog(obj) }
            .show()
    }

    private fun getJoshToday() {
        dialog!!.show()
        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call =
            services.getJoshToday(
                SharedPrefManager.getInstance(context)!!.user.id.toString(),
                SharedPrefManager.getInstance(context)!!.user.managerId.toString()
            )
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                println("getJoshToday")
                println(SharedPrefManager.getInstance(context)!!.user.id.toString())
                println(SharedPrefManager.getInstance(context)!!.user.managerId.toString())
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {

                            updateMoodUI(jsonObject)

                            val jsonArray = jsonObject.getJSONArray("moodalytics")

                            setUpLineChart(jsonArray)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                dialog!!.dismiss()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                dialog!!.dismiss()
                Log.e("getJoshToday t", t.message!!)
            }
        })
    }

    private fun getJoshTodayForManager() {
        dialog!!.show()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getJoshTodayForManager(
            Objects.requireNonNull(SharedPrefManager(context).user.id!!),
            Objects.requireNonNull(SharedPrefManager(context).user.teamId!!.toInt())
        )
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                println("getJoshTodayForManager")
                println(Objects.requireNonNull(SharedPrefManager(context).user.id!!))
                if (response.code() == 200) {
                    assert(response.body() != null)
                    Log.e("getJoshTodayForManager", response.body().toString())
                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {

                            updateMoodUI(jsonObject)

                            val jsonArray = jsonObject.getJSONArray("moodalytics")

                            setUpLineChart(jsonArray)

                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
                dialog!!.dismiss()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                dialog!!.dismiss()
                Log.e("getJoshTodayForManager t", t.message!!)
            }
        })
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun updateMoodUI(jsonObject: JSONObject) {

        val data = jsonObject.getJSONObject("data")
        val list = Gson().fromJson(
            data.toString(),
            JoshModel::class.java
        )
        if (data.isNull("emoji_point")) {
            clickable = true
            if (SharedPrefManager.getInstance(context)!!.user.roleId == 1) {
                getJoshReasonType()
            } else {
                getJoshReasonTypeForManager()
            }
        } else {
            val sdf =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val dd = sdf.parse(data.getString("created_at").substring(0, 19))
            val newFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a")
            val newDate = newFormat.format(dd!!)
            binding!!.time.text = newDate
            when (list.emoji_point) {
                5 -> {
                    emojiEffect("image5")
                    binding!!.text5.visibility = View.VISIBLE
                }
                4 -> {
                    emojiEffect("image4")
                    binding!!.text4.visibility = View.VISIBLE
                }
                3 -> {
                    emojiEffect("image3")
                    binding!!.text3.visibility = View.VISIBLE
                }
                2 -> {
                    emojiEffect("image2")
                    binding!!.text2.visibility = View.VISIBLE
                }
                1 -> {
                    emojiEffect("image1")
                    binding!!.text1.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
        when (jsonObject.getInt("team_mood")) {
            5 -> {
                binding!!.teamMood.setImageResource(R.drawable.happy)
                binding!!.teamMoodTv.text = "The team is feeling awesome\ntoday"
            }
            4 -> {
                binding!!.teamMood.setImageResource(R.drawable.smile)
                binding!!.teamMoodTv.text = "The team is feeling good\ntoday"
            }
            3 -> {
                binding!!.teamMood.setImageResource(R.drawable.okay)
                binding!!.teamMoodTv.text = "The team is feeling Okay\ntoday"
            }
            2 -> {
                binding!!.teamMood.setImageResource(R.drawable.amber)
                binding!!.teamMoodTv.text = "The team is feeling Low/Upset\ntoday"
            }
            1 -> {
                binding!!.teamMood.setImageResource(R.drawable.angry)
                binding!!.teamMoodTv.text = "The team is feeling Angry/Stressed\ntoday"
            }
            else -> {
                binding!!.teamMoodTv.text = "The team is feeling good\ntoday"
            }
        }
    }

    private fun setUpLineChart(jsonArray: JSONArray) {
        val turnsType = object : TypeToken<List<MoodalyticsModel>>() {}.type
        val list: ArrayList<MoodalyticsModel> =
//                                ArrayList()
            Gson().fromJson(jsonArray.toString(), turnsType)


        val lineEntriesList: ArrayList<Entry> = ArrayList()


        val labels = ArrayList<String>()

        for (i in 0 until list.size) {
            val model = list[i]
            labels.add(getStringFormatted(model.created_at))
            lineEntriesList.add(Entry((i).toFloat(), model.emoji_point.toFloat()))
        }

        val lineDataSet = LineDataSet(lineEntriesList, null)

        lineDataSet.valueFormatter = DefaultValueFormatter(0)
        lineDataSet.valueTextSize = 10f

        val lineData = LineData(lineDataSet)

        binding!!.lineChart.legend.isEnabled = false

        binding!!.lineChart.data = lineData

        binding!!.lineChart.description.isEnabled = false

        val xLine = binding!!.lineChart.axisLeft
        xLine.labelCount = 3
        xLine.axisMaximum = 5f
        xLine.axisMinimum = 1f
        xLine.spaceMin = .5f

//        binding!!.lineChart.extraRightOffset = 25f

        val xAxisLine: XAxis = binding!!.lineChart.xAxis
        xAxisLine.valueFormatter = IndexAxisValueFormatter(labels)
        xAxisLine.textColor = ContextCompat.getColor(context, R.color.white)

        xAxisLine.setAvoidFirstLastClipping(false)  //always set to false

        xAxisLine.labelRotationAngle = -60f

        // below line is to set granularity
        // to our x axis labels.
        xAxisLine.granularity = 1.0f
        // below line is to enable
        // granularity to our x axis.
        xAxisLine.isGranularityEnabled = true

        // below line is to set position
        // to our x-axis to bottom.
        xAxisLine.position = XAxis.XAxisPosition.BOTTOM
        xAxisLine.setCenterAxisLabels(false)

//      binding!!.lineChart.setViewPortOffsets(0f,0f,0f,100f)
        binding!!.lineChart.extraBottomOffset = 40f

        val yLine = binding!!.lineChart.axisRight
        yLine.isEnabled = false

        // on below line we are setting colors for our bar chart text
        lineDataSet.valueTextColor = Color.WHITE
        lineDataSet.lineWidth = 2f
        lineDataSet.circleColors
        lineDataSet.setCircleColor(ContextCompat.getColor(context, R.color.green_light))
        lineDataSet.circleHoleColor = ContextCompat.getColor(context, R.color.green_light)

        // on below line we are setting color for our bar data set
        lineDataSet.color = ContextCompat.getColor(context, R.color.green_light)

        binding!!.lineChart.axisLeft.textColor = ContextCompat.getColor(context, R.color.white)

        // below line is to make our
        // bar chart as draggable.
        binding!!.lineChart.isDragEnabled = true

        // below line is to make visible
        // range for our bar chart.
        binding!!.lineChart.setVisibleXRangeMaximum(8f)

        binding!!.lineChart.invalidate()

    }

}

class LineChartXAxisValueFormatter : IndexAxisValueFormatter() {

    override fun getFormattedValue(value: Float): String {

        // Convert float value to date string
        // Convert from seconds back to milliseconds to format time  to show to the user
//        val emissionsMilliSince1970Time = value.toLong() * 1000

        // Show time in local version
//        val timeMilliseconds = Date(emissionsMilliSince1970Time)
//        val dateTimeFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        return value.toInt().toString() + "%"
    }
}