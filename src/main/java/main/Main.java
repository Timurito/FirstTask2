package main;


import java.io.File;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
        } else {
            validateParams(args);
        }
    }

    private static void printHelp() {
        System.out.println("<program name> <input file name (String)> <output file name (String)> <append to output file (boolean)>");
    }

    private static void validateParams(String[] args) {
        if (args.length != 3) {
            printHelp();
        } else if (!new File(args[0]).exists()) {
            System.out.println("Sorry, input file is not found.");
        } else if (!(args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false"))) {
            System.out.println("Sorry, third parameter should be a boolean value (true or false).");
        } else {
            new TaskProcess(args);
        }
    }
}
