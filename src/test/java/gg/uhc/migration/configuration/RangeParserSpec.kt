package gg.uhc.migration.configuration

import com.google.common.collect.BoundType
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.properties.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

private fun Table1<String>.shouldAllFail() = {
    forAll(this) { range ->
        shouldThrow<IllegalArgumentException> {
            range.readRange()
        }
    }
}

private fun simpleTable(vararg ranges: String) = table(
    headers("Range"),
    *ranges.map { row(it) }.toTypedArray()
)

@RunWith(JUnitPlatform::class)
object RangeParserSpec : Spek({
    describe("range parsing") {
        it ("parses infinite bounds") {
            val spec = table(
                headers("Range",        "Has lower bound",  "Has upper bound"),
                row(    "(-Inf..+Inf)", false,              false),
                row(    "(-Inf..0)",    false,              true),
                row(    "(0..+Inf)",    true,               false)
            )

            forAll(spec) { range, expectLower, expectUpper ->
                range.readRange().apply {
                    hasLowerBound() shouldBe expectLower
                    hasUpperBound() shouldBe expectUpper
                }
            }
        }

        it("parses bound types") {
            val spec = table(
                headers("Range",    "Lower Type",       "Upper Type"),
                row(    "[0..10]",  BoundType.CLOSED,   BoundType.CLOSED),
                row(    "[0..10)",  BoundType.CLOSED,   BoundType.OPEN),
                row(    "(0..10)",  BoundType.OPEN,     BoundType.OPEN)
            )

            forAll(spec) { range, expectLower, expectUpper ->
                range.readRange().apply {
                    lowerBoundType() shouldBe expectLower
                    upperBoundType() shouldBe  expectUpper
                }
            }
        }

        it("fails on invalid/missing ..") {
            table(
                headers("Range"),
                row("[0.10]"),
                row("[0..10..20]")
            ).shouldAllFail()
        }

        it("parsed numerical bounds") {
            val spec = table(
                headers("Range",            "Left", "Right"),
                row(    "[-10..10]",        -10.0,    10.0),
                row(    "[9.20..1010.2)",   9.20,   1010.2)
            )

            forAll(spec) { range, expectLower, expectUpper ->
                range.readRange().apply {
                    lowerEndpoint() shouldBe expectLower
                    upperEndpoint() shouldBe expectUpper
                }
            }
        }

        it("fails on incorrect bounds for singleton") {
            simpleTable("(0..0)", "[0..0)", "(0..0]").shouldAllFail()
        }

        it("fails on incorrect bounds for unbounded ends") {
            simpleTable("[-Inf..0)", "(0..+Inf]", "[-Inf..+Inf]").shouldAllFail()
        }

        it("fails on incorrect Inf type") {
            simpleTable("(+Inf..0)", "(0..-Inf)", "(+Inf..-Inf)").shouldAllFail()
        }

        it("fails on incorrect numbers") {
            simpleTable("(0..X)", "(Y..0)", "(Z..G)").shouldAllFail()
        }

        it("fails on invalid bounds characters") {
            simpleTable("/0..10]", "[0..10/", "s0..10f").shouldAllFail()
        }

        it("fails on crossed bounds") {
            simpleTable("(10..0)", "(5..-5)", "(0..-10)").shouldAllFail()
        }
    }
})