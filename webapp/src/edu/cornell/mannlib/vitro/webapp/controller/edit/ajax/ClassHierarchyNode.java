package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import java.util.ArrayList;
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
	
	public ClassHierarchyNode(VClass vcl, List<ClassHierarchyNode> children) {
		this.vcl = vcl;
		this.children = children;
		this.text = vcl.getName();
		this.vClassURI = vcl.getURI();
		this.ontName = ontName;
		this.icon = "/vivo/images/orangedot.png";
	}
	
	public ClassHierarchyNode(VClass vcl) {
		this.vcl = vcl;
		this.children = new ArrayList<ClassHierarchyNode>();
		this.text = vcl.getName();
		this.vClassURI = vcl.getURI();
		this.ontName = ontName;
		this.icon = "/vivo/images/orangedot.png";
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
