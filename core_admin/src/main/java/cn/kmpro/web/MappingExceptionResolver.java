/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.kmpro.web;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一的异常处理器.
 * 普通请求重定向到500页面, ajax请求返回json对象.
 * <p/>
 * {@link HandlerExceptionResolver} implementation
 */
public class MappingExceptionResolver implements HandlerExceptionResolver {
    private Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        logger.error("====系统框架抛出异常:" + ex,ex);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("ex", ex);
        return new ModelAndView("error/500", model);
    }
}
