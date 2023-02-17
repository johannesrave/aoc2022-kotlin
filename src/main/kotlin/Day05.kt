import java.io.File

private typealias CrateStack = List<Char>

private data class Move(val amt: Int, val from: Int, val to: Int)

fun main() {
    val stacks = parseStacks("input/05.txt")
    val moves = parseMoves("input/05.txt")
    val solutionA = solve(stacks, moves, moveOneByOne)
    println(solutionA)
    val solutionB = solve(stacks, moves, moveAsStack)
    println(solutionB)
}

private fun solve(
    stacks: Array<CrateStack>,
    moves: List<Move>,
    moveFunc: (stacks: Array<CrateStack>, move: Move) -> Array<CrateStack>
): Any {
    var mutableStacks = stacks.map { it.toCharArray().toList() }.toTypedArray()
    for (move in moves) {
        mutableStacks = moveFunc(mutableStacks, move)
    }

    return mutableStacks.map { it.first() }.toString()
}

private fun parseStacks(input: String): Array<CrateStack> {
    return File(input)
        .readText(Charsets.UTF_8)
        .split("\n 1   2   3   4   5   6   7   8   9\n\n")
        .first()
        .split('\n')
        .map { it.slice(1..9 * 4 step 4).toCharArray() }
        .toTypedArray()
        .transpose()
        .map { it.filter { it != ' ' } }
        .toTypedArray()
}

private fun Array<CharArray>.transpose(): Array<CharArray> {
    val buffer = Array(this.first().size) { CharArray(this.size) { ' ' } }
    this.forEachIndexed { i, row ->
        row.forEachIndexed { j, _ ->
            buffer[j][i] = this[i][j]
        }
    }
    return buffer
}

private fun parseMoves(input: String): List<Move> {
    val rx = Regex("move (\\d*) from (\\d) to (\\d)")

    return File(input)
        .readText(Charsets.UTF_8)
        .split("\n 1   2   3   4   5   6   7   8   9\n\n")
        .last()
        .split('\n')
        .map {
            val (amt, start, target) = rx.find(it)!!.groupValues
                .drop(1)
                .map {
                    it.toInt()
                }
            return@map Move(amt, start - 1, target - 1)
        }
}

private val moveOneByOne = { stacks: Array<CrateStack>, move: Move ->
    val (amt, start, target) = move
    stacks[target] = stacks[start].take(amt).reversed() + stacks[target]
    stacks[start] = stacks[start].drop(amt)
    stacks
}

private val moveAsStack = { stacks: Array<CrateStack>, move: Move ->
    val (amt, from, to) = move
    stacks[to] = stacks[from].take(amt) + stacks[to]
    stacks[from] = stacks[from].drop(amt)
    stacks
}