package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.google.gson.annotations.Expose;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;

public class PropHierarchyNode {
	private ObjectProperty op;
	@Expose
	private List<PropHierarchyNode> children;
	
	@Expose
	public String text;
	
	@Expose
	public String propURI;
	
	@Expose
	public String ontName;
	
	@Expose
	public String icon;
	
	@Expose
	public Hashtable<String, String> a_attr;
	
	public PropHierarchyNode(ObjectProperty op, List<PropHierarchyNode> children) {
		this.op = op;
		this.children = children;
		this.text = op.getLocalName();
		this.propURI = op.getURI();
		this.icon = "/vivo/images/bluedot.png";
		this.a_attr = new Hashtable<String, String>();
		this.a_attr.put("data-vclass-uri", op.getURI());
		this.a_attr.put("title", op.getURI());
	}
	
	public PropHierarchyNode(ObjectProperty op) {
		this.op = op;
		this.children = new ArrayList<PropHierarchyNode>();
		this.text = op.getLocalName();
		this.propURI = op.getURI();
		this.icon = "/vivo/images/bluedot.png";
		this.a_attr = new Hashtable<String, String>();
		this.a_attr.put("data-vclass-uri", op.getURI());
		this.a_attr.put("title", op.getURI());
	}
	
	public PropHierarchyNode(String name) {
		this.text = name;
		this.children = new ArrayList<PropHierarchyNode>();
		this.a_attr = new Hashtable<String, String>();
		this.a_attr.put("data-vclass-uri", "#");
		this.a_attr.put("title", "#");
	}
	
	public void addChild(PropHierarchyNode node) {
		this.children.add(node);
	}
	
	public List<PropHierarchyNode> children() {
		return this.children;
	}
	
	public ObjectProperty getProp() {
		return this.op;
	}
}
