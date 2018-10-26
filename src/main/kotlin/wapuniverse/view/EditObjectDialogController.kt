package wapuniverse.view

import javafx.beans.property.IntegerProperty
import javafx.beans.property.StringProperty
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.TextField
import wapuniverse.model.EditObjectDialog
import wapuniverse.view.extensions.map
import java.net.URL
import java.util.ResourceBundle

class EditObjectDialogController(
        private val editObjectDialog: EditObjectDialog
) : Initializable {
    @FXML
    lateinit var idTextField: TextField

    @FXML
    lateinit var nameTextField: TextField

    @FXML
    lateinit var logicTextField: TextField

    @FXML
    lateinit var imageSetTextField: TextField

    @FXML
    lateinit var animationTextField: TextField

    @FXML
    lateinit var xTextField: TextField

    @FXML
    lateinit var yTextField: TextField

    @FXML
    lateinit var zTextField: TextField

    @FXML
    lateinit var iTextField: TextField

    @FXML
    lateinit var scoreTextField: TextField

    @FXML
    lateinit var pointsTextField: TextField

    @FXML
    lateinit var smartsTextField: TextField

    @FXML
    lateinit var powerupTextField: TextField

    @FXML
    lateinit var damageTextField: TextField

    @FXML
    lateinit var healthTextField: TextField

    @FXML
    lateinit var speedXTextField: TextField

    @FXML
    lateinit var speedYTextField: TextField

    @FXML
    lateinit var faceDirTextField: TextField

    @FXML
    lateinit var xMinTextField: TextField

    @FXML
    lateinit var xMaxTextField: TextField

    @FXML
    lateinit var directionTextField: TextField

    @FXML
    lateinit var yMinTextField: TextField

    @FXML
    lateinit var yMaxTextField: TextField

    @FXML
    lateinit var speedTextField: TextField

    @FXML
    lateinit var saveButton: Button

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        bind(idTextField, editObjectDialog.id)
        bind(nameTextField, editObjectDialog.name)
        bind(logicTextField, editObjectDialog.logic)
        bind(imageSetTextField, editObjectDialog.imageSet)
        bind(animationTextField, editObjectDialog.animation)
        bind(xTextField, editObjectDialog.x)
        bind(yTextField, editObjectDialog.y)
        bind(zTextField, editObjectDialog.z)
        bind(iTextField, editObjectDialog.i)
        bind(scoreTextField, editObjectDialog.score)
        bind(pointsTextField, editObjectDialog.points)
        bind(smartsTextField, editObjectDialog.smarts)
        bind(powerupTextField, editObjectDialog.powerup)
        bind(damageTextField, editObjectDialog.damage)
        bind(healthTextField, editObjectDialog.health)
        bind(speedXTextField, editObjectDialog.speedX)
        bind(speedYTextField, editObjectDialog.speedY)
        bind(faceDirTextField, editObjectDialog.faceDir)
        bind(xMinTextField, editObjectDialog.xMin)
        bind(xMaxTextField, editObjectDialog.xMax)
        bind(directionTextField, editObjectDialog.direction)
        bind(yMinTextField, editObjectDialog.yMin)
        bind(yMaxTextField, editObjectDialog.yMax)
        bind(speedTextField, editObjectDialog.speed)
        saveButton.setOnAction { editObjectDialog.save() }
    }
}

private fun bind(textField: TextField, intValue: IntegerProperty) {
    textField.textProperty().value = intValue.value.toString()
    intValue.bind(textField.textProperty().map { it.toInt() })
}

private fun bind(textField: TextField, stringValue: StringProperty) {
    textField.textProperty().value = stringValue.value.toString()
    stringValue.bind(textField.textProperty())
}
