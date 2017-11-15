import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.Arrays;
import java.io.PrintWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.lang.Thread;

public class Factoring {
	private int[] primes,array_r;
	private BigInteger N;
	private int[][] binMat;
	private int eq,limit;

	public Factoring(BigInteger N, int limit){
		this.N = N;
		primes = new int[limit];
		eq = limit+2;
		array_r = new int[eq];
		this.limit = limit;
		binMat = new int[eq][limit];
	}

	public void quadSieve(){
		//generate x_i and y_i
		int k = 1;
		int j = 1;
		int nbrEq = 0;
		try {
			File file = new File("matrix.txt");
			PrintWriter pw = new PrintWriter(file);
			pw.println(eq + " " + limit);
			while (nbrEq < eq){
				int r = squareRoot(N.multiply(BigInteger.valueOf(k))).intValue()+j;
				int r2 = (int)Math.pow(r,2);
				int[] factors = checkIfSmooth(r2%N.intValue());
				if (factors != null){
					boolean same = false;
					for (int i = 0; i < nbrEq; i++){
						if (Arrays.equals(binMat[i], factors)){
							same = true;
							break;
						}
					}
					if (!same){
						for (int i = 0; i < factors.length; i++){
							binMat[nbrEq][i] = factors[i];
							array_r[nbrEq] = r;
							pw.print(factors[i] + " ");
						}
						pw.println("");
						nbrEq++;
					}
				}
				if (k > j){
					j++;
				} else {
					k++;
				}
			}
			pw.flush();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public BigInteger getFactor(){

		try (BufferedReader in = new BufferedReader(new FileReader("out.txt"));){
			String s = in.readLine();
			int nbrSol = Integer.parseInt(s);
			ArrayList<Integer> indices = new ArrayList<Integer>();
			for (int i = 0; i < nbrSol; i++){
				s = in.readLine();
				int lastIndex = -1;
				while (s.indexOf('1',lastIndex+1) != -1){
					lastIndex = s.indexOf('1', lastIndex+1);
					indices.add(lastIndex/2);
				}
				int x = 1;
				int y = 1;
				for (int index : indices){
					x = x * array_r[index];
					for (int j = 0; j < limit; j++){
						int temp = binMat[index][j]*primes[j];
						if (temp != 0){
							y = y*temp;
						}
					}
				}
				System.out.println("x: " + x + " y: " + y);
				y = (int)Math.sqrt(y);
				BigInteger gcd = N.gcd(BigInteger.valueOf(y-x));
				if (gcd.intValue() != 1 && gcd.compareTo(N) != 0){
					return gcd;
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public BigInteger squareRoot(BigInteger x) {
      BigInteger right = x, left = BigInteger.ZERO, mid;
      while(right.subtract(left).compareTo(BigInteger.ONE) > 0) {
            mid = (right.add(left)).shiftRight(1);
            if(mid.multiply(mid).compareTo(x) > 0)
                  right = mid;
            else
                  left = mid;
      }
      return left;
}

	public int[] checkIfSmooth(int nbr){
		int primeIndex = 0;
		int[] factors = new int[primes.length];
		while (primeIndex < primes.length && nbr != 1){
			if (nbr % primes[primeIndex] == 0){
				nbr = nbr/primes[primeIndex];
				factors[primeIndex] = (factors[primeIndex]+1);
			} else {
				primeIndex++;
			}
		}
		if (nbr == 1){
			int sum = 0;
			for (int i = 0; i < factors.length; i++){
				sum += factors[i];
			}
			if (sum == 0)
				return null;
			return factors;
		} else {
			return null;
		}
	}

	private void parsePrimes(){
		try (Scanner scan = new Scanner(new File("primes.txt"))){
			int index = 0;
			while (scan.hasNextInt() && index < primes.length){
				primes[index] = scan.nextInt();
				index++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		BigInteger N = new BigInteger(args[0]);
		int limit = Integer.parseInt(args[1]);
		Factoring fact = new Factoring(N,limit);
		fact.parsePrimes();
		fact.quadSieve();
		// for(int i = 0; i < fact.eq; i++){
		// 	for(int j = 0; j < limit; j++){
		// 		System.out.print(fact.binMat[i][j] + " ");
		// 	}
		// 	System.out.println("");
		// }
		ProcessBuilder pb = new ProcessBuilder("./a.out","matrix.txt","out.txt");
		try{
			final Process p=pb.start();
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		BigInteger factor = fact.getFactor();
		if (factor != null){
			BigInteger factor2 = N.divide(factor);
			System.out.println("The factors of " + N + " are " + factor + " and " + factor2);
		} else {
			System.out.println("No solution");
		}

	}

}
