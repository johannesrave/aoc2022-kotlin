fun main() {
    val day10 = Day10("input/10.txt")
//    val day10 = Day10("input/10_test.txt")

    val solutionA = day10.calculateSignalStrength()
    println(solutionA)
    val solutionB = day10.renderCRT()
    println(solutionB)
}

class Day10(inputFileName: String) : Day(inputFileName) {
    fun calculateSignalStrength(): Any {
        val commands = parseToCommands(input).iterator()

        var signalStrength = 0
        var addReg = 0
        var xReg = 1
        var processing = false
        val relevantCycles = 20..220 step 40

        for (cycle in 1..220) {
            if (cycle in relevantCycles) {
                signalStrength += (cycle * xReg)
            }
            if (processing) {
                xReg += addReg
                addReg = 0
                processing = false
                continue
            }
            commands
                .next()
                .takeIf { it.startsWith("addx") }?.let {
                    addReg += it.split(' ')[1].toInt()
                    processing = true
                }
        }
        return signalStrength
    }

    private fun parseToCommands(input: String): List<String> {
        return input.split('\n')
    }

    fun renderCRT(): Any {
        val commands = parseToCommands(input).iterator()

        var addReg = 0
        var xReg = 1
        var processing = false
        val buffer = emptyList<Char>().toMutableList()

        for (cycle in 0..239) {
            if (cycle % 40 in xReg-1..xReg+1) {
                buffer.add('#')
            } else {
                buffer.add('.')
            }
            if (processing) {
                xReg += addReg
                addReg = 0
                processing = false
                continue
            }
            commands.next()
                .takeIf { it.startsWith("addx") }
                ?.let {
                    addReg += it.split(' ')[1].toInt()
                    processing = true
                }
        }

        return buffer.chunked(40)
            .map { it.joinToString("") }
            .joinToString("\n")

    }
}



