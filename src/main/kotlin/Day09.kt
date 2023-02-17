import kotlin.system.measureTimeMillis

fun main() {
    val day09 = Day09("input/09.txt")
//    val day09 = Day09("input/09_test.txt")

    val solutionA = day09.solve(2)
    println(solutionA)
    val solutionB = day09.solve(10)
    println(solutionB)

    val elapsedTime = measureTimeMillis {
        day09.solve(10)
    }
    println("Time taken: $elapsedTime ms")
}

class Day09(inputFileName: String) : Day(inputFileName) {
    fun solve(length: Int): Any {
        val rope: MutableList<Pos> = List(length) { Pos() }.toList().toMutableList()
        val touchedByTail = rope.toMutableList()
        val moves = parse()

        moves.forEach { move ->
            (0 until move.dist).forEach { _ ->
                for (i in rope.indices) {
                    rope[i] = when (i) {
                        0 -> move.makeMove(rope[0])
                        else -> rope[i].follow(rope[i - 1])
                    }
                }
                touchedByTail.add(rope.last())
            }
        }
        return touchedByTail.distinct().count()
    }

    private fun parse(): List<Move> {
        return input
            .split('\n')
            .map {
                val (dir, dist) = it.split(' ')
                Move(Dir.valueOf(dir), dist.toInt())
            }
    }

    data class Move(private val dir: Dir, val dist: Int) {
        fun makeMove(pos: Pos) = dir.makeMove(pos)
    }

    data class Pos(val x: Int = 0, val y: Int = 0) {
        fun dist(to: Pos) = Dist(h = to.x - x, v = to.y - y)
        fun follow(to: Pos): Pos {
            val dist = dist(to)
            return when {
            // @formatter:off
                dist.h >  1 && dist.v >  1  -> Pos(to.x - 1, to.y - 1)
                dist.h < -1 && dist.v >  1  -> Pos(to.x + 1, to.y - 1)
                dist.h >  1 && dist.v < -1  -> Pos(to.x - 1, to.y + 1)
                dist.h < -1 && dist.v < -1  -> Pos(to.x + 1, to.y + 1)
                dist.v >  1                 -> Pos(   to.x,     to.y - 1)
                dist.v < -1                 -> Pos(   to.x,     to.y + 1)
                dist.h >  1                 -> Pos(to.x - 1,    to.y)
                dist.h < -1                 -> Pos(to.x + 1,    to.y)
                else -> this
            // @formatter:on
            }
        }
    }

    enum class Dir {

        U {
            override fun makeMove(pos: Pos): Pos = Pos(pos.x, pos.y + 1)
        },

        D {
            override fun makeMove(pos: Pos): Pos = Pos(pos.x, pos.y - 1)
        },

        L {
            override fun makeMove(pos: Pos): Pos = Pos(pos.x - 1, pos.y)
        },

        R {
            override fun makeMove(pos: Pos): Pos = Pos(pos.x + 1, pos.y)
        };

        abstract fun makeMove(pos: Pos): Pos
    }

    class Dist(val h: Int, val v: Int)
}



