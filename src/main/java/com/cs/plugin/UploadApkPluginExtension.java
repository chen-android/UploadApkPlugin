package com.cs.plugin;

public class UploadApkPluginExtension {
    public String apkFileLocation;
    public String apiKey;
    /**
     * 应用安装方式，值为(1,2,3，默认为1 公开安装)。1：公开安装，2：密码安装，3：邀请安装
     */
    public String buildInstallType;
    /**
     * 密码安装的密码
     */
    public String buildPassword;
    public String buildUpdateDescription;
}
