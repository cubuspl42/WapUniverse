package wapuniverse.model

import javafx.collections.ObservableList

interface World {
    val objects: ObservableList<out WapObject>
}
