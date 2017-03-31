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
    var collection = RandomWeightedSet<Int>()

    beforeEachTest {
        collection = RandomWeightedSet<Int>()
    }

    context("addition") {
        it("should allow addition of an item") {
            collection.size shouldBe 0

            collection.add(10) shouldBe true

            collection.size shouldBe 1

            collection.add(11) shouldBe true

            collection.size shouldBe 2
        }

        it("should disallow duplicates") {
            collection.add(10) shouldBe true
            collection.add(10) shouldBe false
        }

        it("should work with bulk add") {
            collection.addAll((1..10).toList()) shouldBe true
            collection.size shouldBe 10

            // all should be weight 1
            (1..10).all { collection.getWeight(it) == 1 } shouldBe true
        }

        it("should disallow duplicates in bulk add") {
            collection.add(10)
            collection.addAll((1..10).toList()) shouldBe true
            collection.size shouldBe 10

            // all should be weight 1
            (1..10).all { collection.getWeight(it) == 1 } shouldBe true
        }

        it("should allow bulk add with weights") {
            collection.addAll((1..10).toList(), (1..10).toList()) shouldBe true
            collection.size shouldBe 10

            (1..10).all { collection.getWeight(it) == it } shouldBe true
        }

        it("should disallow duplicates in bulk add with weights") {
            collection.add(10)
            collection.addAll((1..10).toList(), (1..10).toList()) shouldBe true
            collection.size shouldBe 10

            collection.getWeight(10) shouldBe 1
            (1..9).all { collection.getWeight(it) == it } shouldBe true
        }
    }

    context("removal") {
        beforeEachTest {
            collection.addAll((1..10).toList())
        }

        it("should allow clearing") {
            collection.clear()
            collection.size shouldBe 0
        }

        it("should remove by element") {
            collection.remove(10)

            collection.size shouldBe 9
            collection.contains(10) shouldBe false
        }

        it("should allow bulk remove") {
            collection.removeAll((5..10).toList())

            collection.size shouldBe 4
            collection.containsAll((1..4).toList()) shouldBe true
            (5..10).any { collection.contains(it) } shouldBe false
        }
    }

    context("storing weights") {
        beforeEachTest {
            collection.add(element = 5, weight = 15)
            collection.add(element = 10, weight = 5)
        }

        it("should store weights") {
            collection.getWeight(10) shouldBe 5
            collection.getWeight(5) shouldBe 15
        }

        it("should remove weights") {
            collection.remove(10)
            collection.getWeight(10) shouldBe null
        }

        it("should store weights on addAll") {
            collection.addAll(listOf(1, 2), listOf(1, 2)) shouldBe true
            collection.getWeight(1) shouldBe 1
            collection.getWeight(2) shouldBe 2
        }

        it("should return null if not known") {
            collection.getWeight(3) shouldBe null
        }

        it("should reallocate with correct weights on removal") {
            collection.remove(5) shouldBe true
            collection.getWeight(10) shouldBe 5
        }

        it("should reallocate with correct weights on removalAll") {
            collection.removeAll(listOf(15, 5)) shouldBe true
            collection.getWeight(10) shouldBe 5
        }

        it("should reallocate with correct weights on retainAll") {
            collection.retainAll(listOf(10)) shouldBe true
            collection.getWeight(10) shouldBe 5
            collection.getWeight(5) shouldBe null
        }
    }

    context("getting") {
        beforeEachTest {
            collection.add(element = 10, weight = 5)
            collection.add(element = 5, weight = 15)
        }

        it("should throw on fetching from empty collection") {
            collection.clear()

            shouldThrow<IllegalStateException> {
                collection.get()
            }
        }

        it("should return the expected item with a single element") {
            collection.remove(10)
            collection.get() shouldBe 5
        }

        it("should respect contains") {
            collection.contains(10) shouldBe true
            collection.contains(5) shouldBe true
            collection.contains(3) shouldBe false
        }

        it("should respect containsAll") {
            collection.containsAll(listOf(10, 5)) shouldBe true
            collection.containsAll(listOf(10, 5, 4)) shouldBe false
        }

        it("should respect isEmpty") {
            collection.isEmpty() shouldBe false
            collection.clear()
            collection.isEmpty() shouldBe true
        }

        context("with overriden random") {
            beforeEachTest {
                collection.random = object: Random() {
                    private var attempt = 0

                    override fun nextInt(max: Int): Int =
                        attempt++ / 5
                }
            }

            it("should select based on weights") {
                val results = (0 until 100)
                    .map { collection.get() }
                    .groupBy { it }
                    .mapValues { it.value.size }

                results[10] shouldBe 25
                results[5] shouldBe 75
            }
        }
    }

    context("retaining") {
        beforeEachTest {
            collection.add(element = 10, weight = 5)
            collection.add(element = 5, weight = 15)
        }

        it("should honour retainAll") {
            collection.retainAll(listOf(10, 5)) shouldBe false
            collection.retainAll(listOf(10)) shouldBe true
            collection.size shouldBe 1
            collection.contains(5) shouldBe false
            collection.contains(10) shouldBe true
        }
    }
})