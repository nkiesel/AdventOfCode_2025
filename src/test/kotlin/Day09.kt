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
        val red = parse(input).map { LongPos(it.x, it.y) }
        val green = buildList {
            for ((p1, p2) in (red + red[0]).zipWithNext()) {
                if (p1.x == p2.x) {
                    for (y in min(p1.y, p2.y)..max(p1.y, p2.y)) {
                        add(LongPos(p1.x, y))
                    }
                } else {
                    for (x in min(p1.x, p2.x)..max(p1.x, p2.x)) {
                        add(LongPos(x, p1.y))
                    }
                }
            }
        }
        val redOrGreen = (red + green).toSet()
        println("redOrGreen: ${redOrGreen.size}")

//        val minX = red.minOf { it.x }
//        val maxX = red.maxOf { it.x }
        val minY = red.minOf { it.y }
        val maxY = red.maxOf { it.y }
        val rows =
            (minY..maxY).associateWith { y -> redOrGreen.filter { it.y == y }.map { it.x }.sorted().distinct() }
//        val cols =
//            (minX..maxX).associateWith { x -> redOrGreen.filter { it.x == x }.map { it.y }.sorted().distinct() }

        val positive = mutableListOf<MutableSet<LongPos>>(mutableSetOf())
        val negative = mutableListOf<MutableSet<LongPos>>(mutableSetOf())

        fun addToCache(p: LongPos, b: Boolean) {
            val cache = if (b) positive else negative
            val l = cache.last()
            if (l.size < Int.MAX_VALUE / 1000) {
                l += p
            } else {
                println("${cache.size} $b")
                cache += mutableSetOf(p)
            }
        }

        fun isPositive(p: LongPos) = positive.any { p in it }

        fun isNegative(p: LongPos) = negative.any { p in it }

        fun isGreenOrRed(p: LongPos): Boolean {
            if (p in redOrGreen || isPositive(p)) return true
            if (isNegative(p)) return false
//            val ry = cols[p.x]
            val rx = rows[p.y]
            val b = /*ry != null && */ rx != null && rx.count { it > p.x } % 2 != 0
//                    && ry.count { it > p.y } % 2 != 0
            addToCache(p, b)
            return b
        }

        fun allRedOrGreen(p1: LongPos, p2: LongPos): Boolean {
            val minX = min(p1.x, p2.x)
            val maxX = max(p1.x, p2.x)
            val minY = min(p1.y, p2.y)
            val maxY = max(p1.y, p2.y)
            if (!isGreenOrRed(LongPos(minX, minY))) return false
            if (!isGreenOrRed(LongPos(minX, maxY))) return false
            if (!isGreenOrRed(LongPos(maxX, minY))) return false
            if (!isGreenOrRed(LongPos(maxX, maxY))) return false
            for (x in minX..maxX) {
                if (!isGreenOrRed(LongPos(x, minY))) return false
                if (!isGreenOrRed(LongPos(x, maxY))) return false
            }
            for (y in minY..maxY) {
                if (!isGreenOrRed(LongPos(minX, y))) return false
                if (!isGreenOrRed(LongPos(maxX, y))) return false
            }
//            for (x in minX + 1 until maxY) {
//                for (y in minY + 1 until maxY) {
//                    if (!isGreenOrRed(LongPos(x, y))) {
//                        return false
//                    }
//                }
//            }
            return true
        }
        buildMap {
            for ((i, p1) in red.withIndex()) {
                for (j in i + 1 until red.size) {
                    val p2 = red[j]
                    val d = ((p1.x delta p2.x) + 1).toLong() * ((p1.y delta p2.y) + 1)
                    put(Pair(p1, p2), d)
                }
            }
        }.entries.sortedByDescending { it.value }.forEach { (p, d) ->
            if (allRedOrGreen(p.first, p.second)) return d
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

    with(Day09) {
        xtest("one") {
            one(sample) shouldBe 50L
            one(input) shouldBe 4782151432L
        }

        test("two") {
//            two(sample) shouldBe 24L
            two(input) shouldBe 0L
        }
    }
})
