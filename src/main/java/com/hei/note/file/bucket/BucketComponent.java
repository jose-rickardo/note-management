package com.hei.note.file.bucket;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BucketComponent {

  private final Path root;

  public BucketComponent(@Value("${local.bucket.dir:/tmp/note-management-bucket}") String dir) {
    this.root = Path.of(dir);
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new RuntimeException("Could not create local bucket directory: " + dir, e);
    }
  }

  public String upload(File file, String objectKey) {
    try {
      var target = root.resolve(objectKey);
      Files.createDirectories(target.getParent());
      Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
      return objectKey;
    } catch (IOException e) {
      throw new RuntimeException("Local stub upload failed for key " + objectKey, e);
    }
  }

  public byte[] download(String objectKey) {
    try {
      return Files.readAllBytes(root.resolve(objectKey));
    } catch (IOException e) {
      throw new RuntimeException("Local stub download failed for key " + objectKey, e);
    }
  }
}
