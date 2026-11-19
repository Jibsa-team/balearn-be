package com.jipsa.balearn.domain.chat

import org.springframework.stereotype.Component
import java.sql.Connection
import javax.sql.DataSource

@Component
class ChatMaintenance(private val dataSource: DataSource) {
    fun withLock(work: () -> Unit) {
        dataSource.connection.use { connection ->
            if (!connection.lockResult("SELECT GET_LOCK('balearn:chat-maintenance', 0)")) return
            try {
                work()
            } finally {
                try {
                    check(connection.lockResult("SELECT RELEASE_LOCK('balearn:chat-maintenance')"))
                } catch (exception: Exception) {
                    // Invalidate a pooled connection that may still hold the lock.
                    connection.abort(Runnable::run)
                    throw exception
                }
            }
        }
    }

    private fun Connection.lockResult(sql: String): Boolean = createStatement().use { statement ->
        statement.executeQuery(sql).use { result -> result.next() && result.getInt(1) == 1 }
    }
}
