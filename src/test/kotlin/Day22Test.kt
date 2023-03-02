import Day22.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class Day22Test {

    @Test
    fun `number of parsed tiles is 83`() {
        assertEquals(83, Tile.Path.parseFrom("input/22_test.txt").size)
    }

    @Test
    fun `first tile is at Pos(9, 1) and connected to up, down and right`() {
        val firstTile = Tile.Path.parseFrom("input/22_test.txt").first()
        assertEquals(Pos(9, 1), firstTile.pos)
        assertEquals(Pos(9, 12), firstTile.up?.pos)
        assertEquals(Pos(9, 2), firstTile.down?.pos)
        assertEquals(Pos(10, 1), firstTile.right?.pos)
        assertNull(firstTile.left)
    }

    @Test
    fun `result for test-data is correct`() {
        val day22 = Day22()
        assertEquals(6032, day22.solveA("input/22_test.txt"))
    }


    @Test
    fun `turning Right from East is South`() {
        val newDir = Dir.E.turn(Turn.R)
        assertEquals(Dir.S, newDir)
    }

    @Test
    fun `turning Left from North is West`() {
        val newDir = Dir.N.turn(Turn.L)
        assertEquals(Dir.W, newDir)
    }

    @Test
    fun `turning Right from West is North`() {
        val newDir = Dir.W.turn(Turn.R)
        assertEquals(Dir.N, newDir)
    }


    @Test
    fun `parsing the Moves produces 7 Moves`() {
        assertEquals(7, Move.parseFrom("input/22_test.txt").size)
    }
}