<%--
  Created by IntelliJ IDEA.
  User: 马兴佳
  Date: 2021/7/20
  Time: 9:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
    <title>管理员首页</title>
    <script type="text/javascript">
        function del(id){
            var v = window.confirm('你确定要删除当前记录吗？');
            if(v){
                window.location.href = '<%=path%>/admin/delete?id='+id;
            }

        }

        function deleteAll(){
            var ids = document.getElementsByName('id');
            var idValues = '';
            for(var i=0;i<ids.length;i++){
                if(ids[i].checked == true){
                    idValues += ids[i].value + ",";
                }
            }
            var v = window.confirm('你确定要删除当前记录吗？');
            if(v){
                window.location.href = '<%=path%>/admin/delete?id=' + idValues;
            }
        }

        function saveAdmin(){
            window.location.href = '<%=path%>/admin/addPage';
        }
    </script>
</head>

<body bgcolor="#20b2aa">
<div style="width:100%;height:3%;text-align: center;vertical-align: middle">管理员信息<br/>
<input type="text" name="rolename" class="abc input-default" placeholder="" value="">&nbsp;&nbsp;
<button type="button">查询</button>
<input type="button" value="添加管理员" onclick="saveAdmin();">
<input type="button" value="批量删除" onclick="deleteAll();">

<table border="1" width="70%" align="center">
    <tr>
        <td>
            <input type="checkbox" name="checkAll">
        </td>
        <td>id</td>
        <td>账号</td>
        <td>密码</td>
        <td>姓名</td>
        <td>性别</td>
        <td>生日</td>
        <td>身份证号</td>
        <td>删除</td>
        <td>修改</td>
    </tr>
    <c:forEach items="${adminUsers}" var="a">
        <tr>
            <td><input type="checkbox" name="ids" value="${a.id }"></td>
            <td>${a.id }</td>
            <td>${a.username }</td>
            <td>${a.password }</td>
            <td>${a.name }</td>
            <td>${a.sex }</td>
            <td>${a.birthday}</td>
            <td>${a.inumber }</td>
            <td><input type="button" value="删除" onclick="del('${a.id }')"></td>
            <td><a href="<%=path %>/admin/editPage?id=${a.id }">修改</a></td>
        </tr>
    </c:forEach>
</table>
</div>
</body>
</html>

