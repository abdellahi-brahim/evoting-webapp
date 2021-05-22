<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>eVoting</title>
    </head>
    <body>
        <h1>Welcome To Evoting!</h1>

        <s:form action="user">
            <s:submit value="User Login"/>
        </s:form>

        <s:form action="admin">
            <s:submit value="Admin Panel"/>
        </s:form>
    </body>
</html> 