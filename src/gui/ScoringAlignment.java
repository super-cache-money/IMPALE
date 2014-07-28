package gui;

import gui.Residue.ResidueType;
import gui.UndoRedoTree.Node;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class ScoringAlignment extends ArrayList<ArrayList<Residue.ResidueType>> {
	int progressUpdateInterval = 50;
	int scoreFlushInterval = 100;
	//disable scoring disable_scoring HERE
	boolean disable_scoring = false;
	AtomicBoolean upToDate;
	long currprog = 0;
	Object progressBarLock = new Object();
//	Object savingSessionScoresLock = new Object();
//	AtomicBoolean savingSessionScores;
//Alignment al;
HashSet <Integer> seqschangedset;
long numOfPairs = 0;
QueueWorker qw;
AtomicInteger pacmanCurrentUpdate;
ActionSequence lastAction = null;
//usedaslock
public LinkedList<ActionSequence> scoreProcessingQueue;
StickyScoreBlock [] quickblock;
public Object QueueWorkerLock;
public Object scoreComputeLock;
public Vector<StickyScoreBlock> stickyblocks;
Vector<ScoreWorker> currentWorkers;
public Object EditLock;
public Object scoreEditingLock = new Object();
AtomicInteger outstandingEditWorkers;

long time;
Boolean busy = false;
BigInteger oldscore;
BigInteger score;
int labelwidth = 25;
int [] [] netscore;
AtomicInteger outstandingFreshWorkers;
long editscorediff;
int numeditworkers = 3;
int longestupperbound = 0;

public ScoringAlignment()
{
	
	pacmanCurrentUpdate= new AtomicInteger(0);
	upToDate=new AtomicBoolean(false);
	EditLock = new Object();
	
	currentWorkers = new Vector<ScoreWorker>();
	netscore= new int [Alignment.al.size()][Alignment.al.size()];
	oldscore = BigInteger.ZERO;
	score = BigInteger.ZERO;
	QueueWorkerLock = new Object();
	scoreComputeLock = new Object();
	for(int i = 0 ; i< Alignment.al.size(); i++)
	{
		add(new ArrayList<Residue.ResidueType>(){
			
			@Override
			public Residue.ResidueType get(int num)
			{

				try{
				return super.get(num);
				}
				catch(Exception e)
				{
					return Residue.ResidueType.BLANK;
				}
				
			}
			
			
			
		});
		for(int j = 0; j < Alignment.al.longestSeq;j++)
		{
			get(i).add(Alignment.al.get(i).get(j).getType());
		}
	}
	
	System.out.println(netscore.length + "netlength");
	
	seqschangedset = new HashSet<Integer>();
	scoreProcessingQueue = new LinkedList<ActionSequence>()
			{
		
		@Override
		public int size()
		{
			int out = super.size();
			if(outstandingEditWorkers!=null)
			if(outstandingEditWorkers.get()>0)
			{
				out++;
			}
			if(outstandingFreshWorkers!=null)
			if(outstandingFreshWorkers.get()>0)
			{
				out++;
			}
			
			return out;
		}
		
		@Override
		public boolean add(ActionSequence e)
		{
//			pacmanCurrentUpdate.set(size()+1);
			boolean bl = super.add(e);
			upToDate.set(false);
			SwingUtilities.invokeLater(ScoringAlignment.this.new SetBusyIcon(true));
//			SwingUtilities.invokeLater(new Runnable(){
//
//				@Override
//				public void run() {
////					if(Alignment.al.panel.topPanel.editsInQLabel!=null)
//					Alignment.al.panel.topPanel.editsInQLabel.setText(pacmanCurrentUpdate.get()+"");
//					// TODO Auto-generated method stub
//					
//				}
//				
//			});
			synchronized(QueueWorkerLock)
			{
				if(!Alignment.al.blockEdits)
			QueueWorkerLock.notify();
			}
			
			return bl;
		}
		
		@Override
		public ActionSequence pop()
		{
			
			return super.pop();
		}
		
		
			};
			
	stickyblocks = new Vector<StickyScoreBlock>();
	qw = new QueueWorker();
	qw.execute();
}

public int getLongestSeq()
{
	int max = 0;
	for(int i = 0; i<this.size();i++)
	{
		if(this.get(i).size()>max)
		{
			max = this.get(i).size();
		}
	}
	longestupperbound = max;
	return max;
}
public int getBlockPosAtPos(int res) //same as getRegion()
{
	for(int i = 0; i < stickyblocks.size(); i++)
	{
		if(stickyblocks.get(i).endres>res)
			return i;
			
			
	}
	return -1;
}
public void prepareQuickblock()
{
	int longest = getLongestSeq();
	System.out.println(longest);
	quickblock = new StickyScoreBlock[longestupperbound];
	for(int i = 0; i < stickyblocks.size(); i++)
	{
//		System.out.println("block " +i + " " + stickyblocks.get(i).endres);
		for(int j = stickyblocks.get(i).startres; j <= stickyblocks.get(i).endres; j++)
		{
			try{
			quickblock[j]= stickyblocks.get(i);
			}
			catch(ArrayIndexOutOfBoundsException ex)
			{
				System.out.println("Main alignment stickyblocks longer than scoring stickyblocks. oh well.");
			}
			
		}
		
		
		
	}
	StickyScoreBlock last = stickyblocks.get(stickyblocks.size()-1);
	
	for(int i = (last.endres)+1; i<longestupperbound;i++)
	{
		quickblock[i] = last;
	}
	
	
}

public void dispatchToScoreWorkers(Vector<Stack<SequencePairwise>> coreWork, Node node)
{

	currprog = 0;
	Alignment.al.panel.topPanel.scoreProgressBar.setVisible(true);
	Alignment.al.panel.topPanel.scoreProgressBar.setValue(0);
	outstandingFreshWorkers = new AtomicInteger(coreWork.size());
	Vector<ScoreWorker> workers = new Vector<ScoreWorker>();
	boolean cancel;
	synchronized(busy)
	{
		cancel = busy;
			
	}
	if(cancel)
	for(int i = 0; i < currentWorkers.size(); i++)
	{
		currentWorkers.get(i).cancel(false);
	}
	progressUpdateInterval=(int)(numOfPairs/20/coreWork.size()); //this kind of updates in 5% jumps, since cores tend to finish work at a similar time
	if(progressUpdateInterval ==0)
		progressUpdateInterval=1;
	for(int i = 0; i<coreWork.size();i++)
	{
		workers.add(new ScoreWorker(coreWork.get(i), node));
		workers.get(i).execute();
	}
	synchronized(currentWorkers)
	{
		currentWorkers = workers;
	}
	
	
}

public int computeEditSumOfPairs( ActionSequence currentActions) throws InterruptedException
{
	UndoRedoTree.Node node = currentActions.node;
	System.out.println("lub" + longestupperbound);
	editscorediff = 0;
	Iterator<Integer> changedit = currentActions.nodeseqschanged.iterator();
	HashSet<SequencePairwise> allpairs = new HashSet<SequencePairwise>();
	 currentWorkers = new Vector<ScoreWorker>();
	Vector<Stack<SequencePairwise>> workerlists = new Vector<Stack<SequencePairwise>>(); 
	for(int i = 0; i < numeditworkers;i++)
		workerlists.add(new Stack<SequencePairwise>());
	for(int i = 0; i < currentActions.nodeseqschanged.size(); i++)
	{
		int currentseq = changedit.next();
		for(int j = 0; j < Alignment.al.size(); j++)
		{
			allpairs.add(new SequencePairwise(currentseq,j));
		}
	}
	Iterator<SequencePairwise> allpairsit = allpairs.iterator();
	for(int i = 0; i < allpairs.size();i++)
	{
		workerlists.get(i%numeditworkers).push(allpairsit.next());
	}
	numOfPairs=allpairs.size();
	dispatchToScoreWorkers(workerlists,node);
	return 0;
}
public int computeFreshSumOfPairs(UndoRedoTree.Node node) throws InterruptedException
{
	netscore= new int [Alignment.al.size()][Alignment.al.size()];
	time = new Date().getTime();
	currprog = 0;
//	System.out.println("toodloo" + this.get(0).get(0));
	boolean cancel = false;
	//firstpublish = true;
	
	int total = 0;
	long score = new Long(0);
	int numcores = Runtime.getRuntime().availableProcessors();
	
	Vector<Stack<SequencePairwise>> cores = new Vector<Stack<SequencePairwise>>();
	numcores = 4;
	for(int i = 0; i < numcores; i++)
	{
		cores.add(new Stack<SequencePairwise>());
	}
	
	int counter = 0;
	
	for(int i = 0; i < this.size(); i ++)
	{
		
		for(int j = i; j < this.size(); j ++) //including diagonal
		{
			//scores[i][j] = Alignment.al.get(i).pairwiseSubset(Alignment.al.get(j), startres, endres);
			cores.get(counter%cores.size()).push(new SequencePairwise(i,j));
			//total = total + scores[i][j];
			counter++;
		}
			
	}
	
	numOfPairs=counter;
	dispatchToScoreWorkers(cores, node);
	return 0;
}


public StickyScoreBlock getBlockAt(int res)
{
	for(int i = 0; i < stickyblocks.size(); i++)
	{
		if(stickyblocks.get(i).endres>=res)
			return stickyblocks.get(i);
	}
	
	return null;
}
class StickyScoreBlock
{
	int startres, endres;
	int [][] scores;
	int totalscore;
	public StickyScoreBlock(int startres, int endres)
	{
		scores = new int [Alignment.al.size()][Alignment.al.size()];
		this.startres = startres;
		this.endres = endres;
		totalscore =0;
	}
	public void addScores()
	{
		totalscore = 0;
		for(int i = 0; i < Alignment.al.size(); i++)
			for(int j = i + 1; j < Alignment.al.size(); j++)
		{
			totalscore+= scores[j][i];
		}
	}
	public void increment(int block1, int block2, int score)
	{
		if(block1>block2)
		{
			scores[block2][block1]+=score;
		}
		else
		{
			scores[block1][block2]+=score;
		}
		
	}
	
	public int get(int num1, int num2)
	{
		if(num1>num2)
			return scores[num1][num2];
		else
			return scores[num2][num1];
	}
	
}



class SetBusyIcon implements Runnable
{

	boolean busy;
	SetBusyIcon(boolean bool)
	{ 
		busy=bool;
	}
	@Override
	public void run() {
		ImageIcon icon;
		if(busy)
		{
			icon = Alignment.al.panel.topPanel.busyTrueIcon;
		}
		else
		{
			icon = Alignment.al.panel.topPanel.busyFalseIcon;
		}
		
		Alignment.al.panel.topPanel.busyLabel.setIcon(icon);
		// TODO Auto-generated method stub
		
	}
	
}





//formally pairwiseComparisonSticky
public int pairwiseComparisonChange(int seq1, int seq2)
{
//	for(int i = 0; i < regions.size(); i++)
//	{
//		regions.get(i).increment(i, i, i); WTF REALLY NOW
//	}
	int score = 0;
	boolean gapOpen1 = false;
	boolean gapOpen2 = false;
	ArrayList<ResidueType> main = this.get(seq1);
	ArrayList<ResidueType> other = this.get(seq2);
	Residue.ResidueType thisres, otherres;
	int startpos = 0;
	int endpos = longestupperbound-1;
	//Clear out old stickyblock scores
	if(seq1>seq2)
	{
	for(int i = 0; i < stickyblocks.size(); i++)
	{
		stickyblocks.get(i).scores[seq2][seq1] = 0;
	}
	}
	else
	{
	for(int i = 0; i < stickyblocks.size(); i++)
	{
		stickyblocks.get(i).scores[seq1][seq2] = 0;
	}
	}
	
	
	for(int i = startpos; i<=endpos; i++)
	{
		
		thisres = main.get(i);
		otherres = other.get(i);
		if(thisres!=Residue.ResidueType.BLANK || otherres!=Residue.ResidueType.BLANK)
		{
		//check equality

		
		//Gap for this res
		
		if(thisres==Residue.ResidueType.BLANK)
		{
			gapOpen2 = false;
			if(gapOpen1)
			{
				score+=Sequence.scoreGapExtension;
			}
			else
			{
				score+=Sequence.scoreGapOpen;
				gapOpen1 = true;
			}
		}
		else if(otherres==Residue.ResidueType.BLANK)
		{
			gapOpen1=false;
			if(gapOpen2)
			{
				score+=Sequence.scoreGapExtension;
			}
			else
			{
				score+=Sequence.scoreGapOpen;
				gapOpen2 = true;
			}
		}
		else
		{
			gapOpen1=false;
			gapOpen2 = false;
			if(Sequence.isProtein)
				score+=Residue.AAsubstitution[thisres.getInt()][otherres.getInt()];
			else
				score+=Residue.DNAsubstitution[thisres.getInt()][otherres.getInt()];
		}

		}
		if(i<quickblock.length)
		quickblock[i].increment(seq1, seq2, score);
		else
		{
			quickblock[quickblock.length-1].increment(seq1, seq2, score);
		}
		//int j = sb.score;
	}
	//System.out.println(score);
	return score;
}
//Obsolete I think, used before only when RedoEverything
//
//public int pairwiseComparison(int seq1, int seq2)
//{
////	for(int i = 0; i < regions.size(); i++)
////	{
////		regions.get(i).increment(i, i, i);
////	}
//	Vector<Residue.ResidueType> main = this.get(seq1);
//	Vector<Residue.ResidueType> other = this.get(seq2);
//	int score = 0;
//	boolean gapOpen1 = false;
//	boolean gapOpen2 = false;
//	Residue.ResidueType thisres, otherres;
//	int startpos = 0;
//	int endpos = longestupperbound-1;
//	for(int i = startpos; i<=endpos; i++)
//	{
//		
//		thisres = main.get(i);
//		otherres = other.get(i);
//		if(thisres!=Residue.ResidueType.BLANK || otherres!=Residue.ResidueType.BLANK)
//		{
//		//check equality
//
//		
//		//Gap for this res
//		
//		if(thisres==Residue.ResidueType.BLANK)
//		{
//			gapOpen2 = false;
//			if(gapOpen1)
//			{
//				score+=Sequence.scoreGapExtension;
//			}
//			else
//			{
//				score+=Sequence.scoreGapOpen;
//				gapOpen1 = true;
//			}
//		}
//		else if(otherres==Residue.ResidueType.BLANK)
//		{
//			gapOpen1=false;
//			if(gapOpen2)
//			{
//				score+=Sequence.scoreGapExtension;
//			}
//			else
//			{
//				score+=Sequence.scoreGapOpen;
//				gapOpen2 = true;
//			}
//		}
//		else
//		{
//			gapOpen1 = false;
//			gapOpen2 = false;
//			score+=Residue.substitution[thisres.getInt()][otherres.getInt()];
//		}
//
//		}
////		newBlock[i].increment(seq1, seq2, score);
//		quickblock[i].increment(seq1,seq2,score);
//		//int j = sb.score;
//	}
//	//System.out.println(score);
//	return score;
//}


public class QueueWorker extends SwingWorker<Void, Void>
{

	@Override
	protected Void doInBackground() {
		Vector<ScoreWorker> workers = null;
		try {
			while(true)
			{
				if(isCancelled())
				{
					System.out.println("closing queueworker thread");
					if(workers!=null)
						for(int i = 0; i < workers.size();i++)
							workers.get(i).cancel(true);
					return null;
				}
				synchronized(QueueWorkerLock)
				{
				boolean timetowait = false;
				synchronized(scoreProcessingQueue)
				{
					if(scoreProcessingQueue.isEmpty())
					{
						if(Alignment.al.currentEdit.timertask==null)
						{
							upToDate.set(true);
							SwingUtilities.invokeLater(ScoringAlignment.this.new SetBusyIcon(false));
						}
						timetowait = true;
					}	
				}
				

				System.out.println("about to wait!");
				if(timetowait)
				{

						try {
							QueueWorkerLock.wait(); //isnt woken up if al.blockEdits = true;
							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							System.out.println("closing queueworker thread");
							if(workers!=null)
								for(int i = 0; i < workers.size();i++)
									workers.get(i).cancel(true);
							return null;
						}
					}
				}
				
				System.out.println("done waiting! gobble gobble");
				
				ActionSequence currentActions;


				
				synchronized(scoreProcessingQueue)
				{
					currentActions = scoreProcessingQueue.pop();
					lastAction = currentActions;
					
				}

				
				if(disable_scoring)
					continue;
				if(currentActions.editarr.size()>0)
				{
				if(currentActions.editarr.get(0) instanceof RedoEverythingAction)
				{
					currentActions.editarr.get(0).run(); //resets sticky blocks.
					System.out.println("redooing everything");
					score = BigInteger.ZERO;
					prepareQuickblock();
					computeFreshSumOfPairs(currentActions.node);

				}
				else
				{
					for(int i = 0; i < currentActions.editarr.size(); i++)
					{
						currentActions.editarr.get(i).run();
					}
					
//					if(!currentActions.node.scored)
					if(true)
					{
						computeEditSumOfPairs(currentActions);
						//Seq change worker
					}

					
				}
				synchronized(scoreComputeLock)
				{
					try {
						scoreComputeLock.wait();
					} catch (InterruptedException e) {
						System.out.println("QueueWorker thread cancelled");
						if(workers!=null)
							for(int i = 0; i < workers.size();i++)
								workers.get(i).cancel(true);
//						e.printStackTrace();
						return null;
						// TODO Auto-generated catch block	
					}
				}
//				ScoringAlignment.this.get(0).su
//				Vector<Residue.ResidueType> curr = new Vector<Residue.ResidueType>(ScoringAlignment.this.get(0).subList(90,135));
//				Alignment.al.debugTrace.add(curr);
				}
			}
		} 
		catch(InterruptedException e)
		{
			System.out.println("closing queueworker thread");
			if(workers!=null)
				for(int i = 0; i < workers.size();i++)
					workers.get(i).cancel(true);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			
		}
		return null;
		

//		return null;
	}
	
}
public class ScoreWorker extends SwingWorker<Void, Integer>
{
	
	Stack<SequencePairwise> st;
	int scorediff;
	boolean cancelled;
	UndoRedoTree.Node node;
	int startingSize;
	int count = 0;
	int oldprog = 0;
	public ScoreWorker(Stack<SequencePairwise> st, UndoRedoTree.Node node)
	{
		cancelled = false;
		this.st = st;
		startingSize = st.size();
		this.node = node;
		scorediff = 0;
	}
	
	@Override
	protected void process(List<Integer> progList)
	{
//		if(progLi)
		int diff=progList.get(progList.size()-1)-oldprog;
		currprog+=diff;
		oldprog= progList.get(progList.size()-1);
//		int prog = progList.get(progList.size()-1);
		synchronized(progressBarLock)
		{
//		System.out.println(""+(int) ((currprog)*100/(numOfPairs)));
		Alignment.al.panel.topPanel.scoreProgressBar.setValue((int) ((currprog)*100/(numOfPairs)));
		}
	}
	@Override
	protected Void doInBackground()  {
		Thread.currentThread().setPriority((Thread.MAX_PRIORITY+Thread.MIN_PRIORITY)/2);
		
		try{
		oldscore = score;
		synchronized (busy)
		{
			busy = true;
		}
		
		while(!st.isEmpty())
		{
//			Thread.sleep(15);
			SequencePairwise current = st.pop();
			long original = netscore[current.seq1][current.seq2];
			netscore[current.seq1][current.seq2] = pairwiseComparisonChange(current.seq1, (current.seq2));
			count++;
			if(current.seq1!=current.seq2)
			scorediff+=netscore[current.seq1][current.seq2]-original;
//			publish(0);
			if(this.isCancelled())
			{
				System.out.println("FreshScoreWorker thread cancelled");
				cancelled =true;
				return null;
			}
			if(count%progressUpdateInterval==0)
			{
				publish(count);
			}
			if(count%scoreFlushInterval==0)
			{
				
				synchronized(scoreEditingLock)
				{
					
					
					score = score.add(BigInteger.valueOf(scorediff));
					scorediff = 0;
//					scorediff = 0;
				}
//				System.out.println("currscore " + );
			}
		}
		int lollies = 10;
		int popscotch = 5;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			this.cancel(true);
		}

		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void done()
	{
		if(!cancelled)
		{
		synchronized (scoreEditingLock)
		{
//			score = score.add(BigInteger.valueOf(tot));
			score = score.add(BigInteger.valueOf(scorediff));
//			System.out.println("Thread completed! score " + tot);
		}
		int num = outstandingFreshWorkers.decrementAndGet();
		
		//If this is the last worker thread of the block
		if (num==0)
		{
			
			System.out.println("all done!");
			boolean last = false;
				
			String difftxt = "Change:";
			String c = "-";
			while(difftxt.length()+c.length()<labelwidth)
				difftxt+=" ";
			Alignment.al.panel.topPanel.diffLabel.setText(difftxt+c);
			
			for(int i = 0; i < ScoringAlignment.this.stickyblocks.size(); i++)
				ScoringAlignment.this.stickyblocks.get(i).addScores();
			
			


				
						// TODO Auto-generated method stub
			Alignment.al.netBlock.publishScore(score);
			synchronized(scoreProcessingQueue)
			{
				lastAction = null;
			}
					
				

			System.out.println("score:" + score  + " time:" + (new Date().getTime() - time));
			node.score = score;
			node.scored = true;
			synchronized (busy)
			{
				busy.notifyAll();
				busy = false;
				
			}
			synchronized (scoreComputeLock)
			{
				scoreComputeLock.notify();
			}
//			ScoreBlock.this.publishScore();		
			
		}
		else
		{
			System.out.println(num + " left");
		}
		
		}
	}
	
}








}


