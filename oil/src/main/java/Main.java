import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.UnaryOperator;

public class Main {

    public static void main(String[] args) {
        oil();
    }

    private static void oil() {
        UnaryOperator<Double> sigmoid = x -> 1 / (1 + Math.exp(-x));
        UnaryOperator<Double> dsigmoid = y -> y * (1 - y);
        NeuralNetwork nn = new NeuralNetwork(0.001, sigmoid, dsigmoid, 10000, 5000, 3000, 2000, 1000, 500, 100, 2);


        File[] spillFiles = new File("oil/src/main/resources/spills").listFiles();
        File[] noSpillFiles = new File("oil/src/main/resources/noSpills").listFiles();

        File[] allFiles = new File[spillFiles.length + noSpillFiles.length];
        System.arraycopy(spillFiles, 0, allFiles, 0, spillFiles.length);
        System.arraycopy(noSpillFiles, 0, allFiles, spillFiles.length, noSpillFiles.length);

        int samples = allFiles.length;
        BufferedImage[] images = new BufferedImage[samples];
        int[] answers = new int[samples];
        for (int i = 0; i < samples; i++) {
            try {
                images[i] = ImageIO.read(allFiles[i]);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            // spill == 0; no_spill == 1;
            answers[i] = i < spillFiles.length ? 0 : 1;
        }

        double[][] inputs = new double[samples][100 * 100];

        for (int i = 0; i < samples; i++) {
            for (int x = 0; x < 100; x++) {
                for (int y = 0; y < 100; y++) {
                    if (images[i] != null) {
                        inputs[i][x + y * 100] = (images[i].getRGB(x, y) & 0xff) / 255.0;
                    }
                }
            }
        }

        int epochs = 10;
        for (int i = 1; i < epochs; i++) {
            int right = 0;
            double errorSum = 0;
            int batchSize = 100;
            for (int j = 0; j < batchSize; j++) {
                int imgIndex = (int) (Math.random() * samples);
                double[] targets = new double[2];
                int answer = answers[imgIndex];
                targets[answer] = 1;

                double[] outputs = nn.feedForward(inputs[imgIndex]);
                int maxDigit = 0;
                double maxDigitWeight = -1;
                for (int k = 0; k < 2; k++) {
                    if (outputs[k] > maxDigitWeight) {
                        maxDigitWeight = outputs[k];
                        maxDigit = k;
                    }
                }
                if (answer == maxDigit) {
                    right++;
                }
                for (int k = 0; k < 2; k++) {
                    errorSum += (targets[k] - outputs[k]) * (targets[k] - outputs[k]);
                }
                nn.backpropagation(targets);
            }
            System.out.println("epoch: " + i + ". correct: " + right + ". error: " + errorSum);
        }

        FormOil f = new FormOil(nn);
    }
}