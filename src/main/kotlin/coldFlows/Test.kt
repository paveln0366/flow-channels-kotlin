package org.example.coldFlows

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
private val scope = CoroutineScope(dispatcher)

fun main() {
    val flow = Repository.timer
    scope.launch {
        flow.collect {
            println(it)
        }
    }
    scope.launch {
        flow.collect {
            println(it)
        }
        println("Finished")
    }
}