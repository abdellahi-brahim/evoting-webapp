<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>eVoting | Dashboard</title>
  </head>
  <body>
    <h2>Welcome back <s:property value="%{#session['Profile'].name}"/>!</h2>
    <h3>Welcome back <s:property value="%{selectedElection}"/></h3>
    <s:if test="lists.size>0">
      <s:form action="listValidation">
        <s:select value="Election Lists"
        name = "selectedElection"
        headerKey="-1" headerValue="Blank Vote"
        list="lists"/>
        
        <s:submit value = "Vote"/>
    </s:form>	
  </s:if>
  <s:else>
      This election has no lists
  </s:else>
  </body>
</html>