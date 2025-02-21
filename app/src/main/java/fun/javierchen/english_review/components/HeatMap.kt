package `fun`.javierchen.english_review.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import `fun`.javierchen.english_review.model.HeatMapCell

@Composable
fun HeatMap(
    heatMapData: List<HeatMapCell>,
    modifier: Modifier = Modifier,
    cellSize: Dp = 28.dp,
    spacing: Dp = 2.dp,
    showValues: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme
    val weeks = heatMapData.groupBy { it.weekNumber }.keys.sorted()
    val maxCount = heatMapData.maxOfOrNull { it.value } ?: 1
    val cornerRadius = 4.dp
    val elevation = 1.dp

    Column(modifier = modifier) {
        // 顶部星期标签
        Row(Modifier.padding(start = 32.dp)) {
            (0..6).forEach { day ->
                Box(
                    modifier = Modifier
                        .size(cellSize)
                        .padding(spacing),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (day) {
                            0 -> "一" 1 -> "二" 2 -> "三"
                            3 -> "四" 4 -> "五" 5 -> "六"
                            else -> "日"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Row {
            // 左侧周数标签
            Column(Modifier.width(32.dp)) {
                weeks.forEach { week ->
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .padding(spacing),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${week + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 热力图主体
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()  // 新增
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(spacing),
                horizontalArrangement = Arrangement.spacedBy(spacing)

            ) {
                items(heatMapData.sortedWith(compareBy(
                    { it.weekNumber },
                    { it.dayOfWeek }
                ))) { cell ->
                    HeatMapCell(
                        cell = cell,
                        size = cellSize,
                        cornerRadius = cornerRadius,
                        elevation = elevation,
                        maxCount = maxCount,
                        colorScheme = colorScheme,
                        showValues = showValues
                    )
                }
            }
        }
    }
}

@Composable
private fun HeatMapCell(
    cell: HeatMapCell,
    size: Dp,
    cornerRadius: Dp,
    elevation: Dp,
    maxCount: Int,
    colorScheme: ColorScheme,
    showValues: Boolean
) {
    Box(
        modifier = Modifier
            .size(size)
            .shadow(elevation, RoundedCornerShape(cornerRadius))
            .background(
                getColorForValue(cell.value, maxCount, colorScheme),
                RoundedCornerShape(cornerRadius)
            )
            .clip(RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center
    ) {
        if (showValues && cell.value > 0) {
            Text(
                text = cell.value.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = when {
                    // 根据背景亮度自动选择文字颜色
                    getColorForValue(cell.value, maxCount, colorScheme).luminance() > 0.4f ->
                        Color.Black.copy(alpha = 0.9f)
                    else ->
                        Color.White.copy(alpha = 0.95f)
                },
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.15f), // 半透明背景增强对比度
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            )
        }
    }
}

private fun getColorForValue(value: Int, maxCount: Int, colorScheme: ColorScheme): Color {
    val alpha = when {
        maxCount == 0 -> 0f
        value == 0 -> 0.1f  // 零值保留最小可见度
        else -> (value.toFloat() / maxCount).coerceIn(0.2f, 0.9f)
    }
    return lerp(
        colorScheme.surfaceVariant.copy(alpha = 0.7f),
        colorScheme.primary.copy(alpha = 0.9f),
        alpha
    ).run {
        // 添加颜色亮度调整
        if (alpha > 0.5f) this else this.copy(alpha = alpha * 1.2f)
    }
}


