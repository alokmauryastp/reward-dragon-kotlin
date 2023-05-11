package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.model.CampaignData
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.OnRefresh
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import java.time.LocalDate


class TeamCampaignAdapter(
    private val campaignData: List<CampaignData>,
    private val context: Context?,
    private val listener: ClickListener,
    private val refresh: OnRefresh
) : RecyclerView.Adapter<TeamCampaignAdapter.ViewHolder>() {


    interface ClickListener {
        fun onClickListener( id: Int, i1: Int)
    }

    var dialog: CustomLoader? = null
    var onRefresh: OnRefresh? = null

    init {
        onRefresh = refresh

        dialog = CustomLoader(context,android.R.style.Theme_Translucent_NoTitleBar)
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val start_date: TextView = itemView.findViewById(R.id.start_date)
        val enddate: TextView = itemView.findViewById(R.id.enddate)


        val action_btn: MaterialButton = itemView.findViewById(R.id.action_btn)
        val parent: LinearLayout = itemView.findViewById(R.id.parentll)
        val exapand_btn: ImageView = itemView.findViewById(R.id.expand_btn)
        val collapse_btn: ImageView = ItemView.findViewById(R.id.collapse_btn)

        val headChallengeNameTv: TextView = itemView.findViewById(R.id.challenge_name_head)

        val layout: LinearLayout = itemView.findViewById(R.id.layout)




    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val campaignData = campaignData[position]

        holder.parent.visibility = View.GONE
        holder.start_date.text = campaignData.start_date

        holder.enddate.text = campaignData.end_date
        holder.headChallengeNameTv.text = campaignData.campaign_name


//        val arrayAdapter = KpiArrayAdapterTest(context!!, campaignData.kpi_data)

        val layoutInflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        for(item in campaignData.kpi_data){
            val view: View = layoutInflator.inflate(R.layout.custom_kpi_list, null)
            val text : TextView = view.findViewById(R.id.rulename)
            val point : TextView = view.findViewById(R.id.rulepoint)
            text.text = item.rule
            point.text = item.point.toString()
            holder.layout.addView(view)
        }


        holder.exapand_btn.setOnClickListener {

            holder.parent.visibility = View.VISIBLE
            holder.exapand_btn.visibility = View.GONE
            holder.collapse_btn.visibility = View.VISIBLE
            holder.parent.visibility = View.VISIBLE
            holder.exapand_btn.visibility = View.GONE
            holder.collapse_btn.visibility = View.VISIBLE
        }

        holder.collapse_btn.setOnClickListener {
            holder.parent.visibility = View.GONE
            holder.exapand_btn.visibility = View.VISIBLE
            holder.collapse_btn.visibility = View.GONE
            holder.parent.visibility = View.GONE
            holder.exapand_btn.visibility = View.VISIBLE
            holder.collapse_btn.visibility = View.GONE
        }
        if (campaignData.is_broadcast == 0) {
            holder.action_btn.text = "Broadcast"
            holder.action_btn.changeColor(R.color.green)

        } else if (campaignData.is_broadcast == 1) {
            holder.action_btn.text = "End Campaign"
            holder.action_btn.changeColor(R.color.red)
        }

        if (campaignData.is_completed_by_manager == 1) {
            holder.action_btn.text = "Ended"
            holder.action_btn.changeColor(R.color.light_gray)
        }

        holder.action_btn.setOnClickListener {
            if (campaignData.is_broadcast == 0) {
                listener.onClickListener( campaignData.id, 1)
                // sendBroadcastStatus(Constant.managerUserId, campaignData.id!!, 1)
            } else if (holder.action_btn.text.toString() == "End Campaign") {
                endCampaignByManager(campaignData.id.toString())
            }

        }
    }

    private fun MaterialButton.changeColor(color: Int) {
        this.background.setTintList(ContextCompat.getColorStateList(context, color))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.teamcampaignlist, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return campaignData.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun endCampaignByManager(campaignId: String) {

        dialog!!.show()
        val map: HashMap<String, Any> = hashMapOf()
        map["user_id"] = SharedPrefManager.getInstance(context!!)!!.user.id.toString()
        map["campaign_id"] = campaignId
        map["is_completed_by_manager"] = 1
        map["updated_end_date"] = LocalDate.now().toString()
        println(map)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.endCampaignByManager(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("endCampaignByManager")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode: Int = obj.getInt("response_code")
                    if (resCode == 200) {
                        refresh.refresh()
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


