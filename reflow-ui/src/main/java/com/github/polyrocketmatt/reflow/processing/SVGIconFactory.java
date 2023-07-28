package com.github.polyrocketmatt.reflow.processing;

import com.github.polyrocketmatt.reflow.DecompilerController;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class SVGIconFactory {

    @SuppressWarnings("ConstantConditions")
    public static ImageView fromSVG(String path) {
        try {
            ImageView imageView = new ImageView();

            imageView.setFitWidth(20);
            imageView.setFitHeight(20);

            InputStream is = DecompilerController.class.getResource(path).openStream();
            BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
            TranscoderInput transcoderInput = new TranscoderInput(is);
            try {
                transcoder.transcode(transcoderInput, null);
                BufferedImage image = transcoder.getBufferedImage();
                BufferedImage processedImage = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, 64, 64, Scalr.OP_ANTIALIAS);
                Image img = SwingFXUtils.toFXImage(processedImage, null);

                imageView.setImage(img);
            } catch (TranscoderException ex) {
                ex.printStackTrace();
            }

            is.close();

            return imageView;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

}
