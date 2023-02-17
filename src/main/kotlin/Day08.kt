import java.io.File

fun main() {
    val input = File("input/08.txt").readText(Charsets.UTF_8)
//    val input = File("input/08_test.txt").readText(Charsets.UTF_8)

    val solutionA = solveA(input)
    println(solutionA)
    val solutionB = solveB(input)
    println(solutionB)
}

private fun solveA(input: String): Any {
    val forest = parseToForest(input)
    val transposedForest = forest.transpose()
    return forest.mapIndexed { y, row ->
        row.filterIndexed { x, tree ->
            val col = transposedForest[x]

            getLanes(col, y, row, x).any { lane -> lane.all { it < tree } }
        }.count()
    }.sum()
}

private fun solveB(input: String): Any {
    val forest = parseToForest(input)
    val transposedForest = forest.transpose()
    return forest.mapIndexed { y, row ->
        row.mapIndexed { x, tree ->
            val col = transposedForest[x]

            getLanes(col, y, row, x)
                .fold(1) { acc: Int, lane: List<Int> -> acc * findTreesVisibleFrom(tree, lane) }
        }.max()
    }.max()
}

private fun parseToForest(input: String): Array<IntArray> {
    return input.split('\n')
        .map {
            it.toCharArray()
                .map { it.digitToInt() }
                .toIntArray()
        }.toTypedArray()
}

private fun getLanes(col: IntArray, y: Int, row: IntArray, x: Int): List<List<Int>> {
    val toTop = col.take(y).reversed()
    val toRgt = row.drop(x + 1)
    val toBtm = col.drop(y + 1)
    val toLft = row.take(x).reversed()
    return listOf(toTop, toRgt, toBtm, toLft)
}

private fun findTreesVisibleFrom(tree: Int, lane: List<Int>): Int {
    var visibleTrees = 1
    for (i: Int in lane.indices) {
        visibleTrees = i + 1
        if (tree <= lane[i]) break
    }
    return visibleTrees
}

private fun Array<IntArray>.transpose(): Array<IntArray> {
    val buffer = Array(this.first().size) { IntArray(this.size) }
    this.forEachIndexed { i, row ->
        row.forEachIndexed { j, _ ->
            buffer[j][i] = this[i][j]
        }
    }
    return buffer
}