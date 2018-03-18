package com.edubreeze.utils;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

public class ImageUtil {

    public static final String JPG_IMAGE_FORMAT = "jpg";

    public static BufferedImage convertToBuffered(Image image) {
        // null if in case you want to reuse BufferedImage object for memory reasons
        return SwingFXUtils.fromFXImage(image, null);
    }

    public static byte[] convertToByteArray(BufferedImage bufImg) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufImg, JPG_IMAGE_FORMAT, baos);
        return baos.toByteArray();
    }

    public static Image convertToImage(byte[] imageBytes) throws  IOException{
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        BufferedImage bufImg = ImageIO.read(bais);

        return SwingFXUtils.toFXImage(bufImg, null);
    }

    public static byte[] convertFidToByte(Fid image) {
        if(image != null) {
            Fid.Fiv view = image.getViews()[0];
            return view.getImageData();
        }
        return null;
    }
    public static Image convertFidToJavaFXImage(Fid image) {
        Fid.Fiv view = image.getViews()[0];

        final AtomicReference<WritableImage> ref = new AtomicReference<>();
        BufferedImage img = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        img.getRaster().setDataElements(0, 0, view.getWidth(), view.getHeight(), view.getImageData());
        ref.set(SwingFXUtils.toFXImage(img, ref.get()));
        img.flush();

        return ref.get();
    }

    /**
     * for converting saved fingerprint image created from Fid.Fiv views get Image data
     * @param fidBytes
     * @return
     * @throws IOException
     */
    public static Image convertFidImageDataBytesToImage(byte[] fidBytes) throws UareUException, IOException {
        Fid fid = UareUGlobal.GetImporter().ImportFid(fidBytes, Fid.Format.ANSI_381_2004);
        Fid.Fiv image = fid.getViews()[0];

        return convertToImage(image.getImageData());
    }
}
