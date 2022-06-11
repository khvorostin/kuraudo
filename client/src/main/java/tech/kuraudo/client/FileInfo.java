package tech.kuraudo.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Обёртка для данных о файле.
 */
public class FileInfo {

    public enum FileType {
        FILE("F"),
        DIR("D");

        private String name;

        FileType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private String fileName;
    private FileType type;
    private long size;
    private LocalDateTime lastModified;

    public FileInfo(Path path) {
        try {
            this.fileName = path.getFileName().toString();
            this.size = Files.size(path);
            this.type = Files.isDirectory(path) ? FileType.DIR : FileType.FILE;
            if (this.type == FileType.DIR) {
                this.size = -1;
            }
            this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(3));
        } catch (IOException e) {
            throw new RuntimeException("Unable to create file info from path");
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Object getValueByAttrName(String attribute) {
        switch (attribute) {
            case "Type" -> {
                return getType();
            }
            case "Name" -> {
                return getFileName();
            }
            case "Size" -> {
                return getSize();
            }
            case "Modified" -> {
                return getLastModified();
            }
        }

        return null;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }
}
