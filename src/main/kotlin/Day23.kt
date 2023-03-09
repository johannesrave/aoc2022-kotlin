import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    val day23 = Day23()

    measureTimeMillis {
        day23.solve().also { result -> println("No elf moved in round $result") }
        // 1079
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }
}

class Day23 {
    val elf = '#'
    val emptyTile = '.'
    val conflictedTarget = 'X'
    fun solve(inputFileName: String = "input/${this.javaClass.name.drop(3)}.txt", iterations: Int = 10): Any {

        var board = File(inputFileName).readText(Charsets.UTF_8).toBoard()
        val dirCycle = cycleDirs()

        repeat(Int.MAX_VALUE) { round ->
            // for part A: calculate and print the number of empty tiles after shrinking the board
            if (round == iterations) {
                board.onEach { println(it) }

                val _board = shrinkBoard(board)
                println("after shrinking")
                _board.onEach { println(it) }

                val resultPartA = _board.sumOf { row -> row.count { it == emptyTile } }
                println("the board has $resultPartA empty tiles after $iterations rounds.")
            }

            val dirs = dirCycle.next()
            //    expand board if necessary (if an elf is touching the edge)
            //    -> instead, we could guess maximal boundaries of the board and just use the memory, probably faster
            if (boardMustBeExpanded(board)) board = expandBoard(board)

            // for part B: flag whether a move was proposed, else return the current round (int)
            var noMovesNecessary = true

            // consider spacing and propose moves
            board.onEachIndexed { y, row ->
                row.onEachIndexed traverseRow@{ x, c ->
                    if (c != elf) return@traverseRow
                    //  check if elf should try to move at all
                    val neighbouringElves = getAllNeighbours(x to y, board).any { it == elf }
                    if (!neighbouringElves) return@traverseRow


                    //  check which direction is a candidate for moving to
                    val emptyDir = dirs.find { dir -> getNeighboursInDir(x to y, dir, board).none { it == elf } }
                        ?: return@traverseRow
                    noMovesNecessary = false

                    // check if no other elf has proposed to walk there already
                    val (nx, ny) = getCoordsInDir(x to y, emptyDir) // neighbour coords
                    when (board[ny][nx]) {
                        // if the tile is empty, flag it with the direction that this elf is in when seen
                        // from it. this can later be used to swap the two tiles.
                        emptyTile -> board[ny][nx] = emptyDir.getOpposing().toChar()
                        // if the tile is marked with a Dir, the elf in that direction from it is
                        // proposing to move there. setting this tile to 'conflicted' instead will also keep
                        // them from moving.
                        'N', 'S', 'W', 'E' -> board[ny][nx] = conflictedTarget
                        // if the tile is already conflicted, don't propose to move
                        conflictedTarget -> {}
                    }
                }
            }
            // an alternative would be to look for N,E,S,W or X in the board to check for moves made,
            // but flagging seems a bit cheaper as we're performing the comparisons already
            if (noMovesNecessary) return round+1

            // make moves by swapping proposed Dir tiles with the respective elves
            board.onEachIndexed { y, row ->
                row.onEachIndexed traverseRow@{ x, c ->
                    when (c) {
                        'N', 'S', 'W', 'E' -> {
                            val dirOfElfMovingHere = Dir.valueOf(c.toString())
                            val elfMovingHere = getCoordsInDir(x to y, dirOfElfMovingHere)
                            elfMovingHere.let { (ex, ey) -> board[ey][ex] = emptyTile }
                            board[y][x] = elf
                        }

                        'X' -> board[y][x] = emptyTile
                    }
                }
            }
        }
        return -1
    }

    private fun cycleDirs(): Iterator<List<Dir>> =
        generateSequence(listOf(Dir.N, Dir.S, Dir.W, Dir.E)) { (d0, d1, d2, d3) -> listOf(d1, d2, d3, d0) }.iterator()

    enum class Dir {
        N, W, S, E;

        fun getOpposing(): Dir = Dir.values().let { dirs -> dirs[(ordinal + 2) % 4] }
        fun toChar(): Char = this.toString()[0]
    }

    fun getAllNeighbours(pos: Pair<Int, Int>, board: Array<CharArray>): List<Char> = pos.let { (x, y) ->
        listOf(
            board[y - 1][x - 1], board[y - 1][x], board[y - 1][x + 1], board[y][x + 1],
            board[y + 1][x - 1], board[y + 1][x], board[y + 1][x + 1], board[y][x - 1]
        )
    }

    fun getNeighboursInDir(pos: Pair<Int, Int>, dir: Dir, board: Array<CharArray>): List<Char> = pos.let { (x, y) ->
        when (dir) {
            Dir.N -> listOf(board[y - 1][x - 1], board[y - 1][x], board[y - 1][x + 1])
            Dir.S -> listOf(board[y + 1][x - 1], board[y + 1][x], board[y + 1][x + 1])
            Dir.W -> listOf(board[y - 1][x - 1], board[y][x - 1], board[y + 1][x - 1])
            Dir.E -> listOf(board[y - 1][x + 1], board[y][x + 1], board[y + 1][x + 1])
        }
    }

    fun getCoordsInDir(pos: Pair<Int, Int>, dir: Dir): Pair<Int, Int> = pos.let { (x, y) ->
        when (dir) {
            Dir.N -> x to y - 1
            Dir.S -> x to y + 1
            Dir.W -> x - 1 to y
            Dir.E -> x + 1 to y
        }
    }

    fun expandBoard(board: Array<CharArray>, c: Char = '.'): Array<CharArray> {
        val emptyTile = arrayOf(c).toCharArray()

        return arrayOf(Array(board.size + 2) { c }.toCharArray()) +
            board.map { emptyTile + it + emptyTile } +
            arrayOf(Array(board.size + 2) { c }.toCharArray())
    }

    fun shrinkBoard(board: Array<CharArray>): Array<CharArray> {
        val minX = board.minOf { it.indexOf(elf).takeIf { it >= 0 } ?: board.size }
        val maxX = board.maxOf { it.lastIndexOf(elf) }
        val minY = board.find { it.any { c -> c == elf } }.let { row -> board.indexOf(row) }
        val maxY = board.findLast { it.any { c -> c == elf } }.let { row -> board.indexOf(row) }
        println("minx: $minX, maxX: $maxX, minY: $minY, mixY: $maxY")
        return board.sliceArray(minY..maxY)
            .map { row -> row.sliceArray(minX..maxX) }
            .toTypedArray()
    }

    fun boardMustBeExpanded(board: Array<CharArray>): Boolean =
        elf in board.first() || elf in board.last() || board.any { it.first() == elf } || board.any { it.last() == elf }
}

private fun String.toBoard(): Array<CharArray> = this.split('\n').map { it.toCharArray() }.toTypedArray()
