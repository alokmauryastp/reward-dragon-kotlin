package aara.technologies.rewarddragon.testing.ViewModelFactory


import aara.technologies.rewarddragon.testing.Repository.BaseRepository
import aara.technologies.rewarddragon.testing.Repository.Repository
import aara.technologies.rewarddragon.testing.Repository.TestRepository
import aara.technologies.rewarddragon.testing.ViewModel.DashboardViewModel
import aara.technologies.rewarddragon.testing.ViewModel.MainActivityViewModel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory(private val repository: BaseRepository, private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> DashboardViewModel(
                repository as TestRepository,
                context
            ) as T

            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> MainActivityViewModel(repository as Repository) as T


            else -> throw IllegalArgumentException("ViewModelClass Not Found")
        }


        //MainViewModel(repository, context) as T
    }


}