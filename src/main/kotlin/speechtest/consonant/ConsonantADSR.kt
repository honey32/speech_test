package speechtest.consonant

import speech.audio.util.MilSec

interface ConsonantADSR {
    fun getAmplitude(milSec: MilSec): Double
    val vowelSuppressionDuration: MilSec


    object NoConsonant : ConsonantADSR {
        override fun getAmplitude(milSec: MilSec): Double = 0.0
        override val vowelSuppressionDuration: MilSec = 0.0
    }

    object S : ConsonantADSR {
        val maxAmp = 4.0
        override fun getAmplitude(milSec: MilSec): Double = when {
            milSec < vowelSuppressionDuration + 10 ->
                maxAmp * milSec / (vowelSuppressionDuration + 10)
            else -> 0.0
        }

        override val vowelSuppressionDuration: MilSec = 180.0

    }
}