package wapuniverse.editor

import javafx.collections.ObservableMap
import org.reactfx.value.Var
import wapuniverse.editor.util.Disposable

class EditObjectContext(
        private val wapObject: WapObject
) : Disposable() {
    private val exportedAttrs = wapObject.exportAttrs()

    fun commit() {
        check(!isDisposed)
        dispose()
    }

    fun abort() {
        check(!isDisposed)
        wapObject.importAttrs(exportedAttrs)
        dispose()
    }

    private fun <T> editAttr(attrs: ObservableMap<WapObjectAttrKey<T>, T>, key: WapObjectAttrKey<T>) =
            checked(this, attrs.varAt(key))

    fun editAttr(key: WapObjectIntAttrKey) = editAttr(wapObject.intAttrs, key)

    fun editAttr(key: WapObjectStringAttrKey) = editAttr(wapObject.strAttrs, key)
}

private fun <K, V> ObservableMap<K, V>.varAt(key: K): Var<V> {
    return Var.fromVal(valAt(key)) { this[key] = it}
}

fun <T> checked(disposable: Disposable, variable: Var<T>): Var<T> {
    return variable // TODO bind()?...
}
