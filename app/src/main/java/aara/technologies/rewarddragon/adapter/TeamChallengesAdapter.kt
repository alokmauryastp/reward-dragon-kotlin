package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.model.ChallengeData
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant.convert24to12format
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.OnRefresh
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TeamChallengesAdapter(
    private val challengeData: ArrayList<ChallengeData>,
    private val context: Context?,
    private val listener: ClickListener,
    private val onRefresh: OnRefresh,
) : RecyclerView.Adapter<TeamChallengesAdapter.ViewHolder>() {

    interface ClickListener {
        fun onClickListener(id: Int, i1: Int)
    }

    var dialog: CustomLoader? = null

    init {
        dialog = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)
        dialog!!.setCancelable(false)
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val startDate: TextView = itemView.findViewById(R.id.start_date)
        val endDate: TextView = itemView.findViewById(R.id.enddate)
        val activityDetails: TextView = itemView.findViewById(R.id.activityDetails)
        val bonusPoints: TextView = itemView.findViewById(R.id.bonus_points)
        val participation: TextView = itemView.findViewById(R.id.perception)

        val winTv: TextView = itemView.findViewById(R.id.wintv)

        val actionBtn: MaterialButton = itemView.findViewById(R.id.action_btn)
        val parent: LinearLayout = itemView.findViewById(R.id.parentll)
        val expandBtn: ImageView = itemView.findViewById(R.id.expand_btn)
        val collapseBtn: ImageView = ItemView.findViewById(R.id.collapse_btn)

        val headChallengeNameTv: TextView = itemView.findViewById(R.id.challenge_name_head)
//        val challengeName: TextView = itemView.findViewById(R.id.challenge_name)

        val kpi: TextView = itemView.findViewById(R.id.kpi)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val challengeData = challengeData[position]

        // if (position == 0) {
        holder.parent.visibility = View.GONE
        holder.startDate.text = convert24to12format(challengeData.startTime.toString())

        holder.endDate.text = convert24to12format(challengeData.endTime.toString())
        holder.activityDetails.text = challengeData.activityDetails
        holder.participation.text = challengeData.participationPercent.toString()+" %"
        holder.winTv.text = challengeData.winPercent.toString()+" %"
        holder.bonusPoints.text = challengeData.bonusPoint.toString()

        holder.headChallengeNameTv.text = challengeData.challengeName
        holder.kpi.text = challengeData.kpiName


        holder.expandBtn.setOnClickListener {

            holder.parent.visibility = View.VISIBLE
            holder.expandBtn.visibility = View.GONE
            holder.collapseBtn.visibility = View.VISIBLE
        }

        holder.collapseBtn.setOnClickListener {
            holder.parent.visibility = View.GONE
            holder.expandBtn.visibility = View.VISIBLE
            holder.collapseBtn.visibility = View.GONE
        }
        if (challengeData.isBroadcasted == 0) {
            holder.actionBtn.text = "Broadcast"
            holder.actionBtn.changeColor(R.color.green)
        } else if (challengeData.isBroadcasted == 1) {
            holder.actionBtn.text = "End Challenge"
            holder.actionBtn.changeColor(R.color.red)
        }
        if(challengeData.isCompletedByManager ==1){
            holder.actionBtn.text = "Ended"
            holder.actionBtn.changeColor(R.color.light_gray)
        }

        holder.actionBtn.setOnClickListener {
            if (challengeData.isBroadcasted == 0) {
                listener.onClickListener(challengeData.id!!, 1)
                // sendBroadcastStatus(Constant.managerUserId, challengeData.id!!, 1)
            } else if (holder.actionBtn.text.toString() == "End Challenge") {
                endChallengeByManager(challengeData.id.toString())
            }
        }
    }

    private fun MaterialButton.changeColor(color: Int) {
        this.background.setTintList(ContextCompat.getColorStateList(context, color))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.teamchallengeslist, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return challengeData.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun endChallengeByManager(challengeId: String) {
        dialog!!.show()
        val map: HashMap<String, String> = hashMapOf()
        map["user_id"] = SharedPrefManager.getInstance(context!!)!!.user.id.toString()
        map["challenge_id"] = challengeId
        map["is_completed_by_manager"] = "1"
        map["updated_end_time"] = LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss",Locale.getDefault()))
        map["end_challenge_time"] = LocalTime.now().plusSeconds(1).format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss",Locale.getDefault()))
        println(map)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.endChallengeByManager(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("endChallengeByManager")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode: Int = obj.getInt("response_code")
                    if (resCode == 200) {
                        onRefresh.refresh()
                        Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show()
                    }
                }
                dialog!!.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialog!!.dismiss()
            }
        })
    }

}