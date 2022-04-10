package com.afauzi.peoemergency.coroutine

import org.junit.Test
import java.util.*
import java.util.concurrent.Executors

class ExecutorServiceTest {
    @Test
    fun testSingleThread(){
        val executorsService = Executors.newSingleThreadExecutor()
        repeat(10){
            val runnable = Runnable {
                Thread.sleep(1000)
                println("Done $it ${Thread.currentThread().name} ${Date()}")
            }
            executorsService.execute(runnable)
//            println("Berhasil menyimpan thread runnable $it kedalam queue/antrian ")
        }

        println("Menunggu")
        Thread.sleep(11000)
        println("Selesai")
    }
    @Test
    fun testFixThreadPool(){
        val executorsService = Executors.newFixedThreadPool(3)
        repeat(10){
            val runnable = Runnable {
                Thread.sleep(1000)
                println("Done $it ${Thread.currentThread().name} ${Date()}")
            }
            executorsService.execute(runnable)
//            println("Berhasil menyimpan thread runnable $it kedalam queue/antrian ")
        }

        println("Menunggu")
        Thread.sleep(11000)
        println("Selesai")
    }

}