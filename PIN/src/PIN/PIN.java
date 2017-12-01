package PIN;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class PIN {
	
	public static void main(String[] args) {
		PIN pin = new PIN();
		pin.run();
	}
	
	public void run() {
		DeBruijn deBruijn2 = new DeBruijn(new int[]{1,0,0,1}, 2, new int[]{1,1,1,1}, new int[]{1,0,0,0});
		DeBruijn deBruijn5 = new DeBruijn(new int[]{2,0,2,4}, 5, new int[]{1,1,1,1}, new int[]{1,0,0,0});
		
		try {
			PrintWriter pw = new PrintWriter(new File("deBruijnSequence.txt"));
			for (int i = 0; i < 10003; i++) {
				int z10 = nextZ10(deBruijn2.nextVal(), deBruijn5.nextVal());
				pw.print(z10);
				System.out.println(z10);
			}
			pw.flush();
			pw.close();
			System.out.println("DONE");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
		
		
//		HashSet<String> hs = new HashSet<String>(625);
//		for (int i = 0; i < 625; i++) {
//			deBruijn5.printstate();
//			System.out.println("Output = " + deBruijn5.nextVal());
//			if (!hs.add(deBruijn5.getstate())) {
//				System.out.println(deBruijn5.getstate());
//			}
//			deBruijn5.nextVal();
//			hs.add(deBruijn5.getstate());
//		}
//		System.out.println("Size of set: " + hs.size());
	}
	
	public int nextZ10(int db2Val, int db5Val) {
		if (db2Val == 1) {
			return db5Val + 5;
		} else {
			return db5Val;
		}
	}
	
	class DeBruijn {
		int[] coeff;
		int mod;
		int[] currentstate;
		int[] prezero;
		boolean iszerostate;
		
		public DeBruijn(int[] coeff, int mod, int[] initialstate, int[] prezero) {
			this.coeff = coeff;
			this.mod = mod;
			this.currentstate = initialstate;
			this.prezero = prezero;
			this.iszerostate = false;
		}
		
		public void printstate() {
			
			for (int i = 0; i < currentstate.length; i++) {
				System.out.print(currentstate[i]);
			}
			System.out.println();
		}
		
		public String getstate() {
			if(iszerostate) {
				return "0000";
			}
			
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < currentstate.length; i++) {
				sb.append(currentstate[i]);
			}
			return sb.toString();
		}
		
		public int nextVal() {
			int output = currentstate[0];
						
			//Account for the zerostate
			boolean isprezerostate = true;
			for (int i = 0; i < currentstate.length; i++) {
				if (currentstate[i] != prezero[i]) {
					isprezerostate = false;
					break;
				}
			}
			
			if (isprezerostate && !iszerostate) {
				iszerostate = true;
				return output;
			}
				
			//Calculate new value
			int newVal = 0;
			for (int i = 0; i < currentstate.length; i++) {
				newVal += currentstate[i]*coeff[i];
//				System.out.println("Addition: " + newVal);
				newVal %= mod;
//				System.out.println("Modulus: " + newVal);
			}
//			System.out.println("newVal = " + newVal);
			
			//Shift all registers
			for (int i = 0; i < currentstate.length-1; i++) {
				currentstate[i] = currentstate[i+1];
			}
			
			//Insert new value at right-most index
			currentstate[currentstate.length-1] = newVal;
			
			
			if (iszerostate) {
				iszerostate = false;
//				System.out.println("AT ZERO STATE");
				return 0;
			}
			
			return output;
		}
	}
}
