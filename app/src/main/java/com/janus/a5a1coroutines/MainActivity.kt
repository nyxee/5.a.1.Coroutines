package com.janus.a5a1coroutines

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis


class MainActivity : AppCompatActivity() {
    private val RESULT_2 = "Result #2"
    private val RESULT_1 = "Result #1"
    private var jobCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            logThread("buttonClick")
//            CoroutineScope(IO).launch {
//                fakeAPIRequest()
//            }
//            fakeAPIRequest2()
            fakeAPIRequest3()
        }
        text.movementMethod = ScrollingMovementMethod()

    }

    private suspend fun setNewText(input: String) {
        val newText = text.text.toString() + "\n$input"
        text.text = newText
    }

    private suspend fun  setTextOnMainThread(input: String){
        withContext(Main){
            setNewText(input)
        }
    }
    private suspend fun fakeAPIRequest() {
        val result1 = "JOB: $jobCount -> ${getAPIResult()}"
        println("debug: $result1}")
        setTextOnMainThread(result1)

        val result2 = "JOB: $jobCount -> ${getAPIResult2()}"
        println("debug: $result2}")
        setTextOnMainThread(result2)

        incrementCount()
    }

    private fun fakeAPIRequest2() {
        CoroutineScope(IO).launch {
            val job3 = launch {
                incrementCount()
            }
            job3.join()

            val job1 = launch{
                val time = measureTimeMillis {
                    println("debug: Launching Job1 in THREAD:: ${Thread.currentThread().name}")
                    val result = "JOB: $jobCount  -> ${getAPIResult()}"
                    println("debug: $result}")
                    setTextOnMainThread(result)
                }
                println("debug: Completed Job1 in $time sec")
            }

            val job2 = launch{
                val time = measureTimeMillis {
                    println("debug: Launching Job2 in THREAD:: ${Thread.currentThread().name}")
                    val result = "JOB: $jobCount -> ${getAPIResult2()}"
                    println("debug: $result}")
                    setTextOnMainThread(result)
                }
                println("debug: Completed Job2 in $time sec")
            }
//            job1.join()
//            job2.join()S
        }
    }

    private fun fakeAPIRequest3() {
        CoroutineScope(IO).launch {

                val executionTime = measureTimeMillis {
                    val currentCount: Deferred<Int> = async {
                        incrementCount()
                        jobCount
                    }
                    val thisJobCount = currentCount.await()
                    println("debug: currentCount -> $thisJobCount")

                    val result1:Deferred<String> = async {
                        println("debug: Launching Job1 in THREAD:: ${Thread.currentThread().name}")
                        "JOB: $jobCount, $thisJobCount -> ${getAPIResult()}"
                    }

                    setTextOnMainThread(result1.await())
                    println("debug: $result1}")


                    val result2:Deferred<String> = async {
                        println("debug: Launching Job2 in THREAD:: ${Thread.currentThread().name}")
                        "JOB: $jobCount, $thisJobCount -> ${getAPIResult2()}"
                    }


                    setTextOnMainThread(result2.await())
                    println("debug: $result2}")
                }
                println("debug: Completed Jobs in $executionTime sec")

        }
    }
    private suspend fun incrementCount(){
        withContext(Main){
            logThread("incrementCount $jobCount")
            delay(1000)
            jobCount += 1
        }
    }

    private suspend fun getAPIResult(): String {
        logThread("getAPIResult1")
        delay(1000)

        return RESULT_1
    }

    private suspend fun getAPIResult2(): String {
        logThread("getAPIResult2")
        delay(1000)

        return RESULT_2
    }
    private fun logThread(methodName: String){
        println("debug: $methodName: ${Thread.currentThread().name}")

    }
}