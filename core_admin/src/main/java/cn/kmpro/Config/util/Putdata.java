/**
 * 
 */
package cn.kmpro.Config.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @desc 输出工具类
 * @version 1.0
 */
public class Putdata {

	/**
	 * @desc  格式化输出  Map格式的数据
	 * @return
	 */
	public static Map<String, Object> printf(String pname, String pdata, String source) {
		Map<String, Object> ret = new HashMap<>();
		ret.put(pname, pdata);
		ret.put("source", source);
		return ret;
	}

}
