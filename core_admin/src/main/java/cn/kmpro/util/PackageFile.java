/**
 * Package:cn.kmpro.util;
 * $Id$
 * Copyright(c) 2001-2005 www.afteryuan.com
 */
package cn.kmpro.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PackageFile {
    private static Logger logger = LoggerFactory.getLogger(PackageFile.class);

    public static URL getFile(String pack, String fileName) {
        String ext = FileUtil.getExtension(fileName);
        Set<URI> fs = getFiles(pack, ext);
        for (URI f : fs) {
            try {
                URL url = f.toURL();
                if (url.getFile().endsWith(fileName)) return url;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param pack
     * @return
     */
    public static Set<URI> getFiles(String pack, String extendName) {
        long st = System.currentTimeMillis();

        // 第一个class类的集合
        Set<URI> classes = new LinkedHashSet<>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageDirName = pack.replace('.', '/');
        if (!packageDirName.endsWith("/")) packageDirName += "/";
        logger.debug("looking for packageDirName: " + packageDirName);
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            logger.debug(String.valueOf(Thread.currentThread().getContextClassLoader()));

            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                logger.debug("url:" + url);

                // 得到协议的名称
                String protocol = url.getProtocol();
                logger.debug("protocol:" + protocol);
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(pack, filePath, recursive, classes, extendName);
                } else if ("jar".equals(protocol)) {
                    findAndAddClassesInPackageByJar(pack, url, recursive, classes, extendName);
                } else if ("wsjar".equals(protocol)) {//IBM WAS Jar
                    findAndAddClassesInPackageByWsJar(pack, url, recursive, classes, extendName);
                } else if ("zip".equals(protocol)) {
                    findAndAddClassesInPackageByZip(pack, url, recursive, classes, extendName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug("包文件扫描完成耗时：" + (System.currentTimeMillis() - st));

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<URI> classes, final String extendName) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || (file.getName().endsWith(extendName));
            }
        });
        // 循环所有文件
        if (dirfiles != null) {
            for (File file : dirfiles) {
                // 如果是目录 则继续扫描
                if (file.isDirectory()) {
                    findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes, extendName);
                } else {
                    classes.add(file.toURI());
                }
            }
        }
    }

    /**
     * 以jar包的形式读取指定的文件到集合中
     *
     * @param classes
     * @param extendName
     */
    private static void findAndAddClassesInPackageByJar(String packageName, URL url, boolean recursive, Set<URI> classes, final String extendName) {
        String packageDirName = packageName.replace('.', '/');
        // 定义一个JarFile
        JarFile jar;
        try {
            // 获取jar
            jar = ((JarURLConnection) url.openConnection()).getJarFile();
            // 从此jar包 得到一个枚举类
            Enumeration<JarEntry> entries = jar.entries();
            // 同样的进行循环迭代
            while (entries.hasMoreElements()) {
                // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                // 如果是以/开头的
                if (name.charAt(0) == '/') {
                    // 获取后面的字符串
                    name = name.substring(1);
                }
                // 如果前半部分和定义的包名相同
                if (name.startsWith(packageDirName)) {
                    int idx = name.lastIndexOf('/');
                    // 如果以"/"结尾 是一个包
//                    if (idx != -1) {
                    // 获取包名 把"/"替换成"."
//                        packageName = name.substring(0, idx)
//                                .replace('/', '.');//这个packageName从来没有用过，属于不必要的赋值
//                    }
                    // 如果可以迭代下去 并且是一个包
                    // 如果是一个.class文件 而且不是目录
                    if (name.endsWith(extendName)
                            && !entry.isDirectory()) {
                        // 去掉后面的".class" 获取真正的类名
                        String pre = url.getFile();
                        if (pre.endsWith("/")) pre = pre.substring(0, pre.length() - 1);
                        String p = pre + "/" + FileUtil.parseFileNameFromPath(entry.getName(), "/");
                        URL entityUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), p);
                        classes.add(entityUrl.toURI());
                    }
                }
            }
        } catch (IOException e) {
            logger.error("在扫描用户定义文件时从jar包获取文件出错", e);
        } catch (URISyntaxException e) {
            logger.error("URL无法转成URI.",e);
        }
    }

    private static void findAndAddClassesInPackageByWsJar(String packageName, URL url, boolean recursive, Set<URI> classes, final String extendName) {
        String packageDirName = packageName.replace('.', '/');
        // 定义一个JarFile
        String path = url.getPath();
        int startIndex = path.indexOf("/");
        if (startIndex == -1) startIndex = 0;
        path = path.substring(startIndex, path.indexOf("!"));
        logger.debug("path:" + path);
        try {
            Enumeration<JarEntry> entries;
            try (JarFile jar = new JarFile(new File(path))) {
                // 从此jar包 得到一个枚举类
                entries = jar.entries();
            }
            // 同样的进行循环迭代
            while (entries.hasMoreElements()) {
                // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                // 如果是以/开头的
                if (name.charAt(0) == '/') {
                    // 获取后面的字符串
                    name = name.substring(1);
                }
                // 如果前半部分和定义的包名相同
                if (name.startsWith(packageDirName)) {
                    int idx = name.lastIndexOf('/');
                    // 如果以"/"结尾 是一个包
//                    if (idx != -1) {
                    // 获取包名 把"/"替换成"."
//                        packageName = name.substring(0, idx)
//                                .replace('/', '.');
//                    }
                    // 如果可以迭代下去 并且是一个包
                    // 如果是一个.class文件 而且不是目录
                    if (name.endsWith(extendName)
                            && !entry.isDirectory()) {
                        // 去掉后面的".class" 获取真正的类名
                        String pre = url.getFile();
                        if (pre.endsWith("/")) pre = pre.substring(0, pre.length() - 1);
                        String p = pre + "/" + FileUtil.parseFileNameFromPath(entry.getName(), "/");
                        URL entityUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), p);
                        classes.add(entityUrl.toURI());
                    }
                }
            }
        } catch (IOException e) {
            logger.error("在扫描用户定义文件时从jar包获取文件出错(WS)", e);
        } catch (URISyntaxException e) {
            logger.error("URL无法转换成URI.",e);
        }
    }

    private static void findAndAddClassesInPackageByZip(String packageName, URL url, boolean recursive, Set<URI> classes, final String extendName) {
        String packageDirName = packageName.replace('.', '/');
        String path = url.getPath();
        path = path.substring(0, path.indexOf("!"));
        try {
            Enumeration<JarEntry> entries;
            try (JarFile jar = new JarFile(new File(path))) {
                // 从此jar包 得到一个枚举类
                entries = jar.entries();
            }
            // 同样的进行循环迭代
            while (entries.hasMoreElements()) {
                // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                // 如果是以/开头的
                if (name.charAt(0) == '/') {
                    // 获取后面的字符串
                    name = name.substring(1);
                }
                // 如果前半部分和定义的包名相同
                if (name.startsWith(packageDirName)) {
                    int idx = name.lastIndexOf('/');
                    // 如果以"/"结尾 是一个包
//                    if (idx != -1) {
                    // 获取包名 把"/"替换成"."
//                        packageName = name.substring(0, idx)
//                                .replace('/', '.');
//                    }
                    // 如果可以迭代下去 并且是一个包
                    // 如果是一个.class文件 而且不是目录
                    if (name.endsWith(extendName)
                            && !entry.isDirectory()) {
                        // 去掉后面的".class" 获取真正的类名
                        String pre = url.getFile();
                        if (pre.endsWith("/")) pre = pre.substring(0, pre.length() - 1);
                        String p = pre + "/" + FileUtil.parseFileNameFromPath(entry.getName(), "/");
                        URL entityUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), p);
                        classes.add(entityUrl.toURI());
                    }
                }
            }
        } catch (IOException e) {
            logger.error("在扫描用户定义文件时从jar包获取文件出错", e);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) throws IOException {
//        Set<URL> fs = PackageFile.getFiles("com.afteryuan.dao", ".class");
//        Iterator<URL> it = fs.iterator();
//        while (it.hasNext()) {
//            URL f = it.next();
//            process(f.openStream());
//        }
//    }
}
