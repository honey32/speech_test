package speechtest.consonant

import speech.audio.AudioContext
import speech.audio.util.MilSec

interface Consonant {
    val ctx: AudioContext
    val adsr: ConsonantADSR
    operator fun invoke(t: MilSec, freqBase: Double): Double

    class NoConsonant(override val ctx: AudioContext): Consonant {
        override operator fun invoke(t: MilSec, freqBase: Double): Double = 0.0

        override val adsr: ConsonantADSR = ConsonantADSR.NoConsonant
    }
}