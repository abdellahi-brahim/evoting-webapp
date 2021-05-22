<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>eVoting | Dashboard</title>
  </head>
  <body>
    <h2>Welcome <s:property value="%{#session['Profile'].name}"/>!</h2>

    <s:if test="elections.size>0">
      <s:form action="electionValidation">
        <s:select value="On Going Elections"
        name = "selectedElection"
        headerKey="-1" headerValue="Select one election"
        list="elections" />
        
        <s:submit value = "View Lists"/>
    </s:form>	
  </s:if>
  <s:else>
      There are no on going elections.
  </s:else>
  </body>
</html>