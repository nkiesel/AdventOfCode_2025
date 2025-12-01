import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.math.abs

object Day01 {
    private val dir = mapOf('L' to -1, 'R' to 1)
    private fun parse(input: List<String>) = input.map { dir[it[0]]!! * it.substring(1).toInt() }

    fun one(input: List<String>): Int {
        var num = 50
        var zeros = 0
        for (n in parse(input)) {
            num = (num + n) % 100
            if (num == 0) zeros++
        }
        return zeros
    }

    fun two(input: List<String>): Int {
        var num = 50
        var zeros = 0
        for (n in parse(input)) {
            zeros += abs(n) / 100
            num += n % 100
            if (num == 0 || num >= 100 || num < 0 && num != n % 100) {
                zeros++
            }
            num = (num + 100) % 100
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
