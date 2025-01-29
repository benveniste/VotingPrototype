package com.smofs.wsfs.postgres

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.smofs.wsfs.aws.AwsApi
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import mu.KotlinLogging
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import java.util.Base64
import javax.sql.DataSource

private const val POOL_SIZE = 10

class WSFSDataSource {
    companion object {
        private val logger = KotlinLogging.logger {}

        init {
            Security.addProvider(BouncyCastleProvider())
        }
    }

    private fun get(name: String): DataSource {
        val credMap = AwsApi.getSecretMap("WSFS-Database")

        val config = HikariConfig()
        config.jdbcUrl = "jdbc:postgresql://${credMap["host"]}:${credMap["port"]}/${credMap[name]}"
        config.username = credMap["username"]
        config.password = credMap["password"]
        config.driverClassName = "org.postgresql.Driver"
        config.maximumPoolSize = POOL_SIZE // Everybody into the pool
        logger.info("Attempting to connect to ${config.jdbcUrl}")

        return HikariDataSource(config)

    }

    fun getDataSource(): DataSource {
        return get("dbname")
    }

    fun getTestDataSource(): DataSource {
        return get("testdbname")
    }

    fun getAesSecretKey(): ByteArray {
        val credMap = AwsApi.getSecretMap("WSFS-Database")
        return Base64.getDecoder().decode(credMap["aesBase64"])
    }
}
