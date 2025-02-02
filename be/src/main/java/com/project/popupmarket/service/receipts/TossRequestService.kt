package com.project.popupmarket.service.receipts

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.popupmarket.config.TossPaymentConfig
import com.project.popupmarket.dto.payment.TossPaymentTO
import com.project.popupmarket.exception.custom.PaymentException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.util.*

@Service
class TossRequestService(
    private val tossPaymentConfig: TossPaymentConfig,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(TossRequestService::class.java)

    fun requestPayment(payment: TossPaymentTO): TossPaymentTO {
        val encodeKey = Base64.getEncoder()
            .encodeToString("${tossPaymentConfig.testSecreteKey}:".toByteArray(StandardCharsets.UTF_8))

        try {
            val requestBody = mapOf(
                "paymentKey" to payment.paymentKey,
                "amount" to payment.totalAmount,
                "orderId" to payment.orderId
            )

            val request = HttpRequest.newBuilder()
                .uri(URI.create("${TossPaymentConfig.TOSS_URL}confirm"))
                .header("Authorization", "Basic $encodeKey")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                .build()

            val response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString())

            if (response == null || response.statusCode() != 200) {
                throw PaymentException("결제 승인 실패: " + (response?.body() ?: "응답 없음"))
            }

            println(response.body())

            return objectMapper.readValue(response.body(), TossPaymentTO::class.java)

        } catch (e: IOException) {
            log.error("결제 요청 중 오류 발생: {}", e.message)
            cancelPayment(payment.paymentKey, "결제 요청 중 오류 발생")
            throw PaymentException("결제 요청 중 IOException이 발생했습니다.", e)
        } catch (e: InterruptedException) {
            log.error("결제 요청 중 스레드 인터럽트 발생: {}", e.message)
            cancelPayment(payment.paymentKey, "결제 요청 중 오류 발생")
            throw PaymentException("결제 요청 중 InterruptedException이 발생했습니다.", e)
        }
    }

    fun cancelPayment(paymentKey: String, message: String): HttpResponse<String> {
        val encodeKey = Base64.getEncoder()
            .encodeToString("${tossPaymentConfig.testSecreteKey}:".toByteArray(StandardCharsets.UTF_8))

        try {
            val requestBody = objectMapper.writeValueAsString(mapOf("cancelReason" to message))

            val request = HttpRequest.newBuilder()
                .uri(URI.create("${TossPaymentConfig.TOSS_URL}$paymentKey/cancel"))
                .header("Authorization", "Basic $encodeKey")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build()

            val response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() != 200) {
                log.error("결제 취소 실패: HTTP 상태 코드 {}", response.statusCode())
                throw RuntimeException("결제 취소 실패: 상태 코드 ${response.statusCode()}")
            }
            return response
        } catch (e: IOException) {
            log.error("결제 취소 요청 실패(입출력 에러): {}", e.message)
            throw RuntimeException("결제 취소 요청 중 IOException이 발생했습니다.", e)
        } catch (e: InterruptedException) {
            log.error("결제 취소 요청 실패(인터럽트): {}", e.message)
            throw RuntimeException("결제 취소 요청 중 InterruptedException이 발생했습니다.", e)
        }
    }
}