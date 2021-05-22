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
        <body>
            <h2>Login</h2>
            <s:actionerror/>
            <s:actionmessage/>
    
            <s:form action="submit">
                <s:textfield name="id" label="ID Number"/>
                <s:textfield name="username" label="Username"/>
                <s:password name="password" label="Password"/>
                <s:submit value = "Login"/>
            </s:form>
        </body> 
    </body>
</html>