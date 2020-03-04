package com.longofo.decryptjar;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Test {
    public static void main(String[] args) {
        /*String jarPath = "";
        test(jarPath);
        test1(jarPath);
        test2(jarPath);*/
        String sourceJarPath = "";
        String newJarPath = sourceJarPath.substring(0, sourceJarPath.lastIndexOf(".")) + "-decrypt.jar";

        DecryptJar decryptJar = new DecryptJar(sourceJarPath, newJarPath);
        decryptJar.decrypt();
    }

    public static void test(String jarPath) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(new File(jarPath));
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry entry = entrys.nextElement();
                String name = entry.getName();
                long size = entry.getSize();
                long compressedSize = entry.getCompressedSize();
                System.out.println(name + "\t" + size + "\t" + compressedSize);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void test1(String jarPath) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(new File(jarPath), true);
            Manifest manifest = jarFile.getManifest();
            Attributes attributes = manifest.getMainAttributes();

            for (Iterator it = attributes.keySet().iterator(); it.hasNext(); ) {
                Attributes.Name attrName = (Attributes.Name) it.next();
                String attrValue = attributes.getValue(attrName);

                System.out.println(attrName + "\t" + attrValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void test2(String jarPath) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(new File(jarPath), true);
            Enumeration<JarEntry> entrys = jarFile.entries();

            while (entrys.hasMoreElements()) {
                JarEntry entry = entrys.nextElement();

                String entryName = entry.getName();
                String comment = entry.getComment();
                long uncompressSize = entry.getSize();
                long compressSize = entry.getCompressedSize();
                long crc = entry.getCrc();
                int method = entry.getMethod();
                long time = entry.getTime();
                boolean isDirectory = entry.isDirectory();

                System.out.println("---------------------------" + entryName + "---------------------------");
                System.out.println("entryName: " + entryName);
                System.out.println("comment: " + comment);
                System.out.println("uncompressSize: " + uncompressSize);
                System.out.println("compressSize: " + compressSize);
                System.out.println("compress method: " + method);
                System.out.println("crc: " + crc);
                System.out.println("time: " + time);
                System.out.println("isDirectory: " + isDirectory);

                System.out.println("manifest attributes:\n");
                try {
                    Manifest manifest = jarFile.getManifest();
                    Attributes attributes = manifest.getMainAttributes();
                    for (Iterator it = attributes.keySet().iterator(); it.hasNext(); ) {
                        Attributes.Name attrName = (Attributes.Name) it.next();
                        String attrValue = attributes.getValue(attrName);

                        System.out.println(attrName + "\t" + attrValue);
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}