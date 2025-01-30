package com.project.popupmarket.exception.custom

class PaymentException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}