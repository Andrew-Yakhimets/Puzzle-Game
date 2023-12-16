package com.example.puzzlegame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class PuzzleGame extends Application {

    private static final int ROWS = 4;
    private static final int COLS = 4;
    private static final int IMAGE_WIDTH = 512;
    private static final int IMAGE_HEIGHT = 512;

    private ImageView[][] puzzlePieces = new ImageView[ROWS][COLS];

    @Override
    public void start(Stage primaryStage) {
        Image originalImage = new Image("file:src/main/resources/images/puzzle.png");
        int pieceWidth = IMAGE_WIDTH / COLS;
        int pieceHeight = IMAGE_HEIGHT / ROWS;

        GridPane grid = new GridPane();

        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                ImageView piece = new ImageView();
                piece.setImage(originalImage);
                piece.setViewport(new javafx.geometry.Rectangle2D(y * pieceWidth, x * pieceHeight, pieceWidth, pieceHeight));
                piece.setFitWidth(pieceWidth);
                piece.setFitHeight(pieceHeight);

                piece.setOnMouseDragged(this::onMouseDragged);

                puzzlePieces[x][y] = piece;
                grid.add(piece, y, x);
            }
        }

        Scene scene = new Scene(grid);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Puzzle Game");
        primaryStage.setWidth(originalImage.getWidth());
        primaryStage.setHeight(originalImage.getHeight());
        primaryStage.show();
    }

    private void onMouseDragged(MouseEvent event) {
        ImageView piece = (ImageView) event.getSource();
        piece.setX(event.getSceneX() - piece.getFitWidth() / 2);
        piece.setY(event.getSceneY() - piece.getFitHeight() / 2);
    }

    public static void main(String[] args) {
        System.out.println("Launching...");
        launch(args);
    }
}
