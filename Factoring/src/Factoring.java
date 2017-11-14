import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.Arrays;
import java.io.PrintWriter;
import java.io.File;

public class Factoring {
	private int[] primes;
	private BigInteger N;
	private int[][] binMat;
	private int eq,limit;

	public Factoring(BigInteger N, int limit){
		this.N = N;
		primes = new int[limit];
		eq = limit+2;
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
		//ProcessBuilder pb = new ProcessBuilder("./GaussBin")
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
				factors[primeIndex] = (factors[primeIndex]+1)%2;
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
	}

}
