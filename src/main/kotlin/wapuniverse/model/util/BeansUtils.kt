package wapuniverse.model.util

import wapuniverse.model.Dialog
import wapuniverse.util.optionalProperty

fun <TDialog : Dialog?> dialogProperty() =
        optionalProperty<TDialog?>().apply {
            addListener { _, _, dialog ->
                dialog?.addCloseListener { clear() }
            }
        }
