package utils

import listeners.FileListener
import java.io.*
import java.nio.file.FileSystems
import java.nio.file.Files
import java.util.zip.ZipInputStream

/**
 * This utility extracts files and directories of a standard zip file to a destination directory.
 *
 * @author www.codejava.net Modified by
 * @author Adit Modhvadia
 */
class UnzipUtility {
    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by destDirectory
     * (will be created if does not exists).
     *
     * @param zipFilePath zip file path
     * @param destDirectory destination directory path
     * @throws IOException File may not be created
     */
    @Throws(IOException::class)
    fun unzip(
        zipFilePath: String,
        destDirectory: String,
        fileListener: FileListener?
    ) {
        IoUtility.createDirectory(destDirectory)

        val zipIn = ZipInputStream(FileInputStream(zipFilePath))
        var entry = zipIn.nextEntry
        val fileSystem = FileSystems.getDefault()

        // iterates over entries in the zip file
        while (entry != null) {
            println(entry.name)
            val filePath = destDirectory + FileSystems.getDefault().separator + entry.name
            if (!entry.isDirectory && entry.name.contains(".zip")) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath)
            } else if (entry.isDirectory) {
                // if the entry is a directory, make the directory
                val dir = File(filePath)
                dir.mkdir()
            } else {
                //                InputStream is = file.getInputStream(entry);
                val bis = BufferedInputStream(zipIn)
                val uncompressedFileName = destDirectory + FileSystems.getDefault().separator + entry.name
                val uncompressedFilePath = fileSystem.getPath(uncompressedFileName)
                Files.createFile(uncompressedFilePath)
                val fileOutput = FileOutputStream(uncompressedFileName)
                while (bis.available() > 0) {
                    fileOutput.write(bis.read())
                }
                fileOutput.close()
                fileListener?.fileFound(File(uncompressedFilePath.toString()))
                println("Written :" + entry.name)
            }
            zipIn.closeEntry()
            entry = zipIn.nextEntry
        }
        zipIn.close()
    }

    /**
     * Extracts a zip entry (file entry) using given ZipInputStream at the given filepath.
     *
     * @param zipIn given ZipInputStream
     * @param filePath given filepath
     * @throws IOException standard IOException
     */
    @Throws(IOException::class)
    private fun extractFile(zipIn: ZipInputStream, filePath: String) {
        val bos = BufferedOutputStream(FileOutputStream(filePath))
        val bytesIn = ByteArray(BUFFER_SIZE)
        var read: Int
        while (zipIn.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
        bos.close()
    }

    companion object {
        /** Size of the buffer to read/write data.  */
        private const val BUFFER_SIZE = 4096
    }
}