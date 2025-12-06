import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object Day06 {
    fun one(input: List<String>): Long {
        val ops = input.last().filter { it != ' ' }.toList()
        val nums = List(ops.size) { mutableListOf<Long>() }
        for (line in input.dropLast(1)) {
            line.longs().forEachIndexed { i, l -> nums[i] += l }
        }
        return ops.withIndex().sumOf {
            when (it.value) {
                '+' -> nums[it.index].sum()
                '*' -> nums[it.index].times()
                else -> error("unsupported op")
            }
        }
    }

    fun two(input: List<String>): Long {
        val indices = 0..(input.size - 2)
        val nums = mutableListOf<Long>()
        var sum = 0L
        val ops = input.last()
        var x = ops.lastIndex
        while (x >= 0) {
            nums += indices.map { input[it][x] }.joinToString("").trim().toLong()
            val op = ops[x--]
            sum += when (op) {
                '+' -> nums.sum()
                '*' -> nums.times()
                else -> continue
            }
            nums.clear()
            // skip over empty column
            x--
        }
        return sum
    }
}

object Day06Test : FunSpec({
    val input = lines("Day06")

    val sample = """
        123 328  51 64 
         45 64  387 23 
          6 98  215 314
        *   +   *   +  
    """.trimIndent().lines()

    with(Day06) {
        test("one") {
            one(sample) shouldBe 4277556L
            one(input) shouldBe 5361735137219L
        }

        test("two") {
            two(sample) shouldBe 3263827L
            two(input) shouldBe 11744693538946L
        }
    }
})
