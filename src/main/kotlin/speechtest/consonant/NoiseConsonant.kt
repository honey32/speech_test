package speechtest.consonant

import speech.audio.AudioContext
import speech.audio.util.MilSec
import speech.audio.util.sinFromFreq
import java.util.*
import kotlin.math.*

interface NoiseConsonant: Consonant {
    val noiseData: DoubleArray

    override operator fun invoke(t: MilSec, freqBase: Double): Double =
        if(t < adsr.vowelSuppressionDuration) {
            noiseData[ctx.milSecToSamples(t) % noiseData.size] * adsr.getAmplitude(t)
        } else {
            0.0
        }


    class S(override val ctx: AudioContext): NoiseConsonant {
        override val adsr: ConsonantADSR = ConsonantADSR.S
        override val noiseData: DoubleArray = DoubleArray(ctx.sampleRate) { s ->
            Math.random()
        }.also {
            for(i in 0 until it.size - 1) {
                it[i] = 0.5 * (it[i + 1] - it[i])
            }
        }
    }

    class SH(override val ctx: AudioContext): NoiseConsonant {
        override val noiseData = DoubleArray(ctx.sampleRate) { s ->
            Math.random()
        }.also {
            for(i in 0 until it.size - 1) {
                it[i] = 0.5 * (it[i + 1] - it[i])
            }
        }

        override val adsr: ConsonantADSR = ConsonantADSR.S
    }
}