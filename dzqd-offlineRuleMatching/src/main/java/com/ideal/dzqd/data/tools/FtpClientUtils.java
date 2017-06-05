package com.ideal.dzqd.data.tools;

import com.google.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yaloo on 2017/6/1.
 */
@Singleton
public class FtpClientUtils {

  private static final Logger LOG = LoggerFactory.getLogger(FtpClientUtils.class);
  private final FTPClient ftpClient;

  public FtpClientUtils() {
    ftpClient = new FTPClient();
  }

  /**
   * 建立FTP链接，FTP服务器地址、端口、登陆用户信息都在配置里配置即可。
   */
  public boolean connectFtp(String ftpAddress, String ftpPort, String frpUserName,
      String frpPassword) throws IOException {
    LOG.info("*****连接FTP服务器...*****");
    try {
      ftpClient.connect(ftpAddress, Integer.valueOf(ftpPort).intValue());
      ftpClient.setControlEncoding("UTF-8");
      if (ftpClient.login(frpUserName, frpPassword)) {
        LOG.info("*****连接FTP服务器成功！*****");
        return true;
      }

      int reply = ftpClient.getReplyCode();
      if (!FTPReply.isPositiveCompletion(reply)) {
        LOG.error("*****连接失败!响应代码为【" + reply + "】*****");
        ftpClient.disconnect();
      }
    } catch (Exception e) {
      LOG.error("*****连接失败：" + e.getMessage());
    }
    return false;
  }

  /**
   * 设置FTP客户端 被动模式、数据模式为二进制、字符编码UTF-8
   */
  public void setConnectType() {
    try {
      ftpClient.enterLocalPassiveMode();
      ftpClient.setDefaultTimeout(1000 * 120);//120秒
      ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
      ftpClient.setControlEncoding("UTF-8");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * 断开与远程服务器的连接
   */
  public void disconnect() {
    if (ftpClient.isConnected()) {
      try {
        ftpClient.disconnect();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  /**
   * 过滤不符合的文件并批量下载
   *
   * @param remoteFileReg 文件前缀的正则表达式
   * @param localPath 本地路径 .property 文件配置
   * @param remote_down_path ftp文件路径
   * @return List 下载到本地的文件路径 集合
   */
  @SuppressWarnings("unchecked")
  public List<String> downloads(String remoteFileReg, String localPath, String remote_down_path)
      throws IOException {
    List<String> fileNames = new ArrayList<>();
    LOG.info("*****转移到服务器目录：" + remote_down_path);
    setConnectType();
    boolean changeFlag = ftpClient.changeWorkingDirectory(remote_down_path);
    FTPFile[] files = ftpClient.listFiles();
    //String[] names = ftpClient.listNames();
    LOG.info("*****改变目录是否成功：" + changeFlag);
    LOG.info("*****服务器上report目录下所有校验报告的文件数为：【" + files.length + "】");
    if (files.length == 0) {
      LOG.info("*****未在服务器上找到文件！*****");
      return null;
    } else {//目录下有文件
      //把 bak文件的前缀找出来   ，区分读取和未读取的xls 和 xlsx ,只下载 未读取的文件 
      List<String> bakList = new ArrayList<String>();
      List<String> list = new ArrayList<String>();

      for (int i = 0; i < files.length; i++) {
        FTPFile ftpFile = files[i];
        String fileName = ftpFile.getName();

        if (!fileName.endsWith(".bak") && ftpFile == null) {
          LOG.info("*******  " + fileName + "文件无数据!");
          continue;
        }

        //匹配指定的文件前缀 和后缀 为    .bak 格式的文件
        //bak 文件是文件读取完毕后生成的标记文件
        Pattern bak = Pattern.compile("^" + remoteFileReg + "\\.bak");
        Matcher m = bak.matcher(fileName);
        if (m.find()) {
          //取.bak文件的 前缀
          //System.out.println(fileName);
          //System.out.println(fileName.split("\\.")[0]);
          bakList.add(fileName.split("\\.")[0]);
          continue;
        }

        //匹配指定的文件前缀 和后缀 为    .gz  格式的文件
        //TODO 以后遇到其他的格式文件 需要把后缀抽出来作为参数传入  \.DAT\.gz$
        String reg = "^" + remoteFileReg + "\\.DAT\\.gz$";
        Pattern xls = Pattern.compile(reg);

        Matcher mm = xls.matcher(fileName);
        if (mm.find()) {
          list.add(fileName);
          continue;
        }
      }

      //过滤掉 已解析的文件
      for (int i = 0; i < list.size(); i++) {
        String xls = list.get(i);
        for (String bak : bakList) {
          //bak文件存在 , 去掉此文件
          if (xls.indexOf(bak) != -1) {
            list.remove(i);
          }
        }
      }

      for (String fFile : list) {
        //下载未读取的文件
        File downFile = new File(localPath + fFile);
        //System.out.println(localPath);
        File downPath = new File(localPath);
        if (!downPath.exists()) {
          downPath.mkdirs();
        }
        String fileDir = remote_down_path + fFile;
        OutputStream os = new FileOutputStream(downFile);
        ftpClient.retrieveFile(new String(fileDir.getBytes("UTF-8"), "ISO-8859-1"), os);
        LOG.info("*****文件已下载到：" + downFile.getAbsolutePath() + "******");
        fileNames.add(downFile.getAbsolutePath());
        os.close();
      }
      LOG.info("**** 此次共下载了" + list.size() + "个文件! *****");
    }
    return fileNames;
  }

  /**
   * 上传标志文件
   */
  public boolean upload(String localFileName, String remoteFileName) {

    boolean b = false;
    try {
      File file = new File(localFileName);
      FileInputStream input = new FileInputStream(file);
      b = ftpClient.changeWorkingDirectory(remoteFileName);
      LOG.info("*****改变目录是否成功：" + b);
      String remoteFile = remoteFileName + file.getName();
      b = ftpClient.storeFile(new String(remoteFile.getBytes("UTF-8"), "ISO-8859-1"), input);
      if (b) {
        LOG.info(" ****** 标志文件" + localFileName + "上传成功!");
      }
      input.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return b;
  }

  public static void main(String[] args) throws IOException {
    FtpClientUtils ftpClientUtils = new FtpClientUtils();
    ftpClientUtils.connectFtp("10.0.180.25", "21", "anhui", "anhui@123");
    List<String> files = ftpClientUtils
        .downloads("20170530_13_.*", "/Users/yaloo/Downloads/", "/sceneuse/");
    ftpClientUtils.disconnect();

    for (String file : files) {
      System.out.println(file);
    }

  }
}