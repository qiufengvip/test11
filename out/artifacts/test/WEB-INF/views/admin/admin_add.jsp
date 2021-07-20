<%--
  Created by IntelliJ IDEA.
  User: 马兴佳
  Date: 2021/7/20
  Time: 12:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="cn.kmpro.demo.admin.model.AdminUser"  isELIgnored="false"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!--图标样式-->
<link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.bootcss.com/bootstrap-datetimepicker/4.17.47/css/bootstrap-datetimepicker.min.css" rel="stylesheet">
<script src="https://cdn.bootcss.com/jquery/3.3.1/jquery.js"></script>
<script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script src="https://cdn.bootcss.com/moment.js/2.24.0/moment-with-locales.js"></script>
<script src="https://cdn.bootcss.com/bootstrap-datetimepicker/4.17.47/js/bootstrap-datetimepicker.min.js"></script>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
    <title>添加管理员</title>

</head>
<body bgcolor="#e9967a">
<form action="<%=path %>/admin/add/" method="post">
    账号:<input type="text" οnkeyup="this.value=this.value.replace(/[^\w_]/ g,'');" name="username" ><br>
    密码:<input type="text"  name="password"><br>
    姓名:<input type="text"  name="name"><br>
    性别:
    <select id="sex" name="sex">
        <option value="1">男</option>
        <option value="0">女</option>
    </select>
    <br>
    生日：<input type="date"  name="birthday"><br>
    身份证号:<input type="text"  name="inumber"><br>
    <br/>
    <input type="submit" value="提交">
</form>


</body>
</html>
