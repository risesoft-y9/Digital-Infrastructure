package com.fabriceci.fmc.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    private static final int BUFFER_SIZE = 4096;

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    static public byte[] zipFolder(File dir) throws IOException {

        ZipOutputStream zout = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        try {
            zout = new ZipOutputStream(bout);
            zipFile(dir, dir.getName(), zout);
            zout.close();
            return bout.toByteArray();
        } finally {
            if (zout != null) {
                zout.flush();
                zout.close();
            }
            if (bout != null) {
                bout.close();
            }
        }
    }
}
