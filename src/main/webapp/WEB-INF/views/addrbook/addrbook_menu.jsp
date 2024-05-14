<%--
    Document   : addrbook
    Created on : 2024. 5. 10., 오전 10:22:33
    Author     : guym3
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="deu.cse.spring_webmail.control.CommandType"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib tagdir="/WEB-INF/tags/addrbook" prefix="mytags"%>

<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@environment.getProperty('spring.datasource.username')" var="db_username" />
<spring:eval expression="@environment.getProperty('spring.datasource.password')" var="db_password" />
<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><%= session.getAttribute("userid") %>님의 주소록</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
        <script>
            <c:if test="${!empty msg}">
            alert("${msg}");
            </c:if>
        </script>
    </head>
    <body>
        <%@include file="/WEB-INF/views/header.jspf"%>
        <div id="sidebar">
            <jsp:include page="addrbar_menu.jsp" />
        </div>
        <div id='main'>
            <h2 style="color: indigo"><%= session.getAttribute("userid") %>님의 주소록</h2>
            <c:catch var="errorReason">
                <mytags:addrbook user="${db_username}" password="${db_password}" schema="mail" table="addrbook" username='<%= (String)session.getAttribute("userid")%>'/>
            </c:catch>

            ${empty errorReason ? "<noerror/>" : errorReason}
        </div>
        <%@include file="/WEB-INF/views/footer.jspf"%>
    </body>
</html>
