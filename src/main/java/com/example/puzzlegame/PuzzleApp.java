package com.example.puzzlegame;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;

public class PuzzleApp extends Application {
    private static final int ROWS = 4;
    private static final int COLS = 4;
    private static final int IMAGE_WIDTH = 300;
    private static final int IMAGE_HEIGHT = 300;
    private static final int PUZZLE_HOLDER_SIZE = 300;
    private static final String IMAGE_PATH = "file:src/main/resources/images/puzzle.png";
    private Button solveButton;
    private Button restartButton;

    private final Map<String, double[]> puzzleToPlaceholder = new HashMap<>();


    @Override
    public void start(Stage stage) {
        List<ImageView> puzzlePieces = cutImageIntoPuzzlePieces();
        stage.setTitle("Puzzle");

        Pane playground = new Pane();
        playground.setStyle("-fx-background-color: cornsilk;");
        playground.setPrefSize(1200, 500);

        playground.getChildren().add(generatePuzzleGrid());

        VBox puzzlesHolder = new VBox(10);
        puzzlesHolder.setStyle("-fx-background-color: #e2a491; -fx-padding: 10;");
        puzzlesHolder.setAlignment(Pos.TOP_CENTER);
        puzzlesHolder.setMaxWidth(PUZZLE_HOLDER_SIZE);
        puzzlesHolder.setMinWidth(PUZZLE_HOLDER_SIZE);
        puzzlesHolder.getChildren().add(generatePuzzleRows(puzzlePieces, playground));

        HBox layout = new HBox();
        layout.getChildren().addAll(puzzlesHolder, playground);

        solveButton = new Button("Solve Puzzle");
        solveButton.setOnAction(event -> solvePuzzle(puzzlePieces, playground));

        restartButton = new Button("Restart Game");
        restartButton.setOnAction(event -> restartGame(puzzlePieces, playground, puzzlesHolder));

        puzzlesHolder.getChildren().addAll(solveButton, restartButton);

        stage.setScene(new Scene(layout));
        stage.show();
    }

    private HBox generatePuzzleRows(List<ImageView> puzzlePieces, Pane playground) {
        VBox row1 = new VBox(5);
        VBox row2 = new VBox(5);

        for (int i = 0; i < puzzlePieces.size() / 2; i++) {
            addDragAndDropFunctionality(puzzlePieces.get(i), playground);
            row1.getChildren().add(puzzlePieces.get(i));
        }
        for (int i = puzzlePieces.size() / 2; i < puzzlePieces.size(); i++) {
            addDragAndDropFunctionality(puzzlePieces.get(i), playground);
            row2.getChildren().add(puzzlePieces.get(i));
        }
        HBox rowsContainer = new HBox(10);
        rowsContainer.getChildren().addAll(row1, row2);
        rowsContainer.setAlignment(Pos.CENTER);
        return rowsContainer;
    }

    private TilePane generatePuzzleGrid() {
        int placeholderWidth = IMAGE_WIDTH / COLS;
        int placeholderHeight = IMAGE_HEIGHT / ROWS;

        int padding = 10;

        TilePane grid = new TilePane();
        grid.setPadding(new Insets(padding));
        grid.setPrefColumns(COLS);
        grid.setPrefRows(ROWS);

        int imageId = 1;
        for (int i = 0; i < COLS * ROWS; i++) {
            int col = i % COLS;
            int row = i / COLS;

            Rectangle rectangle = new Rectangle(placeholderWidth, placeholderHeight, Color.WHITE);
            rectangle.setStroke(Color.BLACK);
            grid.getChildren().add(rectangle);

            double x = col * placeholderWidth + rectangle.getWidth() / 2.0 + PUZZLE_HOLDER_SIZE + padding;
            double y = row * placeholderHeight + placeholderHeight / 2.0;

            puzzleToPlaceholder.put(String.valueOf(imageId++), new double[]{x, y});
        }

        return grid;
    }

    private List<ImageView> cutImageIntoPuzzlePieces() {
        List<ImageView> puzzlePieces = new ArrayList<>();

        Image originalImage = new Image(IMAGE_PATH);
        int pieceWidth = IMAGE_WIDTH / COLS;
        int pieceHeight = IMAGE_HEIGHT / ROWS;

        int id = 1;
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                ImageView piece = new ImageView();
                piece.setImage(originalImage);
                piece.setViewport(new Rectangle2D(y * pieceWidth, x * pieceHeight, pieceWidth, pieceHeight));
                piece.setFitWidth(pieceWidth);
                piece.setFitHeight(pieceHeight);
                piece.setId(String.valueOf(id++));

                puzzlePieces.add(piece);
            }
        }

        Collections.shuffle(puzzlePieces);
        return puzzlePieces;
    }

    private void addDragAndDropFunctionality(ImageView puzzlePiece, Pane playground) {
        DropShadow dropShadow = new DropShadow();
        Glow glow = new Glow();
        puzzlePiece.setEffect(dropShadow);

        puzzlePiece.setOnMousePressed(mouseEvent -> {
            Pane parentVBox = (Pane) puzzlePiece.getParent();

            parentVBox.getChildren().remove(puzzlePiece);
            playground.getChildren().add(puzzlePiece);
        });

        puzzlePiece.setOnMouseReleased(mouseEvent -> {
            double sceneX = mouseEvent.getSceneX();
            double sceneY = mouseEvent.getSceneY();

            double[] validCoordinates = puzzleToPlaceholder.get(puzzlePiece.getId());
            double distance = Math.sqrt(Math.pow(sceneX - validCoordinates[0], 2) + Math.pow(sceneY - validCoordinates[1], 2));

            double errorThreshold = 20;

            if (distance < errorThreshold) {
                setPieceToCorrectPlace(puzzlePiece);
            }
        });

        puzzlePiece.setOnMouseDragged(mouseEvent -> {
            double mouseX = mouseEvent.getSceneX();
            double mouseY = mouseEvent.getSceneY();

            puzzlePiece.setLayoutX(mouseX - 3.6 * puzzlePiece.getBoundsInLocal().getWidth());
            puzzlePiece.setLayoutY(mouseY - puzzlePiece.getBoundsInLocal().getHeight() / 3.4);
        });

        puzzlePiece.setOnMouseEntered(mouseEvent -> {
            puzzlePiece.setCursor(Cursor.HAND);
            dropShadow.setInput(glow);
        });

        puzzlePiece.setOnMouseExited(mouseEvent -> dropShadow.setInput(null));
    }

    private void setPieceToCorrectPlace(ImageView puzzlePiece) {
        double[] validCoordinates = puzzleToPlaceholder.get(puzzlePiece.getId());
        double x = validCoordinates[0] - puzzlePiece.getFitWidth() * COLS - puzzlePiece.getFitWidth() / 2.15;
        double y = validCoordinates[1] - puzzlePiece.getFitHeight() / 2.9;

        puzzlePiece.setLayoutX(x);
        puzzlePiece.setLayoutY(y);

        puzzlePiece.setOnMouseDragged(null);
        puzzlePiece.setEffect(null);
    }


    private void solvePuzzle(List<ImageView> puzzlePieces, Pane playground) {
        for (ImageView piece : puzzlePieces) {
            Pane parentVBox = (Pane) piece.getParent();

            parentVBox.getChildren().remove(piece);
            playground.getChildren().add(piece);

            setPieceToCorrectPlace(piece);
        }
    }

    private void restartGame(List<ImageView> puzzlePieces, Pane playground, VBox puzzleHolder) {
        for (ImageView piece : puzzlePieces) {
            Pane parentVBox = (Pane) piece.getParent();
            if (parentVBox != null) {
                parentVBox.getChildren().remove(piece);
            }
        }

        puzzlePieces.clear();
        puzzlePieces.addAll(cutImageIntoPuzzlePieces());

        puzzleHolder.getChildren().clear();
        puzzleHolder.getChildren().add(generatePuzzleRows(puzzlePieces, playground));

        puzzleHolder.getChildren().addAll(solveButton, restartButton);
    }
}
