<%-- 
    Document   : insert_userinfo
    Created on : 2024. 5. 3., 오전 10:33:54
    Author     : user
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>회원가입 폼</title>
         <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main_style.css">
    </head>
    <body>
        <h1>회원가입</h1>
        <hr />
        
        <form action="${pageContext.request.contextPath}/insert" method="POST">
            <table border="0">
                <tbody>
                    <tr>
                        <td>ID</td>
                        <td><input type="text" name="rid" size="20"/></td>
                    </tr>
                    <tr>
                        <td>PASSWORD</td>
                        <td><input type="text" name="rpw" size="20"/></td>
                    </tr>
                    <tr>
                        <td>NAME</td>
                        <td><input type="text" name="name" size="20"/></td>
                    </tr>
                    <tr>
                        <td>PHONE</td>
                        <td><input type="text" name="phone" size="20"/></td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <center>
                                <input type="submit" value="가입" /> <input type="reset"
                                                                          value="초기화"/>
                            </center>                           
                        </td>
                    </tr>
                </tbody>
            </table>
        </form>
    </body>
</html>