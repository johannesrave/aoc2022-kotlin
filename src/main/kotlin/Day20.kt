import com.ginsberg.cirkle.CircularList
import com.ginsberg.cirkle.circular
import kotlin.system.measureTimeMillis

fun main() {
    val day20 = Day20("input/20.txt")
    measureTimeMillis {
        day20.solveA().also { result -> println(result) }
        // 5904
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }

    measureTimeMillis {
        day20.solveB().also { result -> println(result) }
        // 8332585833851
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }

}

class Day20(inputFileName: String) : Day(inputFileName) {
    fun solveA(): Long {
        val numbers = parseFrom(input).map { it.toLong() }
        val mixedNumbers = mixLongs(numbers)

        val offset = mixedNumbers.indexOf(0)

        val a = mixedNumbers[offset + 1000]
        val b = mixedNumbers[offset + 2000]
        val c = mixedNumbers[offset + 3000]

        return a + b + c
    }

    fun solveB(): Long {
        val numbers = parseFrom(input).map { it * 811589153L } // implicitly convert to Long
        val mixedNumbers = mixLongs(numbers, 10)

        val offset = mixedNumbers.indexOf(0)

        val a = mixedNumbers[offset + 1000]
        val b = mixedNumbers[offset + 2000]
        val c = mixedNumbers[offset + 3000]

        return a + b + c
    }

    fun mixLongs(numbers: List<Long>, times: Int = 1): CircularList<Long> {
        val boxedLongs = numbers.map { BoxedLong(it) }
        val circle = boxedLongs.toMutableList().circular()

        println(circle)
        repeat(times) {
            boxedLongs.forEach {
                if (it.v == 0L) return@forEach

                val i = circle.indexOf(it)
                circle.remove(it)
                val offset = (i + it.v) % circle.size
                circle.add(offset.toInt(), it)
            }
        }
        bringToFront(circle, 0L)

        return circle.map { it.v }.circular()
    }

    private fun bringToFront(circle: MutableList<BoxedLong>, newFirst: Long) {
        val subList = circle.takeWhile { it.v != newFirst }
        circle.removeAll(subList)
        circle.addAll(subList)
    }

    private fun parseFrom(input: String): List<Int> =
        input.split('\n').map { it.toInt() }

    class BoxedLong(val v: Long) {
        override fun toString(): String {
            return v.toString()
        }
    }
}