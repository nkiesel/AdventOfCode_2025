import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object Day04 {
    private fun CharArea.liftable(p: Point) = neighbors8(p, '@').size < 4

    fun one(input: List<String>): Int {
        val area = CharArea(input)
        return area.tiles { it == '@' }.count { area.liftable(it) }
    }

    fun two(input: List<String>): Int {
        val area = CharArea(input)
        var removed = 0
        while (true) {
            val removable = area.tiles { it == '@' }.filter { area.liftable(it) }.toList()
            if (removable.isEmpty()) return removed
            removed += removable.size
            removable.forEach { area[it] = '.' }
        }
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
