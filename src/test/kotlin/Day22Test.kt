import Day22.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class Day22Test {

    @Test
    fun `number of parsed tiles is 83`() {
        assertEquals(83, Tile.Path.parseFlatFrom("input/22_test.txt").size)
    }

    @Test
    fun `first tile is at Pos(9, 1) and connected to up, down and right`() {
        val firstTile = Tile.Path.parseFlatFrom("input/22_test.txt").first()
        assertEquals(Pos(9, 1), firstTile.pos)
        assertEquals(Pos(9, 12), firstTile.north?.pos)
        assertEquals(Pos(9, 2), firstTile.south?.pos)
        assertEquals(Pos(10, 1), firstTile.east?.pos)
        assertNull(firstTile.west)
    }

    @Test
    fun `result for test-data is correct`() {
        val day22 = Day22()
        assertEquals(6032, day22.solveA("input/22_test.txt"))
    }

    @Test
    fun `input is parsed as 6 squares`() {
//        val cube = Cube(
//            Face.A(9..12, 1.. 4),
//            Face.B(1.. 4, 5.. 8),
//            Face.C(5.. 8, 5.. 8),
//            Face.D(9..12, 5.. 8),
//            Face.E(9..12, 9..12),
//            Face.F(13..16,9..12)
//        )

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

        val cubicTiles = Tile.Path.parseCubicFrom("input/22_test.txt", cube)
        val pathsBySide = cubicTiles.groupBy { it.face }
        assertEquals(6, pathsBySide.size)
        assertEquals(13, pathsBySide[A]?.size)

        val pathA = pathsBySide[A]?.get(0)
        assertEquals(Pos(10, 1), pathA?.east?.pos)
        assertEquals(Pos(9, 2), pathA?.south?.pos)
        assertEquals(Pos(5, 5), pathA?.west?.pos)
        assertNull(pathA?.north?.pos)

        println(D.edgeInfo.west.otherFace.region)
        val pathD = pathsBySide[D]?.get(0)
        assertEquals(Pos(10, 5), pathD?.east?.pos)
        assertNull(pathD?.south?.pos)
        assertEquals(Pos(8, 5), pathD?.west?.pos)
        assertEquals(Pos(9, 4), pathD?.north?.pos)

        println(D.edgeInfo.west.otherFace.region)
        val pathD2 = pathsBySide[D]?.find { it.pos == Pos(12, 6) }
        assertEquals(Pos(15, 9), pathD2?.east?.pos)
        assertEquals(Pos(12, 7), pathD2?.south?.pos)
        assertEquals(Pos(11, 6), pathD2?.west?.pos)
        assertNull(pathD2?.north?.pos)
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