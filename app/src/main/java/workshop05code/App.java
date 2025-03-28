package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            System.out.println("Wordle created and connected.");
            logger.info("Database connected and Wordle game initialized.");
        } else {
            System.out.println("Not able to connect. Sorry!");
            logger.severe("Database connection failed.");
            return;
        }

        if (wordleDatabaseConnection.createWordleTables()) {
            System.out.println("Wordle structures in place.");
            logger.info("Wordle structures successfully created.");
        } else {
            System.out.println("Not able to launch. Sorry!");
            logger.severe("Failed to create Wordle structures.");
            return;
        }

        // Let's add some words to valid 4 letter words from the data.txt file
        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                wordleDatabaseConnection.addValidWord(i, line);
                logger.info("Added word to database: " + line);
                i++;
            }
        } catch (IOException e) {
            System.out.println("Not able to load. Sorry!");
            System.out.println(e.getMessage());
            logger.severe("Failed to load words from file: " + e.getMessage());
            return;
        }

        // Let's get them to enter a word
        try (Scanner scanner = new Scanner(System.in)) {
            String guess;
            do {
                System.out.print("Enter a 4 letter word for a guess or q to quit: ");
                guess = scanner.nextLine();

                if (guess.equals("q")) {
                    System.out.println("Exiting the game.");
                    logger.info("User exited the game.");
                    break;
                }

                System.out.println("You've guessed '" + guess + "'.");

                if (wordleDatabaseConnection.isValidWord(guess)) {
                    System.out.println("Success! It is in the list.\n");
                    logger.info("Correct guess: " + guess);
                } else {
                    System.out.println("Sorry. This word is NOT in the list.\n");
                    logger.warning("Incorrect guess: " + guess);
                }
            } while (!guess.equals("q"));
        } catch (NoSuchElementException | IllegalStateException e) {
            e.printStackTrace();
            logger.severe("Error occurred while reading user input: " + e.getMessage());
        }
    }
}