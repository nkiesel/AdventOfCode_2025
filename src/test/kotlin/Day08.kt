import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.math.pow
import kotlin.math.sqrt

object Day08 {
    private data class P3(val x: Double, val y: Double, val z: Double) {
        constructor(l: List<Int>) : this(l[0].toDouble(), l[1].toDouble(), l[2].toDouble())

        fun distance(o: P3) =
            sqrt((x - o.x).pow(2) + (y - o.y).pow(2) + (z - o.z).pow(2))
    }

    fun three(input: List<String>, part: Part, rep: Int = 1000): Int {
        val boxes = input.map { P3(it.ints()) }
        val circuits = boxes.map { mutableSetOf(it) }.toMutableList()
        val distances = buildMap {
            for (i1 in boxes.indices) {
                val b1 = boxes[i1]
                for (i2 in i1 + 1 until boxes.size) {
                    val b2 = boxes[i2]
                    put(listOf(b1, b2), b1.distance(b2))
                }
            }
        }.entries.sortedBy { it.value }
        var connections = 0
        while (true) {
            val shortest = distances[connections]
            val (b1, b2) = shortest.key
            val c1 = circuits.first { it.contains(b1) }
            val c2 = circuits.first { it.contains(b2) }
            if (c1 != c2) {
                if (part == Part.TWO && circuits.size == 2) {
                    return b1.x.toInt() * b2.x.toInt()
                }
                c1.addAll(c2)
                circuits.remove(c2)
            }
            connections++
            if (part == Part.ONE && connections == rep) {
                return circuits.map { it.size }.sorted().takeLast(3).product()
            }
        }
    }
}

object Day08Test : FunSpec({
    val input = lines("Day08")

    val sample = """
        162,817,812
        57,618,57
        906,360,560
        592,479,940
        352,342,300
        466,668,158
        542,29,236
        431,825,988
        739,650,466
        52,470,668
        216,146,977
        819,987,18
        117,168,530
        805,96,715
        346,949,466
        970,615,88
        941,993,340
        862,61,35
        984,92,344
        425,690,689
    """.trimIndent().lines()

    with(Day08) {
        test("one") {
            three(sample, Part.ONE, 10) shouldBe 40
            three(input, Part.ONE, 1000) shouldBe 163548
        }

        test("two") {
            three(sample, Part.TWO) shouldBe 25272
            three(input, Part.TWO) shouldBe 772452514
        }
    }
})

/*
I first got both gold stars by always finding the closest not connected yet boxes. But that of course computed
the distance between boxes a lot or times, and that code ran for 30 seconds.  Then I thought about it more and
came up with the current solution: compute the distances between all boxes once and store it in a list sorted by
the distance.  Then the code simply iterates over that list and computes the circuits. This now runs in less than
1 second for both parts.
 */
