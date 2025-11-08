import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.FunSpec
import java.net.URL
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.time.measureTimedValue

class UtilKtTest : FunSpec({
    test("powerSet") {
        val s = setOf("A", "B", "C")
        val ps = s.powerSet().filter { it.isNotEmpty() && it.size != s.size }
        val pss = ps.map { it to (s - it) }
        pss shouldHaveSize 6
        println(pss)
        println(2.0.pow(16).toInt())
        val sn = (1..16).toSet()
        println(sn)
        println(measureTimedValue { sn.powerSet().size })
        println(measureTimedValue { sn.powerSetSeq().count() })
    }

    test("powerSetSeq") {
        val s = setOf("A", "B", "C")
        val ps1 = s.powerSetSeq().toList()
        val ps2 = s.powerSet().toList()
        ps1 shouldBe ps2
    }

    test("permutations") {
        println(listOf("a", "b", "c").permutations().toList())
        listOf("a", "b", "c").permutations().toList() shouldHaveSize 6
    }

    test("neighbors4") {
        val ia = Array(5) { IntArray(5) }

        ia.neighbors4(0, 0) shouldHaveSize 2
        ia.neighbors4(0, 1) shouldHaveSize 3
        ia.neighbors4(0, 4) shouldHaveSize 2
        ia.neighbors4(1, 1) shouldHaveSize 4
    }

    test("neighbors8") {
        val ia = Array(5) { IntArray(5) }

        ia.neighbors8(0, 0) shouldHaveSize 3
        ia.neighbors8(0, 1) shouldHaveSize 5
        ia.neighbors8(0, 4) shouldHaveSize 3
        ia.neighbors8(1, 1) shouldHaveSize 8
    }

    test("chunkedBy") {
        listOf(3, 1, 4, 1, 5, 9).chunkedBy { it % 2 == 0 } shouldBe listOf(listOf(3, 1), listOf(1, 5, 9))
    }

    test("countingMap") {
        val map = CountingMap<String>()
        map.inc("a")
        map.inc("b")
        map.inc("c", 3L)
        map.inc("a")

        map shouldHaveSize 3
        map.count("a") shouldBe 2L
        map.count("d") shouldBe 0L

        map.entries().sumOf { it.value } shouldBe 6
    }

    test("countingMapWithInit") {
        val map = CountingMap(listOf("a", "b", "c", "d"))
        map.inc("a")
        map.inc("b")
        map.inc("c", 3L)
        map.inc("a")

        map shouldHaveSize 4
        map.count("a") shouldBe 3L
        map.count("d") shouldBe 1L

        map.entries().sumOf { it.value } shouldBe 10
    }

    test("gcd") {
        gcd(10, 5) shouldBe 5
        gcd(10, 15) shouldBe 5
        gcd(10L, 15L) shouldBe 5L
        gcd(10, 10) shouldBe 10
        gcd(101, 103) shouldBe 1
        gcd(101 * 3, 103 * 6) shouldBe 3
    }

    test("numbers") {
        "a3b1c4d".ints() shouldBe listOf(3, 1, 4)
        "-1,3-".ints() shouldBe listOf(-1, 3)
        "a3b1c4d".longs() shouldBe listOf(3L, 1L, 4L)
        "-1,3-".longs() shouldBe listOf(-1L, 3L)
    }

    test("lcm") {
        lcm(10, 5) shouldBe 10
        lcm(10, 15) shouldBe 30
        lcm(10L, 15L) shouldBe 30L
        lcm(10, 10) shouldBe 10
        lcm(101, 103) shouldBe 101 * 103
        lcm(101 * 3, 103 * 6) shouldBe 101 * 103 * 6
    }

    test("minMax") {
        listOf(3, 1, 4, 1, 5, 9).minMax() shouldBe intArrayOf(1, 9)
        val (i, a) = listOf(3, 1, 4).minMax()
        i shouldBe 1
        a shouldBe 4
        listOf(3L, 1L, 4L, 1L, 5L, 9L).minMax() shouldBe longArrayOf(1L, 9L)
    }

    test("minMax using multiFold") {
        fun minMax(l: List<Int>) = l.multiFold(listOf(Int.MAX_VALUE, Int.MIN_VALUE), listOf(::min, ::max))
        minMax(listOf(3, 1, 4, 1, 5, 9)) shouldBe intArrayOf(1, 9)
    }

    test("minMax using multiReduce") {
        fun minMax(l: List<Int>) = l.multiReduce(::min, ::max)
        minMax(listOf(3, 1, 4, 1, 5, 9)) shouldBe intArrayOf(1, 9)
    }

    test("multiFold") {
        listOf(3, 1, 4).multiFold(listOf(Int.MAX_VALUE, Int.MIN_VALUE), listOf(::min, ::max)) shouldBe listOf(1, 4)
    }

    test("multiReduce") {
        listOf(3, 1, 4).reduce(::min) shouldBe 1
        listOf(3, 1, 4).reduce(::max) shouldBe 4
        listOf(3, 1, 4).multiReduce(::min, ::max) shouldBe listOf(1, 4)
    }

    test("manhattanDistance") {
        manhattanDistance(0, 0, 2, 2) shouldBe 4
        manhattanDistance(0L, 0L, 2L, 2L) shouldBe 4L
        manhattanDistance(Point(0, 0), Point(2, 2)) shouldBe 4
        manhattanDistance(0, 0, 0, 2, 2, 2) shouldBe 6
        manhattanDistance(0L, 0L, 0L, 2L, 2L, 2L) shouldBe 6L
        manhattanDistance(intArrayOf(0, 0), intArrayOf(1, 2)) shouldBe 3
        manhattanDistance(longArrayOf(0L, 0L), longArrayOf(1L, 2L)) shouldBe 3L
    }
})
