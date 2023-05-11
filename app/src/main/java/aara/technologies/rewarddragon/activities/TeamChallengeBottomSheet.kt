package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.databinding.FragmentTeamChallengeBinding
import aara.technologies.rewarddragon.model.IndustryModel
import aara.technologies.rewarddragon.model.KpiModel
import aara.technologies.rewarddragon.model.PurposeModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant
import aara.technologies.rewarddragon.utils.Constant.convert12to24format
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.OnRefresh
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "TeamChallengeBottomShee"

class TeamChallengeBottomSheet(refresh: OnRefresh) : BottomSheetDialogFragment() {

    private lateinit var timeStr: String
    var binding: FragmentTeamChallengeBinding? = null

    var challengePurposeId: Int? = null
    var industryId: Int? = null
    var kpiId: Int? = null
    val items: MutableList<PurposeModel> = ArrayList()

    var customLoader: CustomLoader? = null
    var onRefresh: OnRefresh? = null

    var startTimeText: String? = null
    var endTimeText: String? = null
    var bonusTimeText: String? = null

    var sharedPrefManager: SharedPrefManager? = null

    private val tAG = "TeamChallengeBottomSheet"
    var timeSelected = false

    init {
        onRefresh = refresh

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTeamChallengeBinding.inflate(
            layoutInflater
        )
        customLoader = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)
        sharedPrefManager = SharedPrefManager.getInstance(requireContext())
        Log.i(tAG, "onCreateView: ")
        return binding!!.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(tAG, "onViewCreated: ")
        binding!!.starttimetv.setOnClickListener {
            showDialog(binding!!.starttimetv)
            // timePickerDialog(binding!!.starttimetv)
        }

        binding!!.endtimetv.setOnClickListener {
            showDialog(binding!!.endtimetv)
            // timePickerDialog(binding!!.endtimetv)

        }

        binding!!.submitBtn.setOnClickListener {

            Log.i(TAG, "onViewCreated: "+binding!!.bonusPoints.text)
            if (binding!!.challengeName.text.toString().isEmpty()) {
                binding!!.challengeName.error = "Enter Challenge Name"
            } else if (binding!!.starttimetv.text.toString().isEmpty()) {
                binding!!.starttimetv.error = "Select Start time"
            } else if (binding!!.endtimetv.text.toString().isEmpty()) {
                binding!!.endtimetv.error = "Select End time"
            } else if (binding!!.activityDetails.text.toString().isEmpty()) {
                binding!!.activityDetails.error = "Enter Activity Details"
            } else if (binding!!.bonusPoints.text.toString().isEmpty()) {
                binding!!.bonusPoints.error = "Enter Bonus Point"
            } else if (!timeCompare()) {
                Toast.makeText(
                    context,
                    "Start Time is not less then Current Time and End time is not less then Start time !",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                createChallenge(
                    binding!!.challengeName.text.toString(),
                    challengePurposeId,
                    binding!!.starttimetv.text.toString(),
                    binding!!.endtimetv.text.toString(),
                    binding!!.activityDetails.text.toString(),
                    binding!!.bonusPoints.text.toString()
                )
            }
        }




        binding!!.calenderIv.setOnClickListener {
            //timePickerDialog(binding!!.bonusPoints)

            showDialog(binding!!.bonusPoints)


        }

        getChallengePurpose()
        getIndustryListData()
    }

    private fun showDialog(startTime: TextView) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.custom_timepickerlayout)

        val linearLayout = dialog.findViewById<LinearLayout>(R.id.linearLayout)

        timePicker(linearLayout, dialog, startTime)

        dialog.show()

    }

    private fun timeCompare(): Boolean {

        val pattern = "hh:mm:ss a"
        val sdf = SimpleDateFormat(pattern)

        val currentTime3 = Calendar.getInstance().time

        Log.i(TAG, "timeCompare: current1 $currentTime3")


        var currentTime1 = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(currentTime3)

        Log.i(TAG, "timeCompare: current2 $currentTime1")


        var currentTime = currentTime1.replace("am", "AM").replace("pm", "PM");




        Log.i(TAG, "timeCompare: startTime $startTimeText")
        Log.i(TAG, "timeCompare: endTime $endTimeText")

        try {
            val date1 = sdf.parse(startTimeText)
            var date2 = sdf.parse(endTimeText)
            var currentTime = sdf.parse(currentTime)
            // Outputs -1 as date1 is before date2
            if (date1 != null) {
                var startTime = date1.time
                var endTime = date2.time
                var currTime = currentTime.time

                Log.i(TAG, "timeCompare: date1 $startTime")
                Log.i(TAG, "timeCompare: date2 $endTime")
                Log.i(TAG, "timeCompare: current $currTime")
                return if (startTime >= currTime) {
                    Log.i(
                        TAG,
                        "compaire: date one greter then or equal currentTime ${endTime > startTime}"
                    )


                    return endTime > startTime

                } else {
                    Log.i(TAG, "compaire: date one less then currentTime ")
                    false
                }
            }

        } catch (e: ParseException) {
            // Exception handling goes here

        }
        return false;

    }

    private fun timePicker(linearLayout: LinearLayout, dialog: Dialog, selectedTimeTv: TextView) {

        // val txtView = TextView(context)
        val timePicker = TimePicker(context)
        val select = Button(context)

        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        var hour_ = ""
        var min_ = ""
        var amPm1 = ""

        timePicker.layoutParams = layoutParams
        timePicker.setOnTimeChangedListener { _, hour, minute ->
            var hour = hour
            var amPm = ""

            // AM_PM decider logic
            when {
                hour == 0 -> {
                    hour += 12
                    amPm = "AM"
                }
                hour == 12 -> amPm = "PM"
                hour > 12 -> {
                    hour -= 12
                    amPm = "PM"
                }
                else -> amPm = "AM"
            }

            hour_ = if (hour > 9) hour.toString() else "0$hour"
            min_ = if (minute > 9) minute.toString() else "0$minute"
            amPm1 = amPm
        }
        linearLayout.addView(timePicker)
        // linearLayout.addView(txtView)
        select.text = "Select"
        select.setOnClickListener {
            var currentTime =
                SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
            if (hour_.isNullOrEmpty() && min_.isNullOrEmpty()) {

                //     val msg = "$hour_:$min_:00 $amPm1"
                currentTime = currentTime.replace("am", "AM").replace("pm", "PM");
                selectedTimeTv.text = currentTime
                dialog.dismiss()
                Log.i(TAG, "timePicker: not selected ")
            } else {
                val msg = "$hour_:$min_:00 $amPm1"
                selectedTimeTv.text = msg
                dialog.dismiss()
                Log.i(TAG, "timePicker: selected ")

            }

            if (selectedTimeTv.tag.equals("startTime")) {
                startTimeText = selectedTimeTv.text.toString()
            } else if (selectedTimeTv.tag.equals("endTime")) {
                endTimeText = selectedTimeTv.text.toString()
            } else if (selectedTimeTv.tag.equals("bonusTime")) {
                bonusTimeText = selectedTimeTv.text.toString()
            }


        }
        linearLayout.addView(select)
    }

    private fun getIndustryListData() {
        val map: HashMap<String, String> = hashMapOf()
        map["unique_code"] =
            sharedPrefManager!!.user.uniqueCode.toString()
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val result = dataServices.getIndustryListData(map)
        result.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getIndustryListData")
                println(response.code())
                println(response.body())

                if (response.code() == 200) {

                    val jsonArray = JSONArray(
                        Gson().toJson(
                            response.body()?.getAsJsonArray("industry_work_data")
                        )
                    )
                    val turnsType = object : TypeToken<List<IndustryModel>>() {}.type
                    val list: ArrayList<IndustryModel> =
                        Gson().fromJson(jsonArray.toString(), turnsType)
                    val items: ArrayList<String?> = ArrayList()
                    for (item in list) {
                        items.add(item.name)
                    }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.simple_spinner_item,
                        items
                    ) //setting the country_array to spinner
                    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                    binding!!.industry.adapter = adapter
                    binding?.industry?.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                p0: AdapterView<*>?,
                                p1: View?,
                                p2: Int,
                                p3: Long
                            ) {
                                industryId = list[p2].id
                                println("industryId")
                                println(industryId)
                                getKpiListData(industryId.toString())
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {
                            }

                        }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
            }
        })
    }

    private fun getKpiListData(industryId: String) {
        customLoader?.show()
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val result = dataServices.getKpiListData(industryId, "Challenge")
        Log.i(TAG, "getKpiListData: " + Gson().toJson(result.request()))
        result.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                /*        println("getKpiListData")
                        println(response.body())*/

                Log.i(TAG, "onResponse: " + response.body())
                val jsonArray =
                    JSONArray(Gson().toJson(response.body()?.getAsJsonArray("kpi_name_data")))

                if (jsonArray.isNull(0)) {
                    binding!!.bonusPoints.text = null
                    binding!!.calenderIv.visibility = View.GONE
                    binding!!.bonusPoints.isEnabled = true
                    Log.i(TAG, "onResponse: jsonArray.isNull")
                }
                val turnsType = object : TypeToken<List<KpiModel>>() {}.type
                val list: ArrayList<KpiModel> =
                    Gson().fromJson(jsonArray.toString(), turnsType)
                val items: ArrayList<String?> = ArrayList()
                for (item in list) {
                    items.add(item.name)
                    //  items.add(item.is_time)
                }
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.simple_spinner_item,
                    items
                ) //setting the country_array to spinner
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                binding!!.kpi.adapter = adapter


                binding?.kpi?.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            p2: Int,
                            p3: Long
                        ) {
                            kpiId = list[p2].id
                            val isTime: Int = list[p2].is_time
                            onUIThread(isTime)

                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
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


    private fun onUIThread(isTime: Int) {
        activity?.runOnUiThread(Runnable {

            if (isTime == 1) {
                binding!!.bonusPoints.text = null
                binding!!.calenderIv.visibility = View.VISIBLE
                binding!!.bonusPoints.isEnabled = false
            } else {
                Log.i(TAG, "onResponse: keyboard  ")

                binding!!.bonusPoints.text = null
                binding!!.calenderIv.visibility = View.GONE
                binding!!.bonusPoints.isEnabled = true
                /*  binding!!.bonusPoints.isFocusableInTouchMode = true
                  binding!!.bonusPoints.isCursorVisible = true
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                      binding!!.bonusPoints.focusable = View.FOCUSABLE
                  }
                  binding!!.bonusPoints.isFocusable = true*/
            }

        })
    }

    private fun timePickerDialog(timeEdittext: TextView) {

        val mTimePicker: TimePickerDialog
        val myCalendar1 = Calendar.getInstance()
        val hour = myCalendar1[Calendar.HOUR_OF_DAY]
        val minute = myCalendar1[Calendar.MINUTE]

        mTimePicker = TimePickerDialog(
            context,
            { _, hourOfDay, minute1 ->
                var time = "$hourOfDay:$minute1"
                val fmt = SimpleDateFormat("hh:mm")
                var date: Date? = null
                try {
                    date = fmt.parse(time)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                val fmtOut = SimpleDateFormat("hh:mm:ss a")
                val formattedTime = fmtOut.format(date)
                //timeStr = formattedTime

                if (timeEdittext.tag.equals("startTime")) {
                    startTimeText = formattedTime
                } else if (timeEdittext.tag.equals("endTime")) {
                    endTimeText = formattedTime
                } else if (timeEdittext.tag.equals("bonusTime")) {
                    bonusTimeText = timeEdittext.text.toString()
                }
                timeEdittext!!.text = formattedTime
                Log.i(
                    TAG,
                    "onTimeSet: $formattedTime"
                )
            }, hour, minute, false
        )
        mTimePicker.show()

        mTimePicker.setTitle("Select Time")

        /*TimePickerDialog dialog = new TimePickerDialog(getContext(), timeSetListener, hour, minute, true);
        dialog.show();*/
    }


    private fun getChallengePurpose() {
        customLoader!!.show()
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val result = dataServices.getChallengePurposeList()

        result.enqueue(object : Callback<JsonObject>, AdapterView.OnItemSelectedListener {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getChallengePurpose")
                try {


                    val jsonArray =
                        JSONArray(
                            Gson().toJson(
                                response.body()?.getAsJsonArray("challenge_purpose")
                            )
                        )
                    val turnsType = object : TypeToken<List<PurposeModel>>() {}.type
                    val list: ArrayList<PurposeModel> =
                        Gson().fromJson(jsonArray.toString(), turnsType)

                    //    items.add("Select")

                    val arrayList: ArrayList<String> = ArrayList()

                    if (items != null && arrayList != null) {
                        items.clear()
                        arrayList.clear()
                    }

                    for (item in list) {
                        println(item.purpose_name)
                        val pm = PurposeModel(item.id, item.purpose_name, item.status)
                        items.add(pm)
                        arrayList.add(item.purpose_name)
                    }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.simple_spinner_item, arrayList
                    ) //setting the country_array to spinner
                    binding?.spinner?.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                p0: AdapterView<*>?,
                                p1: View?,
                                p2: Int,
                                p3: Long
                            ) {
                                challengePurposeId = items[p2].id
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {
                            }

                        }

                    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item2)
                    binding!!.spinner.adapter = adapter

                    customLoader!!.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
                customLoader!!.dismiss()
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }


        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChallenge(
        challengeName: String,
        challenge_purpose_id: Int?,
        start_time: String,
        end_time: String,
        activity_Details: String,
        reward_point: String
    ) {
        customLoader?.show()
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["user_id"] = sharedPrefManager!!.user.id.toString()
        hashMap["challenge_purpose_id"] = challenge_purpose_id.toString()
        hashMap["challenge_name"] = challengeName
        hashMap["start_time"] = convert12to24format(start_time)
        hashMap["end_time"] = convert12to24format(end_time)
        hashMap["activity_details"] = activity_Details
        hashMap["bonus_point"] = reward_point
        hashMap["manager_created_date"] = Constant.getCurrentDate()
        hashMap["manager_created_time"] = Constant.getCurrentTime()
        hashMap["industry_work_type_id"] = "" + industryId
        hashMap["kpi_name_id"] = "" + kpiId
        hashMap["broadcast_id"] = 1
        println("createTeamChallenge $hashMap")
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = dataServices.createChallenge(hashMap)
        Log.i(tAG, "createChallenge: $hashMap")
        Log.i(tAG, "createChallenge: ${Gson().toJson(call.request())}")
        Log.i(tAG, "createChallenge: ${call.isExecuted}")
        call.enqueue(object : Callback<ChallengeResponse> {
            override fun onResponse(
                call: Call<ChallengeResponse>,
                response: Response<ChallengeResponse>
            ) {

                Log.i(
                    TAG,
                    "onResponse:resss " + response.code() + " msg " + response.message() + " code " + response.code()
                )

                try {
                    Log.i(tAG, "onResponse1: " + response.body().toString())
                    val res: ChallengeResponse? = response.body()
                    Log.i(
                        tAG,
                        "onResponse: ${res?.response_code} msg ${res?.message}  challenge_id ${res?.challenge_id}"
                    )
                    if (res!!.response_code == 200) {
                        AlertDialog.Builder(requireContext())
                            .setIcon(R.drawable.ic_baseline_check_circle_24)
                            .setTitle("Success!")
                            .setMessage(res.message)
                            .setPositiveButton("OK") { _, _ ->
                            }
                            .show()
                        onRefresh?.refresh()
                        dismiss()
                        sendToWhatsapp(res?.challenge_id.toString())

                    } else {
                        AlertDialog.Builder(requireContext())
                            .setIcon(R.drawable.close_circle)
                            .setTitle("Error !")
                            .setMessage(res.message)
                            .setPositiveButton("Close") { _, _ ->


                            }
                            .show()
                    }
                } catch (e: Exception) {

                    Log.i(TAG, "onResponse: ${e.message}")
                    println(e.message)

                }
                customLoader!!.dismiss()
            }

            override fun onFailure(call: Call<ChallengeResponse>, t: Throwable) {
                Toast.makeText(context, "" + t.message, Toast.LENGTH_SHORT).show()
                customLoader?.dismiss()
            }
        })
    }


    data class ChallengeResponse(
        @SerializedName("message") var message: String? = null,
        @SerializedName("response_code") var response_code: Int? = null,
        @SerializedName("challenge_id") var challenge_id: String? = null
    )


    private fun sendToWhatsapp(challenge_id: String) {
        val map: HashMap<String, Any> = hashMapOf()
        map["user_id"] = sharedPrefManager!!.user.id.toString()
        map["challenge_id"] = challenge_id

        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.sendChallengeToWhatsapp(map)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
              //  Log.e("$TAG sendToWhatsapp", response.body().toString())

            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
            }
        })
    }


}