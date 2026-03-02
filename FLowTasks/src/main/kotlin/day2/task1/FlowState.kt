package org.example.day2.task1

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

fun main() = runBlocking {

    val viewModel = CounterViewModel()

    val job = launch {
        viewModel.counter.collect { value ->
            println("Counter value: $value")
        }
    }

    delay(1000)
    viewModel.increment()

    delay(1000)
    viewModel.increment()

    delay(1000)
    viewModel.decrement()

    delay(1000)
    job.cancel()
}

class CounterViewModel {

    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter.asStateFlow()

    fun increment() {
        _counter.value += 1
    }

    
    fun decrement() {
        _counter.value -= 1
    }


}



