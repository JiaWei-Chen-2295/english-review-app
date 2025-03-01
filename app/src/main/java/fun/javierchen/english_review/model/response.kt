package `fun`.javierchen.english_review.model.response // 注意保持包路径一致

import com.google.gson.annotations.SerializedName
import `fun`.javierchen.english_review.model.Message

data class CompletionResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("object")
    val objectType: String,
    @SerializedName("created")
    val created: Long,
    @SerializedName("model")
    val model: String,
    @SerializedName("choices")
    val choices: List<Choice>,
    @SerializedName("usage")
    val usage: Usage
) {
    data class Choice(
        @SerializedName("index")
        val index: Int,
        @SerializedName("message")
        val message: Message,
        @SerializedName("finish_reason")
        val finishReason: String
    )

    data class Usage(
        @SerializedName("prompt_tokens")
        val promptTokens: Int,
        @SerializedName("completion_tokens")
        val completionTokens: Int,
        @SerializedName("total_tokens")
        val totalTokens: Int
    )
}
