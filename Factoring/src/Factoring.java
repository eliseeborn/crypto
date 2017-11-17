import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;

public class Factoring {
	private ArrayList<Integer> factorbase;
	private int relations, factorbaseSize;
	private BigInteger N;
	private int[][] binMat;
	private int[][] exponentList;
	private ArrayList<BigInteger> savedr;

	public static void main(String[] args) {
		Factoring factorer = new Factoring();
		factorer.run(factorer);
	}

	public void run(Factoring f) {
		f.setUp();
		final long startTime = System.currentTimeMillis();
		f.quadSieve();
		ArrayList<boolean[]> gaussResults = f.runGauss();
		BigInteger firstFactor = f.tryAllSolutions(gaussResults);
		if (firstFactor != null) {
			BigInteger secondFactor = N.divide(firstFactor);
			System.out.println("Factors are: " + firstFactor + " and " + secondFactor);
		} else {
			System.out.println("No solution found.");
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("Total execution time: " + (endTime - startTime)/1000 + " seconds");
	}

	private void setUp() {
		System.out.println("Enter N: ");
		Scanner sc = new Scanner(System.in);
		N = new BigInteger(sc.nextLine().trim());
		System.out.println("Enter size of factorbase: ");
		factorbaseSize = sc.nextInt();
		sc.close();
		
		factorbase = new ArrayList<Integer>(factorbaseSize);
		relations = factorbaseSize;
		
		exponentList = new int[relations][factorbaseSize];
		savedr = new ArrayList<BigInteger>(relations);
		binMat = new int[relations][factorbaseSize];
		
		//Read in the correct number primes from primes.txt
		System.out.print("Reading primes from primes.txt...");
		try (Scanner scanner = new Scanner(new File("primes.txt"))) {
			int index = 0;
			while (scanner.hasNextInt() && index < factorbaseSize) {
				factorbase.add(scanner.nextInt());
				index++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println(" DONE");
	}

	private void quadSieve() {
		System.out.print("Generating " + relations + " relations for the matrix...");

		int k = 1;
		int j = 1;
		int nbrRelations = 0;
		while (nbrRelations < relations) {
			for (; j <= k; j++) {
				BigInteger r = squareRoot(N.multiply(BigInteger.valueOf(k))).add(BigInteger.valueOf(j));
				if (checkIfSmooth(r, nbrRelations)) {
					nbrRelations++;
					System.out.println("Relations found: " + nbrRelations + "/" + relations);
				}
				if(!(nbrRelations < relations)){ 
					break;
				}
			}
			k++;
			j = 1;
		}
		System.out.println(" DONE");
	}

	private boolean checkIfSmooth(BigInteger r, int nbrRel) {
		// r2 must not be 0 or 1.
		BigInteger r2 = r.pow(2).mod(N);
		if (r2.equals(BigInteger.ZERO)) {
			return false;
		}
		if (r2.equals(BigInteger.ONE)) {
			return false;
		}

		int[] exponents = new int[factorbaseSize];

		// divide r2 with primes from factorbase, keep track of which factors are used
		// and how many times.
		int primeIndex = 0;
		while (primeIndex < factorbaseSize && !r2.toString().equals(BigInteger.ONE)) {
			if (r2.mod(new BigInteger(String.valueOf(factorbase.get(primeIndex)))).equals(BigInteger.ZERO)) {
				r2 = r2.divide(new BigInteger(String.valueOf(factorbase.get(primeIndex))));
				exponents[primeIndex]++;
			} else {
				primeIndex++;
			}
		}

		// at this point, if r2 != 1, then the factor base was insufficient.
		if (!r2.equals(BigInteger.valueOf(1))) {
			return false;
		}

		// if we get this far, then we know it's B-smooth
		// construct the binary matrix that is to be fed to GaussBin.exe
		int[] binaryexponents = new int[factorbaseSize];
		for (int i = 0; i < factorbaseSize; i++) {
			binaryexponents[i] = exponents[i] % 2;
		}
		// do not add any duplicates
		for (int i = 0; i < nbrRel; i++) {
			boolean same = true;
			for (int j = 0; j < factorbaseSize; j++) {
				if (binaryexponents[j] != binMat[i][j]) {
					same = false;
					break;
				}
			}
			if (same) {
				return false;
			}
		}
		for (int i = 0; i < factorbaseSize; i++) {
			binMat[nbrRel][i] = binaryexponents[i];
			exponentList[nbrRel][i] = exponents[i];
		}
		savedr.add(r);
		return true;
	}

	/**
	 * Provided by the course
	 * 
	 * @param x
	 * @return
	 */
	private BigInteger squareRoot(BigInteger x) {
		BigInteger right = x, left = BigInteger.ZERO, mid;
		while (right.subtract(left).compareTo(BigInteger.ONE) > 0) {
			mid = (right.add(left)).shiftRight(1);
			if (mid.multiply(mid).compareTo(x) > 0)
				right = mid;
			else
				left = mid;
		}
		return left;
	}

	private ArrayList<boolean[]> runGauss() {
		System.out.print("Running GaussBin.exe... ");
		// Write to matrix.txt
		try {
			File file = new File("matrix.txt");
			PrintWriter pw = new PrintWriter(file);
			pw.println(relations + " " + factorbaseSize);
			for (int i = 0; i < exponentList.length; i++) {
				for (int j = 0; j < exponentList[0].length; j++) {
					pw.print(exponentList[i][j] + " ");
				}
				pw.println("");
			}

			pw.flush();
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Run GaussBin.exe, input is matrix.txt, output is out.txt
		try {
			ProcessBuilder pb = new ProcessBuilder("Gaussbin.exe", "matrix.txt", "out.txt");
			final Process p = pb.start();
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Read out.txt
		ArrayList<boolean[]> solutions = new ArrayList<boolean[]>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("out.txt"));
			int nbrSolutions = Integer.parseInt(br.readLine());
			for (int s = 0; s < nbrSolutions; s++) {
				String str = br.readLine();
				boolean[] solutionRow = new boolean[exponentList.length];
				String[] line = str.split(" ");
				for (int i = 0; i < exponentList.length; i++) {
//					if (line.length != exponentList.length) {
//						System.out.println("Mismatched amount of elements!  s: " + s);
//						System.out.println("file says: " + line.length + " program says " + exponentList.length);
//						System.exit(0);
//					}
					solutionRow[i] = Integer.parseInt(line[i]) == 1 ? true : false;
				}
				solutions.add(solutionRow);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(" DONE");
		return solutions;
	}

	private BigInteger tryAllSolutions(ArrayList<boolean[]> gaussResults) {
		System.out.print("Trying all solutions... ");
		for (boolean[] solution : gaussResults) {
			int[] currentSolution = new int[exponentList[0].length];
			BigInteger x = BigInteger.ONE;
			for (int i = 0; i < solution.length; i++) {
				if (solution[i]) {
					for (int j = 0; j < exponentList[0].length; j++) {
						currentSolution[j] += exponentList[i][j];
					}
					x = x.multiply(savedr.get(i));
				}
			}

			for (int i = 0; i < currentSolution.length; i++) {
				currentSolution[i] = currentSolution[i] / 2;
			}
			BigInteger y = BigInteger.ONE;
			for (int i = 0; i < currentSolution.length; i++) {
				y = y.multiply(BigInteger.valueOf(factorbase.get(i)).pow(currentSolution[i]));
			}

			BigInteger d = x.subtract(y).gcd(N);

			if (!d.equals(N) && !d.equals(BigInteger.ONE)) {
				System.out.println("DONE");
				return d;
			}

		}
		System.out.println("DONE");
		return null;
	}

}
