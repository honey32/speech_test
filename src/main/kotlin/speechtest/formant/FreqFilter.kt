package speechtest.formant

import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

interface FreqFilter {
    fun getAmplitude(freq: Double, amp: Double): Double

    fun getNormalizedAmplitude(freq: Double): Double = min(1.0, max(0.0, getAmplitude(freq, 1.0)))

    operator fun times(filter: FreqFilter) = object: FreqFilter {
        override fun getAmplitude(freq: Double, amp: Double): Double =
            this@FreqFilter.getAmplitude(freq, amp).let { new_amp -> filter.getAmplitude(freq, new_amp)}
    }

    fun getSpectrum(freqBase: Double, harmonyLevel: Int) = DoubleArray(harmonyLevel + 1) { i ->
        if (i == 0) 0.0
        else 0.5 * getNormalizedAmplitude(freqBase * i)
    }

    companion object {
        fun Vowel.testSpectrum(baseFreq: Double) {
            this.toFreqFilter(baseFreq).testSpectrum(baseFreq)
        }

        fun FreqFilter.testSpectrum(baseFreq: Double) {
            (20 downTo 1).forEach { hIdx ->
                val freq = hIdx * baseFreq
                val fmt = DecimalFormat("0000")
                val amp = getAmplitude(freq, 1.0)
                val graded = min(10, (amp * 10).roundToInt())
                print(fmt.format(freq))
                print(" : ")
                (0 until graded).forEach{
                    print("@")
                }
                (graded until 10).forEach {
                    print("-")
                }

                println(" $amp")
            }
        }
    }
}