import java.io.File

fun main() {
    val input = File("input/06.txt").readText(Charsets.UTF_8)
    val solutionA = solve(input, 4)
    println(solutionA)
    val solutionB = solve(input, 14)
    println(solutionB)
}

private fun solve(input: String, markerLength: Int): Any {
    val window = emptyMap<Char, Int>().toMutableMap()
    input.asSequence().forEachIndexed { i, c ->
        if (!window.containsKey(c)) window[c] = 1
        else window[c] = window[c]!! + 1

        if (i < markerLength) return@forEachIndexed

        val outgoing = input[i - markerLength]
        window[outgoing] = window[outgoing]!! - 1
        if (window[outgoing]!! <= 0) window.remove(outgoing)
        if (window.size == markerLength) return i + 1
    }
    return -1
}