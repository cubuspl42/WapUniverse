package wapuniverse.editor.util

interface MutableMatrix<T> : Matrix<T> {
    fun set(i: Int, j: Int, value: T)
}
