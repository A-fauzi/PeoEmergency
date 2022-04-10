package com.afauzi.peoemergency.coroutine

import org.junit.Test
import java.util.*
import kotlin.concurrent.thread

class ThreadTest {
    @Test
    fun tesThreadName() {
        val threadName = Thread.currentThread().name
        println("Running in thread $threadName")
    }

    @Test
    fun testNewThread() {
        // Java Thread
//        val runnable = Runnable {
//            println(Date())
//            Thread.sleep(2000)
//            println("Finish : ${Date()}")
//        }
//        val thread = Thread(runnable)
//        thread.start()

        // Kotlin Thread
        thread(start = true) {
            Thread.sleep(1000)
            println(Date())
            Thread.sleep(1000)
            println(Date())
            Thread.sleep(1000)
            println(Date())
            Thread.sleep(1000)
            println(Date())
            Thread.sleep(1000)
            println("Finish : ${Date()}")
        }

        println("Running Date")
        Thread.sleep(6000)
        println("Selesai")
    }
}