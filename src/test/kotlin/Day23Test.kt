import Day23.Dir
import kotlin.test.Test
import kotlin.test.assertEquals

class Day23Test {

    val testInput = """
        ....#..
        ..###.#
        #...#.#
        .#...##
        #.###..
        ##.#.##
        .#..#..
        """.trimIndent()

    @Test
    fun `N getOpposing() gives S`() {
        assertEquals(Dir.S, Dir.N.getOpposing())
    }
}
