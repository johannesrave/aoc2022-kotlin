import kotlin.system.measureTimeMillis

fun main() {
    // val day17a = Day17("input/17.txt")
    // measureTimeMillis {
    //     day17a.calculateHeight(2022)
    //         .also { result -> println(result) }
    // }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }
    // 3090

    val day17b = Day17("input/17.txt")
    measureTimeMillis {
        day17b.calculateHeight(1000000000000)
            .also { result -> println(result) }
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }
    // 1530057803453

}

class Day17(inputFileName: String) : Day(inputFileName) {
    fun calculateHeight(numOfRounds: Long): Any {

        val cave = Cave()
        val moves = Move.parseFrom(input)

        val startSequence = mutableListOf<Int>()
        val periodicSequence = mutableListOf<Int>()

        for (i in 0 until numOfRounds) {
            var fallingRock: Rock? = Rock.nextAt(cave.spawnPosition())
            while (fallingRock != null) {
                val move = moves.next()
                fallingRock.moveSidewaysIfPossible(move, cave)

                if (fallingRock.canMoveDown(cave)) {
                    fallingRock.moveDown()
                } else {
                    fallingRock.comeToRest(cave)
                    fallingRock = null
                    detectPeriod(cave.heightIncrements)?.run {
                        println("found period at $this")
                        // take this shortcut if a periodically repeating pattern could be detected
                        startSequence += cave.heightIncrements.take(first).toMutableList()
                        periodicSequence += cave.heightIncrements.subList(first, last)
                        return calculateHeightUsingPeriods(numOfRounds, startSequence, periodicSequence)
                    }
                }
            }
        }

        return cave.height
    }

    private fun detectPeriod(data: MutableList<Int>, sampleLength: Int = 30): IntProgression? {
        // the idea behind this shortcut is to detect repeating patterns in the height gains of the
        // cave, caused by the interaction of the two infinite but periodic feeds in the rocks and
        // the moves. if such periods are detected, then the rest of the calculation collapses into
        // simple arithmetics, see "calculateHeightUsingPeriods()".

        if (data.size < sampleLength * 2) return null

        val dataString = data.joinToString("")
        val pattern = dataString.takeLast(sampleLength).toRegex()
        val stringToSearch = dataString.dropLast(sampleLength)

        return pattern.find(stringToSearch)
            ?.run { return range.first..(data.size - sampleLength) }
    }

    private fun calculateHeightUsingPeriods(
        numOfRounds: Long,
        startSequence: MutableList<Int>,
        periodicSequence: MutableList<Int>
    ): Long {
        // if periods could be detected, then all the rounds can be split into three sequences:
        // 1. an initial sequence that doesn't yet repeat but leads up to the period
        // 2. a periodically repeating sequence
        // 3. (in most cases) an incomplete periodic sequence (="tail"), if the number of rounds
        // doesn't coincide with the length of the first two sequences
        // calculating and summing up the height-gains for these sequences yields the final result.
        val roundsAfterStartingSequence = numOfRounds - startSequence.size
        val roundsForEndingSequence = roundsAfterStartingSequence % periodicSequence.size
        val roundsForPeriodicCalculation = roundsAfterStartingSequence - roundsForEndingSequence
        val numberOfPeriods = roundsForPeriodicCalculation / periodicSequence.size

        val heightAfterStartingSequence = startSequence.sum()
        val heightAfterPeriodicSequence = numberOfPeriods * periodicSequence.sum()
        val heightAfterEndingSequence = periodicSequence.take(roundsForEndingSequence.toInt()).sum()

        return heightAfterStartingSequence + heightAfterPeriodicSequence + heightAfterEndingSequence
    }

    class Cave (maximumHeight: Int = 5000){
        private val width = 7
        private val tiles = Array(maximumHeight) { Array(width) { false } }
        var height = -1
        val heightIncrements = mutableListOf<Int>()

        fun add(rock: Rock) {
            rock.shape.forEach { (x, y) -> tiles[y][x] = true }
            val prevHeight = height
            height = height.coerceAtLeast(rock.topEdge())
            heightIncrements += height - prevHeight
        }

        fun downIsBlocked(pos: Pos) = pos.y == 0 || tiles[pos.y - 1][pos.x]
        fun leftIsBlocked(pos: Pos) = pos.x == 0 || tiles[pos.y][pos.x - 1]
        fun rightIsBlocked(pos: Pos) = pos.x == width - 1 || tiles[pos.y][pos.x + 1]
        fun spawnPosition(): Pos = Pos(2, height + 4)
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
        val topEdge = { shape.maxOf { it.y } }
        val bottomEdge = { shape.minOf { it.y } }

        var shape = originalShape.map { it.copy() }

        fun canMoveDown(cave: Cave): Boolean = shape.none { cave.downIsBlocked(it) }
        fun moveDown() = shape.onEach { tile -> tile.y-- }

        fun moveSidewaysIfPossible(move: Move, cave: Cave) {
            when (move) {
                Move.L -> if (shape.none { cave.leftIsBlocked(it) }) shape.onEach { tile -> tile.x-- }
                Move.R -> if (shape.none { cave.rightIsBlocked(it) }) shape.onEach { tile -> tile.x++ }
            }
        }

        override fun toString(): String {
            return "${this.name} at ${this.leftEdge()} / ${this.bottomEdge()} (${
                shape.joinToString("-") { (x, y) -> "$x/$y" }
            })"
        }

        fun comeToRest(cave: Cave) {
            cave.add(this)

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

    enum class Move {
        L, R;

        companion object {
            internal fun parseFrom(input: String): Iterator<Move> =
                generateSequence(0) { it + 1 }
                    .map { input[it % input.length] }
                    .map {
                        when (it) {
                            '<' -> L
                            '>' -> R
                            else -> throw IllegalArgumentException()
                        }
                    }.iterator()
        }
    }

    data class Pos(var x: Int = 0, var y: Int = 0)
}

