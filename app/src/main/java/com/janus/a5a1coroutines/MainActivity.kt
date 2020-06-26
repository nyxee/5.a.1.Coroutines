package com.janus.a5a1coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {
    private val RESULT_2 = "Result #2"
    private val RESULT_1 = "Result #1"
    private var jobCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            CoroutineScope(IO).launch {
                fakeAPIRequest()
            }
        }
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

    private suspend fun incrementCount(){
        jobCount += 1
    }

    private suspend fun getAPIResult(): String {
        logThread("getAPIResult")
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