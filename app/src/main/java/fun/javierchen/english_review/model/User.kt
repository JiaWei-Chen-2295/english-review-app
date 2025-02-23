package `fun`.javierchen.english_review.model

/**
 * 用户数据类
 */
data class User(
    val id: Long,
    val username: String,
    val email: String,
    val avatar: String?
)