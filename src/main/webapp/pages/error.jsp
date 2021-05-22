<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
    <head>
        <title>eVoting</title>
    </head>
    <body>
        <div>
            <h1>Error</h1>
            <p><a href="<s:url action='index'/>">Return to main menu</main></a></p>
        </p>
        </div>
        <div>
            <p><s:property value="exceptionStack"/></p>
        </div>
    </body>
</html>