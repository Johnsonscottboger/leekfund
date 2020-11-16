package model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 基金信息
 * @property fcode 代码
 * @property shortName 名称
 * @property gszzl 日实际涨幅
 * @property nav 净值估算
 * @property navChgRt 净值估算涨幅
 * @property gzTime 更新时间
 */
data class Fund(
        val fcode: String,
        val shortName: String,
        val gszzl: Double,
        val nav: Double,
        val navChgRt: Double,
        val gzTime: LocalDateTime,
)