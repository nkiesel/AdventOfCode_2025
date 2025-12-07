import Direction.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object Day07 {
    fun one(input: List<String>): Int {
        val area = CharArea(input)
        val pos = mutableSetOf(area.first('S'))
        var splits = 0
        do {
            val next = mutableSetOf<Point>()
            for (p in pos) {
                val n = p.move(S)
                if (n in area) {
                    if (area[n] == '^') {
                        listOf(n.move(W), n.move(E)).filter { it in area }.forEach { next += it }
                        splits++
                    } else {
                        next += n
                    }
                }
            }
            pos.clear()
            pos.addAll(next)
        } while (pos.isNotEmpty())
        return splits
    }

    fun two(input: List<String>): Long {
        val cols = input[0].length
        var timelines = LongArray(cols)
        timelines[input[0].indexOf('S')] = 1
        for (l in input) {
            if (l.any { it == '^' }) {
                val next = LongArray(cols)
                for (i in next.indices) {
                    if (l[i] != '^') {
                        next[i] = timelines[i]
                        for (j in listOf(i - 1, i + 1)) {
                            if (j in next.indices && l[j] == '^') next[i] += timelines[j]
                        }
                    }
                }
                timelines = next
            }
        }
        return timelines.sum()
    }
}

object Day07Test : FunSpec({
    val input = lines("Day07")

    val sample = """
        .......S.......
        ...............
        .......^.......
        ...............
        ......^.^......
        ...............
        .....^.^.^.....
        ...............
        ....^.^...^....
        ...............
        ...^.^...^.^...
        ...............
        ..^...^.....^..
        ...............
        .^.^.^.^.^...^.
        ...............
    """.trimIndent().lines()

    with(Day07) {
        test("one") {
            one(sample) shouldBe 21
            one(input) shouldBe 1622
        }

        test("two") {
            two(sample) shouldBe 40L
            two(input) shouldBe 10357305916520L
        }
    }
})

/*
Took me some time before I found a solution for part 2.  I first wrote the code to iterate through all possible paths
using a recursive internal function, but while that worked for the sample input, it ran for minutes for the real input
without finishing.  I then thought to somehow cache for the '^' how many paths I found starting from this point so
that I would not have to run through these again.  But my real solution now is to simply iterate over every line once
and compute how many ways the points in that line can be reached: either simply from the line above, or also from left
or right if the line has '^' on these sides.
 */
