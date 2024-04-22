package com.fabriceci.fmc.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageUtils {

    public static Dimension getImageSize(File file) {

        BufferedImage readImage = null;
        Dimension dim = new Dimension();
        try {
            readImage = ImageIO.read(file);
            dim.height = readImage.getHeight();
            dim.width = readImage.getWidth();
        } catch (Exception e) {
            readImage = null;
        }
        return dim;
    }

    public static Dimension getImageSize(String path) {

        BufferedImage readImage = null;
        Dimension dim = new Dimension();
        try {
            readImage = ImageIO.read(new File(path));
            dim.height = readImage.getHeight();
            dim.width = readImage.getWidth();
        } catch (Exception e) {
            readImage = null;
        }
        return dim;
    }
}
