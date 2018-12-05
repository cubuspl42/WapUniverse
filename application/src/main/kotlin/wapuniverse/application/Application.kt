package wapuniverse.application

class Application(
        private val presenter: ApplicationPresenter
) {
    private val rootWindows = mutableSetOf<RootWindow>()

    init {
        openNewRootWindow()
    }

    private fun openNewRootWindow() {
        rootWindows.add(RootWindow(presenter))
    }
}
