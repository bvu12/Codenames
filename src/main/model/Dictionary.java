package model;

import java.io.*;
import java.util.*;

// Represents a dictionary of ~400 possible words
// SOURCE: https://boardgamegeek.com/thread/1413932/word-list
public class Dictionary {
    private List<String> fullDictionary;
    private int counter;                 // Return the counter index when prompted
    protected static final int SKIP = 5; // Arbitrarily skip 5 lines when parsing the dictionary
//    private static final String FILE_PATH = ".\\data\\dictionary.txt"; // Dictionary path
    protected static final int NUM_DICTIONARY_WORDS = 400;               // Size of provided dictionary

    private Scanner scanner;

    // MODIFIES: this
    // EFFECTS: initializes the class
    public Dictionary() {
        this.fullDictionary = new ArrayList<>();
        this.counter = 0;
    }

    // MODIFIES: this
    // EFFECTS: read in dictionary.txt and add to dictionary
    public void initializeDictionary() {
        // Read the file and add to fullDictionary
        // SOURCES consulted for help:
        //  1) https://stackoverflow.com/questions/19973543/scanner-keeps-throwing-filenotfound-exception/19973734
        //  2) https://www.java67.com/2012/11/how-to-read-file-in-java-using-scanner-example.html
        //  3) https://stackoverflow.com/questions/593671/remove-end-of-line-characters-from-java-string
//        File dictionary = new File(FILE_PATH);
//
//        try {
//            this.scanner = new Scanner(dictionary).useDelimiter("\n");
//            while (scanner.hasNext()) {
//                fullDictionary.add(scanner.next().replaceAll("\r", ""));
//            }
//            scanner.close();
//
//        } catch (FileNotFoundException e) {
//            System.out.println("File not found!!");
//        }

        // We use this to read information from the packaged JAR file
        try {
            // SOURCE: https://stackoverflow.com/questions/16953897/how-to-read-a-text-file-inside-a-jar
            InputStream inputStream
                    = ClassLoader.getSystemClassLoader().getSystemResourceAsStream("dictionary.txt");
            InputStreamReader streamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader in = new BufferedReader(streamReader);

            for (String line; (line = in.readLine()) != null;) {
                // do something with the line
                fullDictionary.add(line.replaceAll("\r", ""));
            }
        } catch (IOException e) {
            System.out.println("File not found!!");
        }


    }

    // EFFECTS: // Randomize the order of the dictionary
    public void shuffleDictionary() {
        Collections.shuffle(fullDictionary);
    }

    // EFFECTS: returns the dictionary
    public List<String> getDictionary() {
        return fullDictionary;
    }

    // MODIFIES: this
    // EFFECTS: increments the counter by SKIP and returns a dictionary word at the index of counter
    //          return blank if not yet initialized
    public String getNextWord() {
        String returnedWord;
        if (fullDictionary.isEmpty()) {
            returnedWord = "";
        } else {
            counter += SKIP;  // Modify counter so the same word is not returned twice
            if (counter >= NUM_DICTIONARY_WORDS) { // Out of bounds!
                returnedWord = "";
            } else {
                returnedWord = fullDictionary.get(counter);
            }
        }

        return returnedWord;
    }

    // EFFECTS: returns the counter
    public int getCounter() {
        return counter;
    }

    // REQUIRES: given value is > 0
    // MODIFIES: this
    // EFFECTS: sets counter to given value
    public void setCounter(int val) {
        counter = val;
    }

    // EFFECTS: returns the scanner
    public Scanner getScanner() {
        return scanner;
    }

    // EFFECTS: returns true if the dictionary is empty
    public boolean isEmpty() {
        return fullDictionary.isEmpty();
    }

}
