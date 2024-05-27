<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>회원 수정 폼</title>
         <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main_style.css">
    </head>
    <body>
        <h1>회원 수정</h1>
        <hr />
        
        <form action="${pageContext.request.contextPath}/insert" method="POST">
            <table border="0">
                <tbody>
                    <tr>
                        <td>ID</td>
                        <td><input type="text" name="id" value="<%= rs.getString("username") %>" readonly/></td>
                    </tr>
                    <tr>
                        <td>NAME</td>
                        <td><input type="text" name="name" value="<%= rs.getString("name") %>" readonly/></td>
                    </tr>
                    <tr>
                        <td>PASSWORD</td>
                        <td><input type="password" name="password" value="<%= rs.getString("password") %>"></td>
                    </tr>
                    <tr>
                        <td>PHONE</td>
                        <td><input type="text" name="phone" value="<%= rs.getString("phone") %>"></td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <center>
                                <input type="submit" value="수정" />
                            </center>                           
                        </td>
                    </tr>
                </tbody>
            </table>
        </form>
    </body>
</html>