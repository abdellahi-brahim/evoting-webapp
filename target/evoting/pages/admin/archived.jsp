<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>eVoting | Archived Elections</title>
  </head>
  <body>
    <h2>Welcome back <s:property value="%{#session['Profile'].name}"/></h2>
    
    <p><a href="<s:url action='dashboard'/>">Return to main menu</main></a></p>
  </body>
</html>