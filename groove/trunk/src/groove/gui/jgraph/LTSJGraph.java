/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: LTSJGraph.java,v 1.6 2007-05-20 07:17:49 rensink Exp $
 */
package groove.gui.jgraph;

import groove.graph.Element;
import groove.gui.ExploreStrategyMenu;
import groove.gui.Options;
import groove.gui.SetLayoutMenu;
import groove.gui.Simulator;
import groove.gui.layout.Layouter;
import groove.gui.layout.SpringLayouter;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.State;
import groove.lts.Transition;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import org.jgraph.graph.DefaultGraphCell;

/**
 * Implementation of MyJGraph that provides the proper popup menu.
 * To construct an instance, setupPopupMenu() should be called
 * after all global final variables have been set.
 */
public class LTSJGraph extends JGraph {
    /** Constructs an instance of tje j-graph for a given simulator. */
    public LTSJGraph(Simulator simulator) {
    	super(LTSJModel.EMPTY_LTS_JMODEL);
        this.simulator = simulator;
        this.exploreMenu = new ExploreStrategyMenu(simulator);
        addMouseListener(new MyMouseListener());
        getGraphLayoutCache().setSelectsAllInsertedCells(false);
        setLayoutMenu.selectLayoutAction(createInitialLayouter());
        setEnabled(false);
    }

	/**
	 * Creates the layouter to be used at construction time.
	 */
	protected Layouter createInitialLayouter() {
		return new MyForestLayouter();
	}
    
    /** Specialises the return type to a {@link JModel}. */
    @Override
    public LTSJModel getModel() {
    	return (LTSJModel) graphModel;
    }
    
    /**
     * This implementation adds actions to move to different states within the LTS,
     * to apply the current transition and to explore the LTS, and
     * subsequently invokes the super implementation. 
     */
    @Override
    protected void fillPopupMenu(JPopupMenu result) {
        addSeparatorUnlessFirst(result);
        // State exploration sub-menu
        result.add(simulator.getApplyTransitionAction());
        result.add(getExploreMenu());

        // Goto sub-menu
        result.addSeparator();
        result.add(simulator.getGotoStartStateAction());
        result.add(scrollToCurrentAction);
        super.fillPopupMenu(result);
    }

    @Override
    protected void activatePopupMenu(Point atPoint) {
        super.activatePopupMenu(atPoint);
        if (getModel().getActiveTransition() == null)
            scrollToCurrentAction.setState(simulator.getCurrentState());
        else {
            scrollToCurrentAction.setTransition(simulator.getCurrentTransition());
        }
    }

    /**
	 * Overwrites the menu, so the forest layouter takes the LTS start state as
	 * its root.
	 */
    @Override
    protected SetLayoutMenu createSetLayoutMenu() {
        SetLayoutMenu result = new SetLayoutMenu(this, new SpringLayouter());
        result.addLayoutItem(createInitialLayouter());
        return result;
    }
    
    /**
     * Scrolls the view to a given node or edge of the underlying graph model.
     * 
     * @require nodeOrEdge instanceof State || nodeOrEdge instanceof Transition
     */
    public void scrollTo(Element nodeOrEdge) {
        JCell cell = ((GraphJModel) getModel()).getJCell(nodeOrEdge);
        assert cell != null;
        Rectangle2D bounds = getCellBounds(cell);
        if (bounds != null) {
            Rectangle scrollRect = new Rectangle((int) bounds.getX() - 100,
                    (int) bounds.getY() - 100, (int) bounds.getWidth() + 200,
                    (int) bounds.getHeight() + 200);
            scrollRectToVisible(scrollRect);
        }
    }

	/**
	 * Lazily creates and returns the exploration menu.
	 */
	protected final JMenu getExploreMenu() {
		if (exploreMenu == null) {
			exploreMenu = new ExploreStrategyMenu(simulator);
		}
		return this.exploreMenu;
	}

	/**
     * The simulator to which this j-graph is associated.
     */
    private final Simulator simulator;
    /**
     * The exploration menu for this jgraph.
     */
    private JMenu exploreMenu;
    /**
     * Action to scroll the JGraph to the current state or derivation.
     */
    private final ScrollToCurrentAction scrollToCurrentAction = new ScrollToCurrentAction();
    
	/**
	 * Action to scroll the LTS display to a (previously set) node or edge.
	 * @see #scrollTo(Element)
	 */
	public class ScrollToCurrentAction extends AbstractAction {
	    public void actionPerformed(ActionEvent evt) {
	        if (simulator.getCurrentState() == null)
	            scrollTo(simulator.getCurrentTransition());
	        else
	            scrollTo(simulator.getCurrentState());
	    }
	
	    /** 
	     * Adapts the name of the action so that it reflects that the element
	     * to scroll to is a given transition.
	     */
	    public void setTransition(Transition edge) {
	        putValue(Action.NAME, Options.SCROLL_TO_ACTION_NAME + " derivation");
	    }
	
	    /** 
	     * Adapts the name of the action so that it reflects that the element
	     * to scroll to is a given state.
	     */
	    public void setState(State node) {
	        putValue(Action.NAME, Options.SCROLL_TO_ACTION_NAME + " state");
	    }
	}

    /** 
     * Mouse listener that
     * activates a state or transition on a single click, and
     * switches to the state panel on a double click.
     */
    private class MyMouseListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent evt) {
			if (evt.getButton() == MouseEvent.BUTTON1) {
				// scale from screen to model
				java.awt.Point loc = evt.getPoint();
				// find cell in model coordinates
				DefaultGraphCell cell = (DefaultGraphCell) getFirstCellForLocation(loc.x,
						loc.y);
				if (cell instanceof GraphJEdge) {
					GraphTransition edge = (GraphTransition) ((GraphJEdge) cell).getEdge();
					simulator.setTransition(edge);
				} else if (cell instanceof GraphJVertex) {
					GraphState node = (GraphState) ((GraphJVertex) cell).getNode();
					if (!simulator.getCurrentState().equals(node)) {
						simulator.setState(node);
					}
				}
			}
		}
	}

	/**
	 * A specialization of the forest layouter that takes the LTS start graph
	 * as its suggested root.
	 */
	private class MyForestLayouter extends groove.gui.layout.ForestLayouter {
	    /**
	     * Creates a prototype layouter
	     */
	    public MyForestLayouter() {
	        super();
	    }
	
	    /**
	     * Creates a new instance, for a given {@link JGraph}.
	     */
	    public MyForestLayouter(String name, JGraph jgraph) {
	        super(name, jgraph);
	    }
	
	    /**
	     * This method returns a singleton set consisting of the LTS start state.
	     */
	    @Override
	    protected Collection<?> getSuggestedRoots() {
	        LTSJModel jModel = getModel();
	        return Collections.singleton(jModel.getJCell(jModel.getGraph().startState()));
	    }
	
	    /**
	     * This implementation returns a {@link MyForestLayouter}.
	     */
	    @Override
	    public Layouter newInstance(JGraph jGraph) {
	        return new MyForestLayouter(name, jGraph);
	    }
	}
}