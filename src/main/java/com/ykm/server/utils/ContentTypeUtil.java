package com.ykm.server.utils;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;

/**
 * 广州引力网络科技有限公司
 * 功能：
 * 日期：2019/12/24-11:53
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
public class ContentTypeUtil {

    public static boolean isImage(File file){
        MimetypesFileTypeMap mtftp = new MimetypesFileTypeMap();
        mtftp.addMimeTypes("image png tif jpg jpeg bmp");
        String mimetype= mtftp.getContentType(file);
        String type = mimetype.split("/")[0];
        return type.equals("image");
    }

    public static String getContentType(File file){
        return new MimetypesFileTypeMap().getContentType(file);
    }

    public static void main(String[] args){
        String contentType = new MimetypesFileTypeMap().getContentType(new File("/Users/wenxiaoyu/标签 (1).png"));
        System.out.println(contentType);
    }
}
