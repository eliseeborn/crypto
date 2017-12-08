public class Correlation {
    private String actualKeyStream;
    private int[] C1coeff, C2coeff, C3coeff;
    private int[] stateC1, stateC2, stateC3; //Saves the state for a corresponding greatestCorrelation
    private double greatestCorrelation;
    private String[] partialKeys;
    		

    public Correlation() {
        actualKeyStream = "0000111100111100001001010111001000000100111110101000011011100010101110010110011000011100111011000001101110011111010111111010101100100110111110111010100100001110011010110111010010101011001001000";
        C1coeff = new int[]{1,0,1,1,0,0,1,1,0,1,0,1,1};
        C2coeff = new int[]{1,0,1,0,1,1,0,0,1,1,0,1,0,1,0};
        C3coeff = new int[]{1,1,0,0,1,0,0,1,0,1,0,0,1,1,0,1,0};
        stateC1 = new int[13];
        stateC2 = new int[15];
        stateC3 = new int[17];
        greatestCorrelation = 0;
        partialKeys = new String[3];
    }
    
    public boolean confirmKey() {
    	String comparisonKeyStream = "";
    	for(int i = 0; i < actualKeyStream.length(); i++) {
    		int sum = Character.getNumericValue(partialKeys[0].charAt(i));
    		sum += Character.getNumericValue(partialKeys[1].charAt(i));
    		sum += Character.getNumericValue(partialKeys[2].charAt(i));
    		if (sum >= 2) {
    			comparisonKeyStream += "1";
    		} else {
    			comparisonKeyStream += "0";
    		}
    	}
    	return comparisonKeyStream.equals(actualKeyStream);
    }
    
    public void getAllStates() {
        //For first LFSR
        greatestCorrelation = 0;
        for (int i = 0; i < Math.pow(2, 13); i++) {
            String currentState = Integer.toBinaryString(i);
            currentState = padMyString(currentState, 13);
            String keyStream = genKeyStream(currentState, C1coeff);
            double correlation = compareKeyStreams(keyStream);
            if (correlation > greatestCorrelation) {
                greatestCorrelation = correlation;
                setState(currentState, stateC1);
                partialKeys[0] = keyStream;
            }
        }
        System.out.println("Greatest correlation for C1: " + greatestCorrelation);
        
        
        //For second LFSR
        greatestCorrelation = 0;
        for (int i = 0; i < Math.pow(2, 15); i++) {
            String currentState = Integer.toBinaryString(i);
            currentState = padMyString(currentState, 15);
            String keyStream = genKeyStream(currentState, C2coeff);
            double correlation = compareKeyStreams(keyStream);
            if (correlation > greatestCorrelation) {
                greatestCorrelation = correlation;
                setState(currentState, stateC2);
                partialKeys[1] = keyStream;
            }
        }
        System.out.println("Greatest correlation for C2: " + greatestCorrelation);

        //For third LFSR
        greatestCorrelation = 0;
        for (int i = 0; i < Math.pow(2, 17); i++) {
            String currentState = Integer.toBinaryString(i);
            currentState = padMyString(currentState, 17);
            String keyStream = genKeyStream(currentState, C3coeff);
            double correlation = compareKeyStreams(keyStream);
            if (correlation > greatestCorrelation) {
                greatestCorrelation = correlation;
                setState(currentState, stateC3);
                partialKeys[2] = keyStream;
            }	
        }
        System.out.println("Greatest correlation for C3: " + greatestCorrelation);
}
    
    private String padMyString(String currentState, int i) {
        int nrToPad = i - currentState.length();
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < nrToPad; j++) {
            sb.append("0");
        }
        return sb.toString()+currentState;
    }

    private void setState(String currentState, int[] state) {
        for (int i = 0; i < state.length; i++) {
            state[i] = Character.getNumericValue(currentState.charAt(i));
        }
    }
    
    private String genKeyStream(String state, int[] coeff) {
        int[] stateC = new int[state.length()];
        for (int i = 0; i < coeff.length; i++) {
            stateC[i] = Character.getNumericValue(state.charAt(i));
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < actualKeyStream.length(); i++) {
            sb.append(stateC[0]);

            //Calculate new value
            int newVal = 0;
            for (int j = 0; j < stateC.length; j++) {
                newVal += stateC[j]*coeff[j];
                newVal %= 2;
            }
            //Shift all registers
            for (int j = 0; j < stateC.length-1; j++) {
                stateC[j] = stateC[j+1];
            }
            //Insert new value at right-most index
            stateC[stateC.length-1] = newVal;
        }
        return sb.toString();
    }
    
    private double compareKeyStreams(String keyStream) {
        int counter = 0;
        for (int i = 0; i < actualKeyStream.length(); i++) {
            if(keyStream.charAt(i) != actualKeyStream.charAt(i)) {
                counter++;
            }
        }
        double correlation = (double)counter/(double)keyStream.length();
        return 1-correlation;
    }

    public static void main(String[] args) {
        Correlation cor = new Correlation();
        cor.getAllStates();
        System.out.println();
        for (int i = 0; i < cor.stateC1.length; i++) {
            System.out.print(cor.stateC1[i]);
        }
        System.out.println();
        for (int i = 0; i < cor.stateC2.length; i++) {
            System.out.print(cor.stateC2[i]);
        }
        System.out.println();
        for (int i = 0; i < cor.stateC3.length; i++) {
            System.out.print(cor.stateC3[i]);
        }
        System.out.println();
        if(cor.confirmKey()) {
        	System.out.println("Correlation attack was successful");
        }
    }

}


