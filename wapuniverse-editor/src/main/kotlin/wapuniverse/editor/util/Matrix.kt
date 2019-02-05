package wapuniverse.editor.util

interface Matrix<T> {
    fun get(i: Int, j: Int): T

    fun forEach(function: (Int, Int, T) -> Unit) {
        for (i in 0 until rowCount) {
            for (j in 0 until columnCount) {
                function(i, j, get(i, j))
            }
        }
    }

    val rowCount: Int

    val columnCount: Int
}
