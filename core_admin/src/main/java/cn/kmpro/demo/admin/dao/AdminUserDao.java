package cn.kmpro.demo.admin.dao;

import cn.kmpro.demo.admin.model.AdminUser;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AdminUserDao extends PagingAndSortingRepository<AdminUser,String> {

}
