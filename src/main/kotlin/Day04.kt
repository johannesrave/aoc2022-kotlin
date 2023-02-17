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
        .filter { cond(it) }
        .count()
}

private val fullyContained: (List<Int>) -> Boolean =
    { ints -> (ints[0] >= ints[2] && ints[1] <= ints[3]) || (ints[0] <= ints[2] && ints[1] >= ints[3]) }

private val partiallyContained: (List<Int>) -> Boolean =
    { ints -> (ints[0] >= ints[2] && ints[0] <= ints[3]) || (ints[1] >= ints[2] && ints[1] <= ints[3]) }

private val contained: (List<Int>) -> Boolean =
    { ints -> partiallyContained(ints) || fullyContained(ints) }
