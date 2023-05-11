package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.Dashboard
import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.adapter.HealthFitnessAdapter
import aara.technologies.rewarddragon.adapter.InspairedLivingAdapter
import aara.technologies.rewarddragon.adapter.SkillHobbyAdapter
import aara.technologies.rewarddragon.databinding.ActivityMyWellbeingBinding
import aara.technologies.rewarddragon.model.CompanySiteModel
import aara.technologies.rewarddragon.model.LeadershipDataModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.*
import aara.technologies.rewarddragon.utils.Constant.getCurrentDate
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit

class MyWellBeing : Fragment() {

    public var googleFitPermissionRequestCode = 1125
    private val TAG = "MyWellBeing"
    var binding: ActivityMyWellbeingBinding? = null
    private var fitnessOptions: FitnessOptions? = null
    val map: HashMap<String, String> = hashMapOf()
    var dialog: CustomLoader? = null
    lateinit var getContext: Context
    var SharedPrefManager: SharedPrefManager? = null
    var message: String? = null
    var points: String? = null
    var contextm: Context? = null
    var selectedCatDays = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityMyWellbeingBinding.inflate(
            layoutInflater
        )
        getContext = requireContext()
        contextm = Dashboard.context
        // Log.i(TAG, "onCreateView: running")
        return binding!!.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = CustomLoader(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)

        Log.i(TAG, "onViewCreated: ")

        binding!!.recyclerVideo.layoutManager =
            GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
        binding!!.recyclerCompanySite.layoutManager =
            GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)

        binding!!.recyclerInspiredLiving.layoutManager =
            GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)

        binding!!.myMood.setOnClickListener {
            startActivity(Intent(requireContext(), MyJoshForToday::class.java))
        }

        checkGoogleFitPermission()
        getWellBeingData()
        habitOfTheDay()
        getHealthFitnessListData()
        getSkillHobbyList()
        getInspiredLivingList()

    }

    private fun getWellBeingData() {
        val adapter = ArrayAdapter(
            getContext,
            R.layout.simple_spinner_item1,
            arrayOf("Today", "Yesterday", "WTD", "MTD", "YTD", "ALL")
        ) //setting the country_array to spinner
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding!!.timePeriod.adapter = adapter
        binding!!.timePeriod.setSelection(selectedCatDays)
        binding!!.timePeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                map["time_period_all"] = getTime(p0?.selectedItem.toString())
                println(map)
                selectedCatDays = p2
                getWellBeingList()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPrefManager =
            aara.technologies.rewarddragon.utils.SharedPrefManager.getInstance(requireContext())
        Log.i(TAG, "onCreate: ")
    }

    private fun getSkillHobbyList() {
        dialog?.show()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getSkillHobbyList()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getSkillHobbyList")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        val turnsType = object : TypeToken<ArrayList<CompanySiteModel>>() {}.type
                        val list: ArrayList<CompanySiteModel> = Gson().fromJson(
                            obj.getJSONArray("skill_and_hobby_data").toString(),
                            turnsType
                        )
                        println(list.size)
                        binding!!.recyclerCompanySite.adapter =
                            SkillHobbyAdapter(list, Dashboard.context)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == googleFitPermissionRequestCode) {
            val result: GoogleSignInResult? =
                data?.let { Auth.GoogleSignInApi.getSignInResultFromIntent(it) }
            handleSignInResult(result)
        }
    }

    private fun handleSignInResult(task: GoogleSignInResult?) {
        if (task?.isSuccess == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                checkGoogleFitPermission()
            }
            Log.i(TAG, "handleSignInResult: success")
        } else {
            Log.i(TAG, "handleSignInResult: ${task?.status}")
        }
    }


    // Health & fitness
    private fun getHealthFitnessListData() {
        dialog?.show()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getLeaderShipListData()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getHealthFitnessListData")
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        val turnsType = object : TypeToken<ArrayList<LeadershipDataModel>>() {}.type
                        val list: ArrayList<LeadershipDataModel> = Gson().fromJson(
                            obj.getJSONArray("leadership_list_Data").toString(),
                            turnsType
                        )
                        println(list.size)
                        binding!!.recyclerVideo.adapter =
                            HealthFitnessAdapter(
                                list,
                                Dashboard.context,
                                listner = object : onClickkListener {
                                    override fun onCellClickListener(model: LeadershipDataModel) {

                                        val videoId =
                                            model.video_url.substring(
                                                (model.video_url.length - 11),
                                                model.video_url.length
                                            )
                                        val intent = Intent(context, PlayVideoActivity::class.java)
                                            .putExtra("videoId", model.id)
                                            .putExtra("from", "health")
                                            .putExtra("link", videoId)
                                        startActivityForResult(intent, 5221)
                                    }
                                })
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

    //TODO Inspired living

    private fun getInspiredLivingList() {
        dialog?.show()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getFinanceArtList()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                Log.i(TAG, "onResponse: inspired " + response.body())

                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        val turnsType = object : TypeToken<ArrayList<CompanySiteModel>>() {}.type
                        val list: ArrayList<CompanySiteModel> = Gson().fromJson(
                            obj.getJSONArray("finance_and_art_data").toString(),
                            turnsType
                        )
                        println(list.size)
                        binding!!.recyclerInspiredLiving.adapter =
                            InspairedLivingAdapter(list, Dashboard.context)
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


    private fun updateStepTaken(stepsTaken: Int) {
        val map: HashMap<String, Any> = hashMapOf()
        map["user_profile_id"] =
            SharedPrefManager!!.user.id.toString()
        map["steps_count"] = stepsTaken
        map["date"] = getCurrentDate()
        println(map)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.updateStepTaken(map)
        println(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.i(
                    TAG,
                    "updateStepTaken res: " + Gson().toJson(response.body()) + " code " + response.code()
                )
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    showBonusDialog(obj)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
            }
        })
    }


    @Throws(JSONException::class)
    private fun showBonusDialog(obj: JSONObject) {
        val obj2: JSONObject = obj.getJSONObject("reward_points_data")
        Log.i(TAG, "onResponse: $obj2")
        if (obj2.length() > 0) {
            val points = obj2.getString("reward_points")
            val message = obj2.getString("reward_message")

            if (points.toInt() > 0) {
                var alert = Constant.AlertDialog2(
                    contextm!!,
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
    }

    private fun updateHeartPoints(heartPoint: Int) {
        Log.i(TAG, "updateHeartPoints: working")
        val map: HashMap<String, Any> = hashMapOf()
        map["user_profile_id"] = SharedPrefManager!!.user.id.toString()
        map["heart_points_count"] = heartPoint
        map["date"] = getCurrentDate()
        println(map)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.updateHeartPoints(map)
        println(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.i(
                    TAG,
                    "updateHeartPoints res : " + Gson().toJson(response.body()) + " code " + response.code()
                )
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    showBonusDialog(obj)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Log.i(TAG, "onFailure: " + t.message)
                println(t.message)
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

    private fun getWellBeingList() {
        map["user_profile_id"] =
            SharedPrefManager!!.user.id.toString()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.wellBeingList(map)
        println(map)
        call.enqueue(object : Callback<JsonObject> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getWellBeingList")
                println(response.code())
                println(response.body())

                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        binding!!.learningHours.text = obj.getString("learning_hours")
                        binding!!.meditationTime.text = obj.getString("meditation_hours")
                        binding!!.wellBeingPercent.progress = obj.getInt("wellbeing_percent")
                        binding!!.percent.text = obj.getString("wellbeing_percent") + " %"
                        binding!!.stepsTaken.text = obj.getString("steps_count")
                        println("heart_points_count")
                        println(obj.getString("heart_points_count"))
                        binding!!.heartPoints.text = obj.getString("heart_points_count")
                    }
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
            }
        })

    }

    private fun habitOfTheDay() {
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.habitOfTheDay()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("habitOfTheDay")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    binding!!.habitOfTheDay.text = obj.getString("health_habbit")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkGoogleFitPermission() {
        Log.e(TAG, "cheGoogleFitPer")
        fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .build()

        val account: GoogleSignInAccount = getGoogleAccount()
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions!!)) {

            AlertDialog.Builder(requireContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.app_name)
                .setMessage("Reward Dragon wants to access your Google Account linked with google fit to get their Steps and Heart Points Data ")
                .setPositiveButton("OK") { _, _ ->

                    GoogleSignIn.requestPermissions(
                        this,
                        googleFitPermissionRequestCode,
                        account,
                        fitnessOptions!!
                    )


                }
                .setNegativeButton("Not Now", null)
                .show()

            Log.e(TAG, "request for google login1")
        } else {
            binding!!.connected.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.green),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            requestForHistory()
        }

    }

    private fun getGoogleAccount(): GoogleSignInAccount {
        return GoogleSignIn.getAccountForExtension(requireContext(), fitnessOptions!!)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun requestForHistory() {
        val cal: Calendar = Calendar.getInstance()
        cal.time = Date()
        val endTime: Long = cal.timeInMillis
        val year = java.time.LocalDateTime.now().year
        val month = Date().month
        val day = Date().date

        cal.set(year, month, day)
        cal.set(Calendar.HOUR_OF_DAY, 0) // so it get all day and not the current hour

        cal.set(Calendar.MINUTE, 0) // so it get all minute and not the current minute

        cal.set(Calendar.SECOND, 0) // so it get all second and not the current second

        val startTime: Long = cal.timeInMillis

        Log.i(TAG, "Range Start: $startTime")
        Log.i(TAG, "Range End: $endTime")

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .aggregate(DataType.TYPE_HEART_POINTS)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(
            requireContext(),
            GoogleSignIn.getAccountForExtension(requireContext(), fitnessOptions!!)
        ).readData(readRequest)
            .addOnSuccessListener { response ->
                for (dataSet in response.buckets.flatMap { it.dataSets }) {
                    dumpDataSet(dataSet)
                    getWellBeingData()
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was an error reading data from Google Fit", e)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun dumpDataSet(dataSet: DataSet) {
        for (dp in dataSet.dataPoints) {

            for (field in dp.dataType.fields) {
                Log.i(TAG, "\tField: ${field.name} Value: ${dp.getValue(field)}")
                if (field.name == "steps") {
                    updateStepTaken(dp.getValue(field).toString().toInt())
                } else if (field.name == "duration") {

                    val handler = Handler()
                    handler.postDelayed({
                        binding!!.heartPoints.text = dp.getValue(field).toString()
                        updateHeartPoints(dp.getValue(field).toString().toInt())
                    }, 5000)

                }
            }
        }
    }

}