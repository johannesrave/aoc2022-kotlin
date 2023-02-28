
import kotlin.test.Test
import kotlin.test.assertEquals

class Day20Test {
    private val day20 = Day20("input/20_test.txt")

    @Test
    fun `result for test-data is correct`() {
        val mixedList = day20.solveA()
        assertEquals(3, mixedList)
    }

    @Test
    fun `list is identical after sorting`() {
        val mixedList: List<Long> = day20.mixLongs(listOf(3, 1, 0))
        assertEquals(listOf<Long>(3, 1, 0), mixedList)
    }

    @Test
    fun `number larger than the size of the list is sorted correctly`() {
        val mixedList = day20.mixLongs(listOf(1, 2, -3, 3, -2, 0, 8))

        assertEquals(listOf<Long>(-2, 1, 8, 2, -3, 0, 3), mixedList)
    }

    @Test
    fun `identical numbers are sorted correctly`() {
        val mixedList = day20.mixLongs(listOf(0, -1, -1, 1))

        assertEquals(listOf<Long>(-1, 1, -1, 0), mixedList)
    }
}