import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXProgressBar;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

public class MainController {

    @FXML public BorderPane borderPane;
    @FXML public ImageView QRImage;
    @FXML public JFXProgressBar progressBar;
    @FXML public StackPane stackPane;
    @FXML private JFXButton genBtn;
    @FXML public Label expiredLabel;

    @FXML private void initialize() {
        expiredLabel.setVisible(false);
        genQRCode(); // gen a QR code at startup
        genBtn.setOnAction(event -> genQRCode());
    }

    private void showMessage(String title, String msg) {
        JFXButton okBtn = new JFXButton("Ok");
        okBtn.getStyleClass().add("dialog-accept");

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label(title));
        layout.setBody(new Label(msg));
        layout.setActions(okBtn);

        JFXDialog dialog = new JFXDialog(borderPane, layout, JFXDialog.DialogTransition.CENTER);
        okBtn.setOnAction(event -> dialog.close());
        dialog.show(stackPane);
    }

    private void genQRCode() {
        genBtn.setDisable(true); // disable btn to avoid spamming requests to the server

        // Run slow operations in background thread to avoid blocking the UI thread (causes UI lag).
        // Any update to UI elements must uses Platform.runLater(..) to do so or nothing will happen.
        new Thread(() -> {
            try {
                Thread.sleep(250); // let ripple animation finish

                String fileName = "C:\\ProgramData\\mars\\.computerID.txt";
                FileReader filereader = new FileReader(fileName);
                BufferedReader bufferedReader = new BufferedReader(filereader);
                String computerName = bufferedReader.readLine();

                final String uuid = UUID.randomUUID().toString();
                HttpResponse<JsonNode> response = Unirest.post("http://52.33.35.165:8080/api/register-uuid").field("uuid", uuid).asJson();
                switch (response.getStatus()) {
                    case 200:
                        // animate the progress bar to show how much time until the QR code expire
                        new Thread(() -> {
                            JSONObject obj = new JSONObject(response.getBody());
                            final int ttl = obj.getJSONObject("object").getInt("ttl");
                            final int waitTime = 17; // millis; ~60fps
                            int timeLeft = ttl;

                            while (timeLeft >= 0) {
                                try {
                                    Thread.sleep(waitTime);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                final int finalTimeLeft = timeLeft;
                                Platform.runLater(() -> progressBar.setProgress((double) (finalTimeLeft - waitTime) / ttl));
                                timeLeft = timeLeft - waitTime;
                            }

                            Platform.runLater(() -> {
                                expiredLabel.setVisible(false);
                                progressBar.setProgress(0);
                                genBtn.setDisable(false);
                            });
                        }).start();

                        // display the QR code with the uuid and computer name embedded
                        byte[] imageBytes = QRCode.from(uuid + "\n" + computerName).withSize(368, 368).to(ImageType.JPG).stream().toByteArray();
                        ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes);
                        Platform.runLater(() -> QRImage.setImage(new Image(imageStream)));
                        break;
                    // todo: handle other status codes?
                    default:
                        Platform.runLater(() -> showMessage(String.valueOf(response.getStatus()), response.getStatusText()));
                }
            } catch (UnirestException | InterruptedException e) {
                Platform.runLater(() -> showMessage("Error!", "System has encountered an unexpected error."));
                e.printStackTrace();
            } catch (IOException e) {
                Platform.runLater(() -> showMessage("Error!", "File of computer ID not found. Try re-install the program."));
            }
        }).start();
    }
}
