package wapuniverse.viewmodel

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext

class Initializable {
    var isInitialized = false
        private set

    fun init(function: () -> Unit) {
        check(!isInitialized)
        function()
        isInitialized = true
    }
}

class EditorLoadingContext(
        val filePath: Path
) {

}

typealias ChooseFile = () -> Path?

class EditorContext {

}

class Context {

}

class Contextual {
    fun enter(f: Context) {

    }
}

class EditorWindow {
    fun open() {

    }
}

class RootWindow(
        override val coroutineContext: CoroutineContext
) : CoroutineScope {
    interface View {
        fun chooseWorldFile(): Path?
    }

    private val initializable = Initializable()

    private val context = Contextual()

    private lateinit var view: View

    private val editorLoadingContextProp = SimpleObjectProperty<EditorLoadingContext>()

    val editorLoadingContext = editorLoadingContextProp as ObservableValue<EditorLoadingContext>

    private val editorContextProp = SimpleObjectProperty<EditorContext>()

    val editorContext = editorContextProp as ObservableValue<EditorContext>

    fun init(
            view: View
    ) = initializable.init {
        this.view = view
    }

    fun open() {
        check()
        view.chooseWorldFile()?.let { path ->
            open(path)
        }
    }

    private fun open(path: Path) {
        launch {
            val wwd = async(IO) { loadWwd(path) }
            editorLoadingContextProp.value = EditorLoadingContext(path)
            context.enter(Context())
//            enterContext(EditorLoadingContext(path))
        }
    }

//    private fun enterContext(editorLoadingContext: EditorLoadingContext) {
//
//    }

    fun save() {

    }

    private fun check() {
        check(initializable.isInitialized)
    }
}

fun loadWwd(path: Path) {

}