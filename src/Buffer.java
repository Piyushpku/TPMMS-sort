import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 */

/**
 * @author ekjot
 *
 */
public class Buffer {
	private int numberOfTuples = 0;
	private int tuplesReadFromFile = 0;
	private int tuplesItCanHold;
	private static DataReader dr;
	private byte pointer = 0;
	private short fileAddress;

	boolean eof = false;

	private Tuple[] buffer;

	public void incPointer() throws IOException {
		Tuple latestTuple = buffer[pointer];
		do {
			pointer++;
			if (pointer == tuplesItCanHold)
				loadBuffer();
		} while (latestTuple.hasSameEmpId(buffer[pointer]));
	}

	Buffer() {
		tuplesItCanHold = 1;// buffer to hold minimum one tuple
		buffer = new Tuple[tuplesItCanHold];
	}

	Buffer(int tuplesMemoryCanHold) {// short because child class Sublist may have 'M' tuples
		this.tuplesItCanHold = tuplesMemoryCanHold;
		buffer = new Tuple[tuplesMemoryCanHold];
	}

	void addTuple(Tuple tuple) {
		if (numberOfTuples < tuplesItCanHold) {
			buffer[numberOfTuples++] = tuple;
		} else {
			System.out.println("This buffer cannot take more tuples. Error in Buffer class.");
		}
	}

	int getNumberOfTuples() {
		return numberOfTuples;
	}

	Tuple getTupleAt(int index) {
		return buffer[index];
	}

	void flush() {
		this.pointer = 0;

		while (numberOfTuples > 0) {
			buffer[--numberOfTuples] = new Tuple();
		}

	}

	Tuple currentTuple() {
		return buffer[pointer];
	}

	void loadBuffer() throws IOException {

		this.flush();
		dr = new DataReader(fileAddress + ".txt");
		byte firstByte = dr.readByte();

		if (firstByte == -1) {
			eof = true;
			// memory may have more buffer space but file ends
		}

		// skip reader to new tuples
		for (int j = 0; j < tuplesReadFromFile; j++) {
			dr.readTuple(firstByte);
			firstByte = dr.readByte();
		}

		// read next tuples
		for (byte i = 0; i < tuplesItCanHold; i++) {

			if (firstByte == -1) {
				eof = true;
				// memory may have more buffer space but file ends
				break;
			}

			addTuple(dr.readTuple(firstByte));
			tuplesReadFromFile++;
			firstByte = dr.readByte();
		}
		dr.delink();
	}

	public void incOutPointer() throws IOException {
		pointer++;

		if (pointer == tuplesItCanHold) {
			//writeToFile("Out.txt", true);
			flush();
		}

	}

	void writeToFile(String fileName, boolean append) throws IOException {
		BufferedWriter br = new BufferedWriter(new FileWriter(fileName, append));
		for (int index = 0; index < getNumberOfTuples(); index++) {
			// never write empty tuple
			if (!this.getTupleAt(index).isEmpty()) {
				br.write(getTupleAt(index).toString() + "\r\n");
			}
		}

		br.close();
	}

	public void setFileAddress(short fileAddress) {
		this.fileAddress = fileAddress;
	}
}
