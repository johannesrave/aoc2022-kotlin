import java.io.File

fun main() {
    val solutionA = solve("input/04.txt", fullyContained)
    println(solutionA)

    val solutionB = solve("input/04.txt", contained)
    println(solutionB)
}

private fun solve(input: String, cond: (List<Int>) -> Boolean): Any {

    val regex = Regex("""(\d+)-(\d+),(\d+)-(\d+)""")
    return File(input)
        .readText(Charsets.UTF_8)
        .split("\n")
        .map { regex.find(it)!!.groupValues.drop(1).map { it.toInt() } }
        .count { cond(it) }
}

private val fullyContained: (List<Int>) -> Boolean =
    { (lowerA, upperA, lowerB, upperB) -> (lowerA >= lowerB && upperA <= upperB) || (lowerA <= lowerB && upperA >= upperB) }

private val partiallyContained: (List<Int>) -> Boolean =
    { (lowerA, upperA, lowerB, upperB) -> (lowerA in lowerB..upperB) || (upperA in lowerB..upperB) }

private val contained: (List<Int>) -> Boolean =
    { ints -> partiallyContained(ints) || fullyContained(ints) }
