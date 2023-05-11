package aara.technologies.rewarddragon.testing.Repository

import aara.technologies.rewarddragon.model.UnreadNotificationCount
import aara.technologies.rewarddragon.services.DataServices
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TestRepository(private val dataServices: DataServices) {

    private val notificationCountLiveData = MutableLiveData<UnreadNotificationCount>()

    val notiCount: LiveData<UnreadNotificationCount>
        get() = notificationCountLiveData


    suspend fun getNotiCount(userid: Int) {
        val result = dataServices.getNotificationCount(userid)
        if (result?.body() != null) {
            notificationCountLiveData.postValue(result.body())
        }
    }




}