package speech.audio

import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.delay
import kotlin.math.pow

object SignalParser {
    private class RegexUtil<R>(val str: String) {
        fun case(pred: (String) -> Boolean, fn: (String) -> R): R? = if(pred(str)) fn(str) else null
        fun case(regex: Regex, fn: (MatchResult.Destructured) -> R): R? = regex.matchEntire(str)?.destructured?.let(fn)

        fun R?.case(pred: (String) -> Boolean, fn: (String) -> R): R? = this ?: this@RegexUtil.case(pred, fn)
        fun R?.case(regex: Regex, fn: (MatchResult.Destructured) -> R): R? = this ?: this@RegexUtil.case(regex, fn)
    }

    private fun <R> match(line: String, fn: RegexUtil<R>.() -> R) = RegexUtil<R>(line).run(fn)

    fun parseInstruction(line: String): Signal.Recorded? = match(line) {
        val i = RegexOption.IGNORE_CASE

        case(String::isEmpty) {
            null
        }.case("""^wait\s*(\d+)""".toRegex(i)) { (ms) ->
            Signal.Recorded.Wait(ms.toLong())
        }.case("""^end""".toRegex(i)) { _ ->
            Signal.Recorded.End
        }.case("""^(note)?\s*off""".toRegex(i)) { _ ->
            Signal.NoteOff.recorded()
        }.case("""^(note)?\s*on\s*(\d+(?:\.\d+)?)\s*(.*)$""".toRegex(i)) {
            (_, noteNum, meta) ->
            Signal.NoteOn(noteNum.toDouble(), meta).recorded()
        }.case("""^update\s*(\S+)\s*(\d+(?:\.\d+)?)""".toRegex(i)) {
            (propName, value) ->
            Signal.Update(propName, value.toDouble()).recorded()
        }
    }

    suspend fun parseRealtimeInto(lines: Sequence<String>, destination: AudioSynthesizer) {
        loop@for(inst in lines.map(::parseInstruction)) {
            when(inst) {
                null -> continue@loop
                is Signal.Recorded.Wait -> continue@loop
                is Signal.Recorded.End -> break@loop
                is Signal.Recorded.Wrapped -> destination.signalInput.send(inst.signal)
            }
        }
        CompletableDeferred<Unit>().also {
            destination.input.send(AudioInputable.Msg.Terminate(it))
        }.join()
    }

    suspend fun execSignals(lines: Iterable<Signal.Recorded>, destination: SendChannel<Signal>) {
        loop@for(inst in lines) {
            println(inst)
            when(inst) {
                is Signal.Recorded.Wait -> delay(inst.ms)
                is Signal.Recorded.End -> break@loop
                is Signal.Recorded.Wrapped -> destination.send(inst.signal)
            }

        }
    }
}