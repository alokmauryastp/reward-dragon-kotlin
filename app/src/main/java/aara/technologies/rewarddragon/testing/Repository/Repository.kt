package aara.technologies.rewarddragon.testing.Repository

import aara.technologies.rewarddragon.services.DataServices

class Repository(private val api: DataServices) : BaseRepository() {

    suspend fun getJoshData(
        userid: String,
        managerId: String
    ) = safeApiCall { api.getJoshToday_Test(userid, managerId) }


    suspend fun getWinLevelPoints(
        userid: HashMap<String, String>
    ) = safeApiCall { api.getWinLevelPoints_test(userid) }


    suspend fun getCustomerKpiData(
        userid: HashMap<String, String>
    ) = safeApiCall { api.getCustomerKpiData_test(userid) }


    suspend fun getWellBeingData(
        hashMap: HashMap<String, String>
    ) = safeApiCall { api.wellBeingList_Test(hashMap) }


    suspend fun getVoucherData(
    ) = safeApiCall { api.homepageVoucherList_Test() }


    suspend fun getRewardPoints(hashMap: HashMap<String, String>) =
        safeApiCall { api.getRewardPoints_Test(hashMap) }

    suspend fun checkUserLogin(hashMap: HashMap<String, String> /* = java.util.HashMap<kotlin.String, kotlin.String> */) =
        safeApiCall { api.userLoginCheck(hashMap) }

}