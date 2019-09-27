import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Formatter;
import java.util.Scanner;
import java.util.Vector;

public class Main {

    private static boolean validFile = false;       // checks if the file is valid

    public static void main(String[] args) throws IOException {
        String file;                                // source code file
        FileReader fr;                              // FileReader
        Scanner scan = new Scanner(System.in);      // InputStream scanner

        do {
            System.out.print("Enter file name (input.txt): ");  // instructions
            file = scan.nextLine();                             // gets user input for the filename
            fr = getFile(file);                                 // reads file and returns filereader
        } while (!validFile);                                   // loops until a valid file is found

        Lexer lexer = new Lexer();                              // initialize the lexer
        Vector[] tokens = lexer.getTokens(fr);                  // gets a list of tokens from the file
        Vector[] matches = lexer.matchTokens(tokens);           // categorizes the tokens to a lexeme

        File outfile = new File("output.txt");                  // output file name
        Formatter fmt = new Formatter(outfile);                 // text formatter

        for (int i = 0; i < matches[0].size(); i++) {
            fmt.format("%-20s%s%n", matches[0].get(i), matches[1].get(i));          // write formatted text to output file
            System.out.printf("%-20s%s%n", matches[0].get(i), matches[1].get(i));   // prints out the tokens and lexeme
        }
        fmt.close();
    }

    private static FileReader getFile(String file) {
        FileReader fr = null;         // FileReader
        try {
            fr = new FileReader(file);                      // assign the FileReader a file
            validFile = true;                               // true when a file is found
        } catch (FileNotFoundException e) {
            System.out.println("File was not found.");      // error message
        }
        return fr;      // return FileReader
    }
}
