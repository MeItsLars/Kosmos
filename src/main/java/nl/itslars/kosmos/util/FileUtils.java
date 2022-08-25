package nl.itslars.kosmos.util;


import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.io.Files;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

public class FileUtils {

    private FileUtils() {
    }

    public static boolean isSymbolicLink(File file) {
        try {
            File canonicalFile = file.getCanonicalFile();
            File absoluteFile = file.getAbsoluteFile();
            File parentFile = file.getParentFile();
            return !canonicalFile.getName().equals(absoluteFile.getName()) ||
                    parentFile != null && !parentFile.getCanonicalPath().equals(canonicalFile.getParent());
        } catch (IOException var4) {
            return true;
        }
    }

    public static ImmutableList<File> listFiles(File dir) {
        File[] files = dir.listFiles();
        return files == null ? ImmutableList.of() : ImmutableList.copyOf(files);
    }

    public static ImmutableList<File> listFiles(File dir, FilenameFilter filter) {
        File[] files = dir.listFiles(filter);
        return files == null ? ImmutableList.of() : ImmutableList.copyOf(files);
    }

    public static File createTempDir(String prefix) {
        return createTempDir(new File(System.getProperty("java.io.tmpdir")), prefix);
    }

    public static File createTempDir(File parentDir, String prefix) {
        String baseName = "";
        if (prefix != null) {
            baseName = baseName + prefix + "-";
        }

        baseName = baseName + System.currentTimeMillis() + "-";

        for (int counter = 0; counter < 10000; ++counter) {
            File tempDir = new File(parentDir, baseName + counter);
            if (tempDir.mkdir()) {
                return tempDir;
            }
        }

        throw new IllegalStateException(
                "Failed to create directory within 10000 attempts (tried " + baseName + "0 to " + baseName + 9999 +
                        ')');
    }

    public static boolean deleteDirectoryContents(File directory) {
        Preconditions.checkArgument(directory.isDirectory(), "Not a directory: %s", directory);
        if (isSymbolicLink(directory)) {
            return false;
        }
        else {
            boolean success = true;

            File file;
            for (UnmodifiableIterator<File> var2 = listFiles(directory).iterator(); var2.hasNext();
                 success = deleteRecursively(file) && success) {
                file = var2.next();
            }

            return success;
        }
    }

    public static boolean deleteRecursively(File file) {
        boolean success = true;
        if (file.isDirectory()) {
            success = deleteDirectoryContents(file);
        }

        return file.delete() && success;
    }

    public static boolean copyDirectoryContents(File src, File target) {
        Preconditions.checkArgument(src.isDirectory(), "Source dir is not a directory: %s", src);
        if (isSymbolicLink(src)) {
            return false;
        }
        else {
            target.mkdirs();
            Preconditions.checkArgument(target.isDirectory(), "Target dir is not a directory: %s", src);
            boolean success = true;

            File file;
            for (UnmodifiableIterator<File> var3 = listFiles(src).iterator(); var3.hasNext();
                 success = copyRecursively(file, new File(target, file.getName())) && success) {
                file = var3.next();
            }

            return success;
        }
    }

    public static boolean copyRecursively(File src, File target) {
        if (src.isDirectory()) {
            return copyDirectoryContents(src, target);
        }
        else {
            try {
                Files.copy(src, target);
                return true;
            } catch (IOException var3) {
                return false;
            }
        }
    }

    public static File newFile(String parent, String... paths) {
        Objects.requireNonNull(parent, "parent is null");
        Objects.requireNonNull(paths, "paths is null");
        return newFile(new File(parent), ImmutableList.copyOf(paths));
    }

    public static File newFile(File parent, String... paths) {
        Objects.requireNonNull(parent, "parent is null");
        Objects.requireNonNull(paths, "paths is null");
        return newFile(parent, ImmutableList.copyOf(paths));
    }

    public static File newFile(File parent, Iterable<String> paths) {
        Objects.requireNonNull(parent, "parent is null");
        Objects.requireNonNull(paths, "paths is null");
        File result = parent;

        String path;
        for (Iterator<String> var3 = paths.iterator(); var3.hasNext(); result = new File(result, path)) {
            path = var3.next();
        }

        return result;
    }
}
