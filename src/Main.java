import javafx.application.Application;
import javafx.stage.Stage;
import util.FileUtil;
import util.Route;
import util.SceneManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        FileUtil.ensureDataFilesExist();
        SceneManager.getInstance().initialize(primaryStage);
        SceneManager.getInstance().navigateTo(Route.LOGIN);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
