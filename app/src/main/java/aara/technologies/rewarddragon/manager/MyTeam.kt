package aara.technologies.rewarddragon.manager

import aara.technologies.rewarddragon.adapter.MyTeamAdapter
import aara.technologies.rewarddragon.databinding.ActivityMyTeamBinding
import aara.technologies.rewarddragon.model.TeamsDataModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyTeam : AppCompatActivity() {
    var binding: ActivityMyTeamBinding? = null
    var adapter: MyTeamAdapter? = null
    var dialog: CustomLoader? = null
    lateinit var context: Context

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyTeamBinding.inflate(layoutInflater)
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

        binding!!.searchHere.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter?.filter(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        getTeamData()
    }

    private val TAG = "MyTeam"
    private fun getTeamData() {
        dialog?.show()
        val map: HashMap<String, String> = hashMapOf()
        map["manager_id"] = SharedPrefManager.getInstance(context)!!.user.id.toString()
        map["team_id"] = SharedPrefManager.getInstance(context)!!.user.teamId.toString()
        map["unique_code"] = SharedPrefManager.getInstance(context)!!.user.uniqueCode.toString()
        println(map)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getTeamData(map)
        call.enqueue(object : Callback<JsonObject> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getTeamData")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {

                    try {
                        val obj =
                            JSONObject(
                                Gson().toJson(
                                    response.body()?.getAsJsonObject("teams_data")
                                )
                            )
                        binding!!.companyName.text = obj.getString("organization_name")
                        var address: Boolean = obj.optBoolean("address")

                       /* binding!!.baseLocation.text =
                            (if (address) obj.getString("address") else "")*/
                        binding!!.teamName.text = obj.getString("team_name")
                        binding!!.managerName.text = obj.getString("manager_name")
                        val array = obj.getJSONArray("teams_data")

                        if (array.length() > 0) {
                            val turnsType = object : TypeToken<ArrayList<TeamsDataModel>>() {}.type
                            val list: ArrayList<TeamsDataModel> =
                                Gson().fromJson(array.toString(), turnsType)
                            println(list.size)
                            binding!!.teamMember.text = "Members(${list.size})"
                            binding!!.recyclerView.layoutManager = LinearLayoutManager(context)
                            adapter = MyTeamAdapter(list, context)
                            binding!!.recyclerView.adapter = adapter
                        }
                    } catch (ex: JSONException) {
                        Log.i(TAG, "onResponse: ${ex.message}")
                    }


                }
                dialog?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
                dialog?.dismiss()
            }
        })
    }


}