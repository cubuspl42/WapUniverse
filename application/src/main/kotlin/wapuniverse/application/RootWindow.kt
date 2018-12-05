package wapuniverse.application

class RootWindow(
        presenter: ApplicationPresenter
) {
    private val realWindow = presenter.createRootWindow()
}
