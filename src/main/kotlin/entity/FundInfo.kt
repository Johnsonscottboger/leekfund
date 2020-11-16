package entity

/**
 * 基金信息实体
 */
data class FundInfo(
        val id: Int? = null,
        val code: String,
        val alphabet: String,
        val name: String,
        val type: String,
        val pinyin: String,
)