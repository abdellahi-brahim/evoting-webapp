<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Register User</title>
  </head>
  <body>
    <h3>Register New User</h3>

    <s:form action = "registerUser">
        <s:textfield name="person.firstName" label="First Name"/>
        <s:textfield name="person.lastName" label="Last Name"/>
        <s:textfield name="person.phoneNumber" label="Phone Number"/>  
        <s:textfield name="person.address" label="Address"/>
        <s:textfield name="person.idNumber" label="ID Number" type="Number"/>
        <s:textfield name="person.idValidity" label="ID Validity" type="Date"/>
        <s:textfield name="person.userName" label="Username"/>
        <s:textfield name="person.password" label="Password"/>

        <s:select name="person.faculty" label="Faculty"
        headerKey="-1" headerValue="Select your faculty"
        list="faculties"/>
        
        <s:select name="person.department" label="Department"
        headerKey="-1" headerValue="Select your department"
        list="departments"/>
        
        <s:radio name="person.type" label="Type"
        headerKey="-1" headerValue="Select your type"
        list="userTypes"/>
        
        <s:submit value = "Register User"/>
    </s:form>	
    <p><a href="<s:url action='dashboard'/>">Return to main menu</main></a></p>
  </body>
</html>