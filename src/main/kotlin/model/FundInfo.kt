package model

/**
 * 基金信息
 * @property code 代码
 * @property alphabet 首字母
 * @property name 名称
 * @property type 类型
 * @property pinyin 拼音
 */
data class FundInfo(
        val code: String,
        val alphabet: String,
        val name: String,
        val type: String,
        val pinyin: String
)