package aara.technologies.rewarddragon.services

import aara.technologies.rewarddragon.App.sharedPrefManager
import aara.technologies.rewarddragon.utils.Constant.baseUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitInstance {

    private var client: OkHttpClient =
        OkHttpClient.Builder().addInterceptor(ServiceInterceptor(sharedPrefManager.user.token))
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build()

    private val TAG = "RetrofitInstance"

    fun getInstance(): Retrofit {
        return Retrofit
            .Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            // we need to add converter factory to
            // convert JSON object to Java object
            .build()
    }


}