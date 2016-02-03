package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;

public class GetPropHierarchyUtils {
	
	/*public class PropGraphNode {
		List<PropGraphNode> incomingNeighbors;
		List<PropGraphNode> outgoingNeighbors;
		ObjectProperty prop;
		
		public PropGraphNode(ObjectProperty prop, List<PropGraphNode> incomingNeighbors, List<PropGraphNode> outgoingNeighbors) {
			this.prop = prop;
		}
		
	}
	
	public class PropGraph {
		private List<PropGraphNode> nodes;
		private ObjectPropertyDao opDao;  
		
		public PropGraph(ObjectPropertyDao opDao) {
			this.opDao = opDao;
			this.nodes = new ArrayList<PropGraphNode>();
		}
		
		public static PropGraphNode createPropGraphNode(ObjectProperty prop) {
			List<ObjectProperty> superproperties = prop.get
			List<PropGraphNode> superPropNodes;
			
			for(ObjectProperty superprop : superproperties) {
				superPropNodes.add(getPropGraphNode(superprop));
			}
			
			return new PropGraphNode(prop, superproperties, subproperties);
		}
		
		public static PropGraphNode getPropGraphNode(ObjectProperty prop) {
			for(PropGraphNode node : nodes) {
				if(node.prop.equals(prop)) {
					return node;
				}
			}
			
			return createPropGraphNode(prop);
		}
	}
	
	public static PropHierarchyNode generateFullTree() {
		
	}*/
}
