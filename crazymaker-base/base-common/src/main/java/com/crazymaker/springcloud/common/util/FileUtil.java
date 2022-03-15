package com.crazymaker.springcloud.common.util;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.sql.Blob;
import java.util.concurrent.CompletableFuture;


public class FileUtil
{


    /**
     * 生成源码文件
     *
     * @param filePath 文件路径
     * @param content  文件内容
     */
    public static void generateFile(String filePath, String content) throws FileAlreadyExistsException
    {
        File file = new File(filePath);
        if (file.exists())
        {
            throw new FileAlreadyExistsException(filePath + "文件已经存在" );
        } else
        {
            FileUtil.saveWriter(file, content);
        }
    }

    /**
     * 字符编码
     */
    public static final String ENCODE = "UTF-8";

    /**
     * 获取模板文件路径
     *
     * @param clazz 类对象
     */
    public static String templatePath(Class<?> clazz)
    {
        return clazz.getResource("" ).getPath() + clazz.getSimpleName() + ".tpl";
    }

    /**
     * 保存文本文件
     *
     * @param file    文件对象
     * @param content 文件内容
     */
    public static void saveWriter(File file, String content)
    {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try
        {
            if (!file.exists())
            {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos, ENCODE);
            osw.write(content);
            osw.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            if (osw != null)
            {
                try
                {
                    osw.close();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
            if (fos != null)
            {
                try
                {
                    fos.close();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static String fileUpload(String attachid, String attachname, Blob accessorycontent, String filepath)
    {

        int one = attachname.lastIndexOf("." );
        String filetype = attachname.substring((one + 1), attachname.length());
        String urlfile = filepath + "/" + attachid + "." + filetype;

        if (null != accessorycontent)
        {

            CompletableFuture<String> future = CompletableFuture.supplyAsync(() ->
            {
                //用来检测程序运行时间
                long startTime = System.currentTimeMillis();
                System.out.println(startTime);
                System.out.println("开始附件保存本地:" + attachname);
                File updir = new File(filepath);
                if (!updir.exists())
                {
                    System.out.println("dir not exists, build it ..." );
                    updir.mkdir();
                }
                FileOutputStream fos = null;
                InputStream in = null;
                File files = new File(urlfile);
                if (!files.exists())
                {
                    try
                    {

                        fos = new FileOutputStream(files);
                        in = accessorycontent.getBinaryStream();
                        int len = (int) accessorycontent.length();

                        byte[] buffer = new byte[len]; // 建立缓冲区

                        while ((len = in.read(buffer)) != -1)
                        {

                            fos.write(buffer, 0, len);

                        }
                        fos.close();
                        in.close();

                        long endTime = System.currentTimeMillis();
                        System.out.println(endTime);
                        System.out.println("附件保存本地完成:" + attachname);

                    } catch (Exception e)
                    {
                        e.printStackTrace();

                        long endTime = System.currentTimeMillis();
                        System.out.println(endTime);
                        System.out.println("附件保存本地失败:" + attachname);

                    } finally
                    {
                        if (in != null)
                        {
                            try
                            {
                                in.close();
                            } catch (IOException e)
                            {
                            }
                        }
                        if (fos != null)
                        {
                            try
                            {
                                fos.close();
                            } catch (IOException e)
                            {
                            }
                        }
                    }
                } else
                {
                    long endTime = System.currentTimeMillis();
                    System.out.println(endTime);
                    System.out.println("附件本地已存在无需保存本地 :" + attachname);
                }

                return "task true";
            });

            future.whenComplete((result, exception) ->
            {
                if (null == exception)
                {
                    System.out.println("datas from previous task: " + result);
                }
            });

            return attachid + "." + filetype;
        } else
        {

            return "";
        }

    }

    public static String fileUp(String attachid, String attachname, byte[] accessorycontent, String filepath)
    {
        int one = attachname.lastIndexOf("." );
        String filetype = attachname.substring((one + 1), attachname.length());
        String urlfile = filepath + "/" + attachid + "." + filetype;

        if (null != accessorycontent)
        {

            CompletableFuture<String> future = CompletableFuture.supplyAsync(() ->
            {
                // long running task
                //用来检测程序运行时间
                long startTime = System.currentTimeMillis();
                System.out.println(startTime);
                System.out.println("开始附件保存本地:" + attachname);
                File updir = new File(filepath);
                if (!updir.exists())
                {
                    System.out.println("dir not exists, build it ..." );
                    updir.mkdir();
                }
                FileOutputStream fos = null;
                BufferedOutputStream bos = null;
                File files = new File(urlfile);
                if (!files.exists())
                {
                    try
                    {

                        fos = new FileOutputStream(files);
                        bos = new BufferedOutputStream(fos);
                        bos.write(accessorycontent);
                        bos.flush();
                        bos.close();
                        fos.flush();
                        fos.close();

                        long endTime = System.currentTimeMillis();
                        System.out.println(endTime);
                        System.out.println("附件保存本地完成:" + attachname);

                    } catch (Exception e)
                    {
                        e.printStackTrace();

                        long endTime = System.currentTimeMillis();
                        System.out.println(endTime);
                        System.out.println("附件保存本地失败:" + attachname);

                    } finally
                    {
                        if (bos != null)
                        {
                            try
                            {
                                bos.close();
                            } catch (IOException e)
                            {
                            }
                        }
                        if (fos != null)
                        {
                            try
                            {
                                fos.close();
                            } catch (IOException e)
                            {
                            }
                        }
                    }
                } else
                {
                    long endTime = System.currentTimeMillis();
                    System.out.println(endTime);
                    System.out.println("附件本地已存在无需保存本地 :" + attachname);
                }
                return "task true";
            });

            future.whenComplete((result, exception) ->
            {
                if (null == exception)
                {
                    System.out.println("datas from previous task: " + result);
                }
            });

            return attachid + "." + filetype;
        } else
        {

            return "";
        }

    }


}
