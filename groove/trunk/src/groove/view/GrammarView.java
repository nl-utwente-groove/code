/* $Id: GrammarView.java,v 1.1 2007-04-29 09:22:35 rensink Exp $ */
package groove.view;

import groove.graph.Graph;
import groove.trans.GraphGrammar;
import groove.trans.NameLabel;
import groove.trans.Rule;
import groove.trans.SystemProperties;

import java.util.Map;

/**
 * Interface encapsulating a representation of a rule system that is essentially
 * a set of rule views, available as a map from names to views.
 * The view as a whole has a name and a set of properties.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface GrammarView<V extends RuleView> {
	/** Returns the name of the rule system. */
	public String getName();
	
	/** Returns the properties of the rule system. */
	public SystemProperties getProperties();
	
	/** 
	 * Adds a rule view, and returns the possible view previously stored under that name.
	 * @param rule the rule view to be added; non-<code>null</code> 
	 * @return a rule view previously stored under the name of <code>rule</code>
	 */
	public V add(V rule) throws FormatException;
	
	/** Returns a map from rule names to rule views. */
	public Map<NameLabel,V> getRuleViewMap();

    /**
     * Returns the rule view stored for a given rule name.
     */
    public V getRuleView(NameLabel name);
    
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
