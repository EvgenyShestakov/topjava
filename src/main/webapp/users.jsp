<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="ru">
<head>
    <title>Users</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Users</h2>
<ul>
    <li><a href="users">User List</a></li>
    <form method="post" action="users">
        <label>
            <select name="userId">
                <option value="1">Admin</option>
                <option value="2">User</option>
            </select>
        </label>
        <p><input type="submit" value="Select User"></p>
    </form>
</ul>
</body>
</html>