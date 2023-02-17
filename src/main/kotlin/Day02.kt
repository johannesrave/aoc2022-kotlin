import java.io.File

private val naiveLookUp = mapOf(
    "A X" to 3+1,
    "B X" to 0+1,
    "C X" to 6+1,
    "A Y" to 6+2,
    "B Y" to 3+2,
    "C Y" to 0+2,
    "A Z" to 0+3,
    "B Z" to 6+3,
    "C Z" to 3+3,
)

private val secretLookUp = mapOf(
    "A X" to 3+0,
    "B X" to 1+0,
    "C X" to 2+0,
    "A Y" to 1+3,
    "B Y" to 2+3,
    "C Y" to 3+3,
    "A Z" to 2+6,
    "B Z" to 3+6,
    "C Z" to 1+6,
)

fun main() {
    val solutionA = solve("input/02.txt", naiveLookUp)
    val solutionB = solve("input/02.txt", secretLookUp)
    println(solutionA)
    println(solutionB)
}

private fun solve(input: String, lu: Map<String, Int>): Int {
    return File(input)
        .readText(Charsets.UTF_8)
        .split("\n")
        .mapNotNull {lu[it]}
        .sum()
}
