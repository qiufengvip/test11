package cn.kmpro.Config.dao;


import cn.kmpro.Config.model.KmConfig;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface KmConfigDao  extends PagingAndSortingRepository<KmConfig,String> {
}
