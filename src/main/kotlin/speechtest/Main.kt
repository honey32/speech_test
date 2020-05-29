package speechtest

import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import speech.audio.Signal
import speech.audio.AudioContext
import speech.audio.AudioPlayerImpl
import speech.audio.GainControl
import speech.audio.SignalParser

fun main(args: Array<String>) = runBlocking{

    val ctx = AudioContext(
            sampleRate = 44100,
            bitDepth = 16,
            deltaMs = 100)
    ctx.apply {
        val output = AudioPlayerImpl(ctx)
        val gain = GainControl(ctx)
        gain.connectTo(output.input)

        val synth = VoiceSynthImpl(ctx)
        synth.connectTo(output.input)

        launch {
            synth.performClock()
        }

        gain.signalInput.send(Signal.Update("gain", 0.1))

        launch {
            System.`in`.reader().useLines {
                SignalParser.parseRealtimeInto(it, synth)
            }
//            SignalParser.execSignals(
//                    listOf(
//                            Signal.NoteOn(360.0, "A").recorded(),
//                            Signal.Recorded.Wait(100),
//                            Signal.NoteOn(365.0, "I").recorded(),
//                            Signal.Recorded.Wait(100),
//                            Signal.NoteOn(366.0, "E").recorded(),
//                            Signal.Recorded.Wait(1000),
//                            Signal.Recorded.End
//                    ),
//                    synth.signalInput)
        }.join()
    }

    launch{}.join()
}