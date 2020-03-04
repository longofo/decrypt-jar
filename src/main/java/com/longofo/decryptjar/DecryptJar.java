package com.longofo.decryptjar;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class DecryptJar {
    public String sourceJarPath;
    public String newJarPath;

    public DecryptJar(String sourceJarPath, String saveJarPath) {
        this.sourceJarPath = sourceJarPath;
        this.newJarPath = saveJarPath;
    }

    public void decrypt() {
        try {
            check();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        JarOutputStream newJarFile = null;
        JarFile sourceJarFile = null;

        System.out.println(String.format("开始解密%s包，并将结果保存到%s包", this, sourceJarFile, this.newJarPath));
        try {
            sourceJarFile = new JarFile(new File(this.sourceJarPath));
            newJarFile = new JarOutputStream(new FileOutputStream(this.newJarPath));


            // 获取原始jar包所有条目并逐个处理
            Enumeration<JarEntry> sourceEntrys = sourceJarFile.entries();
            while (sourceEntrys.hasMoreElements()) {
                JarEntry sourceEntry = sourceEntrys.nextElement();
                String sourceEntryName = sourceEntry.getName();
                JarEntry newEntry = new JarEntry(sourceEntryName);

                newJarFile.putNextEntry(newEntry);

                System.out.println(String.format("处理条目：%s", sourceEntryName));

                //如果条目不是目录，就获取条目数据
                byte[] entryData = null;
                if (!sourceEntry.isDirectory()) {
                    InputStream sourceEntryStream = sourceJarFile.getInputStream(sourceEntry);
                    entryData = IOUtility.toByteArray(sourceEntryStream);
                }

                /*
                如果条目不是目录是class文件且开头为"CAFEBABE"则为没有加密的文件，直接写入之前的数据；
                如果条目不是目录是class文件且开头不为"CAFEBABE"则为加密文件，先解密数据再写入；
                否则直接写入之前的数据；
                */
                if (!sourceEntry.isDirectory() && sourceEntryName.endsWith(".class") && entryData != null) {
                    byte[] firstFourBytes = new byte[4];
                    System.arraycopy(entryData, 0, firstFourBytes, 0, 4);

                    BigInteger bigInteger = new BigInteger(1, firstFourBytes);
                    if (bigInteger.toString(16).toUpperCase().equals("CAFEBABE")) {
                        System.out.println(String.format("条目：%s为正常class，直接写入之前的数据", sourceEntryName));
                        newJarFile.write(entryData);
                    } else {
                        System.out.println(String.format("条目：%s为加密class，解密数据后写入数据", sourceEntryName));
                        byte[] classData = decryptClass(entryData);
                        newJarFile.write(classData);
                    }
                } else if (!sourceEntry.isDirectory() && entryData != null) {
                    System.out.println(String.format("条目：%s为其他类型，直接写入之前的数据", sourceEntryName));
                    newJarFile.write(entryData);
                } else {
                    System.out.println(String.format("条目：%s为目录", sourceEntryName));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DogException e) {
            e.printStackTrace();
        } finally {
            try {
                if (newJarFile != null) {
                    newJarFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (sourceJarFile != null) {
                    sourceJarFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开始处理前检查
     *
     * @throws Exception
     */
    private void check() throws Exception {
        if (!this.sourceJarPath.endsWith(".jar") || !this.newJarPath.endsWith(".jar")) {
            throw new Exception("提供的路径需要以.jar结束");
        }

        File sourceJarFile = new File(this.sourceJarPath);
        File newJarFile = new File(this.newJarPath);

        if (!sourceJarFile.exists()) {
            throw new Exception(String.format("%s不存在，原jar包需要提供存在的路径", this.sourceJarPath));
        }

        if (newJarFile.exists()) {
            throw new Exception(String.format("%s存在，新jar包需要在目标文件夹中不存在", this.sourceJarPath));
        }
    }

    /**
     * 解密class数据
     *
     * @param encryptClassData
     * @return
     * @throws DogException
     */
    private byte[] decryptClass(byte[] encryptClassData) throws DogException {
        byte[] classData = null;
        byte[] datas = Base64.decodeBase64(encryptClassData);
        classData = RSMocnoyees.decode(RSMocnoyees.getPublicKey("65537", Base64Util.decode("Nzg4NDM2MTAxMzc1NzA0MDQ1Nzc3ODQ3MzM0OTg2NzgxNjEzNDM5Mzg5OTMyODA2ODcwNDQ0Nzk4NDIyODE2MTk0MTEzMzA2NDcyNjkzNTQzMDg4NjUyODc4NDA0NjUwMDEwMDAyNjI0ODQ4NjMxMzA3MjgzMTc4NzE1ODYzMjE1OTYzMDY3NDkwNTYzNDc1NTg0ODM0NzU1NzQ5MDI2NDkyMDk5NTUyMTIzNDAyOTA2NDIyMzgzMTQ1ODUzMjc3OTM4MDQxMDQ5MTU5NzczOTk0ODY3NzA5NzYwMjQzMDcwNTQzMjA3")), datas, 96);

        return classData;
    }
}
