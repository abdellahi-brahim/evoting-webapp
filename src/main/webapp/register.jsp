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

    <s:form action="register">
        <s:token/>
        <s:textfield key="personBean.firstName"/>
        <s:textfield key="personBean.lastName"/>
        <s:textfield key="personBean.phoneNumber" type="Number"/>  
        <s:textfield key="personBean.address"/>
        <s:textfield key="personBean.idNumber" type="Number"/>
        <s:textfield key="personBean.idValidity" type="Date"/>
        <s:textfield key="personBean.userName"/>
        <s:textfield key="personBean.password"/>

        <s:select key="personBean.faculty"
        headerKey="-1" headerValue="Select your faculty"
        list="faculties"/>
        
        <s:select key="personBean.department"
        headerKey="-1" headerValue="Select your department"
        list="departments" />
        
        <s:radio key="personBean.type"
        headerKey="-1" headerValue="Select your type"
        list="userTypes" />
        
        <s:submit value = "Register"/>
    </s:form>	
  </body>
</html>