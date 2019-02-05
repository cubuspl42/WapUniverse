package wapuniverse.editor.util

class IntMatrix(
        override val rowCount: Int,
        override val columnCount: Int,
        init: () -> Int
) : MutableMatrix<Int> {
    private val array = IntArray(rowCount * columnCount) { init() }

    override fun set(i: Int, j: Int, value: Int) {
        array[index(i, j)] = value
    }

    override fun get(i: Int, j: Int) = array[index(i, j)]

    private fun index(i: Int, j: Int) = i * columnCount + j
}
