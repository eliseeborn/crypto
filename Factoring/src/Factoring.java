import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Scanner;

public class Factoring {
	private BigInteger[] primes;
	private BigInteger N;
	private int[][] binMat;
	
	public Factoring(BigInteger N, int limit){
		this.N = N;
		primes = new BigInteger[limit];
		binMat = new int[limit+5][limit+5];
	}
	
	private void parsePrimes(){
		try (Scanner scan = new Scanner(new File("primes.txt"))){
			int index = 0;
			while (scan.hasNextBigInteger() && index < primes.length){
				primes[index] = scan.nextBigInteger();
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

	}

}
