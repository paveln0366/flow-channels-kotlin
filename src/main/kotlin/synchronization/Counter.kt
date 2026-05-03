package org.example.synchronization

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.Executors

class Counter {

    private val mutex = Mutex()
    var value: Int = 0

    suspend fun inc() {
        mutex.withLock {
            Thread.sleep(1)
            value++
        }
    }
}

private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
private val scope = CoroutineScope(dispatcher)

fun main() {
    val counter = Counter()
    scope.launch {
        buildList {
            repeat(100) {
                scope.launch {
                    repeat(10) {
                        counter.inc()
                    }
                }.let { add(it) }
            }
        }.joinAll()
        println(counter.value)
    }
}