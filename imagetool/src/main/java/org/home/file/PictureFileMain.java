package org.home.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.BaseEncoding;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

public class PictureFileMain {

  private static final Logger logger = LoggerFactory.getLogger(PictureFileMain.class);

  public static void main(String[] args) {

    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e1) {
      throw new RuntimeException(e1);
    }

    MongoDao dao = new MongoDao();

    try (Stream<Path> paths = Files.walk(Paths.get("C:\\Users\\Jaewan Kim\\Pictures"))) {

      //@formatter:off
      Predicate<Path> dir = x -> x.toFile().isDirectory();
      Predicate<Path> file = x -> x.toFile().isFile();
      Predicate<Path> jpg = x -> x.toString().toLowerCase().endsWith("jpg");
      Predicate<Path> gif = x -> x.toString().toLowerCase().endsWith("gif");

      Predicate<Path> combined = file.and(jpg).or(gif);
      //@formatter:on

      Function<PictureFile, DBObject> convert = x -> {
        return BasicDBObjectBuilder.start()
                                   .add("name", x.path.toString())
                                   .add("md5", x.md5)
                                   .add("size", x.size)
                                   .get();
      };

      paths.filter(combined)
           .map(x -> {
             try {
               return new PictureFile(x, BaseEncoding.base16()
                                                     .encode((digest.digest(Files.readAllBytes(x)))), Files.size(x));
             } catch (Exception e) {
               throw new RuntimeException(e);
             }
           })
//           .peek(x -> logger.info(String.format("file: %s, checksum: %s, size: %d", x.path, x.md5, x.size)))
           .collect(Collectors.groupingBy(x->x.size))
           .entrySet()
           .stream()
           .filter(x-> x.getValue().size() > 1)
           .forEach(x-> {
             x.getValue().forEach(y->logger.info(y.path.toString()));
           });
//           .map(convert)
//           .forEach(dao::save);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
