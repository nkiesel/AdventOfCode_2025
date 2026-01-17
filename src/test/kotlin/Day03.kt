import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object Day03 {
    fun one(input: List<String>) = input.sumOf { l ->
        val f = l.dropLast(1).max()
        val s = l.substring(l.indexOf(f) + 1).max()
        "$f$s".toInt()
    }

    fun two(input: List<String>): Long {
        return input.sumOf { l ->
            val digits = l.toMutableList()
            val num = mutableListOf<Char>()
            while (num.size < 12) {
                val n = ('9' downTo '0').first { d ->
                    digits.indexOf(d).let { it != -1 && it <= digits.size + num.size - 12 }
                }
                num.add(n)
                digits.subList(0, digits.indexOf(n) + 1).clear()
            }

            num.joinToString(separator = "").toLong()
        }
    }

    fun three(input: List<String>, digits: Int): Long {
        return input.sumOf { l ->
            var startIndex = 0
            val endIndex = l.length - digits
            (1..digits).map { i ->
                l.substring(startIndex, endIndex + i).max().also { startIndex = l.indexOf(it, startIndex) + 1 }
            }.joinToString(separator = "").toLong()
        }
    }
}

object Day03Test : FunSpec({
    val input = lines("Day03")

    val sample = """
        987654321111111
        811111111111119
        234234234234278
        818181911112111
    """.trimIndent().lines()

    with(Day03) {
        test("one") {
            one(sample) shouldBe 357
            one(input) shouldBe 17412
            three(sample, 2) shouldBe 357
            three(input, 2) shouldBe 17412
        }

        test("two") {
            two(sample) shouldBe 3121910778619L
            two(input) shouldBe 172681562473501L
            three(sample, 12) shouldBe 3121910778619L
            three(input, 12) shouldBe 172681562473501L
        }
    }
})

/*
Struggled a bit with part 2, but then realized that this could be made more generic and efficient, which resulted
in `three`. I initially did not realize that `"23512".max() == '5'`, but of course Kotlin always allows to interpret
a string as a collection of chars, and therefore no need to use `"23512".toList().max()`.
 */
