package com.example.roomrawsql1

import android.util.Log
import android.view.Choreographer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class SortingPerformanceTest {
    private lateinit var scenario: ActivityScenario<MainActivity>
    private var testCount = 1
    private val renderingTimes = mutableListOf<Long>()

    @Before
    fun setup() {
        Runtime.getRuntime().gc()
        System.runFinalization()

        scenario = ActivityScenario.launch(MainActivity::class.java)
        waitForDataLoaded()
    }

    @After
    fun cleanup() {
        scenario.close()
        Runtime.getRuntime().gc()
        System.runFinalization()
    }

    @Test
    fun runAscSortingTests() {
        repeat(5) {
            testAscSortingCombination()
            resetDataToOriginal()
            testCount++
        }

        // Log hasil akhir
        logFinalResults()
    }

    private fun testAscSortingCombination() {
        // Set spinner selections secara manual melalui activity
        setAllSpinnersToAscManual()

        // Trigger sorting
        onView(withId(R.id.buttonSortOK)).perform(click())

        // Tunggu sampai data selesai diproses (bukan rendering)
        Thread.sleep(100) // Tunggu proses sorting selesai

        // Ukur hanya waktu rendering
        val renderingDuration = measureRenderingTime()

        renderingTimes.add(renderingDuration)
        logTestResults(renderingDuration)
    }

    private fun measureRenderingTime(): Long {
        val latch = CountDownLatch(1)
        var renderingTime = 0L
        var measurementTaken = false

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.recyclerViewPersons)
            val startTime = System.nanoTime()

            // Gunakan salah satu pendekatan saja
            val choreographer = Choreographer.getInstance()
            val frameCallback = object : Choreographer.FrameCallback {
                override fun doFrame(frameTimeNanos: Long) {
                    if (!measurementTaken) {
                        measurementTaken = true
                        choreographer.removeFrameCallback(this)
                        renderingTime = (System.nanoTime() - startTime) / 1_000_000
                        latch.countDown()
                    }
                }
            }
            choreographer.postFrameCallback(frameCallback)
            recyclerView.requestLayout()
        }

        return if (latch.await(2, TimeUnit.SECONDS)) renderingTime else -1
    }

    // Alternatif lebih sederhana menggunakan View.post
    private fun measureRenderingTimeSimple(): Long {
        val latch = CountDownLatch(1)
        var renderingTime = 0L

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.recyclerViewPersons)

            val startTime = System.nanoTime()

            // Post runnable yang akan dieksekusi setelah rendering selesai
            recyclerView.post {
                val endTime = System.nanoTime()
                renderingTime = (endTime - startTime) / 1_000_000
                latch.countDown()
            }

            // Trigger rendering
            recyclerView.requestLayout()
        }

        if (latch.await(2, TimeUnit.SECONDS)) {
            return renderingTime
        } else {
            Log.w("RenderingMeasure", "Rendering timeout detected")
            return -1
        }
    }

    private fun setAllSpinnersToAscManual() {
        scenario.onActivity { activity ->
            activity.findViewById<android.widget.Spinner>(R.id.spinnerSortName).setSelection(0)
            activity.findViewById<android.widget.Spinner>(R.id.spinnerSortAge).setSelection(0)
            activity.findViewById<android.widget.Spinner>(R.id.spinnerSortDepartment).setSelection(0)
            activity.findViewById<android.widget.Spinner>(R.id.spinnerSortSalary).setSelection(0)
            Log.d("SpinnerSetup", "All spinners set to ASC position manually")
        }
    }

    private fun resetDataToOriginal() {
        try {
            Runtime.getRuntime().gc()
            System.runFinalization()

            onView(withId(R.id.buttonReset)).perform(click())

            // Tunggu sampai reset selesai
            Thread.sleep(300)
            waitForDataLoaded()

            Runtime.getRuntime().gc()
            System.runFinalization()

        } catch (e: Exception) {
            Log.e("ResetError", "Failed to reset data", e)
            throw e
        }
    }

    private fun waitForDataLoaded() {
        // Tunggu sampai data terload dengan simple delay
        Thread.sleep(500)
    }

    private fun logTestResults(duration: Long) {
        println("=== TEST ITERATION $testCount ===")
        println("Rendering Time Only: ${duration}ms")
        println("Items rendered: ${getItemCount()}")
        println("================================")
    }

    private fun logFinalResults() {
        println("\n=== FINAL RESULTS ===")
        println("Total Tests: ${renderingTimes.size}")
        println("Average Rendering Time: ${renderingTimes.average()}ms")
        println("Min Rendering Time: ${renderingTimes.minOrNull()}ms")
        println("Max Rendering Time: ${renderingTimes.maxOrNull()}ms")
        println("All Times: ${renderingTimes.joinToString()}")
    }

    private fun getItemCount(): Int {
        val itemCount = AtomicInteger(0)
        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.recyclerViewPersons)
            itemCount.set(recyclerView.adapter?.itemCount ?: 0)
        }
        return itemCount.get()
    }
}