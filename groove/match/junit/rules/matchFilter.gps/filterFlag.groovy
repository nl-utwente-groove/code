import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.host.HostEdge;
import groove.grammar.rule.RuleToHostMap;

public static boolean filterFlag(HostGraph host, RuleToHostMap anchorMap) {
        HostNode image = anchorMap.nodeMap()
            .values()
            .iterator()
            .next();
        boolean result = true;
        for (HostEdge edge: host.outEdgeSet(image)) {
        	if (edge.label().text().equals("flag")) {
        		result = false;
        		break;
        	}
        }
        return result;
}
