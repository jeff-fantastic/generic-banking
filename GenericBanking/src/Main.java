import com.jefftastic.genericbanking.*;

public class Main {
    public static void main(String[] args) {
        // Declare variables
        AccountManager aM = new AccountManager();
        boolean isRunning = true;

        // Main loop
        while (isRunning) {
            int op = aM.update();
            if (op == -1)
                isRunning = false;
        }

        // Save data to csv file here
        // ...

        // Stop program
        System.exit(0);
    }
}
