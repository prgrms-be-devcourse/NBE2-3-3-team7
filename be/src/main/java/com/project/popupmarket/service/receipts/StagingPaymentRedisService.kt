package com.project.popupmarket.service.receipts

import com.project.popupmarket.entity.StagingPayment
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class StagingPaymentRedisService(
    private val template: RedisTemplate<String, Any>
) {
    private fun generateKey(orderId: String): String = "staging:$orderId"

    fun save(id: String, stagingPayment: StagingPayment) {
        val key = generateKey(id)
        template.opsForValue().set(key, stagingPayment, 5L, TimeUnit.MINUTES)
    }

    fun find(id: String): StagingPayment? {
        val key = generateKey(id)
        return template.opsForValue().get(key) as? StagingPayment
    }

    fun delete(id: String) {
        val key = generateKey(id)
        template.delete(key)
    }
}
