package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;

public class GetPropHierarchyUtils {
	
	public class PropGraphNode {
		List<PropGraphNode> incomingNeighbors;
		List<PropGraphNode> outgoingNeighbors;
		ObjectProperty prop;
		
		public PropGraphNode(ObjectProperty prop, List<PropGraphNode> incomingNeighbors, List<PropGraphNode> outgoingNeighbors) {
			this.prop = prop;
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
	
	public class PropGraph {
		private List<PropGraphNode> nodes;
		private ObjectPropertyDao opDao;  
		
		public PropGraph(ObjectPropertyDao opDao) {
			this.opDao = opDao;
			this.nodes = new ArrayList<PropGraphNode>();
		}
		
		public PropGraphNode createPropGraphNode(ObjectProperty prop) {
			List<ObjectProperty> superproperties = getPropsForURIList(opDao.getSuperPropertyURIs(prop.getURI(), true), opDao);
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
	}
	
	public static PropHierarchyNode generateFullTree() {
		return null;
	}
}
