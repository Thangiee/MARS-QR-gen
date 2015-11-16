import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("QR code example");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        System.out.println("Generating QR code");
        ByteArrayOutputStream stream = QRCode.from("Hello World").withSize(256, 256).to(ImageType.JPG).stream();
        ImageIcon QRCodeImage = new ImageIcon(stream.toByteArray());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(QRCodeImage), BorderLayout.CENTER);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }
}
