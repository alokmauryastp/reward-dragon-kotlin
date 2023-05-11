package aara.technologies.rewarddragon.testing.ViewModel

import aara.technologies.rewarddragon.model.UnreadNotificationCount
import aara.technologies.rewarddragon.testing.Repository.TestRepository
import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: TestRepository, context: Context) : ViewModel() {

    private val TAG = "MainViewModel"
    var prefManager: SharedPrefManager? = SharedPrefManager.getInstance(context)
    init {
        viewModelScope.launch {
            Dispatchers.IO
            prefManager?.user?.id?.let { repository.getNotiCount(it) }

            //  Log.i(TAG, ":userid " + prefManager!!.user.id)
        }
    }
    val notiCount: LiveData<UnreadNotificationCount>
        get() = repository.notiCount

}