package gg.uhc.migration.configuration

import com.google.common.base.Preconditions.checkArgument
import com.google.common.collect.BoundType
import com.google.common.collect.Range

private data class RangeBound(val bound: Double, val boundType: BoundType)

private fun Char.readLeftBoundType() = when (this) {
    '[' -> BoundType.CLOSED
    '(' -> BoundType.OPEN
    else -> throw IllegalArgumentException("Invalid bound range type character, expected either [ or (")
}

private fun Char.readRightBoundType() = when (this) {
    ']' -> BoundType.CLOSED
    ')' -> BoundType.OPEN
    else -> throw IllegalArgumentException("Invalid bound range type character, expected either ] or )")
}

private fun String.readBound(left: Boolean): RangeBound? {
    checkArgument(this.length > 1, "Must be at least 2 characters long")

    // check for open lower bound
    if (left && this.equals("(-Inf", ignoreCase = true))
        return null

    // check for open higher bound
    if (!left && this.equals("+Inf)", ignoreCase = true))
        return null

    // grab the bound type character and text number
    val type: BoundType
    val textRange: String
    if (left) {
        type = this.first().readLeftBoundType()
        textRange = this.drop(1)
    } else {
        type = this.last().readRightBoundType()
        textRange = this.dropLast(1)
    }

    val range: Double = try {
        textRange.toDouble()
    } catch (e: NumberFormatException) {
        throw IllegalArgumentException("Invalid range number: $textRange", e)
    }

    return RangeBound(range, type)
}

private fun String.readLeftBound(): RangeBound? = this.readBound(true)
private fun String.readRightBound(): RangeBound? = this.readBound(false)

fun String.readRange(): Range<Double> {
    // notation is formatted in style of [x1..x2)
    // split it into left and right bounds
    val bounds = this
        .split("..")
        .filterNot(String::isEmpty)

    // must be 2 parts
    if (bounds.size != 2)
        throw IllegalArgumentException("Missing single `..` from region definition")

    val left = bounds[0].readLeftBound()
    val right = bounds[1].readRightBound()

    return when {
        left == null && right == null -> Range.all<Double>()
        left == null -> Range.upTo(right!!.bound, right.boundType)
        right == null -> Range.downTo(left.bound, left.boundType)
        else -> Range.range(left.bound, left.boundType, right.bound, right.boundType)
    }
}