package com.netfliz.netfliz.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageResizerService {

    /**
     * Resize image from MultipartFile to a specific maxWidth (keeps aspect ratio).
     * Returns byte[] of output (PNG/JPEG based on original format).
     */
    public byte[] resize(byte[] bytes, int maxWidth, String outputFormat) throws IOException {
        try (var inputStream = new ByteArrayInputStream(bytes)) {
            BufferedImage original = ImageIO.read(inputStream);
            if (original == null) throw new IOException("Không thể đọc file ảnh.");

            double scale = maxWidth >= original.getWidth() ? 1.0 : (maxWidth / (double) original.getWidth());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Thumbnails.of(original)
                    .scale(scale)
                    .outputFormat(outputFormat)
                    .toOutputStream(baos);

            return baos.toByteArray();
        }
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
