package aara.technologies.rewarddragon.testing.ViewModelFactory

import aara.technologies.rewarddragon.testing.ViewModel.DashboardViewModel
import aara.technologies.rewarddragon.testing.Repository.TestRepository
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DashboardViewModelFactory(
    private val repository: TestRepository,
    private val context: Context
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DashboardViewModel(repository, context) as T
    }

}