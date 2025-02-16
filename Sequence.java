import java.io.*;
import java.util.Random;

public class Sequence
{
	public static void main(String[] args)
	{
		long startTime = System.currentTimeMillis();
        String pathToInput = "wiki_dump.xml";
        String pathToOutput = "sequence_output.txt";
		int linesPerDoc[] = {5000, 10000, 15000, 20000};
		try
		{
			// Initializing a BufferedWriter and BufferedReader
			BufferedWriter output_buffer = new BufferedWriter(new FileWriter(pathToOutput));
			BufferedReader input_buffer = new BufferedReader(new FileReader(pathToInput));
            Random r = new Random();
            int nlines = 0;
            int id = 0;

            while(input_buffer.ready()) {
				if(nlines == 0){ 
					nlines = linesPerDoc[r.nextInt(4)];
					System.out.println("New doc with id : " + id + "lines: " + nlines);
					id += 1;
				}
                nlines -= 1;
                String line = input_buffer.readLine();
                output_buffer.write(line + " docId=" + id + "\n");
            }

			// Closing Buffer
			output_buffer.close();
            input_buffer.close();
			System.out.println("Written successfully");
		}
		catch (IOException except)
		{
			except.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Time taken: " + (endTime - startTime) + "ms");
	}
}
