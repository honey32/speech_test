package speech.audio.util

import kotlin.math.sin

typealias MilSec = Double

fun sinFromFreq(milSec: MilSec, freq: Double): Double {
    val phase = milSec / 1000.0 * freq * 2 * Math.PI
    return sin(phase)
}
