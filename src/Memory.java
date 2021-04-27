import java.io.IOException;

/**
 * @author ekjot
 *
 */
public class Memory {
	private Buffer[] memory;
	short buffersItCanHold;
	private short numberOfBuffers;

	Memory(short buffersItCanHold) {
		this.buffersItCanHold = buffersItCanHold;
		numberOfBuffers = 0;
		memory = new Buffer[buffersItCanHold];
	}

	void addBuffer(Buffer buffer) {
		if (numberOfBuffers < buffersItCanHold) {
			memory[numberOfBuffers++] = buffer;
		} else {
			System.out.println("Memory cannot take more buffers. Error in Memory class.");
		}
	}

	short getNumberOfBuffers() {
		return numberOfBuffers;
	}

	Buffer getBufferAt(short index) {
		return memory[index];
	}

	Buffer phaseOneSort() {
		Buffer sublist = new Buffer(buffersItCanHold);

		// simple bubble sort
		for (short i = 0; i < numberOfBuffers; i++) {

			Tuple minTuple = getBufferAt((byte) 0).getTupleAt((byte) 0);
			// because in phase one every buffer has only one tuple

			short minTupleInd = 0;

			for (short j = 1; j < numberOfBuffers; j++) {

				Tuple t = getBufferAt(j).getTupleAt((byte) 0);

				if (!minTuple.isLessThan(t, true)) {

					minTuple = t;
					minTupleInd = j;
				}

			}
			sublist.addTuple(minTuple);
			getBufferAt(minTupleInd).flush();

		}
		return sublist;
	}

	public long phaseTwoSort() throws IOException {
		long out = 0L;
		short j;
		while ((j = checkEOF()) != -1) {

			Tuple minTuple = getBufferAt(j).currentTuple();
			short minTupleIndex = j;
			short outputBuffer = (short) (buffersItCanHold - 1);
			for (short i = (short) (j + 1); i < outputBuffer; i++) {

				Tuple tuple2 = getBufferAt(i).currentTuple();
				if (minTuple.hasSameEmpId(tuple2)) {
					if (!minTuple.isLessThan(tuple2, true)) {
						getBufferAt(minTupleIndex).incPointer();

						minTuple = tuple2;
						minTupleIndex = i;
					} else {
						getBufferAt(i).incPointer();
					}
				} else if (!minTuple.isLessThan(tuple2, true)) {
					minTuple = tuple2;
					minTupleIndex = i;
				}

			}
			getBufferAt(outputBuffer).addTuple(minTuple);
			getBufferAt(minTupleIndex).incPointer();
			getBufferAt(outputBuffer).incOutPointer();
			out++;

		}

		//getBufferAt((short) (buffersItCanHold - 1)).writeToFile("Out.txt", true);
		getBufferAt((short) (buffersItCanHold - 1)).flush();
		return out;
	}

	private short checkEOF() {
		for (short i = 0; i < buffersItCanHold - 1; i++) {
			if (!getBufferAt(i).eof) {
				return i;
			}
		}
		return -1;
	}

}
