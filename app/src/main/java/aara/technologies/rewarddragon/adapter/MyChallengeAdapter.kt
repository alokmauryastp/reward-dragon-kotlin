package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.databinding.MyChallengeLayoutBinding
import aara.technologies.rewarddragon.model.ChallengeModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant
import aara.technologies.rewarddragon.utils.Constant.convert24to12format
import aara.technologies.rewarddragon.utils.OnBonusDialogDismissInterface
import aara.technologies.rewarddragon.utils.OnRefresh
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MyChallengeAdapter(
    var arrayList: ArrayList<ChallengeModel>,
    var context: Context,
    var onRefresh: OnRefresh
) :
    RecyclerView.Adapter<MyChallengeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_challenge_adapter, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = arrayList[position]

        holder.challengeName.text = model.challengeName

        holder.fullDetails.setOnClickListener {
            openDialog(model)
        }
    }


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun printDifferenceDateForHours(time: String, timer: TextView) {
        // 2022-08-06T19:04:36

        val currentTime = Calendar.getInstance().time
//        val endDateDay = "06/08/2022 21:00:00"
//        val endDateDay = "2022-08-06T19:04:36"

        val sdf = SimpleDateFormat("yyyy-MM-dd")

        val currentDate = sdf.format(Date())

        println("currentDate")

        println(currentDate)

        val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val endDate = format1.parse("$currentDate $time")

        //milliseconds
        var different = endDate.time - currentTime.time
        var countDownTimer = object : CountDownTimer(different, 1000) {

            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                var diff = millisUntilFinished
                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                val hoursInMilli = minutesInMilli * 60
                val daysInMilli = hoursInMilli * 24

                val elapsedDays = diff / daysInMilli
                diff %= daysInMilli

                val elapsedHours = diff / hoursInMilli
                diff %= hoursInMilli

                val elapsedMinutes = diff / minutesInMilli

                diff %= minutesInMilli

                val elapsedSeconds = diff / secondsInMilli

                val hour = (if (elapsedHours > 9) elapsedHours else "0$elapsedHours")
                val minute = (if (elapsedMinutes > 9) elapsedMinutes else "0$elapsedMinutes")
                val seconds = if (elapsedSeconds > 9) elapsedSeconds else "0$elapsedSeconds"

                timer.text = "$hour : $minute : $seconds"

            }

            override fun onFinish() {
                timer.text = "Available!"
            }
        }.start()
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var challengeName: TextView
        var fullDetails: ImageView

        init {
            challengeName = itemView.findViewById(R.id.challenge_name)
            fullDetails = itemView.findViewById(R.id.full_details)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun openDialog(model: ChallengeModel) {
        val binding = MyChallengeLayoutBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context).create()

        binding.challengeName.text = model.challengeName
        binding.startTime.text = convert24to12format(model.startTime.toString())
        binding.endTime.text = convert24to12format(model.endTime.toString())
        binding.description.text = model.activityDetails
        binding.kpi.text = model.kpiName.toString()
        binding.points.text = model.bonusPoint.toString()
        if (model.isAccepted == 1) {
            binding.acceptBtn.text = "Accepted"
        } else {
            binding.acceptBtn.setOnClickListener {
                acceptChallenge(model.id.toString(), dialog)
            }
        }
        binding.close.setOnClickListener { dialog.dismiss() }
        printDifferenceDateForHours(model.endTime.toString(), binding.timer)
        dialog.setView(binding.root)
        dialog.show()
    }

    private val TAG = "MyChallengeAdapter"
    private fun acceptChallenge(challengeId: String, dialog: AlertDialog) {
        val map: HashMap<String, Any> = HashMap()
        map["user_id"] = SharedPrefManager.getInstance(context)!!.user.id.toString()
        map["challenge_id"] = challengeId
        map["is_accepted"] = 1
        println(map)
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = dataServices.acceptChallenge(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                Log.i(TAG, "acceptChallenge: "+response.body()+" code "+response.code())

                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.get("response_code")
                    dialog.dismiss()
                    onRefresh.refresh()

                    val obj2: JSONObject = obj.getJSONObject("reward_points_data")
                    Log.i(TAG, "onResponse: $obj2")
                    if (obj2.length() > 0) {
                        val points = obj2.getString("reward_points")
                        val message = obj2.getString("reward_message")

                        if (points.toInt() > 0) {
                            var alert = Constant.AlertDialog2(
                                context,
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
                    Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("acceptChallenge t")
                println(t.message)
            }
        })
    }

    private fun completeChallenge(challengeId: String) {
        val map: HashMap<String, String> = HashMap()
        map["user_id"] = SharedPrefManager.getInstance(context)!!.user.id.toString()
        map["challenge_id"] = challengeId
        map["is_completed_by_customer"] = "1"
        println(map)
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = dataServices.completeChallenge(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("completeChallenge")
                println(response.body().toString())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.get("response_code")
                    Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("completeChallenge t")
                println(t.message)
            }
        })
    }
}