package speechtest

import speech.audio.AudioContext
import speech.audio.AudioInputable
import speech.audio.AudioSynthesizer
import speech.audio.Signal
import speech.audio.util.ADSR
import speech.audio.util.MilSec
import speech.audio.util.NoteNumToFreq
import speech.audio.util.sinFromFreq
import speechtest.formant.FreqFilter
import speechtest.formant.ResonantFilter
import speechtest.formant.Vowel

class VoiceSynthImpl(override val ctx: AudioContext): AudioSynthesizer() {
    override var accepts: Boolean = true

    private val delta = ctx.deltaMs

    private val vowelSet = Vowel.VowelSet(0.95)
    private val adsr = ADSR(10.0, 10.0, 0.6, 40.0)

    private var timeSpent: MilSec = 0.0
    private var currentMora: Mora? = null

    private val tempelament = NoteNumToFreq.EqualTemperament

    val harmonyLevel = 20


    private fun parseMora(inst: Signal.NoteOn) = inst.meta?.let {
        MoraParser.parseMora(vowelSet, it, null, ctx)
    }?.let { (c, v) ->
        Mora(tempelament(inst.noteNum), c, v)
    }

    override fun onInstruction(inst: Signal) {
        when(inst) {
            is Signal.NoteOff -> {
                currentMora = null
            }
            is Signal.NoteOn -> {
                currentMora = parseMora(inst)
                timeSpent = 0.0
                currentMora?.let {
                    println(it.vowel.name)
                    FreqFilter.apply { it.vowel.testSpectrum(it.freq) }
                }
            }
            is Signal.Update -> {
                currentMora = currentMora?.copy(freq = tempelament(inst.value))
            }
        }
    }

    override suspend fun onDataPassed(msg: AudioInputable.Msg) = when(msg) {
        is AudioInputable.Msg.Terminate -> output.send(AudioInputable.Msg.Terminate(msg.response))
        is AudioInputable.Msg.Data -> msg.channels.sendToOutput()
        is AudioInputable.Msg.Silence -> generate().let { arrayOf(it).sendToOutput() }
    }

    private fun generate(): DoubleArray {
        val (freqBase, consonant, vowel) = currentMora ?: return DoubleArray(ctx.deltaSamples)
        val moeVoice = vowel.toFreqFilter(freqBase) * ResonantFilter.Moe
        return withTimeSpent { time ->
            val amplitudes = moeVoice.getSpectrum(freqBase, harmonyLevel)
            DoubleArray(ctx.deltaSamples) { i ->
                val t: MilSec = time + ctx.samplesToMilSec(i)
                val tSuppress = consonant.adsr.vowelSuppressionDuration

                if(t > tSuppress) {
                    adsr(t - tSuppress) * generateFromAmpSpectrum(amplitudes, freqBase, t)
                } else {
                    consonant.invoke(t, freqBase)
                }
            }
        }
    }

    private inline fun <R>withTimeSpent(fn: (MilSec) -> R): R {
        val time = timeSpent
        timeSpent += delta
        return fn(time)
    }

    private fun generateFromAmpSpectrum(ampSpectrum: DoubleArray, freqBase: Double, tMs: MilSec) = (1 until harmonyLevel).sumByDouble { hIdx ->
        val freq = hIdx * (freqBase + 0.004 * sinFromFreq(tMs, 10.0))
        sinFromFreq(tMs, freq) * (ampSpectrum[hIdx])
    }
}