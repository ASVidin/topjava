<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>EditMeal</title>
    <style>
        td {
            width: 150px;
            text-align: left;
            padding: 8px;
        }
    </style>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Edit meal</h2>
<form name="frmAddMeal" method="POST">
    <input type="hidden" id="id" name="id" value="${meal.id}">
    <table>
        <tr>
            <td><label for="datetime">Date time:</label></td>
            <td><input type="datetime-local" id="datetime" name="datetime" value="${meal.dateTime}" required/><br/></td>
        </tr>
        <tr>
            <td><label for="description">Description:</label></td>
            <td><input list="mealsType" id="description"
                       name="description"
                       value="${meal.description}"
                       placeholder="Выберите из списка"
                       pattern="[А-Яа-я]+"
                       size="23"
                       required/>

                <datalist id="mealsType">
                    <option value="Завтрак">
                    <option value="Обед">
                    <option value="Ужин">
                    <option value="Другое">
                </datalist>
                <br/>
            </td>
        </tr>
        <tr>
            <td><label for="calories">Calories:</label></td>
            <td><input type="text"
                       id="calories"
                       name="calories"
                       value="${meal.calories}"
                       placeholder="от 1 до 9999"
                       pattern="[0-9]+"
                       maxlength="4"
                       size="23"
                       required/><br/></td>
        </tr>
    </table>
    <input type="submit" value="Save">
    <button type="button" onclick="window.history.back()">Cancel</button>
</form>
</body>
</html>