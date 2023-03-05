import Day22.Cube.Face
import Day22.Cube.Topology
import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    val day22 = Day22()
//    measureTimeMillis {
//        day22.solveA().also { result -> println(result) }
//    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }


    measureTimeMillis {
        day22.solveB(topology = Day22.inputTopology).also { result -> println(result) }
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }
    // 135088 is too low
}

class Day22 {
//    fun solveA(inputFileName: String = "input/${this.javaClass.name.drop(3)}.txt"): Any {
//        val tiles = Tile.parseFlatFrom(inputFileName)
//        val moves = Move.parseFrom(inputFileName)
//        Token.tile = tiles.first()
//        for (move in moves) {
//            Token.makeMove(move)
//        }
//        return 1000 * Token.tile.pos.y + 4 * Token.tile.pos.x + Token.dir.ordinal
//    }


    fun solveB(inputFileName: String = "input/${this.javaClass.name.drop(3)}.txt", topology: Topology): Any {
//        val cube = prepareTestFaces()
        val cube = Cube(inputFileName, topology)
//        val tiles = Tile.parseCubicFrom(inputFileName, cube)
        val moves = Move.parseFrom(inputFileName)
        val token = Token(cube.faces[Face.A]!!.values.first())
        for (move in moves) {
            token.makeMove(move)
//            println("y: ${token.tile.pos.y}, x: ${token.tile.pos.x}, dir: ${token.dir}")
        }

        println("y: ${token.tile.pos.y + 1}, x: ${token.tile.pos.x + 1}, dir: ${token.dir}")
        return (1000 * (token.tile.pos.y + 1)) + (4 * (token.tile.pos.x + 1)) + token.dir.ordinal
    }

    data class Token (var tile: Tile, var dir: Dir = Dir.E){
        fun makeMove(move: Move) {
            for (i in 0 until move.dist) {
                val (linkTurn, linkTile) = tile.linkTowards[dir] ?: break
                tile = linkTile
                dir = turn(linkTurn)
            }
            dir = turn(move.turn)
        }

        private fun turn(turn: Turn): Dir = when (turn) {
            Turn.R -> Dir.values()[(dir.ordinal + 1).mod(4)]
            Turn.L -> Dir.values()[(dir.ordinal - 1).mod(4)]
            Turn.BACK -> Dir.values()[(dir.ordinal + 2).mod(4)]
            Turn.NOOP -> dir
        }
    }

    data class Cube(val inputFileName: String, val topology: Topology) {
        val arrays = parseFileToArrays(inputFileName)
        val faces = topology.faceRegions
            .mapValues { (_, region) -> filterWalls(region, arrays) }
            .mapValues { (_, facePositions) -> facePositions.associateWith { Tile(it) } }
            .apply { linkTilesInternally(this) }
            .apply { linkEdgeTiles(this, topology) }

        fun parseFileToArrays(inputFileName: String): Array<CharArray> {
            return File(inputFileName).readText(Charsets.UTF_8)
                .split("\n\n").first() // trim away moves
                .split('\n').map { it.toCharArray() }.toTypedArray()
        }

        fun filterWalls(region: List<Pos>, arrays: Array<CharArray>) = region.filter { (x, y) -> arrays[y][x] == '.' }

        private fun linkTilesInternally(faces: Map<Face, Map<Pos, Tile>>) {
            faces.onEach { (_, tiles) ->
                tiles.onEach { (pos, tile) ->
                    tile.linkTowards[Dir.E] = tiles[Pos(pos.x + 1, pos.y)]?.let { Turn.NOOP to it }
                    tile.linkTowards[Dir.S] = tiles[Pos(pos.x, pos.y + 1)]?.let { Turn.NOOP to it }
                    tile.linkTowards[Dir.W] = tiles[Pos(pos.x - 1, pos.y)]?.let { Turn.NOOP to it }
                    tile.linkTowards[Dir.N] = tiles[Pos(pos.x, pos.y - 1)]?.let { Turn.NOOP to it }
                }
            }
        }

        private fun linkEdgeTiles(faces: Map<Face, Map<Pos, Tile>>, topology: Topology) {
            faces.onEach { (face, tiles) ->

                Dir.values()
                    .associateWith { dir -> tiles.filterKeys { it in topology.faceEdges[face]!![dir]!! } }
                    .onEach { (dir, edgeTiles) ->
                        edgeTiles.onEach { (_, tile) ->
                            val (linkFace, linkPos, linkTurn) = topology.getLinkFaceAndPosition(tile, face, dir)
                            tile.linkTowards[dir] = faces[linkFace]!![linkPos]?.let { linkTurn to it }
                        }
                    }
            }
        }

        enum class Face { A, B, C, D, E, F }

        data class Topology(val edgeLength: Int, val facePositions: FacePositions, val faceRelations: FaceRelations) {
            val faceRegions = facePositions.faces.mapValues { (_, facePos) ->
                val xStart = (facePos.x * edgeLength)
                val xEnd = xStart + edgeLength
                val yStart = (facePos.y * edgeLength)
                val yEnd = yStart + edgeLength

                (xStart until xEnd).crossProduct(yStart until yEnd).map { (x, y) -> Pos(x, y) }
            }

            val faceEdges = faceRegions.mapValues { (_, region) ->
                val eastX = region.maxOf { it.x }
                val southY = region.maxOf { it.y }
                val westX = region.minOf { it.x }
                val northY = region.minOf { it.y }
                mapOf(
                    Dir.E to region.filter { it.x == eastX },
                    Dir.S to region.filter { it.y == southY },
                    Dir.W to region.filter { it.x == westX },
                    Dir.N to region.filter { it.y == northY }
                )
            }

            fun getLinkFaceAndPosition(tile: Tile, face: Face, dir: Dir): Triple<Face, Pos, Turn> {
                val localPos = toLocalPos(tile.pos, face)
                val (otherFace, turn) = this.faceRelations.faces[face]!![dir]!!
                val max = edgeLength - 1
                val newPos = when (dir) {
                    Dir.E -> when (turn) {
                        Turn.R -> Pos(max - localPos.y, 0)
                        Turn.L -> Pos(localPos.y, max)
                        Turn.BACK -> Pos(max, max - localPos.y)
                        Turn.NOOP -> Pos(0, localPos.y)
                    }

                    Dir.S -> when (turn) {
                        Turn.R -> Pos(max, localPos.x)
                        Turn.L -> Pos(0, max - localPos.x)
                        Turn.BACK -> Pos(max - localPos.x, max)
                        Turn.NOOP -> Pos(localPos.x, 0)
                    }

                    Dir.W -> when (turn) {
                        Turn.R -> Pos(max - localPos.y, max)
                        Turn.L -> Pos(localPos.y, 0)
                        Turn.BACK -> Pos(0, max - localPos.y)
                        Turn.NOOP -> Pos(max, localPos.y)
                    }

                    Dir.N -> when (turn) {
                        Turn.R -> Pos(0, localPos.x)
                        Turn.L -> Pos(max, max - localPos.x)
                        Turn.BACK -> Pos(max - localPos.x, 0)
                        Turn.NOOP -> Pos(localPos.x, max)
                    }
                }
                return Triple(otherFace, toGlobalPos(newPos, otherFace), turn)
            }

            private fun toLocalPos(globalPos: Pos, face: Face): Pos {
                val xOffset = this.facePositions.faces[face]!!.x * edgeLength
                val yOffset = this.facePositions.faces[face]!!.y * edgeLength
                return Pos(globalPos.x - xOffset, globalPos.y - yOffset)
            }

            private fun toGlobalPos(localPos: Pos, face: Face): Pos {
                val xOffset = this.facePositions.faces[face]!!.x * edgeLength
                val yOffset = this.facePositions.faces[face]!!.y * edgeLength
                return Pos(localPos.x + xOffset, localPos.y + yOffset)
            }

            data class FacePositions(val faces: Map<Face, Pos>)

            data class FaceRelations(val faces: Map<Face, Map<Dir, Pair<Face, Turn>>>)
        }
    }

    data class Pos(val x: Int, val y: Int)

    enum class Dir { E, S, W, N }

    enum class Turn { R, L, BACK, NOOP }
    data class Tile(
        val pos: Pos,
        val linkTowards: MutableMap<Dir, Pair<Turn, Tile>?> = Dir.values().associateWith { null }.toMutableMap()
    ) {
        override fun toString(): String {
            return "Tile at $pos, linked to ${
                linkTowards.map { (dir, link) ->
                    val (turn, tile) = link ?: return@map "$dir: nothing, "
                    "$dir: ${tile.pos}${if (turn != Turn.NOOP) "turning $turn, " else ", "}"
                }
            }"
        }

    }

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
    }

    companion object {

        val inputTopology = Topology(
            50,
            Topology.FacePositions(
                mapOf(
                    Face.A to Pos(1, 0),
                    Face.B to Pos(2, 0),
                    Face.C to Pos(1, 1),
                    Face.D to Pos(0, 2),
                    Face.E to Pos(1, 2),
                    Face.F to Pos(1, 3),
                )
            ),
            Topology.FaceRelations(
                mapOf(
                    Face.A to mapOf(
                        Dir.E to (Face.B to Turn.NOOP),
                        Dir.S to (Face.C to Turn.NOOP),
                        Dir.W to (Face.D to Turn.BACK),
                        Dir.N to (Face.F to Turn.R)
                    ),
                    Face.B to mapOf(
                        Dir.E to (Face.E to Turn.BACK),
                        Dir.S to (Face.C to Turn.R),
                        Dir.W to (Face.A to Turn.NOOP),
                        Dir.N to (Face.F to Turn.BACK)
                    ),
                    Face.C to mapOf(
                        Dir.E to (Face.B to Turn.L),
                        Dir.S to (Face.E to Turn.L),
                        Dir.W to (Face.B to Turn.NOOP),
                        Dir.N to (Face.A to Turn.R)
                    ),
                    Face.D to mapOf(
                        Dir.E to (Face.E to Turn.NOOP),
                        Dir.S to (Face.F to Turn.NOOP),
                        Dir.W to (Face.A to Turn.BACK),
                        Dir.N to (Face.C to Turn.R)
                    ),
                    Face.E to mapOf(
                        Dir.E to (Face.B to Turn.BACK),
                        Dir.S to (Face.F to Turn.R),
                        Dir.W to (Face.D to Turn.NOOP),
                        Dir.N to (Face.C to Turn.NOOP)
                    ),
                    Face.F to mapOf(
                        Dir.E to (Face.E to Turn.L),
                        Dir.S to (Face.B to Turn.BACK),
                        Dir.W to (Face.A to Turn.L),
                        Dir.N to (Face.D to Turn.NOOP)
                    ),
                )
            )
        )

        val testTopology = Topology(
            4,
            Topology.FacePositions(
                mapOf(
                    Face.A to Pos(2, 0),
                    Face.B to Pos(0, 1),
                    Face.C to Pos(1, 1),
                    Face.D to Pos(2, 1),
                    Face.E to Pos(2, 2),
                    Face.F to Pos(3, 2),
                )
            ),
            Topology.FaceRelations(
                mapOf(
                    Face.A to mapOf(
                        Dir.E to (Face.F to Turn.BACK),
                        Dir.S to (Face.D to Turn.NOOP),
                        Dir.W to (Face.C to Turn.L),
                        Dir.N to (Face.B to Turn.BACK)
                    ),
                    Face.B to mapOf(
                        Dir.E to (Face.C to Turn.NOOP),
                        Dir.S to (Face.E to Turn.BACK),
                        Dir.W to (Face.F to Turn.R),
                        Dir.N to (Face.A to Turn.BACK)
                    ),
                    Face.C to mapOf(
                        Dir.E to (Face.D to Turn.NOOP),
                        Dir.S to (Face.E to Turn.L),
                        Dir.W to (Face.B to Turn.NOOP),
                        Dir.N to (Face.A to Turn.R)
                    ),
                    Face.D to mapOf(
                        Dir.E to (Face.F to Turn.R),
                        Dir.S to (Face.E to Turn.NOOP),
                        Dir.W to (Face.C to Turn.NOOP),
                        Dir.N to (Face.A to Turn.NOOP)
                    ),
                    Face.E to mapOf(
                        Dir.E to (Face.F to Turn.NOOP),
                        Dir.S to (Face.B to Turn.BACK),
                        Dir.W to (Face.C to Turn.R),
                        Dir.N to (Face.D to Turn.NOOP)
                    ),
                    Face.F to mapOf(
                        Dir.E to (Face.A to Turn.BACK),
                        Dir.S to (Face.B to Turn.L),
                        Dir.W to (Face.E to Turn.NOOP),
                        Dir.N to (Face.D to Turn.L)
                    ),
                )
            )
        )
    }
}

private fun IntRange.crossProduct(other: IntRange) =
    this.flatMap { n -> List(other.count()) { n } zip other }
