package life.majiang.community.controller;

import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.mapper.AttentionMapper;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.*;
import life.majiang.community.service.CollectService;
import life.majiang.community.service.NotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class UserHomeController {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AttentionMapper attentionMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CollectService collectService;
    @GetMapping("userhome/{action}")
    public String userhome(HttpServletRequest request, Model model, @PathVariable(name = "action") String action,@RequestParam(name = "page", defaultValue = "1") Integer page,
                           @RequestParam(name = "size", defaultValue = "5") Integer size){
        User user = (User) request.getSession().getAttribute("user");
        //发帖数
        QuestionExample questionExample=new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(user.getId());
        Integer totalCount=(int)questionMapper.countByExample(questionExample);
        //粉丝数
        AttentionExample attentionExample=new AttentionExample();
        attentionExample.createCriteria().andAttentionedUserEqualTo(user.getId());
        Integer attentionmeCount=(int)attentionMapper.countByExample(attentionExample);
        //关注数
        AttentionExample attentionExample1=new AttentionExample();
        attentionExample1.createCriteria().andAttentionUserEqualTo(user.getId());
        List<Attention> attentions= attentionMapper.selectByExample(attentionExample1);
        Integer meattentionCount=attentions.size();
        //得到关注的所有人的id
        //循环生成questions
        //再按时间顺序排序
        if ("home".equals(action)) {
            List<Question> questions=new ArrayList<>();
            List<QuestionDTO> questionDTOList = new ArrayList<>();
            for(Attention attention:attentions){
                //Long attentioneuUserId=attention.getAttentionedUser();
                QuestionExample questionExample1=new QuestionExample();
                questionExample1.createCriteria().andCreatorEqualTo(attention.getAttentionedUser());
                List<Question> questions1=new ArrayList<>();
                questions1=questionMapper.selectByExample(questionExample1);
                questions.addAll(questions1);
            }
            Collections.sort(questions, new Comparator<Question>() {
                @Override
                public int compare(Question question1, Question question2) {
                    Long id1 = question1.getId();
                    Long id2 = question2.getId();
                    //可以按Question对象的其他属性排序，只要属性支持compareTo方法
                    return id2.compareTo(id1);
                }
            });
            for (Question question : questions) {
                User user1 = userMapper.selectByPrimaryKey(question.getCreator());
                QuestionDTO questionDTO = new QuestionDTO();
                BeanUtils.copyProperties(question, questionDTO);
                questionDTO.setUser(user1);
                questionDTOList.add(questionDTO);
            }
            model.addAttribute("questionDTOList",questionDTOList);
            model.addAttribute("section", "home");
            model.addAttribute("sectionName", "我的主页");
        } else if ("notification".equals(action)) {
            PaginationDTO paginationDTO = notificationService.list(user.getId(), page, size);
            model.addAttribute("pagination", paginationDTO);
            model.addAttribute("section", "notification");
            model.addAttribute("sectionName", "我的通知");
        }else if ("collect".equals(action)) {
            PaginationDTO paginationDTO = collectService.list(user.getId(), page, size);
            model.addAttribute("pagination", paginationDTO);
            model.addAttribute("section", "collect");
            model.addAttribute("sectionName", "我的收藏");
        }
        model.addAttribute("attentionmeCount",attentionmeCount);
        model.addAttribute("meattentionCount",meattentionCount);
        model.addAttribute("totalCount",totalCount);
        return  "userhome";
    }

   /* @ResponseBody
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public String updateImg(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        long id=user.getId();
        String path = this.getServletContext().getRealPath("/");	//获取服务器地址
        Part p = request.getPart("uploadImg");//获取用户选择的上传文件
        String fname2="";
        if (p.getContentType().contains("image")) {					// 仅处理上传的图像文件
            ApplicationPart ap = (ApplicationPart) p;
            //获取上传文件名
            String fname1 = ap.getSubmittedFileName();
            System.out.println(ap.getSubmittedFileName());
            System.out.println(ap.getClass());
            System.out.println(ap.getInputStream());
            //以下代码取得文件的后缀名
            int dot=fname1.lastIndexOf(".");
            String extentname=fname1.substring(dot+1);
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
            firstname=firstname+xxxx;
            fname2=firstname+"."+extentname;
            // 写入 web 项目根路径下的upload文件夹中
            p.write(path + "/upload/" + fname2);
            System.out.print(path + "/upload/" + fname2);
        }
        else{

            System.out.println("aa");
        }
        String pic_url="/project1/upload/" + fname2;
        System.out.println(pic_url+"mm");
        //System.out.println(img+"qq");
        boolean flag= UserService.AdduserimgByName(id,pic_url);
        System.out.println(pic_url+"mm");
        if(flag) {
            response.sendRedirect("Student_User");
        }else {
            System.out.println("失败");
        }
        return  "redirect:/";
    }*/
}
