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
        assertEquals(day25.SNAFUtoInt('0'), 0)
        assertEquals(day25.SNAFUtoInt('1'), 1)
        assertEquals(day25.SNAFUtoInt('2'), 2)
    }

    @Test
    fun `SNAFU digits are added correctly`() {
        val day25 = Day25()
        assertEquals(day25.addWithCarryOver('2', '2'), '1' to '-')
        assertEquals(day25.addWithCarryOver('1', '1'), '0' to '2')
        assertEquals(day25.addWithCarryOver('0', '0'), '0' to '0')
        assertEquals(day25.addWithCarryOver('=', '='), '-' to '1')
    }

    @Test
    fun `SNAFU numbers 11 and 11 are added correctly`() {
        val day25 = Day25()
        val first = arrayOf('1', '1').reversed().toCharArray()
        val second = arrayOf('1', '1').reversed().toCharArray()
        val expectedResult = arrayOf('2', '2').reversed().toCharArray()

        assertContentEquals(expectedResult, day25.add(first, second))
    }

    @Test
    fun `SNAFU numbers 22 and 22 are added correctly`() {
        val day25 = Day25()
        val first = arrayOf('2', '2').reversed().toCharArray()
        val second = arrayOf('2', '2').reversed().toCharArray()
        val expectedResult = arrayOf('1', '0', '-').reversed().toCharArray()

        assertContentEquals(expectedResult, day25.add(first, second))
    }

    @Test
    fun `SNAFU numbers = and = are added correctly`() {
        val day25 = Day25()
        val first = arrayOf('=').reversed().toCharArray()
        val second = arrayOf('=').reversed().toCharArray()
        val expectedResult = arrayOf('-', '1').reversed().toCharArray()

        assertContentEquals(expectedResult, day25.add(first, second))
    }


    @Test
    fun `test input is added correctly`() {
        val day25 = Day25()
        val first = arrayOf('=').reversed().toCharArray()
        val second = arrayOf('=').reversed().toCharArray()
        val expectedResult = arrayOf('-', '1').reversed().toCharArray()

        assertContentEquals(expectedResult, day25.add(first, second))
    }
}
