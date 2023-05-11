package aara.technologies.rewarddragon.activities

import aara.technologies.rewarddragon.adapter.MyConcernAdapter
import aara.technologies.rewarddragon.databinding.FragmentConcernBinding
import aara.technologies.rewarddragon.utils.Constant.closeList
import aara.technologies.rewarddragon.utils.Constant.openList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

class ConcernFragment : Fragment() {
    var binding: FragmentConcernBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConcernBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val position = arguments?.getInt("position")
  //      println("position")
      //  println(position)
        if (position == 0) {
        //    println(openList.size)
            if (openList.size == 0) {
                binding!!.notFound.visibility= View.VISIBLE
            } else {
                binding!!.notFound.visibility= View.GONE
                binding!!.recyclerView.adapter = MyConcernAdapter(openList, requireContext(),1)
            }
        } else {
        //    println(openList.size)
            if (closeList.size == 0) {
                binding!!.notFound.visibility= View.VISIBLE
            } else {
                binding!!.notFound.visibility= View.GONE
                binding!!.recyclerView.adapter = MyConcernAdapter(closeList, requireContext(),2)
            }
        }


    }


}