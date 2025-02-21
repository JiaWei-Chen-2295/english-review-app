package `fun`.javierchen.english_review.model

data class HeatMapCell(
    val value: Int, // 提交次数
    val dayOfWeek: Int, // 星期几 (0-6)
    val weekNumber: Int // 第几周
)