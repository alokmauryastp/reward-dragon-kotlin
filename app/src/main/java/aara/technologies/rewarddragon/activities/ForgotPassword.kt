package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.R
import aara.technologies.rewarddragon.databinding.ActivityForgotPasswordBinding
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant.isValidEmail
import aara.technologies.rewarddragon.utils.CustomLoader
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPassword : AppCompatActivity() {

    var binding: ActivityForgotPasswordBinding? = null
    var dialog: CustomLoader? = null
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        context = this

        dialog = CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog!!.setCancelable(false)


        binding!!.forgetPasswordBtn.setOnClickListener {

            if (binding!!.email.text.toString().isEmpty()) {
                binding!!.email.requestFocus()
                binding!!.email.error = "Required"
            } else if (isValidEmail(binding!!.email.text.toString())) {
                binding!!.email.requestFocus()
                binding!!.email.error = "Enter valid email"
            } else if (binding!!.mobile.text.toString().isEmpty()) {
                binding!!.mobile.requestFocus()
                binding!!.mobile.error = "Required"
            } else if (binding!!.mobile.text.toString().length != 10) {
                binding!!.mobile.requestFocus()
                binding!!.mobile.error = "Enter valid Mobile"
            } else {
                resetPassword()
            }
        }
    }

    private val TAG = "ForgotPassword"
    private fun resetPassword() {
        dialog!!.show()
        val map: HashMap<String, String> = HashMap()
        map["user_email"] = binding!!.email.text.toString()
        map["user_mobile_no"] = binding!!.mobile.text.toString()
        println(map)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val result = services.sendOtp(map)
        result.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.i(
                    TAG,
                    "onResponse: " + Gson().toJson(response.body()) + " msg " + response.message() + " code " + response.code()
                )
                if (response.code() != 200) {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
                if (response.code() == 200) {
                    val json = JSONObject(Gson().toJson(response.body()))
                    val resCode = json.getInt("response_code")
                    if (resCode == 200) {

                        AlertDialog.Builder(context)
                            .setIcon(R.drawable.ic_baseline_check_circle_24)
                            .setTitle("OTP Sent")
                            .setCancelable(false)
                            .setMessage(json.getString("message"))
                            .setPositiveButton("Next") { _, _ ->
                                startActivity(
                                    Intent(applicationContext, ConfirmPassword::class.java)
                                        .putExtra("email", binding!!.email.text.toString())
                                        .putExtra("mobile", binding!!.mobile.text.toString())
                                )
                                finish()
                            }
//                        .setNegativeButton("Not Now", null)
                            .show()
                    } else if (resCode == 201) {

                        Toast.makeText(
                            applicationContext,
                            json.getString("message"),
                            Toast.LENGTH_LONG
                        ).show()

                    }

                } else if (response.code() == 400) {
                    println(response.message())
                    Toast.makeText(
                        applicationContext,
                        "There is no active user associated with this e-mail address",
                        Toast.LENGTH_LONG
                    ).show()
                }
                dialog!!.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("onFailed")
                println(t.message)
                dialog!!.dismiss()
            }
        })
    }
}