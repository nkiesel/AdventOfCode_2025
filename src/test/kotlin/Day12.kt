import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object Day12 {
    data class Region(val x: Int, val y: Int, val q: List<Int>) {
        fun fits(shapes: List<Int>): Boolean {
            val required = q.zip(shapes).sumOf { it.first * it.second }
            if (required > x * y) return false
            if ((x / 3 * 3) * (y / 3 * 3) >= required) return true
            error("region $this unknown")
        }
    }

    private fun parse(input: List<String>): Pair<List<Int>, List<Region>> {
        val shapes = mutableListOf<Int>()
        for (lines in input.chunkedBy { it.isBlank() }) {
            if (lines[0].endsWith(":")) {
                shapes += lines.drop(1).sumOf { l -> l.count { it == '#' } }
            } else {
                return shapes to lines.map { l -> l.ints().let { Region(it[0], it[1], it.drop(2)) } }
            }
        }
        error("parsing")
    }

    fun one(input: List<String>): Int {
        val (shapes, regions) = parse(input)
        return regions.count { r -> r.fits(shapes) }
    }
}

object Day12Test : FunSpec({
    val input = lines("Day12")

    with(Day12) {
        test("one") {
            one(input) shouldBe 557
        }
    }
})

/*
Oh man!  Solution was really simple. I first wrote code that rotated and flipped the present shapes, and then I
started looking at the 5 shapes to see how I could arrange them in a way to reduce the amount of unused places
in a region.  Then I decided to first eliminate all regions that are too small. And then I thought that
if the region is large enough to fit all the presents in whatever rotation, that could be a good answer even
if I did not know how to align the presents.  And that produced the correct result!!!
I then simplified the code to just count the used places for the shapes.  This solution does not work for
the sample, but so be it...
 */
