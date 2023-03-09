import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    val day24 = Day24()
    measureTimeMillis {
        day24.solve().also { result -> println(result) }
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }
    // A: 297
    // B: 856
}

private typealias Board = Array<IntArray>

class Day24 {
    fun solve(inputFileName: String = "input/${this.javaClass.name.drop(3)}.txt"): Any {

        var board = File(inputFileName).readText(Charsets.UTF_8).split('\n')
            .map { row ->
                row.toCharArray().map { c ->
                    when (c) {
                        '.' -> Empty.b
                        '#' -> Wall.b
                        '^' -> Blizzard.N.b
                        '>' -> Blizzard.E.b
                        'v' -> Blizzard.S.b
                        '<' -> Blizzard.W.b
                        else -> throw IllegalArgumentException("Unknown character found when parsing board: $c")
                    }
                }.toIntArray()
            }.toTypedArray()

//        println(board.toCustomString())
//        println()
        val iterations = (board.size * board[0].size) - 1
        val states = mutableListOf(board)

        repeat(iterations) { board = moveAllBlizzards(board, states) }

        val targetPos = listOf(
            Pos(board[0].size - 2, board.size - 1),
            Pos(1, 0),
            Pos(board[0].size - 2, board.size - 1)
        )
        var targetCounter = 0

        var posQueue = setOf(Pos(1, 0))
        sequence { while (true) yieldAll(states) }.forEachIndexed { i, state ->
//            println("posQueue-size: ${posQueue.size}")
//            println(posQueue)
            posQueue = posQueue
                .flatMap { (x, y) ->
                    listOf(Pos(x, y - 1), Pos(x + 1, y), Pos(x, y + 1), Pos(x - 1, y), Pos(x, y))
                        .filter { (px, py) -> state.getOrNull(py)?.getOrNull(px) == Empty.b }
                }.toSet()
            posQueue.find { it == targetPos[targetCounter] }?.let {
                println("$it reached in $i minutes")
                posQueue = setOf(targetPos[targetCounter])
                targetCounter++
                if (targetCounter == targetPos.size) return i
            }
        }
        return -1
    }


    private fun moveAllBlizzards(
        board: Board,
        states: MutableList<Board>
    ): Board {
        val buffer = board.copyCleared()

        board.onEachIndexed { y, row ->
            row.onEachIndexed { x, _ ->
                moveBlizzards(x, y, board, buffer)
            }
        }
//        println(buffer.toCustomString())
//        println()
        states.add(buffer)
        return buffer
    }

    fun moveBlizzards(x: Int, y: Int, board: Board, buffer: Board) = Blizzard.values()
        .filter { blizz -> board[y][x] and blizz.b == blizz.b }
        .forEach { blizz -> blizz.nextTile(x to y, board).let { (x, y) -> buffer[y][x] = buffer[y][x] + blizz.b } }

    enum class Blizzard(val b: Int) {
        N(0b00000001), E(0b00000010), S(0b00000100), W(0b00001000);

        fun nextTile(c: Pair<Int, Int>, board: Board): Pair<Int, Int> = c.let { (x, y) ->
            return when (this) {
                N -> x to y - 1
                E -> x + 1 to y
                S -> x to y + 1
                W -> x - 1 to y
            }.takeUnless { (nx, ny) -> board[ny][nx] == Wall.b } ?: when (this) {
                N -> x to board.size - 2
                E -> 1 to y
                S -> x to 1
                W -> board.first().size - 2 to y
            }
        }
    }

    object Wall {
        const val b: Int = 0b00010000
    }

    object Empty {
        const val b: Int = 0b00000000
    }

    data class Pos(val x: Int, val y: Int)

    fun Board.toCustomString(): String =
        joinToString("\n") {
            it.joinToString(",", "[", "]") { it.toString().padStart(3) }
        }

    fun Board.copyCleared(): Board =
        map { row -> row.map { if (it == Wall.b) Wall.b else Empty.b }.toIntArray() }.toTypedArray()
}

