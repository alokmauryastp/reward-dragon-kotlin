package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.Dashboard
import aara.technologies.rewarddragon.databinding.ActivitySplashBinding
import aara.technologies.rewarddragon.utils.Constant
import aara.technologies.rewarddragon.utils.Constant.androidId
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings.Secure
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    var binding: ActivitySplashBinding? = null
    var tag = "Splash"
    var sharedPrefManager: SharedPrefManager? = null

    @SuppressLint("SimpleDateFormat", "HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        sharedPrefManager = SharedPrefManager.getInstance(this)
        val token = sharedPrefManager!!.user.token.toString()
        Log.i(TAG, ":onCreate token $token")

/*
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ssSSS")
        val currentDate = sdf.format(Date())
        println(" C DATE is  $currentDate")*/
        androidId = Secure.getString(this.contentResolver, Secure.ANDROID_ID)


     //   Log.d("Android", "Android ID : $androidId")

        Handler(Looper.myLooper()!!).postDelayed({
            checkUserLogin()
        }, 3000)


     /*   if (intent.extras != null) {
            for (key in intent.extras!!.keySet()) {
                val value = intent.extras!!.getString(key)
                Log.d(TAG, "Key: $key Value: $value")
            }
        }
*/

    }




    private val TAG = "SplashActivity"
    private fun checkUserLogin() {
        val uri: Uri? = intent.data
//        println("uri")
//        println(uri)
        if (uri != null) {
            // if the uri is not null then we are getting the
            // path segments and storing it in list.
            println("uri1")
            val parameters: List<String> = uri.getQueryParameters("token")
            println(parameters[0])
            for (param in parameters) {
                println(param)
            }
            // after that we are extracting string from that parameters.
//            val param = parameters[parameters.size - 1]
            // on below line we are setting
            // that string to our text view
            // which we got as params.

            startActivity(
                Intent(
                    applicationContext,
                    ConfirmPassword::class.java
                ).putExtra("token", parameters[0])
            )
            finish()
        } else {

            Log.i(TAG, "checkUserLogin: " + "working ")
            val userType = sharedPrefManager!!.user.roleName
            val token = sharedPrefManager!!.user.token
            val data = sharedPrefManager!!.user
            if (sharedPrefManager?.isLoggedIn == true) {

                aara.technologies.rewarddragon.App.token = sharedPrefManager!!.user.token.toString()
                Log.i(TAG, ": token " + token)

                Log.i(
                    "splash",
                    "onWindowFocusChanged: " + sharedPrefManager!!.user.token.toString()
                )
                startActivity(
                    Intent(
                        applicationContext,
                        Dashboard::class.java
                    ).putExtra(Constant.userType, userType)
                )
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }

    }
}