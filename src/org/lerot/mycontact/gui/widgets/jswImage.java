package org.lerot.mycontact.gui.widgets;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.apache.commons.codec.binary.Base64;
import org.imgscalr.Scalr;

public class jswImage
{

	private class jswBufferedImage
	{
		BufferedImage image;

		public void parseToImage() throws IOException
		{
			String base64str = encodedimage.replaceAll("\\\\n", "\n");
			byte[] base64 = base64str.getBytes(Charset.forName("UTF-8"));
			byte[] decoded = Base64.decodeBase64(base64);
			image = ImageIO.read(new ByteArrayInputStream(decoded));

		}

		public void rotateCw()
		{
			int width = image.getWidth();
			int height = image.getHeight();
			BufferedImage newImage = new BufferedImage(height, width,
					image.getType());

			for (int i = 0; i < width; i++)
				for (int j = 0; j < height; j++)
					newImage.setRGB(height - 1 - j, i, image.getRGB(i, j));

			image = newImage;
		}

		public void ScaleImage(int targetheight) throws IOException
		{

			int height = image.getHeight();
			int width = image.getWidth();
			float scale = targetheight / (float) height;
			int targetwidth = (int) ((float) width * scale);
			BufferedImage scaledimage = Scalr.resize(image,
					Scalr.Method.BALANCED, targetwidth, getTargetheight());
			image = scaledimage;
		}

	}

	public static byte[] decodeImage(String imageDataString)
	{
		return Base64.decodeBase64(imageDataString);
	}

	public static String encodeImage(byte[] imageByteArray)
	{
		return Base64.encodeBase64URLSafeString(imageByteArray);
	}

	String encodedimage;

	private int rotation = 0;
	private int targetheight = 0;

	public jswImage()
	{
		encodedimage = null;
		setTargetheight(0);
		setRotation(0);

	}

	public jswImage(String base64string)
	{
		encodedimage = base64string;
		setTargetheight(0);
		setRotation(0);
	}

	public jswLabel DisplayImage()
	{
		jswLabel lbl;
		try
		{
			jswBufferedImage img = new jswBufferedImage();
			img.parseToImage();
			if (targetheight > 0) img.ScaleImage(targetheight);
			ImageIcon icon = new ImageIcon(img.image);
			lbl = new jswLabel();
			lbl.setIcon(icon);

		} catch (Exception e)
		{
			lbl = new jswLabel(" image not parsed ");
		}

		return lbl;
	}

	public JLabel DisplayRotatedImage() throws IOException
	{
		jswBufferedImage img = new jswBufferedImage();
		img.parseToImage();
		img.rotateCw();
		ImageIcon icon = new ImageIcon(img.image);
		JLabel lbl = new JLabel();
		lbl.setIcon(icon);
		return lbl;
	}

	public String getEncodedImage()
	{

		return encodedimage;
	}

	int getRotation()
	{
		return rotation;
	}

	int getTargetheight()
	{
		return targetheight;
	}

	/**
	 * @desc Image manipulation - Conversion
	 * 
	 * @filename ImageManipulation.java
	 * @author Jeevanandam M.
	 * @copyright myjeeva.com
	 */

	public void importfile(String filename)
	{

		File file = new File(filename);
		try
		{
			// Reading a Image file from file system
			FileInputStream imageInFile = new FileInputStream(file);
			byte imageData[] = new byte[(int) file.length()];
			imageInFile.read(imageData);

			// Converting Image byte array into Base64 String
			String imageDataString = encodeImage(imageData);

			imageInFile.close();
			encodedimage = imageDataString;

			System.out.println("Image Successfully Manipulated!");
		} catch (FileNotFoundException e)
		{
			System.out.println("Image not found" + e);
		} catch (IOException ioe)
		{
			System.out.println("Exception while reading the Image " + ioe);
		}

	}

	void setRotation(int rotation)
	{
		this.rotation = rotation;
	}

	public void setTargetheight(int targetheight)
	{
		this.targetheight = targetheight;
	}

}
