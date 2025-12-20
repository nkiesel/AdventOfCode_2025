import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.math.abs
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
            Machine(target, buttons, g[3].ints())
        }
    }

    fun one(input: List<String>): Int {
        fun click(lights: MutableList<Boolean>, m: Machine, b: Int) =
            lights.indices.forEach { i -> lights[i] = lights[i] xor (m.buttons[b][i]) }
        return parse(input).sumOf { machine ->
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
                        if (count >= clicks) break
                    }
                }
            }
            check(clicks != Int.MAX_VALUE) { "Unsolvable machine" }
            clicks
        }
    }

    fun two(input: List<String>): Long = parse(input).sumOf { solveMachine(it) }

    // Solves the system of linear equations using Gaussian elimination to express
    // dependent variables in terms of free variables. Then performs a search
    // over a limited range of values for the free variables to find the
    // non-negative integer solution that minimizes the total number of button presses.
    private fun solveMachine(machine: Machine): Long {
        val numVars = machine.buttons.size

        val equations = machine.joltage.mapIndexed { r, target ->
            val coeffs = machine.buttons.indices.mapNotNull { c ->
                if (machine.buttons[c][r]) c to 1L else null
            }.toMap().toMutableMap()
            target.toLong() to coeffs
        }.toMutableList()

        val defined = mutableMapOf<Int, Pair<Long, Map<Int, Long>>>()

        while (true) {
            var pivotEqIndex = -1
            var pivotVar = -1
            var pivotCoeff = 0L

            // Try to find +/- 1 coefficient
            for (i in equations.indices) {
                val (_, coeffs) = equations[i]
                val v = coeffs.entries.find { abs(it.value) == 1L }
                if (v != null) {
                    pivotEqIndex = i
                    pivotVar = v.key
                    pivotCoeff = v.value
                    break
                }
            }

            if (pivotEqIndex == -1) break

            val (const, coeffs) = equations.removeAt(pivotEqIndex)
            val defConst = const * pivotCoeff
            val defMap = coeffs.filter { it.key != pivotVar }.mapValues { -it.value * pivotCoeff }

            for ((v, pair) in defined) {
                val (dConst, dMap) = pair
                if (pivotVar in dMap) {
                    val c = dMap[pivotVar]!!
                    val newMap = dMap.toMutableMap()
                    newMap.remove(pivotVar)
                    val newConst = dConst + c * defConst
                    for ((dv, dc) in defMap) {
                        newMap[dv] = newMap.getOrDefault(dv, 0L) + c * dc
                    }
                    defined[v] = newConst to newMap
                }
            }

            defined[pivotVar] = defConst to defMap

            for (i in equations.indices) {
                val (eConst, eCoeffs) = equations[i]
                if (pivotVar in eCoeffs) {
                    val c = eCoeffs[pivotVar]!!
                    eCoeffs.remove(pivotVar)
                    val newConst = eConst - c * defConst
                    for ((dv, dc) in defMap) {
                        eCoeffs[dv] = eCoeffs.getOrDefault(dv, 0L) + c * dc
                    }
                    val toRemove = eCoeffs.filter { it.value == 0L }.keys
                    toRemove.forEach { eCoeffs.remove(it) }
                    equations[i] = newConst to eCoeffs
                }
            }
        }

        val allVars = (0 until numVars).toSet()
        val definedVars = defined.keys
        val freeVars = (allVars - definedVars).toList()

        var minTotal: Long? = null

        val range = when {
            freeVars.isEmpty() -> 0L..0L
            freeVars.size == 1 -> 0L..10000L
            freeVars.size == 2 -> 0L..500L
            else -> 0L..50L
        }

        fun search(index: Int, currentFree: MutableMap<Int, Long>) {
            if (index == freeVars.size) {
                for ((const, coeffs) in equations) {
                    val sum = coeffs.entries.sumOf { (v, c) -> currentFree[v]!! * c }
                    if (sum != const) return
                }

                var currentTotal = freeVars.sumOf { currentFree[it]!! }

                for ((dConst, dMap) in defined.values) {
                    val valV = dConst + dMap.entries.sumOf { (v, c) -> currentFree[v]!! * c }
                    if (valV < 0L) return
                    currentTotal += valV
                }

                if (minTotal == null || currentTotal < minTotal!!) {
                    minTotal = currentTotal
                }
                return
            }

            val v = freeVars[index]
            for (valV in range) {
                currentFree[v] = valV
                search(index + 1, currentFree)
            }
        }

        search(0, mutableMapOf())
        return minTotal ?: error("Unsolvable machine")
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
        test("one") {
            one(sample) shouldBe 7
            one(input) shouldBe 538
        }

        test("two") {
            two(sample4) shouldBe 180L
            two(sample) shouldBe 33L
            two(sample3) shouldBe 221L
            two(sample2) shouldBe 78L
            two(input) shouldBe 20298L
        }
    }
})

/*
I could not solve part2 of day 10 on my own. I had a working code that solved the sample and the first 5 machines
from the input, but then that code ran for 15+ hours for the 6th machine without producing an answer.  I therefore
used Gemini to solve this by telling it:
> My Day10.kt seems to create the correct results for part 2, however it runs way too slow. It currently tries to
> find the correct solution for
> `[.##....##.] (0,1,2,4,5,6,7,8) (1,2,3,5,6,7,8,9) (0,2,3,4,5,6,7,8) (0,3,5) (2,3,5,6,7,8,9) (2,3,6,7) (4,5,9) (1,2,3,6,9) (1,4,5,6,7,8) (2,3,4,6,9) (1,2,4,6,7,8,9) {14,26,46,38,43,42,48,40,31,45}`
> for more than 6 hours without a result. So what is the missing optimization?

and its answer was:

> The issue with the previous approach was likely the exponential complexity of the search space, especially when
> dealing with many buttons and large target values. The recursive backtracking or brute-force search over combinations
>  of button presses becomes infeasible.
>
> I've replaced the twoB implementation with a new approach that uses Gaussian elimination (or a simplified form of it
> tailored for integer solutions) to solve the system of linear equations.
> Here's a breakdown of the changes:
> 1. solveMachine function: This function now attempts to solve the system of linear equations defined by the
>  buttons and target joltages.
>   ◦ It represents the problem as a system where each row (target joltage) is a linear combination of button presses.
>   ◦ It performs variable elimination to express some variables (button press counts) in terms of others ("free variables").
>   ◦ It then iterates through possible values for the free variables (within a reasonable range) to find a solution that
>     satisfies all constraints (non-negative button presses) and minimizes the total number of clicks.
>
> This approach should be significantly faster for systems where the number of free variables is small, which is typical
> for these kinds of problems. The search space for the free variables is limited, making it much more efficient than a
> full search.
>
> Please try running the test again. If it's still slow or incorrect, we might need to refine the range for the free
> variables or handle the Diophantine equations more generally (e.g., using Hermite Normal Form), but this substitution
> method is a strong first step for optimization.

The first solution was incorrect and I told Gemini that it answer was too low. Its answer was

> I've increased the search range for the free variables. The previous range of 0..100 was likely too restrictive,
> especially for cases with only 1 or 2 free variables where the solution could be much larger.
> I've adjusted the range logic to be dynamic based on the number of free variables:
>  • 0 free variables: Range is 0..0 (only one check needed).
>  • 1 free variable: Range is 0..10000. This should cover most reasonable cases.
>  • 2 free variables: Range is 0..500.
>  • 3+ free variables: Range is 0..50.
> This should allow finding solutions with larger button press counts while keeping the search space manageable for
> cases with more degrees of freedom.
> Let's see if this yields the correct answer. If it's still too low or slow, we might need to implement a more
> sophisticated integer programming solver (like Branch and Bound or Gomory's cut), but hopefully, this heuristic
> adjustment is sufficient for the Advent of Code input constraints.

That version that produced the correct result. I then beautified the created code a bit, but now still have to learn
what this code is actually doing. Therefore, the gold star for part 2 was not really earned by me.
 */
