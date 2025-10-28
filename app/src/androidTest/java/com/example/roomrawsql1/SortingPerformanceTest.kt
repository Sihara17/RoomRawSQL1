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
        try {
            // optional: pull latest GC
            Runtime.getRuntime().gc()
            System.runFinalization()
        } catch (e: Exception) { /* ignore */ }

        scenario.close()
    }

    @Test
    fun runAscSortingTests() {
        // contoh: jalankan 5 iterasi (sesuaikan jumlah sesuai kebutuhan)
        repeat(5) {
            testAscSortingCombination(it + 1)
            resetDataToOriginal()
            testCount++
        }

        // Log hasil akhir ke logcat juga (CSV sudah terbuat oleh PerfRecorder)
        logFinalResults()
    }

    private fun testAscSortingCombination(runId: Int) {
        // metadata contoh: "RawSQL|single" atau "Room|relational"
        // kalau kamu punya flag/variable untuk memilih RawSQL vs Room, set metadata sesuai.
        val condition = detectCurrentCondition() // implementasi sederhana di bawah
        val scenarioName = detectCurrentScenario() // implementasi sederhana
        val meta = "$condition|$scenarioName|run:$runId"

        // Set spinner selections secara manual
        setAllSpinnersToAscManual()

        // ukur: kita ingin merekam CPU/Memory/WALL + renderingTime
        PerfRecorder.measureAndSave("$condition-$scenarioName-run-$runId", meta) {
            // Trigger sorting (aksi yang ingin diukur)
            onView(withId(R.id.buttonSortOK)).perform(click())

            // Tunggu sampai proses sorting/logika selesai (bukan rendering): kecil delay
            Thread.sleep(100)

            // Ukur rendering secara spesifik (blocking)
            val renderingDuration = measureRenderingTime()
            renderingTimes.add(renderingDuration)

            // record rendering time juga ke log (sekaligus kita tambahkan ke metadata via println)
            Log.i("PerfExtra", "render_ms=$renderingDuration, items=${getItemCount()}")
            println("render_ms=$renderingDuration, items=${getItemCount()}")
        }
    }

    // Tetap gunakan implementasi Choreographer yang sudah kamu punya
    private fun measureRenderingTime(): Long {
        val latch = CountDownLatch(1)
        var renderingTime = 0L
        var measurementTaken = false

        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.recyclerViewPersons)
            val startTime = System.nanoTime()

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

        return if (latch.await(3, TimeUnit.SECONDS)) renderingTime else -1
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
        Thread.sleep(500)
    }

    private fun logFinalResults() {
        println("\n=== FINAL RESULTS ===")
        println("Total Rendering Samples: ${renderingTimes.size}")
        println("Average Rendering Time: ${renderingTimes.average()}ms")
        println("Min Rendering Time: ${renderingTimes.minOrNull()}ms")
        println("Max Rendering Time: ${renderingTimes.maxOrNull()}ms")
        println("All Render Times: ${renderingTimes.joinToString()}")
    }

    private fun getItemCount(): Int {
        val itemCount = AtomicInteger(0)
        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.recyclerViewPersons)
            itemCount.set(recyclerView.adapter?.itemCount ?: 0)
        }
        return itemCount.get()
    }

    // Simple detectors: jika kamu punya logic yang membedakan RawSQL vs Room
    // sesuaikan implementasi ini dengan kondisi di project-mu.
    private fun detectCurrentCondition(): String {
        // contoh default - ubah jika kamu punya cara memilih RawSQL vs Room
        return "UNKNOWN" // ganti "RawSQL" atau "Room" sesuai konfigurasi test kamu
    }

    private fun detectCurrentScenario(): String {
        // contoh default - ubah jika kamu menjalankan skenario tunggal vs relasi
        return "UNKNOWN_SCENARIO"
    }
}
