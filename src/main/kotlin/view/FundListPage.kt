package view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import model.Fund

@ExperimentalFoundationApi
@Composable
fun FundList(funds: SnapshotStateList<Fund>, onDeleteItemClicked: (item: Fund) -> Unit = {}) {
    Box {
        val state = rememberLazyListState()
        LazyColumnFor(funds, state = state) { p ->
            FundItem(p, onDeleteItemClicked = onDeleteItemClicked)
        }
        VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(state, funds.size, averageItemSize = 25.dp)
        )
    }
}

/**
 * 一个基金项
 * @param fund 指定的基金信息
 */
@Composable
fun FundItem(
        fund: Fund,
        onDeleteItemClicked: (item: Fund) -> Unit = {}
) {
    Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
    ) {
        val image = if (fund.gszzl > 0) {
            if (fund.gszzl >= 2) imageResource("up.png")
            else imageResource("up1.png")
        } else {
            if (fund.gszzl <= -2) imageResource("down.png")
            else imageResource("down1.png")
        }
        Image(image)
        Spacer(Modifier.preferredWidth(1.dp))
        Text("${if (fund.gszzl >= 0) "+" else ""}${fund.gszzl}%")
        Spacer(Modifier.preferredWidth(3.dp))
        Text(AnnotatedString(fund.shortName), overflow = TextOverflow.Clip, modifier = Modifier.clickable {  })
        Box(
                modifier = Modifier.fillMaxWidth(),
                alignment = Alignment.CenterEnd,
        ) {
            IconButton(onClick = { onDeleteItemClicked(fund) },
            ) {
                Icon(Icons.Filled.Delete)
            }
        }
    }
}
