package cn.kmpro.demo.admin.controller;

import cn.kmpro.demo.admin.model.AdminUser;
import cn.kmpro.demo.admin.service.AdminUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/admin")
public class HomeController {
    @Autowired
    protected AdminUserService adminUserService;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(method = RequestMethod.GET,value = "/index")
    public String home(){
        logger.info("accessing home!");
        return "home";
    }


    //查询全部管理员
    @RequestMapping(method = RequestMethod.GET,value = "/query")
    public ModelAndView queryAll() throws Exception {
        //查询全部管理员
          ModelAndView modelAndView = new ModelAndView();
//        int page=0;//当前页的索引
//        int size=5;//每页显示5条数据
//        PageRequest pagetable = new PageRequest(page,size);
//        Page<AdminUser> p = this.adminUserService.queryAllTable(pagetable);

        List<AdminUser> adminUsers = adminUserService.queryAll();
        logger.info("begin query");

//        logger.info("数据的总条数：",p.getTotalElements());
//        logger.info("总页数",p.getTotalPages());
//        for (AdminUser user : p) {
//            logger.info("user",user);
//        }
        modelAndView.addObject("adminUsers", adminUsers);
        modelAndView.setViewName("admin/admin_list");
        return modelAndView;
    }


    //删除管理员
    @RequestMapping(method = RequestMethod.GET,value = "/delete")
    public String delete(String ids){

        ids = ids.substring(0, ids.length() -1);

        String[] idss = ids.split(",");
        AdminUser adminUser = new AdminUser();

        for(int i=0;i<idss.length;i++){

            adminUser.setId(idss[i]);
            adminUserService.delete(ids);
        }

        logger.info("delete admin!");
        return "redirect:/admin/query";
    }

    //进入添加页面
    @RequestMapping("/addPage")
    public String addPage(){

        logger.info("get into addPage");
        return "admin/admin_add";

    }
    //添加管理员
    @RequestMapping(method = RequestMethod.POST,value = "/add")
    public String add(AdminUser adminUser){

        logger.info("add admin!");
        adminUserService.add(adminUser);
        return "redirect:/admin/query";
    }

    //进入修改页面
    @RequestMapping("/editPage")
    public  String editPage(HttpServletRequest request,@RequestParam(value="id")String id){
        AdminUser adminUser = adminUserService.queryById(id);
        logger.info("get into toEdit",adminUser);
        request.setAttribute("adminUser",adminUser);
        return "admin/admin_edit";
    }

    //修改管理员
    @RequestMapping(method = RequestMethod.POST,value = "/edit")
    public String edit(AdminUser adminUser){

        logger.info("edit admin!");
        adminUserService.edit(adminUser);
        return "redirect:/admin/query";
    }
}
