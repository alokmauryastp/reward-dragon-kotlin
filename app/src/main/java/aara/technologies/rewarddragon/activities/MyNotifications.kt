package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.Dashboard
import aara.technologies.rewarddragon.adapter.NotificationAdapter
import aara.technologies.rewarddragon.databinding.ActivityMyNotificationsBinding
import aara.technologies.rewarddragon.model.NotificationModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.InternetCheck
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyNotifications : Fragment() {

    var binding: ActivityMyNotificationsBinding? = null
    var selectedList: MutableList<String> = mutableListOf()
    var customLoader: CustomLoader? = null
    var list: List<NotificationModel> = ArrayList()

    var sendbudgeIntent: Intent? = null
    var index: Int = 1
    var limit = 1

    var sharedPrefManager: SharedPrefManager? = null


    override fun onStart() {
        super.onStart()
        sendbudgeIntent = Intent()
        sendbudgeIntent?.action = "change.text.request"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityMyNotificationsBinding.inflate(layoutInflater)

        customLoader = CustomLoader(context, android.R.style.Theme_Translucent_NoTitleBar)
        sharedPrefManager = SharedPrefManager.getInstance(requireContext())

        Log.i(TAG, "onCreateView: working")
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding!!.recyclerview.layoutManager = layoutManager


        binding!!.nestedScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            // on scroll change we are checking when users scroll as bottom.
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                // in this method we are incrementing page number,
                // making progress bar visible and calling get data method.
                index++
                customLoader?.show()
                getNotification(index)
            }
        })

        if (InternetCheck().isNetworkAvailable(requireContext())) {
            customLoader?.show()
            getNotification(index)
        }

        binding!!.remove.setOnClickListener {
            selectedList.clear()
            for (item in list) {
                if (item.is_selected) {
                    selectedList.add(item.id.toString())
                }
            }
            /*  println("remove")
              println(selectedList.toString())*/
            deleteNotification()
        }

        binding!!.markRead.setOnClickListener {
            selectedList.clear()
            for (item in list) {
                if (item.is_selected) {
                    selectedList.add(item.id.toString())
                }
            }
            /*  println("markRead")
              println(selectedList.toString())*/
            markRead()
        }

        binding!!.markUnread.setOnClickListener {
            selectedList.clear()
            for (item in list) {
                if (item.is_selected) {
                    selectedList.add(item.id.toString())
                }
            }
            /*    println("markUnread")
                println(selectedList.toString())*/
            markUnRead()
        }

    }

    private fun markRead() {
        customLoader?.show()
        val map: HashMap<String, Any> = HashMap()
        map["user_id"] = sharedPrefManager!!.user.id.toString()
        map["notification_ids"] = selectedList
        println(map)
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = dataServices.markReadNotification(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println(response.code())
                println(response.body())
                customLoader?.dismiss()

                Log.i(TAG, "onResponse: markRead " + response.code())

                //
                if (response.code() == 200) {
                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {
                            val notiCount = jsonObject.getInt("unread_notification_count")
                            sendbudgeIntent?.putExtra("changed_text", notiCount.toString());
                            context!!.sendBroadcast(sendbudgeIntent);
                            // Log.i(TAG, "onResponse:  unread "+jsonObject.getInt("unread_notification_count"))

                        }
                    } catch (e: Exception) {
                        Log.i(TAG, "onResponse: " + e.message)
                        e.printStackTrace()
                    }
                } else {
                    Log.i(TAG, "onResponse: " + response.code())
                }
                //
                customLoader?.show()
                limit = 1
                index = 1
                if (!list.isNullOrEmpty()) {
                    list = emptyList()
                }
                getNotification(index)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
                customLoader?.dismiss()
            }
        })
    }

    private val TAG = "MyNotifications"

    private fun markUnRead() {
        customLoader?.show()
        val map: HashMap<String, Any> = HashMap()
        map["user_id"] = sharedPrefManager!!.user.id.toString()
        map["notification_ids"] = selectedList
        println(map)
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = dataServices.markUnReadNotification(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println(response.code())
                Log.i(TAG, "onResponse: markUnRead " + response.body())
                customLoader?.dismiss()
                if (response.code() == 200) {
                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {
                            val notiCount = jsonObject.getInt("unread_notification_count")
                            sendbudgeIntent?.putExtra("changed_text", notiCount.toString());
                            context!!.sendBroadcast(sendbudgeIntent);
                            // Log.i(TAG, "onResponse:  unread "+jsonObject.getInt("unread_notification_count"))

                        }
                    } catch (e: Exception) {
                        Log.i(TAG, "onResponse: " + e.message)
                        e.printStackTrace()
                    }
                }
                limit = 1
                index = 1
                if (!list.isNullOrEmpty()) {
                    list = emptyList()
                }
                getNotification(index)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
                customLoader?.dismiss()
            }
        })
    }

    private fun deleteNotification() {
        customLoader?.show()
        val map: HashMap<String, Any> = HashMap()
        map["user_id"] = sharedPrefManager!!.user.id.toString()
        map["notification_ids"] = selectedList
        println(map)
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = dataServices.deleteNotification(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.i(TAG, "onResponse: deleteNotification " + response.body())

                println(response.code())
                println(response.body())
                customLoader?.dismiss()
                if (response.code() == 200) {
                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {
                            val notiCount = jsonObject.getInt("unread_notification_count")
                            sendbudgeIntent?.putExtra("changed_text", notiCount.toString());
                            context!!.sendBroadcast(sendbudgeIntent);
                            // Log.i(TAG, "onResponse:  unread "+jsonObject.getInt("unread_notification_count"))

                        }
                    } catch (e: Exception) {
                        Log.i(TAG, "onResponse: " + e.message)
                        e.printStackTrace()
                    }
                }
                customLoader?.show()
                limit = 1
                index = 1
                if (!list.isNullOrEmpty()) {
                    list = emptyList()
                }
                getNotification(index)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
                customLoader?.dismiss()
            }
        })
    }

    private fun getNotification(page: Int) {

        if (page > limit) {
            // checking if the page number is greater than limit.
            // displaying toast message in this case when page>limit.
            Toast.makeText(context, "That's all the data..", Toast.LENGTH_SHORT).show()
            // hiding our progress bar.
            customLoader!!.dismiss()
            return
        }

        val map: HashMap<String, String> = HashMap()
        map["user_id"] = sharedPrefManager!!.user.id.toString()
        map["page"] = page.toString()
        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = dataServices.getNotification(map)
        Log.i(TAG, "getNotification: ${Gson().toJson(call.request())}")
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.i(
                    TAG,
                    "onResponse: getNotification code: " + response.code() + " msg: " + response.message() + " data:  " + response.body()
                )
                // println(response.body().toString())
                customLoader?.dismiss()
                if (response.code() == 200) {
                    val jsonObject = JSONObject(Gson().toJson(response.body()))

                    val resCode = jsonObject.getInt("response_code")
                    if (resCode == 200) {
                        if (!jsonObject.isNull("total_no_pages")) {
                            limit = jsonObject.getInt("total_no_pages")
                        }
                        val notiCount = jsonObject.getInt("unread_notification_count")
                        sendbudgeIntent?.putExtra("changed_text", notiCount.toString());
                        Dashboard.context.sendBroadcast(sendbudgeIntent);

                        list = Gson().fromJson(
                            jsonObject.getJSONArray("notifications").toString(),
                            Array<NotificationModel>::class.java
                        ).toList()
                        if (list.isNullOrEmpty()) {
                            binding!!.notFound.visibility = View.VISIBLE
                            binding!!.recyclerview.visibility = View.GONE
                        } else {


                            val adapter = NotificationAdapter(list, Dashboard.context)
                            binding!!.recyclerview.adapter = adapter
                            binding!!.selectAll.setOnClickListener {
                                for (item in list) {
                                    item.is_selected = true
                                }
                                adapter.notifyDataSetChanged()
                            }

                            binding!!.notFound.visibility = View.GONE
                            binding!!.recyclerview.visibility = View.VISIBLE
                        }
                    }

                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.i("NotificationRes", t.message.toString())
                customLoader?.dismiss()
            }
        })
    }
}