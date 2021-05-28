package com.cgm.experiments.blogapplicationdsl.utilities

open class ServerPort(private val serverPort: Int? = null): Port {
    override fun getPort(): Int = serverPort
        ?: RandomServerPort.getPort()
}

object RandomServerPort: Port {
    override fun getPort(): Int = (10000..10500).random()
}

interface Port {
    fun getPort(): Int
}