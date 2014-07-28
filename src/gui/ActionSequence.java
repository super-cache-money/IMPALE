package gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

class ActionSequence implements Serializable
{
	ArrayList<ScoreAction> editarr;
	HashSet<Integer> nodeseqschanged;
	UndoRedoTree.Node node;
	
	public ActionSequence (ArrayList<ScoreAction> editarr, HashSet<Integer> nodeseqschanged, UndoRedoTree.Node node)
	{
		
		this.editarr = (ArrayList<ScoreAction>) editarr.clone();
		this.nodeseqschanged = Alignment.al.scoreal.seqschangedset;
		Alignment.al.scoreal.seqschangedset = new HashSet<Integer>();
		Alignment.al.scorequeue.clear();
		this.node = node;
	}
	
	
	
	
}