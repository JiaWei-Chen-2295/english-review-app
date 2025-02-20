package `fun`.javierchen.english_review.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage // 网络图片组件
import `fun`.javierchen.english_review.R


@Composable
fun ProfileScreen() {
    // 模拟用户数据
    val userName = remember { "Javier Chen" }
    val learningDays = remember { 128 }
    val masteredWords = remember { 3850 }
    val accuracy = remember { 92 }
    val dailyGoal = remember { "30 分钟" }
    val avatarUrl = remember { "https://profile-avatar.csdnimg.cn/a7e8397ab14b4e07b0ee1e570638f9d3_javierchen_2295.jpg!1" } // 使用本地资源更佳

    // 模拟图表数据
    val fakeChartData = remember {
        listOf(4.2f, 5.1f, 3.8f, 4.9f, 5.5f, 6.0f, 4.7f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // 用户信息头
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

        Spacer(modifier = Modifier.height(24.dp))

        // 数据卡片组
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
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

        Spacer(modifier = Modifier.height(24.dp))

        // 学习时间图表
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "本周学习时长",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                ChartView(data = fakeChartData)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 最近学习
        RecentActivities(
            words = listOf("Ambiguous", "Phenomenon", "Quintessential", "Ephemeral")
        )
    }
}

@Composable
private fun InfoCard(title: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ChartView(data: List<Float>) {
    val maxValue = data.maxOrNull() ?: 0f
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
    ) {
        // 绘制折线图逻辑
    }
}

@Composable
private fun RecentActivities(words: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "最近学习",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            words.forEach { word ->
                Text(
                    text = word,
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
