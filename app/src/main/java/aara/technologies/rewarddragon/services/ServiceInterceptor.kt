package aara.technologies.rewarddragon.services

import aara.technologies.rewarddragon.utils.SharedPrefManager
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.http2.ConnectionShutdownException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class ServiceInterceptor(token: String?) : Interceptor {

    private var token_: String? = token;

    private val TAG = "ServiceInterceptor"
    lateinit var sharedPrefManager:SharedPrefManager

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        Log.i(TAG, "intercept:token $token_")

        try {
            if (request.header("No-Authentication") == null) {
                //val token = getTokenFromSharedPreference();
                //or use Token Function
                if (!token_.isNullOrEmpty()) {
                    request = request.newBuilder()
                        .addHeader("Authorization", token_)
                        .build()
                }
            }

            return chain.proceed(request)
        } catch (e: Exception) {

            var msg = ""
            var code: Int
            when (e) {
                is SocketTimeoutException -> {
                    code = 502
                    msg = "Timeout - Please check your internet connection"
                }
                is UnknownHostException -> {
                    code = 502
                    msg = "Unable to make a connection. Please check your internet"
                }
                is ConnectionShutdownException -> {
                    code = 502
                    msg = "Connection shutdown. Please check your internet"
                }
                is IOException -> {
                    code = 503
                    msg = "Server is unreachable, please try again later."
                }
                is IllegalStateException -> {
                    code = 999
                    msg = "${e.message}"
                }
                else -> {
                    code = 999
                    msg = "${e.message}"
                }
            }

            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message(msg)
                .body(ResponseBody.create(null, "{${e}}")).build()

        }


    }

}
