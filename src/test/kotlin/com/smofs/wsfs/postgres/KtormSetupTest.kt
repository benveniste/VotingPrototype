package com.smofs.wsfs.postgres

import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.ktorm.database.Database
import org.ktorm.database.asIterable

class KtormSetupTest {
    private val logger = KotlinLogging.logger {}

    @Test
    fun setup() {
        val database = assertDoesNotThrow { Database.connect(WSFSDataSource().getDataSource()) }
        val sql = "SELECT * FROM persons"
        database.useConnection { conn ->
            conn.prepareStatement(sql).use { statement ->
                statement.executeQuery().asIterable().forEach { resultSet ->
                    logger.info { resultSet.getString("surname") }
                }
            }
        }
    }
}
