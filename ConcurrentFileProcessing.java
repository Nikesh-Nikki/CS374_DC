import java.io.*;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class FileProcessor implements Runnable {
    private String inputFile;
    private long startByte;
    private long endByte;
    private BufferedWriter writer; 
    private static final Object lock = new Object();
    private Random random = new Random();
    private static int linesPerDoc[] = {5000, 10000, 15000, 20000};
    private int id = 0;
    private int nthreads;

    public FileProcessor(String inputFile, long startByte, long endByte, BufferedWriter writer, int startId, int nthreads) {
        this.inputFile = inputFile;
        this.startByte = startByte;
        this.endByte = endByte;
        this.writer = writer;
        this.id = startId;
        this.nthreads = nthreads;
    }

    @Override
    public void run() {
        try (RandomAccessFile raf = new RandomAccessFile(inputFile, "r")) {
            raf.seek(startByte); // Move to start position
            // convert raf to BufferedReader
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(raf.getFD())));
            if (startByte != 0) { 
                br.readLine(); // Skip a possibly partial first line
            }
            int linesLeft = 0;
            long charCount = startByte;
            String line;
            while ((line = br.readLine()) != null) {
                charCount += line.length();
                if (linesLeft == 0) {
                    linesLeft = linesPerDoc[random.nextInt(4)];
                    id += nthreads;
                    System.out.println("New doc with id : " + id + " lines: " + linesLeft);
                }
                
                synchronized (lock) { // Synchronize writing to prevent corruption
                    writer.write(line + "docId=" + id + "\n");
                }
                linesLeft--;
                if (charCount > endByte) break; // Stop if we exceed assigned range
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class ConcurrentFileProcessing {
    public static void main(String[] args) throws IOException {

        int nthreads = Integer.parseInt(args[0]);

        long startTime = System.currentTimeMillis();
        String inputFile = "wiki_dump.xml";
        String outputFile = "threaded_output.txt";

        // Get file size and split into two halves
        File file = new File(inputFile);
        long fileSize = file.length();
        long midPoint = fileSize / 2;
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        ExecutorService executor = Executors.newFixedThreadPool(2);
        // Create a shared BufferedWriter
        try {
            
            for(int i = 1; i <= nthreads; i++) {
                executor.execute(new FileProcessor(inputFile, fileSize * (i - 1) / nthreads,
                                                                 fileSize* i / nthreads, writer, -i, nthreads));
            }
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
                    System.err.println("Threads didn't finish in time!");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Now it's safe to close the writer
            writer.close();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
    }
}
