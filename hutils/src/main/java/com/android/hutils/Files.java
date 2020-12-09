package com.android.hutils;

import android.text.TextUtils;
import android.util.Base64;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Files {

    /**
     * 算文件的MD5
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static String md5(File file) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        InputStream inputStream = new FileInputStream(file);
        int len;
        byte[] buffer = new byte[10240];
        while ((len = inputStream.read(buffer)) != -1) {
            md5.update(buffer, 0, len);
        }
        inputStream.close();
        return Base64.encodeToString(md5.digest(), Base64.NO_WRAP);
    }

    /**
     * 合并文件夹，将source文件夹内容合并到dest文件夹
     *
     * @param sourceDir 源文件夹
     * @param destDir 合并的目标文件夹
     * @param strategy 合并策略
     * @return
     * @throws Exception
     */
    public static void merge(File sourceDir, File destDir, int strategy) throws Exception {
        if (sourceDir == null || destDir == null || !sourceDir.exists() || !destDir.exists() || !sourceDir.isDirectory()
                || !destDir.isDirectory()) {
            return;
        }
        String targetPath = destDir.getAbsolutePath();
        File[] files = sourceDir.listFiles();
        for (File file : files) {
            realMerge(file, targetPath, strategy);
        }
    }

    private static void realMerge(File file, String targetPath, int strategy) throws Exception {
        String newTargetpath = targetPath + File.separator + file.getName();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File childFile : files) {
                realMerge(childFile, newTargetpath, strategy);
            }
            File folder = new File(newTargetpath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
        } else if (file.isFile()) {
            File newFile = new File(newTargetpath);
            if (newFile.exists()) {
                switch (strategy) {
                    case MergeStrategy.COVER:
                        newFile.delete();
                        mergeFile(file, newFile);
                        break;
                    case MergeStrategy.SKIP:
                        return;
                    default:
                        break;
                }
            } else {
                mergeFile(file, newFile);
            }
        }
    }

    private static void mergeFile(File sourceFile, File destFile) throws Exception {
        if (sourceFile == null || destFile == null) {
            return;
        }
        File parent = destFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel out = new FileOutputStream(destFile).getChannel();
        FileChannel in = new FileInputStream(sourceFile).getChannel();
        in.transferTo(0, in.size(), out);
        in.close();
        out.close();
    }

    /**
     * 复制文件夹，将source文件夹内容复制到dest文件夹
     *
     * @param source
     * @param dest
     * @throws Exception
     */
    public static void copy(File source, File dest) throws Exception {
        if (source == null || dest == null || !source.exists() || !source.isDirectory()) {
            return;
        }
        if (dest.exists()) {
            deleteAllFiles(dest);
        }
        if (!dest.exists()) {
            dest.mkdirs();
        }
        File[] files = source.listFiles();
        String targetPath = dest.getAbsolutePath();
        for (File file : files) {
            realMerge(file, targetPath, MergeStrategy.COVER);
        }
    }

    /**
     * 清空文件夹
     *
     * @param folder
     */
    public static void clearFolder(File folder) {
        if (folder == null || !folder.exists() || !folder.isDirectory()) {
            return;
        }
        File[] files = folder.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                deleteAllFiles(file);
            }
        }
    }

    /**
     * 删除文件夹及其下所有的文件
     *
     * @param file
     */
    public static void deleteAllFiles(File file) {
        if (!file.isDirectory()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    deleteAllFiles(f);
                }
            }
            file.delete();
        }
    }

    /**
     * 文件合并策略
     */
    public interface MergeStrategy {

        public static final int SKIP = 1;
        public static final int COVER = 2;
    }

    /**
     * 解压文件
     *
     * @param file
     * @return
     */
    public static File unZip(File file) {
        if (file == null || !file.exists() || !file.getName().endsWith(".zip")) {
            return null;
        }
        try {
            String root = file.getAbsoluteFile().getParent() + "/";
            File folder = new File(root);
            ZipInputStream zipis = new ZipInputStream(new FileInputStream(file));
            ZipEntry fentry = null;
            while ((fentry = zipis.getNextEntry()) != null) {
                if (fentry.isDirectory()) {
                    File dir = new File(root + fentry.getName());
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                } else {
                    String fname = root + fentry.getName();
                    File childFile = new File(fname);
                    if (!childFile.getParentFile().exists()) {
                        childFile.getParentFile().mkdirs();
                    }
                    if (!childFile.exists()) {
                        childFile.createNewFile();
                    }
                    FileOutputStream out = new FileOutputStream(childFile);
                    BufferedOutputStream bufferOut = new BufferedOutputStream(out);
                    byte[] doc = new byte[2048];
                    int n;
                    while ((n = zipis.read(doc, 0, 2048)) != -1) {
                        bufferOut.write(doc, 0, n);
                    }
                    bufferOut.close();
                    out.close();
                }
            }
            zipis.close();
            return folder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 压缩文件
     *
     * @param input 要压缩的文件路径
     * @param output 输出压缩文件路径
     * @return 返回成功或失败
     */
    public static boolean zip(String input, String output) {
        if (TextUtils.isEmpty(input) || TextUtils.isEmpty(output)) {
            return false;
        }
        ZipOutputStream outZip = null;
        try {
            File inFile = new File(input);
            File outFile = new File(output);
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }
            outZip = new ZipOutputStream(new FileOutputStream(outFile));
            zipFile(inFile.getParent(), inFile.getName(), outZip);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outZip != null) {
                try {
                    outZip.flush();
                    outZip.finish();
                    outZip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private static void zipFile(String root, String relativePath, ZipOutputStream zipOutputSteam) {
        if (zipOutputSteam == null) {
            return;
        }
        FileInputStream inputStream = null;
        try {
            File file = new File(root, relativePath);
            if (file.isFile()) {
                ZipEntry zipEntry = new ZipEntry(relativePath);
                inputStream = new FileInputStream(file);
                zipOutputSteam.putNextEntry(zipEntry);
                int len;
                byte[] buffer = new byte[4096];
                while ((len = inputStream.read(buffer)) != -1) {
                    zipOutputSteam.write(buffer, 0, len);
                }
                zipOutputSteam.closeEntry();
            } else {
                String[] list = file.list();
                if (list.length <= 0) {
                    ZipEntry zipEntry = new ZipEntry(relativePath + File.separator);
                    zipOutputSteam.putNextEntry(zipEntry);
                    zipOutputSteam.closeEntry();
                } else {
                    for (String item : list) {
                        zipFile(root, relativePath + File.separator + item, zipOutputSteam);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
