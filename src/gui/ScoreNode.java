package gui;

import java.util.Queue;

import gui.UndoRedoTree.Node;

public class ScoreNode {
	
	public Node n;
	public Queue <ScoreAction> scoreActions;
	
	public ScoreNode(Node n, Queue<ScoreAction> scoreActions)
	{
		this.n = n;
		this.scoreActions = scoreActions;
	}
	
	

}
