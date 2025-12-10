import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import kotlin.math.min

object Day10 {
    private data class Machine(val target: List<Boolean>, val buttons: List<List<Boolean>>, val joltage: List<Int>)

    private fun parse(input: List<String>): List<Machine> {
        val r1 = Regex("""\[(.+)+] (.+) \{(.+)\}""")
        return input.map { l ->
            val g = r1.matchEntire(l)!!.groupValues
            val target = g[1].map { it == '#' }
            val buttons = g[2].split(" ").map { it.ints() }.map {
                List(target.size) { i -> it.contains(i) }
            }.toList()
            val joltage = g[3].ints()
            Machine(target, buttons, joltage)
        }
    }

    fun one(input: List<String>): Int {
        val machines = parse(input)
        var sum = 0
        fun click(lights: MutableList<Boolean>, m: Machine, b: Int) = lights.indices.forEach { i -> lights[i] = lights[i] xor(m.buttons[b][i]) }
        for ((mi, machine) in machines.withIndex()) {
            var clicks = Int.MAX_VALUE
            val bi = machine.buttons.indices.toList()
            machine@ for (ps in bi.powerSet().sortedBy { it.size }) {
                if (ps.isEmpty()) continue
                if (ps.size >= clicks) break
                val combined = ps.map { machine.buttons[it] }.reduce { a, b -> a.zip(b).map { it.first or it.second } }
                if (machine.target.zip(combined).any { it.first && !it.second }) continue@machine
                for (p in ps.toList().permutations()) {
                    var count = 0
                    val lights = MutableList(machine.target.size) { false }
                    for (i in p) {
                        click(lights, machine, i)
                        count++
                        if (lights == machine.target) {
                            clicks = min(clicks, count)
                            if (clicks == 1) break@machine
                        }
                        if (count >= clicks) {
                            break
                        }
                    }
                }
            }
            check(clicks != Int.MAX_VALUE) { "No solution found for $mi" }
//            println("Machine ${mi + 1}: $clicks clicks")
            sum += clicks
        }
        return sum
    }

    fun two(input: List<String>): Int {
        val machines = parse(input)
        for ((mi, machine) in machines.withIndex()) {
            val bi = machine.buttons.indices.toList()
            val buttons = machine.buttons.map { b ->
                b.map { if (it) 1 else 0 }
            }
            var clicks = Int.MAX_VALUE
        }
        return 0
    }
}

object Day10Test : FunSpec({
    val input = lines("Day10")

    val sample = """
        [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
        [...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
        [.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}
    """.trimIndent().lines()

    with(Day10) {
        test("one") {
            one(sample) shouldBe 7
            println("-".repeat(50))
            val r = one(input)
            r shouldBe 538
        }

        test("two") {
            two(sample) shouldBe 0
//            two(input) shouldBe 0
        }
    }
})
