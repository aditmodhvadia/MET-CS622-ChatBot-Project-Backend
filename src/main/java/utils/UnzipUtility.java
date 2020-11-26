package utils;

import listeners.FileListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This utility extracts files and directories of a standard zip file to a destination directory.
 *
 * @author www.codejava.net Modified by
 * @author Adit Modhvadia
 */
public class UnzipUtility {
  /** Size of the buffer to read/write data. */
  private static final int BUFFER_SIZE = 4096;

  /**
   * Extracts a zip file specified by the zipFilePath to a directory specified by destDirectory
   * (will be created if does not exists).
   *
   * @param zipFilePath zip file path
   * @param destDirectory destination directory path
   * @throws IOException File may not be created
   */
  public void unzip(
      @Nonnull String zipFilePath,
      @Nonnull String destDirectory,
      @Nullable FileListener fileListener)
      throws IOException {
    File destDir = new File(destDirectory); // create destination directory if it does not exist
    if (!destDir.exists()) {
      destDir.mkdir();
    }
    ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
    ZipEntry entry = zipIn.getNextEntry();
    FileSystem fileSystem = FileSystems.getDefault();

    // iterates over entries in the zip file
    while (entry != null) {
      System.out.println(entry.getName());
      String filePath = destDirectory + FileSystems.getDefault().getSeparator() + entry.getName();
      if (!entry.isDirectory() && entry.getName().contains(".zip")) {
        // if the entry is a file, extracts it
        extractFile(zipIn, filePath);
      } else if (entry.isDirectory()) {
        // if the entry is a directory, make the directory
        File dir = new File(filePath);
        dir.mkdir();
      } else {
        //                InputStream is = file.getInputStream(entry);
        BufferedInputStream bis = new BufferedInputStream(zipIn);
        String uncompressedFileName =
            destDirectory + FileSystems.getDefault().getSeparator() + entry.getName();
        Path uncompressedFilePath = fileSystem.getPath(uncompressedFileName);
        Files.createFile(uncompressedFilePath);
        FileOutputStream fileOutput = new FileOutputStream(uncompressedFileName);
        while (bis.available() > 0) {
          fileOutput.write(bis.read());
        }
        fileOutput.close();
        if (fileListener != null) {
          fileListener.fileFound(new File(uncompressedFilePath.toString()));
        }
        System.out.println("Written :" + entry.getName());
      }
      zipIn.closeEntry();
      entry = zipIn.getNextEntry();
    }
    zipIn.close();
  }

  /**
   * Extracts a zip entry (file entry) using given ZipInputStream at the given filepath.
   *
   * @param zipIn given ZipInputStream
   * @param filePath given filepath
   * @throws IOException standard IOException
   */
  private void extractFile(@Nonnull ZipInputStream zipIn, @Nonnull String filePath)
      throws IOException {
    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
    byte[] bytesIn = new byte[BUFFER_SIZE];
    int read = 0;
    while ((read = zipIn.read(bytesIn)) != -1) {
      bos.write(bytesIn, 0, read);
    }
    bos.close();
  }
}
