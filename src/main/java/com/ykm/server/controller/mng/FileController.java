package com.ykm.server.controller.mng;

import com.google.zxing.WriterException;
import com.ykm.server.consts.ResponseCode;
import com.ykm.server.controller.dto.ResponseDto;
import com.ykm.server.utils.OSSUtils;
import com.ykm.server.utils.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 广州引力网络科技有限公司
 * 功能：
 * 日期：2019/6/4-14:01
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
@RestController("/file")
@RequestMapping("/file")
public class FileController {
    private static final Logger log =  LoggerFactory.getLogger(FileController.class);

    @Autowired
    PathUtils pathUtils;

    @Value("${oss.accessUrl}")
    private String ossAccessUrl;

    @Autowired
    OSSUtils ossUtils;


    @RequestMapping("/upload")
    public ResponseDto<Map<String,Object>> upload(@RequestParam("file")MultipartFile file, String name) throws IOException, WriterException {

        Map<String, Object> rtn = new HashMap<>();
        rtn.put("success", false);
        rtn.put("message", "上传失败");
        rtn.put("url", "");

        String fileName = StringUtils.isEmpty(name)? file.getOriginalFilename() : name;

        log.info("the file name is {}, size is {} 字节", fileName, file.getSize());

        String url = ossAccessUrl + "mng/" + fileName;

        String tmpPath = pathUtils.getDir() + File.separator + "tmp";
        File tmpDir = new File(tmpPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        File localFile = new File(tmpPath, fileName);

        //如果是传入服务器  file.getInputStream();用输入输出流来读取
        //储存为本地文件
        file.transferTo(localFile);

        boolean ossUpload = ossUtils.uploadFile(localFile, name);
        localFile.deleteOnExit();

        if (!ossUpload) {
            return new ResponseDto<>(null, ResponseCode.Fail);
        }
        rtn.put("success", true);
        rtn.put("message", "上传成功");
        rtn.put("name", name);
        rtn.put("url", url);

        return new ResponseDto<>(rtn, ResponseCode.OK);
    }
}
