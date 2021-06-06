import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FormOil extends JFrame implements ActionListener {

    private JButton button;
    private JLabel answer;
    private JFrame frame;
    private BufferedImage image;
    private NeuralNetwork network;

    public FormOil(NeuralNetwork network) throws HeadlessException {
        this.network = network;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());
        button = new JButton("Choose file...");
        button.addActionListener(this);
        this.add(button);
        this.pack();
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            openAndRecognise(null);
        }
    }

    private void openAndRecognise(String imagePath) {
        if (imagePath == null) {
            JFileChooser chooser = new JFileChooser();
            int response = chooser.showOpenDialog(null);
            if (response == JFileChooser.APPROVE_OPTION) {
                imagePath = chooser.getSelectedFile().getAbsolutePath();
            }
        }

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            image = ImageIO.read((new File(imagePath)));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        double[] inputs = new double[100 * 100];
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                inputs[x + y * 100] = (image.getRGB(x, y) & 0xff) / 255.0;
            }
        }

        double[] outputs = network.feedForward(inputs);
        String guess = "";
        double maxDigitWeight = -1;
        for (int i = 0; i < 2; i++) {
            if (outputs[i] > maxDigitWeight) {
                maxDigitWeight = outputs[i];
                guess = i == 0 ? "OIL SPILL ON PHOTO" : "NO OIL SPILL ON PHOTO";
            }
        }

        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(image));
        answer = new JLabel();
        answer.setText(guess);
        frame.getContentPane().add(answer, BorderLayout.NORTH);
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.pack();
    }
}

