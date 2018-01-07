package wapuniverse.model

import javafx.collections.ObservableSet

interface World {
    val objects: ObservableSet<WapObject>
}
