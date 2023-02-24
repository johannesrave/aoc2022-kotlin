import kotlin.system.measureTimeMillis

fun main() {
    val day19 = Day19("input/19_test.txt")
    measureTimeMillis {
        day19.solve()
            .also { result -> println(result) }
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }
}

class Day19(inputFileName: String) : Day(inputFileName) {
    fun solve(): Any {
        val blueprints = Blueprint.parseFrom(input)

        val result = blueprints.map { it.maximizeForGeodes() }

        return result
    }

    data class Blueprint(
        val oreForOre: Int,
        val oreForClay: Int,
        val oreForObsidian: Int,
        val clayForObsidian: Int,
        val oreForGeode: Int,
        val obsidianForGeode: Int
    ) {
        fun maximizeForGeodes(): List<State> {
            val t = 3

            var states = listOf(State())

            repeat(t) { i ->
                states = states.flatMap { state ->
                    listOfNotNull(
                        state.buildNothing(),
                        state.buildOreBotOrNull(this),
                        state.buildClayBotOrNull(this),
                        state.buildObsidianBotOrNull(this),
                        state.buildGeodeBotOrNull(this)
                    )
                }
                println("states in round $i:")
                println(states.joinToString("\n"))
            }
            return states
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
                if (ore >= blueprint.oreForObsidian && clay >= blueprint.clayForObsidian) {
                    val (ore, clay, obsidian, geodes) = calculateResources(
                        ore = ore - blueprint.oreForObsidian,
                        clay = clay - blueprint.clayForObsidian,
                    )
                    copy(obsidianBots = obsidianBots + 1, ore = ore, clay = clay, obsidian = obsidian, geodes = geodes)
                } else null

            fun buildGeodeBotOrNull(blueprint: Blueprint): State? =
                if (ore >= blueprint.oreForGeode && obsidian >= blueprint.obsidianForGeode) {
                    val (ore, clay, obsidian, geodes) = calculateResources(
                        ore = ore - blueprint.oreForGeode,
                        obsidian = obsidian - blueprint.obsidianForGeode
                    )
                    copy(geodeBots = geodeBots + 1, ore = ore, clay = clay, obsidian = obsidian, geodes = geodes)
                } else null

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
                        val (oreForOre, oreForClay, oreForObsidian, clayForObsidian, oreForGeode, obsidianForGeode) =
                            match.groupValues
                                .drop(1)
                                .mapNotNull { it.toIntOrNull() }
                        return@map Blueprint(
                            oreForOre,
                            oreForClay,
                            oreForObsidian,
                            clayForObsidian,
                            oreForGeode,
                            obsidianForGeode
                        )
                    }.toList()
            }
        }
    }
}

private operator fun <E> List<E>.component6(): E = get(5)
