import kotlin.system.measureTimeMillis

fun main() {
    // val day17a = Day18("input/17.txt")
    // measureTimeMillis {
    //     day17a.calculateHeight(2022)
    //         .also { result -> println(result) }
    // }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }
    // 3090

    val day18 = Day18("input/18.txt")
    measureTimeMillis {
        day18.solve()
            .also { result -> println(result) }
        // 4044 was too high
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }

}

class Day18(inputFileName: String) : Day(inputFileName) {

    /*
    algo:
    - build a bounding cube of the droplet as a  3D-array
    - find all startpoints by picking all voxels around droplet-voxels, that aren't part of the droplet
    - floodfill the bounding cube to find continuous voxel-groups
    - add all groups that don't touch the bounding cube to the droplet-group
    - calculate its surface


    alternative algo (not implemented):
    - build bounding cube overlapping droplet on each side
    - fill bounding cube with seed in a corner
    - calculate the surface
    - deduct outer surfaces of bounding cube
    */
    fun solve(): Any {
        val droplet = parseVoxelsFrom(input)

        val zLength = droplet.maxOf { it.z }
        val yLength = droplet.maxOf { it.y }
        val xLength = droplet.maxOf { it.x }

        // 0 = empty, 1 = droplet, 2 = visited, 4 = border
        val boundingCube =
            Array(zLength + 2) { z ->
                Array(yLength + 2) { y ->
                    IntArray(xLength + 2) { x ->
                        if (x == 0 || y == 0 || z == 0 || x == xLength + 1 || y == yLength + 1 || z == zLength + 1) 4
                        else 0
                    }
                }
            }

        droplet.forEach { (x, y, z) -> boundingCube[z][y][x] = 1 }

        println(
            boundingCube.joinToString("\n\n") { xArray ->
                xArray.joinToString("\n") { yArray ->
                    yArray.joinToString("_")
                }
            }
        )

        val pocketSeeds = droplet
            .flatMap { it.getNeighbours() }
            .filter { seed -> seed !in droplet }
            .filter { (nx, ny, nz) -> boundingCube.getOrNull(nz)?.getOrNull(ny)?.getOrNull(nx) == 4 }
//            .also { println(it) }

        val pockets = pocketSeeds.mapNotNull { seedVoxel ->
            val (x, y, z) = seedVoxel
            if (boundingCube[z][y][x] != 0) {
//                println("$seedVoxel isn't flagged empty - skipping")
                return@mapNotNull null
            }
//            println("filling pocket starting at $seedVoxel")

            val pocket = mutableSetOf<Voxel>()
            val queue = mutableListOf(Voxel(x, y, z))
            boundingCube[z][y][x] = 2

            var currentVoxel: Voxel
            while (queue.isNotEmpty()) {
//                println("length of queue: ${queue.size}")
                currentVoxel = queue.removeFirst()
                pocket.add(currentVoxel)
                val neighbours = currentVoxel
                    .getNeighbours()
                    .filter { (nx, ny, nz) -> boundingCube[nz][ny][nx] == 0 }

                neighbours.forEach { (nx, ny, nz) -> boundingCube[nz][ny][nx] = 2 }

                queue.addAll(neighbours)
            }
            return@mapNotNull pocket
        }
            .filter { it.any { voxel -> voxel.getNeighbours().none {(nx, ny, nz) -> boundingCube[nz][ny][nx] == 4 } } }
            .toSet()

        println("pockets:")
        println(pockets.joinToString("\n"))

        return droplet.getSurfaces() - pockets.sumOf { it.getSurfaces() }
    }

    private fun parseVoxelsFrom(input: String): Set<Voxel> {
        return input
            .split('\n')
            .map { it.split(",") }
            .map { Voxel(it[0].toInt(), it[1].toInt(), it[2].toInt()) }
            .toSet()

    }

    data class Voxel(val x: Int, val y: Int, val z: Int) {
        fun isLeftOf(other: Voxel) = x == other.x - 1 && y == other.y && z == other.z
        fun isRightOf(other: Voxel) = x == other.x + 1 && y == other.y && z == other.z
        fun isInFrontOf(other: Voxel) = x == other.x && y == other.y - 1 && z == other.z
        fun isBehindOf(other: Voxel) = x == other.x && y == other.y + 1 && z == other.z
        fun isOnTopOf(other: Voxel) = x == other.x && y == other.y && z == other.z - 1
        fun isBelow(other: Voxel) = x == other.x && y == other.y && z == other.z + 1

        fun getNeighbours() = listOf(
            Voxel(x + 1, y, z),
            Voxel(x - 1, y, z),
            Voxel(x, y + 1, z),
            Voxel(x, y - 1, z),
            Voxel(x, y, z + 1),
            Voxel(x, y, z - 1),
        )
    }

    private fun Set<Voxel>.getSurfaces(): Int {
        var surfaces = size * 6

        for (voxel in this) {
            surfaces -= this.filter { voxel.isInFrontOf(it) }.size * 2
            surfaces -= this.filter { voxel.isLeftOf(it) }.size * 2
            surfaces -= this.filter { voxel.isOnTopOf(it) }.size * 2
        }
        return surfaces
    }
}

