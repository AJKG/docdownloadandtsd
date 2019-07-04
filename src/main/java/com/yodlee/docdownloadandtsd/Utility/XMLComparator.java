package com.yodlee.docdownloadandtsd.Utility;

/**

 * Copyright (c) 2019 Yodlee Inc. All Rights Reserved.

 *

 * This software is the confidential and proprietary information of Yodlee, Inc.

 * Use is subject to license terms.

 *

 */

import com.yodlee.docdownloadandtsd.Utility.NodeUtil.CompareLogic;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author RBharadwaj
 *
 */
public class XMLComparator {
	
	private XMLTree jdapTree = null;
	private XMLTree ajaxTree = null;
	public long noOfComparisions = 0;
	private HashMap<String, HashMap<String,Set<String>>> nodeStateDiffMap = new HashMap<String, HashMap<String,Set<String>>>();
	public XMLComparator(XMLTree jdapTree, XMLTree ajaxTree){
		if(jdapTree==null || ajaxTree==null){
			throw new NullPointerException("Either or both of the passed tree is null");
		}
		this.jdapTree = jdapTree;
		this.ajaxTree = ajaxTree;
	}
	
	public XMLComparator(String JdapXML, String AjaxXML){
		XMLParser jdapXMLParser = new XMLParser(JdapXML);
		XMLTree jdapTree = jdapXMLParser.parseXML();
		XMLParser AjaxXMLParser = new XMLParser(AjaxXML);
		XMLTree ajaxTree = AjaxXMLParser.parseXML();
		
		this.jdapTree = jdapTree;
		this.ajaxTree = ajaxTree;
	}
	
	private HashMap<String,String> compare(XMLTree jdapTree, XMLTree ajaxTree){
		HashMap<String,String> listOfObjectMaps = new HashMap<String,String>();
		
		if(jdapTree==null || ajaxTree==null){
			////System.out.println("Either of the tree is null...");
			return null;
		}

		compare(jdapTree.getRoot(), ajaxTree.getRoot());
		int heightOfjdapTree = jdapTree.traverse();
		int heightOfajaxTree = ajaxTree.traverse();
		listOfObjectMaps.put("jdapDifferenceXML",jdapTree.getXML(true));
		listOfObjectMaps.put("ajaxDifferenceXML",ajaxTree.getXML(true));
		this.nodeStateDiffMap.put("jdapAccountStateDiff",jdapTree.getNodeStateDiffMap());
		this.nodeStateDiffMap.put("ajaxAccountStateDiff",ajaxTree.getNodeStateDiffMap());
		return listOfObjectMaps;
	}
	
	public HashMap<String,String> compare(){
		return this.compare(this.jdapTree, this.ajaxTree);
	}
	
	
	/*This method compares the two node based on the argument 'compareLogic'*/
	private boolean compare(XMLNode node1, XMLNode node2, String compareLogic, List<String> rowdelFields){
		noOfComparisions++;
		if(node1==null || node2==null){
			return false;
		}else{
			//System.out.println("Comparing Nodes::"+node1.asXML()+"--"+node2.asXML());
			////System.out.println("compareLogic::"+compareLogic+"--"+rowdelFields.size());
		}
		
		
		if(!node1.getName().equals(node2.getName()) || node1.getLevel()!=node2.getLevel()){
			//System.out.println("tag Name different");
			return false;
		}else if((node1.isMapped() || node2.isMapped()) && !(node1.isMapped() && node2.isMapped()&& node1.getMappedTo()==node2)){
			//Checking if either one or both the nodes are already mapped and not mapped to each other then returning false 
			//One of the node is mapped with some other node
			//System.out.println("Either node is already Mapped");
			return false; 
		}else if(compareLogic.equals(CompareLogic.ROWDEL_FIELDS)){
			//System.out.println("Using rowdel fields");
			
			if(rowdelFields==null || rowdelFields.size()==0){
				//System.out.println("Rowdelfields passed is null");
				return false;
			}
			
			if(node1.getRowdelKeys()!=null && node2.getRowdelKeys()!=null )
			{

				//System.out.println("Rowdelfield matching here");
				boolean isMatchFound = false;
				HashMap<String, String> node1RowdelKeys = node1.getRowdelKeys();
				HashMap<String, String> node2RowdelKeys = node2.getRowdelKeys();
			
				//All the rowdel fields must match
				for(String rowdelField : rowdelFields){
					String node1RowdelValue = node1RowdelKeys.get(rowdelField);
					String node2RowdelValue = node2RowdelKeys.get(rowdelField);
					
					if(node1RowdelValue==null || node2RowdelValue==null || node1RowdelValue.length()==0 || node2RowdelValue.length()==0){
						//Since this rowdel field is not present, need to check other rowdel fields for comparison
						//System.out.println("Skipping rowdelField :"+rowdelField);
						continue;
					}
					
					isMatchFound = matchNodesBy(node1RowdelValue, node2RowdelValue, rowdelField);
					
					if(!isMatchFound){
						//Even if one field does not match rowdel fails
						//Hence, no need to match other fields
						break; 
					}
				}
				
				
				if(isMatchFound){
					mapNodes(node1,node2);
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}else if(compareLogic.equals(CompareLogic.ATTRIBUTES)){
				
				if(node1.getAttributes()!=null && node2.getAttributes()!=null && node1.getAttributes().size()>0){
					
					boolean isMatchFound = true;
						
						//Matching exact attributes
						for(String attributeKey : node1.getAttributes().keySet()){
							
							/*if(isAttributeToSkip(attributeKey)){
								System.out.println("Skipping ::"+attributeKey);
								continue;
							}*/
							
							String node1AttributeValue = node1.getAttributes().get(attributeKey);
							String node2AttributeValue = node2.getAttributes().get(attributeKey);
							
							if(node1AttributeValue==null || node2AttributeValue==null || node1AttributeValue.equals("") || node2AttributeValue.equals("")
								 || !node1AttributeValue.equals(node2AttributeValue)){
								//If either attribute is not present, null or not matching, we are returning false.
								isMatchFound = false;
								break;
							}
						}
						
						if(isMatchFound){
							mapNodes(node1,node2);
							return true;
						}else{
							return false;
						}	
						
				}else{
					return false;
				}
			
		}else if(compareLogic.equals(CompareLogic.TAG)){
			//System.out.println("Matching the tags here!!");
			
			//If the tags must be compared using the rowdel keys only, comparison based on tags must be skipped.
			if(node1.isHasRowdelKeys()||node2.isHasRowdelKeys()){
					return false;
			}
			
			if(node1.getParent()!=null && node2.getParent()!=null){
				XMLNode parentNode1 = node1.getParent();
				XMLNode parentNode2 = node2.getParent();
				
				//Parents of these nodes are matched with each other and hence these nodes shall match
				if(parentNode1.isMapped() && parentNode1.getMappedTo()==parentNode2
						&& parentNode2.isMapped() && parentNode2.getMappedTo()==parentNode1){
					mapNodes(node1,node2);
					return true;
				}else{
					return false;
				}
			}
		}
		
		if(node1.getChilds()!=null && node2.getChilds()!=null){
			
			//Checking if the rowdel eligible fields are matching
			
				for(String comparisonLogic : NodeUtil.comparisonLogics){

					if(comparisonLogic.equals(CompareLogic.ROWDEL_FIELDS)){
						
						    //Looping over the childs of first tree node
							for(int i =0; i<node1.getChilds().size(); i++){
								
								XMLNode childToCompare = node1.getChilds().get(i);
								
								List<List<String>> ruleList = RowdelUtil.getRuleList(jdapTree.getContainer(), childToCompare.getName());
								if(ruleList==null || ruleList.size()==0){
									continue; // The node is not a rowdelNode
								}
								
								//Applying all the rowdel Rules applicable to the node
								for(List<String> rowdelRule : ruleList){
									
									if(childToCompare.isMapped()){
										break; //Since the node is already mapped; no more comparisons required.
									}
									
									//Looping over the childs of second tree node
									for(int j=0; j<node2.getChilds().size();j++){
										boolean matchFound = compare(node1.getChilds().get(i),node2.getChilds().get(j),comparisonLogic, rowdelRule);
										if(matchFound){
											compare(node1.getChilds().get(i),node2.getChilds().get(j), CompareLogic.DEFAUT);
											break;
										}
									}
								}
								
								//end for loop for second tree nodes
							}//end for loop for first tree nodes
						
					}else{
						for(int i =0; i<node1.getChilds().size(); i++){
							for(int j=0; j<node2.getChilds().size();j++){
								boolean matchFound = compare(node1.getChilds().get(i),node2.getChilds().get(j),comparisonLogic, null);
								if(matchFound){
									compare(node1.getChilds().get(i),node2.getChilds().get(j), CompareLogic.DEFAUT);
									break;
								}
							}//end for loop for second tree nodes
						}//end for loop for first tree nodes
					}
				}//end of comparison Logic
	
			
		}
		
		//We have reached this node after bypassing all the above cases. Hence these nodes are same but have data descripancy
		if(/*!node1.isHasRowdelKeys()&&!node2.isHasRowdelKeys()&&*/!node1.isMapped()&&!node2.isMapped()){
			//System.out.println(node1.getRowdelKeys()+"::"+node1.isHasRowdelKeys()+"::"+node1.getName()+"::"+node2.getName());
			mapNodes(node1,node2);
			return true;
		}
		
		return false;
	}
	
	private boolean compare(XMLNode node1, XMLNode node2){
		return compare(node1, node2, CompareLogic.DEFAUT,null);
	}
	
	private boolean compare(XMLNode node1, XMLNode node2, String compareLogic){
		return compare(node1, node2, compareLogic,null);
	}
	
	private boolean isDiscripant(XMLNode node1, XMLNode node2){
		if(node1==null || node2==null){
			return (node1==null && node2==null)?false:true;
		}else if(!node1.getName().equals(node2.getName())){
			return true; 
		}else{
			
			if(!node1.getValue().equals("") && !node1.getValue().equals(node2.getValue())){
				return true;				//If found difference in the value sending discrepancy true
			}else if(node1.getAttributes()==null && node2.getAttributes()==null){
				return false;
			}else if(node1.getAttributes()==null || node2.getAttributes()==null){
				
				//If all the attributes present are from the attributes that needs to be skipped, 
				//then the nodes are equal else, the nodes are descripant
				//Ex : Node  1: <transactionList> ; Node 2 : <transactionList pageHash='-313823316'> are equal
				XMLNode node = node1;
				if(node1.getAttributes()==null){
					node = node2;
				}
				
				boolean missMatch = false;
				for(String attributeName : node.getAttributes().keySet()){
					if(!isAttributeToSkip(attributeName)){
						missMatch = true;
						break;
					}
				}
				
				return missMatch;
				
			}else{
				
				boolean missMatch = false;
				for(String attributeName : node1.getAttributes().keySet()){
					if(isAttributeToSkip(attributeName)){
						continue;
					}
					String node1AttributeValue = node1.getAttributes().get(attributeName);
					String node2AttributeValue = node2.getAttributes().get(attributeName);
					
					if(node1AttributeValue==null || node2AttributeValue==null
							|| !node1AttributeValue.equals(node2AttributeValue)){
						missMatch = true;
						break;
					}
				}
				
				//If found difference in attributes sending discrepancy true
				return missMatch;
			}
		}
	}
	
	private boolean matchNodesBy(String node1RowdelValue, String node2RowdelValue, String rowdelField){

		boolean isMatchFound = false;
		
		System.out.println(node1RowdelValue+"::"+node2RowdelValue+"::"+rowdelField);
		if(node1RowdelValue==null || node2RowdelValue==null || node1RowdelValue.length()==0 || node2RowdelValue.length()==0){
			//value for the rowdel fields shall not be null or empty string
			//System.out.println("Attribute value for "+rowdelField+" is null");
			return false;
		}else if(node1RowdelValue.equals(node2RowdelValue)){
			isMatchFound = true;
		}else {
			
			switch(rowdelField){

				case NodeUtil.ACCOUNT_NUMBER :
				
					//Matching partial account number (Since full account number did not match)
					if(node1RowdelValue.length()>=4 && node2RowdelValue.length()>=4
							&& node1RowdelValue.substring(node1RowdelValue.length()-4).equals(node2RowdelValue.substring(node2RowdelValue.length()-4))){
						//Matching the last 4 digits of the account number
						isMatchFound = true;	
					}
				
					break;
			
				case NodeUtil.AMOUNT :
				
					//Converting the amount to double and then mapping here.
					node1RowdelValue = node1RowdelValue.replaceAll(",", "");
					node2RowdelValue = node2RowdelValue.replaceAll(",", "");
				
					double amount1 = Double.parseDouble(node1RowdelValue);
					double amount2 = Double.parseDouble(node2RowdelValue);
				
					if(amount1==amount2){
						isMatchFound = true;
					}
				
					break;
				
				default :
					//Since use case matched
					isMatchFound = false;
					break;
		
			}
		}
		
		
		return isMatchFound;
	
	}
	
	private void mapNodes(XMLNode node1, XMLNode node2){
		if(node1==null || node2==null){
			return;
		}
		
		node1.setMappedTo(node2);
		node2.setMappedTo(node1);
		
		if(isDiscripant(node1, node2)){
			node1.setDiscrepancyType(NodeUtil.DIFFERENCE_TYPE_DISCREPANCT_NODE);
			node2.setDiscrepancyType(NodeUtil.DIFFERENCE_TYPE_DISCREPANCT_NODE);
		}else{
			node1.setDiscrepancyType(NodeUtil.DIFFERENCE_TYPE_NONE);
			node2.setDiscrepancyType(NodeUtil.DIFFERENCE_TYPE_NONE);
		}
	}

	
	/**
	 * @return the nodeStateDiff 
	 * 		String : Represents the key : jdapAccountStateDiff or ajaxAccountStateDiff respective to the type of firemem.(JDAP or AJAX)
	 * 		String : Represents the key : accountNumber(x) to map the discrepancy
	 * 		Set<String> : Represents the states that have the discrepancy for the accountNumber(x).
	 * 	
	 */
	public HashMap<String, HashMap<String, Set<String>>> getNodeStateDiff() {
		return nodeStateDiffMap;
	}

	/**
	 * @return the jdapTree
	 */
	public XMLTree getJdapTree() {
		return jdapTree;
	}

	/**
	 * @return the ajaxTree
	 */
	public XMLTree getAjaxTree() {
		return ajaxTree;
	}
	
	private boolean isAttributeToSkip(String attributeName){
		
		boolean isSkip = false;
		
		for(String attributeToCompare : NodeUtil.attributesToSkipForDiscripancy){
			if(attributeName==null || attributeName.trim().equals(attributeToCompare)){
				isSkip = true;
				break;
			}
		}
		
		return isSkip;
	}
	
}
