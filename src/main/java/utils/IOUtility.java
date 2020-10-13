package utils;

import listeners.FileListener;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IOUtility {
    private static IOUtility instance;

    private IOUtility() {
    }

    public static IOUtility getInstance() {
        if (instance == null) {
            instance = new IOUtility();
        }
        return instance;
    }

    /**
     * Use to read/get contents of the given file
     *
     * @param inputFile given file
     * @return contents of file from given file path
     */
    private String readFile(File inputFile) {
        BufferedReader br;                                              // Buffered reader for faster reads
        StringBuilder fileTextStringBuilder = new StringBuilder();      // String builder which will hold the file content
        try {
            br = new BufferedReader(new FileReader(inputFile));         // BufferedReader now points to the file
            String readText;                                            // holds file content line by line
            while ((readText = br.readLine()) != null) {                // read file line by line
//                System.out.println(readText);
                fileTextStringBuilder.append(readText + "\n");                 // append file contents
            }
        } catch (IOException e) {
            e.printStackTrace();                                        // IOException occurred
        }
        return fileTextStringBuilder.toString();                        // return file contents
    }

    /**
     * Use to read/get contents of the given file line by line in a List
     *
     * @param inputFile given file
     * @return contents of file from given file path line by line
     */
    public static List<String> getFileContentsLineByLine(File inputFile) {
        BufferedReader br;      // Buffered reader for faster reads
        List<String> fileContents = new ArrayList<>();      // List which will hold the file contents line by line
        try {
            br = new BufferedReader(new FileReader(inputFile));         // BufferedReader now points to the file
            String readText;                                            // holds file content line by line
            while ((readText = br.readLine()) != null) {                // read file line by line
                fileContents.add(readText);     // add file contents to List
            }
        } catch (IOException e) {
            e.printStackTrace();                                        // IOException occurred
        }
        return fileContents;                        // return file contents
    }

    public void appendToFile(File destinationFile, File sourceFile) {
        FileWriter fr = null;
        BufferedWriter br = null;
        try {
            fr = new FileWriter(destinationFile, true);
            br = new BufferedWriter(fr);
            br.write(readFile(sourceFile));

        } catch (IOException e) {
            System.out.println(destinationFile.getPath());
            System.out.println(sourceFile.getPath());
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Use to iterate through all the files and directories from the given folder and listen for any zip files or files found
     *
     * @param folder       given folder
     * @param fileListener given File Listener
     */
    public void iterateFilesAndFolder(final File folder, FileListener fileListener) {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                System.out.println("Found directory: " + fileEntry.getPath());
                iterateFilesAndFolder(fileEntry, fileListener);
            } else if (fileEntry.getName().contains(".zip")) {
                System.out.println("Zip file found: " + fileEntry.getPath());
                fileListener.zipFileFound(fileEntry.getPath());
            } else {
                System.out.println(fileEntry.getName());
                fileListener.fileFound(new File(fileEntry.getPath()));
            }
        }
    }

    /**
     * Use to create a Directory with the given name
     *
     * @param dirName given name
     */
    public void createDirectory(String dirName) {
        File dir = new File(dirName);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    /**
     * Use to create a file with given name at the given path and return the said file
     *
     * @param path     given path
     * @param fileName given file name
     * @return the said file
     */
    public File createEmptyFile(String path, String fileName) {
        createDirectory(path);
        File file = new File(path + fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                return file;
            } catch (IOException e) {
                System.out.println("File not created: " + file.getPath());
                e.printStackTrace();
            }
        }
        return file;
    }


    /**
     * Use to find the sensor data entries that match with the given query string in the given file
     *
     * @param file  given file
     * @param query given query
     * @return sensor data entries that match with given file
     */
    public String findSearchResultsFromFile(File file, String query) {
        BufferedReader br;                                              // Buffered reader for faster reads
        StringBuilder result = new StringBuilder();                     // String builder which will hold the file content
        try {
            br = new BufferedReader(new FileReader(file));              // BufferedReader now points to the file
            String line;                                                // holds file content line by line
            while ((line = br.readLine()) != null) {                    // read file line by line
                if (line.contains(query)) {
                    result.append(line).append("\n");                   // append file contents
                }
            }
        } catch (IOException e) {
            e.printStackTrace();                                        // IOException occurred
        }
        return result.toString();
    }

}
