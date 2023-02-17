fun main() {
    val day11 = Day11("input/11.txt")
    val solutionB = day11.calculateMonkeyBusiness(10_000)
    println(solutionB)
}

class Day11(inputFileName: String) : Day(inputFileName) {

    fun calculateMonkeyBusiness(numberOfRounds: Int): Any {
        val monkeys = Monkey.parseToMonkeys(input)

        repeat(numberOfRounds) {
            for (monkey in monkeys.values) {
                monkey.countWhileInspectingAllItems(monkeys)
            }
        }

        return monkeys.values
            .map { it.inspections }
            .sorted()
            .takeLast(2)
            .fold(1.toLong()) { acc, it -> acc * it }
    }

    class Monkey(
        val id: String,
        private val items: MutableList<Item>,
        val inspect: (Item) -> Item,
        private val divisor: Int,
        private val trueId: String,
        private val falseId: String,
    ) {
        var inspections = 0

        fun countWhileInspectingAllItems(monkeys: Map<String, Monkey>) {
            items.forEach { item ->
                val inspectedItem = inspect(item)
                val nextMonkey = if (inspectedItem[divisor] == 0) monkeys[trueId] else monkeys[falseId]
                nextMonkey?.items?.add(inspectedItem)
                inspections++
            }
            items.clear()
        }

        companion object {
            private val pattern =
                """Monkey (\d*):
            |  Starting items: ([0-9, ]*)
            |  Operation: new = old ([*+]) ([0-9]+|old)
            |  Test: divisible by (\d+)
            |    If true: throw to monkey (\d+)
            |    If false: throw to monkey (\d+)"""
                    .trimMargin()
                    .toRegex()

            fun parseToMonkeys(input: String): Map<String, Monkey> {

                val divisors = pattern.findAll(input).toList()
                    .map { it.destructured.component5().toInt() }

                return pattern
                    .findAll(input)
                    .map {
                        val (id, itemsString, operatorString, operandString, divisorString, trueId, falseId) = it.destructured

                        val items: MutableList<Item> = itemsString.split(", ")
                            .map { it.toInt() }
                            .map { item -> divisors.associateWith { div -> item % div } }
                            .toMutableList()
                        val inspect: (Item) -> Item = when {
                            operandString == "old" -> { a: Item -> a.square() }
                            operatorString == "*" -> { a: Item -> a * operandString.toInt() }
                            operatorString == "+" -> { a: Item -> a + operandString.toInt() }
                            else -> { a: Item -> a } // this can't happen and is just to keep the type from being nullable.
                        }

                        Monkey(id, items, inspect, divisorString.toInt(), trueId, falseId)

                    }.associateBy { it.id }
            }

//        fun inspectAllItems(monkeys: Map<String, Monkey>) {
//            items.forEach { item ->
//                val worry = op(item) / 3
//                val nextMonkey = if (worry % divisor == 0) trueId else falseId
//                monkeys[nextMonkey]?.items?.add(worry)
//                inspections++
//            }
//            items.clear()
//        }
        }
    }

}
private operator fun Item.times(a: Int): Item = this.mapValues { (div, n) -> (n * a) % div }

private operator fun Item.plus(a: Int): Item = this.mapValues { (div, n) -> (n + a) % div }

private fun Item.square(): Item = this.mapValues { (div, n) -> (n * n) % div }

private typealias Item = Map<Int, Int>


