package com.ykm.server.utils;

import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * wenxy技有限公司
 * 功能：
 * 日期：2019/7/12-16:59
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
@Component
public class PathUtils {

    public File getBaseJarPath() {
        ApplicationHome home = new ApplicationHome(getClass());
        File jarFile = home.getSource();

        if(jarFile == null){
           return  home.getDir();
        }
        return jarFile.getParentFile();
    }

    public String  getDir() {
        File file = getBaseJarPath();
        if(file.isDirectory()){
            return file.getAbsolutePath();
        }
        return file.getParent();
    }
}
