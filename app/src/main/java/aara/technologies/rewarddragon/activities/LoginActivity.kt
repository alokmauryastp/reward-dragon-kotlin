package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.App.token
import aara.technologies.rewarddragon.Dashboard
import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.databinding.ActivityLoginBinding
import aara.technologies.rewarddragon.firebase.MyFirebaseMessagingService
import aara.technologies.rewarddragon.model.User
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant
import aara.technologies.rewarddragon.utils.Constant.androidId
import aara.technologies.rewarddragon.utils.Constant.baseUrl
import aara.technologies.rewarddragon.utils.Constant.isValidEmail
import aara.technologies.rewarddragon.utils.Constant.loginRewardObject
import aara.technologies.rewarddragon.utils.Constant.loginWelcomeObject
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    var binding: ActivityLoginBinding? = null
    var dialog: CustomLoader? = null
    var roleId: Int = 1
    var context: Context? = null
    private var deviceName = ""
    var sharedPrefManager: SharedPrefManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        context = this

        sharedPrefManager = SharedPrefManager.getInstance(this@LoginActivity)
        deviceName = android.os.Build.MANUFACTURER + "/" + android.os.Build.MODEL
        println("deviceName")
        println(deviceName)

        dialog = CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar)
        binding!!.signup.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext, SignUpActivity::class.java
                )
            )
        }

        binding!!.termOfServices.setOnClickListener {
            startActivity(
                Intent(applicationContext, WebViewVertActivity::class.java).putExtra(
                    "link",
                    "$baseUrl/dragonadmin/terms/"
                )
            )
        }
        binding!!.forgot.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext, ForgotPassword::class.java
                )
            )
        }
        binding!!.loginTv.setOnClickListener(this)

        binding!!.radioGroup.setOnCheckedChangeListener { p0, _ ->
            val selectedId = p0?.checkedRadioButtonId
            if (selectedId == R.id.user) {
                roleId = 1
            } else if (selectedId == R.id.manager) {
                roleId = 2
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->

            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            println("token")
            println(token)
            sharedPrefManager!!.setString(SharedPrefManager.FIREBASE_TOKEN, token)

            // Log and toast
//            val msg = getString(R.string.msg_token_fmt, token)
//            Log.d(TAG, msg)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })


    }

    @SuppressLint("SetTextI18n")
    override fun onClick(view: View) {
        when (view.id) {
            R.id.login_tv -> {
                if (validation()) {
                    checkAlreadyLogin()
                }
            }
        }
    }

    private fun validation(): Boolean {

        val email: String = binding!!.email.text.toString()
        val password: String = binding!!.password.text.toString()

        if (email.isEmpty()) {
            binding!!.email.requestFocus()
            binding!!.email.error = "Enter email"
            return false
        }
        if (isValidEmail(email)) {
            binding!!.email.requestFocus()
            binding!!.email.error = "Enter valid email"
            return false
        }
        if (password.isEmpty()) {
            binding!!.password.requestFocus()
            binding!!.password.error = "Required"
            return false
        }
        if (password.length < 6) {
            binding!!.password.requestFocus()
            binding!!.password.error = "Password should be at least 6 characters"
            return false
        }

        if (!binding!!.checkbox.isChecked) {
            Toast.makeText(applicationContext, "Please check Terms of Service", Toast.LENGTH_LONG)
                .show()
            return false
        }
        return true
    }

    private val TAG = "LoginActivity"
    private fun checkAlreadyLogin() {
        dialog!!.show()
        val hashMap: HashMap<String, Any> = hashMapOf()
        hashMap["role_id"] = roleId
        hashMap["email"] = binding!!.email.text.toString()
        hashMap["password"] = binding!!.password.text.toString()
        hashMap["device_id"] = androidId
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.checkAlreadyLogin(hashMap)
        Log.i(TAG, "checkAlreadyLogin: " + Gson().toJson(call.request()))
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                //  println("checkAlreadyLogin")
                //  println(response.code())
                //  println(response.body())

                Log.i(
                    TAG,
                    "onResponse: " + Gson().toJson(response.body()) + " msg " + response.message()
                )
                if (response.code() == 200) {
                    val obj = JSONObject(Gson().toJson(response.body()))
                    val resCode = obj.getInt("response_code")
                    val msg = obj.getString("message")
                    when (resCode) {
                        200 -> {
                            login()
                        }
                        201 -> {
                            val builder = AlertDialog.Builder(
                                ContextThemeWrapper(context, R.style.myDialog)
                            )
                            builder.setMessage("You are logged in another device \nDo you want to login with this device?")
                            builder.setCancelable(false).setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(R.string.app_name)
                            builder.setNegativeButton(
                                "No"
                            ) { dialog, _ -> dialog.cancel() }

                            builder.setPositiveButton(
                                "Yes"
                            ) { _, _ ->
                                login()
                            }

                            val alert = builder.create()
                            alert.show()
                            val nButton: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                            nButton.setTextColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.white
                                )
                            )
                            val pButton: Button = alert.getButton(BUTTON_POSITIVE)
                            pButton.setTextColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.white
                                )
                            )
                        }
                        202 -> {
                            Toast.makeText(
                                applicationContext,
                                msg,
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        response.message(),
                        Toast.LENGTH_LONG
                    ).show()
                    Log.i(TAG, "onResponse: ${response.code()}")
                    Log.i(TAG, "onResponse: body ${response.body()}")
                }
                dialog?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
                dialog?.dismiss()
            }
        })
    }

    private fun login() {
        dialog!!.show()
        val hashMap: HashMap<String, Any> = hashMapOf()
        hashMap["role_id"] = roleId
        hashMap["email"] = binding?.email?.text.toString()
        hashMap["password"] = binding?.password?.text.toString()
        hashMap["firebase_token"] = MyFirebaseMessagingService.getToken(this)
        hashMap["device_id"] = androidId
        hashMap["device_name"] = deviceName
        hashMap["platform"] = "Android"
        println("click on login")
        println(hashMap.toString())
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val result = services.login(hashMap)
        //  println(Gson().toJson(result.request()))
        result.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                Log.i(
                    TAG,
                    "onResponse: login " + Gson().toJson(response.body()) + " response.code() " + response.code()
                )
                /*    println("login")
                println(response.code())
                println(response.body())*/
                if (response.code() == 200) {
                    val json = JSONObject(Gson().toJson(response.body()))
                    val responseCode: Int = json.getInt("response_code")
                    if (responseCode == 200) {
                        val user: User = Gson().fromJson(
                            json.getJSONObject("user_data").toString(),
                            User::class.java
                        )

                        Log.i(TAG, "onResponse:userData " + Gson().toJson(user))
                        sharedPrefManager?.userLogin(user)
                        token = user.token.toString()
                        Log.i(TAG, "onResponse:new Token " + user.token)
                        // println("Logged In")

                        val bonusJsonObject: JSONObject = json.getJSONObject("reward_points_data")
                        val welcomeBonusJsonObject: JSONObject =
                            json.getJSONObject("welcome_message")

                        loginRewardObject = bonusJsonObject
                        loginWelcomeObject = welcomeBonusJsonObject

                        goIntent()
                        //showBonusDialog(json)

                    } else if (responseCode == 201) {
                        Toast.makeText(
                            applicationContext,
                            json.getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                dialog!!.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
                dialog!!.dismiss()
            }
        })
    }

    fun goIntent() {
        if (roleId == 1) {
            val intent = Intent(applicationContext, Dashboard::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(Constant.userType, Constant.user)
            startActivity(intent)
            finish()
        } else {

            val intent = Intent(applicationContext, Dashboard::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(Constant.userType, Constant.manager)
            startActivity(intent)
            finish()

        }
    }


//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        // TODO Auto-generated method stub
////        super.onWindowFocusChanged(hasFocus)
//        val width: Int = binding!!.parent.width
//        println("width")
//        println(width)
////        updateSizeInfo();
//    }

}