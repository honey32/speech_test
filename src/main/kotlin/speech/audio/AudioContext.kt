package speech.audio

import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.delay
import speech.audio.util.MilSec
import kotlin.math.max
import kotlin.system.measureTimeMillis

class AudioContext(val sampleRate: Int, val bitDepth: Int, val deltaMs: Int) {
    fun AudioFilter.connectTo(output: SendChannel<AudioInputable.Msg>) {
        this.output = output
    }

    val deltaSamples = milSecToSamples(deltaMs.toDouble())

    fun samplesToMilSec (i: Int): MilSec = (i * (1000.0 / sampleRate))
    fun milSecToSamples (d: MilSec): Int = (d / (1000.0 / sampleRate)).toInt()

    suspend fun AudioSynthesizer.performClock() {
        while(accepts) {
            val time = measureTimeMillis {
                input.send(AudioInputable.Msg.Silence)
            }

            delay(max(0, ctx.deltaMs - time))
        }
    }
}
