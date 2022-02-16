package ui;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import model.Event;
import model.EventLog;

import javax.imageio.ImageIO;

public class Main {

    public static void main(String[] args) throws IOException {
        // Code to play the console-based version of the game
//        try {
//            new CodenamesConsole();
//        } catch (FileNotFoundException e) {
//            System.out.println("Unable to run application: file not found");
//        }

        new CodenamesGUI();
    }
}

