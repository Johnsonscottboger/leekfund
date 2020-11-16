package resolver


/**
 * 解析器
 */
interface IResolve<T : Any> {

    /**
     * 将 [content] 解析为 [T] 实例
     */
    fun resolve(content: String): T
}