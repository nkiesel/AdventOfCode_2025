import io.kotest.core.spec.style.FunSpec
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
        fun click(lights: MutableList<Boolean>, m: Machine, b: Int) =
            lights.indices.forEach { i -> lights[i] = lights[i] xor (m.buttons[b][i]) }
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


    private fun clickButtons(machineIndex: Int, machine: Machine): Int {
        val buttons = machine.buttons.map { b -> b.map { if (it) 1 else 0 } }.distinct()
        val impossible = mutableSetOf<List<Int>>()

        fun clickButtons2(machineIndex: Int, missing: List<Int>): Int? {

            var curBest = Int.MAX_VALUE
            var calls = 0
            val seen = mutableMapOf<String, Int>()

            fun click(missing: List<Int>, clicks: Int, depth: Int): Int? {
                calls++
                if (clicks >= curBest) return null
                if (missing.any { it < 0 }) return null
                if (missing.all { it == 0 }) {
                    curBest = min(curBest, clicks)
//                    println("Machine $machineIndex curBest=$curBest after $calls calls")
                    return clicks
                }
                val l = missing.joinToString(",")
                val s = seen[l]
                if (s == null || s > clicks) {
                    seen[l] = clicks
                } else {
                    return null
                }
                if (missing in impossible) return null
                val clickableButtons = missing.withIndex().filter { it.value > 0 }
                    .map { m -> m.index to buttons.filter { b -> b[m.index] == 1 && b.indices.all { b[it] <= missing[it] } } }
                if (clickableButtons.isEmpty()) return null
                val (mi, mButtons) = clickableButtons.minBy { it.second.size }
                if (mButtons.size == 1) {
                    val clicksForButton = missing[mi] / mButtons.first()[mi]
                    val u = missing.zip(mButtons.first()).map { it.first - it.second * clicksForButton }
                    return click(u, clicks + clicksForButton, depth + 1)
                }
                val gcd = gcd(missing)
                if (gcd != 1) {
                    val adjusted = missing.map { it / gcd }
                    val c = clickButtons2(machineIndex, adjusted)
                    if (c != null) return clicks + c * gcd
                }
                val b2 = clickableButtons.flatMap { it.second }.distinct()
//                val mButtons2 = clickableButtons.minBy { missing[it.first] }.second
                val nk = b2.map { cb ->
                    val u = missing.zip(cb).map { it.first - it.second }
                    val c = click(u, clicks + 1, depth + 1)
                    if (c == null) impossible += u
                    c
                }
                return nk.filterNotNull().minOrNull()
            }

            return click(missing, 0, 0)
        }

        return clickButtons2(machineIndex, machine.joltage) ?: error("No solution found")
    }

    fun two(input: List<String>): Int {
        val machines = parse(input)
        var clicks = 0
        println()
        for ((mi, machine) in machines.withIndex()) {
            val clickButtons = clickButtons(mi, machine)
            println("$mi clicks=$clickButtons")
            clicks += clickButtons
        }
        return clicks
    }

    fun twoA(input: List<String>): Int {
        val machines = parse(input)

        class State(val missing: IntArray, val clicks: Int)

        fun clickButtons(machine: Machine): Int {
            val buttons = machine.buttons.map { b -> b.map { if (it) 1 else 0 } }.distinct().map { it.toIntArray() }
            val queue = ArrayDeque<State>()
            val num = machine.joltage.size
            queue.add(State(machine.joltage.toIntArray(), 0))
            val seen = linkedSetOf<String>()
            while (queue.isNotEmpty()) {
                val s = queue.removeFirst()
                for (b in buttons) {
                    val n = IntArray(num) { s.missing[it] - b[it] }
                    if (n.all { it == 0 }) {
                        return s.clicks + 1
                    }
                    if (n.none { it < 0 } && seen.add(n.joinToString(","))) {
                        queue.add(State(n, s.clicks + 1))
                    }
                }
                while (seen.size > 1000) {
                    seen.removeFirst()
                }
            }
            error("No solution found")
        }

        var total = 0
        for ((mi, machine) in machines.withIndex()) {
            val clickButtons = clickButtons(machine)
            total += clickButtons
            println("Machine ${mi + 1}: $clickButtons clicks")
        }
        return total
    }

    fun counts(sum: Int, parts: Int): Sequence<List<Int>> = sequence {
        if (parts <= 0) {
            if (sum == 0) yield(emptyList())
            return@sequence
        }
        if (parts == 1) {
            yield(listOf(sum))
            return@sequence
        }

        for (i in 0..sum) {
            for (rest in counts(sum - i, parts - 1)) {
                yield(listOf(i) + rest)
            }
        }
    }

    fun twoB(input: List<String>): Int {
        val machines = parse(input)

        class State(val missing: IntArray, val clicks: Int)

        fun clickButtons(missing: IntArray, buttons: List<IntArray>): Int? {
            if (missing.all { it == 0 }) return 0
            if (missing.any { it < 0 }) return null
            val possibleButtons =
                missing.withIndex().filter { it.value > 0 }.map { it.index to buttons.filter { b -> b[it.index] == 1 } }
                    .sortedBy { it.second.size }
            for ((mi, mButtons) in possibleButtons) {
                val count = missing[mi]
                if (mButtons.size == 1) {
                    val u = missing.zip(mButtons.first()).map { it.first - it.second * count }.toIntArray()
                    if (u.all { it >= 0 }) {
                        val clickButtons = clickButtons(u, buttons)
                        if (clickButtons != null) return clickButtons + count
                    }
                    return null
                }
                if (mButtons.size <= 15) {
                    return buildSet {
                        counts(count, mButtons.size).forEach { l ->
                            val u = missing.mapIndexed { index, m ->
                                m - mButtons.withIndex().sumOf { it.value[index] * l[it.index] }
                            }.toIntArray()
                            if (u.all { it >= 0 }) {
                                val clickButtons = clickButtons(u, buttons)
                                if (clickButtons != null) add(clickButtons + count)
                            }
                        }
                    }.minOrNull()
                }
            }

            val queue = ArrayDeque<State>()
            val num = missing.size
            queue.add(State(missing, 0))
            val seen = linkedSetOf<String>()
            while (queue.isNotEmpty()) {
                val s = queue.removeFirst()
                for (b in buttons) {
                    val n = IntArray(num) { s.missing[it] - b[it] }
                    if (n.all { it == 0 }) {
                        return s.clicks + 1
                    }
                    if (n.all { it >= 0 } && seen.add(n.joinToString(","))) {
                        queue.add(State(n, s.clicks + 1))
                    }
                }
                while (seen.size > 1000) {
                    seen.removeFirst()
                }
            }
            return null
        }

        var total = 0
        for ((mi, machine) in machines.withIndex()) {
            val buttons = machine.buttons.map { b -> b.map { if (it) 1 else 0 } }.distinct().map { it.toIntArray() }
            val clickButtons =
                clickButtons(machine.joltage.toIntArray(), buttons) ?: error("No solution found for machine $mi")
            total += clickButtons
            println("Machine $mi: $clickButtons clicks")
        }
        println("Total: $total")
        return total
    }

    fun twoC(input: List<String>): Int {
        val machines = parse(input)

        var total = 0

        for ((mi, machine) in machines.withIndex()) {
            val buttons = machine.buttons.map { b -> b.map { if (it) 1 else 0 } }.distinct().map { it.toIntArray() }
            val seen = mutableMapOf<Int, Int?>()

            fun clickButtons(missing: IntArray): Int? {
                if (missing.all { it == 0 }) return 0
                if (missing.any { it < 0 }) return null
                val key = missing.joinToString(",").hashCode()
                if (seen.containsKey(key)) return seen[key]
                val (mi, mButtons) =
                    missing.withIndex().filter { it.value > 0 }
                        .map { it.index to buttons.filter { b -> b[it.index] == 1 } }.minBy { it.second.size }
                val count = missing[mi]
                return buildSet {
                    counts(count, mButtons.size).forEach { l ->
                        val u = missing.mapIndexed { index, m ->
                            m - mButtons.withIndex().sumOf { it.value[index] * l[it.index] }
                        }.toIntArray()
                        val clickButtons = if (u.all { it >= 0 }) clickButtons(u) else null
                        seen[u.joinToString(",").hashCode()] = clickButtons
                        if (clickButtons != null) add(clickButtons + count)
                    }
                }.minOrNull()
            }

            val clickButtons = clickButtons(machine.joltage.toIntArray()) ?: error("Machine $mi: Failed")
            total += clickButtons
            println("Machine $mi: $clickButtons clicks")
        }

        println("Total: $total")
        return total
    }

}

object Day10Test : FunSpec({
    val input = lines("Day10")

    val sample = """
        [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
        [...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
        [.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}
    """.trimIndent().lines()
    val sample2 = """
        [..#.##] (0,1,3,4) (0,3,4) (0,5) (0,1,2) (3,5) (0,2,3,4) (2,3) {58,27,37,57,37,24}
    """.trimIndent().lines()
    val sample3 = """
        [.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {200,221,221,100,200,100}
    """.trimIndent().lines()
    val sample4 = """
        [####] (0,1,2,3) (0,1,3) {180,180,10,180}
    """.trimIndent().lines()


    with(Day10) {
//        test("one") {
//            one(sample) shouldBe 7
//            println("-".repeat(50))
//            val r = one(input)
//            r shouldBe 538
//        }

        test("two") {
//            twoC(sample4) shouldBe 180
//            twoC(sample) shouldBe 33
//            two(sample) shouldBe 33
//            twoA(sample) shouldBe 33
//            twoC(sample3) shouldBe 221
//            twoC(sample2) shouldBe 78
//            twoA(sample2) shouldBe 0
//            twoA(sample3) shouldBe 221
//            twoA(sample2) shouldBe 33
//            two(input) shouldBe 0
//            twoA(input) shouldBe 0
            twoC(input) shouldBe 0
        }
    }
})
