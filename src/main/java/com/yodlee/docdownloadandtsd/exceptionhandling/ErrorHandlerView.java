package com.yodlee.docdownloadandtsd.exceptionhandling;/** * Copyright (c) 2019 Yodlee Inc. All Rights Reserved. * * This software is the confidential and proprietary information of Yodlee, Inc. * Use is subject to license terms. * */public class ErrorHandlerView{	private String path;public String getPath() {	return path;}public void setPath(String path) {	this.path = path;}private String message ;public String getMessage() {	return message;}public void setMessage(String message) {	this.message = message;}public Integer getStatus() {	return status;}public void setStatus(Integer status) {	this.status = status;}private Integer status;public ErrorHandlerView(String message,Integer status,String path){	this.message=message;	this.status=status;	this.path=path;}	}