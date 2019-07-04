package com.yodlee.docdownloadandtsd.Utility;

/**

 * Copyright (c) 2019 Yodlee Inc. All Rights Reserved.

 *

 * This software is the confidential and proprietary information of Yodlee, Inc.

 * Use is subject to license terms.

 *

 */

import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

public class XMLTree {
	
	private XMLNode root = null;
	private int height = 0;
	private int numberOfLeafNodes = 0;
	private int numberOfNodes = 0;
	private HashMap<String, XMLNode> pathObjectMap = new HashMap<String,XMLNode>();
	private Stack<String> stack = new Stack<String>();
	private HashMap<String,Set<String>> nodeStateDiffMap = new HashMap<String,Set<String>>();
	private String container = "";
	
	//A tree MUST have a root node.
	public XMLTree(XMLNode root){
		this.root = root;
	}

	/**
	 * @return the root
	 */
	public XMLNode getRoot() {
		return root;
	}

	/**
	 * @return the height of the tree
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the numberOfLeafNodes
	 */
	public int getNumberOfLeafNodes() {
		return numberOfLeafNodes;
	}

	/**
	 * @return the numberOfNodes
	 */
	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	public boolean addXMLNode(XMLNode parentNode, XMLNode nodeToBeAdded){
		XMLNode node = searchNode(parentNode);
		
		if(node == null){
			return false;
		}
		
		boolean isAdded = node.addChild(nodeToBeAdded);
		
		if(this.height<(nodeToBeAdded.getLevel()+1)){
			this.height = nodeToBeAdded.getLevel()+1;
		}
		
		return isAdded;
	}
	
	public XMLNode searchNode(XMLNode node){
		return searchTopToBottom(this.root, node);
	}
	
	
	public XMLNode searchBottomUp(XMLNode startNode, XMLNode node){

		if(startNode==null || node==null){
			return null;
		}else if(startNode.equals(node)){
			return startNode;
		}else if(startNode.getParent()==null){
			return null;
		}
			
		return searchBottomUp(startNode.getParent(), node);
	}
	
	public XMLNode searchTopToBottom(XMLNode startNode, XMLNode node){
		if(startNode==null || node==null){
			return null;
		}else if(startNode.equals(node)){
			return startNode;
		}else if(startNode.getChilds()==null){
			return null;
		}
				
		XMLNode foundNode = null;
		
		for(XMLNode subtreeRootNode : startNode.getChilds()){
			foundNode = searchTopToBottom(subtreeRootNode, node);
			if(foundNode!=null){
				break;
			}
		}
		
		return foundNode;
	}
	
	public int traverse(){
		this.height = this.root.traverse();
		pathObjectMap = this.root.traverseAndPopulate(this.nodeStateDiffMap);
		return height;
	}
	
	public static XMLTree parseXMLToTree(String XML){
		if(XML==null || XML.equals("")){
			return null;
		}
		
		return null;
	}
	
	public String getXML(){
		return this.getXML(this.root, false);
	}
	
	public String getXML(boolean showDifferenceColor){
		return getXML(this.root,showDifferenceColor);
	}
	
	public String getXML(XMLNode node, boolean showDifferenceColor){
		String xml = "";
		
		if(node==null){
			return "";
		}

		xml+=node.asXML(showDifferenceColor);
		
		if(node.getChilds()==null || node.getChilds().size()==0){
			return xml;
		}
		
		
		for(XMLNode childNode : node.getChilds()){
			xml+=getXML(childNode,showDifferenceColor);
		}
		
		if(showDifferenceColor){
			xml+="&lt;/"+node.getName()+"&gt;";
		}else{
			xml+="</"+node.getName()+">";
		}
		
		return xml+"<br>";
	}

	/**
	 * @return the pathObjectMap
	 */
	public HashMap<String, XMLNode> getPathObjectMap() {
		return pathObjectMap;
	}

	/**
	 * @return the nodeStateDiffMap
	 */
	public HashMap<String, Set<String>> getNodeStateDiffMap() {
		return nodeStateDiffMap;
	}

	/**
	 * @return the container
	 */
	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}
}
