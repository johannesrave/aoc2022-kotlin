import kotlin.system.measureTimeMillis


fun main() {
    val day16 = Day16("input/16.txt")

    println(day16.parseValves())
    var solutionA: Int
    val durationA = measureTimeMillis { solutionA = day16.search(30) }
    println("duration in milliseconds: $durationA")
    println(solutionA)
    println(day16.searchCache.size)
    println(day16.cacheCounter)

    day16.searchCache.clear()
    day16.cacheCounter = 0
    var solutionB: Int
    val durationB = measureTimeMillis { solutionB = day16.search(26, elephant = true) }
    println("duration in milliseconds: $durationB")
    println(solutionB)
    println(day16.searchCache.size)
    println(day16.cacheCounter)

}

class Day16(inputFileName: String) : Day(inputFileName) {
    // heavily inspired by https://topaz.github.io/paste/#XQAAAQDfAgAAAAAAAAA0m0pnuFI8c82uPD0wiI6r5tRTRja98xwzlfwFtjHHMXROBlAd++OM5E2aWHrlz38tgjgBrDMkBDPm5k7eRTLnCaSEUZUXANmWw6a7dmZdD+qaJFp7E26PQ9Ml4fpikPmCeDnULBn3YHI/yLHbVDEdzTxQZhxa+aFb3fX8qpx50mBxYGkYIvkYoHqoND3JEEe2PE8yfBjpZNgC+Vp30p9nwCUTCSQrDlaj6RCgZyoOK4E/0QTzzMTpAvuwXfRpaEG4N87Y0Rr49K516SKwkvAatNXD/MBZ2thEgjpndUPRb/SA5eo0d/OjeyIlhgFibQYYZ4KHpAn3uPUJ9CSsdyr6/TnmqI95UsPYgMPNLWjLQmy3+35ne8bKXq3SHASY+91H7LIAFMGp5QhI53Qkvfo+WAJDHW6OTabv0QXSAvP57DAnBBAMS+R0W4H3bc4fRaVa+nfP7ifAKLKxGr1w3jHedPV2HRQ4bLOdmkB0vO9OReM6lNK7nTH1EF91P5PwmenHxXGnjjhp12efsEpBwFP/p/Vk7z/7zxwFT7c5+MBovbAHfbFNxQZtnVlrS1cGvRmx5bufXqoglHIp7DFNWyZVPp8TE5qiC8hSEyzLr/+x2pjq

    private lateinit var flows: Map<String, Int>
    private lateinit var distances: Map<Pair<String, String>, Int>

    fun parseValves(): Pair<Map<String, Int>, Map<Pair<String, String>, Int>> {
        val valvePattern = """Valve (\w{2}) has flow rate=(\d+); tunnels? leads? to valves? ([A-Z, ]{2,})""".toRegex()

        val valves = emptySet<String>().toMutableSet()
        val flows = emptyMap<String, Int>().toMutableMap()
        val distances = emptyMap<Pair<String, String>, Int>().toMutableMap().withDefault { 1000 }

        valvePattern
            .findAll(input)
            .forEach {
                val (_, valve, flow, tunnels) = it.groupValues
                valves += valve
                if (flow != "0" || valve == "AA") {
                    flows += valve to flow.toInt()
                }
                for (tunnel in tunnels.split(", ")) {
                    distances[valve to tunnel] = 1
                }
            }

        for (via in valves) {
            for (start in valves - via) {
                for (target in valves - via - start) {
                    val oldDistance = distances.getValue(start to target)
                    val newDistance = distances.getValue(start to via) + distances.getValue(via to target)
                    distances[start to target] = oldDistance.coerceAtMost(newDistance)
                }
            }
        }

        this.flows = flows
        this.distances = distances
            .filter { (key) -> key.first in flows.keys && key.second in flows.keys - "AA" }
            .mapValues { it.value + 1 }

        return Pair(flows, this.distances)
    }

    val searchCache = mutableMapOf<SearchParams, Int>()
    var cacheCounter = 0

    fun search(
        t: Int,
        targets: Map<String, Int> = this.flows - "AA",
        current: String = "AA",
        elephant: Boolean = false
    ): Int {
        val key = SearchParams(t, targets, current, elephant)
        if (!searchCache.containsKey(key)) {
            cacheCounter++
            searchCache[key] = targets
                .mapNotNull { (valve, flow) ->
                    val tRemaining = t - distances.getValue(current to valve)
                    if (tRemaining < 0) return@mapNotNull null
                    (flow * tRemaining) + search(tRemaining, targets - valve, valve, elephant = elephant)
                }.plus(if (elephant) search(26, targets) else 0)
                .max()
        }

        return searchCache.getValue(key)
    }

    data class SearchParams(
        val t: Int,
        val targets: Map<String, Int>,
        val current: String,
        val elephant: Boolean
    )
}