package org.example.day2.task1

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

fun main() = runBlocking {
    val manager = NotificationManager()

    val collector1 = launch {
        manager.events.collect {
            println("Collector 1 received: $it")
        }
    }

    delay(500)

    val collector2 = launch {
        manager.events.collect {
            println("Collector 2 received: $it")
        }
    }

    delay(500)
    manager.sendEvent("Event 1")

    delay(500)
    manager.sendEvent("Event 2")

    delay(1000)

    collector1.cancel()
    collector2.cancel()
}




class NotificationManager {

    private val _events = MutableSharedFlow<String>()
    val events: SharedFlow<String> = _events

    suspend fun sendEvent(message: String) {
        _events.emit(message)
    }
}