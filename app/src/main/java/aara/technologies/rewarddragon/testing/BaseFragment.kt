package aara.technologies.rewarddragon.testing

import aara.technologies.rewarddragon.testing.Repository.BaseRepository
import aara.technologies.rewarddragon.testing.ViewModelFactory.MainViewModelFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VM : ViewModel, B : ViewBinding, R : BaseRepository> : Fragment() {

    protected lateinit var binding: B
    protected lateinit var viewModel: VM

    protected val remoteDataSource = RemoteDataSource()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getFragmentBinding(inflater, container)
        val factory = MainViewModelFactory(getFragmentRepository(), requireContext())
        viewModel = ViewModelProvider(this, factory)[getViewModel()]
        return binding.root
    }


    abstract fun getViewModel(): Class<VM>
    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B
    abstract fun getFragmentRepository(): R

}