package com.cgm.experiments.blogapplicationdsl.utilities

open class ServerPort(private val serverPort: Int? = null): Port {
    override fun getPort(): Int = serverPort
        ?: RandomServerPort(this).getPort()
}

class RandomServerPort(private val port: Port): Port {
    override fun getPort(): Int = (10000..10500).random()
}

interface Port {
    fun getPort(): Int
}