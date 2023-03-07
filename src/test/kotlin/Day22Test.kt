import Day22.*
import Day22.Companion.inputTopology
import Day22.Companion.testTopology
import Day22.Cube.Face
import org.junit.jupiter.api.Assertions.assertNull
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class Day22Test {
    @Test
    fun `parsing the Moves produces 7 Moves`() {
        assertEquals(7, Move.parseFrom("input/22_test.txt").size)
    }

    @Test
    fun `cube is initialized with 6 faces`() {
        val cube = Cube("input/22_test.txt", testTopology)
        assertEquals(6, cube.faces.size)
    }

    @Test
    fun `topology regions contains correct positions`() {

        val expectedPositionsInRegionA = listOf(
            Pos(8, 0),
            Pos(8, 1),
            Pos(8, 2),
            Pos(8, 3),
            Pos(9, 0),
            Pos(9, 1),
            Pos(9, 2),
            Pos(9, 3),
            Pos(10, 0),
            Pos(10, 1),
            Pos(10, 2),
            Pos(10, 3),
            Pos(11, 0),
            Pos(11, 1),
            Pos(11, 2),
            Pos(11, 3),
        )

        val initializedRegionA = testTopology.faceRegions[Face.A]!!
        assertEquals(16, initializedRegionA.size)
        for (pos in expectedPositionsInRegionA)
            assertContains(initializedRegionA, pos)
//        assertContentEquals(expectedPositionsInRegionA, initializedRegionA)

    }

    @Test
    fun `cube faces have correct number of tiles`() {
        val cube = Cube("input/22_test.txt", testTopology)
        assertEquals(13, cube.faces[Face.A]!!.size)
        assertEquals(14, cube.faces[Face.B]!!.size)
        assertEquals(15, cube.faces[Face.C]!!.size)
        assertEquals(13, cube.faces[Face.D]!!.size)
        assertEquals(14, cube.faces[Face.E]!!.size)
        assertEquals(14, cube.faces[Face.F]!!.size)
    }

    @Test
    fun `cube face at Pos(8, 0) is linked correctly`() {
        val cube = Cube("input/22_test.txt", testTopology)
        val startingTile = cube.faces[Face.A]?.get(Pos(8, 0))
        val linkEast = startingTile?.linkTowards?.get(Dir.E)?.second
        val linkSouth = startingTile?.linkTowards?.get(Dir.S)?.second
        val linkWest = startingTile?.linkTowards?.get(Dir.W)?.second
        val linkNorth = startingTile?.linkTowards?.get(Dir.N)?.second
        assertEquals(Pos(9, 0), linkEast?.pos)
        assertEquals(Pos(8, 1), linkSouth?.pos)
        assertEquals(Pos(4, 4), linkWest?.pos)
        assertNull(linkNorth?.pos)
    }

    @Test
    fun `cube face at Pos(11, 3) is linked correctly`() {
        val cube = Cube("input/22_test.txt", testTopology)
        val startingTile = cube.faces[Face.A]!![Pos(11, 3)]!!
        val linkEast = startingTile.linkTowards[Dir.E]?.second
        val linkSouth = startingTile.linkTowards[Dir.S]?.second
        val linkWest = startingTile.linkTowards[Dir.W]?.second
        val linkNorth = startingTile.linkTowards[Dir.N]?.second
        assertEquals(Pos(15, 8), linkEast?.pos)
        assertNull(linkSouth?.pos)
        assertEquals(Pos(10, 3), linkWest?.pos)
        assertEquals(Pos(11, 2), linkNorth?.pos)
    }

    @Test
    fun `cube face at Pos(8, 11) is linked correctly`() {
        val cube = Cube("input/22_test.txt", testTopology)
        val startingTile = cube.faces[Face.E]!![Pos(8, 11)]!!
        val linkEast = startingTile.linkTowards[Dir.E]?.second
        val linkSouth = startingTile.linkTowards[Dir.S]?.second
        val linkWest = startingTile.linkTowards[Dir.W]?.second
        val linkNorth = startingTile.linkTowards[Dir.N]?.second
        assertEquals(Pos(9, 11), linkEast?.pos)
        assertEquals(Pos(3, 7), linkSouth?.pos)
        assertEquals(Pos(4, 7), linkWest?.pos)
        assertEquals(Pos(8, 10), linkNorth?.pos)
    }


    @Test
    fun `cube face at Pos(11, 5) is linked correctly`() {
        val cube = Cube("input/22_test.txt", testTopology)
        val startingTile = cube.faces[Face.D]!![Pos(11, 5)]!!
        val linkEast = startingTile.linkTowards[Dir.E]?.second
        val linkSouth = startingTile.linkTowards[Dir.S]?.second
        val linkWest = startingTile.linkTowards[Dir.W]?.second
        val linkNorth = startingTile.linkTowards[Dir.N]?.second
        assertEquals(Pos(14, 8), linkEast?.pos)
        assertEquals(Pos(11, 6), linkSouth?.pos)
        assertEquals(Pos(10, 5), linkWest?.pos)
        assertNull(linkNorth?.pos)
    }

    @Test
    fun `partB token position is correct after moves`() {
        val cube = Cube("input/22_test.txt", testTopology)
//        Token.tile = cube.faces[Face.A]!!.values.first()
        val token = Token(cube.faces[Face.A]!!.values.first())
        val moves = Move.parseFrom("input/22_test.txt").iterator()

        val expectedPositions = listOf(
            Pos(10, 0) to Dir.S,
            Pos(10, 5) to Dir.E,
            Pos(14, 10) to Dir.W,
            Pos(10, 10) to Dir.S,
            Pos(1, 5) to Dir.E,
            Pos(6, 5) to Dir.N,
            Pos(6, 4) to Dir.N
        )
        for ((expectedPos, expectedDir) in expectedPositions) {
            token.makeMove(moves.next())
            assertEquals(expectedPos to expectedDir, token.tile.pos to token.dir)
        }
    }

    @Test
    fun `cube result for test-data is correct`() {
        val day22 = Day22()
        assertEquals(5031, day22.solveB("input/22_test.txt", testTopology))
    }

    @Test
    fun `cube faces are linked correctly when using 'real' input`() {
        val cube = Cube("input/22.txt", inputTopology)
        cube.faces[Face.A]?.toList()?.first().also { println(it) }
        cube.faces[Face.B]?.toList()?.first().also { println(it) }
        cube.faces[Face.C]?.toList()?.first().also { println(it) }
        cube.faces[Face.D]?.toList()?.first().also { println(it) }
        cube.faces[Face.E]?.toList()?.first().also { println(it) }
        cube.faces[Face.F]?.toList()?.first().also { println(it) }
    }
}
