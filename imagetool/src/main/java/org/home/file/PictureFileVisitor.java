package org.home.file;

import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class PictureFileVisitor extends SimpleFileVisitor<Path> {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @SuppressWarnings("unchecked")
  public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
    File jpegFile = file.toFile();
    Metadata metadata;
    try {
      metadata = ImageMetadataReader.readMetadata(jpegFile);
      logger.debug(String.format("%s is a jpeg file.", jpegFile.getAbsolutePath()));
    } catch (ImageProcessingException e1) {
      logger.error(String.format("%s is not a jpeg file", jpegFile.getAbsolutePath()));
      return FileVisitResult.CONTINUE;
    }

    Iterator<Directory> iter = metadata.getDirectoryIterator();
    while (iter.hasNext()) {
      Directory dir = iter.next();
      dir.getTagIterator().forEachRemaining(x -> {
        Tag tag = ((Tag) x);
        try {
//          System.out.println(String.format("name: %s, type: %s, value: %s", tag.getTagName(), tag.getTagType(), tag.getDescription()));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }

      });
    }
    return FileVisitResult.CONTINUE;
  }
}
