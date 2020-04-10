package life.majiang.community.controller;

import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.model.UserExample;
import life.majiang.community.util.shiyan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ShiyanController {
    @Autowired
    private UserMapper userMapper;
    @GetMapping("/daochu")
    public String shiyan(HttpServletResponse response){
        List<User> user_list=new ArrayList<User>();
        UserExample example=new UserExample();
        example.createCriteria().andIdIsNotNull();
        user_list=userMapper.selectByExample(example);
        System.out.println(user_list.size());
        String filename= null;//中文文件名必须使用此句话
        try {
            filename = new String("document.xls".getBytes(),"iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("application/octet-stream");
        response.setContentType("application/OCTET-STREAM;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename="+filename );

        String[] headers = { "编号","编号", "姓名", "Token","创建时间", "登录时间" ,"用户类型","头像","电话","密码"};  //表格的标题栏
        try {
            shiyan<User> ex = new shiyan<User>();  //构造导出类

            OutputStream out = new BufferedOutputStream(response.getOutputStream());

            String  title = user_list.get(0).getName();  //title需要自己指定 比如写Sheet

            ex.exportExcel(title,headers, user_list, out);  //title是excel表中底部显示的表格名，如Sheet
            out.close();
            //JOptionPane.showMessageDialog(null, "导出成功!");
            System.out.println("excel导出成功！");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "shiyan1";//.getUser().getId()
    }
}
