/* $Id: GrammarView.java,v 1.2 2007-04-30 19:53:31 rensink Exp $ */
package groove.view;

import groove.graph.Graph;
import groove.trans.GraphGrammar;
import groove.trans.RuleNameLabel;
import groove.trans.SystemProperties;

import java.util.Map;
import java.util.Set;

/**
 * Interface encapsulating a representation of a rule system that is essentially
 * a set of rule views, available as a map from names to views.
 * The view as a whole has a name and a set of properties.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface GrammarView<V extends RuleView> extends View<GraphGrammar> {
	/** Returns the name of the rule system. */
	public String getName();
	
	/** Returns the (fixed) properties of the rule system. */
	public SystemProperties getProperties();
	
	/** 
	 * Adds a rule view, and returns the possible view previously stored under that name.
	 * @param rule the rule view to be added; non-<code>null</code> 
	 * @return a rule view previously stored under the name of <code>rule</code>
	 */
	public V addRule(V rule) throws FormatException;
	
	/** Returns an unmodifiable map from rule names to rule views. */
	public Map<RuleNameLabel, V> getRuleMap();

    /**
     * Returns the rule view stored for a given rule name.
     */
    public V getRule(RuleNameLabel name);
    
    /**
     * Returns an unmodifiable map from priorities to non-empty sets of 
     * rules in this grammar with that priority.
     */
    public Map<Integer, Set<RuleView>> getPriorityMap();
    
	/** Returns the start graph of this grammar view. */
	public Graph getStartGraph();
	
	/** 
	 * Lazily converts the view to a fixed rule system.
	 * This may throw an exception if the view has errors.
	 * @return a rule system based on the name, properties and rules stored as views
	 * @throws FormatException
	 */
	public GraphGrammar toGrammar() throws FormatException;
}
