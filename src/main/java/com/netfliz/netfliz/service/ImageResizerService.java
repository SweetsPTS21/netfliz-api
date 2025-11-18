package com.netfliz.netfliz.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageResizerService {

    /**
     * Resize image from MultipartFile to a specific maxWidth (keeps aspect ratio).
     * Returns byte[] of output (PNG/JPEG based on original format).
     */
    public byte[] resize(MultipartFile file, int maxWidth, String outputFormat) throws IOException {
        BufferedImage original = ImageIO.read(file.getInputStream());
        if (original == null) throw new IOException("Không thể đọc file ảnh.");

        // If image width is already smaller than maxWidth, just re-encode
        int width = original.getWidth();
        double scale = maxWidth >= width ? 1.0 : (maxWidth / (double) width);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Thumbnails.of(original)
                .scale(scale)
                .outputFormat(outputFormat)
                .toOutputStream(baos);

        return baos.toByteArray();
    }

    // overload: resize from BufferedImage
    public byte[] resize(BufferedImage input, int maxWidth, String outputFormat) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int width = input.getWidth();
        double scale = maxWidth >= width ? 1.0 : (maxWidth / (double) width);
        Thumbnails.of(input)
                .scale(scale)
                .outputFormat(outputFormat)
                .toOutputStream(baos);
        return baos.toByteArray();
    }
}
