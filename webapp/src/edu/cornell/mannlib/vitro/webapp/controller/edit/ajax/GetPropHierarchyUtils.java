package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.ConversionException;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;

public class GetPropHierarchyUtils {
	
	public static class PropGraphNode {
		List<PropGraphNode> incomingNeighbors;
		List<PropGraphNode> outgoingNeighbors;
		ObjectProperty prop;
		
		public PropGraphNode(ObjectProperty prop, List<PropGraphNode> incomingNeighbors, List<PropGraphNode> outgoingNeighbors) {
			this.prop = prop;
		}
		
		public ObjectProperty getProp() {
			return this.prop;
		}
		
	}
	
	private static List<ObjectProperty> getPropsForURIList(List<String> propURIs, ObjectPropertyDao opDao) {
        List<ObjectProperty> props = new ArrayList<ObjectProperty>();
        Iterator<String> urIt = propURIs.iterator();
        while (urIt.hasNext()) {
            String propURI = urIt.next();
            ObjectProperty op = opDao.getObjectPropertyByURI(propURI);
            if (op != null) {
                props.add(op);
            }
        }
        return props;
    }
	
	public static class PropGraph {
		private List<PropGraphNode> nodes;
		private ObjectPropertyDao opDao;  
		
		public PropGraph(ObjectPropertyDao opDao) {
			this.opDao = opDao;
			this.nodes = new ArrayList<PropGraphNode>();
			
			generateNodes();
		}
		
		private void generateNodes() {
			for(ObjectProperty prop : opDao.getAllObjectProperties()) {
				addOrGetPropNode(prop);
			}
		}
		
		public PropGraphNode createPropGraphNode(ObjectProperty prop) {
			List<ObjectProperty> superproperties;
			
			try {
				superproperties = getPropsForURIList(opDao.getSuperPropertyURIs(prop.getURI(), true), opDao);
			}
			catch(Exception e) {
				superproperties = new ArrayList<ObjectProperty>();
			}
			
			List<PropGraphNode> superPropNodes = new ArrayList<PropGraphNode>();
			
			for(ObjectProperty superprop : superproperties) {
				superPropNodes.add(getPropGraphNode(superprop));
			}

			List<ObjectProperty> subproperties = getPropsForURIList(opDao.getSubPropertyURIs(prop.getURI()), opDao);
			List<PropGraphNode> subPropNodes = new ArrayList<PropGraphNode>();
			
			for(ObjectProperty subprop : superproperties) {
				subPropNodes.add(getPropGraphNode(subprop));
			}
			
			
			return new PropGraphNode(prop, superPropNodes, subPropNodes);
		}
		
		public PropGraphNode getPropGraphNode(ObjectProperty prop) {
			for(PropGraphNode node : nodes) {
				if(node.prop.equals(prop)) {
					return node;
				}
			}
			
			return createPropGraphNode(prop);
		}
		
		public void addOrGetPropNode(ObjectProperty prop) {
			for(PropGraphNode node : nodes) {
				if(node.prop.equals(prop)) {
					return;
				}
			}
			
			nodes.add(createPropGraphNode(prop));
		}
		
		public PropHierarchyNode treeRoot() {
			for(PropGraphNode node : nodes) {
				if(node.incomingNeighbors.size() == 0) {
					return new PropHierarchyNode(node.getProp());
				}
			}
			return null;
		}
		
		public PropHierarchyNode generateHierarchy() {
			PropHierarchyNode result = new PropHierarchyNode("Property");
			while(treeRoot() != null) {
				PropHierarchyNode node = treeRoot();
				result.addChild(dfsTree(node));
			}
			return result;
		}
		
		public PropHierarchyNode dfsTree(PropHierarchyNode node) {
			if(opDao.getSubPropertyURIs(node.getProp().getURI()).size() == 0) {
				return node;
			}
			else {
				for(String uri : opDao.getSubPropertyURIs(node.getProp().getURI())) {
					node.addChild(dfsTree(new PropHierarchyNode(opDao.getObjectPropertyByURI(uri))));
				}
				return node;
			}
		}
	}
	
	public static PropHierarchyNode generateFullTree(ObjectPropertyDao opDao) {
		PropGraph pg = new PropGraph(opDao);
		return pg.generateHierarchy();
	}
	
	public static List<PropHierarchyNode> generatePropList(ObjectPropertyDao opDao) {
		List<ObjectProperty> ops = opDao.getAllObjectProperties();
		List<PropHierarchyNode> nodes = new ArrayList<PropHierarchyNode>();
		for(ObjectProperty prop : ops) {
			nodes.add(new PropHierarchyNode(prop));
		}
		return nodes;
	}
}
