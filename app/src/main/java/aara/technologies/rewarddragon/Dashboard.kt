package aara.technologies.rewarddragon

import aara.technologies.rewarddragon.App.token
import aara.technologies.rewarddragon.activities.MyLeaderboard
import aara.technologies.rewarddragon.activities.*
import aara.technologies.rewarddragon.databinding.ActivityDashboardBinding
import aara.technologies.rewarddragon.manager.*
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.testing.Repository.TestRepository
import aara.technologies.rewarddragon.testing.ViewModel.DashboardViewModel
import aara.technologies.rewarddragon.testing.ViewModelFactory.DashboardViewModelFactory
import aara.technologies.rewarddragon.utils.Constant
import aara.technologies.rewarddragon.utils.Constant.androidId
import aara.technologies.rewarddragon.utils.Constant.width
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.OnBonusDialogDismissInterface
import aara.technologies.rewarddragon.utils.SharedPrefManager
import aara.technologies.rewarddragon.utils.SharedPrefManager.Companion.KEY_COMPANY_IMAGE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONException
import org.json.JSONObject
import java.util.*


var dashboardBinding: ActivityDashboardBinding? = null

class Dashboard : AppCompatActivity(), View.OnClickListener {


    private var userType: String? = null
    private var userManagerPopupMenu: PopupWindow? = null
    private var managerPopupMenu: PopupWindow? = null
    private var view: View? = null
    var dialog: CustomLoader? = null
    var prefManager: SharedPrefManager? = null
    var barbudge: BadgeDrawable? = null
    var intentFilter: IntentFilter? = null
    var welcomeBonusStatus: Boolean = false


    lateinit var mainViewModel: DashboardViewModel

    companion object {

        lateinit var context: Context
        var bottomView: BottomNavigationView? = null
        var wellbeingView: View? = null

        @JvmField
        var clickAction: String? = null


    }

    private val budgeCountReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                "change.text.request" -> {
                    val text = intent.getStringExtra("changed_text")!!.toInt()
                    //  Log.i(TAG, "onReceive: $text")
                    if (text > 0) {
                        barbudge!!.number = text
                    } else {
                        barbudge!!.isVisible = false
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)


        if (androidId.isNullOrEmpty()) {
            androidId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        }



        bottomView = findViewById(R.id.bottom_nav);
        wellbeingView = findViewById(R.id.wellbeing)

        prefManager = SharedPrefManager.getInstance(this)


        token = prefManager?.user?.token.toString()

        barbudge = dashboardBinding!!.bottomNav.getOrCreateBadge(R.id.notification)
        barbudge!!.backgroundColor = getColor(R.color.red_budge)
        barbudge!!.badgeTextColor = getColor(R.color.white)

        intentFilter = IntentFilter()
        intentFilter!!.addAction("change.text.request")

        setContentView(dashboardBinding!!.root)
        context = this
        dialog = CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar)


//        Log.e("login data",email);
        userType =
            if (prefManager!!.user.roleId != null) prefManager!!.user.roleId.toString() else ""
        //  Log.e("login data", userType!!)
        if (savedInstanceState == null) {
            if (userType == Constant.user) {
                val fragment = MainActivity()
                addFragmentHome(fragment)
            } else if (userType == Constant.manager) {
                val fragment = HomePageManager()
                addFragmentHome(fragment)
            }
            callNotificationClickEvent()
        }

        dashboardBinding!!.toolbar.toolbarTitle.text =
            prefManager?.user?.companyName

        val dataServices = RetrofitInstance().getInstance().create(DataServices::class.java)
        val testRepository = TestRepository(dataServices)
        mainViewModel = ViewModelProvider(
            this,
            DashboardViewModelFactory(testRepository, context)
        )[DashboardViewModel::class.java]

        mainViewModel.notiCount.observe(this) {

            Log.i(TAG, "onCreate: " + it.unreadNotificationCount)

            if (it.responseCode == 200) {
                val notiCount = it.unreadNotificationCount
                if (notiCount > 0) {
                    barbudge!!.isVisible = true
                    barbudge!!.number = notiCount
                } else {
                    barbudge!!.isVisible = false
                }
            } else {
                barbudge!!.isVisible = false

            }

        }


        //    println("testttttttttttttttttttttt")
        //   println(prefManager?.getString(KEY_COMPANY_IMAGE))


        dashboardBinding?.toolbar?.companyLogo?.let {
            Glide.with(context)
                .load(prefManager?.getString(KEY_COMPANY_IMAGE))
                .into(it)
        }


        Constant.loginWelcomeObject?.let { showWelcomeBonusDialog(it) }

        if (welcomeBonusStatus) {
            Log.i(TAG, "welcomeBonusStatus: true")
            val handler = Handler()
            handler.postDelayed({
                Log.i(TAG, "Bonus data: ${Constant.loginRewardObject}")

                Constant.loginRewardObject?.let { showBonusDialog(it) }
            }, 5000)
        } else {
            Log.i(TAG, "welcomeBonusStatus: false")
            Constant.loginRewardObject?.let { showBonusDialog(it) }
        }


        var from_ = if (intent.getStringExtra("from") != null) intent.getStringExtra("from") else ""
        if (from_ == "MyLeaderboard") {
            val fragment = MyLeaderboard()
            addFragmentHome(fragment)
            dashboardBinding!!.bottomNav.selectedItemId = R.id.leaderboard
        } else if (from_ == "MyWellBeing") {
            val fragment = MyWellBeing()
            addFragmentHome(fragment)
            dashboardBinding!!.bottomNav.selectedItemId = R.id.wellbeing
        } else if (from_ == "my_leaderboard") {
            val fragment = MyLeaderboard()
            addFragmentHome(fragment)
            dashboardBinding!!.bottomNav.selectedItemId = R.id.leaderboard
        }

        dashboardBinding!!.bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    addFragmentHome(if (userType == Constant.user) MainActivity() else HomePageManager())
                    true
                }
                R.id.leaderboard -> {
                    addFragmentHome(MyLeaderboard())
                    true
                }
                R.id.wellbeing -> {
                    addFragmentHome(MyWellBeing())
                    true
                }
                R.id.notification -> {
                    addFragmentHome(MyNotifications())

                    true
                }
                R.id.account -> {
                    addFragmentHome(MyAccount())
                    true
                }
                else -> {
                    true
                }
            }
        }



//        Timer().schedule(object : TimerTask() {
//            override fun run() {
//                runOnUiThread {
//                    print("working \n")
////                    endChallengeCronJob()
////                    endCampaignCronJobApi()
////                    milestoneBonusPointCronJobApi()
//                }
//            }
//        }, 0, 1000)


    }



    override fun onWindowFocusChanged(hasFocus: Boolean) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus)
        width = dashboardBinding!!.parent.width
//        println("width sp")
//        println(width)

    }


    fun callNotificationClickEvent() {
        if (!clickAction.isNullOrEmpty()) {

            when (clickAction) {
                "my_leaderboard" -> {
                    val fragment = MyLeaderboard()
                    addFragmentHome(fragment)
                    dashboardBinding!!.bottomNav.selectedItemId = R.id.leaderboard
                }
                "my_josh" -> {
                    context.startActivity(Intent(context, MyJoshForToday::class.java))
                }
                "my_wellbeing" -> {
                    val fragment = MyWellBeing()
                    addFragmentHome(fragment)
                    dashboardBinding!!.bottomNav.selectedItemId = R.id.wellbeing
                }
                "game_for_today" -> {
                    context.startActivity(Intent(context, MyGameTime::class.java))
                }
                "my_challenges" -> {
                    context.startActivity(Intent(context, MyLatestChallenge::class.java))
                }
                "new_challenge" -> {
                    context.startActivity(
                        Intent(
                            context,
                            MyLatestChallenge::class.java
                        )
                    ) //tested
                }
                "my_resources" -> {
                    context.startActivity(Intent(context, MyResources::class.java))
                }
                "new_campaign" -> {
                    context.startActivity(Intent(context, MyCampaigns::class.java)) //tested
                }
                "home_page" -> {
                    context.startActivity(Intent(context, Dashboard::class.java))
                }
                "my_challenge" -> {
                    context.startActivity(Intent(context, MyLatestChallenge::class.java))
                }
            }


        }
    }


    @Throws(JSONException::class)
    private fun showBonusDialog(obj2: JSONObject) {
        Log.i(TAG, "bonus onResponse: $obj2")
        if (obj2.length() > 0) {
            val points = obj2.getString("reward_points")
            val message = obj2.getString("reward_message")

            if (points.toInt() > 0) {
                var alert = Constant.AlertDialog2(
                    this@Dashboard,
                    R.style.ThemeDialogCustom,
                    points,
                    message, listener = object : OnBonusDialogDismissInterface {
                        override fun onDismiss(boolean: Boolean) {
                            Constant.loginRewardObject = null
                        }
                    })
                alert.show()
            }

            Log.i(TAG, "onResponse: reward_points $points reward_message $message")

        } else {
            Log.i(TAG, "onResponse: reward_points null")

        }
    }


    @Throws(JSONException::class)
    private fun showWelcomeBonusDialog(obj2: JSONObject) {
        Log.i(TAG, "welcome onResponse: $obj2")

        if (obj2.length() > 0) {
            val points = obj2.getString("reward_points")
            val message = obj2.getString("reward_message")

            if (points.toInt() > 0) {
                welcomeBonusStatus = true
                var alert = Constant.AlertDialog2(
                    this@Dashboard,
                    R.style.ThemeDialogCustom,
                    points,
                    message, listener = object : OnBonusDialogDismissInterface {
                        override fun onDismiss(boolean: Boolean) {
                            Constant.loginWelcomeObject = null
                        }
                    })
                alert.show()
            }

            Log.i(TAG, "onResponse: reward_points $points reward_message $message")

        } else {
            Log.i(TAG, "onResponse: reward_points null")

        }
    }


    fun addFragment(fragment: Fragment?) {
        // Create the transaction
        val fts = supportFragmentManager.beginTransaction()
        // Replace the content of the container
        fts.replace(R.id.fragment_container_view, fragment!!)
        // Append this transaction to the backstack
        fts.addToBackStack("optional tag")
        // Commit the changes
        fts.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.i(TAG, "onActivityResult: reqCode $requestCode")

    }


    fun addFragmentHome(fragment: Fragment?) {
        //getNotificationBudge()
        // Create the transaction
        val fragmentManager = supportFragmentManager
        // this will clear the back stack and displays no animation on the screen
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fts = fragmentManager.beginTransaction()
        // Replace the content of the container
        fts.replace(R.id.fragment_container_view, fragment!!)
        // Append this transaction to the backstack
        //  fts.addToBackStack(null);
        // Commit the changes
        fts.commit()
    }

    override fun onResume() {
        super.onResume()
        this.registerReceiver(budgeCountReceiver, intentFilter);
    }

    override fun onStop() {
        super.onStop()

        this.unregisterReceiver(budgeCountReceiver);
    }

    private val TAG = "Dashboard"
/*
    private fun getNotificationBudge() {
        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.getNotificationCount(prefManager!!.user.id!!)
        Log.i(TAG, "req getNotificationBudge: " + Gson().toJson(call.request()))
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.i(TAG, "onResponse: ${response.body()}")
                if (response.code() == 200) {
                    try {
                        val jsonObject = JSONObject(Gson().toJson(response.body()))
                        val resCode = jsonObject.getInt("response_code")
                        if (resCode == 200) {
                            val notiCount = jsonObject.getInt("unread_notification_count")
                            if (notiCount > 0) {
                                barbudge!!.isVisible = true
                                barbudge!!.number = notiCount
                            } else {
                                barbudge!!.isVisible = false
                            }
                        } else {
                            barbudge!!.isVisible = false

                        }
                    } catch (e: Exception) {
                        barbudge!!.isVisible = false
                        Log.i(TAG, "onResponse: " + e.message)
                        e.printStackTrace()
                    }
                } else {
                    barbudge!!.isVisible = false
                }
//                dialog.dismiss()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
//                dialog.dismiss()
                barbudge!!.isVisible = false
                Log.i(TAG, "onFailure: " + t.message)
            }
        })
    }
*/


    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onClick(view: View) {
        when (view.id) {
            /*     R.id.home_iv -> if (userType == Constant.user) {
                     val fragment = MainActivity()
                     if (!fragment.isVisible) {
                         addFragmentHome(fragment)
                     }
                 } else if (userType == Constant.manager) {
                     val fragment = HomePageManager()
                     if (!fragment.isVisible) {
                         addFragmentHome(fragment)
                     }
                 }*/
            /*   R.id.menu -> if (userType == Constant.user) {
                   setPopUpWindow()
                   userManagerPopupMenu!!.showAsDropDown(view, -153, 0)
               } else if (userType == Constant.manager) {
                   managerPopUpWindow()
                   managerPopupMenu!!.showAsDropDown(view, -153, 0)
               }*/
        }
    }





    override fun onBackPressed() {
        //   super.onBackPressed();
        val fragmentManager = supportFragmentManager
        Log.i("backstack ", "" + fragmentManager.backStackEntryCount)
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {

            AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.app_name)
                .setMessage("Are you sure you want close app?")
                .setPositiveButton("Yes") { _, _ -> finishAffinity() }
                .setNegativeButton("No", null)
                .show()
        }
    }


}

object binding {

}
