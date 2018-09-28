package wapuniverse.model

class PencilToolContext(
        private val plane: Plane
) : ToolContext() {
    init {
    }

    override fun uninit() {
    }
}
