package com.ideal.dzqd.data;

import com.ideal.dzqd.data.vo.DownloadEvent;
import com.ideal.dzqd.data.internal.MatchingServer;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        /**
         * 命令行，传参
         */
        DownloadEvent event = new DownloadEvent(App.class);
        event.setFtpUser("anhui");
        event.setFtpPwd("anhui@123");
        event.setProvinceCode("13");
        event.setRemote_down_path("/sceneuse/");
        event.setRemoteFileReg("20170601_13_.*");
        event.setLocalPath("/Users/yaloo/Downloads/");

        new MatchingServer(event).start();
    }
}