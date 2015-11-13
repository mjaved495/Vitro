package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;

public class ClassHierarchyNode {
	
	private VClass vcl;
	private List<ClassHierarchyNode> children;
	
	public ClassHierarchyNode(VClass vcl, List<ClassHierarchyNode> children) {
		this.vcl = vcl;
		this.children = children;
	}
	
	public ClassHierarchyNode(VClass vcl) {
		this.vcl = vcl;
		this.children = new ArrayList<ClassHierarchyNode>();
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
