package cn.kmpro.demo.admin.service;

import cn.kmpro.demo.admin.dao.AdminUserDao;
import cn.kmpro.demo.admin.model.AdminUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class AdminUserService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    AdminUserDao adminUserDao;

    public AdminUser getAdminUser(String id){

        return adminUserDao.findOne(id);
    }

    @Transactional
    public void saveAdminUser(AdminUser adminUser){
        adminUserDao.save(adminUser);
        logger.info("save admin user success:{}",adminUser);
    }



    public List<AdminUser> queryAll() {
        return (List<AdminUser>) adminUserDao.findAll();
    }

    public void delete(String ids) {
        adminUserDao.delete(ids);
    }

    public void add(AdminUser adminUser) {
        adminUserDao.save(adminUser);
    }

    public AdminUser queryById(String id) {
        return   adminUserDao.findOne(id);
    }

    public void edit(AdminUser adminUser) {
        adminUserDao.save(adminUser);
    }

    public Page<AdminUser> queryAllTable(PageRequest pagetable) throws Exception {

        return adminUserDao.findAll(pagetable);
    }
}
