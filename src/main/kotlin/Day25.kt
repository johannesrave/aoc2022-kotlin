import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    val day25 = Day25()
    measureTimeMillis {
        day25.solve().also { result -> println(result) }
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }
}

class Day25 {
    /*
    algo part A
    two ideas:
    1. just add all numbers in their original SNAFU form without ever finding out their dec-value
    2. naive (and probably more work): convert all inputs, add, convert output back to snafu

    trying 1. first:

    put number and successive number into adder, which has 4 arrays:
    - first number
    - second number
    - carry over
    - result

    add char by char beginning with the least significant digit

    add result with next number in list

    when list is empty, print result


     */
    fun solve(inputFileName: String = "input/${this.javaClass.name.drop(3)}.txt"): Any {
        File(inputFileName).readText(Charsets.UTF_8)
        TODO()
    }

    fun parseToCharArrays(input: String) = input.split('\n').map { it.reversed().toCharArray() }
    fun SNAFUtoInt(c: Char): Int = when (c) {
        '=' -> (-2)
        '-' -> (-1)
        '0' -> (0)
        '1' -> (1)
        '2' -> (2)
        else -> throw IllegalArgumentException("unknown SNAFU digit found: $c")
    }

    fun intToSNAFU(i: Int): Char = when (i) {
        (-2) -> '='
        (-1) -> '-'
        (0) -> '0'
        (1) -> '1'
        (2) -> '2'
        else -> throw IllegalArgumentException("can't SNAFU digit found: $i")
    }

    fun add(a: CharArray, b: CharArray): CharArray {

        val (first, second) = if (a.size >= b.size) (a to b) else (b to a)
        val carryOver = CharArray(first.size) { '0' }
        val result = CharArray(first.size) { '0' }

        (first zip second).forEachIndexed { i, (c1, c2) ->
            addWithCarryOver(c1, c2).let { (carryOverDigit, resDigit) ->
                result[i] = resDigit
                carryOver[i+1] = resDigit
            }
        }


    }

    private fun addWithCarryOver(c1: Char, c2: Char) = when (c1) {
        '=' -> when (c2) {
            '=' -> Pair('-', '1')
            '-' -> Pair('-', '2')
            '0' -> Pair('0', '=')
            '1' -> Pair('0', '-')
            '2' -> Pair('0', '0')
            else -> throw IllegalArgumentException("$c1, $c2")
        }

        '-' -> when (c2) {
            '=' -> Pair('-', '2')//
            '-' -> Pair('0', '=')
            '0' -> Pair('0', '-')
            '1' -> Pair('0', '0')
            '2' -> Pair('0', '1')
            else -> throw IllegalArgumentException("$c1, $c2")
        }

        '0' -> when (c2) {
            '=' -> Pair('0', '=')
            '-' -> Pair('0', '-')
            '0' -> Pair('0', '0')
            '1' -> Pair('0', '1')
            '2' -> Pair('0', '2')
            else -> throw IllegalArgumentException("$c1, $c2")
        }

        '1' -> when (c2) {
            '=' -> Pair('0', '-')
            '-' -> Pair('0', '0')
            '0' -> Pair('0', '1')
            '1' -> Pair('0', '2')
            '2' -> Pair('1', '=')
            else -> throw IllegalArgumentException("$c1, $c2")
        }

        '2' -> when (c2) {
            '=' -> Pair('0', '0')
            '-' -> Pair('0', '1')
            '0' -> Pair('0', '2')
            '1' -> Pair('1', '=')
            '2' -> Pair('1', '-')
            else -> throw IllegalArgumentException("$c1, $c2")
        }

        else -> throw IllegalArgumentException("$c1, $c2")
    }
}


