import kotlin.system.measureTimeMillis

fun main() {
    val day17 = Day17("input/17.txt")
    val elapsedTime = measureTimeMillis { day17.solveA(1000000000000).also { println(it) } }
    println("Time taken: $elapsedTime ms")
    // 3090

}

class Day17(inputFileName: String) : Day(inputFileName) {
    fun solveA(rounds: Long): Any {

        val cave = Cave(7)
        val moves = Move.getMoveSequenceFrom(input).iterator()
        cave.fallingRock = Rock.nextAt(Pos(2, 3))
        cave.addTiles(8)

        var rockCounter = 1L

        val sequenceOfIncrements = emptyList<Int>().toMutableList()
        var startSequence = emptyList<Int>().toMutableList()
        var periodicSequence = emptyList<Int>().toMutableList()
        val testSequenceLength = 30



        while (rockCounter <= rounds) {
            val move = moves.next()

            cave.fallingRock.moveSidewaysIfPossible(move, cave)

            try {
                cave.fallingRock.moveDownOrStop(cave)
            } catch (e: Exception) {
                rockCounter++
                val oldHighestTile = cave.highestTile
                cave.addRock(cave.fallingRock)
                sequenceOfIncrements += cave.highestTile - oldHighestTile

                if (sequenceOfIncrements.size > testSequenceLength) {
                    val message = sequenceOfIncrements.joinToString("")

                    val pattern = message.takeLast(testSequenceLength).toRegex()
                    val matchResult = pattern.find(message.dropLast(testSequenceLength), 0)
                    if (matchResult != null) {
                        println("found period from ${matchResult.range.first} to ${sequenceOfIncrements.size}")
                        val period = matchResult.range.first..sequenceOfIncrements.size - testSequenceLength
                        startSequence = sequenceOfIncrements.take(period.first).toMutableList()
                        periodicSequence = sequenceOfIncrements.subList(period.first, period.last)
                        break
                    }
                }

                cave.fallingRock = Rock.nextAt(Pos(2, cave.highestTile + 4))
                cave.addTiles(4)
            }
        }

        println(startSequence)
        println(periodicSequence)

        val roundsAfterStartingSequence = rounds - startSequence.size
        val roundsForEndingSequence = roundsAfterStartingSequence % periodicSequence.size
        val roundsForPeriodicCalculation = roundsAfterStartingSequence - roundsForEndingSequence
        val numberOfPeriods = roundsForPeriodicCalculation / periodicSequence.size

        println(startSequence.size)
        println(roundsForPeriodicCalculation)
        println(roundsForEndingSequence)

        println(startSequence.size + roundsForPeriodicCalculation + roundsForEndingSequence)

        val heightAfterStartingSequence = startSequence.sum()
        val heightAfterPeriodicSequence = numberOfPeriods * periodicSequence.sum()
        val heightAfterEndingSequence = periodicSequence.take(roundsForEndingSequence.toInt()).sum()

        val heightAfterAllRounds =
            heightAfterStartingSequence + heightAfterPeriodicSequence + heightAfterEndingSequence + 1

        return heightAfterAllRounds
    }

    data class Cave(val width: Int) {
        private val tiles = MutableList(1) { Array(width) { false } }
        var highestTile = 0
        lateinit var fallingRock: Rock

        fun addRock(rock: Rock) {
            rock.shape.forEach { (x, y) -> tiles[y][x] = true }
            val minimumValue = rock.topEdge()
            highestTile = highestTile.coerceAtLeast(minimumValue)
        }

        fun downIsBlocked(pos: Pos) = pos.y == 0 || tiles[pos.y - 1][pos.x]
        fun leftIsBlocked(pos: Pos) = pos.x == 0 || tiles[pos.y][pos.x - 1]
        fun rightIsBlocked(pos: Pos) = pos.x == width - 1 || tiles[pos.y][pos.x + 1]

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

        fun addTiles(i: Int) {
            tiles += MutableList(i) { Array(width) { false } }
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
                shape.joinToString("-") { (x, y) -> "$x/$y" }
            })"
        }

        companion object {

            private val rocks =
                generateSequence(0) { it + 1 }
                    .map { Rock.values()[it % Rock.values().size] }
                    .iterator()

            fun nextAt(newPos: Pos): Rock = rocks.next()
                .also { rock ->
                    rock.shape = rock.originalShape
                        .map { tile -> tile.copy() }
                        .onEach { tile -> tile.y += newPos.y; tile.x += newPos.x }
                }
        }
    }

    data class Pos(var x: Int = 0, var y: Int = 0)

    enum class Move {
        L, R;

        companion object {
            internal fun getMoveSequenceFrom(input: String): Sequence<Move> = generateSequence(0) { it + 1 }
                .map { input[it % input.length] }
                .map {
                    when (it) {
                        '<' -> L
                        '>' -> R
                        else -> throw IllegalArgumentException()
                    }
                }
        }
    }
}

