import static java.lang.System.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Steganography {
	String path;
	private Image i;
	BufferedImage image;

	public Steganography (String path) {
		this.path = path;
		i = new ImageIcon(path).getImage();
		File file = new File(path);
		try {
			image = ImageIO.read(file);
		}
		catch (IOException e) {
			System.out.println("The image provided is invalid.");
			System.exit(0);
		}
	}

	public BufferedImage getImage() {
		return image;
	}

	public static void main(String[] args) {
		Scanner scan = new Scanner (in);
		Steganography image = null;
		System.out.println("Do you want to encrypt or decrypt your image?");
		String action = "";
		while (!action.equalsIgnoreCase("encrypt") &&
					 !action.equalsIgnoreCase("decrypt")) {
			System.out.println("Please choose 'encrypt' or 'decrypt'");
			action = scan.nextLine();
		}
		String path = "";
		while (!path.matches("[ .0-9a-zA-Z_]+")){
			System.out.println("Please enter the path to your image. Your image " +
												 "must be a .png or .jpg file.");
			path = scan.nextLine();
			image = new Steganography(path);
		}
		if(action.equals("encrypt")) {
			String message = "";
			while (!message.matches("[ 0-9a-zA-Z]+")) {
				System.out.println("Please enter a message with only " +
				"alphanumeric characters.");
				message = scan.nextLine();
			}
			encode (image, message);
		} else if(action.equals("decrypt")) {
			decode (image);
		}
	}

	public static void encode (Steganography image, String message) {
		byte[] bytes = message.getBytes();
		String colour = "red";
		int y = 0;

		int count = 7;
		for(byte b : bytes) { // for each letter in the message
			for(int x = 0; x <3; x++) {
				Color c = new Color(image.getImage().getRGB(x,y));

				byte red = (byte)c.getRed();
				byte green = (byte)c.getGreen();
				byte blue = (byte)c.getBlue();

				int redBit = (b >>> count) & 1;
				int greenBit = (b >>> (count-1)) & 1;
				int blueBit = (b >>> (count-2)) & 1;
				count -=3;

				red = (byte)((red & 0xFE)| redBit);
				green = (byte)((green & 0xFE)| greenBit);
				blue = (byte)((blue & 0xFE)| blueBit);

				Color newColor = new Color((red & 0xFF),
					(green & 0xFF),(blue & 0xFF));

				image.getImage().setRGB(x, y, newColor.getRGB());
				Color z = new Color(image.getImage().getRGB(x, y));
			}
			count = 7;
			y++;
		}
		try {
			File output = new File("encryptedImage.png");
			ImageIO.write(image.getImage(), "png", output);
		} catch(Exception ex) {
			System.out.println("An error occured while created the new image.");
			System.exit(0);
		}
		System.out.println();
		System.out.println("Your new image with your message has been created.");
		System.out.println("It has been saved as 'encryptedImage.png'.");
	}

	public static void decode (Steganography image) {
		int y = 0;
		String finalMessage = "";
		boolean isChar = true;
		String message = "";
		while(isChar) { // for each letter in the message
			for(int x = 0; x <3; x++) {
				Color color = new Color(image.getImage().getRGB(x,y));

				byte red = (byte)color.getRed();
				byte green = (byte)color.getGreen();
				byte blue = (byte)color.getBlue();

				message+=("" + (red&1));
				message+=("" + (green&1));
				if(x != 2) {
					message+=("" + (blue&1));
				}
			}
			Character c = (char) Integer.parseInt(message, 2);
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
		    	|| Character.isDigit(c) || Character.isWhitespace(c)) {
			  finalMessage += c.toString();
			}  else {
				isChar = false;
			}
			message = "";
			y++;
		}
		System.out.println();
		System.out.println("Your message is: " + finalMessage);
	}
}
