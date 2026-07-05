import javafx.application.Application;
import javafx.stage.Stage;
import util.FileUtil;
import util.NavigationManager;
import util.SceneManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        FileUtil.ensureDataFilesExist();
        SceneManager.getInstance().initialize(primaryStage);
        NavigationManager.getInstance().navigate("/view/dashboard.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
