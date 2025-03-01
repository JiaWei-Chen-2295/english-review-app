package `fun`.javierchen.english_review.model.request

import `fun`.javierchen.english_review.model.Message

data class CompletionRequest(
    val model: String,
    val messages: List<Message>,
    val frequency_penalty: Double = 0.0,
    val max_tokens: Int = 2048,
    val presence_penalty: Double = 0.0,
    val response_format: ResponseFormat = ResponseFormat(),
    val stream: Boolean = false,
    val temperature: Double = 1.0,
    val top_p: Double = 1.0,
    val tools: List<Any>? = null,
    val tool_choice: String? = "none",
    val logprobs: Boolean = false,
    val top_logprobs: Int? = null
)

data class ResponseFormat(
    val type: String = "text"
)