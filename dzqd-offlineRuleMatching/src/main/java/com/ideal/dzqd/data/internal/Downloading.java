package com.ideal.dzqd.data.internal;

import com.google.common.base.Joiner;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.ideal.dzqd.data.conf.AppConfig;
import com.ideal.dzqd.data.tools.FtpClientUtils;
import com.ideal.dzqd.data.tools.ZipUtils;
import com.ideal.dzqd.data.vo.DownloadEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yaloo on 2017/5/26.
 */

public class Downloading {

  private static final Logger LOG = LoggerFactory.getLogger(Downloading.class);

  private final AppConfig config;
  private final FtpClientUtils ftp;

  @Inject
  public Downloading(final AppConfig config, final FtpClientUtils ftp) {
    this.config = config;
    this.ftp = ftp;
  }

  public void download(EventBus eventBus, DownloadEvent event) throws IOException {

    //if(!isDownloaded(event)) {
      ftp.connectFtp("10.0.180.25", "21", event.getFtpUser(), event.getFtpPwd());
      List<String> files = ftp
          .downloads(event.getRemoteFileReg(), event.getLocalPath(), event.getRemote_down_path());
      ftp.disconnect();
      String localUnzipFile = Strings.join(files, ',').replaceAll(".gz", "");
      event.setLocalPath(localUnzipFile);

      // 解压文件
      for (String file : files) {
        ZipUtils.gunzip(file);
      }

      if (files != null && files.size() > 0)
        eventBus.post(event);
    //}
  }

  //判断是否已经下载
  private boolean isDownloaded(DownloadEvent event) {
    String[] cmd = new String[]{"/bin/sh", "-c",
        "ls -l " + Joiner.on('_').join(event.getLocalPath(),event.getCycle(),event.getProvinceCode(),".*.gzip") + " | grep \"^-\"|wc -l"};
    String counts = null;
    try {

      Process ps = Runtime.getRuntime().exec(cmd);
      InputStreamReader reader = new InputStreamReader(ps.getInputStream());
      BufferedReader br = new BufferedReader(reader);
      counts = br.readLine();

      br.close();
      reader.close();
      ps.destroy();
    } catch (IOException e) {
      e.printStackTrace();
    }

    int count = Integer.parseInt(counts);
    if (count > 0)
      return true;
    return false;
  }
}