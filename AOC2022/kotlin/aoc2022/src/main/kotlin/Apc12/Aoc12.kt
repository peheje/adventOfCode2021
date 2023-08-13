package Apc12

import java.io.File
import java.util.*
import kotlin.collections.HashMap

data class EdgeId(val v: Pair<Int, Int>)

fun aoc12() {
    val filepath = "C:\\Users\\peter\\Repos\\LearningFSharp\\AOC2022\\12\\input12.txt"
    val rows = File(filepath).readLines()

    lateinit var origSource: EdgeId
    lateinit var destination: EdgeId

    val map = rows.mapIndexed { y, row ->
        row.toList().mapIndexed { x, char ->
            when (char) {
                'S' -> {
                    origSource = EdgeId(Pair(x, y))
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

    fun dijkstra(source: EdgeId): HashMap<EdgeId, EdgeId?> {
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

        return previous
    }


    fun shortestPath(source: EdgeId): List<EdgeId>? {
        val previous = dijkstra(source)
        var cursor = destination
        val shortestPath = mutableListOf<EdgeId>()
        do {
            val step = previous.getValue(cursor) ?: return null
            shortestPath.add(step)
            cursor = step

        } while (cursor != source)

        return shortestPath.reversed().toList()
    }

    val destinations = findAllDestinations(rows)

    println("part 1 shortest path: ${shortestPath(origSource)?.size}")

    val shortestPathFromLowestPoint =
        destinations
            .mapNotNull { shortestPath(it) }
            .minOf { it.size }
    println("part 2 shortestPathFromLowestPoint $shortestPathFromLowestPoint")
}

private fun findAllDestinations(rows: List<String>): List<EdgeId> {
    val destinations = mutableListOf<EdgeId>()
    for (i in rows.indices) {
        for (j in rows[i].indices) {
            if (rows[i][j] == 'a' || rows[i][j] == 'S')
                destinations.add(EdgeId(Pair(j, i)))
        }
    }
    return destinations
}