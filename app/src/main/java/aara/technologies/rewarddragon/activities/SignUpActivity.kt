package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.databinding.ActivitySignUpBinding
import aara.technologies.rewarddragon.firebase.MyFirebaseMessagingService
import aara.technologies.rewarddragon.model.LocationModel
import aara.technologies.rewarddragon.model.Role
import aara.technologies.rewarddragon.model.TeamModel
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.Constant.isValidEmail
import aara.technologies.rewarddragon.utils.CustomLoader
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SignUpActivity : AppCompatActivity(), OnClickListener {

    var binding: ActivitySignUpBinding? = null
    var dialog: CustomLoader? = null
    private lateinit var services: DataServices
    lateinit var context: Context
    private var roleId = 0

    //var teamId = -1
    var managerId = -1
    var language = ""
    var gender = ""
//    var location = ""

    var locationList: List<LocationModel> = ArrayList()

    var locations: ArrayList<String> = ArrayList()

    var teamList: ArrayList<TeamModel> = ArrayList()

    var teams: ArrayList<String> = ArrayList()

    private var isUniqueCodeValid = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        context = this

        dialog = CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog!!.setCancelable(false)

        services = RetrofitInstance().getInstance().create(DataServices::class.java)

        binding!!.manager.setOnClickListener(this)
        binding!!.user.setOnClickListener(this)
        binding!!.signIn.setOnClickListener(this)

        //Gender Spinner

/*        val genders: ArrayList<String?> = ArrayList()
        genders.add("Select Gender")
        genders.add("Male")
        genders.add("Female")

        val genderAdapter = ArrayAdapter(
            context,
            R.layout.simple_spinner_item,
            genders
        ) //setting the country_array to spinner
        genderAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding!!.genderSpinner.adapter = genderAdapter
        binding!!.genderSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    gender = genders[position].toString()
                    println(gender)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }*/

        //Language Spinner

        /*    val languages: ArrayList<String?> = ArrayList()
            languages.add("Select Language")
            languages.add("Hindi")
            languages.add("English")

            val adapterLang = ArrayAdapter(
                context,
                R.layout.simple_spinner_item, languages
            ) //setting the country_array to spinner
            adapterLang.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding!!.defaultLanguage.adapter = adapterLang
            binding!!.defaultLanguage.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        language = languages[position].toString()
                        println(language)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }
                }*/


        // team spinner

/*
        teams.add("Select Team")
        val teamAdapter = ArrayAdapter(
            context,
            R.layout.simple_spinner_item, teams
        ) //setting the country_array to spinner
        teamAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item2)
         binding!!.team.adapter = teamAdapter

        // location spinner

        locations.add("Select Location")
        val locationAdapter = ArrayAdapter(
            context,
            R.layout.simple_spinner_item, locations
        ) //setting the country_array to spinner
        locationAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item2)
         binding!!.location.adapter = locationAdapter*/

/*
        binding!!.uniqueCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length == 9) {
                    println("api call")
                    println(p0.toString())
                         //getTeamList(p0.toString())
                    //  getBaseLocation(p0.toString())
                } else {
                    isUniqueCodeValid = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
*/

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        binding!!.dateOfJoining.setOnClickListener {
            val dpd = DatePickerDialog(context, { _, year, monthOfYear, dayOfMonth ->
                binding!!.dateOfJoining.setText(
                    "$year-" + String.format(
                        "%02d",
                        monthOfYear + 1
                    ) + "-" + String.format("%02d", dayOfMonth)
                )
            }, year, month, day)
            dpd.show()
        }


        binding!!.submit.setOnClickListener {
            if (validation()) {
                signUp()
            }
        }
    }

    private fun validation(): Boolean {
        val firstName: String = binding!!.firstName.text.toString()
        val lastName: String = binding!!.lastName.text.toString()
        val uniqueCode: String = binding!!.uniqueCode.text.toString()
        val email: String = binding!!.email.text.toString()
        val mobile: String = binding!!.mobile.text.toString()
        val designation: String = binding!!.designation.text.toString()
        val dateOfJoining: String = binding!!.dateOfJoining.text.toString()
        val password: String = binding!!.password.text.toString()
        val conPassword: String = binding!!.conPassword.text.toString()

        if (roleId == 0) {
            Toast.makeText(applicationContext, "Select Role", Toast.LENGTH_LONG).show()
            return false
        }
        if (firstName.isEmpty()) {
            binding!!.firstName.requestFocus()
            binding!!.firstName.error = "Required"
            return false
        }
        if (lastName.isEmpty()) {
            binding!!.lastName.requestFocus()
            binding!!.lastName.error = "Required"
            return false
        }

        if (uniqueCode.isNullOrEmpty()) {
            binding!!.uniqueCode.requestFocus()
            binding!!.uniqueCode.error = "Required"
            return false
        }
        if (email.isEmpty()) {
            binding!!.email.requestFocus()
            binding!!.email.error = "Required"
            return false
        }
        if (isValidEmail(email)) {
            binding!!.email.requestFocus()
            binding!!.email.error = "Enter valid email"
            return false
        }
        if (mobile.isEmpty()) {
            binding!!.mobile.requestFocus()
            binding!!.mobile.error = "Required"
            return false
        }
        if (mobile.length < 10) {
            binding!!.mobile.requestFocus()
            binding!!.mobile.error = "Enter valid Mobile"
            return false
        }
        if (designation.isEmpty()) {
            binding!!.designation.requestFocus()
            binding!!.designation.error = "Required"
            return false
        }
        if (dateOfJoining.isEmpty()) {
            binding!!.dateOfJoining.requestFocus()
            binding!!.dateOfJoining.error = "Required"
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
        if (conPassword.isEmpty()) {
            binding!!.conPassword.requestFocus()
            binding!!.conPassword.error = "Required"
            return false
        }
        if (password != conPassword) {
            binding!!.conPassword.requestFocus()
            binding!!.conPassword.error = "Both password should be same"
            return false
        }
        /*      if (binding!!.defaultLanguage.selectedItemPosition == 0) {
                  Toast.makeText(context, "Select Language", Toast.LENGTH_SHORT).show()
                  return false
              }
              if (binding!!.genderSpinner.selectedItemPosition == 0) {
                  Toast.makeText(context, "Select Gender", Toast.LENGTH_SHORT).show()
                  return false
              }
              if (binding!!.team.selectedItemPosition == 0) {
                  Toast.makeText(context, "Select Team", Toast.LENGTH_SHORT).show()
                  return false
              }
        if (managerId == 0) {
            Toast.makeText(context, "Manager not available for this Team", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        if (binding!!.location.selectedItemPosition == 0) {
            Toast.makeText(context, "Select Location", Toast.LENGTH_SHORT).show()
            return false
        }*/
        return true
    }

    private fun signUp() {
        dialog!!.show()
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["role_id"] = roleId
        hashMap["first_name"] = binding?.firstName?.text.toString()
        hashMap["last_name"] = binding?.lastName?.text.toString()
        hashMap["unique_code"] = binding?.uniqueCode?.text.toString()
        hashMap["email"] = binding?.email?.text.toString()
        hashMap["mobile_no"] = binding?.mobile?.text.toString()
        hashMap["new_password"] = binding?.password?.text.toString()
        hashMap["confirm_password"] = binding?.conPassword?.text.toString()
        hashMap["firebase_token"] = MyFirebaseMessagingService.getToken(this)
        //   hashMap["gender"] = binding!!.genderSpinner.selectedItem.toString()
        hashMap["designation"] = binding!!.designation.text.toString()
        // hashMap["language"] = binding!!.defaultLanguage.selectedItem.toString()
        // hashMap["team_id"] = teamId.toString()
        //  hashMap["manager_id"] = ""
        // hashMap["location"] = location
        hashMap["org_doj"] = binding!!.dateOfJoining.text.toString()

        println("click on submit")
        println(hashMap.toString())

        val result = services.signUp(hashMap)
        result.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println(response.code())
                println(response.body())
                if (response.code() == 200) {1

                    val json = JSONObject(Gson().toJson(response.body()))
                    val responseCode: Int = json.getInt("response_code")

                    if (responseCode == 200) {
                        AlertDialog.Builder(context)
                            .setTitle("Success")
                            .setMessage(json.getString("message"))
                            .setPositiveButton(
                                "OK"
                            ) { dialog, _ ->
                                dialog.dismiss()
                                finishAffinity() //
                                startActivity(Intent(applicationContext, LoginActivity::class.java))
                            }
                            .show()
                    } else if (responseCode == 201) {

                        AlertDialog.Builder(context)
                            .setTitle("Error")
                            .setMessage(json.getString("message"))
                            .setPositiveButton(
                                "OK"
                            ) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()

                    }

                } else if (response.code() == 400) {
                    println(response.message())
                    Toast.makeText(applicationContext, "Something Went Wrong", Toast.LENGTH_LONG)
                        .show()
                }

                dialog!!.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialog!!.dismiss()
            }
        })


    }

    private fun getTeamList(uniqueCode: String) {
        dialog?.show()
        teamList.clear()
        teams.clear()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call =
            services.getTeamListForSignup(uniqueCode.uppercase())
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                /*  println("getTeamList")
                  println(response.code())
                  println(response.body())*/
                if (response.code() == 200) {

                    val array = JSONArray(Gson().toJson(response.body()?.getAsJsonArray("data")))
                    val turnsType = object : TypeToken<List<TeamModel>>() {}.type

                    teamList = Gson().fromJson(array.toString(), turnsType)

                    isUniqueCodeValid = true

                    /*    teams.add("Select Team")
                     for (item in teamList) {
                         println(item.team_name)
                         teams.add(item.team_name)
                     }


                     teamList.add(0, TeamModel("", 0, 0, "", 0, "", ""))

                     binding!!.team.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    if (position != 0) {
                                        teamId = teamList[position].id
                                        managerId = teamList[position].manager_id
                                        println("teamId")
                                        println(teamId)
                                        println(managerId)
                                    }
                                }

                                override fun onNothingSelected(p0: AdapterView<*>?) {

                                }
                            }*/
                }
                dialog?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("onFailure")
                println(t.message)
                dialog?.dismiss()
            }
        })
    }

    private fun getBaseLocation(uniqueCode: String) {
        dialog?.show()
        locationList
        locations.clear()
        val map: HashMap<String, Any> = hashMapOf()
        map["unique_code"] = uniqueCode.uppercase()
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.getBaseLocation(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                println("getBaseLocation")
                println(response.code())
                println(response.body())
                if (response.code() == 200) {
                    val array = JSONArray(
                        Gson().toJson(
                            response.body()?.getAsJsonArray("org_baselocation_list_data")
                        )
                    )
                    locationList =
                        Gson().fromJson(array.toString(), Array<LocationModel>::class.java).toList()

                    locations.add("Select Location")

                    for (item in locationList) {
                        println(item.location)
                        locations.add(item.location)
                    }
                    locationList.toMutableList().add(0, LocationModel("", 0, "", 0, ""))

//                    locationList.add(0, LocationModel("", 0, "", 0, ""))

                    /*      binding!!.location.onItemSelectedListener =
                              object : AdapterView.OnItemSelectedListener {
                                  override fun onItemSelected(
                                      parent: AdapterView<*>?,
                                      view: View?,
                                      position: Int,
                                      id: Long
                                  ) {
                                      if (position != 0) {
                                          location = locationList[position].location
                                          println(location)
                                      }
                                  }

                                  override fun onNothingSelected(p0: AdapterView<*>?) {

                                  }
                              }*/
                }
                dialog?.dismiss()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("onFailure")
                println(t.message)
                dialog?.dismiss()
            }
        })
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getRoles() {
        // launching a new coroutine
        GlobalScope.launch {
            val result = services.getRoles()
            Log.d("ayush: ", result.body()!!.role[0].roleName.toString())
            val list: ArrayList<Role>? = result.body()?.role
            if (list != null) {
                for (role in list) {
                    Log.d("ayush: ", role.roleName.toString())
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            binding!!.manager.id -> {
                roleId = 2
            }
            binding!!.user.id -> {
                roleId = 1
            }
            binding!!.signIn.id -> finish()
        }
    }
}