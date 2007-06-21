package groove.gui;

import groove.control.ControlAutomaton;
import groove.control.ControlView;
import groove.gui.jgraph.ControlJModel;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JGraph;
import groove.gui.layout.Layouter;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.NameLabel;
import groove.view.DefaultGrammarView;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;

public class CAPanel extends JPanel  implements SimulationListener {

	private Options options;
	private ControlAutomaton control; 
	private Simulator simulator;

	AutomatonPanel autPanel;
	JTextPane textPanel;
	
	private DefaultGrammarView grammar;
	
	
	public CAPanel(Simulator simulator)
	{
		super();
		this.setLayout(new BorderLayout());
		JToolBar toolBar = new JToolBar();
		JButton parseButton = new JButton("parse");
		toolBar.add(parseButton, BorderLayout.SOUTH);
		parseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				ControlView cv = CAPanel.this.grammar.getControl();
				String program = CAPanel.this.textPanel.getText();
				if( program == null )
					return;
				if( cv == null ) {
					cv = new ControlView();
					cv.initScope(grammar);
					cv.setProgram(CAPanel.this.textPanel.getText());
					cv.loadProgram();
					grammar.setControl(cv);
				} else {
					cv.setProgram(CAPanel.this.textPanel.getText());
					cv.loadProgram();
				}
				CAPanel.this.simulator.start();
			}
		}
		);
		
		this.add(toolBar, BorderLayout.NORTH);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(300);
		splitPane.add(autPanel = new AutomatonPanel(simulator));
		splitPane.add(textPanel = new JTextPane());
		this.add(splitPane, BorderLayout.CENTER);
		
		simulator.addSimulationListener(this);
		this.simulator = simulator;
	}
	
	/** 
	 * Needed by Simulator because this is not a JGraphPanel
	 * @return the JGraphPanel in the JSplitPane
	 */
	public JGraphPanel getJGraphPanel() {
		return autPanel;
	}

	public void applyTransitionUpdate(GraphTransition transition) {
		//
	}

	public void setGrammarUpdate(DefaultGrammarView grammar) {
		this.grammar = grammar;
		
		autPanel.getJGraph().setModel(GraphJModel.EMPTY_JMODEL);
		autPanel.setEnabled(false);
		textPanel.setText("");
		textPanel.setEnabled(false);
		textPanel.setEditable(false);

		if( grammar.getControl() != null )
		{
			ControlView cv = grammar.getControl();
			textPanel.setText(cv.program());
			textPanel.setEnabled(true);
			textPanel.setEditable(true);
			
			autPanel.setEnabled(true);
			JGraph jGraph = autPanel.getJGraph();

			jGraph.setEnabled(true);
			GraphJModel model = GraphJModel.newInstance(cv.getAutomaton(), autPanel.getOptions());
			jGraph.setModel(model);
			autPanel.layoutGraph(cv.getAutomaton());
			autPanel.refreshStatus();
		}
	}

	public void setRuleUpdate(NameLabel name) {
		// TODO Auto-generated method stub
	}

	public void setStateUpdate(GraphState state) {
		// TODO Auto-generated method stub
	}

	public void setTransitionUpdate(GraphTransition transition) {
		// TODO Auto-generated method stub
	}

	public void startSimulationUpdate(GTS gts) {
		// TODO Auto-generated method stub
	}
	
}
	
class AutomatonPanel extends JGraphPanel<JGraph> 
{	
	private Layouter layouter;
	private ControlAutomaton control;
	
	public AutomatonPanel(Simulator simulator){
		super(new JGraph(new ControlJModel(),false), true , simulator.getOptions());
		this.getJGraph().setConnectable(false);
		this.getJGraph().setDisconnectable(false);
		this.getJGraph().setEnabled(false);
		layouter = new MyForestLayouter().newInstance(getJGraph());
	}

	public void layoutGraph(ControlAutomaton aut)
	{
		control = aut;
		layouter.start(true);
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
	        GraphJModel jModel = (GraphJModel) getJModel();
	        
	        return Collections.singleton(jModel.getJVertex(control.startState()));
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
