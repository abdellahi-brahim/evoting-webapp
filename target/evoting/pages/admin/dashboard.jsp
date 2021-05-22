<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>eVoting | Dashboard</title>
  </head>
  <body>
    <h2>Admin Console<s:property value="%{#session['Profile'].name}"/>!</h2>
    
    <s:form action="createUser">
      <s:submit value="Register Person"/>
    </s:form>

    <s:form action="createElection">
      <s:submit value="Create Election"/>
    </s:form>

    <s:form action="createList">
      <s:submit value="Create List"/>
    </s:form>

    <s:form action="onGoingElections">
      <s:submit value="Query On Going Elections"/>
    </s:form>

    <s:form action="archivedElections">
      <s:submit value="Query Finished Elections"/>
    </s:form>

    <s:form action="manageElection">
      <s:submit value="Edit Election Proprieties"/>
    </s:form>

    <p><a href="<s:url action='logout'/>">Logout</a></p>
  </body>
</html>