package groove.gui;

import groove.gui.jgraph.ControlJModel;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JGraph;
import groove.trans.GraphGrammar;

public class CAPanel extends JGraphPanel<JGraph> {

	private Options options;
	
	public CAPanel(Simulator simulator)
	{
		super(new JGraph(new ControlJModel(),false), false , simulator.getOptions());
	}

	public void setGrammar(GraphGrammar grammar)
	{
		if( grammar.getControl() == null ) {
			this.setEnabled(false);
		 this.getJGraph().setModel(null);
			return;
		}
			
		GraphJModel model = GraphJModel.newInstance(grammar.getControl(), getOptions());
		//new ControlJModel(grammar.getControl(), options);
		//System.out.println("Nodes: " + grammar.getControl().nodeCount() + " / Edges: " + grammar.getControl().edgeCount());
		this.getJGraph().setModel(model);
		this.refreshStatus();
	}
	
}
