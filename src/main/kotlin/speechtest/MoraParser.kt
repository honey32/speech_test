package speechtest

import speech.audio.AudioContext
import speechtest.consonant.Consonant
import speechtest.consonant.NoiseConsonant
import speechtest.formant.Vowel

object MoraParser {
    fun parseMora(vs: Vowel.VowelSet, symbol: String, lastSymbol: String?, ctx: AudioContext): Pair<Consonant, Vowel>? = vs.run {
        when(symbol) {
            "あ" -> Consonant::NoConsonant to A
            "い" -> Consonant::NoConsonant to I
            "う" -> Consonant::NoConsonant to U
            "え" -> Consonant::NoConsonant to E
            "お" -> Consonant::NoConsonant to O
            "さ" -> NoiseConsonant::S to A
            "し" -> NoiseConsonant::S to I
            "す" -> NoiseConsonant::S to U
            "せ" -> NoiseConsonant::S to E
            "そ" -> NoiseConsonant::S to O
            "っ" -> null
            "ん" -> Consonant::NoConsonant to N
            else -> null
        }
    }?.let { (f, s) -> f(ctx) to s }
}

