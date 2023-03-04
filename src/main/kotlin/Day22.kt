import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    val day22 = Day22()
//    measureTimeMillis {
//        day22.solveA().also { result -> println(result) }
//    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }

    measureTimeMillis {
        day22.solveB().also { result -> println(result) }
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }
    // 135088 is too low
}

class Day22 {
    fun solveA(inputFileName: String = "input/${this.javaClass.name.drop(3)}.txt"): Any {
        val paths = Tile.Path.parseFlatFrom(inputFileName)
        val moves = Move.parseFrom(inputFileName)
        Token.path = paths.first()
        for (move in moves) {
            Token.makeMove(move)
        }
        return 1000 * Token.path.pos.y + 4 * Token.path.pos.x + Token.dir.ordinal
    }


    fun solveB(inputFileName: String = "input/${this.javaClass.name.drop(3)}_test.txt"): Any {
        val cube = prepareTestFaces()
        val paths = Tile.Path.parseCubicFrom(inputFileName, cube)
        val moves = Move.parseFrom(inputFileName)
        Token.path = paths.first()
        for (move in moves) {
            Token.makeMove(move)
        }

        println("y: ${Token.path.pos.y}, x: ${Token.path.pos.x}, dir: ${Token.dir}")
        return 1000 * Token.path.pos.y + 4 * Token.path.pos.x + Token.dir.ordinal
    }

    object Token {
        lateinit var path: Tile.Path
        var dir: Dir = Dir.E
        fun makeMove(move: Move) {
            println("Token at $path facing $dir, moving: $move")
            for (i in 0 until move.dist) {
                val oldFace = path.face
                path = when (dir) {
                    Dir.E -> path.east as Tile.Path? ?: break
                    Dir.S -> path.south as Tile.Path? ?: break
                    Dir.W -> path.west as Tile.Path? ?: break
                    Dir.N -> path.north as Tile.Path? ?: break
                }
                if (path.face != oldFace)
                    dir = dir.turn(oldFace.getTurn(path.face))
            }
            dir = dir.turn(move.turn)
        }
    }


    sealed class Tile(open val pos: Pos, open val face: Face) {
        data class Wall(override val pos: Pos, override val face: Face) : Tile(pos, face)

        data class Path(override val pos: Pos, override val face: Face) : Tile(pos, face) {
            var north: Tile? = null
            var east: Tile? = null
            var south: Tile? = null
            var west: Tile? = null

            companion object {

                fun parseFlatFrom(inputFileName: String): Collection<Path> {
                    val input = File(inputFileName).readText(Charsets.UTF_8)
                    val board = input
                        .split("\n\n").first() // trim away moves
                        .split('\n').map { it.toCharArray() }.toTypedArray()

                    board.forEach { println(it) }

                    return board
                        .flatMapIndexed { y, row ->
                            row.mapIndexed { x, c ->
                                when (c) {
                                    '.' -> Path(Pos(x + 1, y + 1), Face.Flat)
                                    '#' -> Wall(Pos(x + 1, y + 1), Face.Flat)
                                    else -> null
                                }
                            }.filterNotNull()
                        }.apply {
                            onEach { tile ->
                                if (tile is Path) {
                                    tile.north =
                                        (this.find { it.pos == Pos(tile.pos.x, tile.pos.y - 1) }
                                            ?: this.filter { it.pos.x == tile.pos.x }.maxBy { it.pos.y })
                                            .takeUnless { it is Wall }
                                    tile.south =
                                        (this.find { it.pos == Pos(tile.pos.x, tile.pos.y + 1) }
                                            ?: this.filter { it.pos.x == tile.pos.x }.minBy { it.pos.y })
                                            .takeUnless { it is Wall }
                                    tile.west =
                                        (this.find { it.pos == Pos(tile.pos.x - 1, tile.pos.y) }
                                            ?: this.filter { it.pos.y == tile.pos.y }.maxBy { it.pos.x })
                                            .takeUnless { it is Wall }
                                    tile.east =
                                        (this.find { it.pos == Pos(tile.pos.x + 1, tile.pos.y) }
                                            ?: this.filter { it.pos.y == tile.pos.y }.minBy { it.pos.x })
                                            .takeUnless { it is Wall }
                                }
                            }
                        }.filterIsInstance<Path>()
                }

                fun parseCubicFrom(inputFileName: String, faces: Collection<Face>): Collection<Path> {
                    val input = File(inputFileName).readText(Charsets.UTF_8)
                    val board = input
                        .split("\n\n").first() // trim away moves
                        .split('\n').map { it.toCharArray() }.toTypedArray()

                    board.forEach { println(it) }

                    return board.flatMapIndexed { y, row ->
                        row.mapIndexed { x, c ->
                            val pos = Pos(x + 1, y + 1)
                            when (c) {
                                '.' -> Path(pos, faces.find { pos in it.region }!!)
                                '#' -> Wall(pos, faces.find { pos in it.region }!!)
                                else -> null
                            }
                        }.filterNotNull()
                    }.apply {
                        val tilesBySide = groupBy { it.face }
                        filterIsInstance<Path>().onEach { tile -> tile.findAndSetAdjacentTiles(tilesBySide) }
                    }.filterIsInstance<Path>()
                }

                fun Path.findAndSetAdjacentTiles(tilesByFace: Map<Face, Collection<Tile>>) {
                    val tilesOnSameSide = tilesByFace[this.face]!!
                    east = (tilesOnSameSide.find { it.pos == Pos(pos.x + 1, pos.y) }
                        ?: this.face.getEastEdgeOrNull(this)?.getLinkedTile(this, tilesByFace))
                        .takeUnless { it is Wall }
                    south = (tilesOnSameSide.find { it.pos == Pos(pos.x, pos.y + 1) }
                        ?: this.face.getSouthEdgeOrNull(this)?.getLinkedTile(this, tilesByFace))
                        .takeUnless { it is Wall }
                    west = (tilesOnSameSide.find { it.pos == Pos(pos.x - 1, pos.y) }
                        ?: this.face.getWestEdgeOrNull(this)?.getLinkedTile(this, tilesByFace))
                        .takeUnless { it is Wall }
                    north = (tilesOnSameSide.find { it.pos == Pos(pos.x, pos.y - 1) }
                        ?: this.face.getNorthEdgeOrNull(this)?.getLinkedTile(this, tilesByFace))
                        .takeUnless { it is Wall }
                }
            }
        }
    }

    data class Cube(val a: Face.A, val b: Face.B, val c: Face.C, val d: Face.D, val e: Face.E, val f: Face.F)

    sealed class Face(private val xRange: IntRange, private val yRange: IntRange) {
        lateinit var edgeInfo: Edge.Info
        var edgeLength: Int = xRange.count()
        val region: List<Pos>

        init {
            this.region = this.getPositions(xRange, yRange)
        }

        fun getTurn(face: Face): Turn {
            return when {
                this.edgeInfo.east.otherFace == face -> this.edgeInfo.east.turn
                this.edgeInfo.south.otherFace == face -> this.edgeInfo.south.turn
                this.edgeInfo.west.otherFace == face -> this.edgeInfo.west.turn
                this.edgeInfo.north.otherFace == face -> this.edgeInfo.north.turn
                else -> Turn.NOOP
            }
        }

        private fun getPositions(xRange: IntRange, yRange: IntRange): List<Pos> =
            xRange.crossProduct(yRange).map { (x, y) -> Pos(x, y) }

        class A(xRange: IntRange, yRange: IntRange) : Face(xRange, yRange)
        class B(xRange: IntRange, yRange: IntRange) : Face(xRange, yRange)
        class C(xRange: IntRange, yRange: IntRange) : Face(xRange, yRange)
        class D(xRange: IntRange, yRange: IntRange) : Face(xRange, yRange)
        class E(xRange: IntRange, yRange: IntRange) : Face(xRange, yRange)
        class F(xRange: IntRange, yRange: IntRange) : Face(xRange, yRange)
        object Flat : Face(0..0, 0..0)

        fun getEastEdgeOrNull(path: Tile.Path): Edge? = if (path.pos.x == xRange.last) this.edgeInfo.east else null
        fun getSouthEdgeOrNull(path: Tile.Path): Edge? = if (path.pos.y == yRange.last) this.edgeInfo.south else null
        fun getWestEdgeOrNull(path: Tile.Path): Edge? = if (path.pos.x == xRange.first) this.edgeInfo.west else null
        fun getNorthEdgeOrNull(path: Tile.Path): Edge? = if (path.pos.y == yRange.first) this.edgeInfo.north else null

        fun toLocalPos(pos: Pos): Pos = Pos(pos.x - xRange.first, pos.y - yRange.first)
        fun toGlobalPos(pos: Pos): Pos = Pos(pos.x + xRange.first, pos.y + yRange.first)

    }

    data class Pos(val x: Int, val y: Int)

    enum class Dir {
        E, S, W, N;

        fun turn(turn: Turn): Dir = when (turn) {
            Turn.R -> Dir.values()[(this.ordinal + 1).mod(4)]
            Turn.L -> Dir.values()[(this.ordinal - 1).mod(4)]
            Turn.BACK -> Dir.values()[(this.ordinal + 2).mod(4)]
            Turn.NOOP -> this
        }
    }

    enum class Turn { R, L, BACK, NOOP }
    data class Move(val dist: Int, val turn: Turn) {
        companion object {
            fun parseFrom(inputFileName: String): Collection<Move> {
                val input = File(inputFileName).readText(Charsets.UTF_8)
                val moveString = input.split("\n\n").last() // trim away board

                val pattern = "(?<dist>\\d+)(?<turn>([RL])?)".toRegex()
                return pattern.findAll(moveString)
                    .map { match ->
                        val (dist, turn) = listOf("dist", "turn").map { match.groups[it]?.value }
                        Move(
                            dist?.toInt() ?: 0,
                            Turn.valueOf(turn.takeUnless { it.isNullOrEmpty() } ?: "NOOP"))
                    }.toList()
            }
        }

        private fun prepareInputFaces(): Set<Face> {
            val A = Face.A(51..100, 1..50)
            val B = Face.B(101..150, 1..50)
            val C = Face.C(51..100, 51..100)
            val D = Face.D(1..50, 101..150)
            val E = Face.E(51..100, 101..150)
            val F = Face.F(1..50, 151..200)
            val cube = setOf(A, B, C, D, E, F)

            /*
             AB
             C
            DE
            F
             */

            A.edgeInfo = Edge.Info(
                Edge.E(B),
                Edge.S(C),
                Edge.W(D, Turn.BACK),
                Edge.N(F, Turn.R)
            )
            B.edgeInfo = Edge.Info(
                Edge.E(E, Turn.BACK),
                Edge.S(C, Turn.R),
                Edge.W(A),
                Edge.N(F, Turn.BACK)
            )
            C.edgeInfo = Edge.Info(
                Edge.E(B, Turn.L),
                Edge.S(E, Turn.L),
                Edge.W(B),
                Edge.N(A, Turn.R)
            )
            D.edgeInfo = Edge.Info(
                Edge.E(E),
                Edge.S(F),
                Edge.W(A, Turn.BACK),
                Edge.N(C, Turn.R)
            )
            E.edgeInfo = Edge.Info(
                Edge.E(B, Turn.BACK),
                Edge.S(F, Turn.R),
                Edge.W(D),
                Edge.N(C)
            )
            F.edgeInfo = Edge.Info(
                Edge.E(E, Turn.L),
                Edge.S(B, Turn.BACK),
                Edge.W(A, Turn.L),
                Edge.N(D)
            )
            return cube
        }
    }

    sealed class Edge(val otherFace: Face, val turn: Turn) {

        open fun getLinkedTile(path: Tile.Path, tilesByFace: Map<Face, Collection<Tile>>): Tile? {
            val otherTiles = tilesByFace[otherFace]!!
            val localPos = path.face.toLocalPos(path.pos)
            val max = path.face.edgeLength - 1
            val newPos = when (this) {
                is E -> when (turn) {
                    Turn.R -> Pos(max - localPos.y, 0)
                    Turn.L -> Pos(localPos.y, max)
                    Turn.BACK -> Pos(max, max - localPos.y)
                    Turn.NOOP -> Pos(0, localPos.y)
                }

                is S -> when (turn) {
                    Turn.R -> Pos(max, localPos.x)
                    Turn.L -> Pos(0, max - localPos.x)
                    Turn.BACK -> Pos(max - localPos.x, max)
                    Turn.NOOP -> Pos(localPos.x, 0)
                }

                is W -> when (turn) {
                    Turn.R -> Pos(max - localPos.y, max)
                    Turn.L -> Pos(localPos.y, 0)
                    Turn.BACK -> Pos(0, max - localPos.y)
                    Turn.NOOP -> Pos(max, localPos.y)
                }

                is N -> when (turn) {
                    Turn.R -> Pos(0, localPos.x)
                    Turn.L -> Pos(max, max - localPos.x)
                    Turn.BACK -> Pos(max - localPos.x, 0)
                    Turn.NOOP -> Pos(localPos.x, max)
                }
            }

            return otherTiles.find { otherFace.toLocalPos(it.pos) == newPos }
        }

        class E(to: Face, turn: Turn = Turn.NOOP) : Edge(to, turn)

        class S(to: Face, turn: Turn = Turn.NOOP) : Edge(to, turn)

        class W(to: Face, turn: Turn = Turn.NOOP) : Edge(to, turn)

        class N(to: Face, turn: Turn = Turn.NOOP) : Edge(to, turn)
        data class Info(val east: E, val south: S, val west: W, val north: N)
    }

    private fun prepareTestFaces(): Set<Face> {
        val A = Face.A(9..12, 1..4)
        val B = Face.B(1..4, 5..8)
        val C = Face.C(5..8, 5..8)
        val D = Face.D(9..12, 5..8)
        val E = Face.E(9..12, 9..12)
        val F = Face.F(13..16, 9..12)
        val cube = setOf(A, B, C, D, E, F)

        A.edgeInfo = Edge.Info(
            Edge.E(F, Turn.BACK),
            Edge.S(D),
            Edge.W(C, Turn.L),
            Edge.N(B, Turn.BACK)
        )
        B.edgeInfo = Edge.Info(
            Edge.E(C),
            Edge.S(E, Turn.BACK),
            Edge.W(F, Turn.R),
            Edge.N(A, Turn.BACK)
        )
        C.edgeInfo = Edge.Info(
            Edge.E(D),
            Edge.S(E, Turn.L),
            Edge.W(B),
            Edge.N(A, Turn.R)
        )
        D.edgeInfo = Edge.Info(
            Edge.E(F, Turn.R),
            Edge.S(E),
            Edge.W(C),
            Edge.N(A)
        )
        E.edgeInfo = Edge.Info(
            Edge.E(F),
            Edge.S(B, Turn.BACK),
            Edge.W(C, Turn.R),
            Edge.N(D)
        )
        F.edgeInfo = Edge.Info(
            Edge.E(A, Turn.BACK),
            Edge.S(B, Turn.L),
            Edge.W(E),
            Edge.N(D, Turn.L)
        )
        return cube
    }
}


private fun IntRange.crossProduct(other: IntRange) =
    this.flatMap { n -> List(other.count()) { n } zip other }

