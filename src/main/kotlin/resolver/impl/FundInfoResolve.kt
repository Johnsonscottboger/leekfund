package resolver.impl

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import model.FundInfo
import resolver.IResolve

/**
 * 基金信息解析器
 */
class FundInfoResolve : IResolve<List<FundInfo>> {
    /**
     * 将 [content] 解析为 [T] 实例
     */
    override fun resolve(content: String): List<FundInfo> {
        val startIndex = content.indexOf('[')
        val json = content.substring(startIndex, content.length - 1)
        val response = JSON.parseObject(json, object : TypeReference<Array<Array<String>>>() {})
        val list = mutableListOf<FundInfo>()
        for (items in response) {
            list.add(FundInfo(items[0], items[1], items[2], items[3], items[4]))
        }
        return list
    }
}