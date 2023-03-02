import kotlin.properties.Delegates
import kotlin.system.measureTimeMillis

fun main() {
    val day21 = Day21("input/21.txt")
    measureTimeMillis {
        day21.solveA().also { result -> println(result) }
        // 22382838633806
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }

    measureTimeMillis {
        day21.solveB().also { result -> println(result) }
        // 3099532691300
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }
}

class Day21(inputFileName: String) : Day(inputFileName) {
    fun solveA(input: String = this.input): Any {
        val monkeys = Monkey.parseFrom(input)
        val root = monkeys["root"]!!
        root.calculateNumbersForAllMonkeys()
        return root.number
    }

    fun solveB(input: String = this.input): Long? {
        val monkeys = Monkey.parseFrom(input)
        val root = monkeys["root"]!! as Monkey.OpMonkey
        root.calculateNumbersForAllMonkeys() // to calculate and set all Monkey numbers
        return root.right.replaceHumnNumber(root.left.number) ?: root.left.replaceHumnNumber(root.right.number)
    }

    abstract class Monkey(open val name: String) {
        abstract var number: Long

        abstract fun calculateNumbersForAllMonkeys(): Long
        abstract fun replaceHumnNumber(v: Long): Long?

        data class NumberMonkey(override val name: String, override var number: Long) : Monkey(name) {
            override fun calculateNumbersForAllMonkeys(): Long = number
            override fun replaceHumnNumber(v: Long): Long? = if (name == "humn") v else null
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
            override fun calculateNumbersForAllMonkeys(): Long {
                val left = left.calculateNumbersForAllMonkeys()
                val right = right.calculateNumbersForAllMonkeys()

                // set/save the number for all OpMonkeys visited by the tree traversal
                number = when (op) {
                    "+" -> (left + right)
                    "-" -> (left - right)
                    "*" -> (left * right)
                    "/" -> (left / right)
                    else -> throw IllegalArgumentException("$op is not a valid MonkeyOperator!")
                }
                return number
            }

            override fun replaceHumnNumber(v: Long): Long? {
                return when (op) {
                    "+" -> right.replaceHumnNumber(v - left.number)
                        ?: left.replaceHumnNumber(v - right.number)

                    "-" -> right.replaceHumnNumber(left.number - v)
                        ?: left.replaceHumnNumber(v + right.number)

                    "*" -> right.replaceHumnNumber(v / left.number)
                        ?: left.replaceHumnNumber(v / right.number)

                    "/" -> right.replaceHumnNumber(left.number / v)
                        ?: left.replaceHumnNumber(v * right.number)

                    else -> throw IllegalArgumentException("$op is not a valid MonkeyOperator!")
                }
            }
        }

        companion object {
            val pattern =
                "(?<name>\\w+): (?:(?<number>\\d+)|(?<leftName>\\w+) (?<op>[+*/-]) (?<rightName>\\w+))".toRegex()

            fun parseFrom(input: String): Map<String, Monkey> = pattern.findAll(input).map { match ->
                val (name, number, op, leftName, rightName) = listOf("name", "number", "op", "leftName", "rightName")
                    .map { match.groups[it]?.value }
                when {
                    name != null && number != null ->
                        NumberMonkey(name, number.toLong())

                    name != null && op != null && leftName != null && rightName != null ->
                        OpMonkey(name, op, leftName, rightName)

                    else -> throw IllegalArgumentException("$name is missing args for bot NumberMonkey and OpMonkey!")
                }
            }.associateBy { it.name }
                .apply {
                    onEach { (_, monkey) ->
                        if (monkey is OpMonkey) {
                            monkey.left = this[monkey.leftName]!!
                            monkey.right = this[monkey.rightName]!!
                        }
                    }
                }
        }
    }
}