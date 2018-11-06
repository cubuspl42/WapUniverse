package wapuniverse.model.util

class IntArray2D(
        private val rowCount: Int,
        private val columnCount: Int,
        init: (Int) -> Int
) {
    private val array = IntArray(rowCount * columnCount, init)

    fun set(i: Int, j: Int, value: Int) {
        array[index(i, j)] = value
    }

    fun get(i: Int, j: Int) = array[index(i, j)]

    private fun index(i: Int, j: Int) = i * columnCount + j
}
