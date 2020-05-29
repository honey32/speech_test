package speech.audio

import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import speech.audio.util.MilSec

abstract class AudioFilter : AudioInputable {
    lateinit var output: SendChannel<AudioInputable.Msg>

    override val input = actor<AudioInputable.Msg>(capacity = 128) {
        for (msg in channel) filterInputActor.send(msg)
    }
    val signalInput = actor<Signal>(capacity = 128) {
        for (msg in channel) filterInputActor.send(msg)
    }

    private val filterInputActor : SendChannel<Any> = actor(capacity = 512) {
        for(msg in channel) {
            when (msg){
                is AudioInputable.Msg -> onDataPassed(msg)
                is Signal -> onInstruction(msg)
                else -> throw Exception()
            }
        }
    }

    abstract fun onInstruction(inst: Signal)

    abstract suspend fun onDataPassed(msg: AudioInputable.Msg)

    abstract var accepts: Boolean

    suspend fun Array<DoubleArray>.sendToOutput() {
        output.send(AudioInputable.Msg.Data(this))
    }

    suspend fun terminateOutput(response: CompletableDeferred<Unit>): CompletableDeferred<Unit> {
        output.send(AudioInputable.Msg.Terminate(response))
        return response
    }
}