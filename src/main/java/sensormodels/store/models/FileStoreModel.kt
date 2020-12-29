package sensormodels.store.models

import sensormodels.DatabaseModel
import java.io.File

interface FileStoreModel : DatabaseModel {
    val fileName: String?
    var file: File?
    fun setFormattedDate()
    val startTime: String?
}