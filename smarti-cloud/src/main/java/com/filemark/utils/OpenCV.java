package com.filemark.utils;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class OpenCV {

  private final static Logger logger = Logger.getLogger(OpenCV.class.getName());
  static String NATIVE_LIBRARY_NAME ="opencv_java300";
  static enum OS {
    OSX("^[Mm]ac OS X$"),
    LINUX("^[Ll]inux$"),
    WINDOWS("^[Ww]indows.*");

    private final Set<Pattern> patterns;

    private OS(final String... patterns) {
      this.patterns = new HashSet<Pattern>();

      for (final String pattern : patterns) {
        this.patterns.add(Pattern.compile(pattern));
      }
    }

    private boolean is(final String id) {
      for (final Pattern pattern : patterns) {
        if (pattern.matcher(id).matches()) {
          return true;
        }
      }
      return false;
    }

    public static OS getCurrent() {
      final String osName = System.getProperty("os.name");

      for (final OS os : OS.values()) {
        if (os.is(osName)) {
          logger.log(Level.FINEST, "Current environment matches operating system descriptor \"{0}\".", os);
          return os;
        }
      }

      throw new UnsupportedOperationException(String.format("Operating system \"%s\" is not supported.", osName));
    }
  }

  static enum Arch {
	AEM_32("arm","arm7","arm53"),  
    X86_32("i386", "i686","x86"),
    X86_64("amd64", "x86_64");

    private final Set<String> patterns;

    private Arch(final String... patterns) {
      this.patterns = new HashSet<String>(Arrays.asList(patterns));
    }

    private boolean is(final String id) {
      return patterns.contains(id);
    }

    public static Arch getCurrent() {
      final String osArch = System.getProperty("os.arch");

      for (final Arch arch : Arch.values()) {
        if (arch.is(osArch)) {
          logger.log(Level.FINEST, "Current environment matches architecture descriptor \"{0}\".", arch);
          return arch;
        }
      }

      throw new UnsupportedOperationException(String.format("Architecture \"%s\" is not supported.", osArch));
    }
  }

  private static class UnsupportedPlatformException extends RuntimeException {
    private UnsupportedPlatformException(final OS os, final Arch arch) {
      super(String.format("Operating system \"%s\" and architecture \"%s\" are not supported.", os, arch));
    }
  }

  private static class TemporaryDirectory {
    final Path path;

    public TemporaryDirectory() {
      try {
        path = Files.createTempDirectory("");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    public Path getPath() {
      return path;
    }

    public TemporaryDirectory markDeleteOnExit() {
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          delete();
        }
      });

      return this;
    }

    public void delete() {
      if (!Files.exists(path)) {
        return;
      }

      try {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
          @Override
          public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
              throws IOException {
            Files.deleteIfExists(dir);
            return super.postVisitDirectory(dir, e);
          }

          @Override
          public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
              throws IOException {
            Files.deleteIfExists(file);
            return super.visitFile(file, attrs);
          }
        });
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

  }

  /**
   * Exactly once per {@link ClassLoader}, attempt to load the native library (via {@link System#loadLibrary(String)} with {@link Core#NATIVE_LIBRARY_NAME}). If the first attempt fails, the native binary will be extracted from the classpath to a temporary location (which gets cleaned up on shutdown), that location is added to the {@code java.library.path} system property and {@link ClassLoader#usr_paths}, and then another call to load the library is made. Note this method uses reflection to gain access to private memory in {@link ClassLoader} as there's no documented method to augment the library path at runtime. Spurious calls are safe.
   */
  public static void loadShared() {
    SharedLoader.getInstance();
  }

  public static void loadShared(String lib_name) {
	    new SharedLoader(lib_name);
	  }  
  /**
   * @see <a href="http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom">Initialization-on-demand holder idiom</a>
   */
  private static class SharedLoader {
    private Path libraryPath;
    private String lib_name;

    private SharedLoader() {
      try {
        System.loadLibrary(NATIVE_LIBRARY_NAME);
        logger.log(Level.FINEST, "Loaded existing OpenCV library \"{0}\" from library path.", NATIVE_LIBRARY_NAME);
      } catch (final UnsatisfiedLinkError ule) {

        /* Only update the library path and load if the original error indicates it's missing from the library path. */
        if (!String.format("no %s in java.library.path", NATIVE_LIBRARY_NAME).equals(ule.getMessage())) {
          logger.log(Level.FINEST, String.format("Encountered unexpected loading error."), ule);
          throw ule;
        }

        /* Retain this path for cleaning up the library path later. */
        this.libraryPath = extractNativeBinary();

        addLibraryPath(libraryPath.getParent());
        System.loadLibrary(NATIVE_LIBRARY_NAME);

        logger.log(Level.FINEST, "OpenCV library \"{0}\" loaded from extracted copy at \"{1}\".", new Object[]{NATIVE_LIBRARY_NAME, System.mapLibraryName(NATIVE_LIBRARY_NAME)});
      }
    }
    
    public SharedLoader(String lib_name) {
    	this.lib_name = lib_name;
        try {
          System.loadLibrary(lib_name);
          logger.log(Level.FINEST, "Loaded existing OpenCV library \"{0}\" from library path.", lib_name);
        } catch (final UnsatisfiedLinkError ule) {

          /* Only update the library path and load if the original error indicates it's missing from the library path. */
          if (!String.format("no %s in java.library.path", lib_name).equals(ule.getMessage())) {
            logger.log(Level.FINEST, String.format("Encountered unexpected loading error."), ule);
            throw ule;
          }

          /* Retain this path for cleaning up the library path later. */
          this.libraryPath = extractNativeBinary(lib_name);

          addLibraryPath(libraryPath.getParent());
          System.load(this.libraryPath.toString());

          logger.log(Level.FINEST, "OpenCV library \"{0}\" loaded from extracted copy at \"{1}\".", new Object[]{lib_name, System.mapLibraryName(lib_name)});
        }     
    }
    /**
     * Cleans up patches done to the environment.
     */
    @Override
    protected void finalize() throws Throwable {
      super.finalize();

      if (null == libraryPath) {
        return;
      }

      removeLibraryPath(libraryPath.getParent());
    }

    private static class Holder {
      private static final SharedLoader INSTANCE = new SharedLoader();
    }

    public static SharedLoader getInstance() {
      return Holder.INSTANCE;
    }

    /**
     * Adds the provided {@link Path}, normalized, to the {@link ClassLoader#usr_paths} array, as well as to the {@code java.library.path} system property. Uses the reflection API to make the field accessible, and may be unsafe in environments with a security policy.
     *
     * @see <a href="http://stackoverflow.com/q/15409223">Adding new paths for native libraries at runtime in Java</a>
     */
    private static void addLibraryPath(final Path path) {
      final String normalizedPath = path.normalize().toString();

      try {
        final Field field = ClassLoader.class.getDeclaredField("usr_paths");
        field.setAccessible(true);

        final Set<String> userPaths = new HashSet<>(Arrays.asList((String[]) field.get(null)));
        userPaths.add(normalizedPath);

        field.set(null, userPaths.toArray(new String[userPaths.size()]));

        System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + normalizedPath);

        logger.log(Level.FINEST, "System library path now \"{0}\".", System.getProperty("java.library.path"));
      } catch (IllegalAccessException e) {
        throw new RuntimeException("Failed to get permissions to set library path");
      } catch (NoSuchFieldException e) {
        throw new RuntimeException("Failed to get field handle to set library path");
      }
    }

    /**
     * Removes the provided {@link Path}, normalized, from the {@link ClassLoader#usr_paths} array, as well as to the {@code java.library.path} system property. Uses the reflection API to make the field accessible, and may be unsafe in environments with a security policy.
     */
    private static void removeLibraryPath(final Path path) {
      final String normalizedPath = path.normalize().toString();

      try {
        final Field field = ClassLoader.class.getDeclaredField("usr_paths");
        field.setAccessible(true);

        final Set<String> userPaths = new HashSet<>(Arrays.asList((String[]) field.get(null)));
        userPaths.remove(normalizedPath);

        field.set(null, userPaths.toArray(new String[userPaths.size()]));

        System.setProperty("java.library.path", System.getProperty("java.library.path").replace(File.pathSeparator + path.normalize().toString(), ""));
      } catch (IllegalAccessException e) {
        throw new RuntimeException("Failed to get permissions to set library path");
      } catch (NoSuchFieldException e) {
        throw new RuntimeException("Failed to get field handle to set library path");
      }
    }
  }

  /**
   * Exactly once per {@link ClassLoader}, extract the native binary from the classpath to a temporary location (which gets cleaned up on shutdown), and load that binary (via {@link System#load(String)}). Spurious calls are safe.
   */
  public static void loadLocally() {
    LocalLoader.getInstance();
  }

  private static class LocalLoader {
    private LocalLoader() {
      /* Retain this path for cleaning up later. */
      final Path libraryPath = extractNativeBinary();
      System.load(libraryPath.normalize().toString());

      logger.log(Level.FINEST, "OpenCV library \"{0}\" loaded from extracted copy at \"{1}\".", new Object[]{NATIVE_LIBRARY_NAME, System.mapLibraryName(NATIVE_LIBRARY_NAME)});
    }

    private static class Holder {
      private static final LocalLoader INSTANCE = new LocalLoader();
    }

    public static LocalLoader getInstance() {
      return Holder.INSTANCE;
    }
  }

  /**
   * Selects the appropriate packaged binary, extracts it to a temporary location (which gets deleted when the JVM shuts down), and returns a {@link Path} to that file.
   */
  //@CallerSensitive
  private static Path extractNativeBinary() {
    final OS os = OS.getCurrent();
    final Arch arch = Arch.getCurrent();
    return extractNativeBinary(os, arch);
  }

  /**
   * Extracts the packaged binary for the specified platform to a temporary location (which gets deleted when the JVM shuts down), and returns a {@link Path} to that file.
   */
  private static Path extractNativeBinary(final OS os, final Arch arch) {
    final String location;

    switch (os) {
    	case WINDOWS:
        switch (arch) {
          case X86_32:
            location = "/nu/pattern/opencv/windows/x86_32/opencv_java320.dll";
            break;
          case X86_64:
            location = "/nu/pattern/opencv/windows/x86_64/opencv_java320.dll";
            break;
          default:
            throw new UnsupportedPlatformException(os, arch);
        }
        break;

    	case LINUX:
        switch (arch) {
        case AEM_32:
            location = "/org/bytedeco/javacpp/linux-arm";
            break;        
          case X86_32:
            location = "/nu/pattern/opencv/linux/x86_32/opencv_java320.so";
            break;
          case X86_64:
            location = "/nu/pattern/opencv/linux/x86_64/opencv_java320.so";
            break;
          default:
            throw new UnsupportedPlatformException(os, arch);
        }
        break;
      case OSX:
        switch (arch) {
          case X86_64:
            location = "/nu/pattern/opencv/osx/x86_64/libopencv_java249.dylib";
            break;
          default:
            throw new UnsupportedPlatformException(os, arch);
        }
        break;
      default:
        throw new UnsupportedPlatformException(os, arch);
    }

    logger.log(Level.FINEST, "Selected native binary \"{0}\".", location);

    final InputStream binary = OpenCV.class.getResourceAsStream(location);
    final Path destination = new TemporaryDirectory().markDeleteOnExit().getPath().resolve("./" + location).normalize();

    try {
      logger.log(Level.FINEST, "Copying native binary to \"{0}\".", destination);
      Files.createDirectories(destination.getParent());
      Files.copy(binary, destination);
    } catch (final IOException ioe) {
      throw new IllegalStateException(String.format("Error writing native library to \"%s\".", destination), ioe);
    }

    logger.log(Level.FINEST, "Extracted native binary to \"{0}\".", destination);

    return destination;
  }
  
  private static Path extractNativeBinary(final String name) {
	    final String location;
	    final OS os = OS.getCurrent();
	    final Arch arch = Arch.getCurrent();
	    switch (os) {
	    	case WINDOWS:
	        switch (arch) {
	          case X86_32:
	            location = "/nu/pattern/opencv/windows/x86_32/"+name+".dll";
	            break;
	          case X86_64:
	            location = "/nu/pattern/opencv/windows/x86_64/"+name+".dll";
	            break;
	          default:
	            throw new UnsupportedPlatformException(os, arch);
	        }
	        break;

	    	case LINUX:
	        switch (arch) {
	        case AEM_32:
	            location = "/org/bytedeco/javacpp/linux-arm/"+name+".so";
	            break;        
	          case X86_32:
	            location = "/nu/pattern/opencv/linux/x86_32/"+name+".so";
	            break;
	          case X86_64:
	            location = "/nu/pattern/opencv/linux/x86_64/"+name+".so";
	            break;
	          default:
	            throw new UnsupportedPlatformException(os, arch);
	        }
	        break;
	      case OSX:
	        switch (arch) {
	          case X86_64:
	            location = "/nu/pattern/opencv/osx/x86_64/"+name+".dylib";
	            break;
	          default:
	            throw new UnsupportedPlatformException(os, arch);
	        }
	        break;
	      default:
	        throw new UnsupportedPlatformException(os, arch);
	    }

	    logger.log(Level.FINEST, "Selected native binary \"{0}\".", location);

	    final InputStream binary = OpenCV.class.getResourceAsStream(location);
	    final Path destination = new TemporaryDirectory().markDeleteOnExit().getPath().resolve("./" + location).normalize();

	    try {
	      logger.log(Level.FINEST, "Copying native binary to \"{0}\".", destination);
	      Files.createDirectories(destination.getParent());
	      Files.copy(binary, destination);
	    } catch (final IOException ioe) {
	      throw new IllegalStateException(String.format("Error writing native library to \"%s\".", destination), ioe);
	    }

	    logger.log(Level.FINEST, "Extracted native binary to \"{0}\".", destination);

	    return destination;
	  }  
}