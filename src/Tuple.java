/**
 * 
 */

/**
 * @author ekjot
 *
 */
public class Tuple implements Comparable{

	private static final int INF = 999999999;
	private byte[] tuple;

	Tuple() {
		tuple = new byte[100];
	}

	Tuple(byte[] tuple) {
		this.tuple = new byte[100];

		if (tuple.length == 100) {
			for (byte i = 0; i < 100; i++) {
				this.tuple[i] = tuple[i];
			}

		} else {
			System.out.println("Given tuple length is not 100. Error in tuple class.");
		}
	}

	byte[] getEmpID() {
		byte[] empID = new byte[8];
		for (byte i = 0; i < 8; i++) {
			empID[i] = tuple[i];
		}
		return empID;
	}

	byte[] getLastUpdate() {
		byte[] lastUpdate = new byte[10];
		for (byte i = 0; i < 10; i++) {
			lastUpdate[i] = tuple[i + 8];
		}
		return lastUpdate;
	}

	public String toString() {
		String str = "";
		if (tuple.length == 100) {
			for (byte i = 0; i < 100; i++) {
				str += (char) tuple[i];
			}

		} else {
			System.out.println("Given tuple length is not 100. Error in tuple class.");
		}
		return str;

	}

	// tuple is empty when it has EmpID=00000000
	public boolean isEmpty() {

		for (byte i = 0; i < 8; i++) {

			if (tuple[i] != 0) {
				return false;
			}
		}
		return true;
	}

	public boolean isLessThan(Tuple tuple2, boolean considerLastUpdate) {

		// to check if the first tuple is valid or not.
		if (this.isEmpty()) {
			return false;
		}

		// to check if the second tuple is valid or not.
		if (tuple2.isEmpty()) {
			return true;
		}

		byte[] temp1 = this.getEmpID();
		byte[] temp2 = tuple2.getEmpID();

		// comparison of the two employee IDs
		for (int i = 0; i < 8; i++) {
			// System.out.println(emp1[i] +"?"+ emp2[i]);
			if (temp1[i] > temp2[i])
				return false;
			else if (temp1[i] < temp2[i])
				return true;
		}

		if (considerLastUpdate && this.hasSameEmpId(tuple2)) {
			temp1 = this.getLastUpdate();
			temp2 = tuple2.getLastUpdate();

			// if both are equal,checking for last update
			for (int i = 0; i < 10; i++) {

				if (temp1[i] > temp2[i]) {
					return true;
				} else if (temp1[i] < temp2[i]) {
					return false;
				}

			}
		}
		return false;
	}

	public boolean hasSameEmpId(Tuple tuple2) {
		byte[] temp1 = this.getEmpID();
		byte[] temp2 = tuple2.getEmpID();

		// comparison of the two employee IDs
		for (int i = 0; i < 8; i++) {
			if (temp1[i] != temp2[i])
				return false;
		}
		return true;
	}

	@Override
	public int compareTo(Object Tuple2) {

		
		
		Tuple tuple2=(Tuple)Tuple2;

				//		// to check if the first tuple is valid or not.
//		if (this.isEmpty()) {
//			return INF;
//		}
//
//		// to check if the second tuple is valid or not.
//		if (tuple2.isEmpty()) {
//			return -INF;
//		}

		int temp1 = this.getEmpIDAsNum();
		int temp2 = tuple2.getEmpIDAsNum();

//		System.out.println("comparing:  "+this.toString()+"   with  "+tuple2.toString());
//System.out.println(temp1-temp2+"\n");
		
		
		// comparison of the two employee IDs
		return temp1-temp2;

//		if (considerLastUpdate && this.hasSameEmpId(tuple2)) {
//			temp1 = this.getLastUpdate();
//			temp2 = tuple2.getLastUpdate();
//
//			// if both are equal,checking for last update
//			for (int i = 0; i < 10; i++) {
//
//				if (temp1[i] > temp2[i]) {
//					return true;
//				} else if (temp1[i] < temp2[i]) {
//					return false;
//				}
//
//			}
//		}
	
	}

	private int getEmpIDAsNum() {
		int s=0;
		byte[] empId=this.getEmpID();
		for(byte i=0;i<8;i++) {
			s=((s*10)+(empId[i]-48));
		}
		return s;
	}



	

}
