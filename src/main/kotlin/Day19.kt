//import kotlin.test.
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    val day19 = Day19("input/19.txt")
//    val day19 = Day19("input/19_test.txt")
    measureTimeMillis {
        day19.solveA().also { result -> println(result) }
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }

    measureTimeMillis {
        day19.solveB().also { result -> println(result) }
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }
}

class Day19(inputFileName: String) : Day(inputFileName) {
    fun solveA(): Any {
        return Blueprint
            .parseFrom(input)
            .map { Pair(it.id, it.maximizeForGeodes(24)) }
            .also { println(it) }
            .sumOf { it.first * it.second }
    }

    fun solveB(): Any {
        return Blueprint
            .parseFrom(input)
            .take(3)
            .map { it.maximizeForGeodes(32) }
            .also { println(it) }
            .fold(1) { acc, geodes -> acc * geodes }
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
        fun maximizeForGeodes(t: Int): Int {

            File("/test/dump_0").writeText(Json.encodeToString(State()))

            repeat(t) { i ->
                val buffer = File("/test/dump_${i + 1}").bufferedWriter()
                var lineCount = 0

                File("/test/dump_${i}").useLines { lines ->
                    lines.distinct().forEach { line ->
                        lineCount++
                        val state = Json.decodeFromString<State>(line)

                        val list = if (state.canAffordGeodeBot(this)) listOf(state.buildGeodeBot(this))
                        else if (state.canAffordObsidianBot(this) && (i < t - 2)) listOf(state.buildObsidianBot(this))
                        else if (i < t - 2) listOfNotNull(
                            state.buildNothing(),
                            state.buildOreBotOrNull(this),
                            state.buildClayBotOrNull(this),
                        )
                        else listOf(state.buildNothing())

                        list.forEach { newState ->
                            buffer.write(Json.encodeToString(newState))
                            buffer.newLine()
                        }
                    }
                }
//                println("round $i: no of states: $lineCount")
                buffer.close()
            }

            File("/test/dump_${t}").bufferedReader().use { reader ->
                return reader.lineSequence().maxOf { line -> Json.decodeFromString<State>(line).geodes }
                    .also { println("max for ${this.id}: $it") }
            }
        }

        @Serializable
        data class State(
            val oreBots: Short = 1,
            val clayBots: Short = 0,
            val obsidianBots: Short = 0,
            val geodeBots: Short = 0,
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
                    copy(
                        oreBots = (oreBots + 1).toShort(), ore = ore, clay = clay, obsidian = obsidian, geodes = geodes
                    )
                } else null
            }

            fun buildClayBotOrNull(blueprint: Blueprint): State? = if (ore >= blueprint.oreForClay) {
                val (ore, clay, obsidian, geodes) = calculateResources(ore = ore - blueprint.oreForClay)
                copy(
                    clayBots = (clayBots + 1).toShort(), ore = ore, clay = clay, obsidian = obsidian, geodes = geodes
                )
            } else null

            fun buildObsidianBot(blueprint: Blueprint): State {
                val (ore, clay, obsidian, geodes) = calculateResources(
                    ore = ore - blueprint.oreForObsidian,
                    clay = clay - blueprint.clayForObsidian,
                )
                return copy(
                    obsidianBots = (obsidianBots + 1).toShort(),
                    ore = ore,
                    clay = clay,
                    obsidian = obsidian,
                    geodes = geodes
                )
            }

            fun buildGeodeBot(blueprint: Blueprint): State {
                val (ore, clay, obsidian, geodes) = calculateResources(
                    ore = ore - blueprint.oreForGeode, obsidian = obsidian - blueprint.obsidianForGeode
                )
                return copy(
                    geodeBots = (geodeBots + 1).toShort(), ore = ore, clay = clay, obsidian = obsidian, geodes = geodes
                )
            }

            fun canAffordObsidianBot(blueprint: Blueprint) =
                ore >= blueprint.oreForObsidian && clay >= blueprint.clayForObsidian

            fun canAffordGeodeBot(blueprint: Blueprint) =
                ore >= blueprint.oreForGeode && obsidian >= blueprint.obsidianForGeode

            private fun calculateResources(
                ore: Int = this.ore, clay: Int = this.clay, obsidian: Int = this.obsidian, geodes: Int = this.geodes
            ): Resources = Resources(ore + oreBots, clay + clayBots, obsidian + obsidianBots, geodes + geodeBots)
        }

        data class Resources(val ore: Int, val clay: Int, val obsidian: Int, val geodes: Int)

        companion object {
            private val pattern =
                //@formatter:off
                ("Blueprint (\\d+):.*" +
                    "ore robot costs (\\d+) ore.* " +
                    "clay robot costs (\\d+) ore.* " +
                    "obsidian robot costs (\\d+) ore and (\\d+) clay.* " +
                    "geode robot costs (\\d+) ore and (\\d+) obsidian.").toRegex()
                //@formatter:on

            fun parseFrom(input: String): List<Blueprint> {
                return pattern.findAll(input).map { match ->
                    val (id, oreForOre, oreForClay, oreForObsidian, clayForObsidian, oreForGeode, obsidianForGeode) =
                        match.groupValues
                            .drop(1)
                            .mapNotNull { it.toIntOrNull() }
                    return@map Blueprint(
                        id, oreForOre, oreForClay, oreForObsidian, clayForObsidian, oreForGeode, obsidianForGeode
                    )
                }.toList()
            }

            private operator fun <E> List<E>.component6(): E = get(5)
            private operator fun <E> List<E>.component7(): E = get(6)
        }
    }
}