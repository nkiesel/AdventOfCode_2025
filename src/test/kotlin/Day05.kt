import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.math.max
import kotlin.math.min

object Day05 {
    private fun parse(input: List<String>): Pair<Set<LongRange>, Set<Long>> {
        var range = true
        val ranges = mutableSetOf<LongRange>()
        val ingredients = mutableSetOf<Long>()
        input.forEach { line ->
            if (line.isEmpty()) {
                range = false
            } else if (range) {
                line.split('-').let { ranges += it[0].toLong()..it[1].toLong() }
            } else {
                ingredients += line.toLong()
            }
        }
        return ranges to ingredients
    }

    fun one(input: List<String>): Int {
        val (ranges, ingredients) = parse(input)
        return ingredients.count { ingredient -> ranges.any { ingredient in it } }
    }

    fun two(input: List<String>): Long {
        val ranges = parse(input).first.toMutableSet()
        do {
            var merged = false
            for (r in ranges) {
                val o = ranges.filter { it != r }.find { r.first in it || r.last in it }
                if (o != null) {
                    ranges.remove(r)
                    ranges.remove(o)
                    ranges.add(min(r.first, o.first)..max(r.last, o.last))
                    merged = true
                    break
                }
            }
        } while (merged)
        return ranges.sumOf { it.last - it.first + 1 }
    }
}

object Day05Test : FunSpec({
    val input = lines("Day05")

    val sample = """
        3-5
        10-14
        16-20
        12-18

        1
        5
        8
        11
        17
        32
    """.trimIndent().lines()

    with(Day05) {
        test("one") {
            one(sample) shouldBe 3
            one(input) shouldBe 756
        }

        test("two") {
            two(sample) shouldBe 14L
            two(input) shouldBe 355555479253787L
        }
    }
})

/*
Not too complicated, but I first tried to create a set of all fresh ingredients, but the number was too high. Then I
came up with the "merge overlapping ranges" idea.  However, this still did not produce the correct result. It then
took me a few minutes to understand that 2 identical ranges would not be merged.  I thought to compare indices, but
then decided to simply switch to sets.
 */
