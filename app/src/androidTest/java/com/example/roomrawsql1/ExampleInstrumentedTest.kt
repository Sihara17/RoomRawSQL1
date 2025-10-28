import android.os.Environment
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import java.io.File

class PerfTest {
    @Test
    fun recordPerformance() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Simulasi hasil uji (ganti dengan hasil real)
        val data = """
            type,cpu,memory,rendering
            RawSQL,32,180,45
            Kotlin,29,172,47
        """.trimIndent()

        // Simpan ke file internal storage agar bisa diambil nanti
        val outFile = File(context.getExternalFilesDir(null), "perf_results.csv")
        outFile.writeText(data)
    }
}
