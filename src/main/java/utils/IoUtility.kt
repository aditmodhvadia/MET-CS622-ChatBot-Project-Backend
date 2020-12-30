package utils

import listeners.FileListener
import java.io.*
import java.util.*

object IoUtility {
    /**
     * Use to read/get contents of the given file.
     *
     * @param inputFile given file
     * @return contents of file from given file path
     */
    private fun readFile(inputFile: File): String {
        val fileTextStringBuilder = StringBuilder() // String builder which will hold the file content
        try {
            val br = BufferedReader(FileReader(inputFile)) // BufferedReader now points to the file
            var readText: String? // holds file content line by line
            while (br.readLine().also { readText = it } != null) { // read file line by line
                fileTextStringBuilder.append(readText).append("\n") // append file contents
            }
        } catch (e: IOException) {
            e.printStackTrace() // IOException occurred
        }
        return fileTextStringBuilder.toString() // return file contents
    }

    /**
     * Append the contents of source file into destination file.
     *
     * @param destinationFile file to append data to
     * @param sourceFile file from which data will be appended
     */
    fun appendToFile(destinationFile: File, sourceFile: File) {
        var fr: FileWriter? = null
        var br: BufferedWriter? = null
        try {
            fr = FileWriter(destinationFile, true)
            br = BufferedWriter(fr)
            br.write(readFile(sourceFile))
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                br?.close()
                fr?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Use to iterate through all the files and directories from the given folder and listen for any
     * zip files or files found.
     *
     * @param folder given folder
     * @param fileListener given File Listener
     */
    fun iterateFilesAndFolder(folder: File, fileListener: FileListener) {
        for (fileEntry in Objects.requireNonNull(folder.listFiles())) {
            when {
                fileEntry.isDirectory -> {
                    println("Found directory: " + fileEntry.path)
                    iterateFilesAndFolder(fileEntry, fileListener)
                }
                fileEntry.name.contains(".zip") -> {
                    println("Zip file found: " + fileEntry.path)
                    fileListener.zipFileFound(fileEntry.path)
                }
                else -> {
                    println(fileEntry.name)
                    fileListener.fileFound(File(fileEntry.path))
                }
            }
        }
    }

    /**
     * Use to create a Directory with the given name.
     *
     * @param dirName given name
     */
    fun createDirectory(dirName: String): Boolean {
        File(dirName).let {
            return if (!it.exists()) {
                it.mkdir()
            } else false
        }
    }

    /**
     * Use to create a file with given name at the given path and return the said file.
     *
     * @param path given path
     * @param fileName given file name
     * @return the said file
     */
    fun createEmptyFile(path: String, fileName: String): File {
        createDirectory(path)
        File(path + fileName).let {
            if (!it.exists()) {
                try {
                    it.createNewFile()
                    return it
                } catch (e: IOException) {
                    println("File not created: " + it.path)
                    e.printStackTrace()
                }
            }
            return it
        }
    }

    /**
     * Use to find the sensor data entries that match with the given query string in the given file.
     *
     * @param file given file
     * @param query given query
     * @return sensor data entries that match with given file
     */
    fun findSearchResultsFromFile(file: File, query: String): String {
        val br: BufferedReader // Buffered reader for faster reads
        val result = StringBuilder() // String builder which will hold the file content
        try {
            br = BufferedReader(FileReader(file)) // BufferedReader now points to the file
            var line: String // holds file content line by line
            while (br.readLine().also { line = it } != null) { // read file line by line
                if (line.contains(query)) {
                    result.append(line).append("\n") // append file contents
                }
            }
        } catch (e: IOException) {
            e.printStackTrace() // IOException occurred
        }
        return result.toString()
    }

    /**
     * Use to read/get contents of the given file line by line in a List.
     *
     * @param inputFile given file
     * @return contents of file from given file path line by line
     */
    @JvmStatic
    fun getFileContentsLineByLine(inputFile: File): List<String> {
        val br: BufferedReader // Buffered reader for faster reads
        val fileContents: MutableList<String> = mutableListOf() // List which will hold the file contents line by line
        try {
            br = BufferedReader(FileReader(inputFile)) // BufferedReader now points to the file
            var readText: String // holds file content line by line
            while (br.readLine().also { readText = it } != null) { // read file line by line
                fileContents.add(readText) // add file contents to List
            }
        } catch (e: IOException) {
            e.printStackTrace() // IOException occurred
        }
        return fileContents // return file contents
    }
}