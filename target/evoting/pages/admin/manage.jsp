<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>eVoting | On Going Elections</title>
  </head>
  <body>
    <h2>On Going Elections</h2>

    <s:if test="onGoing.size > 0">
        <s:form action = "dashboard">
            <s:select name="election.title" label="On Going Elections"
            headerKey="-1" headerValue="Select one election"
            list="onGoing"/>
    
            <s:submit value = "View Details"/>
        </s:form>	
    </s:if>
    <s:else>
        There are no elections to edit.
    </s:else>
    
    <p><a href="<s:url action='dashboard'/>">Return to main menu</main></a></p>
  </body>
</html>