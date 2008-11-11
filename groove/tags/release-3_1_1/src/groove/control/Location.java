package groove.control;import groove.trans.Rule;import java.util.Set;/** * Interface representing the "location" part of a GraphState. Implementations * can enable reuse of instances by having the methods decide their return value * on the LocationCache passed in the methods' parameters. *  * @author Tom Staijen * @version $Revision $ */public interface Location {    /**     * return the target of a location when traversing a rule, given a     * LocationCache *     */    public Location getTarget(Rule rule, Set<Rule> failedRules);    /**     * Returns the dependency of a given rule at this state. The dependency is     * the set of rules on whose success or failure either the applicability of     * the given rule or the corresponding target location depends.     */    public Set<Rule> getDependency(Rule rule);    /**     * Returns all unexplored rules that are enabled given a set of previously     * matched rules and a set of failed rules.     */    public Set<Rule> getEnabledRules(Set<Rule> matched, Set<Rule> failed);    /** returns whether this would be a success given the associated graph state * */    public boolean isSuccess(Set<Rule> rules);        /** returns the name of the location, if any */    public String getName();}