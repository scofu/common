package com.scofu.common.misc;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/** Paper classpath extractor. */
public class PaperClasspathExtractor {

  /**
   * Main.
   *
   * @param args args
   * @throws IOException io exception
   */
  public static void main(String[] args) throws IOException {
    final var classpathDirectory = new File("classpath");
    classpathDirectory.mkdir();

    final var librariesDirectory = new File("libraries").toPath().toAbsolutePath();
    final var versionsDirectory = new File("versions").toPath().toAbsolutePath();

    final var targetPathExtractor =
        new AtomicReference<Function<Path, String>>(path -> path.getFileName().toString());

    final var visitor =
        new SimpleFileVisitor<Path>() {
          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
              throws IOException {
            if (file.toString().endsWith(".jar")) {
              final var targetFile =
                  new File(classpathDirectory, targetPathExtractor.get().apply(file))
                      .toPath()
                      .toAbsolutePath();
              System.out.println("Copying " + file.getFileName());
              Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return FileVisitResult.CONTINUE;
          }
        };

    System.out.println("Copying from " + librariesDirectory);
    Files.walkFileTree(librariesDirectory, visitor);

    System.out.println("Copying from " + versionsDirectory);
    targetPathExtractor.set(file -> "0-" + file.getFileName().toString());
    Files.walkFileTree(versionsDirectory, visitor);

    System.out.println("Done");
  }
}
