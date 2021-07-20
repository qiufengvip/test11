package cn.kmpro.Config.service;


import cn.kmpro.Config.dao.KmConfigDao;
import cn.kmpro.Config.model.KmConfig;
import cn.kmpro.Config.util.ConfigFormPropertiesUtil;
import cn.kmpro.Config.util.Putdata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @desc
 */
@Service
public class ConfigService {


    @Autowired
    private KmConfigDao kmConfig;

//    private Map<String,Object> DaoConfig;


    /**
     * @desc  从数据库加载配置项
     * @return 无
     */
//    @PostConstruct
//    public String getDaoConfig(){
//        DaoConfig =new HashMap<>();
//        Iterable<KmConfig> all = kmConfig.findAll();
//        all.forEach( as->{
//            DaoConfig.put(as.getField(),as.getVlaue());
//        });
//        System.out.println(DaoConfig);
//        return "获取成功";
//    }


    /**
     * @desc 查询配置项
     * @param value 结构    配置项目名称 ： 配置项内容
     *                      配置项来源  ： 值
     *
     *           配置优先级   数据库    >   jvm启动参数    >   配置文件
     * @return
     */
    public Map<String,Object> getConfig(String value){
        //db查询
        KmConfig one = kmConfig.findOne(value);
        if(one == null){
            //jvm启动参数
            String property = System.getProperty(value);
            if (property ==null){
                // 配置文件
                String contextPropertie = (String) ConfigFormPropertiesUtil.getContextPropertie(value);
                if (contextPropertie ==null){
                    return Putdata.printf(value,null,null);
                }else {
                    return Putdata.printf(value,contextPropertie,"properties");
                }
            }else{
                return Putdata.printf(value,property,"jvm");
            }
        }else {
            String o = one.getVlaue();
            return Putdata.printf(value,o,"mysql");
        }
    }

}
