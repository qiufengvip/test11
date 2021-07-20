/**
 * Package:com.goldcitynet.util;
 * $Id: FileUtil.java,v 1.9 2009/05/15 01:22:32 xuh Exp $
 * Copyright(c) 2001-2005 www.afteryuan.com
 */
package cn.kmpro.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.AccessDeniedException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

/**
 * FileUtil,文件操作工具
 */
public class FileUtil {
    private static final Log log = LogFactory.getLog(FileUtil.class);

    /**
     * 创建任意深度的文件所在文件夹,可以用来替代直接new File(path)。(注意是传入的参数是文件,不是文件夹)
     *
     * @param path 文件(不是文件夹)
     * @return File对象 (文件夹,不是文件)
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public static File createFileDir(String path) throws AccessDeniedException {
        File file = new File(path);
        //寻找父目录是否存在
        File parent = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator)));
        //如果父目录不存在，则递归寻找更上一层目录
        if (!parent.exists()) {
            createFileDir(parent.getPath());
            //创建父目录
            boolean success = parent.mkdirs();
            if (!success) throw new AccessDeniedException(parent.getAbsolutePath(), "不能创建父文件夹！", "可能没有读写权限");
        }
        return file;
    }

    public static File createFile(String path) throws AccessDeniedException {
        createFileDir(path);
        File f = new File(path);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    /**
     * 复制文件
     *
     * @param source
     * @param dest
     * @throws IOException
     */
    public static void copyFile(File source, File dest) {
        FileOutputStream fos = null;
        FileInputStream fis = null;
        try {
            fos = new FileOutputStream(dest);
            fis = new FileInputStream(source);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            //ignored
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(fos);
        }
    }

    /**
     * 获取文件夹下所有一级文件和文件夹名
     *
     * @param file 文件夹路径
     * @return String[] 文件或文件夹名数组
     */
    public static String[] getFileList(File file) {
        String[] files = null;
        if (file.isDirectory()) {
            files = file.list();
        }
        return files;
    }

    /**
     * 根据文件路径返回文件所在的上一层文件夹的路径
     *
     * @param filePath
     * @return
     */
    public static String lastUpperPath(String filePath) {
        return filePath.substring(0, filePath.lastIndexOf("/"));
    }

    /**
     * 重命名文件,采用uuid的算法
     *
     * @param originalName 原始文件名
     * @return 新文件名
     */
    public static String uuidFileName(String originalName) {
        return UUID.randomUUID() + originalName.substring(originalName.lastIndexOf("."), originalName.length());
    }

    /**
     * 根据原始文件名,返回一个服务器保持的文件路径
     * 命名规则为 yyyy/mm/dd
     *
     * @return
     */
    public static String dateStylePath() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);    //获取年
        int month = c.get(Calendar.MONTH) + 1;   //获取月份，0表示1
        int day = c.get(Calendar.DAY_OF_MONTH);    //获取当前天数
        return String.valueOf(year) + File.separator + String.valueOf(month) + File.separator + String.valueOf(day);
    }

    /**
     * 根据路径解析文件名,采用系统默认的文件分隔符切割
     *
     * @param path 待解析的路径
     * @return
     */
    public static String parseFileNameFromPath(String path) {
        return parseFileNameFromPath(path, File.separator);
    }

    /**
     * 根据路径解析文件名,采用系统默认的文件分隔符切割
     *
     * @param path 待解析的路径
     * @return
     */
    public static String parseFileNameFromPath(String path, String separator) {
        return path.substring(path.lastIndexOf(separator) + 1, path.length());
    }

    /**
     * 根据文件名，解析文件扩展名
     *
     * @param name 待解析的路径
     * @return
     */
    public static String parseExtendName(String name) {
        return name.substring(name.lastIndexOf(".") + 1, name.length());
    }

    /**
     * 根据文件路径复制到相同目录，返回新文件路径
     *
     * @return String, String oldDir, String newDir
     */
    public static String copyFileFromPath(String oldPath, String newPath) throws IOException {
        File newFile = new File(newPath);
        File oldFile = new File(oldPath);
        copyFile(oldFile, newFile);
        return newPath;
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }


    /**
     * 列出文件夹及其子文件夹下所有符合条件的文件
     *
     * @param filePath
     * @param fileVec
     * @param fileFilter
     * @return
     */
    private static void listAllFile(String filePath, List<File> fileVec, FileFilter fileFilter) {
        File tempdir = new File(filePath);
        File[] filelist = tempdir.listFiles();//不能加filter，加了filter就不会向下找子目录了
        if (filelist != null) {
            for (File fileP : filelist) {
                //System.out.println(file);
                if (fileP.isFile() && fileFilter.accept(fileP)) {
                    fileVec.add(fileP);
                } else if (fileP.isDirectory()) {
                    listAllFile(fileP.getPath(), fileVec, fileFilter);
                }
            }
        } else log.info("路径不存在:" + filePath);


    }

    /**
     * 列出文件夹及其子文件夹下所有符合条件的文件
     *
     * @param filePath
     * @param fileFilter
     * @return
     */
    public static List<File> listAllFile(String filePath, FileFilter fileFilter) {
        List<File> fileVec = new ArrayList<File>();
        listAllFile(filePath, fileVec, fileFilter);
        return fileVec;
    }

    /**
     * Determine whether a file or directory is actually a symbolic link.
     *
     * @param file the file or directory to check
     * @return true if so
     */
    public static boolean isLink(final File file) {
        try {
            String os = System.getProperty("os.name");
            if (os.indexOf("Windows") >= 0) {
                return false;
            }
            if (file == null || !file.exists()) {
                return false;
            } else {
                String cnnpath = file.getCanonicalPath();
                String abspath = file.getAbsolutePath();
//                log.debug("comparing " + cnnpath + " and " + abspath);
                return !abspath.equals(cnnpath);
            }
        } catch (IOException e) {
            log.warn("could not determine whether " + file.getAbsolutePath() + " is a symbolic link", e);
            return false;
        }
    }

    /**
     * Recursively remove a directory.
     *
     * @param sourceDir the Directory to be removed
     * @return true on success, false otherwise.
     * <p/>
     */
    public static boolean removeDir(final File sourceDir) {
        // try {
        // org.apache.commons.io.FileUtils.deleteDirectory(sourceDir);
        // } catch (IOException e) {
        // log.warn("could not delete " + sourceDir, e);
        // return false;
        // }
        // log.debug("Succesfully removed directory: " + sourceDir);
        // return true;

        if (sourceDir == null) {
            return false;
        }

        boolean allsuccess = true;
        boolean success = true;
        int nrOfFilesDeleted = 0;
        int nrOfDirsDeleted = 0;

        if (sourceDir.isDirectory()) {
            File[] files = sourceDir.listFiles();

            // I've seen listFiles return null, so be carefull, guess dir names too long for OS
            if (files == null) {
                log.warn("Something funny with '" + sourceDir + "'. Name or path too long?");
                log.warn("Could not delete '" + sourceDir + "' from cache");

                // see whether we can rename the dir
                if (sourceDir.renameTo(new File(sourceDir.getParent(), "1"))) {
                    log.warn("Renamed '" + sourceDir + "'");

                    return removeDir(sourceDir); // try again
                } else {
                    log.warn("Could not rename '" + sourceDir + "' to '" + sourceDir.getParent() + "1'");
                }

                return false;
            }

//            log.debug(sourceDir + ": is a directory with " + files.length + " docs");

            for (int i = 0; i < files.length; i++) {
//                log.debug("removing " + files[i]);

                if (files[i].isDirectory()) {
                    success = removeDir(files[i]);
                } else {
                    success = files[i].delete();
                }

                if (!success) {
                    log.warn("could not delete " + files[i] + " from cache");
                } else {
                    nrOfFilesDeleted++;
                }

                allsuccess = allsuccess && success;
            }

//            log.debug("removing " + sourceDir);
            success = sourceDir.delete();

            if (!success) {
                log.warn("could not delete " + sourceDir + " from cache");
            } else {
                nrOfDirsDeleted++;
            }

            allsuccess = allsuccess && success;
        }

        // TODO: make this info at outer level of recursion
        log.debug("Deleted: " + nrOfDirsDeleted + " directories and " + nrOfFilesDeleted + " files from " + sourceDir);
        log.debug("Exiting removeDir for: " + sourceDir + ", " + allsuccess);

        return allsuccess;
    }

    /**
     * Determine whether File is somewhere within Directory.
     *
     * @param file the File.
     * @param dir  the Directory.
     * @return true, if so.
     */
    public static boolean isIn(final File file, final File dir) {
        if ((file == null) || !file.isFile()) {
            return false;
        }

        if ((dir == null) || !dir.isDirectory()) {
            return false;
        }

        String fileString;
        String directoryString;

        try {
            directoryString = dir.getCanonicalPath();
            fileString = file.getCanonicalPath();

            return fileString.startsWith(directoryString);
        } catch (IOException e) {
            log.error("Can't determine whether file is in Dir", e);
        }

        return false;
    }

    /**
     * Get the casesensitive extension (without the '.') of a file.
     *
     * @param sourceFile the File the extension is extracted from.
     * @return extension, empty string if no extension.
     */
    public static String getExtension(final File sourceFile) {
        if (sourceFile == null) {
            return "";
        }

        return getExtension(sourceFile.getName());
    }

    /**
     * Get the casesensitive extension (without the '.') of a file.
     *
     * @param sourceFile the File the extension is extracted from.
     * @return extension, empty string if no extension.
     */
    public static String getExtension(String sourceFile) {
        int index = sourceFile.lastIndexOf('.');
        if (index != -1) {
            return sourceFile.substring(index + 1);
        }
        return "";
    }

    /**
     * Get the getNameWithoutExtension extension (without the '.') of a file.
     *
     * @param sourceFile the File the extension is extracted from.
     * @return extension, empty string if no extension.
     */
    public static String getNameWithoutExtension(String sourceFile) {
        int index = sourceFile.lastIndexOf('.');
        if (index != -1) {
            return sourceFile.substring(0, index);
        }
        return "";
    }

    /**
     * Get the casesensitive extension (without the '.') of a file.
     *
     * @param sourceFile the File the extension is extracted from.
     * @return extension, empty string if no extension.
     */
    public static String getExtensionLowerCase(String sourceFile) {
        int index = sourceFile.lastIndexOf('.');
        if (index != -1) {
            return sourceFile.substring(index + 1).toLowerCase();
        }
        return "";
    }

    /**
     * Create a new directory in the given directory, with prefix and postfix.
     *
     * @param sourceFile the sourceFile to use for the new directory
     * @param dir        the (existing) directory to create the directory in.
     * @return newly created Directory or null.
     * @throws IOException directory can't be created
     */
    public static File createTempDir(final File sourceFile, final File dir) throws IOException {
        File unZipDestinationDirectory = null;

        try {
            // get the full path (not just the name, since we could have recursed into newly created directory)
            String destinationDirectory = sourceFile.getCanonicalPath();

//            log.debug("destinationDirectory: " + destinationDirectory);

            // change extension into _
            int index = destinationDirectory.lastIndexOf('.');
            String extension;

            if (index != -1) {
                extension = destinationDirectory.substring(index + 1);
                destinationDirectory = destinationDirectory.substring(0, index) + '_' + extension;
            }

            // actually create the directory
            unZipDestinationDirectory = new File(destinationDirectory);
            boolean canCreate = unZipDestinationDirectory.mkdirs();

            if (!canCreate) {
                log.warn("Could not create: " + unZipDestinationDirectory);
            }

//            log.debug("Created: " + unZipDestinationDirectory + " from File: " + sourceFile);
        } catch (Exception e) {
            log.error("error creating directory from file: " + sourceFile, e);
        }

        return unZipDestinationDirectory;
    }

    /**
     * Get the casesensitive basename (without the '.') of a file.
     *
     * @param sourceFile the File the basename is extracted from.
     * @return basename, entire name if no extension.
     */
    public static String getBasename(final File sourceFile) {
        if (sourceFile == null) {
            return "";
        }

        // get the basename of the source file
        int index = sourceFile.getName().lastIndexOf('.');

        if (index != -1) {
            return sourceFile.getName().substring(0, index);
        }

        return sourceFile.getName();
    }

    /**
     * Get the MD5 hash (unique identifier based on contents) of a file.
     * <p/>
     * <p>
     * N.B. This is an expensive operation, since the entire file is read.
     * </p>
     *
     * @param sourceFile the File the MD5 hash is created from, can take null or not a normalFile
     * @return MD5 hash of file as a String, null if it can't create a hash.
     */
    public static String getMD5Hash(final File sourceFile) {
//        log.debug("Getting MD5 hash for " + sourceFile);

        final char[] HEX = "0123456789abcdef".toCharArray();

        if (sourceFile == null || !sourceFile.isFile()) {
            log.error("Error creating MD5 Hash for " + sourceFile);
            return null;
        }
        BufferedInputStream bis = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");


            bis = new BufferedInputStream(new FileInputStream(sourceFile));
            md.reset();
            int len = 0;
            byte[] buffer = new byte[8192];
            while ((len = bis.read(buffer)) > -1) {
                md.update(buffer, 0, len);
            }

            byte[] bytes = md.digest();
            if (bytes == null) {
                log.error("MessageDigest has no bytes for " + sourceFile);

                return null;
            }

            // base64? encode the digest
            StringBuffer sb = new StringBuffer(bytes.length * 2);
            int b;
            for (int i = 0; i < bytes.length; i++) {
                b = bytes[i] & 0xFF;
                sb.append(HEX[b >>> 4]);
                sb.append(HEX[b & 0x0F]);
            }

//            log.debug("MD5 hash for " + sourceFile + " is " + sb);
            return sb.toString();
        } catch (Exception e) {
            log.error("Can't determine MD5 hash for " + sourceFile, e);

            return null;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    log.warn("Can't close stream for " + sourceFile, e);
                }
            }
        }
    }


    public static void deleteFile(File file) {
        file.deleteOnExit();
    }

    /**
     * 根据指定的编码写入文件.
     *
     * @param path
     * @param content
     * @param encoding
     * @throws IOException
     */
    public static void write(String path, String content, String encoding)
            throws IOException {
        File file = new File(path);
        createFile(path);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), encoding));
        writer.write(content);
        writer.close();
    }

    /**
     * 按照指定的编码读取文件.
     *
     * @param path
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String read(String path, String encoding) throws IOException {
        StringBuilder content = new StringBuilder();
        File file = new File(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), encoding));
        String line = null;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        reader.close();
        return content.toString();
    }

    /**
     * 按照指定的编码读取文件.
     *
     * @param file
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String read(File file, String encoding) throws IOException {
        return read(file.getPath(), encoding);
    }

    /**
     * 读取文件到gzip压缩的byte数组.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] readFileToGzipByteArray(File file) throws IOException {
        InputStream in = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(bos);
        try {
            in = FileUtils.openInputStream(file);

            int count;
            byte data[] = new byte[1024];
            while ((count = in.read(data, 0, 1024)) != -1) {
                gos.write(data, 0, count);
            }
            gos.finish();
            gos.flush();
            gos.close();
            byte[] bs = bos.toByteArray();
            bos.close();
            return bs;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }


    public static String pathToURL(String path) {
        return path.replace("\\", "/");
    }

    public static MediaType whatType(String ext) {
        ext = ext.toLowerCase();
        if (ext.equals("jpg") || ext.equals("jpeg")) return MediaType.IMAGE_JPEG;
        if (ext.equals("gif")) return MediaType.IMAGE_GIF;
        if (ext.equals("png")) return MediaType.IMAGE_PNG;
        if (ext.equals("txt") || ext.equals("log")) return MediaType.TEXT_PLAIN;
        if (ext.equals("html") || ext.equals("htm")) return MediaType.TEXT_HTML;
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    public static String whatMimeType(String ext) {
        String type = "application/octet-stream";
        if (ext.equalsIgnoreCase("flv")) type = "application/octet-stream";
        if (ext.equalsIgnoreCase("3gp")) type = "video/3gpp";
        if (ext.equalsIgnoreCase("aab")) type = "application/x-authoware-bin";
        if (ext.equalsIgnoreCase("aam")) type = "application/x-authoware-map	";
        if (ext.equalsIgnoreCase("aas")) type = "application/x-authoware-seg";
        if (ext.equalsIgnoreCase("ai")) type = "application/postscript	";
        if (ext.equalsIgnoreCase("aif")) type = "audio/x-aiff	";
        if (ext.equalsIgnoreCase("aifc")) type = "audio/x-aiff	";
        if (ext.equalsIgnoreCase("aiff")) type = "audio/x-aiff	";
        if (ext.equalsIgnoreCase("als")) type = "audio/X-Alpha5";
        if (ext.equalsIgnoreCase("amc")) type = "application/x-mpeg	";
        if (ext.equalsIgnoreCase("ani")) type = "application/octet-stream";
        if (ext.equalsIgnoreCase("asc")) type = "text/plain	";
        if (ext.equalsIgnoreCase("asd")) type = "application/astound	";
        if (ext.equalsIgnoreCase("asf")) type = "video/x-ms-asf";
        if (ext.equalsIgnoreCase("asn")) type = "application/astound	";
        if (ext.equalsIgnoreCase("asp")) type = "application/x-asap	";
        if (ext.equalsIgnoreCase("asx")) type = "video/x-ms-asf";
        if (ext.equalsIgnoreCase("au")) type = "audio/basic	";
        if (ext.equalsIgnoreCase("avb")) type = "application/octet-stream";
        if (ext.equalsIgnoreCase("avi")) type = "video/x-msvideo";
        if (ext.equalsIgnoreCase("awb")) type = "audio/amr-wb";
        if (ext.equalsIgnoreCase("bcpio")) type = "application/x-bcpio	";
        if (ext.equalsIgnoreCase("bin")) type = "application/octet-stream";
        if (ext.equalsIgnoreCase("bld")) type = "application/bld";
        if (ext.equalsIgnoreCase("bld2")) type = "application/bld2";
        if (ext.equalsIgnoreCase("bmp")) type = "application/x-MS-bmp";
        if (ext.equalsIgnoreCase("bpk")) type = "application/octet-stream";
        if (ext.equalsIgnoreCase("bz2")) type = "application/x-bzip2	";
        if (ext.equalsIgnoreCase("cal")) type = "image/x-cals	";
        if (ext.equalsIgnoreCase("ccn")) type = "application/x-cnc";
        if (ext.equalsIgnoreCase("cco")) type = "application/x-cocoa	";
        if (ext.equalsIgnoreCase("cdf")) type = "application/x-netcdf	";
        if (ext.equalsIgnoreCase("cgi")) type = "magnus-internal/cgi	";
        if (ext.equalsIgnoreCase("chat")) type = "application/x-chat	";
        if (ext.equalsIgnoreCase("class")) type = "application/octet-stream";
        if (ext.equalsIgnoreCase("clp")) type = "application/x-msclip	";
        if (ext.equalsIgnoreCase("cmx")) type = "application/x-cmx	";
        if (ext.equalsIgnoreCase("co")) type = "application/x-cult3d-object";
        if (ext.equalsIgnoreCase("cod")) type = "image/cis-cod";
        if (ext.equalsIgnoreCase("cpio")) type = "application/x-cpio	";
        if (ext.equalsIgnoreCase("cpt")) type = "application/mac-compactpro";
        if (ext.equalsIgnoreCase("crd")) type = "application/x-mscardfile";
        if (ext.equalsIgnoreCase("csh")) type = "application/x-csh";
        if (ext.equalsIgnoreCase("csm")) type = "chemical/x-csml";
        if (ext.equalsIgnoreCase("csml")) type = "chemical/x-csml";
        if (ext.equalsIgnoreCase("css")) type = "text/css	";
        if (ext.equalsIgnoreCase("cur")) type = "application/octet-stream";
        if (ext.equalsIgnoreCase("dcm")) type = "x-lml/x-evm";
        if (ext.equalsIgnoreCase("dcr")) type = "application/x-director	";
        if (ext.equalsIgnoreCase("dcx")) type = "image/x-dcx";
        if (ext.equalsIgnoreCase("dhtml")) type = "text/html	";
        if (ext.equalsIgnoreCase("dir")) type = "application/x-director	";
        if (ext.equalsIgnoreCase("dll")) type = "application/octet-stream";
        if (ext.equalsIgnoreCase("dmg")) type = "application/octet-stream";
        if (ext.equalsIgnoreCase("dms")) type = "application/octet-stream";
        if (ext.equalsIgnoreCase("doc")) type = "application/msword	";
        if (ext.equalsIgnoreCase("dot")) type = "application/x-dot";
        if (ext.equalsIgnoreCase("dvi")) type = "application/x-dvi";
        if (ext.equalsIgnoreCase("dwf")) type = "drawing/x-dwf";
        if (ext.equalsIgnoreCase("dwg")) type = "application/x-autocad	";
        if (ext.equalsIgnoreCase("dxf")) type = "application/x-autocad	";
        if (ext.equalsIgnoreCase("dxr")) type = "application/x-director	";
        if (ext.equalsIgnoreCase("ebk")) type = "application/x-expandedbook	";
        if (ext.equalsIgnoreCase("emb")) type = "chemical/x-embl-dl-nucleotide	";
        if (ext.equalsIgnoreCase("embl")) type = "chemical/x-embl-dl-nucleotide	";
        if (ext.equalsIgnoreCase("eps")) type = "application/postscript	";
        if (ext.equalsIgnoreCase("eri")) type = "image/x-eri	";
        if (ext.equalsIgnoreCase("es")) type = "audio/echospeech";
        if (ext.equalsIgnoreCase("esl")) type = "audio/echospeech";
        if (ext.equalsIgnoreCase("etc")) type = "application/x-earthtime	";
        if (ext.equalsIgnoreCase("etx")) type = "text/x-setext	";
        if (ext.equalsIgnoreCase("evm")) type = "x-lml/x-evm";
        if (ext.equalsIgnoreCase("evy")) type = "application/x-envoy	";
        if (ext.equalsIgnoreCase("exe")) type = "application/octet-stream";
        if (ext.equalsIgnoreCase("fh4")) type = "image/x-freehand";
        if (ext.equalsIgnoreCase("fh5")) type = "image/x-freehand";
        if (ext.equalsIgnoreCase("fhc")) type = "image/x-freehand";
        if (ext.equalsIgnoreCase("fif")) type = "image/fif	";
        if (ext.equalsIgnoreCase("fm")) type = "application/x-maker	";
        if (ext.equalsIgnoreCase("fpx")) type = "image/x-fpx	";
        if (ext.equalsIgnoreCase("fvi")) type = "video/isivideo";
        if (ext.equalsIgnoreCase("gau")) type = "chemical/x-gaussian-input";
        if (ext.equalsIgnoreCase("gca")) type = "application/x-gca-compressed	";
        if (ext.equalsIgnoreCase("gdb")) type = "x-lml/x-gdb";
        if (ext.equalsIgnoreCase("gif")) type = "image/gif	";
        if (ext.equalsIgnoreCase("gps")) type = "application/x-gps	";
        if (ext.equalsIgnoreCase("gtar")) type = "application/x-gtar	";
        if (ext.equalsIgnoreCase("gz")) type = "application/x-gzip";
        if (ext.equalsIgnoreCase("hdf")) type = "application/x-hdf";
        if (ext.equalsIgnoreCase("hdm")) type = "text/x-hdml";
        if (ext.equalsIgnoreCase("hdml")) type = "text/x-hdml";
        if (ext.equalsIgnoreCase("hlp")) type = "application/winhlp	";
        if (ext.equalsIgnoreCase("hqx")) type = "application/mac-binhex40";
        if (ext.equalsIgnoreCase("htm")) type = "text/html	";
        if (ext.equalsIgnoreCase("html")) type = "text/html	";
        if (ext.equalsIgnoreCase("hts")) type = "text/html	";
        if (ext.equalsIgnoreCase("ice")) type = "x-conference/x-cooltalk	";
        if (ext.equalsIgnoreCase("ico")) type = "application/octet-stream";
        if (ext.equalsIgnoreCase("ief")) type = "image/ief	";
        if (ext.equalsIgnoreCase("ifm")) type = "image/gif	";
        if (ext.equalsIgnoreCase("ifs")) type = "image/ifs	";
        if (ext.equalsIgnoreCase("imy")) type = "audio/melody";
        if (ext.equalsIgnoreCase("ins")) type = "application/x-NET-Install";
        if (ext.equalsIgnoreCase("ips")) type = "application/x-ipscript	";
        if (ext.equalsIgnoreCase("ipx")) type = "application/x-ipix";
        if (ext.equalsIgnoreCase("it")) type = "audio/x-mod	";
        if (ext.equalsIgnoreCase("itz")) type = "audio/x-mod	";
        if (ext.equalsIgnoreCase("ivr")) type = "i-world/i-vrml";
        if (ext.equalsIgnoreCase("j2k")) type = "image/j2k	";
        if (ext.equalsIgnoreCase("jad")) type = "text/vnd.sun.j2me.app-descriptor	";
        if (ext.equalsIgnoreCase("jam")) type = "application/x-jam";
        if (ext.equalsIgnoreCase("jar")) type = "application/java-archive	";
        if (ext.equalsIgnoreCase("jnlp")) type = "application/x-java-jnlp-file";
        if (ext.equalsIgnoreCase("jpe")) type = "image/jpeg	";
        if (ext.equalsIgnoreCase("jpeg")) type = "image/jpeg";
        if (ext.equalsIgnoreCase("jpg")) type = "image/jpeg	";
        if (ext.equalsIgnoreCase("jpz")) type = "image/jpeg	";
        if (ext.equalsIgnoreCase("js")) type = "application/x-javascript	";
        if (ext.equalsIgnoreCase("jwc")) type = "application/jwc";
        if (ext.equalsIgnoreCase("kjx")) type = "application/x-kjx";
        if (ext.equalsIgnoreCase("lak")) type = "x-lml/x-lak	";
        if (ext.equalsIgnoreCase("latex")) type = "application/x-latex	";
        if (ext.equalsIgnoreCase("lcc")) type = "application/fastman	";
        if (ext.equalsIgnoreCase("lcl")) type = "application/x-digitalloca	";
        if (ext.equalsIgnoreCase("lcr")) type = "application/x-digitalloca	";
        if (ext.equalsIgnoreCase("lgh")) type = "application/lgh";
        if (ext.equalsIgnoreCase("lha")) type = "application/octet-stream";
        if (ext.equalsIgnoreCase("lml")) type = "x-lml/x-lml	";
        if (ext.equalsIgnoreCase("lmlpack")) type = "x-lml/x-lmlpack	";
        if (ext.equalsIgnoreCase("lsf")) type = "video/x-ms-asf";
        if (ext.equalsIgnoreCase("lsx")) type = "video/x-ms-asf";
        if (ext.equalsIgnoreCase("lzh")) type = "application/x-lzh";
        if (ext.equalsIgnoreCase("m13")) type = "application/x-msmediaview";
        if (ext.equalsIgnoreCase("m14")) type = "application/x-msmediaview";
        if (ext.equalsIgnoreCase("m15")) type = "audio/x-mod";
        if (ext.equalsIgnoreCase("m3u")) type = "audio/x-mpegurl";
        if (ext.equalsIgnoreCase("m3url")) type = "audio/x-mpegurl	";
        if (ext.equalsIgnoreCase("ma1")) type = "audio/ma1	";
        if (ext.equalsIgnoreCase("ma2")) type = "audio/ma2	";
        if (ext.equalsIgnoreCase("ma3")) type = "audio/ma3	";
        if (ext.equalsIgnoreCase("ma5")) type = "audio/ma5	";
        if (ext.equalsIgnoreCase("man")) type = "application/x-troff-man";
        if (ext.equalsIgnoreCase("map")) type = "magnus-internal/imagemap";
        if (ext.equalsIgnoreCase("mbd")) type = "application/mbedlet	";
        if (ext.equalsIgnoreCase("mct")) type = "application/x-mascot	";
        if (ext.equalsIgnoreCase("mdb")) type = "application/x-msaccess";
        if (ext.equalsIgnoreCase("mdz")) type = "audio/x-mod";
        if (ext.equalsIgnoreCase("me")) type = "application/x-troff-me	";
        if (ext.equalsIgnoreCase("mel")) type = "text/x-vmel	";
        if (ext.equalsIgnoreCase("mi")) type = "application/x-mif";
        if (ext.equalsIgnoreCase("mid")) type = "audio/midi	";
        if (ext.equalsIgnoreCase("midi")) type = "audio/midi	";
        if (ext.equalsIgnoreCase("mif")) type = "application/x-mif";
        if (ext.equalsIgnoreCase("mil")) type = "image/x-cals	";
        if (ext.equalsIgnoreCase("mio")) type = "audio/x-mio";
        if (ext.equalsIgnoreCase("mmf")) type = "application/x-skt-lbs	";
        if (ext.equalsIgnoreCase("mng")) type = "video/x-mng";
        if (ext.equalsIgnoreCase("mny")) type = "application/x-msmoney";
        if (ext.equalsIgnoreCase("moc")) type = "application/x-mocha	";
        if (ext.equalsIgnoreCase("mocha")) type = "application/x-mocha";
        if (ext.equalsIgnoreCase("mod")) type = "audio/x-mod";
        if (ext.equalsIgnoreCase("mof")) type = "application/x-yumekara";
        if (ext.equalsIgnoreCase("mol")) type = "chemical/x-mdl-molfile	";
        if (ext.equalsIgnoreCase("mop")) type = "chemical/x-mopac-input";
        if (ext.equalsIgnoreCase("mov")) type = "video/quicktime";
        if (ext.equalsIgnoreCase("movie")) type = "video/x-sgi-movie	";
        if (ext.equalsIgnoreCase("mp2")) type = "audio/x-mpeg";
        if (ext.equalsIgnoreCase("mp3")) type = "audio/x-mpeg";
        if (ext.equalsIgnoreCase("mp4")) type = "video/mp4	";
        if (ext.equalsIgnoreCase("mpc")) type = "application/vnd.mpohun.certificate";
        if (ext.equalsIgnoreCase("mpe")) type = "video/mpeg";
        if (ext.equalsIgnoreCase("mpeg")) type = "video/mpeg";
        if (ext.equalsIgnoreCase("mpg")) type = "video/mpeg";
        if (ext.equalsIgnoreCase("mpg4")) type = "video/mp4";
        if (ext.equalsIgnoreCase("mpga")) type = "audio/mpeg";
        if (ext.equalsIgnoreCase("mpn")) type = "application/vnd.mophun.application";
        if (ext.equalsIgnoreCase("mpp")) type = "application/vnd.ms-project";
        if (ext.equalsIgnoreCase("mps")) type = "application/x-mapserver";
        if (ext.equalsIgnoreCase("mrl")) type = "text/x-mrml	";
        if (ext.equalsIgnoreCase("mrm")) type = "application/x-mrm	";
        if (ext.equalsIgnoreCase("ms")) type = "application/x-troff-ms	";
        if (ext.equalsIgnoreCase("mts")) type = "application/metastream";
        if (ext.equalsIgnoreCase("mtx")) type = "application/metastream";
        if (ext.equalsIgnoreCase("mtz")) type = "application/metastream";
        if (ext.equalsIgnoreCase("mzv")) type = "application/metastream";
        if (ext.equalsIgnoreCase("nar")) type = "application/zip";
        if (ext.equalsIgnoreCase("nbmp")) type = "image/nbmp";
        if (ext.equalsIgnoreCase("nc")) type = "application/x-netcdf	";
        if (ext.equalsIgnoreCase("ndb")) type = "x-lml/x-ndb	";
        if (ext.equalsIgnoreCase("ndwn")) type = "application/ndwn	";
        if (ext.equalsIgnoreCase("nif")) type = "application/x-nif";
        if (ext.equalsIgnoreCase("nmz")) type = "application/x-scream	";
        if (ext.equalsIgnoreCase("nokia-op-logo")) type = "image/vnd.nok-oplogo-color	";
        if (ext.equalsIgnoreCase("npx")) type = "application/x-netfpx	";
        if (ext.equalsIgnoreCase("nsnd")) type = "audio/nsnd";
        if (ext.equalsIgnoreCase("nva")) type = "application/x-neva1	";
        if (ext.equalsIgnoreCase("oda")) type = "application/oda";
        if (ext.equalsIgnoreCase("oom")) type = "application/x-AtlasMate-Plugin	";
        if (ext.equalsIgnoreCase("pac")) type = "audio/x-pac	";
        if (ext.equalsIgnoreCase("pae")) type = "audio/x-epac";
        if (ext.equalsIgnoreCase("pan")) type = "application/x-pan	";
        if (ext.equalsIgnoreCase("pbm")) type = "image/x-portable-bitmap";
        if (ext.equalsIgnoreCase("pcx")) type = "image/x-pcx";
        if (ext.equalsIgnoreCase("pda")) type = "image/x-pda";
        if (ext.equalsIgnoreCase("pdb")) type = "chemical/x-pdb";
        if (ext.equalsIgnoreCase("pdf")) type = "application/pdf";
        if (ext.equalsIgnoreCase("pfr")) type = "application/font-tdpfr	";
        if (ext.equalsIgnoreCase("pgm")) type = "image/x-portable-graymap";
        if (ext.equalsIgnoreCase("pict")) type = "image/x-pict";
        if (ext.equalsIgnoreCase("pm")) type = "application/x-perl";
        if (ext.equalsIgnoreCase("pmd")) type = "application/x-pmd	";
        if (ext.equalsIgnoreCase("png")) type = "image/png	";
        if (ext.equalsIgnoreCase("pnm")) type = "image/x-portable-anymap";
        if (ext.equalsIgnoreCase("pnz")) type = "image/png	";
        if (ext.equalsIgnoreCase("pot")) type = "application/vnd.ms-powerpoint	";
        if (ext.equalsIgnoreCase("ppm")) type = "image/x-portable-pixmap";
        if (ext.equalsIgnoreCase("pps")) type = "application/vnd.ms-powerpoint	";
        if (ext.equalsIgnoreCase("ppt")) type = "application/vnd.ms-powerpoint	";
        if (ext.equalsIgnoreCase("pqf")) type = "application/x-cprplayer	";
        if (ext.equalsIgnoreCase("pqi")) type = "application/cprplayer	";
        if (ext.equalsIgnoreCase("prc")) type = "application/x-prc";
        if (ext.equalsIgnoreCase("proxy")) type = "application/x-ns-proxy-autoconfig";
        if (ext.equalsIgnoreCase("ps")) type = "application/postscript	";
        if (ext.equalsIgnoreCase("ptlk")) type = "application/listenup	";
        if (ext.equalsIgnoreCase("pub")) type = "application/x-mspublisher";
        if (ext.equalsIgnoreCase("pvx")) type = "video/x-pv-pvx";
        if (ext.equalsIgnoreCase("qcp")) type = "audio/vnd.qcelp";
        if (ext.equalsIgnoreCase("qt")) type = "video/quicktime";
        if (ext.equalsIgnoreCase("qti")) type = "image/x-quicktime";
        if (ext.equalsIgnoreCase("qtif")) type = "image/x-quicktime	";
        if (ext.equalsIgnoreCase("r3t")) type = "text/vnd.rn-realtext3d	";
        if (ext.equalsIgnoreCase("ra")) type = "audio/x-pn-realaudio	";
        if (ext.equalsIgnoreCase("ram")) type = "audio/x-pn-realaudio	";
        if (ext.equalsIgnoreCase("rar")) type = "application/x-rar-compressed	";
        if (ext.equalsIgnoreCase("ras")) type = "image/x-cmu-raster	";
        if (ext.equalsIgnoreCase("rdf")) type = "application/rdf+xml	";
        if (ext.equalsIgnoreCase("rf")) type = "image/vnd.rn-realflash	";
        if (ext.equalsIgnoreCase("rgb")) type = "image/x-rgb";
        if (ext.equalsIgnoreCase("rlf")) type = "application/x-richlink	";
        if (ext.equalsIgnoreCase("rm")) type = "audio/x-pn-realaudio	";
        if (ext.equalsIgnoreCase("rmf")) type = "audio/x-rmf	";
        if (ext.equalsIgnoreCase("rmm")) type = "audio/x-pn-realaudio	";
        if (ext.equalsIgnoreCase("rmvb")) type = "audio/x-pn-realaudio	";
        if (ext.equalsIgnoreCase("rnx")) type = "application/vnd.rn-realplayer";
        if (ext.equalsIgnoreCase("roff")) type = "application/x-troff	";
        if (ext.equalsIgnoreCase("rp")) type = "image/vnd.rn-realpix	";
        if (ext.equalsIgnoreCase("rpm")) type = "audio/x-pn-realaudio-plugin	";
        if (ext.equalsIgnoreCase("rt")) type = "text/vnd.rn-realtext";
        if (ext.equalsIgnoreCase("rte")) type = "x-lml/x-gps	";
        if (ext.equalsIgnoreCase("rtf")) type = "application/rtf";
        if (ext.equalsIgnoreCase("rtg")) type = "application/metastream	";
        if (ext.equalsIgnoreCase("rtx")) type = "text/richtext	";
        if (ext.equalsIgnoreCase("rv")) type = "video/vnd.rn-realvideo	";
        if (ext.equalsIgnoreCase("rwc")) type = "application/x-rogerwilco";
        if (ext.equalsIgnoreCase("s3m")) type = "audio/x-mod";
        if (ext.equalsIgnoreCase("s3z")) type = "audio/x-mod";
        if (ext.equalsIgnoreCase("sca")) type = "application/x-supercard";
        if (ext.equalsIgnoreCase("scd")) type = "application/x-msschedule";
        if (ext.equalsIgnoreCase("sdf")) type = "application/e-score	";
        if (ext.equalsIgnoreCase("sea")) type = "application/x-stuffit	";
        if (ext.equalsIgnoreCase("sgm")) type = "text/x-sgml	";
        if (ext.equalsIgnoreCase("sgml")) type = "text/x-sgml";
        if (ext.equalsIgnoreCase("sh")) type = "application/x-sh";
        if (ext.equalsIgnoreCase("shar")) type = "application/x-shar	";
        if (ext.equalsIgnoreCase("shtml")) type = "magnus-internal/parsed-html	";
        if (ext.equalsIgnoreCase("shw")) type = "application/presentations";
        if (ext.equalsIgnoreCase("si6")) type = "image/si6	";
        if (ext.equalsIgnoreCase("si7")) type = "image/vnd.stiwap.sis	";
        if (ext.equalsIgnoreCase("si9")) type = "image/vnd.lgtwap.sis	";
        if (ext.equalsIgnoreCase("sis")) type = "application/vnd.symbian.install	";
        if (ext.equalsIgnoreCase("sit")) type = "application/x-stuffit	";
        if (ext.equalsIgnoreCase("skd")) type = "application/x-Koan	";
        if (ext.equalsIgnoreCase("skm")) type = "application/x-Koan	";
        if (ext.equalsIgnoreCase("skp")) type = "application/x-Koan	";
        if (ext.equalsIgnoreCase("skt")) type = "application/x-Koan	";
        if (ext.equalsIgnoreCase("slc")) type = "application/x-salsa	";
        if (ext.equalsIgnoreCase("smd")) type = "audio/x-smd";
        if (ext.equalsIgnoreCase("smi")) type = "application/smil";
        if (ext.equalsIgnoreCase("smil")) type = "application/smil";
        if (ext.equalsIgnoreCase("smp")) type = "application/studiom	";
        if (ext.equalsIgnoreCase("smz")) type = "audio/x-smd";
        if (ext.equalsIgnoreCase("snd")) type = "audio/basic	";
        if (ext.equalsIgnoreCase("spc")) type = "text/x-speech";
        if (ext.equalsIgnoreCase("spl")) type = "application/futuresplash";
        if (ext.equalsIgnoreCase("spr")) type = "application/x-sprite	";
        if (ext.equalsIgnoreCase("sprite")) type = "application/x-sprite	";
        if (ext.equalsIgnoreCase("spt")) type = "application/x-spt";
        if (ext.equalsIgnoreCase("src")) type = "application/x-wais-source";
        if (ext.equalsIgnoreCase("stk")) type = "application/hyperstudio	";
        if (ext.equalsIgnoreCase("stm")) type = "audio/x-mod";
        if (ext.equalsIgnoreCase("sv4cpio")) type = "application/x-sv4cpio";
        if (ext.equalsIgnoreCase("sv4crc")) type = "application/x-sv4crc	";
        if (ext.equalsIgnoreCase("svf")) type = "image/vnd	";
        if (ext.equalsIgnoreCase("svg")) type = "application/xml";
        if (ext.equalsIgnoreCase("svh")) type = "image/svh	";
        if (ext.equalsIgnoreCase("svr")) type = "x-world/x-svr";
        if (ext.equalsIgnoreCase("swf")) type = "application/x-shockwave-flash	";
        if (ext.equalsIgnoreCase("swfl")) type = "application/x-shockwave-flash	";
        if (ext.equalsIgnoreCase("t")) type = "application/x-troff";
        if (ext.equalsIgnoreCase("tad")) type = "application/octet-stream";
        if (ext.equalsIgnoreCase("talk")) type = "text/x-speech";
        if (ext.equalsIgnoreCase("tar")) type = "application/x-tar";
        if (ext.equalsIgnoreCase("taz")) type = "application/x-tar";
        if (ext.equalsIgnoreCase("tbp")) type = "application/x-timbuktu	";
        if (ext.equalsIgnoreCase("tbt")) type = "application/x-timbuktu	";
        if (ext.equalsIgnoreCase("tcl")) type = "application/x-tcl";
        if (ext.equalsIgnoreCase("tex")) type = "application/x-tex";
        if (ext.equalsIgnoreCase("texi")) type = "application/x-texinfo	";
        if (ext.equalsIgnoreCase("texinfo")) type = "application/x-texinfo";
        if (ext.equalsIgnoreCase("tgz")) type = "application/x-tar";
        if (ext.equalsIgnoreCase("thm")) type = "application/vnd.eri.thm	";
        if (ext.equalsIgnoreCase("tif")) type = "image/tiff	";
        if (ext.equalsIgnoreCase("tiff")) type = "image/tiff	";
        if (ext.equalsIgnoreCase("tki")) type = "application/x-tkined	";
        if (ext.equalsIgnoreCase("tkined")) type = "application/x-tkined	";
        if (ext.equalsIgnoreCase("toc")) type = "application/toc";
        if (ext.equalsIgnoreCase("toy")) type = "image/toy	";
        if (ext.equalsIgnoreCase("tr")) type = "application/x-troff";
        if (ext.equalsIgnoreCase("trk")) type = "x-lml/x-gps	";
        if (ext.equalsIgnoreCase("trm")) type = "application/x-msterminal";
        if (ext.equalsIgnoreCase("tsi")) type = "audio/tsplayer";
        if (ext.equalsIgnoreCase("tsp")) type = "application/dsptype	";
        if (ext.equalsIgnoreCase("tsv")) type = "text/tab-separated-values";
        if (ext.equalsIgnoreCase("tsv")) type = "text/tab-separated-values";
        if (ext.equalsIgnoreCase("ttf")) type = "application/octet-stream	";
        if (ext.equalsIgnoreCase("ttz")) type = "application/t-time";
        if (ext.equalsIgnoreCase("txt")) type = "text/plain	";
        if (ext.equalsIgnoreCase("ult")) type = "audio/x-mod	";
        if (ext.equalsIgnoreCase("ustar")) type = "application/x-ustar	";
        if (ext.equalsIgnoreCase("uu")) type = "application/x-uuencode	";
        if (ext.equalsIgnoreCase("uue")) type = "application/x-uuencode";
        if (ext.equalsIgnoreCase("vcd")) type = "application/x-cdlink	";
        if (ext.equalsIgnoreCase("vcf")) type = "text/x-vcard	";
        if (ext.equalsIgnoreCase("vdo")) type = "video/vdo	";
        if (ext.equalsIgnoreCase("vib")) type = "audio/vib	";
        if (ext.equalsIgnoreCase("viv")) type = "video/vivo	";
        if (ext.equalsIgnoreCase("vivo")) type = "video/vivo	";
        if (ext.equalsIgnoreCase("vmd")) type = "application/vocaltec-media-desc	";
        if (ext.equalsIgnoreCase("vmf")) type = "application/vocaltec-media-file	";
        if (ext.equalsIgnoreCase("vmi")) type = "application/x-dreamcast-vms-info	";
        if (ext.equalsIgnoreCase("vms")) type = "application/x-dreamcast-vms	";
        if (ext.equalsIgnoreCase("vox")) type = "audio/voxware";
        if (ext.equalsIgnoreCase("vqe")) type = "audio/x-twinvq-plugin	";
        if (ext.equalsIgnoreCase("vqf")) type = "audio/x-twinvq";
        if (ext.equalsIgnoreCase("vql")) type = "audio/x-twinvq";
        if (ext.equalsIgnoreCase("vre")) type = "x-world/x-vream";
        if (ext.equalsIgnoreCase("vrml")) type = "x-world/x-vrml";
        if (ext.equalsIgnoreCase("vrt")) type = "x-world/x-vrt	";
        if (ext.equalsIgnoreCase("vrw")) type = "x-world/x-vream";
        if (ext.equalsIgnoreCase("vts")) type = "workbook/formulaone	";
        if (ext.equalsIgnoreCase("wav")) type = "audio/x-wav";
        if (ext.equalsIgnoreCase("wax")) type = "audio/x-ms-wax";
        if (ext.equalsIgnoreCase("wbmp")) type = "image/vnd.wap.wbmp";
        if (ext.equalsIgnoreCase("web")) type = "application/vnd.xara	";
        if (ext.equalsIgnoreCase("wi")) type = "image/wavelet";
        if (ext.equalsIgnoreCase("wis")) type = "application/x-InstallShield";
        if (ext.equalsIgnoreCase("wm")) type = "video/x-ms-wm";
        if (ext.equalsIgnoreCase("wma")) type = "audio/x-ms-wma	";
        if (ext.equalsIgnoreCase("wmd")) type = "application/x-ms-wmd";
        if (ext.equalsIgnoreCase("wmf")) type = "application/x-msmetafile";
        if (ext.equalsIgnoreCase("wml")) type = "text/vnd.wap.wml";
        if (ext.equalsIgnoreCase("wmlc")) type = "application/vnd.wap.wmlc";
        if (ext.equalsIgnoreCase("wmls")) type = "text/vnd.wap.wmlscript";
        if (ext.equalsIgnoreCase("wmlsc")) type = "application/vnd.wap.wmlscriptc	";
        if (ext.equalsIgnoreCase("wmlscript")) type = "text/vnd.wap.wmlscript";
        if (ext.equalsIgnoreCase("wmv")) type = "audio/x-ms-wmv";
        if (ext.equalsIgnoreCase("wmx")) type = "video/x-ms-wmx";
        if (ext.equalsIgnoreCase("wmz")) type = "application/x-ms-wmz	";
        if (ext.equalsIgnoreCase("wpng")) type = "image/x-up-wpng	";
        if (ext.equalsIgnoreCase("wpt")) type = "x-lml/x-gps	";
        if (ext.equalsIgnoreCase("wri")) type = "application/x-mswrite	";
        if (ext.equalsIgnoreCase("wrl")) type = "x-world/x-vrml";
        if (ext.equalsIgnoreCase("wrz")) type = "x-world/x-vrml";
        if (ext.equalsIgnoreCase("ws")) type = "text/vnd.wap.wmlscript	";
        if (ext.equalsIgnoreCase("wsc")) type = "application/vnd.wap.wmlscriptc	";
        if (ext.equalsIgnoreCase("wv")) type = "video/wavelet";
        if (ext.equalsIgnoreCase("wvx")) type = "video/x-ms-wvx";
        if (ext.equalsIgnoreCase("wxl")) type = "application/x-wxl";
        if (ext.equalsIgnoreCase("x-gzip")) type = "application/x-gzip	";
        if (ext.equalsIgnoreCase("xar")) type = "application/vnd.xara	";
        if (ext.equalsIgnoreCase("xbm")) type = "image/x-xbitmap	";
        if (ext.equalsIgnoreCase("xdm")) type = "application/x-xdma	";
        if (ext.equalsIgnoreCase("xdma")) type = "application/x-xdma	";
        if (ext.equalsIgnoreCase("xdw")) type = "application/vnd.fujixerox.docuworks";
        if (ext.equalsIgnoreCase("xht")) type = "application/xhtml+xml	";
        if (ext.equalsIgnoreCase("xhtm")) type = "application/xhtml+xml";
        if (ext.equalsIgnoreCase("xhtml")) type = "application/xhtml+xml";
        if (ext.equalsIgnoreCase("xla")) type = "application/vnd.ms-excel";
        if (ext.equalsIgnoreCase("xlc")) type = "application/vnd.ms-excel";
        if (ext.equalsIgnoreCase("xll")) type = "application/x-excel";
        if (ext.equalsIgnoreCase("xlm")) type = "application/vnd.ms-excel";
        if (ext.equalsIgnoreCase("xls")) type = "application/vnd.ms-excel";
        if (ext.equalsIgnoreCase("xlt")) type = "application/vnd.ms-excel";
        if (ext.equalsIgnoreCase("xlw")) type = "application/vnd.ms-excel";
        if (ext.equalsIgnoreCase("xm")) type = "audio/x-mod";
        if (ext.equalsIgnoreCase("xml")) type = "text/xml	";
        if (ext.equalsIgnoreCase("xmz")) type = "audio/x-mod";
        if (ext.equalsIgnoreCase("xpi")) type = "application/x-xpinstall	";
        if (ext.equalsIgnoreCase("xpm")) type = "image/x-xpixmap	";
        if (ext.equalsIgnoreCase("xsit")) type = "text/xml	";
        if (ext.equalsIgnoreCase("xsl")) type = "text/xml	";
        if (ext.equalsIgnoreCase("xul")) type = "text/xul";
        if (ext.equalsIgnoreCase("xwd")) type = "image/x-xwindowdump";
        if (ext.equalsIgnoreCase("xyz")) type = "chemical/x-pdb";
        if (ext.equalsIgnoreCase("yz1")) type = "application/x-yz1";
        if (ext.equalsIgnoreCase("z")) type = "application/x-compress	";
        if (ext.equalsIgnoreCase("zac")) type = "application/x-zaurus-zac";
        if (ext.equalsIgnoreCase("zip")) type = "application/zip";
        return type;
    }

    public static String getAgentFileName(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        String new_filename = fileName;
        String userAgent = request.getHeader("User-Agent").toLowerCase();
        String rtn = fileName;
        userAgent = userAgent.toLowerCase();
        // IE浏览器，只能采用URLEncoder编码
        if (userAgent.contains("trident")) {
            new_filename = URLEncoder.encode(fileName, "UTF8");
            new_filename = StringUtils.replace(new_filename, "+", "%20");//替换空格
            rtn = "filename=\"" + new_filename + "\"";
        }
        // Opera浏览器只能采用filename*
        else if (userAgent.contains("opera")) {
            new_filename = URLEncoder.encode(fileName, "UTF8");
            rtn = "filename*=UTF-8''" + new_filename;
        }
        // Safari浏览器，只能采用ISO编码的中文输出
        else if (userAgent.contains("safari")) {
            rtn = "filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859-1") + "\"";
        }
        // Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
        else if (userAgent.contains("applewebkit")) {
            new_filename = new String(new_filename.getBytes("utf8"), "iso8859-1");
            rtn = "filename=\"" + new_filename + "\"";
        }
        // FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
        else if (userAgent.contains("mozilla")) {
            new_filename = new String(new_filename.getBytes("utf8"), "iso8859-1");
            rtn = "filename*=UTF-8''" + new_filename;
        }
        return rtn;
    }


    public static void main(String[] args) {
        String path = "2014\\7\\17\\5c2a75ad-b8d3-4306-9a4d-5640ed2bea1e.jpg";
//        System.out.println(FileUtil.pathToURL(path));

    }


    public static void append(File dest, String content, String encoding) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(dest, true), encoding));
            writer.write(content);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }


}
