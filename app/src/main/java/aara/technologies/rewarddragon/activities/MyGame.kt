package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.databinding.ActivityMyGameBinding
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class MyGame : Fragment() {
    var binding: ActivityMyGameBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        binding = ActivityMyGameBinding.inflate(
            layoutInflater
        )
        return binding!!.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.userNameTxt.text = SharedPrefManager.getInstance(requireContext())!!.user.firstName+" "+ SharedPrefManager.getInstance(requireContext())!!.user.lastName


    }
}