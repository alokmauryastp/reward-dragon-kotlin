package aara.technologies.rewarddragon.services
import aara.technologies.rewarddragon.activities.MyReward
import aara.technologies.rewarddragon.activities.TeamChallengeBottomSheet
import aara.technologies.rewarddragon.model.CouponData
import aara.technologies.rewarddragon.model.RoleModel
import aara.technologies.rewarddragon.model.UnreadNotificationCount
import aara.technologies.rewarddragon.testing.Model.*
import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface DataServices {

    @GET("accounts/role-lists/")
    suspend fun getRoles(): Response<RoleModel>

    @POST("accounts/signup/")
    fun signUp(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("accounts/login/")
    fun login(@Body map: HashMap<String, Any>): Call<JsonObject>

    @GET("managers/challenge-purpose-list/")
    fun getChallengePurposeList(): Call<JsonObject>

    @GET("managers/campaign-purpose-list/")
    fun getCampaignPurposeList(): Call<JsonObject>

    @POST("api/password_reset/")
    fun passwordReset(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("accounts/send-otp/")
    fun sendOtp(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("api/password_reset/confirm/")
    fun confirmPassword(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("accounts/reset-password/")
    fun resetPassword(@Body map: HashMap<String, String>): Call<JsonObject>

        @POST("customers/challenge-lists/")
    fun getChallengeList(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("customers/campaign-lists/")
    fun getCampaignList(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("managers/create-challenge/")
    fun createChallenge(@Body map: HashMap<String, Any>): Call<TeamChallengeBottomSheet.ChallengeResponse>

    @POST("managers/create-campaign/")
    fun createCampaign(@Body map: HashMap<String, Any>): Call<JsonObject>

    @GET("customers/concern-category-list/")
    fun getConcernCategoryList(): Call<JsonObject>

    @GET("managers/manager-concern-category/")
    fun getConcernCategoryListForManager(): Call<JsonObject>

    @POST("customers/customer-concern-data-update/")
    fun submitConcern(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("managers/manager_raise_concern_update/")
    fun submitConcernForSubmit(@Body map: HashMap<String, String>): Call<JsonObject>

    @GET("customers/customer-concern-data-list/")
    fun getConcernList(@Query("user_profile") id: String): Call<JsonObject>

    @GET("managers/manager_raise_concern_list/")
    fun getConcernListForManager(@Query("user_profile") id: String): Call<JsonObject>

    @POST("managers/review-challenge/")
    fun getTeamChallenges(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("managers/broadcast-challenge/")
    fun updateBroadCastStatus(@Body map: HashMap<String, String>): Call<JsonObject>

    @GET("customers/customer-josh-reason-type/")
    fun getJoshReasonType(): Call<JsonObject>

    @GET("managers/manager_josh_reason_type/")
    fun getJoshReasonTypeForManager(): Call<JsonObject>

    @POST("managers/review-campaign/")
    fun getTeamCampaign(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("managers/broadcast-campaign/")
    fun updateCampaignBroadCastStatus(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("customers/notification-lists/")
    fun getNotification(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("customers/accept-challenge/")
    fun acceptChallenge(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/accept-campaign/")
    fun acceptCampaign(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("customers/complete-challenge/")
    fun completeChallenge(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("customers/complete-campaign/")
    fun completeCampaign(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("customers/mark-read-notifications/")
    fun markReadNotification(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/mark-unread-notifications/")
    fun markUnReadNotification(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/delete-notifications/")
    fun deleteNotification(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/customer-josh-reason-create/")
    fun submitCustomerJosh(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("managers/manager-josh-reason-create/")
    fun submitManagerJosh(@Body map: HashMap<String, Any>): Call<JsonObject>

    @GET("managers/team-profile-list/")
    fun getTeamListForSignup(@Query("unique_code") uniqueCode: String): Call<JsonObject>

    @PUT("accounts/user-profile-update/{id}/")
    fun updateProfile(@Path("id") id: String, @Body map: HashMap<String, String>): Call<JsonObject>

    @POST("managers/team-lists/")
    fun getTeamData(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("managers/team-employee-lists/")
    fun teamEmployeeList(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("customers/employee-list/")
    fun getEmployeeReward(@Body map: java.util.HashMap<String, Any>): Call<JsonObject>


    @POST("managers/end-challenge/")
    fun endChallengeByManager(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("managers/end-campaign/")
    fun endCampaignByManager(@Body map: HashMap<String, Any>): Call<JsonObject>

    @GET("dragonadmin/companysite-list-data/")
    fun getCompanySiteListData(): Call<JsonObject>

    @GET("dragonadmin/skill-and-hobby-list/")
    fun getSkillHobbyList(): Call<JsonObject>

    @GET("dragonadmin/finance-and-art-lists")
    fun getFinanceArtList(): Call<JsonObject>

    @GET("dragonadmin/leadership-list-data/")
    fun getLeaderShipListData(): Call<JsonObject>

    @GET("dragonadmin/learningmaterial-list-data/")
    fun getLearningMaterialListData(@Query("unique_code") body: String): Call<JsonObject>

    @POST("dragonadmin/leader-ship-talk-list/")
    fun getGetLeadershipTalkListData(@Body map: HashMap<String, String>?): Call<JsonObject>

    @GET("dragonadmin/otherlink-list-data/")
    fun getOtherLinkListData(): Call<JsonObject>

    @GET("customers/customer-josh-reason-today/")
    fun getJoshToday(
        @Query("user_profile") id: String,
        @Query("manager_id") managerId: String
    ): Call<JsonObject>

    @GET("customers/customer-josh-reason-today/")
    suspend fun getJoshToday_Test(
        @Query("user_profile") id: String,
        @Query("manager_id") managerId: String
    ): JoshRes

    @GET("managers/manager_josh_reason_today/")
    fun getJoshTodayForManager(
        @Query("user_profile") id: Int,
        @Query("team_id") teamId: Int
    ): Call<JsonObject>

    @GET("dragonadmin/game-name-list/")
    fun getGameList(
        @Query("unique_code") id: String,
        @Query("game_category_id") gameCategoryId: Int
    ): Call<JsonObject>

    @POST("managers/team-mood-today/")
    fun getTeamMoodToday(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("managers/team-leaderboard/")
    fun getTeamLeaderBoard(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("accounts/logout/")
    fun logout(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("customers/my-reward-point-list/")
    fun myRewardPointList(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("managers/industry-list-data/")
    fun getIndustryListData(@Body map: HashMap<String, String>): Call<JsonObject>

    @GET("managers/kpiname-list-data/")
    fun getKpiListData(
        @Query("industry_id") industryId: String,
        @Query("type") type: String
    ): Call<JsonObject>

    @POST("managers/team-leaderboard-filter/")
    fun getTeamLeaderboardFilter(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/win-level-points/")
    fun getWinLevelPoints(@Body map: HashMap<String, String>): Call<JsonObject>

    //testing

    @POST("customers/win-level-points/")
    suspend fun getWinLevelPoints_test(@Body id: java.util.HashMap<String, String>): WinLevelRes

    @POST("managers/kpi-lists/")
    fun getKpiList(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("managers/team-performance-lists/")
    fun getTeamPerformanceList(@Body map: HashMap<String, Any>): Call<JsonObject>

    @GET("customers/challenge-point-data/")
    fun getChallengePoint(@Query("user_profile") userProfile: String): Call<JsonObject>

    @GET("customers/campaign-point-data/")
    fun getCampaignPoint(@Query("user_profile") userProfile: String): Call<JsonObject>

    @POST("managers/manager-kpi-met-and-wip/")
    fun getManagerKpiData(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("customers/kpi-met-and-wip/")
    fun getCustomerKpiData(@Body map: HashMap<String, String>): Call<JsonObject>

    //testing
    @POST("customers/kpi-met-and-wip/")
    suspend fun getCustomerKpiData_test(@Body map: java.util.HashMap<String, String>): KpiDataRes

    @POST("accounts/show-msg-already-login/")
    fun checkAlreadyLogin(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("accounts/user-login-check/")
    fun userLoginCheck(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("customers/customer-played-game-time/")
    fun playedGameTime(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/watch-time-data/")
    fun watchTimeData(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/finance-and-art-data/")
    fun inspiredLivingData(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/read-skill-and-hobby-time-data/")
    fun skillHobbyTimeData(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/learning-material-time-data/")
    fun learningMaterialTimeData(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/customer-played-game-list/")
    fun nextAvailabilityTime(@Body map: HashMap<String, String>): Call<JsonObject>

    @GET("customers/habbit-of-the-day-message/")
    fun habitOfTheDay(): Call<JsonObject>

    @GET("dragonadmin/avatar-image-lists/")
    fun getAvatarImageList(): Call<JsonObject>

    @POST("accounts/update-avatar-image/")
    fun updateAvatarImage(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/wellbeing-lists/")
    fun wellBeingList(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("customers/wellbeing-lists/")
    suspend fun wellBeingList_Test(@Body map: HashMap<String, String>): GetWellBeingListRes

    @POST("managers/team-wellbeing-lists/")
    fun teamWellBeingList(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("customers/save-steps-taken/")
    fun updateStepTaken(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/kpi-performance-lists/")
    fun getKpiPerformanceData(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/game-point-list/")
    fun getGamePoint(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("managers/team-mood-today-filter/")
    fun getTeamMoodTodayFilter(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/user-motivational-message-list/")
    fun getMotivationalMessage(@Body map: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/filter-employee-bar-chart-pie-chart/")
    fun myLeaderboardWinTrends(@Body map: HashMap<String, Any>): Call<JsonObject>

    @GET("https://dashboard.jamdj.one/api/categories")
    fun giftCategoryList(): Call<JsonObject>

    @POST("customers/convert-points-to-money/")
    fun convertMoneyToPoints(@Body map: HashMap<String, Any>): Call<MyReward.PointsToCurrencyRes>

    @GET("https://dashboard.jamdj.one/api/brands")
    fun giftBrandList(): Call<JsonObject>

    @GET("dragonadmin/point-range-lists/")
    fun pointRangeList(): Call<JsonObject>

    @POST("dragonadmin/product-voucher-lists/")
    fun productVoucherList(@Body hashmap: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/list-my-coupon/")
    fun listMyCoupon(@Body hashmap: HashMap<String, String>): Call<CouponData>

    @POST("customers/save-my-coupon/")
    fun claimReward(@Body hashmap: HashMap<String, String> /* = java.util.HashMap<kotlin.String, kotlin.Any> */): Call<JsonObject>

    @POST("dragonadmin/product-voucher-wishlists/")
    fun productVoucherWishList(@Body hashmap: HashMap<String, Any>): Call<JsonObject>

    @GET("dragonadmin/ten-product-voucher-lists/")
    fun homepageVoucherList(): Call<JsonObject>

    @GET("dragonadmin/ten-product-voucher-lists/")
    suspend fun homepageVoucherList_Test(): HomePageVoucherRes

    @POST("dragonadmin/add-to-wishlist/")
    fun addToWishlist(@Body hashmap: HashMap<String, Any>): Call<JsonObject>

    @POST("dragonadmin/remove-from-wishlist/")
    fun removeWishlist(@Body hashmap: HashMap<String, Any>): Call<JsonObject>

    @GET("dragonadmin/game-category-lists/")
    fun gameCategoryList(): Call<JsonObject>

    @POST("customers/reward-points/")
    fun getRewardPoints(@Body hashmap: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/reward-points/")
    suspend fun getRewardPoints_Test(@Body hashmap: HashMap<String, String>): RewardPointsRes

    @POST("managers/send-created-challenge-on-whatsapp/")
    fun sendChallengeToWhatsapp(@Body hashmap: HashMap<String, Any>): Call<JsonObject>

    @POST("managers/send-created-campaign-on-whatsapp/")
    fun sendCampaignToWhatsapp(@Body hashmap: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/customer-reward-resources/")
    fun getCustomerRewardResource(@Body hashmap: HashMap<String, Any>): Call<JsonObject>

    @POST("managers/manager-reward-resources/")
    fun getManagerRewardResource(@Body hashmap: HashMap<String, Any>): Call<JsonObject>

    @POST("managers/team-wellbeing-top-three-lists/")
    fun teamWellbeingTopThreeList(@Body hashmap: HashMap<String, Any>): Call<JsonObject>

    @POST("customers/save-heart-points/")
    fun updateHeartPoints(@Body hashmap: HashMap<String, Any>): Call<JsonObject>

    @POST("dragonadmin/org-baselocation-list-data/")
    fun getBaseLocation(@Body hashmap: HashMap<String, Any>): Call<JsonObject>

    @POST("managers/performance-trend-chart/")
    fun performanceTrendChart(@Body hashmap: HashMap<String, Any>): Call<JsonObject>

    @POST("accounts/update-user-image/")
    fun uploadProfileImage(@Body requestBody: RequestBody): Call<JsonObject>

    @POST("customers/unread-notification-count/")
    @FormUrlEncoded
    suspend fun getNotificationCount(@Field("user_id") dsd: Int): Response<UnreadNotificationCount>

    @POST("managers/team-josh-reason-pie-chart-lists/")
    fun reasonPieChartData(@Body hashmap: HashMap<String, Any>): Call<JsonObject>

    @POST("rewardadmin/list-terms-and-conditions/")
    fun getTermAndCondition(): Call<JsonObject>

    @POST("rewardadmin/list_privacy_policy/")
    fun getPrivacyPolicy(): Call<JsonObject>

}