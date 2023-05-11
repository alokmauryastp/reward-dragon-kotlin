package aara.technologies.rewarddragon.testing.ViewModel

import aara.technologies.rewarddragon.testing.Model.*
import aara.technologies.rewarddragon.testing.Repository.Repository
import aara.technologies.rewarddragon.testing.Resource
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repository: Repository) : ViewModel() {

    private val TAG = "MainActivityViewModel"

    private val joshRes: MutableLiveData<Resource<JoshRes>> = MutableLiveData()
    val joshResponse: LiveData<Resource<JoshRes>>
        get() = joshRes


    private val winLevelRes: MutableLiveData<Resource<WinLevelRes>> = MutableLiveData()
    val response: LiveData<Resource<WinLevelRes>>
        get() = winLevelRes


    private val kpiDataRes: MutableLiveData<Resource<KpiDataRes>> = MutableLiveData()
    val kpiData: LiveData<Resource<KpiDataRes>>
        get() = kpiDataRes


    private val wellBeingListRes: MutableLiveData<Resource<GetWellBeingListRes>> = MutableLiveData()
    val wellBeingData: LiveData<Resource<GetWellBeingListRes>>
        get() = wellBeingListRes


    private val voucherRes: MutableLiveData<Resource<HomePageVoucherRes>> = MutableLiveData()
    val voucherData: LiveData<Resource<HomePageVoucherRes>>
        get() = voucherRes

    private val rewardPointsRes: MutableLiveData<Resource<RewardPointsRes>> = MutableLiveData()
    val rewardData: LiveData<Resource<RewardPointsRes>>
        get() = rewardPointsRes


    private val checkUserLoginRes: MutableLiveData<Resource<CheckUserLoginRes>> = MutableLiveData()
    val checkUserLoginData: LiveData<Resource<CheckUserLoginRes>>
        get() = checkUserLoginRes


    fun getJosh(userId: String, managerId: String) = viewModelScope.launch {
        joshRes.value = repository.getJoshData(userId, managerId)
    }


    fun getWinLevel(userId: HashMap<String, String>) = viewModelScope.launch {
        winLevelRes.value = repository.getWinLevelPoints(userId)
    }


    fun getKpiData(userId: HashMap<String, String>) = viewModelScope.launch {
        Log.i(TAG, "getKpiData:uid $userId")
        kpiDataRes.value = repository.getCustomerKpiData(userId)
    }

    fun getWellbeingList(map2: HashMap<String, String>) = viewModelScope.launch {

        wellBeingListRes.value = repository.getWellBeingData(map2)

    }

    fun getVoucherList() = viewModelScope.launch {
        voucherRes.value = repository.getVoucherData()
    }

    fun getRewardPoints(map: HashMap<String, String>) = viewModelScope.launch {
        rewardPointsRes.value = repository.getRewardPoints(map)
    }


/*    fun getCheckLoginStatus(map:HashMap<String,String>)=viewModelScope.launch {
        checkUserLoginRes.value=repository.checkUserLogin(map)
    }*/


}