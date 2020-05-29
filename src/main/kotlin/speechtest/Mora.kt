package speechtest

import speechtest.consonant.Consonant
import speechtest.formant.Vowel

data class Mora(val freq: Double, val consonant: Consonant, val vowel: Vowel)