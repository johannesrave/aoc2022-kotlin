fun main() {
    val day17 = Day17("input/17_test.txt")
    println (day17.solveA(2022))

}

class Day17(inputFileName: String) : Day(inputFileName) {
    private val moves: Sequence<Move>

    init {
        this.moves = parseMoves(input)
    }

    private fun parseMoves(input: String): Sequence<Move> {

        val inputChars = input.toCharArray()
        return generateSequence(0) { it + 1 }
            .map { inputChars[it % inputChars.size] }
            .map { Move.from(it) }

//        return input.toCharArray().map { Move.from(it) }.asSequence()
    }

    fun solveA(rounds: Int): Any {

//        val cave = Array(moves.count() * 4) { Array(7) { false } }
        val caveWidth = 7
        val cave = Cave(caveWidth, 6000)
        cave.fallingRock = Rock.nextAt(Pos(2, 3))

        var rockCounter = 1

        val infiniteMoves = moves.iterator()
        while (rockCounter <= rounds){
            val move = infiniteMoves.next()

            println(cave.fallingRock)
            println(cave)
            println("MOVING $move")
            cave.fallingRock.moveSidewaysIfPossible(move, cave.width)
            println(cave)
            println("MOVING DOWN")

            try {
                cave.fallingRock.moveDownOrStop(cave)
            } catch (e: Exception) {
                rockCounter++
                cave.addRock(cave.fallingRock)
                cave.fallingRock = Rock.nextAt(Pos(2, cave.highestTile + 4))
                println(e)
            }
        }

        return cave.highestTile
    }

    data class Cave(val width: Int, val height: Int) {
        private val tiles = Array(height) { Array(width) { false } }
        var highestTile = 0
        lateinit var fallingRock: Rock

        fun addRock(rock: Rock) {
            rock.shape.forEach { (x, y) -> tiles[y][x] = true }
            val minimumValue = rock.topEdge()
            highestTile = highestTile.coerceAtLeast(minimumValue)
        }

        fun downIsBlocked(pos: Pos) = pos.y == 0 || tiles[pos.y - 1][pos.x]

        override fun toString(): String {
            return tiles
                .mapIndexed { cy, row ->
                    row.mapIndexed { cx, col ->
                        when {
                            fallingRock.shape.any { (x, y) -> x == cx && y == cy } -> 'T'
                            col -> '#'
                            else -> '`'
                        }
                    }.joinToString("")
                }
                .take(highestTile + 6)
                .reversed()
                .joinToString("\n")
        }
    }

    enum class Rock(val shape: List<Pos>) {

        /**
         * Rock shapes:
         * A       B       C       D        E
         *                         #
         *         .#.     ..#     #
         *         ###     ..#     #        ##
         * ####    .#.     ###     #        ##
         * Reference position is always the lower left square of a rock.
         */

        A(listOf(Pos(0, 0), Pos(1, 0), Pos(2, 0), Pos(3, 0))),

        B(listOf(Pos(1, 0), Pos(0, 1), Pos(1, 1), Pos(1, 2), Pos(2, 1))),

        C(listOf(Pos(0, 0), Pos(1, 0), Pos(2, 0), Pos(2, 1), Pos(2, 2))),

        D(listOf(Pos(0, 0), Pos(0, 1), Pos(0, 2), Pos(0, 3))),

        E(listOf(Pos(0, 0), Pos(1, 0), Pos(0, 1), Pos(1, 1)));

        val leftEdge = { shape.minOf { it.x } }
        val rightEdge = { shape.maxOf { it.x } }
        val topEdge = { shape.maxOf { it.y } }

        val bottomEdge = { shape.minOf { it.y } }

        fun moveDownOrStop(cave: Cave) {
            if (shape.none { cave.downIsBlocked(it) }) shape.forEach { tile -> tile.y-- }
            else throw Exception("rock can't fall deeper, coming to rest")
        }

        fun moveSidewaysIfPossible(move: Move, width: Int) {
            when (move) {
                Move.L -> if (leftEdge() > 0) shape.forEach { tile -> tile.x-- }
                Move.R -> if (rightEdge() + 1 < width) shape.forEach { tile -> tile.x++ }
            }
        }

        override fun toString(): String {
            return "${this.name} at ${this.leftEdge()} / ${this.bottomEdge()} (${
                shape.map { (x, y) -> "$x/$y" }.joinToString("-")
            })"
        }

        companion object {

            private val rocks =
                generateSequence(0) { it + 1 }
                    .map { Rock.values()[it % Rock.values().size] }
                    .iterator()

            fun nextAt(newPos: Pos): Rock = rocks.next()
                .also { it.shape.forEach { tile -> tile.y += newPos.y; tile.x += newPos.x } }
        }
    }

    data class Pos(var x: Int = 0, var y: Int = 0)

    enum class Move {

        L, R;

        companion object {
            fun from(c: Char): Move {
                return when (c) {
                    '<' -> L
                    '>' -> R
                    else -> throw IllegalArgumentException()
                }
            }
        }
    }

}

