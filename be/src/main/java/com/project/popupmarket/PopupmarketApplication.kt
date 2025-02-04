package com.project.popupmarket

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.PropertySource
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
@PropertySource("classpath:.env")
class PopupmarketApplication

fun main(args: Array<String>) {
    runApplication<PopupmarketApplication>(*args)
}