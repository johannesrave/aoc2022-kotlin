import java.io.File


private fun parse(input: String): String = File(input).readText(Charsets.UTF_8)


fun main() {
    val solutionA = solve(1)
    val solutionB = solve(3)
    println(solutionA)
    println(solutionB)
}

private fun solve(n: Int): Int {
    return parse("input/01.txt")
        .split("\n\n")
        .map {
            it.split("\n")
                .sumOf { it.toInt() }
        }
        .sortedDescending()
        .take(n)
        .sum()
}