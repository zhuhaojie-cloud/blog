package life.majiang.community.controller;

import life.majiang.community.dto.CommentDTO;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.enums.CommentTypeEnum;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeException;
import life.majiang.community.mapper.CollectMapper;
import life.majiang.community.model.Collect;
import life.majiang.community.model.CollectExample;
import life.majiang.community.model.User;
import life.majiang.community.service.CommentService;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by codedrinker on 2019/5/21.
 */
@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CollectMapper collectMapper;
    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") String id, Model model, HttpServletRequest request) {
        Long questionId = null;
        try {
            questionId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new CustomizeException(CustomizeErrorCode.INVALID_INPUT);
        }
        User user = (User) request.getSession().getAttribute("user");
        //根据userid和questionid查询是否收藏过。
        if(user!=null) {
            CollectExample collectExample = new CollectExample();
            collectExample.createCriteria().andUserEqualTo(user.getId())
                    .andQuestionEqualTo(questionId);
            List<Collect> collect = collectMapper.selectByExample(collectExample);
            long collectId = 0;
            if (collect.size() != 0) {
                collectId = collect.get(0).getId();
            }
            //返回collectid
            model.addAttribute("collectId", collectId);
        }
        else{
            long collectId1=-1;
            model.addAttribute("collectId", collectId1);
        }
        QuestionDTO questionDTO = questionService.getById(questionId);
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);
        List<CommentDTO> comments = commentService.listByTargetId(questionId, CommentTypeEnum.QUESTION);
        //累加阅读数
       questionService.incView(questionId);
        model.addAttribute("question", questionDTO);
        model.addAttribute("comments", comments);
        model.addAttribute("relatedQuestions", relatedQuestions);
        return "question";
    }
    @GetMapping("/collect/{id}")
    public String question(@PathVariable(name = "id") Long questionId, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        //增加
        Collect collect=new Collect();
        collect.setUser(user.getId());
        collect.setQuestion(questionId);
        collect.setTime(System.currentTimeMillis());
        collectMapper.insert(collect);
        return "redirect:/question/"+questionId;
    }
    @GetMapping("/discollect")
    public String question(@RequestParam(name="collectId") long collectId, @RequestParam(name="id") long questionId){
        //删除
        collectMapper.deleteByPrimaryKey(collectId);
        return "redirect:/question/"+questionId;
    }
}
