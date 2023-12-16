package com.example.puzzlegame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageSplitter {
    public static void main(String[] args) {
        try {
            BufferedImage originalImage = ImageIO.read(new File("src/main/resources/images/puzzle.png"));

            int rows = 4; // Define the number of rows
            int cols = 4; // Define the number of columns

            int pieceWidth = originalImage.getWidth() / cols;
            int pieceHeight = originalImage.getHeight() / rows;

            int count = 0;
            BufferedImage[] imgArray = new BufferedImage[rows * cols];

            for (int x = 0; x < rows; x++) {
                for (int y = 0; y < cols; y++) {
                    imgArray[count] = new BufferedImage(pieceWidth, pieceHeight, originalImage.getType());

                    Graphics2D g = imgArray[count++].createGraphics();
                    g.drawImage(originalImage, 0, 0, pieceWidth, pieceHeight, pieceWidth * y, pieceHeight * x,
                            pieceWidth * y + pieceWidth, pieceHeight * x + pieceHeight, null);
                    g.dispose();
                }
            }

            // Save each puzzle piece
            for (int i = 0; i < imgArray.length; i++) {
                ImageIO.write(imgArray[i], "jpg", new File("src/main/resources/PuzzlePieces/puzzle_piece_" + i + ".jpg"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

