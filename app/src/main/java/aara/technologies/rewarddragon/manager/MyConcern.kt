package aara.technologies.rewarddragon.manager

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.adapter.TabLayoutAdapter
import aara.technologies.rewarddragon.databinding.ActivityMyConcernBinding
import aara.technologies.rewarddragon.model.ConcernCategoryModel
import aara.technologies.rewarddragon.model.ConcernModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant.closeList
import aara.technologies.rewarddragon.utils.Constant.loadAvatarImage
import aara.technologies.rewarddragon.utils.Constant.openList
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class MyConcern : AppCompatActivity() {

    var binding: ActivityMyConcernBinding? = null
    var concernCategoryId: Int = -1
    var customLoader: CustomLoader? = null
    lateinit var context: Context
    var sharedPrefManager: SharedPrefManager? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyConcernBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        context = this

        sharedPrefManager = SharedPrefManager.getInstance(context)

        binding!!.toolbar.toolbarTitle.text =
            sharedPrefManager!!.user.companyName
        com.bumptech.glide.Glide.with(context).load(
            sharedPrefManager!!
                .getString(SharedPrefManager.KEY_COMPANY_IMAGE)!!
        ).into(binding!!.toolbar.companyLogo)
        binding!!.toolbar.back.setOnClickListener { finish() }

        binding!!.userNameTxt.text =
            sharedPrefManager!!.user.firstName + " " + SharedPrefManager.getInstance(
                context
            )!!.user.lastName
        binding!!.designation.text = sharedPrefManager!!.user.designation

        customLoader = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)

        binding!!.tabLayout.addTab(binding!!.tabLayout.newTab().setText("Open Concern"))
        binding!!.tabLayout.addTab(binding!!.tabLayout.newTab().setText("Closed Concern"))
        binding!!.tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        binding!!.viewPager.addOnPageChangeListener(
            TabLayoutOnPageChangeListener(
                binding!!.tabLayout
            )
        )

        binding!!.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding!!.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


        binding!!.raiseConcern.setOnClickListener {
            dialogForRaiseConcern()
        }
        getConcernList()


        loadAvatarImage(context, binding!!.imageView)
    }

    var dialog: Dialog? = null

    private fun dialogForRaiseConcern() {


        dialog = Dialog(context, R.style.Theme_Dialog)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.raise_concern_layout)
        val spinner = dialog?.findViewById(R.id.spinner) as Spinner
        val description = dialog?.findViewById(R.id.description) as TextInputEditText
        val submit = dialog?.findViewById(R.id.submit) as TextView
        getConcernCategoryList(spinner)
        submit.setOnClickListener {
            if (spinner.selectedItemPosition == 0) {
                Toast.makeText(context, "Select Concern Category", Toast.LENGTH_LONG)
                    .show()
            } else if (description.text.toString().isEmpty()) {
                description.requestFocus()
                description.error = "Required"
            } else {
                submitConcern(description.text.toString())
            }
        }

        dialog?.show()

    }

    private fun getConcernCategoryList(spinner: Spinner) {
        customLoader!!.show()
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val result = dataServices.getConcernCategoryList()
        result.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getConcernCategoryList")
                println(response.body())
                try {


                    val jsonArray =
                        JSONArray(Gson().toJson(response.body()?.getAsJsonArray("data")))
                    val turnsType = object : TypeToken<List<ConcernCategoryModel>>() {}.type
                    val list: ArrayList<ConcernCategoryModel> =
                        Gson().fromJson(jsonArray.toString(), turnsType)
                    val items: ArrayList<String> = ArrayList()
                    items.add("Select Concern")
                    for (item in list) {
                        println(item.name)
                        items.add(item.name)
                    }
                    list.add(0, ConcernCategoryModel(0, "", 0))
                    val adapter = ArrayAdapter(
                        context,
                        R.layout.simple_spinner_item, items
                    ) //setting the country_array to spinner
                    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item2)
                    spinner.adapter = adapter
                    spinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                concernCategoryId = list[position].id
                                println("concernCategoryId")
                                println(concernCategoryId)
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {
                            }
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                customLoader!!.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
                customLoader!!.dismiss()
            }
        })
    }

    fun showDialog1(ticket: String) {
        AlertDialog.Builder(context)
            .setIcon(R.mipmap.logo)
            .setTitle("Submitted")
            .setMessage(Html.fromHtml("Thanks for raising the concern, your ticket is  <u><b>$ticket</b> </u> \nYour Concern will be shared with HR/App Admin and Leadership as the case may be."))
            .setCancelable(false)
            .setPositiveButton("OK") { p0, _ ->
                p0?.dismiss()
                getConcernList()
            }
            .show()
    }

    private fun submitConcern(description: String) {
        customLoader!!.show()
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["user_profile"] =
            sharedPrefManager!!.user.id.toString()
        hashMap["concern_category"] = concernCategoryId.toString()
        hashMap["description"] = description
        println("submitConcern")
        println(hashMap)
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val result = dataServices.submitConcern(hashMap)
        result.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    dialog?.dismiss()
                    showDialog1(obj.getString("ticket"))
                } else {
                    Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG)
                        .show()
                }
                customLoader!!.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
                customLoader!!.dismiss()
            }
        })
    }

    private val TAG = "MyConcern"
    private fun getConcernList() {
        openList.clear()
        closeList.clear()
        customLoader!!.show()
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val result =
            dataServices.getConcernList(sharedPrefManager!!.user.id.toString())
        Log.i(TAG, "getConcernList: ${Gson().toJson(result.request())}")
        result.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getConcernList")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val jsonArray =
                        JSONArray(Gson().toJson(response.body()?.getAsJsonArray("data")))
                    for (i in 0 until jsonArray.length()) {
                        val obj: JSONObject = jsonArray[i] as JSONObject
                        if (obj.getInt("status") == 1) {
                            openList.add(Gson().fromJson(obj.toString(), ConcernModel::class.java))
                        } else {
                            closeList.add(Gson().fromJson(obj.toString(), ConcernModel::class.java))
                        }
                        binding!!.viewPager.adapter = TabLayoutAdapter(supportFragmentManager)
                    }
                }
                customLoader!!.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                customLoader!!.dismiss()
            }
        })
    }
}