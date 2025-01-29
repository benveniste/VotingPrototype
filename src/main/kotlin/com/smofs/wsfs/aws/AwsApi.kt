package com.smofs.wsfs.aws

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest

object AwsApi {
    private val mapper = jacksonObjectMapper()
    private val mapTypeRef: TypeReference<Map<String, String>> = object : TypeReference<Map<String, String>>() {}
    private val client = SecretsManagerClient.builder().region(Region.US_EAST_1).build()

    fun getSecret(secretName: String): String {
        val request = GetSecretValueRequest.builder().secretId(secretName).build()
        return client.getSecretValue(request).secretString()
    }

    fun getSecretMap(secretName: String): Map<String, String> {
        return mapper.readValue(getSecret(secretName), mapTypeRef) ?: emptyMap()
    }
}
