package wapuniverse.editor

import wapuniverse.editor.util.Disposable

class EditObjectContext(
        val wapObject: WapObject
) : Disposable() {
    private val exportedAttrs = wapObject.exportAttrs()

    fun <T> setAttr(attrKey: WapObjectAttrKey<T>, value: T) {
        check(!isDisposed)
        wapObject.setAttr(attrKey, value)
    }

    fun commit() {
        check(!isDisposed)
        dispose()
    }

    fun abort() {
        check(!isDisposed)
        wapObject.importAttrs(exportedAttrs)
        dispose()
    }
}
