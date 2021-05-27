<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>eVoting | Create Election</title>
  </head>
  <body>
    <h2>Create Election</h2>

    <s:form action = "registerElection">
        <s:textfield name="election.title" label="Title"/>
        <s:textarea name="election.description" label="Description"/>
        <s:textfield name="election.startDate" label="Start Date" type="datetime-local"/>
        <s:textfield name="election.endDate" label="End Date" type="datetime-local"/>

        <s:select name="election.faculty" label="Faculty"
        headerKey="-1" headerValue="Select your faculty"
        list="faculties"/>
        
        <s:select name="election.department" label="Department"
        headerKey="-1" headerValue="Select your department"
        list="departments"/>
        
        <s:radio name="election.type" label="Type"
        headerKey="-1" headerValue="Select your type"
        list="userTypes"/>
        
        <s:submit value = "Create Election"/>
    </s:form>	
    <p><a href="<s:url action='dashboard'/>">Return to main menu</main></a></p>
  </body>
</html>