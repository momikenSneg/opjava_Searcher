import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) {
        long s = System.currentTimeMillis();
        if (args.length == 0) {
            printHelp();
            return;
        }
        try {
            switch (args[0]) {
                case "--name":
                    if (args.length != 3) {
                        printHelp();
                        break;
                    }
                    Searcher.search(args[1], args[2], false);
                    break;
                case "--data":
                    if (args.length != 3) {
                        printHelp();
                        break;
                    }
                    Searcher.search(args[1], args[2], true);
                    break;
                default:
                    printHelp();
            }
        } catch (IOException | ExecutionException | InterruptedException e){
            e.printStackTrace();
        }
        long e = System.currentTimeMillis();
        System.out.println((double)(e - s)/1000);
    }

    private static void printHelp() {
        System.out.println("Possible arguments: \n\n" +
                "-h : help - prints possible arguments\n" +
                "--name <file name> <folder name>: search for a file in the folder\n" +
                "--data '<text>' <folder|file>: search for substring in file or in files in the folder");
    }
}
