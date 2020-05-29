package speechtest.formant

import kotlin.math.exp
import kotlin.math.max
import kotlin.math.pow

data class GaussianWindow(val meanFreq: Double, val sd: Double, val amplitude: Double) {
    operator fun invoke(value: Double) =
            max(0.0, exp(-((meanFreq - value) / sd).pow(2.0))) * amplitude
}
