package com.jefftastic.genericbanking;

import com.opencsv.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the reading and writing of CSV files
 * based on account information that is passed
 * through during instantiation.
 */
public class Database {
    // Constants
    public static final String DEFAULT_PATH = "." + File.separator + "adb.csv";

    /**
     * Constructs the account list from a CSV file.
     * @param file The filepath to retrieve values from
     * @return The list of accounts constructed from the CSV file
     * @see AccountManager
     */
    public static List<Account> constructAccountList(File file) throws Exception {
        // Get the list of values
        List<String[]> values = readCSV(file);
        List<Account> result = new ArrayList<>();

        // Iterate and construct accounts
        for (String[] value : values) {
            // If the array has too many or too little values, discard
            if (value.length != 3) {
                System.out.printf("""
                        An error occurred during the creation of aDB!
                        Expected 3 values but received %d. Skipping!
                        """, value.length);
                break;
            }
            result.add(new Account(value[0], value[1], Double.parseDouble(value[2])));
        }

        return result;
    }

    /**
     * Reads a CSV file.
     * @param file The filepath to retrieve values from
     * @return A list containing arrays of each line's values.
     */
    public static List<String[]> readCSV(File file) throws Exception {
        // Declare variables
        CSVReader reader = null;
        List<String[]> result = new ArrayList<>();
        String[] line;

        try {
            // Initialize reader and iterate
            reader = new CSVReaderBuilder(new FileReader(file)).withCSVParser(
                    new CSVParserBuilder().withSeparator(';').build()).build();
            while ((line = reader.readNext()) != null)
                result.add(line);

        } catch (FileNotFoundException e) {
            // Print error
            System.out.println("""
                    Could not find file at path "%s".
                    Please verify that a file exists at this path.
                    """);
            e.printStackTrace(System.out);
        } finally {
            // Close reader
            if (reader != null)
                reader.close();
        }

        return result;
    }

    /**
     * Converts an account list into a CSV file.
     * @param aDB List of accounts
     * @see AccountManager
     */
    public static void saveCSV(List<Account> aDB, String path) {
        // Declare variables
        File CSV = new File(path);

        // Iterate through new file
        try (FileWriter fw = new FileWriter(CSV)) {
            for (Account acc : aDB)
                fw.write("%s;%s;%f\n".formatted(
                        acc.getName(), acc.getAddress(), acc.getBalance()
                ));
            fw.flush();
        } catch (IOException e) {
            // Print error
            System.out.println("""
                    Could not translate ADB to CSV file!
                    Something must've gone horribly wrong...
                    """);
            e.printStackTrace(System.out);
        }
    }
}
