package com.afauzi.peoemergency.coroutine

import org.junit.Test
import java.util.*

class MultipleThread {
    @Test
    fun multipleThread() {
        // Java Thread
        val thread1 = Thread(Runnable {
            println(Date())
            Thread.sleep(2000)
            println("Finish Thread 1 : ${Thread.currentThread().name} : ${Date()}")
        })
        val thread2 = Thread(Runnable {
            println(Date())
            Thread.sleep(2000)
            println("Finish Thread 2 : ${Thread.currentThread().name} : ${Date()}")
        })

        thread1.start()
        thread2.start()

        println("Running")
        Thread.sleep(3000)
        println("Finish")
    }
}