package `fun`.javierchen.english_review.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import `fun`.javierchen.english_review.R
import `fun`.javierchen.english_review.components.HeatMap
import `fun`.javierchen.english_review.model.HeatMapCell


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
        HeatMapCell(7, 6, 1)
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
        item{
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
                        .padding(20.dp)
                        .fillMaxSize(),
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
    }
}
@Composable
private fun UserProfileSection(avatarUrl: String, userName: String, learningDays: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "用户头像",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            error = painterResource(R.drawable.default_avatar), // 添加错误占位图
            placeholder = painterResource(R.drawable.loading_avatar) // 添加加载占位图

        )

        Column {
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "已坚持学习 $learningDays 天",
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

