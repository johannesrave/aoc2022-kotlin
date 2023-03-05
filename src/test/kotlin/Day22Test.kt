import Day22.*
import Day22.Companion.testTopology
import Day22.Cube.Face
import org.junit.jupiter.api.Assertions.assertNull
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Day22Test {
//    @Test
//    fun `turning Right from East is South`() {
//        val newDir = Dir.E.turn(Turn.R)
//        assertEquals(Dir.S, newDir)
//    }
//
//    @Test
//    fun `turning Left from North is West`() {
//        val newDir = Dir.N.turn(Turn.L)
//        assertEquals(Dir.W, newDir)
//    }
//
//    @Test
//    fun `turning Right from West is North`() {
//        val newDir = Dir.W.turn(Turn.R)
//        assertEquals(Dir.N, newDir)
//    }

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
            Pos(8, 4),
            Pos(9, 0),
            Pos(9, 1),
            Pos(9, 2),
            Pos(9, 3),
            Pos(9, 4),
            Pos(10, 0),
            Pos(10, 1),
            Pos(10, 2),
            Pos(10, 3),
            Pos(10, 4),
            Pos(11, 0),
            Pos(11, 1),
            Pos(11, 2),
            Pos(11, 3),
            Pos(11, 4)
        )

        val initializedRegionA = testTopology.faceRegions[Face.A]!!
        assertEquals(16, initializedRegionA.size)
        assertContains(initializedRegionA, Pos(8, 0))
        assertContentEquals(expectedPositionsInRegionA, initializedRegionA)
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
    fun `cube result for test-data is correct`() {
        val day22 = Day22()
        assertEquals(5031, day22.solveB("input/22_test.txt", testTopology))
    }
}
