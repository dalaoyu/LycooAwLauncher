package com.lycoo.commons.util;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    public static boolean isMusicFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("mp3") || //
                    ext.equalsIgnoreCase("ogg") || //
                    ext.equalsIgnoreCase("wav") || //
                    ext.equalsIgnoreCase("wma") || //
                    ext.equalsIgnoreCase("m4a") || //
                    ext.equalsIgnoreCase("ape") || //
                    ext.equalsIgnoreCase("dts") || //
                    ext.equalsIgnoreCase("flac") || //
                    ext.equalsIgnoreCase("mp1") || //
                    ext.equalsIgnoreCase("mp2") || //
                    ext.equalsIgnoreCase("aac") || //
                    ext.equalsIgnoreCase("midi") || //
                    ext.equalsIgnoreCase("mid") || //
                    ext.equalsIgnoreCase("mp5") || //
                    ext.equalsIgnoreCase("mpga") || //
                    ext.equalsIgnoreCase("mpa") || //
                    ext.equalsIgnoreCase("m4p") || //
                    ext.equalsIgnoreCase("amr") || //
                    ext.equalsIgnoreCase("m4r")) //
            { //
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

        return false;
    }

    public static boolean isVideoFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("avi") || //
                    ext.equalsIgnoreCase("wmv") || //
                    ext.equalsIgnoreCase("rmvb") || //
                    ext.equalsIgnoreCase("mkv") || //
                    ext.equalsIgnoreCase("m4v") || //
                    ext.equalsIgnoreCase("mov") || //
                    ext.equalsIgnoreCase("mpg") || //
                    ext.equalsIgnoreCase("rm") || //
                    ext.equalsIgnoreCase("flv") || //
                    ext.equalsIgnoreCase("pmp") || //
                    ext.equalsIgnoreCase("vob") || //
//					ext.equalsIgnoreCase("dat") || //
                    ext.equalsIgnoreCase("asf") || //
                    ext.equalsIgnoreCase("psr") || //
                    ext.equalsIgnoreCase("3gp") || //
                    ext.equalsIgnoreCase("mpeg") || //
                    ext.equalsIgnoreCase("ram") || //
                    ext.equalsIgnoreCase("divx") || //
                    ext.equalsIgnoreCase("m4p") || //
                    ext.equalsIgnoreCase("m4b") || //
                    ext.equalsIgnoreCase("mp4") || //
                    ext.equalsIgnoreCase("f4v") || //
                    ext.equalsIgnoreCase("3gpp") || //
                    ext.equalsIgnoreCase("3g2") || //
                    ext.equalsIgnoreCase("3gpp2") || //
                    ext.equalsIgnoreCase("webm") || //
                    ext.equalsIgnoreCase("ts") || //
                    ext.equalsIgnoreCase("tp") || //
                    ext.equalsIgnoreCase("m2ts") || //
                    ext.equalsIgnoreCase("3dv") || //
                    ext.equalsIgnoreCase("3dm")) //
            { //
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

        return false;
    }

    public static boolean isPictureFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("png") || //
                    ext.equalsIgnoreCase("jpeg") || //
                    ext.equalsIgnoreCase("jpg") || //
                    ext.equalsIgnoreCase("gif") || //
                    ext.equalsIgnoreCase("bmp") || //
                    ext.equalsIgnoreCase("jfif") || //
                    ext.equalsIgnoreCase("tiff")) //
            { //
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

        return false;
    }

    public static boolean isAppFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("apk")) {
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

        return false;
    }

    public static boolean isTxtFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("txt")) {
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }

    public static boolean isPdfFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("pdf")) {
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }

    public static boolean isWordFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("doc") || ext.equalsIgnoreCase("docx")) {
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }

    public static boolean isExcelFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("xls") || ext.equalsIgnoreCase("xlsx")) {
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }

    public static boolean isPptFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("ppt") || ext.equalsIgnoreCase("pptx")) {
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }

    public static boolean isHtml32File(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("html")) {
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }

    public static boolean isApkFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("apk")) {
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }

    public static boolean isISOFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("iso")) {
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }

    public static boolean verifyFileByMD5(String md5, String file) {
        return MD5Utils.checkMd5(md5, file);
    }

    /**
     * 删除文件或者目录
     *
     * @param path 文件路径
     *
     *             Created by lancy on 2018/7/31 17:26
     */
    /*
    public synchronized static void deleteFile(String path) {
        deleteFile(new File(path));
    }
    */

    /*
    public synchronized static void deleteFile(File file) {
        if (!file.exists())
            return;

        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }

            // 递归删除
            for (File f : childFile) {
                deleteFile(f.toString());
            }

            file.delete();
        }
    }
    */

    /**
     * 删除文件
     *
     * @param path 文件路径
     * @return 删除成功返回true, 否则返回false
     *
     * Created by lancy on 2019/6/8 2:30
     */
    public synchronized static boolean deleteFile(String path) {
        return deleteFile(new File(path));
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return 删除成功返回true, 否则返回false
     *
     * Created by lancy on 2019/6/8 2:30
     */
    public synchronized static boolean deleteFile(File file) {
        if (!file.exists())
            return false;

        if (file.isFile()) {
            return file.delete();
        }

        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                return file.delete();
            }

            // 递归删除
            for (File f : childFile) {
                deleteFile(f.toString());
            }

            return file.delete();
        }

        return true;
    }

    /**
     * 获取文件名称
     *
     * @param file 文件全路径
     * @return 文件名
     *
     * Created by lancy on 2018/11/22 18:17
     */
    public synchronized static String getName(String file) {
        return getName(file, false);
    }

    /**
     * 获取文件名称
     *
     * @param file         文件全路径
     * @param expandedName 文件名是否包含扩展名
     * @return 文件名
     *
     * Created by lancy on 2018/7/31 17:45
     */
    public synchronized static String getName(String file, boolean expandedName) {
        if (file == null || file.isEmpty()) {
            return "";
        }

        if (file.contains("/")) {
            file = file.substring(file.lastIndexOf("/") + 1);
        }

        if (!expandedName && file.contains(".")) {
            file = file.substring(0, file.lastIndexOf("."));
        }

        return file;
    }

    /**
     * 查看文件属性
     *
     * Created by lancy on 2019/11/20 16:02
     */
    public static List<String> getFileAttributes(String file) {
        return getFileAttributes(new File(file));
    }

    /**
     * 查看文件属性
     *
     * 因为格式问题，目前仅仅返回3种文件信息：
     * attributes[0]: 文件类型+权限， 例如：drwxrwxr-x
     * attributes[1]: 文件所有者, 例如：system
     * attributes[2]: 文件所属组, 例如：media_rw
     *
     * 适配的方案：
     * -- V40_4.4
     * -- RK3128_4.4
     *
     * 例如目录：/mnt/sdcard/Movies
     * 字符串：drwxrwxr-x system   sdcard_rw          2019-11-05 17:28 Movies
     * 转换后：[drwxrwxr-x, system, sdcard_rw]
     *
     * Created by lancy on 2019/11/20 15:58
     */
    public static List<String> getFileAttributes(File file) {
        List<String> attributes = new ArrayList<>();

        if (file.exists()) {
            String cmd;
            if (file.isDirectory()) {
                cmd = "ls -ld " + file.getAbsolutePath();
            } else {
                cmd = "ls -l " + file.getAbsolutePath();
            }

            try {
                Process pp = Runtime.getRuntime().exec(cmd);
                InputStreamReader inputStreamReader = new InputStreamReader(pp.getInputStream());
                LineNumberReader lineNumberReader = new LineNumberReader(inputStreamReader);
                String data = lineNumberReader.readLine();
                LogUtils.debug(TAG, "Data: " + data);
                if (!TextUtils.isEmpty(data)) {
                    // 以空格进行截取
                    // String[] array = data.split("\u0020"); // 只能单个空格进行分割
                    String[] array = data.split("\\s+");      // 能连续空格进行分割
                    if (array.length >= 3) {
                        for (int i = 0; i < 3; i++) {
                            attributes.add(array[i]);
                        }

//                        for (String str : array) {
//                            if (!TextUtils.isEmpty(str)) { // 使用"\\s+"没必要判断
//                                attributes.add(str);
//                            }
//                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return attributes;
    }
}
