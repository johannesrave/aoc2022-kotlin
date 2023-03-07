import Day24.Blizzard
import Day24.Wall
import kotlin.test.Test
import kotlin.test.assertEquals

class Day24Test {

    val testInput = """
        #.######
        #>>.<^<#
        #.<..<<#
        #>v.><>#
        #<^v^^>#
        ######.#
        """.trimIndent()

    @Test
    fun `BlizzardW moves west and BlizzardE moves east`() {
        val day24 = Day24()

        val board: Array<IntArray> = arrayOf(
            IntArray(3),
            arrayOf(0, Blizzard.W.b + Blizzard.E.b, 0).toIntArray(),
            IntArray(3)
        )


        val buffer: Array<IntArray> = arrayOf(
            IntArray(3),
            IntArray(3),
            IntArray(3)
        )

        day24.moveBlizzards(1, 1, board, buffer)

        println(buffer.joinToString { it.joinToString(",") })
        assertEquals(Blizzard.W.b, buffer[1][0])
        assertEquals(Blizzard.E.b, buffer[1][2])
    }

    @Test
    fun `BlizzardN moves north and BlizzardS moves South`() {
        val day24 = Day24()

        val board: Array<IntArray> = arrayOf(
            IntArray(3),
            arrayOf(0, Blizzard.N.b + Blizzard.S.b, 0).toIntArray(),
            IntArray(3)
        )


        val buffer: Array<IntArray> = arrayOf(
            IntArray(3),
            IntArray(3),
            IntArray(3)
        )

        day24.moveBlizzards(1, 1, board, buffer)

        println(buffer.joinToString { it.joinToString(",") })
        assertEquals(Blizzard.N.b, buffer[0][1])
        assertEquals(Blizzard.S.b, buffer[2][1])
    }

    @Test
    fun `BlizzardW resets to east after hitting a wall`() {
        val day24 = Day24()

        val board: Array<IntArray> = arrayOf(
            arrayOf(Wall.b, Blizzard.W.b, 0, 0, Wall.b).toIntArray(),
        )

        val buffer: Array<IntArray> = arrayOf(IntArray(5))

        day24.moveBlizzards(1, 0, board, buffer)

        println(buffer.joinToString { it.joinToString(",") })
        assertEquals(Blizzard.W.b, buffer[0][3])
    }
}
