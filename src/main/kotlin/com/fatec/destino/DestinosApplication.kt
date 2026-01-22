package com.fatec.destino;

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
object DestinoApplication {
    @JvmStatic
    fun main(args: Array<String>) {
        SpringApplication.run(DestinoApplication::class.java, *args)
    }
}