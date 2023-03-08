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
    2. nevermind, the first one worked fine
     */
    fun solve(inputFileName: String = "input/${this.javaClass.name.drop(3)}.txt"): Any {
        val snafus = parseToCharArrays(File(inputFileName).readText(Charsets.UTF_8))

        val chars = snafus.fold(toSnafu('0')) { sum, snafu -> add(sum, snafu) }
        return chars.reversed().joinToString ("")
    }

    fun parseToCharArrays(input: String) = input.split('\n').map { it.reversed().toCharArray() }

    fun toSnafu(vararg c: Char) = c.reversed().toCharArray()

    fun add(a: CharArray, b: CharArray, regWidth: Int = 24): CharArray {
        a.copyInto(CharArray(regWidth) { '0' })
        val regA = a.copyInto(CharArray(regWidth) { '0' })
        val regB = b.copyInto(CharArray(regWidth) { '0' })
        val carryOver = CharArray(regWidth) { '0' }

        do {
            (regA zip regB).forEachIndexed { i, (c1, c2) ->
                addWithCarryOver(c1, c2)
                    .let { (carryOverDigit, resDigit) ->
                        regA[i] = resDigit
                        if (i + 1 < regWidth) carryOver[i + 1] = carryOverDigit
                    }
            }
            carryOver.forEachIndexed { i, _ ->
                regB[i] = carryOver[i]
                carryOver[i] = '0'
            }
        } while (regB.any { it != '0' })

        return regA.dropLastWhile { it == '0' }.toCharArray()
    }

    fun addWithCarryOver(c1: Char, c2: Char) = when (c1) {
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
