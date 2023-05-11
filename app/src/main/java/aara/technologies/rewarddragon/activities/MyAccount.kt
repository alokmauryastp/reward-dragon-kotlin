package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.databinding.FragmentMyAccountBinding
import aara.technologies.rewarddragon.manager.*
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant
import aara.technologies.rewarddragon.utils.CustomLoader
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyAccount : Fragment() {

    var binding: FragmentMyAccountBinding? = null
    var dialog: CustomLoader? = null
    var sharedPrefManager: SharedPrefManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyAccountBinding.inflate(layoutInflater)
        sharedPrefManager = SharedPrefManager.getInstance(requireContext())
        // Inflate the layout for this fragment
        return binding!!.root
    }

    private val TAG = "MyAccount"

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = CustomLoader(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)
        if (sharedPrefManager!!.user.roleId == 2) {
            binding!!.myTeam.visibility = View.VISIBLE
            binding!!.myTeam.setOnClickListener {
                startActivity(Intent(context, MyTeam::class.java))
            }
        }

        binding!!.userNameTxt.text =
            sharedPrefManager!!.user.firstName + " " + sharedPrefManager!!.user.lastName



        binding!!.designation.text = sharedPrefManager!!.user.designation

        Constant.loadAvatarImage(requireContext(), binding!!.imageView)

        binding!!.myProfile.setOnClickListener {
            startActivity(Intent(context, MyProfile::class.java))
        }

        binding!!.myResource.setOnClickListener {
            startActivity(Intent(context, MyResources::class.java))
        }

        binding!!.myCoupons.setOnClickListener {
            startActivity(Intent(context, MyCoupons::class.java))
        }

        binding!!.myConcern.setOnClickListener {
            if (sharedPrefManager!!.user.roleId == 1) {
                startActivity(Intent(context, MyConcern::class.java))
            } else {
                startActivity(Intent(context, MyConcernManager::class.java))
            }
        }

        binding!!.privacyPolicy.setOnClickListener {
            privacyPolicy()
        }

        binding!!.termOfServices.setOnClickListener {

            termAndCondition()
        }

        binding!!.logout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure want to logout?")
                .setPositiveButton(
                    "YES"
                ) { dialog, _ ->
                    dialog.dismiss()
                    logout()
                }
                .setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }


    private fun logout() {
        dialog?.show()
        val map = HashMap<String, String>()
        map["user_profile_id"] = sharedPrefManager!!.user.id.toString()
        map["device_id"] = Constant.androidId
        println("logout")
        println(sharedPrefManager!!.user.id.toString())
        println(sharedPrefManager!!.user.roleId.toString())
        println(map)
        val services = RetrofitInstance().getInstance().create(
            DataServices::class.java
        )
        val call = services.logout(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println(response.code())
                println(response.body())
                try {
                    // Configure sign-in to request the user's ID, email address, and basic
                    // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build()
                    // Build a GoogleSignInClient with the options specified by gso.
                    val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
                    mGoogleSignInClient.signOut()
                    Log.e("logout", response.body().toString())
                    sharedPrefManager!!.logout()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    Toast.makeText(
                        requireContext(),
                        "User Logout Successfully...",
                        Toast.LENGTH_SHORT
                    ).show()
                    requireActivity().finish()
                } catch (e: Exception) {
                    Log.e("logout e", e.message!!)
                }
                dialog?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialog?.dismiss()
                Log.e("logout t", t.message!!)
            }
        })
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

}