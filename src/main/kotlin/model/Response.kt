package model

/**
 * 响应
 */
data class Response<T : Any>(
        val success: Boolean,
        val errMsg: String?,
        val datas: List<T>
)