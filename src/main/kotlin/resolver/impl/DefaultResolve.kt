package resolver.impl

import resolver.IResolve

class DefaultResolve : IResolve<String> {
    /**
     * 将 [content] 解析为 [T] 实例
     */
    override fun resolve(content: String): String {
        return content
    }
}