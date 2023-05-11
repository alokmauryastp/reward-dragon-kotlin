package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.databinding.ActivityConfirmPasswordBinding
import aara.technologies.rewarddragon.firebase.MyFirebaseMessagingService
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant
import aara.technologies.rewarddragon.utils.CustomLoader
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmPassword : AppCompatActivity() {
    var binding: ActivityConfirmPasswordBinding? = null
    var dialog: CustomLoader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmPasswordBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        dialog = CustomLoader(this,android.R.style.Theme_Translucent_NoTitleBar)

        binding!!.changePassword.setOnClickListener {
            if (validation()) {
                resetPassword(
                    binding!!.newPassword.text.toString()
                )
            }
        }


    }

    private fun validation(): Boolean {
        val otp: String = binding!!.otp.text.toString()
        val password: String = binding!!.newPassword.text.toString()
        val confirmPassword: String = binding!!.confirmPassword.text.toString()

        if (otp.isEmpty()) {
            binding!!.otp.requestFocus()
            binding!!.otp.error = "Required"
            return false
        }
        if (otp.length < 4) {
            binding!!.otp.requestFocus()
            binding!!.otp.error = "Enter Valid OTP"
            return false
        }
        if (password.isEmpty()) {
            binding!!.newPassword.requestFocus()
            binding!!.newPassword.error = "Required"
            return false
        }
        if (password.length < 6) {
            binding!!.newPassword.requestFocus()
            binding!!.newPassword.error = "Password should be at least 6 characters"
            return false
        }
        if (confirmPassword.isEmpty()) {
            binding!!.confirmPassword.requestFocus()
            binding!!.confirmPassword.error = "Required"
            return false
        }
        if (password != confirmPassword) {
            binding!!.confirmPassword.requestFocus()
            binding!!.confirmPassword.error = "Both password should be same"
            return false
        }
        return true
    }

    private fun resetPassword(password: String) {
        dialog!!.show()
        println(password)
        val map: HashMap<String, String> = HashMap()
        map["user_email"] = intent.getStringExtra("email").toString()
        map["user_mobile_no"] = intent.getStringExtra("mobile").toString()
        map["otp"] = binding!!.otp.text.toString()
        map["password"] = password
        map["cpassword"] = binding!!.confirmPassword.text.toString()
        map["firebase_token"] = MyFirebaseMessagingService.getToken(this)
        println(map)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val result = services.resetPassword(map)
        result.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("resetPassword")
                println(response.code().toString())
                println(response.body().toString())
                if (response.code() == 200) {
                    val json = JSONObject(Gson().toJson(response.body()))
                    val status: Int = json.getInt("response_code")
                    if (status == 200) {
                        Toast.makeText(
                            applicationContext,
                            "Password Changed Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        startActivity(Intent(applicationContext, LoginActivity::class.java))
                        finish()
                    } else if (status == 201) {
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
                println("onFailed")
                println(t.message)
                dialog!!.dismiss()
            }
        })
    }
}