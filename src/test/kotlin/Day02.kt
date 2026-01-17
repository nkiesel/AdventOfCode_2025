import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object Day02 {
    private fun parse(input: List<String>) =
        input[0].longs(false).chunked(2).map { (s, e) -> s..e }

    fun one(input: List<String>): Long = three(input, Regex("""(\d+)\1"""))

    fun two(input: List<String>): Long = three(input, Regex("""(\d+)\1+"""))

    private fun three(input: List<String>, rx: Regex) =
        parse(input).flatMap { r -> r.filter { rx.matches(it.toString()) } }.distinct().sum()
}

object Day02Test : FunSpec({
    val input = lines("Day02")

    val sample = """
        11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124
    """.trimIndent().lines()

    with(Day02) {
        test("one") {
            one(sample) shouldBe 1227775554L
            one(input) shouldBe 54234399924L
        }

        test("two") {
            two(sample) shouldBe 4174379265L
            two(input) shouldBe 70187097315L
        }
    }
})

/*
Much simpler than Day 1!!! Regular expressions are such a powerful helper!

Update: when reading the description once more, I realized that it said to return the sum of all invalid numbers. So
if the same invalid number would be produced by 2 ranges, should we sum both of them?  I don't think so, so added
a `.distinct()` in my code.  This id not make a difference for my input, but still looks more correct to me.
*/
