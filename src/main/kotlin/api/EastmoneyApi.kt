package api

import annotation.Client
import annotation.Param
import annotation.Request
import enum.RequestMethod
import model.Fund
import model.FundInfo
import resolver.impl.FundInfoResolve
import resolver.impl.FundResolve

/**
 * 东方财富网接口
 */
@Client("https://fundmobapi.eastmoney.com/FundMNewApi")
interface EastmoneyApi {
    /**
     * 获取基金信息
     */
    @Request(RequestMethod.GET, "/FundMNFInfo?pageIndex=1&pageSize=100&appType=ttjj&product=EFund&plat=Android&deviceid=1f5b7fe5e2742t8fb63pq7co&Version=1&Fcodes={codes}", resolver = FundResolve::class)
    fun funds(@Param("codes") codes: String): List<Fund>

    /**
     * 获取所有基金信息
     */
    @Request(RequestMethod.GET, "http://fund.eastmoney.com/js/fundcode_search.js", resolver = FundInfoResolve::class)
    fun all() : List<FundInfo>
}