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
        System.out.println("<program name> <input file name (String)> <output file name (String)>");
    }

    private static void validateParams(String[] args) {
        if (args.length != 2) {
            printHelp();
        } else if (!new File(args[0]).exists()) {
            System.out.println("Sorry, input file is not found.");
        } else {
            new TaskProcess(args[0], args[1]);
        }
    }
}
