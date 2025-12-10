import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.math.max
import kotlin.math.min

object Day09 {
    private fun parse(input: List<String>) = input.map { it.ints().let { Point(it[0], it[1]) } }

    fun one(input: List<String>): Long {
        val points = parse(input)
        var md = 0L
        for ((i, p1) in points.withIndex()) {
            for (j in i + 1 until points.size) {
                val p2 = points[j]
                val d = ((p1.x delta p2.x) + 1).toLong() * ((p1.y delta p2.y) + 1)
                md = max(d, md)
            }
        }
        return md
    }

    fun two(input: List<String>): Long {
        val red = parse(input)
        val minX = red.minOf { it.x }
        val maxX = red.maxOf { it.x }
        val minY = red.minOf { it.y }
        val maxY = red.maxOf { it.y }
        val rowSets = (minY..maxY).associateWith { y -> mutableSetOf<Int>() }
        val colSets = (minX..maxX).associateWith { y -> mutableSetOf<Int>() }

        println("red: ${red.size}")
        val green = buildList {
            var dir = '.'
            for ((p1, p2) in (red + red[0]).zipWithNext()) {
                if (p1.x == p2.x) {
                    for (y in min(p1.y, p2.y) + 1 until max(p1.y, p2.y)) {
                        add(Point(p1.x, y))
                        rowSets[y]!! += p1.x
                    }
                    if (dir == '|') {
                        rowSets[p1.y]!! += p1.x
                    }
                    dir = '|'
                } else if (p1.y == p2.y) {
                    for (x in min(p1.x, p2.x) + 1 until max(p1.x, p2.x)) {
                        add(Point(x, p1.y))
                        colSets[x]!! += p1.y
                    }
                    if (dir == '-') {
                        colSets[p1.x]!! += p1.y
                    }
                    dir = '-'
                }
            }
        }

        if (red.size < 100) {
            val area = CharArea(red.maxOf { it.x } + 3, red.maxOf { it.y } + 2, '.')
            green.forEach { area[it] = 'X' }
            red.forEach { area[it] = '#' }
            area.show()
        }

        val redOrGreen = (red + green).toSet()
        println("redOrGreen: ${redOrGreen.size}")

        val rows = rowSets.mapValues { r -> r.value.sorted().toList() }
        val cols = colSets.mapValues { r -> r.value.sorted().toList() }

        fun isRedOrGreen(p: Point): Boolean {
            if (p in redOrGreen) return true
            val (x, y) = p
            if (x !in minX..maxX || y !in minY..maxY) return false
            val rx = rows[y]
            val ry = cols[x]
            return rx != null && ry != null && (rx.count { it > x } % 2 == 1 || rx.count { it < x } % 2 == 1) && (ry.count { it > y } % 2 == 1 || ry.count { it < y } % 2 == 1)
        }

        val xRanges = mutableMapOf<Point, IntRange>()
        val yRanges = mutableMapOf<Point, IntRange>()
        for (p in red) {
            val (x, y) = p
            var x1 = x
            do x1-- while (isRedOrGreen(Point(x1, y)))
            var x2 = x
            do x2++ while (isRedOrGreen(Point(x2, y)))
            var y1 = y
            do y1-- while (isRedOrGreen(Point(x, y1)))
            var y2 = y
            do y2++ while (isRedOrGreen(Point(x, y2)))
            xRanges[p] = IntRange(x1 + 1, x2 - 1)
            yRanges[p] = IntRange(y1 + 1, y2 - 1)
        }

        fun allRedOrGreen(p1: Point, p2: Point): Boolean {
            return p1.x in xRanges[p2]!! && p1.y in yRanges[p2]!! && p2.x in xRanges[p1]!! && p2.y in yRanges[p1]!!
        }

        if (red.size < 100) {
            val area = CharArea(red.maxOf { it.x } + 3, red.maxOf { it.y } + 2, '.')
            area.tiles().filter { isRedOrGreen(it) }.forEach { area[it] = '*' }
            red.forEach { area[it] = '#' }
            area.show()
        }

        val recs = buildMap {
            for ((i, p1) in red.withIndex()) {
                for (j in i + 1 until red.size) {
                    val p2 = red[j]
                    val d = ((p1.x delta p2.x) + 1).toLong() * ((p1.y delta p2.y) + 1)
                    put(Pair(p1, p2), d)
                }
            }
        }.entries.sortedByDescending { it.value }
        println("recs: ${recs.size}")
        recs.forEach { (p, size) ->
            if (allRedOrGreen(p.first, p.second)) {
                println("p1: ${p.first} p2: ${p.second} size: $size")
                return size
            }
        }
        return 0L
    }
}

object Day09Test : FunSpec({
    val input = lines("Day09")

    val sample = """
        7,1
        11,1
        11,4
        11,7
        9,7
        9,5
        2,5
        2,3
        7,3
    """.trimIndent().lines()
    val sample2 = """
        1,1
        4,1
        4,3
        7,3
        7,1
        8,1
        8,3
        8,5
        1,5
    """.trimIndent().lines()
    val sample3 = """
        1,1
        5,1
        5,3
        2,3
        2,20
        1,20
    """.trimIndent().lines()

    with(Day09) {
        test("one") {
            one(sample) shouldBe 50L
            one(input) shouldBe 4782151432L
        }

        test("two") {
            println("-".repeat(50))
            two(sample) shouldBe 24L
            println("-".repeat(50))
            two(sample2) shouldBe 24L
            println("-".repeat(50))
            two(sample3) shouldBe 40L
            println("-".repeat(50))
            two(input) shouldBe 1450414119L
        }
    }
})
