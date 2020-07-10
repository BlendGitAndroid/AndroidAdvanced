package com.blend.architecture.change_skin.skin;

import java.io.File;

public class Skin {

    /**
     * 文件校验md5值
     */
    public String md5 = "";
    /**
     * 下载地址
     */
    public String url = "xxxx";

    /**
     * 皮肤名
     */
    public String name = "";

    /**
     * 下载完成后缓存地址
     */
    public String path = "";

    public File file;

    public Skin(String md5, String name, String url) {
        this.md5 = md5;
        this.name = name;
        this.url = url;
    }

    public File getSkinFile(File theme) {
        if (null == file) {
            file = new File(theme, name);
        }
        path = file.getAbsolutePath();
        return file;
    }
}
