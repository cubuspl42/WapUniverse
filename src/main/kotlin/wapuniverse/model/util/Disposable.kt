package wapuniverse.model.util

open class Disposable(
        parent: Disposable? = null
) {
    private val children = mutableListOf<Disposable>()

    var isDisposed = false
        private set

    init {
        parent?.addChild(this)
    }

    fun dispose() {
        check(!isDisposed)
        uninit()
        children.forEach { it.dispose() }
        isDisposed = true
    }

    fun addChild(child: Disposable) {
        check(!isDisposed)
        children.add(child)
    }

    inline fun addDisposeListener(crossinline function: () -> Unit) {
        check(!isDisposed)
        addChild(disposable(function))
    }

    protected open fun uninit() {}
}

inline fun disposable(crossinline function: () -> Unit): Disposable {
    return object : Disposable() {
        override fun uninit() {
            function()
        }
    }
}
