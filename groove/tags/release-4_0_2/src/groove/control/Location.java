package groove.control;import groove.trans.Rule;import java.util.Set;/** * Interface representing the "location" part of a GraphState. *  * @author Tom Staijen * @version $Revision $ */public interface Location {    /**     * Returns all unexplored rules that are enabled given a set of previously     * matched rules and a set of failed rules.     */    public Set<Rule> getEnabledRules(Set<Rule> matched, Set<Rule> failed);    /** returns whether this would be a success given the associated graph state */    public boolean isSuccess(Set<Rule> rules);    /** returns the name of the location, if any */    public String getName();}