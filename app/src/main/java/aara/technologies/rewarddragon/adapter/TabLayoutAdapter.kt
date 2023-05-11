package aara.technologies.rewarddragon.adapter

import aara.technologies.rewarddragon.activities.ConcernFragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class TabLayoutAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                val fragment = ConcernFragment()
                val bundle = Bundle()
                bundle.putInt("position", 0)
                fragment.arguments = bundle
                return fragment
            }
            1 -> {
                val fragment = ConcernFragment()
                val bundle = Bundle()
                bundle.putInt("position", 1)
                fragment.arguments = bundle
                return fragment
            }
            else -> {
                ConcernFragment()
            }
        }
    }
}