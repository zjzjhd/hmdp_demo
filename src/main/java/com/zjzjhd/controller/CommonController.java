package com.zjzjhd.controller;

import com.zjzjhd.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: 文件的上传和下载
 * @date 2022/11/15 10:11
 */

@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * @description: 文件上传方法
     * @param: file
     * @return: com.zjzjhd.common.R<java.lang.String>
     */
    @PostMapping("/upload")
    //file 参数名必须跟前端传回来的数据保持一致
    public R<String> upload(MultipartFile file) {

        //file是一个临时文件，需要转存到指定的位置
        //原文件名
        String originalFilename = file.getOriginalFilename();
        //后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名
        String fileName = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是是否存在
        if (!dir.exists()) {
            dir.mkdir(); //文件目录不存在，直接创建一个目录

        }

        try {
            //把前端传过来的文件进行转存
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);

    }

    /**
     * @description: 文件下载
     * @param: name
     * @param: response
     * @return: void
     */
    //输出流需要通过response来获得
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {

        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //输出流，通过输出流将文件写回浏览器 在浏览器展示
            ServletOutputStream servletOutputStream = response.getOutputStream();

            //设置写回去的文件类型
            response.setContentType("image/jpeg");
            //定义缓存区，准备读写文件
            int len = 0;
            byte[] buff = new byte[1024];
            while ((len = fileInputStream.read(buff)) != -1) {
                servletOutputStream.write(buff, 0, len);
                servletOutputStream.flush();
            }

            //关流
            servletOutputStream.close();
            fileInputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
