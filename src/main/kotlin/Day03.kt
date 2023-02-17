import java.io.File

fun main() {
    val prios = createPrios()

    val solutionA = solve("input/03.txt", prios)
    println(solutionA)

    val solutionB = solveB("input/03.txt", prios)
    println(solutionB)
}

private fun createPrios(): Map<Char, Int> {
    val smalls = ('a'..'z').associateWith { it - 'a' + 1 }
    val caps = ('A'..'Z').associateWith { it - 'A' + 27 }
    return smalls + caps
}

private fun solve(input: String, prios: Map<Char, Int>): Any {
    return File(input)
        .readText(Charsets.UTF_8)
        .split("\n")
        .chunkBySplit()
        .sumOf { prios[it]!! }
}

private fun solveB(input: String, prios: Map<Char, Int>): Any {
    return File(input)
        .readText(Charsets.UTF_8)
        .split("\n")
        .chunkByThrees()
        .sumOf { prios[it]!! }
}

private fun List<String>.chunkByThrees() = this
    .map { it.toCharArray() }
    .map { it.toSet() }
    .chunked(3)
    .map { it.reduce { shared, set -> shared.intersect(set) }.first() }

private fun List<String>.chunkBySplit() = this
    .map {
        arrayOf(
            it.substring(0, it.length / 2).toCharArray(),
            it.substring(it.length / 2).toCharArray()
        )
    }.map { it[0].intersect(it[1].toSet()).first() }