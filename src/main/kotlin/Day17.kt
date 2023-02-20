fun main() {
    val day17 = Day17("input/17_test.txt")
    println(day17.solveA(2022))
    // 3090

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

    }

    fun solveA(rounds: Int): Any {

        val caveWidth = 7
        val cave = Cave(caveWidth, rounds * 4)
        cave.fallingRock = Rock.nextAt(Pos(2, 3))

        var rockCounter = 1

        val infiniteMoves = moves.iterator()
//        for (round in )
        while (rockCounter <= rounds) {
            val move = infiniteMoves.next()

//            println(cave.fallingRock)
////            println(cave)
//            println("MOVING $move")
            cave.fallingRock.moveSidewaysIfPossible(move, cave)
////            println(cave)
//            println("MOVING DOWN")

            try {
                cave.fallingRock.moveDownOrStop(cave)
            } catch (e: Exception) {
                rockCounter++
                cave.addRock(cave.fallingRock)
                cave.fallingRock = Rock.nextAt(Pos(2, cave.highestTile + 4))
//                println(e)
                println("spawning rock no $rockCounter: ${cave.fallingRock.name} at ${cave.fallingRock.bottomEdge()} / ${cave.fallingRock.leftEdge()}")
//                println(cave)
            }
        }

        return cave.highestTile+1
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
        fun leftIsBlocked(pos: Pos) = pos.x == 0 || tiles[pos.y][pos.x-1]
        fun rightIsBlocked(pos: Pos) = pos.x == width-1 || tiles[pos.y][pos.x+1]

        override fun toString(): String {
            return tiles
                .mapIndexed { cy, row ->
                    row.mapIndexed { cx, col ->
                        when {
                            fallingRock.shape.any { (x, y) -> x == cx && y == cy } -> 'T'
                            col -> '#'
                            else -> '`'
                        }
                    }.joinToString("", prefix = "${cy.toString().padStart(3)}: ")
                }
                .take(highestTile + 8)
                .reversed()
                .joinToString("\n")
                .plus("highest tile: $highestTile")
        }
    }

    enum class Rock(val originalShape: List<Pos>) {

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

        var shape = originalShape.map { it.copy() }

        fun moveDownOrStop(cave: Cave) {
            if (shape.none { cave.downIsBlocked(it) }) shape.forEach { tile -> tile.y-- }
            else throw Exception("rock can't fall deeper, coming to rest")
        }

        fun moveSidewaysIfPossible(move: Move, cave: Cave) {
            when (move) {
                Move.L -> if (shape.none { cave.leftIsBlocked(it) }) shape.forEach { tile -> tile.x-- }
                Move.R -> if (shape.none { cave.rightIsBlocked(it) }) shape.forEach { tile -> tile.x++ }
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
                .also {
                    it.shape = it.originalShape.map { it.copy() }
                    it.shape.forEach { tile -> tile.y += newPos.y; tile.x += newPos.x }
                }
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

