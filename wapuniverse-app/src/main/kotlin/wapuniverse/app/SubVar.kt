package wapuniverse.app

import org.reactfx.value.Var

fun <T, R> subVar(variable: Var<T>, mapper: (T) -> R, remapper: T.(R) -> T) =
        Var.fromVal(variable.map(mapper)) {
            variable.value = remapper(variable.value, it)
        }!!
