package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static model.Dictionary.NUM_DICTIONARY_WORDS;
import static model.Dictionary.SKIP;
import static org.junit.jupiter.api.Assertions.*;

public class DictionaryTest {
    Dictionary myDict;
    Dictionary initializedAndShuffled;

    @BeforeEach
    void setup() {
        myDict = new Dictionary();
        initializedAndShuffled = new Dictionary();
        initializedAndShuffled.initializeDictionary();
        initializedAndShuffled.shuffleDictionary();
    }

    @Test
    void testConstructor() {
        assertTrue(myDict.isEmpty());
    }

    @Test
    void testInitializeDictionary() {
        myDict.initializeDictionary();
        assertEquals(NUM_DICTIONARY_WORDS, myDict.getDictionary().size());

        Scanner scanner = myDict.getScanner();
        assertNotNull(scanner);
    }

    @Test
    void testShuffleDictionary() {
        myDict.initializeDictionary();
        String firstWord = myDict.getNextWord();
        assertEquals("AMAZON", firstWord);

        myDict.shuffleDictionary();
        String firstWordAgain = myDict.getNextWord();
        assertNotEquals(firstWord, firstWordAgain);

    }


    @Test
    void testGetCounter() {
        int dictionaryCounter = initializedAndShuffled.getCounter();
        assertEquals(0, dictionaryCounter);
        initializedAndShuffled.getNextWord();
        dictionaryCounter = initializedAndShuffled.getCounter();
        assertEquals(SKIP, dictionaryCounter);
    }

    @Test
    void testSetCounter() {
        int dictionaryCounter = initializedAndShuffled.getCounter();
        assertEquals(0, dictionaryCounter);

        int value = 99;
        initializedAndShuffled.setCounter(value);
        dictionaryCounter = initializedAndShuffled.getCounter();
        assertEquals(value, dictionaryCounter);

    }

    @Test
    void testGetDictionary() {
        List<String> listDict;
        listDict = initializedAndShuffled.getDictionary();

        assertFalse(listDict.isEmpty());
        assertEquals(NUM_DICTIONARY_WORDS, listDict.size());
    }

    @Test
    void testGetNextWordInitialized(){
        String blank;
        String correctWord;
        String nextWord;

        // Not yet Initialized
        assertTrue(myDict.isEmpty());
        blank = myDict.getNextWord();
        assertEquals("",blank);

        // Initialized dictionary - in bounds
        myDict.initializeDictionary();
        myDict.shuffleDictionary();
        assertFalse(myDict.isEmpty());
        for (int i = 1; i < NUM_DICTIONARY_WORDS/SKIP; i++) {
            correctWord = myDict.getDictionary().get(i*SKIP);
            nextWord = myDict.getNextWord();
            assertEquals(correctWord, nextWord);
        }

        // Initialized dictionary - out of bounds
        for (int i = NUM_DICTIONARY_WORDS; i < NUM_DICTIONARY_WORDS + 10; i++) {
            blank = myDict.getNextWord();
            assertEquals("", blank);
        }


    }

    @Test
    void testGetNextWordNotInitialized(){
        String blank = myDict.getNextWord();
        assertEquals("",blank);
    }

    @Test
    void testGetScanner(){
        // Scanner should be closed after creation of the Dictionary
        Scanner scanner1 = myDict.getScanner();
        assertNull(scanner1);

        Scanner scanner2 = initializedAndShuffled.getScanner();
        assertNotNull(scanner2);

    }

    @Test
    void testIsEmpty() {
        assertTrue(myDict.isEmpty());
        assertFalse(initializedAndShuffled.isEmpty());

    }
}
