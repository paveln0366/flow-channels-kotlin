package org.example.hotFlows

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

object Repository {

    private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
    private val scope = CoroutineScope(dispatcher)
    private val _timer = MutableSharedFlow<Int>()
    val timer = _timer.asSharedFlow()

    init {
        scope.launch {
            var seconds = 0
            while (true) {
                println("Emitted: $seconds")
                _timer.emit(seconds++)
                delay(1000)
            }
        }
    }
}