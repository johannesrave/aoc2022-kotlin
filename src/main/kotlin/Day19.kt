import kotlin.system.measureTimeMillis

fun main() {
    val day19 = Day19("input/19.txt")
//    val day19 = Day19("input/19_test.txt")
    measureTimeMillis {
        day19.solve()
//            .also { result -> println(result) }
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }
}

class Day19(inputFileName: String) : Day(inputFileName) {
    fun solve(): Any {
        val blueprints = Blueprint.parseFrom(input)
        val t = 24

//        (1..10).map { getEarliestFrom(it, 15) }.also { println(it) }

        val result = blueprints
            .take(2)
            .also { println(it.map { Pair(it.id, it.canMineAtLeastOneGeode(24)) }) }
//            .filter { it.canMineAtLeastOneGeode(t) }
//            .also { println(it.map { it.id }) }
//            .map { Pair(it.id, it.maximizeForGeodes()) }
//            .also { println(it) }
//            .sumOf { it.first * it.second }

        return result
    }

    data class Blueprint(
        val id: Int,
        val oreForOre: Int,
        val oreForClay: Int,
        val oreForObsidian: Int,
        val clayForObsidian: Int,
        val oreForGeode: Int,
        val obsidianForGeode: Int
    ) {
        fun maximizeForGeodes(): Int {
            val t = 24

            var states = setOf(State())

            repeat(t) { i ->
                states = states.flatMap { state ->
                    if (state.canAffordGeodeBot(this)) {
                        listOf(state.buildGeodeBotOrNull(this)!!)
                    } else if (state.canAffordObsidianBot(this)) {
                        listOf(state.buildObsidianBotOrNull(this)!!)
                    } else listOfNotNull(
                        state.buildNothing(),
                        state.buildOreBotOrNull(this),
                        state.buildClayBotOrNull(this),
//                        state.buildObsidianBotOrNull(this)
                    )
                }
                    .filter { it.oreBots + it.clayBots + it.obsidianBots + it.geodeBots < i + 5 }
                    .toSet()
//                    .also {
//                        it.sortedByDescending { it.geodes }
//                            .also { it.takeLast(10).forEach { println(it) } }
//                            .also { it.take(10).forEach { println(it) } }
//                    }

                println("round $i: no of states: ${states.size}")

//                println("states in round $i:")
//                println(states.joinToString("\n"))
            }
            return states.maxOfOrNull { it.geodes } ?: 0
        }

        data class State(
            val oreBots: Int = 1,
            val clayBots: Int = 0,
            val obsidianBots: Int = 0,
            val geodeBots: Int = 0,
            val ore: Int = 0,
            val clay: Int = 0,
            val obsidian: Int = 0,
            val geodes: Int = 0
        ) {
            fun buildNothing(): State {
                val (ore, clay, obsidian, geodes) = calculateResources()
                return copy(ore = ore, clay = clay, obsidian = obsidian, geodes = geodes)
            }

            fun buildOreBotOrNull(blueprint: Blueprint): State? {
                return if (ore >= blueprint.oreForOre) {
                    val (ore, clay, obsidian, geodes) = calculateResources(ore = ore - blueprint.oreForOre)
                    copy(oreBots = oreBots + 1, ore = ore, clay = clay, obsidian = obsidian, geodes = geodes)
                } else null
            }

            fun buildClayBotOrNull(blueprint: Blueprint): State? =
                if (ore >= blueprint.oreForClay) {
                    val (ore, clay, obsidian, geodes) = calculateResources(ore = ore - blueprint.oreForClay)
                    copy(clayBots = clayBots + 1, ore = ore, clay = clay, obsidian = obsidian, geodes = geodes)
                } else null

            fun buildObsidianBotOrNull(blueprint: Blueprint): State? =
                if (canAffordObsidianBot(blueprint)) {
                    val (ore, clay, obsidian, geodes) = calculateResources(
                        ore = ore - blueprint.oreForObsidian,
                        clay = clay - blueprint.clayForObsidian,
                    )
                    copy(obsidianBots = obsidianBots + 1, ore = ore, clay = clay, obsidian = obsidian, geodes = geodes)
                } else null

            fun buildGeodeBotOrNull(blueprint: Blueprint): State? =
                if (canAffordGeodeBot(blueprint)) {
                    val (ore, clay, obsidian, geodes) = calculateResources(
                        ore = ore - blueprint.oreForGeode,
                        obsidian = obsidian - blueprint.obsidianForGeode
                    )
                    copy(geodeBots = geodeBots + 1, ore = ore, clay = clay, obsidian = obsidian, geodes = geodes)
                } else null

            fun canAffordObsidianBot(blueprint: Blueprint) =
                ore >= blueprint.oreForObsidian && clay >= blueprint.clayForObsidian

            fun canAffordGeodeBot(blueprint: Blueprint) =
                ore >= blueprint.oreForGeode && obsidian >= blueprint.obsidianForGeode

            private fun calculateResources(
                ore: Int = this.ore,
                clay: Int = this.clay,
                obsidian: Int = this.obsidian,
                geodes: Int = this.geodes
            ): Resources = Resources(ore + oreBots, clay + clayBots, obsidian + obsidianBots, geodes + geodeBots)
        }

        data class Resources(val ore: Int, val clay: Int, val obsidian: Int, val geodes: Int)

        companion object {
            private val pattern = (
                "Blueprint (\\d+):.*" +
                    "ore robot costs (\\d+) ore.* " +
                    "clay robot costs (\\d+) ore.* " +
                    "obsidian robot costs (\\d+) ore and (\\d+) clay.* " +
                    "geode robot costs (\\d+) ore and (\\d+) obsidian."
                )
                .toRegex()

            fun parseFrom(input: String): List<Blueprint> {
                return pattern
                    .findAll(input)
                    .map { match ->
                        val (id, oreForOre, oreForClay, oreForObsidian, clayForObsidian, oreForGeode, obsidianForGeode) =
                            match.groupValues
                                .drop(1)
                                .mapNotNull { it.toIntOrNull() }
                        return@map Blueprint(
                            id,
                            oreForOre,
                            oreForClay,
                            oreForObsidian,
                            clayForObsidian,
                            oreForGeode,
                            obsidianForGeode
                        )
                    }
                    .toList()
            }

            private operator fun <E> List<E>.component6(): E = get(5)
            private operator fun <E> List<E>.component7(): E = get(6)
        }

        fun canMineAtLeastOneGeode(t: Int): Boolean {
            // the earliest obsidian can be mined when a succession of claybots generates enough clay
            // the first claybot is built after $oreForClay minutes.
            // the second claybot is built after 2*$oreForClay minutes.
            // -> up until now, 1*$oreForClay has been mined (by the first bot)
            // the third claybot is built after 3*$oreForClay minutes.
            // -> up until now, 3*$oreForClay has been mined (by the first and the second bots)
            // the fourth claybot is built after 4*$oreForClay minutes.
            // -> up until now, 6*$oreForClay has been mined (by the first, second and the third bots)
            // the clay progresses 1+3+6+10... which is 0+1+(1+2)+(1+2+3)+(1+2+3+4)

            // Yields 0:
            // Blueprint 1:
            // Each ore robot costs 4 ore.
            // Each clay robot costs 4 ore.
            // Each obsidian robot costs 4 ore and 14 clay.
            // Each geode robot costs 2 ore and 16 obsidian.

            // progression should be:
            // 4min (4 ore) 1cb
            // 8min (4 clay) 2cb
            // 12min (12 clay) 3cb
            // 13min (15 clay)

            // Yields geodes:
            // Blueprint 2:
            // Each ore robot costs 2 ore.
            // Each clay robot costs 2 ore.
            // Each obsidian robot costs 2 ore and 15 clay.
            // Each geode robot costs 2 ore and 7 obsidian.

            //


            println("blueprint ID: $id")
            println("oreForClay: $oreForClay, clayForObsidian: $clayForObsidian, obsidianForGeode: $obsidianForGeode")

            val earliestClay = oreForClay
            println("earliestClay: $earliestClay")
            val earliestObsidian = getEarliestFrom(oreForClay, clayForObsidian)
            println("earliestObsidian: $earliestObsidian")
            val earliestGeode = getEarliestFrom(clayForObsidian, obsidianForGeode)
            println("earliestGeode: $earliestGeode")
            println("can mine at least one geode: ${earliestClay + earliestObsidian + earliestGeode <= t}")

                return earliestClay + earliestObsidian + earliestGeode <= t
        }

    }


}

fun getEarliestFrom(currency: Int, threshold: Int, upper: Int = 20) =
    (1..upper).find { triangularNumber(it) * currency >= threshold } ?: Int.MAX_VALUE

fun triangularNumber(n: Int) = (n * (n + 1)) / 2
