package gg.uhc.migration.collection

import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.util.*

@RunWith(JUnitPlatform::class)
object RandomCollectionSpec : Spek({
    var collection = RandomCollection<Int>()

    beforeEachTest {
        collection = RandomCollection<Int>()
    }

    it("should throw on fetching from empty collection") {
        shouldThrow<IllegalStateException> {
            collection.get()
        }
    }

    it("should increas in size when adding an item") {
        collection.size shouldBe 0

        collection.add(element = 10, weight = 5)

        collection.size shouldBe 1
    }

    context("with a single item") {
        beforeEachTest { collection.add(element = 10, weight = 5) }

        it("should return the expected item with a single element") {
            collection.get() shouldBe 10
        }

        it("should allow removal by element") {
            collection.removeElement(10)

            collection.size shouldBe 0
        }

        it("should allow removal by weightedobject") {
            collection.remove(WeightedEntry(weight = 5, item = 10))

            collection.size shouldBe 0
        }

        it("should disallow duplicate elements") {
            collection.add(element = 10, weight = 5) shouldBe false
            collection.size shouldBe 1
        }
    }

    context("with multiple items") {
        beforeEachTest {
            collection.add(element = 10, weight = 6)
            collection.add(element = 2, weight = 4)

            collection.random = object: Random() {
                private var attempt = 0

                override fun nextInt(max: Int): Int =
                    attempt++ / 10
            }
        }

        it("should select based on weights") {
            val results = (0 until 100)
                .map { collection.get() }
                .groupBy { it }
                .mapValues { it.value.size }

            results[10] shouldBe 60
            results[2] shouldBe 40
        }
    }

    context("with multiple items equal weight") {
        beforeEachTest {
            collection.add(element = 10, weight = 1)
            collection.add(element = 2, weight = 1)

            collection.random = object: Random() {
                private var attempt = 0

                override fun nextInt(max: Int): Int =
                    attempt++ / 50
            }
        }

        it("should select based on weights") {
            val results = (0 until 100)
                .map { collection.get() }
                .groupBy { it }
                .mapValues { it.value.size }

            results[10] shouldBe 50
            results[2] shouldBe 50
        }
    }
})