<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>회원가입</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main_style.css">
        <script>
            <c:if test="${!empty msg}}">
                alert("${msg}");
            </c:if>
        </script>
    </head>
    <body>
        <h1>회원관리</h1>
        <hr/>
        
        <table border="1">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>PASSWORD</th>
                    <th>NAME</th>
                    <th>PHONE</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="row" items="${dataRows}">
                    <tr>
                        <td>${row.rid}</td>
                        <td>${row.rpw}</td>
                        <td>${row.name}</td>
                        <td>${row.phone}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <br>
        <button onclick="location.href='${pageContext.request.contextPath}/update_userinfo'">수정</button>
    </body>
</html>
