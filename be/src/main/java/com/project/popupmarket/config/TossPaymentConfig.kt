package com.project.popupmarket.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class TossPaymentConfig {

    @Value("\${payment.toss.test_secret_api_key}")
    lateinit var testSecreteKey: String

    companion object {
        const val TOSS_URL = "https://api.tosspayments.com/v1/payments/"
    }
}