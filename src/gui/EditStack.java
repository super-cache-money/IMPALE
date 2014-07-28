package gui;

import gui.ScoringAlignment.SetBusyIcon;

import java.awt.Canvas;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class EditStack extends Stack<Edit>implements Runnable, Serializable{
	
	
	public Set<ResiduePos> currentStartingSelected;
	public Set<ResiduePos> currentEndingSelected;
//	Alignment al;
	int secsbetweenedit = 2;
	Set seqschanged;
	Vector<EditStack> edits;
	boolean firstEditAdded = false;
	transient EditTimerTask timertask =null;
	transient Timer timer;
	


	
	public class EditTimerTask extends TimerTask{
		Set seqschangedtimer;
		AtomicBoolean isComplete;
		AtomicBoolean alreadyrun;
		public EditTimerTask()
		{
			super();
			isComplete = new AtomicBoolean(false);
			alreadyrun= new AtomicBoolean(false);

		}
		@Override
		public void run() {
			System.out.println("Starting task");
			synchronized(Alignment.al.scorequeue)
			{	
			if(!alreadyrun.get())//may be cancelled and run immediately in the case of undo
			{   
			Alignment.al.urt.newEdit(EditStack.this);
			synchronized (Alignment.al.scoreal.scoreProcessingQueue)
			{
				Alignment.al.scoreal.scoreProcessingQueue.add(new ActionSequence(Alignment.al.scorequeue,Alignment.al.scoreal.seqschangedset, Alignment.al.urt.currentNode));
			}
			Alignment.al.currentEdit = new EditStack();
			
			isComplete.set(true);
			}
			System.out.println("Ending task");
			}

			// TODO Auto-generated method stub
			
		}
		
	}
	public EditStack()
	{
		super();
//		this.al = al;
		seqschanged = Collections.synchronizedSet(new HashSet<Integer>());
		currentStartingSelected = new HashSet<ResiduePos>();
		currentEndingSelected = new HashSet<ResiduePos>();
		timer = new Timer();

		
		
	}
	
	@Override
	public void run() {
		//THIS SHIT SHOULD ONLY RUN IN UNDO/REDO!
		Set<Integer> affectedseqs = new HashSet<Integer>();
		int count = 0;
		int leftmost = 9999999;
		int rightmost = 0;
		int topmost = 9999999;
		int botmost = 0;
		while(!super.isEmpty())
		{
			
			Edit current = super.pop();
			int seq = Alignment.al.inverseviewmap.get(current.getUnderlyingSeq());
			if(current.respos <leftmost)
				leftmost = current.respos;
			if(current.respos >rightmost)
				rightmost = current.respos;
			if(seq < topmost)
				topmost = seq;
			if(seq > botmost)
				botmost = seq;
			
//				count++;
			affectedseqs.add(current.getUnderlyingSeq());
			current.run();
			
			
		}
		Alignment.al.panel.canvas.viewport.scrollTo(leftmost, rightmost, topmost, botmost);
//		String whocares = JOptionPane.showInputDialog("About to repaint");
//		al.panel.canvas.repaint();
		Iterator<Integer> it = affectedseqs.iterator();
		while(it.hasNext())
		{
			Alignment.al.netBlock.seqChange(it.next());
			
		}
		
		Alignment.al.panel.canvas.viewport.selected.clear();
		Iterator<ResiduePos> selectedIt = currentStartingSelected.iterator();
		while(selectedIt.hasNext())
		{
			ResiduePos currentpos = selectedIt.next();
			Alignment.al.panel.canvas.viewport.selected.add(new ResiduePos(currentpos.location[0],Alignment.al.inverseviewmap.get(currentpos.location[1])));
		}
		// TODO Auto-generated method stub
		
	} 
	
	
	public void pushToTreeAndScore()
	{
		synchronized(Alignment.al.scorequeue)
		{
			if(timertask!=null)
				timertask.cancel();
		timertask = new EditTimerTask();
		
		timer.schedule(timertask,secsbetweenedit*1000);
		Alignment.al.scoreal.upToDate.set(false);
		SwingUtilities.invokeLater(Alignment.al.scoreal.new SetBusyIcon(true));
		}
	}
	
//	public void pushToRedo()
//	{
//		
//		al.urs.redo.push(this);
//		al.currentEdit = new EditStack(al);
//		
//		
//	}
	
//	public void updateScoreBlock()
//	{
//		Vector <Edit> scoreEdit = new Vector<Edit>();
//		
//		for(int i = 0; i < this.size(); i++)
//		scoreEdit.add(this.get(i));
//		al.netBlock.editsQ.add(scoreEdit);
//		
//	}
	

}
