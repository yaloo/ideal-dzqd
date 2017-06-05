package com.ideal.dzqd.data;

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