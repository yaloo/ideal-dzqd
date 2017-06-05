package com.ideal.dzqd.data.internal;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.ideal.dzqd.data.conf.AppConfig;
import com.ideal.dzqd.data.tools.FtpClientUtils;
import com.ideal.dzqd.data.tools.ZipUtils;
import com.ideal.dzqd.data.vo.DownloadEvent;
import java.io.IOException;
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
    ftp.connectFtp("10.0.180.25", "21", event.getFtpUser(), event.getFtpPwd());
    List<String> files = ftp
        .downloads(event.getRemoteFileReg(), event.getLocalPath(), event.getRemote_down_path());
    ftp.disconnect();
    String localUnzipFile = Strings.join(files, ',').replaceAll(".gz","");
    event.setLocalPath(localUnzipFile);

    // 解压文件
    for(String file : files) {
      ZipUtils.gunzip(file);
    }
    eventBus.post(event);
  }
}