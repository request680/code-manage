package com.example.filemanager.controller;

import com.example.filemanager.service.FileManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

/**
 * @author mr.liu
 * @projectName file-manager
 * @package_name com.example.filemanager.controller
 * @className FileManagerController
 * @description Controller
 * @date 2022/2/13 12:06
 */
@Slf4j
@RestController
@RequestMapping("/file-manager")
public class FileManagerController {

    @Autowired
    private FileManagerService fileManagerService;

    @PostMapping("/reverse")
    public String fileReverseService() {
        try {
            fileManagerService.fileReverse();
        } catch (IOException e){
            log.error("文件内容反转错误");
        }
        return null;
    }
}
