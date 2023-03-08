
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Day25Test {

    private val testInput = """
        1=-0-2
        12111
        2=0=
        21
        2=01
        111
        20012
        112
        1=-1=
        1-12
        12
        1=
        122
        """.trimIndent()

    @Test
    fun `input is parsed to Array of reversed CharArrays`() {
        val day25 = Day25()
        val charArrays = day25.parseToCharArrays(testInput)
        assertIs<List<CharArray>>(charArrays)
        assertContentEquals(charArrays[0], arrayOf('2', '-', '0', '-', '=', '1').toCharArray())
    }

    @Test
    fun `SNAFU to Int`() {
        val day25 = Day25()
        assertEquals(day25.SNAFUtoInt('='), -2)
        assertEquals(day25.SNAFUtoInt('-'), -1)
        assertEquals(day25.SNAFUtoInt('0'),  0)
        assertEquals(day25.SNAFUtoInt('1'),  1)
        assertEquals(day25.SNAFUtoInt('2'),  2)
    }

    @Test
    fun `Int to SNAFU`() {
        val day25 = Day25()
        assertEquals(day25.intToSNAFU(-2), '=')
        assertEquals(day25.intToSNAFU(-1), '-')
        assertEquals(day25.intToSNAFU(0), '0' )
        assertEquals(day25.intToSNAFU(1), '1' )
        assertEquals(day25.intToSNAFU(2), '2' )
    }
}
