package com.example.roomrawsql1

import android.app.ActivityManager
import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PerfRecorder {
    private val ctx = InstrumentationRegistry.getInstrumentation().targetContext
    private val TAG = "PerfRecorder"
    private val csvName = "perf_results.csv"

    private fun getPssKb(): Int {
        val am = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val pid = android.os.Process.myPid()
        val memInfo = am.getProcessMemoryInfo(intArrayOf(pid))
        return memInfo[0].totalPss // in KB
    }

    private fun getHeapUsedKb(): Long {
        val rt = Runtime.getRuntime()
        return (rt.totalMemory() - rt.freeMemory()) / 1024
    }

    /**
     * Measure block and append result to perf_results.csv in app filesDir.
     * name: identifier (e.g. "RawSQL_single_run_1")
     * metadata: optional free-form additional info (scenario, condition)
     */
    fun measureAndSave(name: String, metadata: String = "", block: () -> Unit): Map<String, Any> {
        val cores = Runtime.getRuntime().availableProcessors()
        val startWall = SystemClock.elapsedRealtimeNanos()
        val startCpu = android.os.Process.getElapsedCpuTime() // ms
        val memBefore = getPssKb()

        try {
            block()
        } catch (e: Exception) {
            Log.e(TAG, "Measured block threw: ${e.message}", e)
            throw e
        }

        val endCpu = android.os.Process.getElapsedCpuTime()
        val endWall = SystemClock.elapsedRealtimeNanos()
        val memAfter = getPssKb()
        val heapKb = getHeapUsedKb()

        val wallMs = (endWall - startWall) / 1_000_000.0
        val cpuDeltaMs = (endCpu - startCpu).toDouble()
        val cpuPct = if (wallMs > 0) (cpuDeltaMs / (wallMs * cores)) * 100.0 else 0.0

        val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).format(Date())

        val map = mapOf(
            "name" to name,
            "metadata" to metadata,
            "timestamp" to timestamp,
            "wall_ms" to wallMs,
            "cpu_ms" to cpuDeltaMs,
            "cpu_pct" to cpuPct,
            "pss_kb" to memAfter,
            "heap_kb" to heapKb
        )

        // Log and write CSV
        Log.i(TAG, map.toString())
        println(map.toString())

        writeCsv(map)

        return map
    }

    private fun writeCsv(row: Map<String, Any>) {
        try {
            val dir = ctx.filesDir
            val f = File(dir, csvName)
            val header = "name,metadata,timestamp,wall_ms,cpu_ms,cpu_pct,pss_kb,heap_kb\n"
            if (!f.exists()) {
                f.writeText(header)
            }
            FileWriter(f, true).use { fw ->
                fw.append("${escape(row["name"])}," +
                        "${escape(row["metadata"])}," +
                        "${escape(row["timestamp"])}," +
                        "${row["wall_ms"]}," +
                        "${row["cpu_ms"]}," +
                        "${row["cpu_pct"]}," +
                        "${row["pss_kb"]}," +
                        "${row["heap_kb"]}\n")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to write CSV: ${e.message}")
        }
    }

    private fun escape(value: Any?): String {
        return value?.toString()?.replace(",", ";") ?: ""
    }
}
