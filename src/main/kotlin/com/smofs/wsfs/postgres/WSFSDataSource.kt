package com.smofs.wsfs.postgres

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import mu.KotlinLogging
import org.bouncycastle.jce.provider.BouncyCastleProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest
import java.security.Security
import java.util.Base64
import javax.sql.DataSource

private const val POOL_SIZE = 10

class WSFSDataSource {
    companion object {
        private val logger = KotlinLogging.logger {}
        private val mapper = jacksonObjectMapper()
        private val mapTypeRef: TypeReference<Map<String, String>> = object : TypeReference<Map<String, String>>() {}

        init {
            Security.addProvider(BouncyCastleProvider())
        }
    }

    fun getSecret(secretName: String?): String {
        val client = SecretsManagerClient.builder().region(Region.US_EAST_1).build()
        val request = GetSecretValueRequest.builder().secretId(secretName).build()
        val value = client.getSecretValue(request).secretString()
        return value
    }

    private fun get(name: String): DataSource {
        val credMap = mapper.readValue(getSecret("WSFS-Database"), mapTypeRef)

        logger.debug { credMap }

        val config = HikariConfig()
        config.jdbcUrl = "jdbc:postgresql://${credMap["host"]}:${credMap["port"]}/${credMap[name]}"
        config.username = credMap["username"]
        config.password = credMap["password"]
        config.driverClassName = "org.postgresql.Driver"
        config.maximumPoolSize = POOL_SIZE // Everybody into the pool

        return HikariDataSource(config)

    }

    fun getDataSource(): DataSource {
        return get("dbname")
    }

    fun getTestDataSource(): DataSource {
        return get("testdbname")
    }

    fun getAesSecretKey(): ByteArray {
        val credMap = mapper.readValue(getSecret("WSFS-Database"), mapTypeRef)
        return Base64.getDecoder().decode(credMap["aesBase64"])
    }
}
