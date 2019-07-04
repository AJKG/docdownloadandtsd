package com.yodlee.docdownloadandtsd.Utility;

/**

 * Copyright (c) 2019 Yodlee Inc. All Rights Reserved.

 *

 * This software is the confidential and proprietary information of Yodlee, Inc.

 * Use is subject to license terms.

 *

 */

import com.yodlee.docdownloadandtsd.Utility.NodeUtil.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class XMLNode {
	
	private String name = "";
	private String value = "";
	private HashMap<String,String> attributes = null;
	private XMLNode parent = null;
	private boolean isLeaf = true; //by default every new node is a leaf node.									
	private ArrayList<XMLNode> childs = null;
	private int level = 0; 
	private String path = "";
	private String state = State.OTHER;
	
	/*
	 * The path may have 3 enntities:
	 * 		1.TagName
	 * 		2.Attribute of the tag (uniqueId)
	 * 		3.Value
	 * Thus, these final variables represents them accordingly.
		
		DELIMINATOR 			: To separate the nodes in the path.
		TAG_IDENTIFIER 			: to represent the Tag in the path of the node
		ATTRIBUTE_TAG_IDENTIFIER: To represent the attribute in the path of the node.
		VALUE_TAG_IDENTIFIER 	: To represent the value of the node.
	*/
	
	private final String DELIMINATOR = "#";	
	private final String TAG_IDENTIFIER = "T:"; 
	private final String ATTRIBUTE_TAG_IDENTIFIER = "A:"; 
	private final String VALUE_TAG_IDENTIFIER = "V:"; 
	
	/*
	 * 'rowdelkeys' here represent the prominent nodes and there corresponding value to be used for the rowdel comparison.
	 * Ex : the tag <bankAccount> will have the child tag <accountNumber> that shall be used for comparing the nodes of bankAccount
	 * Hence, the value of this child tag shall be stored in the parent tag 'bankAccount' so that we need not traverse further to validate rowdel.
	*/
	private HashMap<String,String> rowdelKeys = null; 
	private boolean hasRowdelKeys = false;
	
	//These variables are used while matching two nodes
	private boolean isMapped = false;
	private XMLNode mappedTo = null;
	
	/*
	 * By default we are considering all the nodes do not match and hence are missing.
	 * This node shall be updated during the comparison if the match or discrepancy is found */
	private String differenceType = NodeUtil.DIFFERENCE_TYPE_MISSING_NODE; 
	
	//Every node MUST have a 'name' hence, no default conststructor provided.
	public XMLNode(String name){
		this(name,null,null);
	}

	public XMLNode(String name, String value) throws Exception{
		this(name,value,null);
	}
	
	public XMLNode(String name, String value, HashMap<String, String> attributes) throws NullPointerException{
		this.state = findState(this);
		
		if(name==null){
			throw new NullPointerException("Tag Name cannot be null!!");
		}else if(value!=null){
			//Since we are setting empty string for no value and not null
			this.value = value;
		}
		
		this.name = name;
		
		if(attributes!=null && attributes.size()>0){
			HashMap<String, String> copyAttributes = new HashMap<String, String>();
			copyAttributes.putAll(attributes);
			this.attributes = copyAttributes;
		}
		
		this.path = getUpdatedPath();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XMLNode other = (XMLNode) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
		this.path = getUpdatedPath();
	}
	
	public HashMap<String, String> getAttributes() {
		return this.attributes;
	}
	
	public void addAttribute(String key, String value){

		if(this.attributes==null){
			this.attributes = new HashMap<String,String>();
		}

		if(key!=null && !key.equals("")){
			this.attributes.put(key, value);
			this.path = getUpdatedPath(); 
			
			if(RowdelUtil.isFieldConsiderableAsRowDelField(key)){
				this.addRowdelKey(key, value);
			}
		}
	}

	public void addAttributes(HashMap<String, String> attributes){
		
		if(attributes==null){
			return;
		}
		
		if(this.attributes==null){
			this.attributes = new HashMap<String,String>();
		}
		
		for(String key : attributes.keySet()){
			addAttribute(key, attributes.get(key));
		}
		
		this.path = getUpdatedPath();
	}
	
	public void removeAttribute(String key){
		if(key!=null && this.attributes.get(key)!=null){
			this.attributes.remove(key);
			this.path = getUpdatedPath(); //In case the removed attribute was 'uniqueId'
		}
	}
	
	public void removeAllAttributes(){
		this.attributes = null;
	}
	
	public XMLNode getParent() {
		return parent;
	}
	
	public void setParent(XMLNode parent) {
		this.parent = parent;
	}
	public boolean isLeaf() {
		return isLeaf;
	}
	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	public ArrayList<XMLNode> getChilds() {
		return childs;
	}
	
	public void addChilds(ArrayList<XMLNode> childs) {
		if(childs==null){
			//System.out.println("Null agrmunet passed for childs!");
			return;
		}
		
		for(XMLNode child : childs){
			this.addChild(child);
		}
	}
	
	public boolean addChild(XMLNode childNode){
		
		if(childNode ==null){
			return false;
		}else if(this.childs == null){
			this.childs = new ArrayList<XMLNode>();
		}
		
		boolean added = false;
		added = this.childs.add(childNode);
		
		if(added){
			this.isLeaf = false;
			childNode.state = findState(childNode);
			this.updateChildNode(childNode);
			if(RowdelUtil.isFieldConsiderableAsRowDelField(childNode.getName())){
				addRowdelKey(childNode.getName(), childNode.getValue());
			}
		}
		
		return added; 
	}
	
	public int getLevel() {
		return level;
	}
	
	private void setLevel(int level) {
		this.level = level;
	}
	
	public int traverse(){
		return traverse(this);
	}
	
	public int traverse(XMLNode node){
		
		if(node==null){
			return -1;
		}
		
		int height = node.level;
		//System.out.println("Path : "+node.getPath()+"::"+node.mappedTo);
		//System.out.println(node.getName()+": Level : "+node.getLevel());
		//System.out.println("Name : "+node.getName());
		//System.out.println("Value : "+node.getValue());
		////System.out.println(node.asXML());
		
		if(node.getChilds()==null){
			return height;
		}
		
		
		for(XMLNode childNode : node.getChilds()){
			/*Uncomment for printing the attributes
			 * if(childNode.getAttributes()!=null){
				//System.out.println("Attributes :");
				
				for(String key : childNode.getAttributes().keySet()){
					//System.out.println(key +" : "+ childNode.getAttributes().get(key));
				}
			}*/
			
			int childTreeHeight = traverse(childNode);
			height = childTreeHeight>height?childTreeHeight:height;
		}
		
		return height;
	}
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
	
	/**
	 * @param path the path to set
	 */
	private void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * @return path of the parent node
	 */
	public String getParentPath(){
		return parent!=null?parent.getPath():"";
	}
	
	/**
	 * @return formatted path of node.
	 * 
	 * This method uses the attributes for the path generation.
	 * If 'uniqueId' is one of the attributes of a node, then it is considered for the unique path creation.
	 * Also, it updates the present path using the path of the parent(if any).
	 * 
	 */
	private String getUpdatedPath(){
		
		String uniqueID = "";
		if(attributes!=null && attributes.containsKey("uniqueId")){
			uniqueID = attributes.get("uniqueId");
		}
		
		return (getParentPath().equals("")?"":getParentPath()+DELIMINATOR)+(uniqueID.equals("")?"":ATTRIBUTE_TAG_IDENTIFIER+"UID"+uniqueID+DELIMINATOR)+TAG_IDENTIFIER+this.name+(value.equals("")?"":DELIMINATOR+VALUE_TAG_IDENTIFIER+value);
	}
	
	
	public int getParentLevel(){
		return parent!=null?parent.getLevel():-1;
	}
	
	private int getUpdatedLevel(){
		return (getParentLevel()==-1?0:getParentLevel()+1);
	}
	
	public HashMap<String,XMLNode> traverseAndPopulate(HashMap<String,Set<String>> nodeStateDiffMap){
		return traverseAndPopulate(this, nodeStateDiffMap);
	}
	
	private HashMap<String,XMLNode> traverseAndPopulate(XMLNode node, HashMap<String,Set<String>> nodeStateDiffMap){
		
		HashMap<String,XMLNode> pathObjectMap = new HashMap<String,XMLNode>();
		
		if(node==null){
			return pathObjectMap;
		}

		pathObjectMap.put(node.getPath(), node.getMappedTo());
		node.state = findState(node);
		
		//This method to fetch the accountNumber and the state for which we are getting the discrepancy
		//The field shall be used later to back track the ajax response for finding the missing data
		if(!node.isMapped() || !node.getDifferenceType().equals(NodeUtil.DIFFERENCE_TYPE_NONE)){
			String accountNumber = "";
			for(XMLNode testNode=node; testNode.getParent()!=null ; testNode=testNode.getParent()){
				if(testNode.getRowdelKeys()!=null && testNode.getRowdelKeys().containsKey(RowdelFields.ACCOUNT_NUMBER) ){
					accountNumber = testNode.getRowdelKeys().get(RowdelFields.ACCOUNT_NUMBER).trim();
					if(!accountNumber.equals("")){
						break;
					}
				}
			}
			
			if(nodeStateDiffMap == null){
				nodeStateDiffMap = new HashMap<String,Set<String>>();
			}
			
			if(!accountNumber.equals("") && !nodeStateDiffMap.containsKey(accountNumber)){
				HashSet<String> states = new HashSet<String>();
				//System.out.println("Node1 :"+node.getName());
				//System.out.println("State1 :"+node.getState());
				states.add(node.getState());
				nodeStateDiffMap.put(accountNumber, states);
			}else if(!accountNumber.equals("") && nodeStateDiffMap.containsKey(accountNumber)){
				Set<String> states = nodeStateDiffMap.get(accountNumber);
				states.add(node.getState());
				//System.out.println("Node2 :"+node.getName());
				//System.out.println("State2 :"+node.getState());
				nodeStateDiffMap.put(accountNumber, states);
			}

		}
		
//		//System.out.println("Name : "+node.getName());
//		//System.out.println("Value : "+node.getValue());

		if(node.getChilds()==null){
			return pathObjectMap;
		}
		
		for(XMLNode childNode : node.getChilds()){
			pathObjectMap.putAll(traverseAndPopulate(childNode, nodeStateDiffMap));
		}
		
		return pathObjectMap;
	}
	
	
	/*
	 * This method shall be called for the node on which we have added a child.
	 * This will thus populate the fields like level, path, parent etc for the child.*/
	private void updateChildNode(XMLNode childNode){
		if(childNode ==null){
			//System.out.println("ChildNode passed for updation is null!");
			return;
		}
		
		childNode.parent = this;
		childNode.path = childNode.getUpdatedPath();
		childNode.level = childNode.getUpdatedLevel(); 
	}
	
	/**
	 * @return the isMapped
	 * 
	 * This field is used for comparing this node.
	 * isMapped = true, represents that this node of present tree is already mapped some node of another tree
	 */
	public boolean isMapped() {
		return isMapped;
	}

	/**
	 * @return the mappedTo
	 */
	public XMLNode getMappedTo() {
		return mappedTo;
	}

	/**
	 * @param isMapped the isMapped to set
	 * This method is called by setMappedTo() to update the status.
	 */
	private void setMapped(boolean isMapped) {
		this.isMapped = isMapped;
	}

	/**
	 * @param mappedTo : to map the mappedTo node with another node 
	 */
	public void setMappedTo(XMLNode mappedTo) {
		
		this.mappedTo = mappedTo;
		
		if(this.mappedTo!=null){
			this.setMapped(true);
		}else{
			this.setMapped(false);
		}
	}

	
	/*	
	 * This method accepts the pair of key and a value.
	 * This attribute key-value pair is added to the parent node so that they can be utilized for validating the rowdel rule,
	 * without traversing the child nodes. 
	 */ 
	private boolean addRowdelKey(String key, String value)
	{
		if(key==null || value==null){
			return false;
		}
		
		if(this.rowdelKeys==null){
			this.rowdelKeys = new HashMap<String,String>();
		}
		
		System.out.println("Adding rowdel keys::"+key+":"+value);
		this.hasRowdelKeys = true;
		return (this.rowdelKeys.put(key, value)!=null);
	}
	
	public HashMap<String,String> getRowdelKeys(){
		return this.rowdelKeys;
	}
	
	public String asXML(){
		return this.asXML(false);
	}
	
	
	
	/*Accepts a boolean variable 'showNodeStatusWithColor'
	 * where parameter : 'true' : returns the XML node in a formatted way(by inserting the color).
	 * 		             'false': returns the XML nodes as it is. 
	 * */

	public String asXML(boolean returnFormattedNodeIfDifferent){

		String doubleQuotes = "'";
		
		String xml = "<";
		
		xml+=this.getName();
		
			if(this.getAttributes()!=null){
				for(String key : this.getAttributes().keySet()){
					xml = xml+" "+key+"="+doubleQuotes+this.getAttributes().get(key)+doubleQuotes;
				}
			}
			
			/*if(this.getRowdelKeys()!=null){
				for(String key : this.getRowdelKeys().keySet()){
					xml = xml+" "+key+"="+doubleQuotes+this.getAttributes().get(key)+doubleQuotes;
				}
			}*/
		xml+=">";
		
		if(this.getValue()!=null && !this.getValue().equals("")){
			xml+=this.getValue()+"</"+this.getName()+">";
		}
		
		//Using this code for
		if(returnFormattedNodeIfDifferent){
			//Replacing, since this format is not being read while being parsed by the react code.
			xml =  xml.replaceAll("<", "&lt;");
			xml =  xml.replaceAll(">", "&gt;");
			switch(this.differenceType){
				case NodeUtil.DIFFERENCE_TYPE_MISSING_NODE :
						//xml = "<font color="+doubleQuotes+NodeUtil.nodeColorMap.get(NodeUtil.DIFFERENCE_TYPE_MISSING_NODE)+doubleQuotes+"><xmp>"+xml+"</xmp></font>";
						xml = "<font color="+doubleQuotes+NodeUtil.nodeColorMap.get(NodeUtil.DIFFERENCE_TYPE_MISSING_NODE)+doubleQuotes+">"+xml+"</font>";
					break;
				case NodeUtil.DIFFERENCE_TYPE_DISCREPANCT_NODE :
						//xml = "<font color="+doubleQuotes+NodeUtil.nodeColorMap.get(NodeUtil.DIFFERENCE_TYPE_DISCREPANCT_NODE)+doubleQuotes+"><xmp>"+xml+"</xmp></font>";
						xml = "<font color="+doubleQuotes+NodeUtil.nodeColorMap.get(NodeUtil.DIFFERENCE_TYPE_DISCREPANCT_NODE)+doubleQuotes+">"+xml+"</font>";
					break;
				case NodeUtil.DIFFERENCE_TYPE_NEW_NODE :
					//xml = "<font color="+doubleQuotes+NodeUtil.nodeColorMap.get(NodeUtil.DIFFERENCE_TYPE_NEW_NODE)+doubleQuotes+"><xmp>"+xml+"</xmp></font>";
					xml = "<font color="+doubleQuotes+NodeUtil.nodeColorMap.get(NodeUtil.DIFFERENCE_TYPE_NEW_NODE)+doubleQuotes+">"+xml+"</font>";
					break;
				case NodeUtil.DIFFERENCE_TYPE_NONE :
					//xml = "<font color="+doubleQuotes+NodeUtil.nodeColorMap.get(NodeUtil.DIFFERENCE_TYPE_NONE)+doubleQuotes+"><xmp>"+xml+"</xmp></font>";
					xml = "<font color="+doubleQuotes+NodeUtil.nodeColorMap.get(NodeUtil.DIFFERENCE_TYPE_NONE)+doubleQuotes+">"+xml+"</font>";
					break;
				default:
					//xml = "<xmp>"+xml+"</xmp>";
					break;
			}
		}
		
		return xml+"<br>";
	
	}

	/**
	 * @return the discrepancyType
	 */
	public String getDifferenceType() {
		return differenceType;
	}

	/**
	 * @param discrepancyType the discrepancyType to set
	 */
	public void setDiscrepancyType(String differenceType) {
		this.differenceType = differenceType;
	}
	
	public static String findState(XMLNode node){
		String state = State.OTHER;
		if(node==null){
			return "";
		}
		
		String tagName = node.getName().toLowerCase();
		
		if(tagName.contains("account")){
			state = State.ACCOUNT_DETAILS;
		}else if(tagName.contains("holding")){
			state = State.HOLDING;
		}else if(tagName.contains("transaction")){
			state = State.TRANSACTION;
		}else if(tagName.contains("statement")){
			state = State.STATEMENTS;
		}else if(node.parent!=null){
			state = node.getParent().getState();
		}else{
			state = State.OTHER;
		}
		
		return state;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @return the hasRowdelKeys
	 */
	public boolean isHasRowdelKeys() {
		return this.hasRowdelKeys;
	}
} 	

