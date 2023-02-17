import java.io.File

abstract class Day(inputFileName: String) {
    val input = File(inputFileName).readText(Charsets.UTF_8)
}
