package speechtest.formant

class ResonantFilter(vararg val windows: GaussianWindow) : FreqFilter {
    override fun getAmplitude(freq: Double, amp: Double): Double =
            amp + windows.sumByDouble { it(freq) }

    companion object {
        val Moe = ResonantFilter(GaussianWindow(5000.0, 1500.0, 0.1))
    }
}