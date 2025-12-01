import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.math.abs

object Day01 {
    private val dir = mapOf('L' to -1, 'R' to 1)
    private fun parse(input: List<String>) = input.map { dir[it[0]]!! * it.substring(1).toInt() }

    fun one(input: List<String>): Int {
        var dial = 50
        var zeros = 0
        for (n in parse(input)) {
            dial = (dial + n) % 100
            if (dial == 0) zeros++
        }
        return zeros
    }

    fun two(input: List<String>): Int {
        var dial = 50
        var zeros = 0
        for (n in parse(input)) {
            zeros += abs(n) / 100
            dial += n % 100
            if (dial == 0 || dial >= 100 || dial < 0 && dial != n % 100) zeros++
            dial = (dial + 100) % 100
        }
        return zeros
    }
}

object Day01Test : FunSpec({
    val input = lines("Day01")

    val sample = """
        L68
        L30
        R48
        L5
        R60
        L55
        L1
        L99
        R14
        L82
    """.trimIndent().lines()

    with(Day01) {
        test("one") {
            one(sample) shouldBe 3
            one(input) shouldBe 1172
        }

        test("two") {
            two(sample) shouldBe 6
            two(input) shouldBe 6932
        }
    }
})

/*
Part 1 was of course very simple as expected, but part 2 again took a few minutes more than expected. The issue was that
even if the resulting dial number is negative (i.e. a left turn of more than the current dial number), it only crossed
0 if it did not start at 0. I first implemented then to turn the dial for "num" steps into the direction, and every time
check if the number is 0:
  repeat(abs(n)) {
      dial = (dial + n.sign + 100) % 100
      if (dial == 0) zeros++
  }
 But then I thought that this would be too expensive for an input like "R9999999".
 */
