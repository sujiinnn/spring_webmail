<%-- 
    Document   : addrbook
    Created on : 2024. 5. 12., 오전 4:55:29
    Author     : guym3
--%>

<%@tag description="put the tag description here" pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>     

<%-- The list of normal or fragment attributes can be specified here: --%>
<%@attribute name="user" required="true"%>
<%@attribute name="password" required="true"%>
<%@attribute name="schema" required="true"%>
<%@attribute name="table" required="true"%>
<%@attribute name="username" required="true"%>


<sql:setDataSource var="dataSrc"
                   url="jdbc:mysql://${mysql_server_ip}:${mysql_server_port}/${schema}?serverTimezone=Asia/Seoul"
                   driver="com.mysql.cj.jdbc.Driver"
                   user="${user}" password="${password}" />

<sql:query var="rs" dataSource="${dataSrc}">
    SELECT addrname, name, concat(substr(phone, 1, 3), '-', substr(phone, 4, 4), '-', substr(phone, 8, 4)) "phone" FROM users u JOIN addrbook a ON a.addrname=u.username WHERE a.username='${username}'
</sql:query>
<table id="addrbook" border="1">
    <thead>
        <tr>
            <th>사용자 ID</th>
            <th>이름</th>
            <th>전화번호</th>
        </tr>
    </thead>
    <tbody>
        <c:choose>
            <c:when test="${pageContext.request.requestURI == '/webmail/WEB-INF/views/addrbook/addrbook_menu.jsp'}">
                <c:forEach var="row" items="${rs.rows}">
                    <tr style="background-color: rgb(255, 255, 255);">
                        <td>
                            <form action="${pageContext.request.contextPath}/write_mail" method="GET">
                                <input type="hidden" name="sender" value="${row.addrname}">
                                <input type="hidden" name="addr" value="${row.addrname}">
                                <input type="submit" value="${row.addrname}" style="border: 0; cursor: pointer; background-color: transparent; font-size: 16px;
                                           color: blue; text-decoration: underline;">
                            </form>
                        </td>
                        <td>${row.name}</td>
                        <td>${row.phone}</td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:when test="${pageContext.request.requestURI == '/webmail/WEB-INF/views/addrbook/add_addr.jsp'}">
                <c:forEach var="row" items="${rs.rows}">
                    <tr style="background-color: rgb(255, 255, 255);">
                        <td>${row.addrname}</td>
                        <td>${row.name}</td>
                        <td>${row.phone}</td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <p>이것은 다른 페이지입니다.</p>
            </c:otherwise>
        </c:choose>
    </tbody>
</table>