package resolver.impl

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import model.Fund
import model.Response
import resolver.IResolve

/**
 * 基金信息解析器
 */
class FundResolve : IResolve<List<Fund>> {
    /**
     * 将 [content] 解析为 [Fund] 实例
     */
    override fun resolve(content: String): List<Fund> {
        val response = JSON.parseObject(content, object : TypeReference<Response<Fund>>() {})
        return response.datas
    }
}