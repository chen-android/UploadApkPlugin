package com.cs.plugin;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;

public class UploadApkPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        UploadApkPluginExtension uploadApkPlugin = project.getExtensions().create("UploadApkPgyerParams", UploadApkPluginExtension.class);

        project.task("uploadToPgyer", task -> {
            task.doLast( lastTask -> {
                if (StringUtils.isBlank(uploadApkPlugin.apkFileLocation)) {
                    throw new IllegalArgumentException("apk文件地址没有配置");
                }
                if (StringUtils.isBlank(uploadApkPlugin.apiKey)) {
                    throw new IllegalArgumentException("apiKey没有配置");
                }
                File apkFilePath = new File(uploadApkPlugin.apkFileLocation);
                if (apkFilePath.exists()) {
                    if (apkFilePath.isDirectory()) {
                        //如果配置的是文件夹，则找目录下第一个.apk文件
                        System.out.println("当前地址：" + uploadApkPlugin.apkFileLocation + "    配置的是目录");
                        File[] files = apkFilePath.listFiles((dir, name) -> name.endsWith(".apk"));
                        if (files != null) {
                            File selectApk = files[0];
                            System.out.println("选择到apk文件：" + selectApk.getName());
                            uploadApk(selectApk, uploadApkPlugin);
                        }else{
                            System.out.println("当前目录下没有apk文件");
                        }
                    } else {
                        //配置的直接对应是apk文件
                        System.out.println("当前地址：" + uploadApkPlugin.apkFileLocation + "    配置的是文件");
                        System.out.println("选择到apk文件：" + apkFilePath.getName());
                        uploadApk(apkFilePath, uploadApkPlugin);
                    }
                } else {
                    throw new RuntimeException("当前地址： " + uploadApkPlugin.apkFileLocation + "不存在");
                }
            });
        });
    }

    private void uploadApk(File file, UploadApkPluginExtension extension) {
        String url = "https://www.pgyer.com/apiv2/app/upload";
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody fileBody = RequestBody.create(file, MediaType.parse("application/octet-stream"));

        MultipartBody body = new MultipartBody.Builder()
                .setType(MediaType.parse("multipart/form-data"))
                .addFormDataPart("file", "app.apk", fileBody)
                .addFormDataPart("buildInstallType", extension.buildInstallType)
                .addFormDataPart("buildPassword", extension.buildPassword)
                .addFormDataPart("buildUpdateDescription", extension.buildUpdateDescription)
                .addFormDataPart("_api_key", extension.apiKey)
                .build();

        final Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response execute = call.execute();
            boolean successful = execute.isSuccessful();
            System.out.println("请求结果："+successful);
            System.out.println("请求结果"+execute.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
