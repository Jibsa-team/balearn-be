package com.jipsa.balearn

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BalearnApplication

fun main(args: Array<String>) {
	runApplication<BalearnApplication>(*args)
}
