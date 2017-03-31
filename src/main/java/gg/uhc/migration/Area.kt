package gg.uhc.migration

import com.google.common.collect.Range

data class Area(val xRange: Range<Double>, val zRange: Range<Double>, val announce: String, val weight: Int) {
    fun areCoordinatesInside(x: Double, z: Double): Boolean = xRange.contains(x) && zRange.contains(z)
}
