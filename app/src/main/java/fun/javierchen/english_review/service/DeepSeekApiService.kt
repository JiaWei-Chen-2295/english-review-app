package `fun`.javierchen.english_review.service

import `fun`.javierchen.english_review.model.request.CompletionRequest
import `fun`.javierchen.english_review.model.response.CompletionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface DeepSeekApiService {
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"  // 新增Accept头
    )
    @POST("https://api.deepseek.com/chat/completions")
    suspend fun createCompletion(
        @Header("Authorization") auth: String,
        @Body request: CompletionRequest
    ): Response<CompletionResponse>
}