import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.rule.RuleToHostMap;

public static boolean filterFlag(HostGraph host, RuleToHostMap anchorMap) {
        HostNode image = anchorMap.nodeMap()
            .values()
            .iterator()
            .next();
        return !host.outEdgeSet(image)
            .stream()
            .allMatch({e -> !e.label()
                .text()
                .equals("flag")});
}
