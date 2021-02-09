<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<html lang="ru">
<head>
    <title>Meals</title>
    <style>
        table {
            border-collapse: collapse;
            width: 500px;
        }

        th, td {
            border: 1px solid cadetblue;
            text-align: center;
            padding: 8px;
        }

        tr:nth-child(even) {
            background-color: #c8f2f7;
        }
    </style>
</head>

<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<table>
    <tr style="background-color: cadetblue">
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
    </tr>
    <jsp:useBean id="list" scope="request" type="java.util.List"/>
    <c:forEach var="objMealTo" items="${list}">
        <tr style="color: ${objMealTo.excess ? "red" : "green"}">
            <td><javatime:format value="${objMealTo.dateTime}" pattern="yyyy-MM-dd HH:mm"/></td>
            <td>${objMealTo.description}</td>
            <td>${objMealTo.calories}</td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
