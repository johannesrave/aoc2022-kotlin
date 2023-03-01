import kotlin.system.measureTimeMillis

fun main() {
    val day21 = Day21("input/21.txt")
    measureTimeMillis {
        day21.solveA().also { result -> println(result) }
        // 22382838633806
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

    abstract class Monkey(open val name: String) {
        abstract fun getResult(): Long

        data class NumberMonkey(
            override val name: String,
            val number: Long
        ) : Monkey(name) {
            override fun getResult(): Long = number
        }

        data class OpMonkey(
            override val name: String,
            val op: String,
            val leftName: String,
            val rightName: String,
        ) : Monkey(name) {
            lateinit var left: Monkey
            lateinit var right: Monkey
            override fun getResult(): Long {
                val left = left.getResult()
                val right = right.getResult()

                return when (op) {
                    "+" -> (left + right)
                    "-" -> (left - right)
                    "*" -> (left * right)
                    "/" -> (left / right)
                    else -> throw IllegalArgumentException("$op is not a valid MonkeyOperator!")
                }.also { println(
                    "${left.toString().padStart(15, '_')} $op ${right.toString().padStart(15, '_')} = ${
                        it.toString().padStart(15)
                    } "
                ) }
            }
        }

        companion object {
            fun parseFrom(input: String): Map<String, Monkey> {
                val pattern =
                    "(?<name>\\w{4}): (?:(?<number>\\d{1,4})|(?<leftName>\\w{4}) (?<op>[+*/-]) (?<rightName>\\w{4}))".toRegex()

                return pattern.findAll(input)
                    .map { match ->
                        when {
                            match.groups["number"] != null ->
                                NumberMonkey(
                                    name = match.groups["name"]?.value!!,
                                    number = match.groups["number"]?.value?.toLong()!!,
                                )

                            else -> {
                                val (name, op, leftName, rightName) = listOf("name", "op", "leftName", "rightName")
                                    .map {
                                        match.groups[it]?.value
                                            ?: throw IllegalArgumentException("$it is missing for a Monkey!")
                                    }
                                OpMonkey(name, op, leftName, rightName)
                            }
                        }
                    }.associateBy { it.name }
                    .apply {
                        onEach { (name, monkey) ->
                            if (monkey is OpMonkey) {
                                if (this.containsKey(monkey.leftName) || this.containsKey(monkey.rightName)) {
                                    monkey.left = this[monkey.leftName]!!
                                    monkey.right = this[monkey.rightName]!!
                                } else
                                    throw IllegalArgumentException("Left ${monkey.leftName} or right ${monkey.rightName} are missing for a $name!")
                            }
                        }
                    }
            }
        }
    }
}