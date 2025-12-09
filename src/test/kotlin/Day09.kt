import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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
        val red = parse(input).map { LongPos(it.x, it.y) }
        println("#red: ${red.size}")
        val green = buildList {
            for ((p1, p2) in (red + red[0]).zipWithNext()) {
                if (p1.x == p2.x) {
                    for (y in min(p1.y, p2.y) + 1 until max(p1.y, p2.y)) {
                        add(LongPos(p1.x, y))
                    }
                } else if (p1.y == p2.y) {
                    for (x in min(p1.x, p2.x) + 1 until max(p1.x, p2.x)) {
                        add(LongPos(x, p1.y))
                    }
                } else {
                    error("not properly connected $p1 and $p2")
                }
            }
        }

        if (red.size < 100) {
            val area = CharArea(red.maxOf { it.x } + 3, red.maxOf { it.y } + 2, '.')
            green.forEach { area[Point(it.x, it.y)] = 'X' }
            red.forEach { area[Point(it.x, it.y)] = '#' }
            area.show()
        }

        val redOrGreen = (red + green).toSet()
        println("#redOrGreen: ${redOrGreen.size}")

        val minY = red.minOf { it.y }
        val maxY = red.maxOf { it.y }
        val rows =
            (minY..maxY).associateWith { y -> redOrGreen.filter { it.y == y }.map { it.x }.sorted().distinct() }

        fun isRedOrGreen(p: LongPos): Boolean {
            if (p in redOrGreen) return true
            val (x, y) = p
            val rx = rows[y]
            return rx != null && x >= rx.first() && x <= rx.last() && rx.count { it > x } % 2 == 1
        }

        val xRanges = mutableMapOf<LongPos, IntRange>()
        val yRanges = mutableMapOf<LongPos, IntRange>()
        for (p in red) {
            val (x, y) = p
            var x1 = x
            do x1-- while (isRedOrGreen(LongPos(x1, y)))
            var x2 = x
            do x2++ while (isRedOrGreen(LongPos(x2, y)))
            var y1 = y
            do y1-- while (isRedOrGreen(LongPos(x, y1)))
            var y2 = y
            do y2++ while (isRedOrGreen(LongPos(x, y2)))
            xRanges[p] = IntRange(x1 + 1, x2 - 1)
            yRanges[p] = IntRange(y1 + 1, y2 - 1)
        }
//        for (r in xRanges) {
//            println("x ${r.key}: ${r.value}")
//        }
//        for (r in yRanges) {
//            println("y ${r.key}: ${r.value}")
//        }

        fun allRedOrGreen(p1: LongPos, p2: LongPos): Boolean {
            return p1.x in xRanges[p2]!! && p1.y in yRanges[p2]!! && p2.x in xRanges[p1]!! && p2.y in yRanges[p1]!!
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
        println("#recs: ${recs.size}")
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
        11,7
        9,7
        9,5
        2,5
        2,3
        7,3
    """.trimIndent().lines()
    val sample2 = """
        1,1
        1,3
        1,5
        2,5
        2,1
        2,2
        2,3
        5,3
        5,5
        5,6
        1,6
    """.trimIndent().lines()

    with(Day09) {
        test("one") {
            one(sample) shouldBe 50L
            one(input) shouldBe 4782151432L
        }

        test("two") {
            println("-".repeat(50))
            two(sample2) shouldBe 20L
            two(sample) shouldBe 24L
            println("-".repeat(50))
            two(input) shouldNotBe 267043150L
        }
    }
})
