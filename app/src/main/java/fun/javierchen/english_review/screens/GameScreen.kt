package `fun`.javierchen.english_review.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import `fun`.javierchen.english_review.R

@Composable
fun DiceRollerApp() {

    /**
     * 该程序使用的技术栈
     *  - 定义可组合函数。
     *  - 使用组合创建布局。
     *  - 使用 Button 可组合函数创建按钮。
     *  - 导入 drawable 资源。
     *  - 使用 Image 可组合函数显示图片。
     *  - 使用可组合函数构建交互式界面。
     *  - 使用 remember 可组合函数将组合中的对象存储到内存中。
     *  - 使用 mutableStateOf() 函数刷新界面以创建可观察对象。
     */

    // todo: 关于 Modifier 的重组
    DiceWithButtonAndImage(
        modifier =
        Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)

    )
}

// Modifier 是修饰和修改 Compose 组件行为的元素集合
@Composable
fun DiceWithButtonAndImage(modifier: Modifier = Modifier) {
    // 可观察对象
    var result by remember { mutableIntStateOf(1) }

    val imageResource = when (result) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }

    // 垂直布局 将形参中的 Modifier 导入
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(imageResource),
            contentDescription = result.toString()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { result = (1..6).random() }) {
            Text(stringResource(R.string.roll))
        }
    }
}