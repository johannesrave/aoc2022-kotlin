import kotlin.properties.Delegates
import kotlin.system.measureTimeMillis

fun main() {
//    val day21 = Day21("input/21_test.txt")
    val day21 = Day21("input/21.txt")
//    measureTimeMillis {
//        day21.solveA().also { result -> println(result) }
//        // 22382838633806
//    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }

    measureTimeMillis {
        day21.solveB().also { result -> println(result) }
        // 3099532691300
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }

}

class Day21(inputFileName: String) : Day(inputFileName) {
    //algo:
    // build tree of monkeys including dependencies:
    //  parse all monkeys
    //  iterate over them, injecting references to dependencies into all monkeys
    // traverse tree depth first (using a stack)
    // enter monkey:
    //  does it have a number? return it
    // if not:
    //      enter left monkey
    //      enter right monkey
    //      operate and return
    // idea: could already recurse while parsing...?

    fun solveA(input: String = this.input): Any {
        val monkeys = Monkey.parseFrom(input)
        val root = monkeys["root"]!!
        val result = root.getResult()
        return result
    }


    // algo:
    // start at root monkey, picking the left value as a base for the right path and vice versa
    // while traversing, remember operations, and reverse them as the result of the humn monkey

    // 6 == x
    // x = 3 * y -> y = x / 3
    // y = 1 + z -> z = y - 1
    // z = ? -> z = 1


    fun solveB(input: String = this.input): Long? {
        val monkeys = Monkey.parseFrom(input)
        val root = monkeys["root"]!! as Monkey.OpMonkey
        root.getResult() // to calculate and set all Monkey numbers
        return root.right.digForHumn(root.left.number) ?: root.left.digForHumn(root.right.number)
    }

    abstract class Monkey(open val name: String) {
        abstract var number: Long

        abstract fun getResult(): Long
        abstract fun digForHumn(v: Long): Long?

        data class NumberMonkey(
            override val name: String, override var number: Long
        ) : Monkey(name) {
            override fun getResult(): Long = number
            override fun digForHumn(v: Long): Long? {
                println(this)
                println("stopping to dig at $name with $v")
                return when (name) {
                    "humn" -> v
                    else -> null
                }
            }
        }

        data class OpMonkey(
            override val name: String,
            val op: String,
            val leftName: String,
            val rightName: String,
        ) : Monkey(name) {
            lateinit var left: Monkey
            lateinit var right: Monkey
            override var number by Delegates.notNull<Long>()
            override fun getResult(): Long {
                val left = left.getResult()
                val right = right.getResult()

                number = when (op) {
                    "+" -> (left + right)
                    "-" -> (left - right)
                    "*" -> (left * right)
                    "/" -> (left / right)
                    else -> throw IllegalArgumentException("$op is not a valid MonkeyOperator!")
                }
                return number.also {
                    println(
                        "$name: ${
                            left.toString().padStart(15, '_')
                        } $op ${
                            right.toString().padStart(15, '_')
                        } = ${
                            it.toString().padStart(15, '_')
                        }"
                    )
                }
            }

            override fun digForHumn(v: Long): Long? {
                println(this)
                return when (op) {
                    "+" -> {
                        right.digForHumn(v - left.number).also { println("right: $v - ${left.number}") }
                            ?: left.digForHumn(Math.subtractExact(v, right.number)).also { println("left: $v - ${right.number}") }
                    }

                    "-" -> {
                        right.digForHumn(left.number - v).also { println("right: ${left.number} - $v") }
                            ?: left.digForHumn(Math.addExact(v, right.number)).also { println("left: $v + ${right.number}") }
                    }

                    "*" -> {
                        right.digForHumn(v / left.number).also { println("right: $v / ${left.number}") }
                            ?: left.digForHumn(Math.divideExact(v, right.number)).also { println("left: $v / ${right.number}") }
                    }

                    "/" -> {
                        right.digForHumn(left.number / v).also { println("right: ${left.number} / $v") }
                            ?: left.digForHumn(Math.multiplyExact(v, right.number)).also { println("left: $v * ${right.number}") }
                    }

                    else -> throw IllegalArgumentException("$op is not a valid MonkeyOperator!")
                }
            }
        }

        companion object {
            fun parseFrom(input: String): Map<String, Monkey> {
                val pattern =
                    "(?<name>\\w+): (?:(?<number>\\d+)|(?<leftName>\\w+) (?<op>[+*/-]) (?<rightName>\\w+))".toRegex()

                return pattern.findAll(input).map { match ->
                    when {
                        match.groups["number"] != null -> NumberMonkey(
                            name = match.groups["name"]?.value!!,
                            number = match.groups["number"]?.value?.toLong()!!,
                        )

                        else -> {
                            val (name, op, leftName, rightName) = listOf(
                                "name",
                                "op",
                                "leftName",
                                "rightName"
                            ).map {
                                match.groups[it]?.value
                                    ?: throw IllegalArgumentException("$it is missing for a Monkey!")
                            }
                            OpMonkey(name, op, leftName, rightName)
                        }
                    }
                }.associateBy { it.name }.apply {
                    onEach { (name, monkey) ->
                        if (monkey is OpMonkey) {
                            if (this.containsKey(monkey.leftName) || this.containsKey(monkey.rightName)) {
                                monkey.left = this[monkey.leftName]!!
                                monkey.right = this[monkey.rightName]!!
                            } else throw IllegalArgumentException("Left ${monkey.leftName} or right ${monkey.rightName} are missing for a $name!")
                        }
                    }
                }
            }
        }
    }
}