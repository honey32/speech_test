package speech.audio

import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.channels.SendChannel
import java.util.*

interface AudioInputable {
    sealed class Msg {
        data class Data(val channels: Array<DoubleArray>) : Msg() {
            override fun equals(other: Any?): Boolean =
                    (this === other) || (other as? Data)?.channels?.contentEquals(channels) ?: false

            override fun hashCode(): Int = Arrays.hashCode(channels)
        }

        object Silence :  Msg()
        data class Terminate(val response: CompletableDeferred<Unit>) : Msg()
    }

    val ctx: AudioContext
    val input: SendChannel<Msg>
}