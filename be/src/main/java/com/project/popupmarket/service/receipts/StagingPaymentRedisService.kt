package com.project.popupmarket.service.receipts

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.project.popupmarket.entity.StagingPayment
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class StagingPaymentRedisService(
    private val template: RedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper
) {
    private fun generateKey(orderId: String): String = "staging:$orderId"

    fun save(id: String, payment: StagingPayment) {
        val key = generateKey(id)
        template.opsForValue().set(key, payment, 10L, TimeUnit.MINUTES)
    }

    fun find(id: String): StagingPayment? {
        val key = generateKey(id)
        val rawData = template.opsForValue().get(key) ?: return null

        val jsonString = objectMapper.writeValueAsString(rawData)
        return objectMapper.readValue(jsonString, object : TypeReference<StagingPayment>() {})
    }

    fun delete(id: String) {
        val key = generateKey(id)
        template.delete(key)
    }
}
