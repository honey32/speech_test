package speech.audio

class GainControl(override val ctx: AudioContext) : AudioFilter() {
    override var accepts: Boolean = true
    var gain: Double = 1.0

    override fun onInstruction(inst: Signal) {
        when(inst) {
            is Signal.Update ->
                gain = inst.value
            else -> {

            }
        }

    }

    override suspend fun onDataPassed(msg: AudioInputable.Msg) {
        when(msg) {
            is AudioInputable.Msg.Data -> {
                msg.channels.forEach { it ->
                    for(i in 0 until it.size) {
                        it[i] = it[i] * gain
                    }
                }
                msg.channels.sendToOutput()
            }
            is AudioInputable.Msg.Terminate -> {
                accepts = false
                output.send(AudioInputable.Msg.Terminate(msg.response))
            }
        }
    }
}