package speech.audio.util

import kotlin.math.pow

interface NoteNumToFreq {
    operator fun invoke(noteNum: Double): Double

    object EqualTemperament: NoteNumToFreq {
        override operator fun invoke(noteNum: Double): Double = 440.0 * 2.0.pow((noteNum - 69) / 12.0)
    }
}