package wapuniverse.util

import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import org.fxmisc.easybind.EasyBind
import org.fxmisc.easybind.EasyBind.listBind
import org.fxmisc.easybind.Subscription
import wapuniverse.view.extensions.subscribe

fun <T> listBind(target: ObservableList<T>, sourceObservable: ObservableValue<ObservableList<T>?>): Subscription {
    var innerSub: Subscription? = null
    val sub = sourceObservable.subscribe { observableList ->
        innerSub?.unsubscribe()
        innerSub = observableList?.let { listBind(target, it) }
    }
    return Subscription {
        sub.unsubscribe()
        innerSub?.unsubscribe()
    }
}

fun <T1, T2, R> combine(
        src1: ObservableValue<T1>,
        src2: ObservableValue<T2>,
        f: (T1, T2) -> R
): ObservableValue<R> {
    return EasyBind.combine(src1, src2, f)
}
