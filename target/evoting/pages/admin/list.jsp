<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>eVoting | Create List</title>
  </head>
  <body>
    <h2>Create List</h2>
    <s:form action = "createList">
        <s:textfield name="list.name" label="List name" required="true"/>

        <s:select name="list.members" label="Members"
        headerKey="-1" headerValue="Select some users to add to list"
        list="users" multiple="true"  required="true"/>

        <s:submit value = "Create List"/>
    </s:form>	
    <p><a href="<s:url action='dashboard'/>">Return to main menu</main></a></p>
  </body>
</html>