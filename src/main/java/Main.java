import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        root.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
        primaryStage.setTitle("M.A.R.S");
        primaryStage.setScene(new Scene(root, 400, 482));
        primaryStage.getIcons().addAll(new Image("image/logo.png"));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        System.exit(0);
    }
}
