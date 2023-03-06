import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    val day23 = Day23()
    measureTimeMillis {
        day23.solveA("input/23_test.txt").also { result -> println(result) }
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }
}

class Day23 {

    /*
    algo:
    expand board if necessary
    iterate over board
    for each '#', check in directions for elf
        if there is an elf, check again
            if there is an 'X', do nothing
            if there is an '@', place an 'X' instead and replace other 'O' with an '#'
            if there is space, place an '@' and replace '#' by an 'O'
    modify order of directions being checked

     */
    fun solveA(inputFileName: String = "input/${this.javaClass.name.drop(3)}.txt", moves: Int = 10): Any {

        var board = File(inputFileName).readText(Charsets.UTF_8).toBoard()
        val dirCycle = cycleDirs()
        val elf = '#'
        val emptyTile = '.'
        val conflictedTarget = 'X'

        repeat(moves) {
            val dirs = dirCycle.next()
//            board.onEach { println(it) }
            println("must board be expanded: ${boardMustBeExpanded(board)}")
            println("board height before: ${board.size}")

            if (boardMustBeExpanded(board)) board = expandBoard(board)
            board.onEach { println(it) }
            println("board height after: ${board.size}")

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

                    // check if no other elf has proposed to walk there already
                    val (nx, ny) = getCoordsInDir(x to y, emptyDir) // neighbour coords
                    val neighbourInDir = board[ny][nx]
                    when (neighbourInDir) {
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
            board.onEach { println(it) }

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


        board.onEach { println(it) }

        return board.sumOf { row -> row.count { it == emptyTile } }
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

    fun boardMustBeExpanded(board: Array<CharArray>, elf: Char = '#'): Boolean =
        elf in board.first() || elf in board.last() || board.any { it.first() == elf } || board.any { it.last() == elf }
}

private fun String.toBoard(): Array<CharArray> = this.split('\n').map { it.toCharArray() }.toTypedArray()
