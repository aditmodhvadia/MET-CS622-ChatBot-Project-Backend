package listeners

import java.io.File

interface FileListener {
    fun fileFound(file: File?)
    fun zipFileFound(path: String?)
}