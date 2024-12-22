package chatbot.vision;

import com.github.eduramiba.webcamcapture.drivers.NativeDriver;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.CountDownLatch;

@Service
public class CameraService {

    public BufferedImage capturePhoto() throws Exception {
        Webcam webcam = initializeWebcam();
        if (webcam == null) {
            System.err.println("No webcam detected!");
            return null;
        }

        CountDownLatch latch = new CountDownLatch(1);
        BufferedImage[] capturedImage = new BufferedImage[1];

        JFrame frame = setupUI(webcam, latch, capturedImage);
        latch.await();

        frame.dispose();
        webcam.close();

        saveCapturedImage(capturedImage[0]);
        return capturedImage[0];
    }

    private Webcam initializeWebcam() {
        Webcam.setDriver(new NativeDriver());
        Webcam webcam = Webcam.getDefault();
        if (webcam != null) {
            webcam.setViewSize(WebcamResolution.VGA.getSize());
            webcam.open(true);
        }
        return webcam;
    }

    private JFrame setupUI(Webcam webcam, CountDownLatch latch, BufferedImage[] capturedImage) {
        JLabel imageLabel = createImageLabel();
        JButton captureButton = createCaptureButton(webcam, latch, capturedImage, imageLabel);
        JPanel buttonPanel = createButtonPanel(captureButton);
        JFrame frame = createMainFrame(imageLabel, buttonPanel);
        startImageUpdateTimer(webcam, imageLabel);
        return frame;
    }

    private JLabel createImageLabel() {
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        return imageLabel;
    }

    private JButton createCaptureButton(Webcam webcam, CountDownLatch latch, BufferedImage[] capturedImage, JLabel imageLabel) {
        JButton captureButton = new JButton("Capture Photo");
        captureButton.addActionListener(e -> {
            BufferedImage image = webcam.getImage();
            if (image != null) {
                capturedImage[0] = image;
                JOptionPane.showMessageDialog(imageLabel, "Photo captured!");
                latch.countDown();
            } else {
                JOptionPane.showMessageDialog(imageLabel, "Failed to capture image!");
            }
        });
        return captureButton;
    }

    private JPanel createButtonPanel(JButton captureButton) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(captureButton);
        return buttonPanel;
    }

    private JFrame createMainFrame(JLabel imageLabel, JPanel buttonPanel) {
        JFrame frame = new JFrame("Webcam Viewer (Updates every 100 ms)");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(imageLabel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setSize(640, 480);
        frame.setVisible(true);
        return frame;
    }

    private void startImageUpdateTimer(Webcam webcam, JLabel imageLabel) {
        Timer timer = new Timer(50, ev -> {
            BufferedImage image = webcam.getImage();
            if (image != null) {
                ImageIcon icon = new ImageIcon(image);
                imageLabel.setIcon(icon);
            }
        });
        timer.start();
    }

    private void saveCapturedImage(BufferedImage image) {
        try {
            File outputFile = new File("mikolaj.jpg");
            boolean success = ImageIO.write(image, "JPG", outputFile);
            if (success) {
                System.out.println("Photo saved successfully to: " + outputFile.getAbsolutePath());
            } else {
                System.err.println("ImageIO.write returned false, the writer couldn't encode the image.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
