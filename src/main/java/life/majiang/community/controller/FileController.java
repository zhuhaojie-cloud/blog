package life.majiang.community.controller;

import life.majiang.community.dto.FileDTO;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.util.AliyunOSSUtil;
import life.majiang.community.util.ExcelOperationUtil;
import org.apache.catalina.core.ApplicationPart;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by codedrinker on 2019/6/26.
 */
@Controller
public class FileController {
    /*@Autowired
    private UCloudProvider uCloudProvider;*/
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    private static final String TO_PATH="upLoad";
    private static final String RETURN_PATH="success";

    @Autowired
    private AliyunOSSUtil aliyunOSSUtil;
    @Autowired
    private UserMapper userMapper;
    //云存储图片方法
    //优点是在云存储上，服务器迁移时不会再传图片
    //下面是选择ucloud的api写的
   /*@RequestMapping("/file/upload")
    @ResponseBody
    public FileDTO upload(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("editormd-image-file");
        try {
            String fileName = uCloudProvider.upload(file.getInputStream(), file.getContentType(), file.getOriginalFilename());
            FileDTO fileDTO = new FileDTO();
            fileDTO.setSuccess(1);
            fileDTO.setUrl(fileName);
            return fileDTO;
        } catch (Exception e) {
            FileDTO fileDTO = new FileDTO();
            fileDTO.setSuccess(0);
            fileDTO.setMessage("上传失败");
            return fileDTO;
        }
    }*/
   //阿里云的oss
    @RequestMapping("/file/upload")
    @ResponseBody
    public FileDTO upload(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("editormd-image-file");
        file.getContentType();
        String filename = file.getOriginalFilename();
        try {
            File newFile = new File(filename);
            FileOutputStream os = new FileOutputStream(newFile);
            os.write(file.getBytes());
            os.close();
            file.transferTo(newFile);
            // 上传到OSS
            String uploadUrl = aliyunOSSUtil.upLoad(newFile);
            FileDTO fileDTO = new FileDTO();
            fileDTO.setSuccess(1);
            fileDTO.setUrl("地址"+uploadUrl);
            return fileDTO;
        } catch (Exception ex) {
           // ex.printStackTrace();
            FileDTO fileDTO = new FileDTO();
            fileDTO.setSuccess(0);
            fileDTO.setMessage("上传失败");
            return fileDTO;
        }
      /*  FileDTO fileDTO = new FileDTO();
        fileDTO.setSuccess(1);
        fileDTO.setMessage("上传成功");
        fileDTO.setUrl("/images/1.png");
        return fileDTO;*/
    }
    @RequestMapping("/daoruExcel")
    @ResponseBody
    public String uploadExcel(HttpServletRequest request) {
        String path = request.getServletContext().getRealPath("/");    //获取服务器地址
        Part p = null;//获取用户选择的上传文件
        try {
            p = request.getPart("file1");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }
        String fname2 = "";
        ApplicationPart ap = (ApplicationPart) p;
        //获取上传文件名
        String fname1 = ap.getSubmittedFileName();
        //以下代码取得文件的后缀名
        int dot = fname1.lastIndexOf(".");
        String extentname = fname1.substring(dot + 1);
        String firstname = "emp1";
        fname2 = firstname + "." + extentname;
        // 写入 web 项目根路径下的upload文件夹中
        try {
            p.write(path + fname2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //写入数据库
        try {
            request.setCharacterEncoding("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String filePath = request.getRealPath(fname2);
        System.out.println(filePath);

        ExcelOperationUtil excelUtil = new ExcelOperationUtil();
        List<User> list = excelUtil.readExcelFileToDB(filePath,User.class);
        for (User emp : list) {
            userMapper.insert(emp);
        }
        return "hello2";
    }
    @RequestMapping("/file/uploadImg")
    //为刷新资源访问不了，需要重启服务器
    public String uploadImg(HttpServletRequest request) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("uploadImg");
        file.getContentType();
        String filename = file.getOriginalFilename();
        File newFile = new File(filename);
        FileOutputStream os = new FileOutputStream(newFile);
        os.write(file.getBytes());
        os.close();
        file.transferTo(newFile);
        // 上传到OSS
        String uploadUrl = aliyunOSSUtil.upLoad(newFile);
        user.setAvatarUrl("地址"+uploadUrl);
        userMapper.updateByPrimaryKey(user);
        return  "redirect:/userhome/home/";
    }
    /*@PostMapping("uploadFile")
    @ResponseBody
    public String uploadFile(MultipartFile multipartFile, HttpServletRequest request) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return "上传文件为空...";
        }
        //basePath拼接完成后，形如：http://192.168.1.20:8080/fileServer
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()+"/images/";
        System.out.println("basePath=" + basePath);
        // String fileName = multipartFile.getOriginalFilename();
        //以下代码取得当前日期
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");//可以方便地修改日期格式
        String firstname = dateFormat.format( now );
        //产生随机数
        Random rd=new Random();
        String xxxx="";
        for(int i=1;i<=4;i++){
            xxxx=xxxx+(rd.nextInt(10)+1);
        }
        //拼接文件名
        firstname=firstname+xxxx+"."+"png";
        File saveFile = new File(basePath, firstname);
        System.out.println("文件保存成功：" + saveFile.getPath());
        multipartFile.transferTo(saveFile);//文件保存

        return saveFile.getPath().toString();
    }*/
    public static String getcontentType(String FilenameExtension) {
        if (FilenameExtension.equalsIgnoreCase(".bmp")) {
            return "image/bmp";
        }
        if (FilenameExtension.equalsIgnoreCase(".gif")) {
            return "image/gif";
        }
        if (FilenameExtension.equalsIgnoreCase(".jpeg") ||
                FilenameExtension.equalsIgnoreCase(".jpg") ||
                FilenameExtension.equalsIgnoreCase(".png")) {
            return "image/jpg";
        }
        if (FilenameExtension.equalsIgnoreCase(".html")) {
            return "text/html";
        }
        if (FilenameExtension.equalsIgnoreCase(".txt")) {
            return "text/plain";
        }
        if (FilenameExtension.equalsIgnoreCase(".vsd")) {
            return "application/vnd.visio";
        }
        if (FilenameExtension.equalsIgnoreCase(".pptx") ||
                FilenameExtension.equalsIgnoreCase(".ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (FilenameExtension.equalsIgnoreCase(".docx") ||
                FilenameExtension.equalsIgnoreCase(".doc")) {
            return "application/msword";
        }
        if (FilenameExtension.equalsIgnoreCase(".xml")) {
            return "text/xml";
        }
        return "image/jpg";
    }
}
