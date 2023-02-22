import kotlin.system.measureTimeMillis

fun main() {
    val day18 = Day18("input/18.txt")
    measureTimeMillis {
        day18.solve()
            .also { result -> println(result) }
    }.also { elapsedTime -> println("Time taken: $elapsedTime ms") }
}

class Day18(inputFileName: String) : Day(inputFileName) {
    /*
    algo (now implemented):
    - build bounding cube overlapping droplet on each side
    - flood fill bounding cube with seed from a corner to get a hull
    - calculate the surface of the hull
    - deduct outer surfaces of bounding cube
    */
    fun solve(): Any {
        val droplet = Voxel.parseFrom(input)

        val boundingCube = droplet.getBoundingCube()
//        println(toString(boundingCube))

        val (length, width, height) = boundingCube.getDimensions()
        val hull = boundingCube.findConnectedVoxelsFrom(length - 1, width - 1, height - 1)

        val outerSurfaces = 2 * length * width + 2 * length * height + 2 * width * height
        val allSurfaces = hull.getSurfaces()

        return Pair(droplet.getSurfaces(), allSurfaces - outerSurfaces)
    }

    data class Voxel(val x: Int, val y: Int, val z: Int) {
        fun isLeftOf(other: Voxel) = x == other.x - 1 && y == other.y && z == other.z
        fun isInFrontOf(other: Voxel) = x == other.x && y == other.y - 1 && z == other.z
        fun isOnTopOf(other: Voxel) = x == other.x && y == other.y && z == other.z - 1
        fun getNeighboursWithinBounds(cube: BoundingCube): Set<Voxel> {
            val (bx, by, bz) = cube.getDimensions()
            // @formatter:off
            return setOfNotNull(
                if (x < bx+1) Voxel(x + 1, y, z) else null,
                if (x > 0)    Voxel(x - 1, y, z) else null,
                if (y < by+1) Voxel(x, y + 1, z) else null,
                if (y > 0)    Voxel(x, y - 1, z) else null,
                if (z < bz+1) Voxel(x, y, z + 1) else null,
                if (z > 0)    Voxel(x, y, z - 1) else null,
            )
            // @formatter:on
        }

        companion object {
            fun parseFrom(input: String): Set<Voxel> {
                return input
                    .split('\n')
                    .map { it.split(",") }
                    .map { Voxel(it[0].toInt(), it[1].toInt(), it[2].toInt()) }
                    .toSet()
            }
        }
    }

    private fun Set<Voxel>.getBoundingCube(): BoundingCube {
        val shiftedDroplet = this.map { (x, y, z) -> Voxel(x + 1, y + 1, z + 1) }

        val xLength = shiftedDroplet.maxOf { it.x }
        val yLength = shiftedDroplet.maxOf { it.y }
        val zLength = shiftedDroplet.maxOf { it.z }

        val boundingCube = BoundingCube(xLength, yLength, zLength)
        shiftedDroplet.forEach { boundingCube.setAtVoxel(it, true) }
        return boundingCube
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

    class BoundingCube(length: Int, width: Int, height: Int) {
        private val cube: Array<Array<BooleanArray>> =
            Array(length + 2) { Array(width + 2) { BooleanArray(height + 2) } }

        fun findConnectedVoxelsFrom(x: Int, y: Int, z: Int): MutableSet<Voxel> {
            val seed = Voxel(x, y, z)
            this.setAtVoxel(seed, true)

            val connectedVoxels = mutableSetOf<Voxel>()
            val queue = mutableListOf(seed)

            var currentVoxel: Voxel
            while (queue.isNotEmpty()) {
                currentVoxel = queue.removeFirst()
                connectedVoxels.add(currentVoxel)
                val neighbours = currentVoxel
                    .getNeighboursWithinBounds(this)
                    .filter { this.getAtVoxel(it) == false }

                neighbours.forEach { this.setAtVoxel(it, true) }
                queue.addAll(neighbours)
            }
            return connectedVoxels
        }

        fun getDimensions(): Triple<Int, Int, Int> {
            val length = cube.first().first().size
            val width = cube.first().size
            val height = cube.size
            return Triple(length, width, height)
        }

        private fun getAtVoxel(voxel: Voxel) = cube[voxel.z][voxel.y][voxel.x]

        fun setAtVoxel(voxel: Voxel, b: Boolean) {
            cube[voxel.z][voxel.y][voxel.x] = b
        }

        override fun toString(): String {
            return cube.joinToString("\n\n") { array ->
                array.joinToString("\n") { boolArray ->
                    boolArray.joinToString("") { if (it) "#" else "`" }
                }
            }
        }
    }
}