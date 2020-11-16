package database.impl

import database.IRepository
import entity.Fund
import java.sql.DriverManager

class FundRepository : IRepository<Fund> {
    private val connection
        get() = DriverManager.getConnection("jdbc:sqlite:leekfund.db")

    override fun initialize() {
        connection.use {
            val statement = it.createStatement().apply {
                queryTimeout = 30
            }
            val rs = statement.executeQuery("select count(1) as count from sqlite_master where type = 'table' and name = 'fund'")
            while (rs.next()) {
                val count = rs.getInt("count")
                if (count > 0) return
            }
            statement.executeUpdate("create table fund (id integer primary key autoincrement , code string)");
        }
    }

    override fun getAll(): List<Fund> {
        connection.use {
            val statement = it.createStatement()
            val rs = statement.executeQuery("select id, code from fund");
            val result = mutableListOf<Fund>()
            while (rs.next()) {
                val id = rs.getInt("id")
                val code = rs.getString("code")
                result.add(Fund(id, code.substring(1, code.length - 1)))
            }
            return result
        }
    }

    override fun insert(entity: Fund) {
        connection.use {
            val preparedStatement = it.prepareStatement("insert into fund (code) values (?)")
            preparedStatement.setString(1, "'${entity.code}'")
            preparedStatement.executeUpdate()
        }
    }

    override fun delete(id: Int) {
        connection.use {
            val preparedStatement = it.prepareStatement("delete from fund where id = ?")
            preparedStatement.setInt(1, id)
            preparedStatement.executeUpdate()
        }
    }

    override fun delete(whereClause: String, vararg params: Any) {
        connection.use {
            val clause = if (whereClause.startsWith("where", true)) whereClause
            else "where $whereClause"
            val preparedStatement = it.prepareStatement("delete from fund $clause")
            params.forEachIndexed { index, any ->
                preparedStatement.setObject(index + 1, any)
            }
            preparedStatement.executeUpdate()
        }
    }
}