package speechtest.formant

import kotlin.math.pow

typealias Formant = GaussianWindow

class Vowel(val name: String, vararg val formants: Formant) {
    class VowelFreqFilter(val formants: Array<out Formant>, val freqBase: Double): FreqFilter {
        override fun getAmplitude(freq: Double, amp: Double) = freqBase / freq * amp *
                formants.map { it.copy(amplitude = it.amplitude * (freq / freqBase)) }.sumByDouble { it(freq) }
    }

    fun toFreqFilter(freqBase: Double) = VowelFreqFilter(formants, freqBase)

    class VowelSet(freqFactor: Double) {

        val A = Vowel("A",
                Formant(1000.0 * freqFactor, 300.0, 1.0),
                Formant(2000.0 * freqFactor, 300.0, 1.0),
                Formant(3500.0 * freqFactor, 1000.0, 0.4)
        )
        val E = Vowel("E",
                Formant( 700.0 * freqFactor, 500.0, 0.9),
                Formant(2500.0 * freqFactor, 500.0, 0.8),
                Formant(3500.0 * freqFactor, 1000.0, 0.4)
        )
        val I = Vowel("I",
                Formant( 400.0 * freqFactor, 300.0, 1.0),
                Formant(2800.0 * freqFactor, 300.0, 0.8),
                Formant(2800.0 * freqFactor, 800.0, 0.4)
        )
        val U = Vowel("U",
                Formant(400.0 * freqFactor, 300.0, 1.0),
                Formant(1400.0 * freqFactor, 300.0, 1.0),
                Formant(3000.0 * freqFactor, 800.0, 0.4)
        )
        val O = Vowel("O",
                Formant(700.0 * freqFactor, 300.0, 0.6),
                Formant(1000.0 * freqFactor, 300.0, 0.6),
                Formant(3000.0 * freqFactor, 800.0, 0.4)
        )

        val N = Vowel("N",
                Formant(200.0 * freqFactor, 500.0, 0.8),
                Formant(1500.0 * freqFactor, 1000.0, 0.4)
        )

        val values = setOf(A, I, U, E, O, N)
        fun forName(name: String) = values.find { it.name.toLowerCase() == name.toLowerCase() }
    }
}