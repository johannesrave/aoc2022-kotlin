import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    val day22 = Day22()
    measureTimeMillis {
        day22.solveA().also { result -> println(result) }
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }
    // 22382838633806
}

class Day22 {
    fun solveA(inputFileName: String = "input/${this.javaClass.name.drop(3)}.txt"): Any {
        val paths = Tile.Path.parseFrom(inputFileName)
        val moves = Move.parseFrom(inputFileName)
        Token.path = paths.first()
        for (move in moves){
            Token.makeMove(move)
        }
        return 1000 * Token.path.pos.y + 4 * Token.path.pos.x + Token.dir.ordinal
    }

    object Token {
        lateinit var path: Tile.Path
        var dir: Dir = Dir.E
        fun makeMove(move: Move) {
            println("Token at $path facing $dir, moving: $move")
            for (i in 0 until move.dist) {
                path = when (dir) {
                    Dir.E -> path.right as Tile.Path? ?: break
                    Dir.S -> path.down as Tile.Path? ?: break
                    Dir.W -> path.left as Tile.Path? ?: break
                    Dir.N -> path.up as Tile.Path? ?: break
                }
            }
            dir = dir.turn(move.turn)
        }
    }


    sealed class Tile(open val pos: Pos) {
        data class Path(override val pos: Pos) : Tile(pos) {
            var up: Tile? = null
            var right: Tile? = null
            var down: Tile? = null
            var left: Tile? = null
            companion object {
                fun parseFrom(inputFileName: String): Collection<Path> {
                    val input = File(inputFileName).readText(Charsets.UTF_8)
                    val board = input
                        .split("\n\n").first() // trim away moves
                        .split('\n').map { it.toCharArray() }.toTypedArray()

                    board.forEach { println(it) }

                    return board
                        .flatMapIndexed { y, row ->
                            row.mapIndexed { x, c ->
                                when (c) {
                                    '.' -> Path(Pos(x + 1, y + 1))
                                    '#' -> Wall(Pos(x + 1, y + 1))
                                    else -> null
                                }
                            }.filterNotNull()
                        }.apply {
                            onEach { tile ->
                                if (tile is Path) {
                                    tile.up =
                                        (this.find { it.pos == Pos(tile.pos.x, tile.pos.y - 1) }
                                            ?: this.filter { it.pos.x == tile.pos.x }.maxBy { it.pos.y })
                                            .takeUnless { it is Wall }
                                    tile.down =
                                        (this.find { it.pos == Pos(tile.pos.x, tile.pos.y + 1) }
                                            ?: this.filter { it.pos.x == tile.pos.x }.minBy { it.pos.y })
                                            .takeUnless { it is Wall }
                                    tile.left =
                                        (this.find { it.pos == Pos(tile.pos.x - 1, tile.pos.y) }
                                            ?: this.filter { it.pos.y == tile.pos.y }.maxBy { it.pos.x })
                                            .takeUnless { it is Wall }
                                    tile.right =
                                        (this.find { it.pos == Pos(tile.pos.x + 1, tile.pos.y) }
                                            ?: this.filter { it.pos.y == tile.pos.y }.minBy { it.pos.x })
                                            .takeUnless { it is Wall }
                                }
                            }
                        }.filterIsInstance<Path>()
                }

            }
        }

        class Wall(override val pos: Pos) : Tile(pos)
    }

    data class Pos(val x: Int = 1, val y: Int = 1)

    enum class Dir {
        E, S, W, N;

        fun turn(turn: Turn): Dir = when (turn) {
            Turn.R -> Dir.values()[(this.ordinal + 1).mod(4)]
            Turn.L -> Dir.values()[(this.ordinal - 1).mod(4)]
            Turn.NOOP -> this
        }
    }

    enum class Turn { R, L, NOOP }
    data class Move(val dist: Int, val turn: Turn) {
        companion object {
            fun parseFrom(inputFileName: String): Collection<Move> {
                val input = File(inputFileName).readText(Charsets.UTF_8)
                val moveString = input.split("\n\n").last() // trim away board

                val pattern = "(?<dist>\\d+)(?<turn>([RL])?)".toRegex()
                return pattern.findAll(moveString)
                    .map { match ->
                        val (dist, turn) = listOf("dist", "turn").map { match.groups[it]?.value }
                        return@map Move(
                            dist?.toInt() ?: 0,
                            Turn.valueOf(turn.takeUnless { it.isNullOrEmpty() } ?: "NOOP"))
                    }.toList()
//                    .onEach { println(it) }


            }
        }

    }
}