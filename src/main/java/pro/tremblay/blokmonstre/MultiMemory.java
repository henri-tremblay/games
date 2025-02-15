package pro.tremblay.blokmonstre;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

public class MultiMemory extends Application {

    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 800;
    private static final int TILE_SIZE = 80;
    private static final int COLUMNS = SCREEN_WIDTH / TILE_SIZE;
    private static final int ROWS = SCREEN_HEIGHT / TILE_SIZE;
    private static final Map<Integer, Multiplication> ANSWERS = allAnswers();

    private static Map<Integer, Multiplication> allAnswers() {
        Map<Integer, Multiplication> answers = new HashMap<>();
        for (int x = 1; x <= 10; x++) {
            for (int y = 1; y <= 10; y++) {
                int result = x * y;
                answers.put(result, new Multiplication(x, y));
            }
        }
        return answers;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private final RandomGenerator random = RandomGenerator.getDefault();
    private EquationView equationView;
    private Pane tilePane = new Pane();

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("MultiMemory");
        VBox root = createContent();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private VBox createContent() {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        equationView = new EquationView("Press Start");
        equationView.setOnMouseClicked(e -> startGame());

        tilePane = new Pane();
        tilePane.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);

        root.getChildren().addAll(equationView, tilePane);

        return root;
    }

    private void startGame() {
        addEquation();
    }

    private void addEquation() {
        Multiplication result = findResult();
        equationView.setText(result.x() + " x " + result.y() + " = ?");

        addResult(result, true);

        Multiplication result1 = findResult(result);
        addResult(result1, false);
        Multiplication result2 = findResult(result, result1);
        addResult(result2, false);
    }

    private Multiplication findResult(Multiplication... toIgnore) {
        int x = random.nextInt(1,11);
        int y = random.nextInt(1, 11);
        Multiplication result = new Multiplication(x, y);
        while(List.of(toIgnore).contains(result)) {
            x = random.nextInt(1,11);
            y = random.nextInt(1, 11);
            result = new Multiplication(x, y);
        }
        return result;
    }

    private void addResult(Multiplication equation, boolean goodOne) {
        int result = equation.result();
        TileView tile = new TileView(Integer.toString(result));
        tile.setTranslateX((equation.x() - 1) * TILE_SIZE);
        tile.setTranslateY((equation.y() - 1) * TILE_SIZE);
        tile.setOnMouseClicked(e -> {
                tile.hide();
                if (goodOne) {
                    tilePane.getChildren().clear();
                    addEquation();
                }
        });
        tilePane.getChildren().add(tile);
    }

    static class EquationView extends StackPane {

        private final Text text;

        EquationView(String content) {
            var border = new Rectangle(SCREEN_WIDTH, TILE_SIZE, null);
            border.setStroke(Color.RED);
            border.setStrokeWidth(4);
            border.setStrokeType(StrokeType.INSIDE);

            text = new Text(content);
            text.setFont(Font.font(64));
            getChildren().addAll(border, text);
        }

        void setText(String content) {
            text.setText(content);
        }
    }

    static class TileView extends StackPane {
        TileView(String content) {
            var border = new Rectangle(TILE_SIZE, TILE_SIZE, null);
            border.setStroke(Color.BLACK);
            border.setStrokeWidth(4);
            border.setStrokeType(StrokeType.INSIDE);

            var text = new Text(content);
            text.setFont(Font.font(64));

            getChildren().addAll(border, text);
        }

        void hide() {
            setVisible(false);
        }
    }

    record Multiplication(int x, int y) {
        public int result() {
            return x * y;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Multiplication m && result() == m.result();
        }

        @Override
        public int hashCode() {
            return x + y;
        }
    }
}
