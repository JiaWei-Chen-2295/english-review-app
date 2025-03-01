package `fun`.javierchen.english_review.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class TranslationExercise(
    @SerializedName("generated_time")
    val generatedTime: LocalDateTime,
    @SerializedName("question_type")
    val questionType: String,
    @SerializedName("chinese_paragraph")
    val chineseParagraph: String,
    @SerializedName("reference_words")
    val referenceWords: String,
    @SerializedName("english_answer")
    val englishAnswer: String
) {
    // 重写toString实现特定输出格式
    @RequiresApi(Build.VERSION_CODES.O)
    override fun toString(): String = """
        生成时间 —— ${generatedTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}
        题目类型 —— $questionType
        题目 —— $chineseParagraph
        参考词 —— $referenceWords
        参考答案 —— $englishAnswer
    """.trimIndent()

    // 验证逻辑扩展
    fun validate(): Boolean {
        return chineseParagraph.length in 15..50 &&
                referenceWords.split("/").size == 3 &&
                englishAnswer.split(" ").size >= 10
    }

    companion object {
       @RequiresApi(Build.VERSION_CODES.O)
       val build:Gson = GsonBuilder()
           .registerTypeAdapter(LocalDateTime::class.java, object :
               JsonDeserializer<LocalDateTime> {
               override fun deserialize(
                   json: JsonElement,
                   type: Type?,
                   context: JsonDeserializationContext?
               ): LocalDateTime = LocalDateTime.parse(
                   json.asString,
                   DateTimeFormatter.ISO_LOCAL_DATE_TIME
               )
           }).create()
    }
}
