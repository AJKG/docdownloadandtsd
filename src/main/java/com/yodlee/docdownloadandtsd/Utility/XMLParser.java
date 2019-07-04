package com.yodlee.docdownloadandtsd.Utility;

/**

 * Copyright (c) 2019 Yodlee Inc. All Rights Reserved.

 *

 * This software is the confidential and proprietary information of Yodlee, Inc.

 * Use is subject to license terms.

 *

 */

import java.io.File;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;


public class XMLParser {
	
	private String xmlString = "";
	private XMLTree xmlTree = null;
	private Stack<String> stack = new Stack<String>();

	public XMLParser(){
		
	}
	
	public XMLParser(String XML){
		this.xmlString = XML;
	}
	
	XMLParser(File file){
		//convert to String and call parser method.
	}
	
	public XMLTree parseXML(String xml){
		XMLNode presentNode = null;
		if(xml==null || xml.equals("") || !xml.startsWith("<")){
			System.out.println("Invalid XML passed!");
			return null;
		}
		
		if(xml.contains("<?xml version=")){
			xml = ((xml.indexOf(">")+1)>=xml.length())?"":xml.substring(xml.indexOf(">")+1);
		}
		
		StringTokenizer strTokenizer = new StringTokenizer(xml, "<");
		
		while(strTokenizer.hasMoreTokens()){
			
			String token = "<"+strTokenizer.nextToken().trim();
			////System.out.println("Tag : "+token);
			if(token.trim().startsWith("</") && token.endsWith(">")){ 
				
				/*This is a closing tag. Ex : </site>*/
				
				token = token.substring(token.indexOf("/")+1,token.indexOf(">")).trim();
				String top = stack.peek();
				if(top.equals(token)){
					stack.pop();
					presentNode = presentNode.getParent();
				}
			}else if(token.trim().startsWith("<") && token.endsWith("/>")){ 
				
				/*This is an empty tag. Ex : <transactionList/> 
				 * Need not add these nodes to the XMLTree*/
				continue;
				
			}else if(token.endsWith(">")){ 
				
				/*This tag has adjacent tag to it. 
				 * Ex: <site><bankAccount> 
				 * Ex: <site name="XYZ"><bankAccount>
				 * */
				
				String tagName = this.fetchTagName(token);
				HashMap<String, String> attributes = this.fetchAttribute("<"+token, tagName);
				XMLNode node = new XMLNode(tagName,null,attributes);
				
				if(xmlTree==null){
					xmlTree = new XMLTree(node);
					presentNode = node;
				}else if(presentNode.addChild(node)){
					presentNode = node;
				}
				
				stack.push(tagName);
			}else if(token.contains(">")){
				String elementNodeString = token.substring(0, token.indexOf(">")+1);
				String value = token.substring(token.indexOf(">")+1);
				
				String tagName = this.fetchTagName(elementNodeString);
				HashMap<String, String> attributes = this.fetchAttribute(elementNodeString, tagName);
				XMLNode node = new XMLNode(tagName,value,attributes);

				if(xmlTree==null){
					xmlTree = new XMLTree(node);
					presentNode = node;
				}else if(presentNode.addChild(node)){
					presentNode = node;
				}
				
				stack.push(tagName);
			}else{
				//System.out.println("New usecase found for parsing tagName!!");
				return null;
			}
			
			if(xmlTree!=null && xmlTree.getContainer().equals("")
					&& NodeUtil.tagContainerMap.get(stack.peek())!=null){
				xmlTree.setContainer(NodeUtil.tagContainerMap.get(stack.peek()));
				//System.out.println("Setting container :"+xmlTree.getContainer());
			}
		}
		
		
		return this.xmlTree;
	}
	
	public XMLTree parseXML(){
		return parseXML(this.xmlString);
	}
	
	private HashMap<String, String> fetchAttribute(String xml, String tagName){
		HashMap<String, String> attributes = new HashMap<String, String>();
		String xmlToBeParsed = xml;
	
		if(xml==null || tagName == null || xml.trim().length()==0 || tagName.trim().length() == 0){
			//System.out.println("Empty entery passed!");
			return attributes;
		}else if(!xml.contains(tagName) || !xml.trim().startsWith("<") || !xml.trim().endsWith(">")){
			//System.out.println("tagName does not match with the node passed!! or illegal format passed");
			return attributes;
		}else{
			xmlToBeParsed = xml.substring(xml.indexOf(tagName)+tagName.length(),xml.lastIndexOf(">"));
		}
		
		String token = "";
		String attributeName = "";
		String attributeValue = "";
		String xmlAlreadyParsed = xml;
		
		while(!xmlToBeParsed.equals("")){
			////System.out.println("xmlToBeParsed::"+xmlToBeParsed);
			int startIndex = 0;
			int parseTillIndex = xmlToBeParsed.indexOf("=");
			
			attributeName = xmlToBeParsed.substring(0,parseTillIndex).trim();
			xmlToBeParsed = xmlToBeParsed.substring(parseTillIndex+1).trim();
			
			//Some xmls have the character(') instead of character(").
			if(xmlToBeParsed.indexOf("\"")==-1){
				startIndex = xmlToBeParsed.indexOf("\'")+1;
				parseTillIndex = xmlToBeParsed.indexOf("\'",startIndex);
			}else{
				startIndex = xmlToBeParsed.indexOf("\"")+1;
				parseTillIndex = xmlToBeParsed.indexOf("\"",startIndex);
			}
			
			attributeValue = xmlToBeParsed.substring(startIndex,parseTillIndex).trim();
			
			
			if(!attributeName.equals("")){
				attributes.put(attributeName, attributeValue);
			}
			
			if((parseTillIndex+1)>=xmlToBeParsed.length()){
				xmlToBeParsed = "";
			}else{
				xmlToBeParsed = xmlToBeParsed.substring(parseTillIndex+1).trim();
			}
		}
		
		return attributes;
	}
	
	private String fetchTagName(String xml){
		String tagName = "";
		
		if(xml==null||xml.trim().length()==0){
			return "";
		}else if(!xml.trim().startsWith("<") || !xml.trim().endsWith(">")){
			//System.out.println("illegal xml format passed");
			return "";
		}
		
		xml = xml.substring(xml.indexOf("<")+1, xml.indexOf(">")).trim();
		
		return xml.contains(" ")?xml.substring(0,xml.indexOf(" ")).trim() : xml.trim();
	}
}
