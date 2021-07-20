<%--
  Created by IntelliJ IDEA.
  User: 马兴佳
  Date: 2021/7/20
  Time: 12:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="cn.kmpro.demo.admin.model.AdminUser"  isELIgnored="false"%>
<%@page import="java.util.Map"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
    <title>修改管理员</title>
    <form action="<%=path %>/admin/edit" method="post">
        <input type="hidden" value="${adminUser.id }" name="id"><br>
        用户名:<input type="text" value="${adminUser.username }" name="username"><br>
        密码:<input type="text" value="${adminUser.password }" name="password"><br>
        姓名:<input type="text" value="${adminUser.name }" name="name"><br>
        性别:<input type="text" value="${adminUser.sex }" name="sex"><br>
        生日：<input type="text" value="${adminUser.birthday }" name="sex"><br>
        身份证号:<input type="text" value="${adminUser.inumber }" name="inumber"><br>
        <input type="submit" value="提交">
    </form>


<%--    <form action="<%=request.getContextPath()%>/admin/edit.action">--%>

<%--        <table>--%>

<%--            <tr>--%>

<%--                <td>桌子名称：</td>--%>
<%--                <td><input  type="hidden"  name="id"  value="${adminUser.id}"/></td>--%>
<%--                <td><input  type="text"  name="name"  value="${adminUser.name}"/></td>--%>
<%--            </tr>--%>


<%--            <tr>--%>
<%--                <td colspan="2">--%>
<%--                    <input  type="submit"  value="保存"  />--%>
<%--                </td>--%>

<%--            </tr>--%>

<%--        </table>--%>


<%--    </form>--%>
</head>
<body>

</body>
</html>
