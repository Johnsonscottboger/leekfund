package database

import database.impl.FundInfoRepository
import database.impl.FundRepository
import entity.Fund
import entity.FundInfo

interface IRepository<T> {
    fun initialize()

    fun getAll(): List<T>

    fun insert(entity: T)

    fun delete(id: Int)

    fun delete(whereClause: String, vararg params: Any)

    companion object {
        fun fund(): IRepository<Fund> {
            return FundRepository()
        }

        fun fundInfo() : IRepository<FundInfo> {
            return FundInfoRepository()
        }
    }
}