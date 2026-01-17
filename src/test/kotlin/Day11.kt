import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object Day11 {
    private fun parse(input: List<String>) =
        input.associate { it.substringBefore(":") to it.substringAfter(": ").split(" ") }

    private data class Path(val dev: String, val count: Long)

    private fun count(devs: Map<String, List<String>>, start: String, end: String): Long {
        var paths = listOf(Path(start, 1))
        var count = 0L
        while (true) {
            val next = buildList {
                for (n in paths) {
                    for (p in devs[n.dev].orEmpty()) {
                        if (p == end) count += n.count else add(Path(p, n.count))
                    }
                }
            }
            if (next.isEmpty()) return count
            paths = next.groupBy { it.dev }.map { (dev, counts) -> Path(dev, counts.sumOf { it.count }) }
        }
    }

    fun one(input: List<String>): Int = count(parse(input), "you", "out").toInt()

    fun two(input: List<String>): Long = parse(input).let { devs ->
        count(devs, "svr", "dac") * count(devs, "dac", "fft") * count(devs, "fft", "out") +
                count(devs, "svr", "fft") * count(devs, "fft", "dac") * count(devs, "dac", "out")
    }
}

object Day11Test : FunSpec({
    val input = lines("Day11")

    val sample = """
        aaa: you hhh
        you: bbb ccc
        bbb: ddd eee
        ccc: ddd eee fff
        ddd: ggg
        eee: out
        fff: out
        ggg: out
        hhh: ccc fff iii
        iii: out
    """.trimIndent().lines()

    val sample2 = """
        svr: aaa bbb
        aaa: fft
        fft: ccc
        bbb: tty
        tty: ccc
        ccc: ddd eee
        ddd: hub
        hub: fff
        eee: dac
        dac: fff
        fff: ggg hhh
        ggg: out
        hhh: out
    """.trimIndent().lines()

    with(Day11) {
        test("one") {
            one(sample) shouldBe 5
            one(input) shouldBe 719
        }

        test("two") {
            two(sample2) shouldBe 2L
            two(input) shouldBe 337433554149492L
        }
    }
})

/*
This was nice!  Part 1 was very straight forward.  For part 2, I first thought that breaking the paths into
multiple groups would be sufficient, but that did not work.  I still thought I would have to avoid circles,
but I never saw any while my part 2 code was building paths similar to part 1.  I then decided to try coding
with the assumption that there would be no circles, and that worked.  oneA is therefore a better solution than
one for part 1.
 */
