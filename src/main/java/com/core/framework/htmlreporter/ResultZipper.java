package com.core.framework.htmlreporter;

import com.core.framework.constant.ReportingConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Slf4j
class ResultZipper {
    private static List<String> fileList = new ArrayList<String>();
    private static String OUTPUT_ZIP_FILE;
    private static String SOURCE_FOLDER;

    public static void zipResults(String reportingFolder) {
        log.trace("zipResults called with {}, should we zip it?{}", reportingFolder, ReportingConstants.ZIP_RESULTS);
        if (ReportingConstants.ZIP_RESULTS) {
            SOURCE_FOLDER = reportingFolder;
            OUTPUT_ZIP_FILE = reportingFolder + "TestResults.zip";
            generateFileList(new File(SOURCE_FOLDER));
            zipIt(OUTPUT_ZIP_FILE);
        }
    }

    private static void zipIt(String zipFile) {
        byte[] buffer = new byte[1024];
        String source = new File(SOURCE_FOLDER).getName();
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);

            System.out.println("Output to Zip : " + zipFile);
            FileInputStream in = null;

            for (String file : fileList) {
                System.out.println("File Added : " + file);
                ZipEntry ze = new ZipEntry(source + File.separator + file);
                zos.putNextEntry(ze);
                try {
                    in = new FileInputStream(SOURCE_FOLDER + File.separator + file);
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                } finally {
                    if(in!=null){
                        in.close();
                    }
                }
            }

            zos.closeEntry();
            System.out.println("Folder successfully compressed");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void generateFileList(File node) {
        // add file only
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
                generateFileList(new File(node, filename));
            }
        }
    }

    private static String generateZipEntry(String file) {
        return file.substring(SOURCE_FOLDER.length(), file.length());
    }
}
