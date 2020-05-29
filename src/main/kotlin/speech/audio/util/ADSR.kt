package speech.audio.util

data class ADSR(val attack: Double, val decay: Double, val sustain: Double, val release: Double) {
    operator fun invoke(ms: MilSec): Double =
            when {
                ms < attack -> ms / attack
                ms < attack + decay -> (attack + decay - ms + sustain * (ms - attack)) / decay
                else -> sustain
            }
}