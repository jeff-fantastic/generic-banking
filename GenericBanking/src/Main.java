import com.jefftastic.genericbanking.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Declare variables
        AccountManager aM;
        List<Account> accounts = new ArrayList<>();
        boolean isRunning = true;

        // Attempt to load existing data
        try {
            File file = new File(Database.DEFAULT_PATH);
            if (file.exists())
                 accounts = Database.constructAccountList(file);
        } catch (Exception e) { throw new RuntimeException(e); }
        aM = new AccountManager(accounts);

        // Main loop
        while (isRunning) {
            int op = aM.update();
            if (op == -1)
                isRunning = false;
        }

        // Save data to csv file here
        Database.saveCSV(aM.getAccounts(), Database.DEFAULT_PATH);

        // Stop program
        System.exit(0);
    }
}
