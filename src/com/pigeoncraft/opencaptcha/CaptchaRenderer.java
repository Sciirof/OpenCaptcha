package com.pigeoncraft.opencaptcha;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class CaptchaRenderer extends MapRenderer {

	private BufferedImage imgRescaled;
	private BufferedImage img;
	public CaptchaRenderer(String urlStr) {
		
		try {
			URL url = new URL(urlStr); // captcha image url
			img = ImageIO.read(url);
			int newImageWidth = 128;
			int newImageHeight = 128;
			imgRescaled = new BufferedImage(newImageWidth, newImageHeight, img.getType());
			Graphics2D g = imgRescaled.createGraphics();
			g.drawImage(img, 0, 0, newImageWidth, newImageHeight, null);
			g.dispose();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	@Override
	public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
		
		mapView.setScale(MapView.Scale.CLOSE);
		mapCanvas.drawImage(0, 0, imgRescaled);
	}
}
