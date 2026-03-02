package org.example.day2.task2

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


//// collect List
//fun main() = runBlocking {
//    flow {
//        emit(1)
////        delay(2000)
//        emit(2)
////        delay(2000)
//        emit(3)
//    }.collectLatest { value ->
//        delay(1000)
//        println("Collected: $value")
//    }
//}

//collect

fun main() = runBlocking {
    flow {
        emit(1)
        emit(2)
        emit(3)
    }.collect { value ->
        delay(1000)
        println("Collected: $value")
    }
}
//


//emit
val flow = MutableSharedFlow<Int>()

suspend fun send() {
    flow.emit(1)
}


//try emit
val flowTryEmit = MutableSharedFlow<Int>()

fun sendFlowTryEmit() {
    val success = flowTryEmit.tryEmit(1)
    println(success)
}

