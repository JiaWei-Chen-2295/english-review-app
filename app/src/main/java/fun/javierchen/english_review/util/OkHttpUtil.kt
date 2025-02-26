package `fun`.javierchen.english_review.util

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

// 单例的 OkHttpClient
object HttpSingleton {
    // 双重校验锁单例
    val client: OkHttpClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}