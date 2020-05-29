package speech

import kotlinx.coroutines.experimental.delay
import speech.audio.AudioFilter
import speech.audio.Signal

class Dsl(val synth: AudioFilter) {
    suspend fun noteOn(type: String, freq: Double) {
        synth.signalInput.send(Signal.NoteOn(freq, type))
    }

    suspend fun silent(time: Int) {
        noteOn("-", 400.0)
        delay(time)
    }

    suspend fun update(paramName: String, value: Double) {
        synth.signalInput.send(Signal.Update(paramName, value))
    }
}