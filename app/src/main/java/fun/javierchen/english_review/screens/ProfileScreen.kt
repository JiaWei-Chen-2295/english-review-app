package `fun`.javierchen.english_review.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import `fun`.javierchen.english_review.R
import `fun`.javierchen.english_review.activities.SettingActivity
import `fun`.javierchen.english_review.components.HeatMap
import `fun`.javierchen.english_review.model.HeatMapCell

/**
 * 扩展函数-跳转设置页面
 */
private fun navigateToSettings(context: Context) {
    val intent = Intent(context, SettingActivity::class.java)
    context.startActivity(intent)
}

@Composable
fun ProfileScreen() {
    // 模拟用户数据
    val userName = remember { "Javier Chen" }
    val learningDays = remember { 128 }
    val masteredWords = remember { 3850 }

    /**
     * 掌握的语块
     */
    val masteredWordsInfo = remember { 20 }
    val accuracy = remember { 92 }
    val dailyGoal = remember { "30 分钟" }
    val avatarUrl =
        remember { "https://profile-avatar.csdnimg.cn/a7e8397ab14b4e07b0ee1e570638f9d3_javierchen_2295.jpg!1" } // 使用本地资源更佳

    // 模拟图表数据
    val fakeChartData = remember {
        listOf(4.2f, 5.1f, 3.8f, 4.9f, 5.5f, 6.0f, 4.7f)
    }

    val heatMapData: List<HeatMapCell> = listOf(
        HeatMapCell(5, 0, 0),
        HeatMapCell(3, 1, 0),
        HeatMapCell(8, 2, 0),
        HeatMapCell(2, 3, 0),
        HeatMapCell(7, 4, 0),
        HeatMapCell(1, 5, 0),
        HeatMapCell(4, 6, 0),
        HeatMapCell(6, 0, 1),
        HeatMapCell(9, 1, 1),
        HeatMapCell(0, 2, 1),
        HeatMapCell(3, 3, 1),
        HeatMapCell(5, 4, 1),
        HeatMapCell(2, 5, 1),
        HeatMapCell(7, 6, 1),

        HeatMapCell(6, 0, 2),
        HeatMapCell(9, 1, 2),
        HeatMapCell(3, 2, 2),
        HeatMapCell(3, 3, 2),
        HeatMapCell(4, 4, 2),
        HeatMapCell(0, 5, 2),
        HeatMapCell(7, 6, 2),

        HeatMapCell(6, 0, 3),
        HeatMapCell(5, 1, 3),
        HeatMapCell(3, 2, 3),
        HeatMapCell(3, 3, 3),
        HeatMapCell(0, 4, 3),
        HeatMapCell(2, 5, 3),
        HeatMapCell(4, 6, 3),


        HeatMapCell(6, 0, 4),
        HeatMapCell(5, 1, 4),
        HeatMapCell(3, 2, 4),
        HeatMapCell(3, 3, 4),
        HeatMapCell(0, 4, 4),
        HeatMapCell(2, 5, 4),
        HeatMapCell(4, 6, 4),

        HeatMapCell(6, 0, 5),
        HeatMapCell(9, 1, 5),
        HeatMapCell(0, 2, 5),
        HeatMapCell(3, 3, 5),
        HeatMapCell(5, 4, 5),
        HeatMapCell(2, 5, 5),
        HeatMapCell(7, 6, 5),

    )

    fun generateFullHeatMapData(rawData: List<HeatMapCell>): List<HeatMapCell> {
        val maxWeek = rawData.maxOfOrNull { it.weekNumber } ?: 0
        return (0..maxWeek).flatMap { week ->
            (0..6).map { day ->
                rawData.firstOrNull { it.weekNumber == week && it.dayOfWeek == day }
                    ?: HeatMapCell((0..6).random(), day, week)
            }
        }
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)

    ) {

        // 用户信息
        item {
            UserProfileSection(avatarUrl, userName, learningDays)
        }

        // 展示数据卡片
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                InfoCard(
                    title = "掌握语块",
                    value = masteredWordsInfo.toString(),
                    unit = "个",
                    modifier = Modifier.weight(1f)
                )
                InfoCard(
                    title = "掌握词汇",
                    value = masteredWords.toString(),
                    unit = "词",
                    modifier = Modifier.weight(1f)
                )
                InfoCard(
                    title = "正确率",
                    value = accuracy.toString(),
                    unit = "%",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 热力图
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "📈 学习热力图",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    ChartView(data = generateFullHeatMapData(heatMapData))
                }
            }

        }

        // 最近学习内容
        item {
            RecentActivities(
                words = listOf("Ambiguous", "Phenomenon", "Quintessential", "Ephemeral")
            )
        }

        item {
            val context = LocalContext.current
            Card(onClick = { navigateToSettings(context) },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Settings, contentDescription = "Setting")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "设置",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ))
                }
            }
        }
        item{
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(text = "© 2025 Javier Chen")
            }

        }

    }
}

@Composable
private fun UserProfileSection(avatarUrl: String, userName: String, learningDays: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "用户头像",
            error = painterResource(R.drawable.default_avatar), // 添加错误占位图
            placeholder = painterResource(R.drawable.loading_avatar), // 添加加载占位图
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    shape = CircleShape
                )
        )

        Column(Modifier.padding(start = 16.dp)) {
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "📅 已坚持学习 $learningDays 天",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

}

@Composable
private fun InfoCard(title: String, value: String, unit: String, modifier: Modifier) {
    Card(
        modifier = Modifier
            .height(120.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$title ($unit)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
private fun ChartView(data: List<HeatMapCell>) {
    HeatMap(
        heatMapData = data,
        modifier = Modifier.fillMaxWidth(),
        cellSize = 35.dp,
        spacing = 6.dp
    )
}

@Composable
private fun RecentActivities(words: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "✨ 最近学习",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(Modifier.height(12.dp))
            words.forEachIndexed { index, word ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.width(24.dp)
                    )
                    Text(
                        text = word,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

