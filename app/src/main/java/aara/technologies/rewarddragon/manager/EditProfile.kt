package aara.technologies.rewarddragon.manager

import aara.technologies.rewarddragon.databinding.FragmentEditProfileBinding
import aara.technologies.rewarddragon.services.DataServices
import aara.technologies.rewarddragon.services.RetrofitInstance
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EditProfile : Fragment() {

    var binding: FragmentEditProfileBinding? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        binding!!.firstName.setText(SharedPrefManager.getInstance(requireContext())!!.user.firstName.toString())
        binding!!.lastName.setText(SharedPrefManager.getInstance(requireContext())!!.user.lastName.toString())


        binding!!.update.setOnClickListener {

            updateProfile()
        }

//        getTeamList()

        return binding!!.root
    }



    private fun updateProfile() {
        val map: HashMap<String, String> = hashMapOf()
        map["first_name"] = binding!!.firstName.text.toString()
        map["last_name"] = binding!!.lastName.text.toString()
        println(map)
        val services = RetrofitInstance().getInstance().create(DataServices::class.java)
        val call = services.updateProfile(
            SharedPrefManager.getInstance(requireContext())!!.user.id.toString(),
            map
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                println(response.code())
                println(response.body())
                if (response.code()==200){
//                    val obj = JSONObject(Gson().toJson(response.body()?.getAsJsonObject("data")))
//                    val firstName = obj.getJSONObject("user").getString("first_name")
//                    val lastName = obj.getJSONObject("user").getString("last_name")

                    SharedPrefManager.getInstance(requireContext())!!.setString(SharedPrefManager.KEY_USERFIRSTNAME,binding!!.firstName.text.toString())
                    SharedPrefManager.getInstance(requireContext())!!.setString(SharedPrefManager.KEY_USERLASTNAME,binding!!.lastName.text.toString())

//                    (context as Dashboard).addFragment(MyProfile())

                    startActivity(Intent(context,MyProfile::class.java))
                }


            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println(t.message)
            }
        })

    }


}