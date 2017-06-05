package com.ideal.dzqd.data.vo;

import java.util.EventObject;

/**
 * Created by yaloo on 2017/5/26.
 */
public class DownloadEvent extends EventObject {

  //private String subSceneId;
  private String provinceCode;
  private String localPath;
  private String ftpUser;
  private String ftpPwd;
  //ftp文件前缀的正则表达式
  private String remoteFileReg;
  // ftp文件路径
  private String remote_down_path;
  private String localSavePath;

  /**
   * Constructs a prototypical Event.
   *
   * @param source The object on which the Event initially occurred.
   * @throws IllegalArgumentException if source is null.
   */
  public DownloadEvent(Object source) {
    this(source,null,null);
  }

  public DownloadEvent(Object source, String provinceCode, String localPath) {
    super(source);
    this.provinceCode = provinceCode;
    this.localPath = localPath;
  }

  public DownloadEvent(Object source, String provinceCode, String localPath, String ftpUser,
      String ftpPwd, String remoteFileReg, String remote_down_path, String localSavePath) {
    super(source);
    this.provinceCode = provinceCode;
    this.localPath = localPath;
    this.ftpUser = ftpUser;
    this.ftpPwd = ftpPwd;
    this.remoteFileReg = remoteFileReg;
    this.remote_down_path = remote_down_path;
  }

  public String getProvinceCode() {
    return provinceCode;
  }

  public void setProvinceCode(String provinceCode) {
    this.provinceCode = provinceCode;
  }

  public String getLocalPath() {
    return localPath;
  }

  public void setLocalPath(String localPath) {
    this.localPath = localPath;
  }

  public String getFtpUser() {
    return ftpUser;
  }

  public void setFtpUser(String ftpUser) {
    this.ftpUser = ftpUser;
  }

  public String getFtpPwd() {
    return ftpPwd;
  }

  public void setFtpPwd(String ftpPwd) {
    this.ftpPwd = ftpPwd;
  }

  public String getRemoteFileReg() {
    return remoteFileReg;
  }

  public void setRemoteFileReg(String remoteFileReg) {
    this.remoteFileReg = remoteFileReg;
  }

  public String getRemote_down_path() {
    return remote_down_path;
  }

  public void setRemote_down_path(String remote_down_path) {
    this.remote_down_path = remote_down_path;
  }

}
