package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.adapter.KPIAdapter
import aara.technologies.rewarddragon.databinding.FragmentTeamCampaignBinding
import aara.technologies.rewarddragon.model.IndustryModel
import aara.technologies.rewarddragon.model.KpiModel
import aara.technologies.rewarddragon.model.PurposeModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.OnRefresh
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class TeamCampaignBottomSheet(refresh: OnRefresh) : BottomSheetDialogFragment() {

    var binding: FragmentTeamCampaignBinding? = null

    var campaignPurposeId: Int = -1
    var industryId: Int? = null
    var customLoader: CustomLoader? = null
    var list: ArrayList<KpiModel> = arrayListOf()
    var onRefresh: OnRefresh? = null
    var sharedPrefManager: SharedPrefManager? = null

    init {
        onRefresh = refresh
    }


    private val tAG = "TeamCampaignBottomSheet"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentTeamCampaignBinding.inflate(
            layoutInflater
        )

        sharedPrefManager = SharedPrefManager.getInstance(requireContext())

        customLoader = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)

        Log.i(tAG, "onCreateView: ")
        getCampaignPurpose()
        getIndustryListData()

        return binding!!.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(tAG, "onViewCreated: ")

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        binding!!.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding!!.startDate.setOnClickListener {
            val dpd = DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                binding!!.startDate.text =
                    year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth

            }, year, month, day)
            dpd.show()
        }

        binding!!.endDate.setOnClickListener {
            val dpd = DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                binding!!.endDate.text =
                    year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                0
            }, year, month, day)
            dpd.show()
        }

        binding!!.createBtn.setOnClickListener {

            var selected = 0
            val ruleList: MutableList<HashMap<String, String>> = mutableListOf()
            for (i in 0 until list.size) {
                val item = list[i]
                if (item.isChecked) {
                    selected++
                    if (item.point == null || item.point.isEmpty()) {
                        binding!!.recyclerView[i].findViewById<EditText>(R.id.point).error = "Required"
                        binding!!.recyclerView[i].findViewById<EditText>(R.id.point).requestFocus()
                    } else {
                        val map: HashMap<String, String> = hashMapOf()
                        map["kpi_id"] = item.id.toString()
                        map["rule"] = item.name
                        map["point"] = item.point
                        ruleList.add(map)
                    }
                }
            }

            if (binding!!.campaignName.text.toString().isEmpty()) {
                binding!!.campaignName.error = "Enter Campaign Name"
            } else if (binding!!.startDate.text.toString().isNullOrEmpty()) {
                binding!!.startDate.error = "Select Start Date"
            } else if (binding!!.endDate.text.toString().isNullOrEmpty()) {
                binding!!.endDate.error = "Select End Date"
            } else if (selected == 0) {
                Toast.makeText(
                    requireContext(),
                    "Please Choose at least one checkbox",
                    Toast.LENGTH_LONG
                ).show()

            } else if (!timeCompare()) {
                Toast.makeText(
                    context,
                    "Start Date is not less then Current Date and End Date is not less then Start Date !",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                //  println("ruleList.toString()")
                // println(selected)
                //  println(ruleList.toString())
                createCampaign(ruleList)
            }
        }

    }

    private val TAG = "TeamCampaignBottomSheet"

    private fun timeCompare(): Boolean {

        val pattern = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(pattern)
        val currentTime = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        try {
            val date1 = sdf.parse(binding?.startDate?.text?.trim().toString())
            var date2 = sdf.parse(binding?.endDate?.text?.trim().toString())
            var currentTime = sdf.parse(currentTime)
            // Outputs -1 as date1 is before date2
            if (date1 != null) {
                return if (date1 >= currentTime) {
                    date2.after(date1);
                } else {
                    false
                }
            }
        } catch (e: ParseException) {
            Log.i(TAG, "timeCompare: ${e.printStackTrace()}")
        }
        return false;

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
                println(response.body())
                try {


                    val jsonArray =
                        JSONArray(
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
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
            }
        })
    }

    private fun getKpiListData(industryId: String) {
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val result = dataServices.getKpiListData(industryId, "Campaign")
        result.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getKpiListData")
                println(response.body())
                try {
                    val jsonArray =
                        JSONArray(Gson().toJson(response.body()?.getAsJsonArray("kpi_name_data")))
                    val turnsType = object : TypeToken<List<KpiModel>>() {}.type
                    list = Gson().fromJson(jsonArray.toString(), turnsType)
                    binding!!.recyclerView.adapter = KPIAdapter(list, context!!)

                } catch (e: Exception) {
                    Log.i(TAG, "onResponse: error ${e.message}")
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
            }
        })
    }

    private fun getCampaignPurpose() {
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val result = dataServices.getCampaignPurposeList()
        result.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                //  println("getCampaignPurpose")
                try {


                    val jsonArray =
                        JSONArray(
                            Gson().toJson(
                                response.body()?.getAsJsonArray("campaign_purpose")
                            )
                        )
                    val turnsType = object : TypeToken<List<PurposeModel>>() {}.type
                    val list: ArrayList<PurposeModel> =
                        Gson().fromJson(jsonArray.toString(), turnsType)
                    val items: ArrayList<String> = ArrayList()
//                items.add("Select")
                    for (item in list) {
                        println(item.purpose_name)
                        items.add(item.purpose_name)
                    }

                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.simple_spinner_item, items
                    ) //setting the country_array to spinner
                    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item2)
                    binding!!.spinner.adapter = adapter
                    binding!!.spinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                p0: AdapterView<*>?,
                                p1: View?,
                                p2: Int,
                                p3: Long
                            ) {
                                campaignPurposeId = list[p2].id
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {
                            }
                        }

                } catch (e: Exception) {
                    Log.i(TAG, "onResponse: ${e.message}")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
            }
        })
    }

    private fun createCampaign(ruleList: MutableList<HashMap<String, String>>) {
        customLoader!!.show()
        val map: HashMap<String, Any> = hashMapOf()
        map["user_id"] = sharedPrefManager!!.user.id.toString()
        map["campaign_purpose_id"] = campaignPurposeId
        map["campaign_name"] = binding!!.campaignName.text.toString()
        map["start_date"] = binding!!.startDate.text.toString()
        map["end_date"] = binding!!.endDate.text.toString()
        map["industry_work_type_id"] = industryId.toString()
        map["broadcast_id"] = 1
        map["criteria_point"] = ruleList
        println(map)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.createCampaign(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                /*    println("createCampaign")
                    println(response.code())
                    println(response.body())
                    println(Gson().toJson(response.errorBody()))*/
                if (response.code() == 200) {

                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode: Int = obj.getInt("response_code")

                    if (resCode == 200) {
                        // Log.i(TAG, "onResponse:campaign_id ${obj.getString("campaign_id")}")
                        sendToWhatsapp(obj.getString("campaign_id"))
                        AlertDialog.Builder(requireContext())
                            .setIcon(R.drawable.ic_baseline_check_circle_24)
                            .setTitle("Success!")
                            .setMessage(obj.getString("message"))
                            .setPositiveButton("OK") { _, _ ->

                            }
//                        .setNegativeButton("Not Now", null)
                            .show()
                        onRefresh?.refresh()
                        dismiss()
                    } else {
                        Toast.makeText(requireContext(),obj.getString("message"),Toast.LENGTH_LONG).show()
                    }
                }
                customLoader!!.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
                customLoader!!.dismiss()
            }
        })
    }


    private fun sendToWhatsapp(challenge_id: String) {
        val map: HashMap<String, Any> = hashMapOf()
        map["user_id"] = sharedPrefManager!!.user.id.toString()
        map["campaign_id"] = challenge_id
        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.sendCampaignToWhatsapp(map)
        Log.i(TAG, "sendToWhatsapp: ${Gson().toJson(call.request())}")
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.e("$TAG sendToWhatsapp", response.body().toString())

            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Log.e(TAG, " sendToWhatsapp ${t.message}")

            }
        })
    }

}









