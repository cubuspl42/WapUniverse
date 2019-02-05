package io.github.jwap32.v1

import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

class FlagProperty(private val self: Any, private val flags: KMutableProperty<Int>, private val bitIndex: Int) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        val f = flags.getter.call(self)
        val m = f and (1 shl bitIndex)
        return m != 0
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        val f = flags.getter.call(self)
        val x = if (value) 1 else 0
        // http://stackoverflow.com/questions/47981/how-do-you-set-clear-and-toggle-a-single-bit-in-c-c
        val nf = f xor ((-x xor f) and (1 shl bitIndex))
        flags.setter.call(self, nf)
    }
}

fun flagProperty(self: Any, flags: KMutableProperty<Int>, bitIndex: Int) = FlagProperty(self, flags, bitIndex)
