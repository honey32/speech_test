package speech.audio

sealed class Signal {
    data class NoteOn(val noteNum: Double, val meta: String?): Signal()
    object NoteOff : Signal()
    data class Update(val paramName: String, val value: Double): Signal()


    fun recorded() = Recorded.Wrapped(this)

    sealed class Recorded {
        data class Wrapped(val signal: speech.audio.Signal): Recorded()
        data class Wait(val ms: Long): Recorded()
        object End: Recorded()
    }
}