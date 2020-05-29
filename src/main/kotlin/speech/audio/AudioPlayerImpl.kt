package speech.audio

import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class AudioPlayerImpl(override val ctx: AudioContext) : AudioInputable {
    val format = AudioFormat(ctx.sampleRate.toFloat(), ctx.bitDepth, 1, true, true)


    fun writeData(msg: AudioInputable.Msg, line: SourceDataLine) {
        val byteDepth = ctx.bitDepth / 8
        val amplitude = 2 shl ctx.bitDepth - 1
        val channelInBytes = ByteArray(byteDepth * format.channels * ctx.deltaSamples)
        if (msg is AudioInputable.Msg.Data) {
            for(i in 0 until ctx.deltaSamples) {
                for(c in 0 until format.channels) {
                    val value = (amplitude * msg.channels[c][i]).roundToLong()
                    for (sub in 0 until byteDepth) {
                        channelInBytes[sub + byteDepth * (c + format.channels * i)] =
                                (value ushr (8 * byteDepth)).toByte()
                    }
                }
            }
        }

        line.write(channelInBytes, 0, channelInBytes.size)
    }


    override val input: SendChannel<AudioInputable.Msg> = actor(capacity = 16) {
        val info = DataLine.Info(SourceDataLine::class.java, format)
        val line = (AudioSystem.getLine(info) as SourceDataLine)

        line.open()
        line.start()

        for(msg in channel) {

            when(msg) {
                is AudioInputable.Msg.Data -> {
                    writeData(msg, line)
                }
                is AudioInputable.Msg.Silence -> {
                    writeData(msg, line)
                }
                is AudioInputable.Msg.Terminate -> {
                    line.drain()
                    line.close()
                    msg.response.complete(Unit)
                }
            }
        }
    }
}