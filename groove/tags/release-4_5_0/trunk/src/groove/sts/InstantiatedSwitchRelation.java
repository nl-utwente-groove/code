package groove.sts;

import groove.lts.GraphTransition;
import groove.lts.RuleTransition;

@SuppressWarnings("all")
public class InstantiatedSwitchRelation {

    private SwitchRelation relation;
    private RuleTransition transition;

    public InstantiatedSwitchRelation(SwitchRelation relation,
            RuleTransition transition) {
        this.relation = relation;
        this.transition = transition;
    }

    public SwitchRelation getSwitchRelation() {
        return this.relation;
    }

    public GraphTransition getTransition() {
        return this.transition;
    }
}
