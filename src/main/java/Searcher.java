import java.io.*;
import java.nio.CharBuffer;
import java.util.Collections;
import java.util.Stack;
import java.util.concurrent.*;

public class Searcher {

    private static int[] pi;
    private static final int BUF_SIZE = 134217728;
    //private static final int BUF_SIZE = 1024;

    public static void search(String data, String folderName, boolean inFile) throws IOException, ExecutionException, InterruptedException {
        pi = getPrefix(data.toCharArray());
        int processors = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(processors);

        File root = new File(folderName);
        Stack<File> files = new Stack<>();
        files.add(root);
        while (!files.isEmpty()) {
            File file = files.pop();
            File[] ch = file.listFiles();
            if (!file.isDirectory()) {
                if (inFile) {
                    FileSearcher fileSearcher = new FileSearcher(file, data);
                    Future<Boolean> result = service.submit(fileSearcher);
                    result.get();
                } else {
                    int l = searchText(data.toCharArray(), file.getName().toCharArray(), file.getName().length());
                    if (l == -1){
                        System.out.println(file.getName());
                    }
                }
            }
            if (ch != null)
                Collections.addAll(files, ch);
        }

        service.shutdown();
    }

    private static class FileSearcher implements Callable<Boolean> {

        File file;
        String text;

        public FileSearcher(File file, String text) {
            this.file = file;
            this.text = text;
        }

        @Override
        public Boolean call() throws Exception {
            FileReader fileReader = new FileReader(file);
            char[] buf = new char[BUF_SIZE];
            char[] S = text.toCharArray();
            int i = fileReader.read(buf, 0, BUF_SIZE);
            while (i > 0) {
                int l = searchText(S, buf, i);
                if (l == -1) {
                    System.out.println(file.getAbsoluteFile().getParent() + File.separator + file.getName());
                    return true;
                }
                CharBuffer charBuffer = CharBuffer.allocate(l + BUF_SIZE + 1);
                charBuffer.put(buf, i - l, l);
                i = fileReader.read(buf, 0, BUF_SIZE);
                charBuffer.put(buf, 0, BUF_SIZE);
                buf = charBuffer.array();
            }
            return true;
        }
    }

    private static int searchText(char[] S, char[] T, int size) {
        int k = 0;
        int l = 0;
        while (k < size){
            if (T[k] == S[l]){
                k++;
                l++;
                if (l == S.length){
                    return -1;
                }
            } else {
                if (l == 0){
                    k++;
                    if (k == size){
                        return 1;
                    }
                } else {
                    l = pi[l - 1];
                }
            }
        }
        return l;
    }

    private static int[] getPrefix(char[] text) {
        int n = text.length;
        int[] pi = new int[n];
        int i = 1;
        int j = 0;
        while (i < n){
            if (text[i] == text[j]){
                pi[i] = j + 1;
                i++;
                j++;
            } else {
                if (j == 0){
                    pi[i] = 0;
                    i++;
                } else {
                    j = pi[j - 1];
                }
            }
        }
        return  pi;
    }
}
