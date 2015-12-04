import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import javax.swing.JProgressBar;
import java.awt.BorderLayout;

import javax.swing.*;
import org.json.*;
import java.io.ByteArrayOutputStream;
import java.io.*;
import java.util.UUID;

public class Main{

    public static void main(String[] args) {
        String computerName = "";
        String fileName = "C:\\ProgramData\\mars\\.computerID.txt";
        String line = null;

        JFrame frame = new JFrame("QR code example");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        try{
            FileReader filereader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(filereader);
            while((line = bufferedReader.readLine()) != null){
                computerName = line;
            }
            genQR(frame, computerName);
        }
        catch(FileNotFoundException ex){
            JOptionPane.showMessageDialog(frame, "File of computer ID not found. Re-install program.");
                System.out.println(fileName+" not found");
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
    public static void genQR(JFrame frame, String output)
    {
        JButton refresh = new JButton("Refresh");
        String ttl;

        System.out.println("Generating QR code");
        UUID uuid= UUID.randomUUID();
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post("http://52.33.35.165:8080/api/register-uuid").field("uuid", uuid.toString()).asJson();
            int statusCode = jsonResponse.getStatus();
            if(statusCode == 200)
            {
                JSONObject obj= new JSONObject(jsonResponse.getBody());
                ttl= obj.getJSONObject("object").get("ttl").toString();
                String code = uuid + "\n" + output;
                output = code;
                System.out.println(output);
            }
            else
            {
                JOptionPane.showMessageDialog(frame, "Server Error");

            }
        } catch (UnirestException e) {
            JOptionPane.showMessageDialog(frame, "Unexpected Error");
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = QRCode.from(output).withSize(256, 256).to(ImageType.JPG).stream();
        ImageIcon QRCodeImage = new ImageIcon(stream.toByteArray());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(QRCodeImage), BorderLayout.CENTER);
       
        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }
}
