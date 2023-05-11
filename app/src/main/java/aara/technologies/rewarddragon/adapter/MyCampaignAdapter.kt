package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.databinding.MyCampaignExpandViewBinding
import aara.technologies.rewarddragon.model.CampaignModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
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

class MyCampaignAdapter(var arrayList: ArrayList<CampaignModel>, var context: Context) :
    RecyclerView.Adapter<MyCampaignAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_campaign_adapter, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("InflateParams")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = arrayList[position]

        holder.campaignName.text = model.campaign_name

        holder.expand.setOnClickListener {
            openDialog(model)
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var campaignName: TextView
        var expand: ImageView

        init {
            campaignName = itemView.findViewById(R.id.campaign_name)
            expand = itemView.findViewById(R.id.expand)

        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun openDialog(model: CampaignModel) {
        val binding = MyCampaignExpandViewBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context).create()
        binding.campaignName.text = model.campaign_name
        binding.startDate.text = model.start_date
        binding.endDate.text = model.end_date
        binding.contactPerson.text = model.contact_person
        val layoutInflator =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        var score = 0
        for (item in model.kpi_data) {
            val view: View = layoutInflator.inflate(R.layout.linearlayout1, null)
            val text: TextView = view.findViewById(R.id.toolbarTitle)
            val point: TextView = view.findViewById(R.id.point)
            text.text = item.rule
            point.text = item.point.toString()
            binding.layout.addView(view)
//            score += item.point
        }
//        binding.overallScore.text = score.toString()
        binding.close.setOnClickListener { dialog.dismiss() }
        dialog.setView(binding.root)
        dialog.show()
    }


    private fun acceptCampaign(campaignId: String) {
        val map: HashMap<String, String> = HashMap()
        map["user_id"] = SharedPrefManager.getInstance(context)!!.user.id.toString()
        map["campaign_id"] = campaignId
        map["is_accepted"] = "1"
        println(map)
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = dataServices.acceptCampaign(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("acceptChallenge")
                println(response.body().toString())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.get("response_code")
                    Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("acceptChallenge t")
                println(t.message)
            }
        })
    }

    private fun completeCampaign(campaignId: String) {
        val map: HashMap<String, String> = HashMap()
        map["user_id"] = SharedPrefManager.getInstance(context)!!.user.id.toString()
        map["campaign_id"] = campaignId
        map["is_completed_by_customer"] = "1"
        println(map)
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = dataServices.completeCampaign(map)
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