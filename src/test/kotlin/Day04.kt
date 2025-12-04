import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object Day04 {
    private fun CharArea.removable(p: Point) = neighbors8(p, '@').size < 4

    fun one(input: List<String>): Int {
        val area = CharArea(input)
        return area.tiles('@').count { area.removable(it) }
    }

    fun two(input: List<String>): Int {
        val area = CharArea(input)
        var removed = 0
        do {
            val removable = area.tiles('@').filter { area.removable(it) }.toList()
            removable.forEach { area[it] = '.' }
            removed += removable.size
        } while (removable.isNotEmpty())
        return removed
    }
}

object Day04Test : FunSpec({
    val input = lines("Day04")

    val sample = """
        ..@@.@@@@.
        @@@.@.@.@@
        @@@@@.@.@@
        @.@@@@..@.
        @@.@@@@.@@
        .@@@@@@@.@
        .@.@.@.@@@
        @.@@@.@@@@
        .@@@@@@@@.
        @.@.@@@.@.
    """.trimIndent().lines()

    with(Day04) {
        test("one") {
            one(sample) shouldBe 13
            one(input) shouldBe 1433
        }

        test("two") {
            two(sample) shouldBe 43
            two(input) shouldBe 8616
        }
    }
})

/*
My CharArea made this very easy!  Solved it in under 10 minutes.
 */
