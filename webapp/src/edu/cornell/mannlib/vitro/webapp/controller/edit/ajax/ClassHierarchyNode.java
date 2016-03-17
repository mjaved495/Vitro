package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.google.gson.annotations.Expose;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;

public class ClassHierarchyNode {
	
	private VClass vcl;
	@Expose
	private List<ClassHierarchyNode> children;
	
	@Expose
	public String text;
	
	@Expose
	public String vClassURI;
	
	@Expose
	public String ontName;
	
	@Expose
	public String icon;
	
	@Expose
	public Hashtable<String, String> a_attr;
	
	@Expose
	public Hashtable<String, String> li_attr;
	
	public ClassHierarchyNode(VClass vcl, List<ClassHierarchyNode> children) {
		this.vcl = vcl;
		this.children = children;
		this.text = vcl.getName();
		this.vClassURI = vcl.getURI();
		this.ontName = ontName;
		this.icon = "/vivo/images/orangedot.png";
		this.a_attr = new Hashtable<String, String>();
		this.a_attr.put("data-vclass-uri", vcl.getURI());
		this.a_attr.put("title", vcl.getURI());
		this.li_attr = new Hashtable<String, String>();
		this.li_attr.put("id", vcl.getLocalName());
	}
	
	public ClassHierarchyNode(VClass vcl) {
		this.vcl = vcl;
		this.children = new ArrayList<ClassHierarchyNode>();
		this.text = vcl.getName();
		this.vClassURI = vcl.getURI();
		this.ontName = ontName;
		this.icon = "/vivo/images/orangedot.png";
		this.a_attr = new Hashtable<String, String>();
		this.a_attr.put("data-vclass-uri", vcl.getURI());
		this.a_attr.put("title", vcl.getURI());
		this.li_attr = new Hashtable<String, String>();
		this.li_attr.put("id", vcl.getLocalName());
	}
	
	public void addChild(ClassHierarchyNode node) {
		this.children.add(node);
	}
	
	public List<ClassHierarchyNode> children() {
		return this.children;
	}
	
	public VClass getVClass() {
		return this.vcl;
	}
}
