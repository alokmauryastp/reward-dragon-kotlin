package aara.technologies.rewarddragon.manager

import aara.technologies.rewarddragon.activities.WebViewVertActivity
import aara.technologies.rewarddragon.adapter.LeaderShipTalkAdapter
import aara.technologies.rewarddragon.adapter.LearningMaterialAdapter
import aara.technologies.rewarddragon.adapter.OtherLinkAdapter
import aara.technologies.rewarddragon.databinding.ActivityMyResourcesBinding
import aara.technologies.rewarddragon.model.LeadershipTalkModel
import aara.technologies.rewarddragon.model.LearningMaterialModel
import aara.technologies.rewarddragon.model.OtherLinkModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant
import aara.technologies.rewarddragon.utils.Constant.loadAvatarImage
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyResources : AppCompatActivity() {
    var binding: ActivityMyResourcesBinding? = null
    var dialog: CustomLoader? = null
    lateinit var context: Context
    var sharedPrefManager: SharedPrefManager? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyResourcesBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        context = this

        sharedPrefManager = SharedPrefManager(this)

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


        binding!!.termOfServices.setOnClickListener {
            termAndCondition()
        }

        binding!!.privacyPolicy.setOnClickListener {
            privacyPolicy()
        }

        binding!!.recyclerOtherLink.layoutManager =
            GridLayoutManager(context, 4, RecyclerView.VERTICAL, false)
        binding!!.recyclerLearningMaterial.layoutManager =
            GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)

        binding!!.recyclerLeadershipTalk.layoutManager =
            GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)

        getCompanySiteListData()
        getLearningMaterialListData()
        getLeadershipTalkListData()
        getOtherLinkListData()

        loadAvatarImage(context, binding!!.imageView)

    }

    private fun termAndCondition() {
        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.getTermAndCondition()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {


                Log.i(TAG, "onResponse: ${response.body()}")

                if (response.code() == 200) {

                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")

                        if (resCode == 200) {

                            var array = jsonObject.getJSONArray("terms_conditions_data")


                            if (array.length() > 0) {

                                for (i in 0 until array.length()) {
                                    val item = array.getJSONObject(i)
                                    Log.i(
                                        TAG,
                                        "onResponse: termcondition " + item
                                            .getString("url_link")
                                    )

                                    startActivity(
                                        Intent(context, WebViewVertActivity::class.java).putExtra(
                                            "link",
                                            item.getString("url_link")
                                        )
                                    )
                                }

                            }


                        } else {
                            Toast.makeText(
                                context,
                                jsonObject.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    } catch (e: Exception) {

                        Log.i(TAG, "onResponse:error ${e.message}")


                    }
                }

            }


            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                TODO("Not yet implemented")
            }


        })

    }

    private fun privacyPolicy() {
        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.getPrivacyPolicy()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {


                Log.i(TAG, "onResponse: ${response.body()}")

                if (response.code() == 200) {

                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")

                        if (resCode == 200) {

                            var array = jsonObject.getJSONArray("privacy_policy_data")


                            if (array.length() > 0) {

                                for (i in 0 until array.length()) {
                                    val item = array.getJSONObject(i)
                                    Log.i(
                                        TAG,
                                        "onResponse: termcondition " + item
                                            .getString("url_link")
                                    )

                                    startActivity(
                                        Intent(context, WebViewVertActivity::class.java).putExtra(
                                            "link",
                                            item.getString("url_link")
                                        )
                                    )
                                }

                            }


                        } else {
                            Toast.makeText(
                                context,
                                jsonObject.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    } catch (e: Exception) {

                        Log.i(TAG, "onResponse:error ${e.message}")


                    }
                }

            }


            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                TODO("Not yet implemented")
            }


        })

    }


    //Company portal / Intranet
    private fun getCompanySiteListData() {
        dialog?.show()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getCompanySiteListData()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getCompanySiteListData")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        val data = obj.getJSONObject("company_site_data")

                        binding!!.clickToExplore.setOnClickListener {
                            startActivity(
                                Intent(context, WebViewVertActivity::class.java).putExtra(
                                    "link",
                                    data.getString("site_url")
                                )
                            )
//                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data.getString("site_url")))
//                            startActivity(browserIntent)
                        }

//                        Glide
//                            .with(context)
//                            .load(data.getString("image_data"))
//                            .placeholder(R.mipmap.logo)
//                            .apply(RequestOptions().override(ViewGroup.LayoutParams.MATCH_PARENT, 400))
//                            .fitCenter()
//                            .into(binding!!.image)

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

    private val TAG = "MyResources"

    // Learning Material Data
    private fun getLearningMaterialListData() {
        dialog?.show()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = sharedPrefManager?.user?.uniqueCode?.let {
            /*val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("unique_code", it)
                .build()*/
            var map: HashMap<String, String>? = hashMapOf()
            map?.put("unique_code", it)


            services.getLearningMaterialListData(it)
        }
        Log.i(TAG, "getLearningMaterialListData: ${Gson().toJson(call!!.request())}")
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                //    println("getLearningMaterialListData")
                println(response.code())
                println("getLearningMaterialListData: \t " + response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        val turnsType =
                            object : TypeToken<ArrayList<LearningMaterialModel>>() {}.type
                        val list: ArrayList<LearningMaterialModel> = Gson().fromJson(
                            obj.getJSONArray("learning_material").toString(),
                            turnsType
                        )
                        println(list.size)
                        binding!!.recyclerLearningMaterial.adapter =
                            LearningMaterialAdapter(list, context)
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


    private fun getLeadershipTalkListData() {
        dialog?.show()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        var map: HashMap<String, String>? = hashMapOf()
        map?.put("unique_code", sharedPrefManager?.user?.uniqueCode!!)
        val call = services.getGetLeadershipTalkListData(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                // println("getLeadershipTalkListData")
                println(response.code())
                println("getLeadershipTalkListData:\t " + response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        val turnsType =
                            object : TypeToken<ArrayList<LeadershipTalkModel>>() {}.type
                        val list: ArrayList<LeadershipTalkModel> = Gson().fromJson(
                            obj.getJSONArray("leader_ship_talk_list").toString(),
                            turnsType
                        )
                        println(list.size)
                        var adapter = LeaderShipTalkAdapter(list, context)
                        binding!!.recyclerLeadershipTalk.adapter = adapter
                        adapter.notifyDataSetChanged()
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


    //Other Links Data
    private fun getOtherLinkListData() {
        dialog?.show()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getOtherLinkListData()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getOtherLinkListData")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    if (resCode == 200) {
                        val turnsType = object : TypeToken<ArrayList<OtherLinkModel>>() {}.type
                        val list: ArrayList<OtherLinkModel> = Gson().fromJson(
                            obj.getJSONArray("other_link_data").toString(),
                            turnsType
                        )
                        println(list.size)
                        binding!!.recyclerOtherLink.adapter =
                            OtherLinkAdapter(list, context)
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

}