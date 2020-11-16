package database.impl

import api.ApiManager
import api.EastmoneyApi
import database.IRepository
import entity.FundInfo
import java.sql.DriverManager
import java.time.Duration
import java.time.LocalDateTime

class FundInfoRepository : IRepository<FundInfo> {
    private val connection
        get() = DriverManager.getConnection("jdbc:sqlite:leekfund.db")

    override fun initialize() {
        connection.use {
            val statement = it.createStatement().apply {
                queryTimeout = 30
            }
            val rs = statement.executeQuery("select count(1) as count from sqlite_master where type = 'table' and name = 'fund_info'")
            while (rs.next()) {
                val count = rs.getInt("count")
                if (count > 0) return
            }
            statement.executeUpdate("""create table fund_info (id integer primary key autoincrement , code text, alphabet text, name text,type text, pinyin text)""");
        }
        initializeLog()
        //查询最近更新时间, 如果有超过一个月就重新查询数据, 并更新数据库
        connection.use {
            val statement = connection.createStatement()
            val rs = statement.executeQuery("select datetime from log order by datetime desc limit 0,1")
            while (rs.next()) {
                val datetimeStr = rs.getString("datetime")
                val dateTime = LocalDateTime.parse(datetimeStr)
                val now = LocalDateTime.now()
                val duration = Duration.between(now, dateTime)
                if (duration.toDays() <= 30)
                    return
            }
        }

        val api = ApiManager.getInstance<EastmoneyApi>()
        val fundInfos = api.all()
        connection.use {
            val statement = connection.createStatement()
            statement.executeUpdate("delete from fund_info")

            val preparedStatement = connection.prepareStatement("""insert into fund_info (code, alphabet, name, type, pinyin) 
                values (?, ?, ?, ?, ?)
            """.trimMargin())
            connection.autoCommit = false
            fundInfos.forEach { info ->
                preparedStatement.setString(1, info.code)
                preparedStatement.setString(2, info.alphabet)
                preparedStatement.setString(3, info.name)
                preparedStatement.setString(4, info.type)
                preparedStatement.setString(5, info.pinyin)
                preparedStatement.addBatch()
            }
            preparedStatement.executeBatch()
            connection.autoCommit = true

            val statement1 = connection.createStatement()
            statement1.executeUpdate("insert into log(datetime) values ('${LocalDateTime.now().toString()})'")
        }
    }

    private fun initializeLog() {
        connection.use {
            val statement = it.createStatement().apply {
                queryTimeout = 30
            }
            val rs = statement.executeQuery("select count(1) as count from sqlite_master where type = 'table' and name = 'log'")
            while (rs.next()) {
                val count = rs.getInt("count")
                if (count > 0) return
            }
            statement.executeUpdate("create table log (id integer primary key autoincrement, datetime text)")
        }
    }

    override fun getAll(): List<FundInfo> {
        connection.use {
            val statement = it.createStatement()
            val rs = statement.executeQuery("select id, code, alphabet, name, type, pinyin from fund_info")
            val result = mutableListOf<FundInfo>()
            while (rs.next()) {
                val id = rs.getInt("id")
                val code = rs.getString("code")
                val alphabet = rs.getString("alphabet")
                val name = rs.getString("name")
                val type = rs.getString("type")
                val pinyin = rs.getString("pinyin")
                result.add(FundInfo(id, code, alphabet, name, type, pinyin))
            }
            return result
        }
    }

    override fun insert(entity: FundInfo) {
        connection.use {
            val preparedStatement = it.prepareStatement("""insert into fund_info (code, alphabet, name, type, pinyin) 
                values (?, ?, ?, ?, ?)
            """.trimMargin())
            preparedStatement.setString(1, entity.code)
            preparedStatement.setString(2, entity.alphabet)
            preparedStatement.setString(3, entity.name)
            preparedStatement.setString(4, entity.type)
            preparedStatement.setString(5, entity.pinyin)
            preparedStatement.executeUpdate()
        }
    }

    override fun delete(id: Int) {
        connection.use {
            val preparedStatement = it.prepareStatement("delete from fund_info where id = ?")
            preparedStatement.setInt(1, id)
            preparedStatement.executeUpdate()
        }
    }

    override fun delete(whereClause: String, vararg params: Any) {
        connection.use {
            val clause = if (whereClause.startsWith("where", true)) whereClause
            else "where $whereClause"
            val preparedStatement = it.prepareStatement("delete from fund_info $clause")
            params.forEachIndexed { index, any ->
                preparedStatement.setObject(index + 1, any)
            }
            preparedStatement.executeUpdate()
        }
    }
}