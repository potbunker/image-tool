package org.home.file;

import java.nio.file.Path;

public class PictureFile {
  public final String md5;
  public final Path path;
  public final long size;
  
  public PictureFile(Path path, String md5, long size) {
    super();
    this.path = path;
    this.md5 = md5;
    this.size = size;
  }
}
