package aara.technologies.rewarddragon.testing

import aara.technologies.rewarddragon.App
import aara.technologies.rewarddragon.services.ServiceInterceptor
import aara.technologies.rewarddragon.utils.Constant
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource {


    private var client: OkHttpClient =
        OkHttpClient.Builder().addInterceptor(ServiceInterceptor(App.sharedPrefManager.user.token))
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build()

    private val TAG = "RetrofitInstance"


    fun <Api> buildApi(
        api: Class<Api>
    ): Api {
        return Retrofit
            .Builder()
            .client(client)
            .baseUrl(Constant.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            // we need to add converter factory to
            // convert JSON object to Java object
            .build()
            .create(api)
    }


}