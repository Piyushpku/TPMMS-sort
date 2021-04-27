import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
//C:\Users\ekjot\git\TPMMS\TPMMS\Data\Data2.txt
/**
 * @author ekjot
 *
 */
public class Main {

	static Memory memory;

	static byte tupleSize = 100;
	static short bufferSize = 4096; // 4kb=4096bytes
	static int totalMemory = 10485760; // 10mb=10485760bytes

	static short tuplesBufferCanHold = (short) (bufferSize / tupleSize);
	static short buffersMemoryCanHold = (short) (totalMemory / bufferSize);
	static int tuplesMemoryCanHold = totalMemory / tupleSize;

	int io = 0;
	// static short sublistNumber;

	/**
	 * @param args
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {

		System.out.println("File 1:");
		String fileAddress1 = getFileAddress();

		System.out.println("\nFile 2:");
		String fileAddress2 = getFileAddress();

		/*
		 * C:\Users\ekjot\git\TPMMS\TPMMS\Data\Data.txt
		 */

		long totalTime = System.nanoTime();

		System.out.println("Phase1 started.\nWorking on your files...");
		short sublistNumber = phaseOneV2(fileAddress1, fileAddress2);
		System.out.println("Phase1 completed.");

		System.out.println("Time to sort :" + (((double) (System.nanoTime() - totalTime) / 1000000000)) + " seconds");

		System.out.println("\nPhase2 started.");
		long numberOfMergedTuples = phaseTwo(sublistNumber);
		System.out.println("Phase2 completed.");

		totalTime = System.nanoTime() - totalTime;

		System.out.println("io=" + (new DataReader()).io);
		System.out.println("\nNumber of Tuples after sorting and merging: " + numberOfMergedTuples);
		System.out.println("Total Time:" + (((double) totalTime / 1000000000)) + " seconds");

	}

	private static short phaseOneV2(String fileAddress1, String fileAddress2) throws IOException {

		short sublistNumber = 0;
		DataReader dr = new DataReader(fileAddress1);
		byte firstByte = dr.readByte();

		short flag = 0;

		boolean file1Complete = false, file2Complete = false;

		do {
			
			ArrayList<Tuple> memory = new ArrayList<Tuple>();

			// fill memory with tuples
			for (int i = 0; i < tuplesMemoryCanHold; i++) {
				// <= in case file1 ends with full memory

				if (firstByte == -1) { // memory may have more space but file ends

					if (!file1Complete) { // if first file ended
						file1Complete = true;

						dr = new DataReader(fileAddress2);
						firstByte = dr.readByte();

						// If the second file is empty
						if (firstByte == -1) {
							file2Complete = true;
							break;
						}
					} else if (!file2Complete) { // if second file ended
						file2Complete = true;
						break;
					}
				}

				Tuple tuple = dr.readTuple(firstByte);

				memory.add(tuple);

				firstByte = dr.readByte();
			}

			// M tuples have been read into memory. now sort into sublist and store
			Buffer sublist = phaseOneSort(memory);
			sublist.writeToFile("" + sublistNumber++ + ".txt", false);

			// exit condition
			if (file2Complete && firstByte == -1)
				break;

		} while (firstByte != -1);
		
		return sublistNumber;
	}

	private static Buffer phaseOneSort(ArrayList<Tuple> memory2) {

		Buffer sublist = new Buffer(tuplesMemoryCanHold);

		Collections.sort(memory2);
		for(Tuple t:memory2) {
			sublist.addTuple(t);
		}
		return sublist;
	
	}

	private static long phaseTwo(short sublistNumber) throws IOException {

		memory = new Memory((short) (sublistNumber + 1));

		for (short i = 0; i < sublistNumber; i++) {
			Buffer buffer = new Buffer(tuplesBufferCanHold);
			buffer.setFileAddress(i);

			buffer.loadBuffer();
			memory.addBuffer(buffer);
		}
		Buffer outputBuffer = new Buffer(tuplesBufferCanHold);
		memory.addBuffer(outputBuffer);

		return memory.phaseTwoSort();

	}

	private static short phaseOne(String fileAddress, String fileAddress2) throws IOException {

		short sublistNumber = 0;
		DataReader dr = new DataReader(fileAddress);
		byte firstByte = dr.readByte();

		short flag = 0;
		do {
			// create new memory
			memory = new Memory(buffersMemoryCanHold);

			// fill memory with M tuples
			while (memory.getNumberOfBuffers() <= buffersMemoryCanHold) {
				// <= in case file1 ends with full memory

				if (flag == 0 && firstByte == -1) { // memory may have more buffer space but file1 ends
					// flag set to 1 when file 1 ends
					flag++;
					dr = new DataReader(fileAddress2);
					firstByte = dr.readByte();
					flag++;
					// If the second file is empty
					if (firstByte == -1)
						break;
				}
				if (memory.getNumberOfBuffers() != buffersMemoryCanHold) {
					// new empty buffer
					Buffer buffer = new Buffer();
					Tuple tuple = dr.readTuple(firstByte);

					buffer.addTuple(tuple);
					memory.addBuffer(buffer);

					firstByte = dr.readByte();

					// if both the files have ended
					if (flag == 2 && firstByte == -1)
						break;

				} else
					break;
			}

			// M tuples have been read into memory. now sort into sublist and store
			Buffer sublist = memory.phaseOneSort();
			sublist.writeToFile("" + sublistNumber++ + ".txt", false);

			// exit condition
			if (flag == 2 && firstByte == -1)
				break;

		} while (firstByte != -1);
		return sublistNumber;
	}

	private static boolean checkFileValidity(String fileAddress) {
		File f = new File(fileAddress);
		return f.exists() && !f.isDirectory();
	}

	private static String getFileAddress() throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Enter File Address: ");
		String address = (new BufferedReader(new InputStreamReader(System.in))).readLine();
		if (!checkFileValidity(address)) {
			System.out.println("Entered File Address is invalid.\nTry again.");
			address = getFileAddress();
		}

		return address;
	}

}
