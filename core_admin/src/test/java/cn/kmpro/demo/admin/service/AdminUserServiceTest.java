package cn.kmpro.demo.admin.service;


import cn.kmpro.demo.admin.model.AdminUser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class AdminUserServiceTest {
        @Autowired
        AdminUserService adminUserService;

   @Test
    public void testSave(){
        AdminUser au = new AdminUser();
        au.setId("2222");
        au.setUsername("adadadad");
        au.setPassword("adadaadas");
        au.setName("张三ss");
        adminUserService.saveAdminUser(au);
        Assert.assertNotNull(au.getId());
    }
//public static void main(String[] args) {
//
//    System.out.println(System.getProperty("configurePath"));
//}
}
