package view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.CoreTextField
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import database.IRepository
import model.FundInfo

@ExperimentalFoundationApi
@Composable
fun FundSearch(
        onItemClicked: (FundInfo) -> Unit = {},
        onCancelClicked: () -> Unit
) {
    val repository = IRepository.fundInfo()
    repository.initialize()
    val all = repository.getAll()

    Column(modifier = Modifier.fillMaxWidth()) {
        val keyword = remember { mutableStateOf("") }
        val list = mutableListOf(*all.filter { p -> p.code.contains(keyword.value) or p.name.contains(keyword.value) }.toTypedArray())
        KeywordInput(
                onTextChanged = {
                    keyword.value = it
                },
                onCancelClicked = onCancelClicked
        )
        FundInfoList(list, onItemClicked)
    }
}

@Composable
private fun KeywordInput(
        onTextChanged: (String) -> Unit,
        onCancelClicked: () -> Unit,
) {
    val text = remember { mutableStateOf("") }
    Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
                value = text.value,
                onValueChange = { p ->
                    text.value = p
                    onTextChanged(p)
                },
                modifier = Modifier.padding(8.dp)
        )
        Button(onClick = onCancelClicked) {
            Text("取消")
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun FundInfoList(list: MutableList<entity.FundInfo>, onItemClicked: (FundInfo) -> Unit) {
    Box {
        val state = rememberLazyListState()
        LazyColumnFor(list, state = state) { p ->
            Text(
                    "${p.code}|${p.name}|${p.type}",
                    modifier = Modifier.clickable { onItemClicked(FundInfo(p.code, p.alphabet, p.name, p.type, p.pinyin)) }.padding(8.dp).fillMaxWidth(),
                    overflow = TextOverflow.Ellipsis
            )
        }
        VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(state, list.size, averageItemSize = 25.dp)
        )
    }
}