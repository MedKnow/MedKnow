package org.example.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class BackupUtil {
    public static void backupFile(String filename) {
        File source = new File(filename);
        if (!source.exists()) {
            return;
        }
        File backup = new File(filename + ".bak");
        try {
            Files.copy(source.toPath(), backup.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("备份文件失败: " + filename + " -> " + e.getMessage());
        }
    }
}