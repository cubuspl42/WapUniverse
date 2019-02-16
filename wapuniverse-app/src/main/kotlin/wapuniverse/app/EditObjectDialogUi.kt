import javafx.geometry.HPos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TitledPane
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import org.reactfx.value.Var
import wapuniverse.app.EditObjectDialog
import wapuniverse.editor.extensions.map
import wapuniverse.util.twoColumnGrid
import wapuniverse.util.vBox
import wapuniverse.editor.WapObjectIntAttrKey as IntKey
import wapuniverse.editor.WapObjectStringAttrKey as StrKey

private typealias Ui = EditObjectDialog

fun editObjectDialogUi(editObjectDialog: EditObjectDialog) =
        editObjectDialog.root()

fun Ui.root() =
        vBox(
                identificationPane(),
                locationPane(),
                attributesPane(),
                buttons(),
                prefWidth = 350.0
        )

fun Ui.attrTextField(attrKey: StrKey) =
        stringTextField(getVar(attrKey))

fun Ui.attrTextField(attrKey: IntKey) =
        intTextField(getVar(attrKey))

fun Ui.identificationPane() = titledPane(
        "Identification",
        twoColumnGrid(
                "ID" to attrTextField(StrKey.ID),
                "Name" to attrTextField(StrKey.NAME),
                "Logic" to attrTextField(StrKey.LOGIC),
                "Image Set" to attrTextField(StrKey.IMAGE_SET),
                "Animation" to attrTextField(StrKey.ANIMATION)
        )
)

fun Ui.locationPane() = titledPane(
        "Location",
        fourColumnGrid(
                Row4(
                        locationTextField("X", IntKey.X),
                        locationTextField("Y", IntKey.Y),
                        locationTextField("Z", IntKey.Z),
                        locationTextField("I", IntKey.I)
                ),
                minWidth = 10.0, prefWidth = 100.0
        )
)

fun Ui.locationTextField(labelText: String, attrKey: IntKey) =
        twoColumnGrid(
                label(labelText) to attrTextField(attrKey),
                column0PercentWidth = 25.0, column1PercentWidth = 75.0
        )


fun Ui.attributesPane() = titledPane(
        "Attributes",
        threeColumnGrid(
                Row3(
                        attributesColumn(
                                "Score" to IntKey.SCORE,
                                "Powerup" to IntKey.POWERUP,
                                "Speed X" to IntKey.SPEED_X,
                                "X Min" to IntKey.X_MIN,
                                "Y Min" to IntKey.X_MAX
                        ),
                        attributesColumn(
                                "Points" to IntKey.POINTS,
                                "Damage" to IntKey.DAMAGE,
                                "Speed Y" to IntKey.SPEED_Y,
                                "X Max" to IntKey.X_MAX,
                                "Y Max" to IntKey.Y_MAX
                        ),
                        attributesColumn(
                                "Smarts" to IntKey.SMARTS,
                                "Health" to IntKey.HEALTH,
                                "Face Dir" to IntKey.FACEDIR,
                                "Direction" to IntKey.DIRECTION,
                                "Speed" to IntKey.SPEED
                        )
                ),
                minWidth = 10.0, prefWidth = 100.0
        )
)

fun Ui.attributesColumn(vararg rows: Pair<String, IntKey>) =
        twoColumnGrid(
                rows.map { (labelText, key) -> label(labelText) to attrTextField(key) },
                column0PercentWidth = 50.0, column1PercentWidth = 50.0
        )

fun Ui.buttons() =
        HBox(
                Button("OK").apply { setOnAction { submit() } },
                Button("Cancel").apply { setOnAction { cancel() } }
        )

private fun twoColumnGrid(vararg rows: Pair<String, Node>) =
        twoColumnGrid(rows.asIterable())

private fun twoColumnGrid(rows: Iterable<Pair<String, Node>>) =
        twoColumnGrid(rows.map { (labelText, node) ->
            label(labelText) to node
        }).apply {
            columnConstraints.addAll(
                    ColumnConstraints().apply {
                        hgrow = Priority.ALWAYS
                        minWidth = 70.0
                        maxWidth = 70.0
                    },
                    ColumnConstraints().apply {
                        hgrow = Priority.ALWAYS
                        minWidth = 128.0
                        maxWidth = Double.POSITIVE_INFINITY
                    }
            )
        }

fun label(labelText: String) =
        Label("$labelText:").apply {
            GridPane.setHalignment(this, HPos.RIGHT)
        }

data class Row4(val column0: Node, val column1: Node, val column2: Node, val column3: Node)

fun fourColumnGrid(vararg rows: Row4, minWidth: Double, prefWidth: Double): GridPane {
    return GridPane().apply {
        columnConstraints.addAll((1..4).map {
            ColumnConstraints(minWidth, prefWidth, Double.POSITIVE_INFINITY).apply {
                percentWidth = 25.0
            }
        })
        rows.forEachIndexed { i, row -> addRow(i, row.column0, row.column1, row.column2, row.column3) }
    }
}

data class Row3(val column0: Node, val column1: Node, val column2: Node)

fun threeColumnGrid(vararg rows: Row3, minWidth: Double, prefWidth: Double): GridPane {
    return GridPane().apply {
        columnConstraints.addAll((1..3).map {
            ColumnConstraints(minWidth, prefWidth, Double.POSITIVE_INFINITY).apply {
                percentWidth = 33.0
            }
        })
        rows.forEachIndexed { i, row -> addRow(i, row.column0, row.column1, row.column2) }
    }
}

fun titledPane(title: String, content: Node) =
        TitledPane(title, content).apply {
            isAnimated = false
        }

fun intTextField(intVar: Var<Int>) =
        TextField().apply {
            textProperty().value = intVar.value.toString()
            intVar.bind(textProperty().map { it.toIntOrNull() })
        }

fun stringTextField(strVar: Var<String>) =
        TextField().apply {
            textProperty().value = strVar.value.toString()
            strVar.bind(textProperty())
        }
