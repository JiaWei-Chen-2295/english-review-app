package `fun`.javierchen.english_review.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import `fun`.javierchen.english_review.activities.MainStudyViewModel
import `fun`.javierchen.english_review.model.TranslationExercise
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainStudyApp(viewModel: MainStudyViewModel = viewModel()) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()


    // 状态管理
    val isLoading by viewModel.isLoading.collectAsState()
    val translateText by viewModel.translateText.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val exercise by viewModel.translationExercise.collectAsState()
    var userAnswer by remember { mutableStateOf("") }

    Scaffold(
      topBar = {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "译学有道",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface, // 🌈 使用surface颜色
            titleContentColor = MaterialTheme.colorScheme.onSurface // 💡 提高对比度
        ),
        modifier = Modifier
            .height(64.dp)
            .padding(horizontal = 12.dp),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior() // 🎯 添加滚动响应
    )
}

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            AdaptiveTranslationCard(viewModel, isLoading, errorMessage, translateText, exercise)


        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AdaptiveTranslationCard(
    viewModel: MainStudyViewModel = viewModel(),
    isLoading: Boolean,
    errorMessage: String?,
    translateText: String,
    exercise: TranslationExercise?
) {


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp, max = 400.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                isLoading -> LoadingIndicator()
                errorMessage != null -> ErrorDisplay(errorMessage)
                else -> TranslationContent(translateText, exercise)
            }

            Button(
                onClick = {
                    viewModel.generateContent(
                        """
            生成一则符合JSON格式的四级翻译题，要求：
            - 包含以下字段：generated_time(ISO8601), question_type, chinese_paragraph, reference_words, english_answer
            - reference_words使用中文(英文)格式并用/分隔
            - 输出纯JSON不包含其他内容
            - 时间是 ${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}
            示例格式：
            {
                "generated_time": "${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}",
                "question_type": "因果分析型",
                "chinese_paragraph": "由于环保意识的提升，可降解包装材料在快递行业得到广泛应用",
                "reference_words": "碳足迹(carbon footprint)/可降解材料(biodegradable materials)/循环经济(circular economy)",
                "english_answer": "Due to the enhancement of environmental awareness, biodegradable packaging materials have been widely used in the express delivery industry."
            }
""".trimIndent()
                    )
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("获取新题目")
            }
        }
    }
}


@Composable
private fun LoadingIndicator() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "题目加载中...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorDisplay(message: String?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "错误图标",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(32.dp)
        )
        Text(
            "题目加载失败",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = message ?: "未知错误",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TranslationContent(content: String, exercise: TranslationExercise?) {
    var showAnswer by remember { mutableStateOf(false) }
    val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 标题部分
        Text(
            text = "翻译练习",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        exercise?.let {
            // 元信息卡片
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 🗳️ 时间显示优化
                    InfoRow(
                        icon = Icons.Default.DateRange,
                        text = "生成时间：${it.generatedTime.format(timeFormatter)}",
                        caption = true
                    )

                    // 🗳️ 题目类型徽章
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = it.questionType,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // 题目卡片
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = it.chineseParagraph,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp, // 🗳️ 优化行高
                        textAlign = TextAlign.Start
                    )

                    // 🗳️ 参考词汇分割线
                    Divider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    ReferenceWordsSection(it.referenceWords)
                }
            }

            // 答案切换按钮
            FilledTonalButton(
                onClick = { showAnswer = !showAnswer },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Icon(
                    imageVector = if (showAnswer) Icons.Default.FavoriteBorder else Icons.Default.Favorite,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(if (showAnswer) "隐藏参考答案" else "查看参考答案")
            }

            // 答案卡片动画
            AnimatedVisibility(
                visible = showAnswer,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                AnswerCard(it.englishAnswer)
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String, caption: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = if (caption) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReferenceWordsSection(words: String) {
    val wordList = words.split("/")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "参考词汇：",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow {
            wordList.forEach { word ->
                val parts = word.split("(")
                val chinese = parts[0]
                val english = parts[1].removeSuffix(")")

                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                    ) {
                        Text(
                            text = chinese,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "($english)",
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnswerCard(answer: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(160.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = answer,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Justify
            )
        }
    }
}
