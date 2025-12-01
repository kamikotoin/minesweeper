import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Random;

public class MinesweeperGUI extends Application {

    private static final int SIZE = 9;      // grid size (9x9)
    private static final int MINES = 10;    // number of mines

    private final Button[][] buttons = new Button[SIZE][SIZE];
    private final int[][] values = new int[SIZE][SIZE]; // -1 = mine, otherwise number of neighboring mines
    private final boolean[][] revealed = new boolean[SIZE][SIZE];
    private final boolean[][] flagged = new boolean[SIZE][SIZE];

    private Label statusLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        initBoard();

        BorderPane root = new BorderPane();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(2);
        grid.setVgap(2);

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Button btn = new Button();
                btn.setMinSize(40, 40);
                final int rr = r, cc = c;
                btn.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        onReveal(rr, cc);
                    } else if (e.getButton() == MouseButton.SECONDARY) {
                        onFlag(rr, cc);
                    }
                });
                buttons[r][c] = btn;
                grid.add(btn, c, r);
            }
        }

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER);
        Button restart = new Button("Restart");
        restart.setOnAction(e -> Platform.runLater(this::restartGame));
        statusLabel = new Label("Mines: " + MINES);
        topBar.getChildren().addAll(restart, statusLabel);

        root.setTop(topBar);
        root.setCenter(grid);

        Scene scene = new Scene(root, 420, 480);
        primaryStage.setTitle("Minesweeper (basic)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initBoard() {
        // reset arrays
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                values[r][c] = 0;
                revealed[r][c] = false;
                flagged[r][c] = false;
                if (buttons[r][c] != null) {
                    buttons[r][c].setText("");
                    buttons[r][c].setDisable(false);
                    buttons[r][c].setStyle("");
                }
            }
        }

        // place mines randomly
        Random rnd = new Random();
        int placed = 0;
        while (placed < MINES) {
            int r = rnd.nextInt(SIZE);
            int c = rnd.nextInt(SIZE);
            if (values[r][c] != -1) {
                values[r][c] = -1;
                placed++;
            }
        }

        // calculate neighbor counts
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (values[r][c] == -1) continue;
                int count = 0;
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int nr = r + dr, nc = c + dc;
                        if (nr >= 0 && nr < SIZE && nc >= 0 && nc < SIZE) {
                            if (values[nr][nc] == -1) count++;
                        }
                    }
                }
                values[r][c] = count;
            }
        }
    }

    private void onReveal(int r, int c) {
        if (revealed[r][c] || flagged[r][c]) return;
        revealed[r][c] = true;

        if (values[r][c] == -1) {
            // reveal all mines and show losing message
            buttons[r][c].setText("💣");
            buttons[r][c].setStyle("-fx-background-color: red; -fx-font-size: 18px;");
            revealAllMines();
            statusLabel.setText("You hit a mine! Game over.");
            disableAllButtons();
            return;
        }

        updateButtonForReveal(r, c);

        if (values[r][c] == 0) {
            // flood-fill neighbors
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int nr = r + dr, nc = c + dc;
                    if (nr >= 0 && nr < SIZE && nc >= 0 && nc < SIZE) {
                        if (!revealed[nr][nc]) onReveal(nr, nc);
                    }
                }
            }
        }

        if (checkWin()) {
            statusLabel.setText("You win! All safe cells revealed.");
            disableAllButtons();
        }
    }

    private void onFlag(int r, int c) {
        if (revealed[r][c]) return;
        flagged[r][c] = !flagged[r][c];
        buttons[r][c].setText(flagged[r][c] ? "F" : "");
        buttons[r][c].setStyle(flagged[r][c] ? "-fx-background-color: orange; -fx-font-weight: bold;" : "");
    }

    private void updateButtonForReveal(int r, int c) {
        Button btn = buttons[r][c];
        int val = values[r][c];
        if (val > 0) {
            btn.setText(String.valueOf(val));
            btn.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        } else {
            btn.setText("");
            btn.setStyle("-fx-background-color: lightgray;");
        }
        btn.setDisable(true);
    }

    private void revealAllMines() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (values[r][c] == -1) {
                    buttons[r][c].setText("💣");
                    buttons[r][c].setDisable(true);
                }
            }
        }
    }

    private boolean checkWin() {
        int cellsToReveal = SIZE * SIZE - MINES;
        int revealedCount = 0;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (revealed[r][c]) revealedCount++;
            }
        }
        return revealedCount >= cellsToReveal;
    }

    private void disableAllButtons() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                buttons[r][c].setDisable(true);
            }
        }
    }

    private void restartGame() {
        initBoard();
        // reset UI
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                buttons[r][c].setText("");
                buttons[r][c].setDisable(false);
                buttons[r][c].setStyle("");
                revealed[r][c] = false;
                flagged[r][c] = false;
            }
        }
        statusLabel.setText("Mines: " + MINES);
    }
}
