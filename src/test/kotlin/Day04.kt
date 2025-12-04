import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object Day04 {
    private fun parse(input: List<String>) = CharArea(input)

    fun one(input: List<String>): Int {
        val area = parse(input)
        return area.tiles { it == '@' }.count { area.neighbors8(it, '@').size < 4 }
    }

    fun two(input: List<String>): Int {
        val area = parse(input)
        var removed = 0
        while (true) {
            val removable = area.tiles { it == '@' }.filter { area.neighbors8(it, '@').size < 4 }.toList()
            if (removable.isEmpty()) break
            removed += removable.size
            removable.forEach { area[it] = '.' }
        }
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
