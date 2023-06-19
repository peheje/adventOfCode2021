import java.io.File
import java.util.PriorityQueue
import kotlin.system.measureTimeMillis

data class EdgeId(val v: Pair<Int, Int>)

fun main() {
    measureTimeMillis {
        aoc12()
    }.let { println("${it}ms") }
}

private fun aoc12() {
    val path = "/Users/phj/Code/F-Sharp-Advent-of-Code-2021/AOC2022/kotlin/aoc2022/src/main/kotlin/input12.txt"
    val rows = File(path).readLines()

    lateinit var destination: EdgeId
    lateinit var source: EdgeId

    val map = rows.mapIndexed { y, row ->
        row.toList().mapIndexed { x, char ->
            when (char) {
                'S' -> {
                    source = EdgeId(Pair(x, y))
                    'a'.code
                }

                'E' -> {
                    destination = EdgeId(Pair(x, y))
                    'z'.code
                }

                else -> char.code
            } - 97
        }
    }

    fun neighborIndices(x: Int, y: Int): List<EdgeId> {
        val indices = mutableListOf<EdgeId>()
        if (x > 0)
            indices.add(EdgeId(Pair(x - 1, y)))
        if (x < map.first().lastIndex)
            indices.add(EdgeId(Pair(x + 1, y)))
        if (y > 0)
            indices.add(EdgeId(Pair(x, y - 1)))
        if (y < map.lastIndex)
            indices.add(EdgeId(Pair(x, y + 1)))
        return indices
    }

    val infinite = 1000

    val graph = HashMap<EdgeId, List<Pair<EdgeId, Int>>>()
    map.forEachIndexed { y, row ->
        row.forEachIndexed { x, value ->
            val indices = neighborIndices(x, y).map { neighbor ->
                val (nx, ny) = neighbor.v
                val difference = map[ny][nx] - value
                val cost = if (difference > 1) infinite else 1
                Pair(neighbor, cost)
            }
            graph[EdgeId(Pair(x, y))] = indices
        }
    }

    val distances = hashMapOf<EdgeId, Int>()
    val previous = HashMap<EdgeId, EdgeId?>()
    val queue = PriorityQueue<Pair<EdgeId, Int>>(compareBy { it.second })

    for (v in graph.keys) {
        distances[v] = infinite
        previous[v] = null
        queue.add(v to infinite)
    }
    distances[source] = 0

    while (queue.isNotEmpty()) {
        val (edge, _) = queue.poll()

        graph.getValue(edge).forEach { (neighbor, weight) ->
            val totalDistance = distances.getValue(edge) + weight
            if (totalDistance < distances.getValue(neighbor)) {
                distances[neighbor] = totalDistance
                previous[neighbor] = edge
                queue.add(neighbor to totalDistance)
            }
        }
    }

    var cursor = destination
    val shortestPath = mutableListOf<EdgeId?>()
    do {
        val step = previous.getValue(cursor) ?: continue
        shortestPath.add(step)
        cursor = step

    } while (cursor != source)

    shortestPath.reversed().forEach {
        println(it)
    }

    println("Done with shortest path ${shortestPath.size}")
}


