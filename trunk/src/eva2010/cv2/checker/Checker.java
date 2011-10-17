package eva2010.cv2.checker;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Checker {

    public static void main(String[] args) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(args[0]));
			
            double[] weights = new double[10];
			
            String line;
            while ((line = in.readLine()) != null) {
                Scanner scanner = new Scanner(line);
                scanner.useDelimiter(" ");
				
                double weight = scanner.nextDouble();
                int bin = scanner.nextInt();
				
                weights[bin] += weight;
            }
			
            double min = Integer.MAX_VALUE;
            double max = Integer.MIN_VALUE;
			
            for (int i = 0; i < 10; i++) {
                min = Math.min(weights[i], min);
                max = Math.max(weights[i], max);

                System.out.println("" + i + ": " + weights[i]);
            }
			
            System.out.println("difference: " + (max - min));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
