package io.azuremicroservices.qme.qme.helpers;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.core.io.ClassPathResource;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeGenerator {	
	
	public static void generateQRCodeImage(String text, int width, int height, String filePath)
			throws WriterException, IOException {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

		Path path = FileSystems.getDefault().getPath(filePath);
		MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

	}

	public static byte[] getQRCodeImage(String text, int width, int height) {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		Graphics2D graphics = null;
		byte[] pngData = null;
		
		try {
			BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
			BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, new MatrixToImageConfig(0xFF000000, 0xFFFFFFFF));
			
			URL url = new ClassPathResource("/static/images/logos/qMe_logo.png").getURL();			
							
			BufferedImage logo = ImageIO.read(url);
			logo = QRCodeGenerator.resizeImage(logo, 200, 200);
			BufferedImage qrFinal = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(), BufferedImage.TYPE_INT_ARGB);
			
			int diffHeight = qrImage.getHeight() - logo.getHeight();
			int diffWidth = qrImage.getWidth() - logo.getWidth();
			
			graphics = (Graphics2D) qrFinal.getGraphics();
			
			graphics.drawImage(qrImage, 0, 0, null);
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			
			graphics.drawImage(logo,  (int) Math.round(diffWidth / 2), (int) Math.round(diffHeight) / 2, null);	
			
			ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
	//		MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
			ImageIO.write(qrFinal, "png", pngOutputStream);
	
			pngData = pngOutputStream.toByteArray();
		} catch (WriterException | IOException e) {
			e.printStackTrace();
		} finally {
			if (graphics != null) {
				graphics.dispose();
			}
		}
		
		return pngData;
	}
	
	public static BufferedImage resizeImage(BufferedImage image, int width, int height) {
		Image temp = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D graphics = scaledImage.createGraphics();
		graphics.drawImage(temp, 0, 0, null);
		graphics.dispose();
		
		return scaledImage;
	}
}
