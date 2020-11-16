import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.Window
import androidx.compose.desktop.WindowEvents
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import api.ApiManager
import api.EastmoneyApi
import database.IRepository
import model.Fund
import view.FundList
import view.FundSearch
import java.awt.image.BufferedImage
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO

@ExperimentalFoundationApi
fun main() {
    val desc = mutableStateOf(true)
    val showSearch = mutableStateOf(false)
    val fundList = mutableStateListOf(
            Fund("000000", "暂无数据", 0.0, 0.0, 0.0, LocalDateTime.now()),
    )
    Window(
            title = "韭菜基金",
            size = IntSize(300, 500),
            events = WindowEvents(
                    onOpen = {
                        val codes = getCodes()
                        refresh(codes, fundList)
                    }
            )
    ) {

        DesktopMaterialTheme {
            if(!showSearch.value) {
                Scaffold(Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                    title = {
                                        Text("Leek Fund")
                                    },
                                    actions = {
                                        IconButton(onClick = {
                                            showSearch.value = true
                                        }) { Icon(Icons.Filled.Add) }
                                        IconButton(onClick = {
                                            if (fundList.isNotEmpty())
                                                fundList.sortByDescending { p -> if (desc.value) -p.navChgRt else p.navChgRt }
                                            desc.value = !desc.value
                                        }) {
                                            Icon(if (desc.value) {
                                                Icons.Filled.KeyboardArrowDown
                                            } else {
                                                Icons.Filled.KeyboardArrowUp
                                            })
                                        }
                                    }
                            )
                        },
                        floatingActionButtonPosition = FabPosition.End,
                        floatingActionButton = {
                            FloatingActionButton(
                                    onClick = {
                                        val codes = getCodes()
                                        refresh(codes, fundList)
                                    }) {
                                Icon(Icons.Filled.Refresh)
                            }
                        }) {
                    Column(modifier = Modifier.padding(5.dp, 10.dp)) {
                        FundList(fundList,
                                onDeleteItemClicked = { fund ->
                                    deleteFundItem(fundList, fund)
                                })
                    }
                }
            }
            else {
                FundSearch(onItemClicked = {p ->
                    showSearch.value = false
                    insert(p.code)
                    val codes = getCodes()
                    refresh(codes, fundList)
                }, onCancelClicked = { showSearch.value = false })
            }
        }
    }
}

/**
 * 获取基金编码
 */
fun getCodes(): List<String> {
    val repository = IRepository.fund()
    val entities = repository.getAll()
    return entities.map { p -> p.code }
}

/**
 * 刷新
 */
fun refresh(codes: List<String>, fundList: MutableList<Fund>) {
    fundList.clear()
    val api = ApiManager.getInstance<EastmoneyApi>()
    val funds = api.funds(codes.joinToString(","))
    fundList.addAll(funds)
}

/**
 * 删除
 */
fun deleteFundItem(fundList: MutableList<Fund>, fund: Fund) {
    val repository = IRepository.fund()
    repository.delete("code = ?", "'${fund.fcode}'")
    fundList.removeIf { p -> p.fcode == fund.fcode }
}

/**
 * 新增
 */
fun insert(code: String) {
    val repository = IRepository.fund()
    repository.insert(entity.Fund(code = code))
}