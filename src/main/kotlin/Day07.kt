import java.io.File
import java.util.*

fun main() {
    val input = File("input/07.txt").readText(Charsets.UTF_8)
//    val testInput = File("input/07_test.txt").readText(Charsets.UTF_8)
    val solutionA = solveA(input)
    println(solutionA)
    val solutionB = solveB(input)
    println(solutionB)
}

private fun solveA(input: String): Any {
    val rootDir = parseToDir(input)
    val allDirs = getAllDirsIn(rootDir)
    return allDirs
        .filter { it.dirSize <= 100_000 }
        .sumOf { it.dirSize }
}

private fun solveB(input: String): Any {
    val rootDir = parseToDir(input)

    val DISK_SIZE = 70_000_000
    val SPACE_FOR_UPDATE = 30_000_000
    val USED_SPACE = rootDir.dirSize
    val NEEDED_SPACE_LEFT = USED_SPACE - (DISK_SIZE - SPACE_FOR_UPDATE)

    val allDirs = getAllDirsIn(rootDir)
    return allDirs
        .filter { it.dirSize >= NEEDED_SPACE_LEFT }
        .minBy { it.dirSize }.dirSize
}

private fun getAllDirsIn(rootDir: Dir): MutableSet<Dir> {
    val queue = listOf(rootDir).toMutableList()
    val allDirs = emptySet<Dir>().toMutableSet()

    while (queue.isNotEmpty()) {
        val dir = queue.removeLast()
        queue.addAll(dir.dirs)
        allDirs.add(dir)
    }
    return allDirs
}

private fun parseToDir(input: String): Dir {
    val cdPattern = Regex("^\\$ cd ([a-zA-Z]\\w*)$")
    val cdUpPattern = Regex("^\\$ cd \\.\\.$")
    val filePattern = Regex("^(?<size>\\d*) ([a-zA-Z.]*)$")

    val rootDir = Dir()
    val dirsToParse = Stack<Dir>()
    dirsToParse += rootDir

    input.splitToSequence('\n')
        .drop(1)
        .filter { it != "$ ls" && !it.startsWith("dir") }
        .forEach {
            when {
                cdPattern.matches(it) -> {
                    val dir = Dir()
                    dirsToParse.peek().dirs.add(dir)
                    dirsToParse.push(dir)
                }

                cdUpPattern.matches(it) -> {
                    val dir = dirsToParse.peek()
                    dir.dirSize = dir.dirs.sumOf { it.dirSize } + dir.fileSize
                    dirsToParse.pop()
                }

                filePattern.matches(it) -> {
                    val fileSize = filePattern.find(it)!!.groups["size"]!!.value.toInt()
                    dirsToParse.peek().fileSize += fileSize
                }
            }
        }

    while (dirsToParse.isNotEmpty()) {
        val dir = dirsToParse.pop()
        dir.dirSize = dir.dirs.sumOf { it.dirSize } + dir.fileSize
    }
    return rootDir
}

private data class Dir(
    var dirs: MutableList<Dir> = emptyList<Dir>().toMutableList(),
    var fileSize: Int = 0,
    var dirSize: Int = 0,
)