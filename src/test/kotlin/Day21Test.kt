import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class Day21Test {

    val testInput = """
        root: pppw + sjmn
        dbpl: 5
        cczh: sllz + lgvd
        zczc: 2
        ptdq: humn - dvpt
        dvpt: 3
        lfqf: 4
        humn: 5
        ljgn: 2
        sjmn: drzm * dbpl
        sllz: 4
        pppw: cczh / lfqf
        lgvd: ljgn * ptdq
        drzm: hmdt - zczc
        hmdt: 32
        """.trimIndent()

    @Test
    fun `result for test-data is correct`() {
        val day21 = Day21("input/21_test.txt")
        assertEquals(152L, day21.solveA(day21.input))
    }


    @Test
    fun `text is parsed to monkeys correctly`() {
        val input = File("input/21.txt").readText(Charsets.UTF_8)
        val monkeys = Day21.Monkey.parseFrom(input)
        val lineNumber = input.lines().count()

        assertEquals(lineNumber, monkeys.size)
        assertDoesNotThrow { monkeys["root"] }
    }


    @Test
    fun `moderate numbers are calculated correctly`() {

        val input = """
        root: aaaa * bbbb
        aaaa: 16
        bbbb: 16
        """.trimIndent()

        val day21 = Day21("input/21_test.txt")
        val result = day21.solveA(input)
        assertEquals(256L, result)
    }
    @Test
    fun `very high numbers throw ArithmeticException`() {

        val input = """
        root: aaaa * bbbb
        aaaa: cccc * dddd
        bbbb: cccc * dddd
        cccc: eeee * ffff
        dddd: eeee * ffff
        eeee: gggg * hhhh
        ffff: gggg * hhhh
        gggg: 16
        hhhh: 16
        """.trimIndent()

        val day21 = Day21("input/21_test.txt")
//        val result =
        assertThrows<ArithmeticException> { day21.solveA(input) }
    }
}