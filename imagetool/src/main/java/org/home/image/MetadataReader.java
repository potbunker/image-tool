package org.home.image;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class MetadataReader {

	public void displayMetadata(Node node) {
		System.out.println(String.format("name: %s, value: %s", node.getNodeName(), node.getNodeValue()));
		NodeList children = node.getChildNodes();
		if (children.getLength() > 0) {
			for (int index = 0; index < children.getLength(); index++) {
				displayMetadata(children.item(index));
			}
		}
	}

	public void read(Path path) throws Exception {
		if (path.toFile().isFile()) {
			ImageInputStream iis = ImageIO
					.createImageInputStream(new BufferedInputStream(
							new FileInputStream(path.toFile())));
			Consumer<ImageReader> action = x -> {
				x.setInput(iis, true);
				try {
					IIOImage image = x.readAll(0, null);
					IIOMetadata metadata = image.getMetadata();
					Arrays.stream(metadata.getMetadataFormatNames())
							.map(metadata::getAsTree)
							.forEach(this::displayMetadata);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
			ImageIO.getImageReadersByMIMEType("image/jpeg").forEachRemaining(
					action);
		}
	}
	
	
	public void read2(Path path) throws Exception {
		File jpegFile = path.toFile();
		Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
		Iterator<Directory> iter = metadata.getDirectoryIterator();
		while (iter.hasNext()) {
			Directory dir = iter.next();
			dir.getTagIterator().forEachRemaining(x->{
				Tag tag = ((Tag) x);
				try {
					System.out.println(String.format("name: %s, type: %s, value: %s", tag.getTagName(), tag.getTagType(), tag.getDescription()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});
		}
		System.out.println(metadata);
	}

	public static void main(String[] args) throws Exception {
		Path path = Paths.get("/Users", "Jaewan Kim", "Pictures", "2011-01-15",
				"001.jpg");
		new MetadataReader().read2(path);
	}
}
