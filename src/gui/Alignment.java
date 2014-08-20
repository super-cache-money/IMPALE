package gui;

//20232691 - OLD SCORE


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


public class Alignment extends ArrayList<Sequence>{
	
	

	/**
	 * 
	 */
	static Alignment al;

    RepeatingReleasedEventsFixer ubuntuReleasedFix; //installed in prepare(), uninstalled in disposeCurrent()
//	public Vector<Vector<ResidueType>> debugTrace; //MAKE SURE TO DELETE THIS SHIT. IT WILL CHOW MEMORY
	ActionSequence savedRunEverything;
	public Residue.ResidueType [] usedResiduesArr;
	public Set<Residue.ResidueType> usedResiduesSet;
	int totalSequencesBeingAligned = 0;
	public boolean blockEdits = false;
	public HashMap <Integer, Column> tempAlignSet;
	public ColumnThread ct;
	public boolean deleteResidues = false;
	public int wtfcounter = 0;
	public UndoRedoTree urt;
	public Set<ResiduePos> changed;
	public ScoringAlignment scoreal;
//	public Vector<Vector<Residue>> scoreBackup;
	private static final long serialVersionUID = 1L;
	public Vector<Boolean> columnIsSticky;
	public int longestSeq;
	public Object autoscrollLock;
	public boolean autoscrollrepaint = false;
	public int longestName;
	public int minStickySize = 20;
	public boolean busyUndo = false;
	public Vector<Column> columns;
	public boolean alignToProfile = true;
	public AbstractButton test;
    public Sequence referenceSequence = null;
	PaintingPanel panel;
	int oldStickyStart;
	int oldStickyEnd;
	Boolean stickiesSet;
	Object stickyLock;
	JTextArea helpText;
	public int editcores =2;
	public CustomScoreQueue scorequeue;
	//public int similarityThreshold = 5;
	//Vector<ScoreBlock> scoreBlocks;
	//matchRatio > 0.90 &&  gapRatio < 0.025
	double minStickyMatch = 0.9;
	double maxStickyGap = 0.02;
	StickyComputeTask sw;
	Vector<Integer> viewmap;
	Vector<Integer> inverseviewmap;

	ScoreBlock netBlock;
	boolean stickyMode;
//	UndoRedoStacks urs;
	EditStack currentEdit;
	boolean normalInsert;
	String longestSeqName;
	int format;
	static enum FileFormat
	{
		FASTA, PHYLIP4,NEXUS,CLUSTAL,UNKNOWN;
	}
	
	

	public void extend()
	{
		longestSeq++;
		for(int i = columnIsSticky.size(); i < longestSeq; i++)
		{
			columnIsSticky.add(columnIsSticky.get(columnIsSticky.size()-1));
		}
		
		for(int i = netBlock.blockVector.size(); i < longestSeq; i++)
		{
			netBlock.blockVector.add(netBlock.blockVector.get(netBlock.blockVector.size()-1));
		}


//		netBlock.
		panel.canvas.viewport.jsbh.setMaximum(longestSeq -panel.canvas.viewport.width+ 10);
		
	}
	public void resetSelection()
    {
        Alignment.al.panel.canvas.viewport.selected.clear();
        Alignment.al.changed.add(new ResiduePos(0,0));
        Alignment.al.panel.canvas.repaint();
    }
	public void retract()
	{

		longestSeq--;
//		columns.remove(columns.size()-1);
		panel.canvas.viewport.jsbh.setMaximum(longestSeq -panel.canvas.viewport.width+ 10);
		
	}

	
	public void shiftBlocksRight(int respos)
	{
		System.out.println("Shifting right: " +respos);
		extend();
		boolean last = this.columnIsSticky.get(respos);
		StickyBlock lastsb = netBlock.getRegion(respos);
		System.out.println("Adding ender:" +lastsb.endres+" at position " +respos);
		netBlock.blockVector.add(respos,lastsb);
		lastsb.endres++;
		for(int i = respos; i < this.longestSeq; i++ )
		{
			if(this.columnIsSticky.get(i)!=last)
			{
				boolean newlast = columnIsSticky.get(i);
				
				columnIsSticky.set(i,last);
				last=newlast;

				
			}
			else
			{
				
			}

		}
		boolean found = false;
		for(int i = 0; i < netBlock.regions.size();i++)
		{
			if(found)
			{
				netBlock.regions.get(i).startres++;
				netBlock.regions.get(i).endres++;
			}
			if(!found && lastsb.startres==netBlock.regions.get(i).startres)
			{
				found=true;
			}

		}
	}
	
	@Override
	public String toString()
	{
		String out = "";
		for(int i = 0; i <size();i++)
		{
			out+=this.getUnderlying(i).toString();
		}
		return out;
	}
	public void shiftBlocksLeft(int respos)
	{
		System.out.println("Shifting right: " +respos);
		retract();
		boolean last = this.columnIsSticky.get(respos);
		StickyBlock lastsb = netBlock.blockVector.get(respos);
		lastsb.endres--;
		for(int i = respos; i < longestSeq; i++ )
		{
			if(this.columnIsSticky.get(i)!=last)
			{
				last = columnIsSticky.get(i);
				lastsb = netBlock.blockVector.get(i);
				columnIsSticky.set(i-1,columnIsSticky.get(i));
				netBlock.blockVector.set(i-1, netBlock.blockVector.get(i));
				lastsb.startres--;
				lastsb.endres--;
			}
			else
			{
				
			}
    
		}
	}



	public HashMap<String, Object> getStateMap()
	//this is called while synchronizing to scoreProcessingQueue
	{
		HashMap<String, Object> map = new HashMap<String,Object>();
		map.put("urt", urt);
		map.put("chartPanel", panel.topPanel.scoreGraph);
		LinkedList<ActionSequence> sqcopy = new LinkedList<ActionSequence>();

		for(ActionSequence as : scoreal.scoreProcessingQueue)
		{
//			sqcopy.add(as);
			sqcopy.add(as);
			
		}
		
		if(scoreal.lastAction!=null)
//			if(!scoreal.lastAction.editarr.get(0) instanceof ScoringAlignment.RedoEverythingAction)
			sqcopy.add(0,scoreal.lastAction);
		map.put("scoreProcessingQueue", sqcopy);
		//we now write a list of edits to roll back in the ScoringAlignment, when the session is loaded.
//		Alignment.al.blockEdits = true;
//		for(int i = 0; i < sqcopy.size();i++)
//		{
//			ActionSequence currAct = sqcopy.get(i);
//			if(currAct.editarr.size()==1  && currAct.editarr.get(0) instanceof RedoEverythingAction)
//			{
//				//do nothing
//			}
//			else
//			{
//				
//			}
//		}
		
		map.put("oldMaxScore", Alignment.al.netBlock.oldMaxScore);
		map.put("minScore", Alignment.al.panel.topPanel.scoreGraph.minScore);
        map.put("referenceSequence", Alignment.al.referenceSequence);
		
		return map;
	}
	public int getBlockEnd(int respos)
	{
		boolean curr = this.columnIsSticky.get(respos);
		int out = 0;
		for(int i = respos;i < longestSeq && (columnIsSticky.get(i) ==curr) ; i++)
		{
			out = i;
		}
		return out;
	}
	
	public int getBlockStart(int respos)
	{
		boolean curr = this.columnIsSticky.get(respos);
		int out = 0;
		for(int i = respos; (i >=0 && columnIsSticky.get(i) ==curr) ; i--)
		{
			out = i;
		}
		return out;
	}
	
	public Vector<PairwiseInfo> reorderAccordingTo(int seq, int [][] scores)
	{
		PairwiseInfo.compareToSeq = seq;

		Vector<PairwiseInfo> similarities = new Vector<PairwiseInfo>();
		for(int i = 0; i < this.size(); i++)
		{
			if(i>seq)
				similarities.add(new PairwiseInfo(i, scores[seq][i]));
			else
				similarities.add(new PairwiseInfo(i, scores[i][seq]));
		}
			
		Collections.sort(similarities);
		
		for(int i = 0; i < this.size();i++)
		{
//			System.out.println((this.size()-1 -i ) + ":" + similarities.get(i).pairwiseScore);
			viewmap.set(this.size()-1 -i,similarities.get(i).seq);
			inverseviewmap.set(similarities.get(i).seq, this.size()-1 -i);
		}
		
		this.panel.canvas.viewport.jsbv.setValue(0);
		panel.headers.rh.repaint();
		return similarities;
	}
	
	public Sequence getUnderlying(int seq)
	{
		return super.get(seq);
	}
	
	@Override
	public Sequence get(int seq)
	{
		int index = viewmap.get(seq);
		return super.get(index);
	}
	public Alignment()
	{

		super();
//		debugTrace = new Vector<Vector<Residue.ResidueType>>();

        SimilarEngine.firstExtend =true;
        al = this;
		scorequeue = new CustomScoreQueue();
		urt = new UndoRedoTree();
		autoscrollLock = new Object();
//		scoreBackup = new Vector<Vector<Residue>>();
        Residue.buildAmbiguityMap();
		Residue.buildDNASubMatrix(Sequence.scoreMatch, Sequence.scoreTransition, Sequence.scoreTransversion);
		System.out.println(this.getClass().getResource("/BLOSUM62.txt").getPath());
//		Sequence.isProtein = true;
		boolean oldIsProtein = Sequence.isProtein;
		Sequence.isProtein = true;
		Residue.AAsubstitution = IO.readSubstitutionMatrix(this.getClass().getResourceAsStream("/BLOSUM62.txt"));
//		Sequence.isProtein = false;
		Sequence.isProtein = oldIsProtein;
		stickyMode = false;
		
		normalInsert = true;
		longestSeq = 0;
		columnIsSticky = new Vector<Boolean>();
		changed = Collections.synchronizedSet(new HashSet<ResiduePos>());
		columns = null;
		//minStickySize;
		stickiesSet = false;
		sw = null;
		currentEdit = new EditStack();
		
		stickyLock = new Object();
		 //sbm = new ScoreBlockManager(this);
		
//		 urs = new UndoRedoStacks(this);
		 longestSeqName="";
	}
	
	
	public void updateLongestSeq()
	{
		int max = 0;
		for (int i = 0; i < size(); i++)
		if (max < get(i).size())
			max = get(i).size();
	}
	
	public void updateLongestName()
	{
		int maxlen = 0;
		for(int i = 0; i < size(); i++)
			if(maxlen<get(i).name.length())
				maxlen = get(i).name.length();
	}
	
	@Override
	public boolean add(Sequence seq)
	{
		boolean bl = super.add(seq);
		if (seq.size() > longestSeq)
			longestSeq = seq.size();
		if(seq.name.length() > longestName)
			longestName = seq.name.length();
		return bl;
		
		
	}
	
	public void padBlanks()
	{
		//This method pads blanks onto the end of the sequences to make them all the same length
		for(int i = 0; i < size(); i++)
		{
			Sequence current = get(i);
			for(int j = 0; j < longestSeq - current.size(); j++)
			{
				current.add(new Residue('-'));   
			}
				
		}
		System.out.println("STATS-seqs=" + this.size() + "longestSeq=" + this.longestSeq);
	}
	
	public void updateConsensus()
	{
		
	}
	
	
	public void deleteResidueNormal(int seqpos, int respos)
	{
		//This method is a non-sticky deletion of the residue at seqpos, respos. 
		Sequence targetseq = get(seqpos);
		Residue.ResidueType restype = targetseq.get(respos).getType();
		targetseq.remove(respos);
		targetseq.add(new Residue('-'));
		boolean clearLastColumn = true;
//		for(int i = respos; i < longestSeq; i++)
//		{
//			changed.add(new ResiduePos(i,seqpos));
//		}
		
		changed.add(new ResiduePos(0,0));
//		if(column)
		for(int i = 0; (i < size())&&clearLastColumn; i++)
		{
			if(!get(i).get(longestSeq-1).isBlank())
			{
				clearLastColumn = false;
			}
		}
		
		if(clearLastColumn)
		{
//			for(int i = 0; i< size(); i++)
//			{
//				get(i).remove(longestSeq-1);
//	
//		}
			
			longestSeq--;
			Alignment.al.netBlock.blockVector.get(Alignment.al.netBlock.blockVector.size()-1).endres--;

//			columns.remove(columns.size()-1);
			panel.canvas.viewport.jsbh.setMaximum(panel.canvas.viewport.jsbh.getMaximum()-1);
		}
		
		currentEdit.add(new DeleteNormalEdit(viewmap.get(seqpos), respos, restype));
		synchronized(scorequeue)
		{
			scorequeue.add(new DeleteAction(viewmap.get(seqpos), respos));
		}
	}
	
	public Residue.ResidueType deleteResidueSticky(int seqpos, int respos, int blankInsertPos)
	{
		Sequence targetseq = this.get(seqpos);
		boolean everythingblank = true;
		//System.out.println(blockVector.);
		for(int i = respos; everythingblank && i <= netBlock.blockVector.get(respos).endres; i++)
		{
			if(!targetseq.get(i).isBlank())
				everythingblank = false;
		}
		if(everythingblank)
			return null;
		//blankInsertPos = -1 by default
		Residue.ResidueType type = targetseq.get(respos).getType();
		
		targetseq.remove(respos);
//		ScoreBlock block = sbm.getBlock(respos);
//
//			int blockPos = sbm.getBlockPos(respos);
		
		int start = getBlockStart(respos);
		int end = getBlockEnd(respos);
		if(blankInsertPos == -1 )
		targetseq.add(end,new Residue('-'));
		else
			targetseq.add(blankInsertPos, new Residue('-'));
		boolean empty = true;
		for(int i = 0; i < this.size(); i ++)
		{
			if(!this.get(i).get(end).isBlank())
				empty = false;
		}
		if(empty)
		{
			for(int i = 0; i < this.size(); i++)
			{
				this.get(i).removePlain(end);
			}
			columns.remove(end);
			shiftBlocksLeft(respos);
			
		}
		changed.add(new ResiduePos(0,0));
//		currentEdit.add(new DeleteStickyEdit(viewmap.get(seqpos), respos,  type, blankInsertPos));
		if(empty)
		{
			synchronized(scorequeue)
			{
			scorequeue.add(new DeleteAction(viewmap.get(seqpos), respos));
			currentEdit.add(new DeleteNormalEdit(viewmap.get(seqpos), respos, type));
//			currentEdit.add(new InsertNormalEdit(viewmap.get(seqpos), respos, r))
			if(blankInsertPos == -1 )
			{
				scorequeue.add(new InsertAction(viewmap.get(seqpos), Residue.ResidueType.BLANK, end));
				currentEdit.add(new InsertNormalEdit(viewmap.get(seqpos), end, new Residue(Residue.ResidueType.BLANK)));
			}
			else
			{
				scorequeue.add(new InsertAction(viewmap.get(seqpos), Residue.ResidueType.BLANK, blankInsertPos));
				currentEdit.add(new InsertNormalEdit(viewmap.get(seqpos), blankInsertPos, new Residue(Residue.ResidueType.BLANK)));
			}
			for(int i = 0; i < this.size(); i++)
			{
				scorequeue.add(new DeleteAction(viewmap.get(i), end));
				currentEdit.add(new DeleteNormalEdit(viewmap.get(i), end, Residue.ResidueType.BLANK));
			}
				scorequeue.add(new MoveBlocksAction(respos,false));
			}
		}
		else
		{
			synchronized(scorequeue)
			{
			scorequeue.add(new DeleteAction(viewmap.get(seqpos), respos));
			currentEdit.add(new DeleteNormalEdit(viewmap.get(seqpos), respos, type));
			if(blankInsertPos == -1 )
			{
				scorequeue.add(new InsertAction(viewmap.get(seqpos), Residue.ResidueType.BLANK, end));
				currentEdit.add(new InsertNormalEdit(viewmap.get(seqpos), end, new Residue(Residue.ResidueType.BLANK)));
			}
			else
			{
				scorequeue.add(new InsertAction(viewmap.get(seqpos), Residue.ResidueType.BLANK, blankInsertPos));
				currentEdit.add(new InsertNormalEdit(viewmap.get(seqpos), blankInsertPos, new Residue(Residue.ResidueType.BLANK)));

			}
			
			}
			
		}
		
		return type;
		
		
	}
	
	
	public void stripTrailingBlanks()
	{
		int index = longestSeq-1;
		while(true)
		{
			if(index<0)
				return;
			boolean allblank = true;
			for(int i = 0; i < size();i++)
			{
				if(!this.get(i).get(index).isBlank())
				{
					allblank=false;
					return;
				}
			}
			for(int i = 0; i < size(); i++)
			{
				this.get(i).remove(index);
			}
			al.longestSeq--;
			index--;
		}
	}
	public int insertResidueSticky(int seqpos, int respos, int blankDelPos, Residue r)
	{
		//blankInsertPos of -1 means delete the first blank residue found after insertion
		//blankInsertPos of -99 means delete the 
		int start = getBlockStart(respos);
		int end = getBlockEnd(respos);
		Sequence targetseq = this.get(seqpos);
		targetseq.add(respos, r);
//		ScoreBlock block = sbm.getBlock(respos);
		boolean deleted = false;
		int delpos = 0;
		if(blankDelPos!=-1 && blankDelPos !=-99)
		{
			targetseq.remove(blankDelPos);
			delpos = blankDelPos;
			deleted = true;
		}
		
		for(int i = end + 1; (i >= respos)&&(!deleted); i -- )
		{
			if(targetseq.get(i).isBlank())
			{
				targetseq.remove(i);
				delpos = i;
				
				deleted = true; //only serves to break out of loop.
			}
		}
		deleted = false;
		if(blankDelPos!=-99)
		for(int i = respos; i < delpos; i++)
		{
			if(!targetseq.get(i).isBlank())
			{
				deleted = true;
			}
		}
		
		
		if (deleted == false)
		{
		targetseq.add(respos, r);
		
//		columns.add(end+1,new Column());
//		columns.get(end+1).put(Residue.ResidueType.BLANK,  new MutableInt(this.size()-1));
//		columns.get(end+1).increment(this.get(seqpos).get(end+1).getType());
			for(int i = 0; i < this.size(); i++)
			{
				if(i!=seqpos)
				{
					this.get(i).addPlain(end+1,new Residue('-'));
				}
			}
			delpos = 0;
			
			shiftBlocksRight(respos);
		}
		 changed.add(new ResiduePos(0,0));
//		 currentEdit.add(new InsertStickyEdit( viewmap.get(seqpos), respos, delpos, r.getType()));
			synchronized (scorequeue)
			{
				if(deleted)
				{
					scorequeue.add(new InsertAction(viewmap.get(seqpos), r.getType(), respos));
					scorequeue.add(new DeleteAction(viewmap.get(seqpos),delpos));
					currentEdit.add(new InsertNormalEdit(viewmap.get(seqpos), respos, new Residue(r.getType())));
					currentEdit.add(new DeleteNormalEdit(viewmap.get(seqpos), delpos, r.getType()));
				}
				else
				{
					scorequeue.add(new InsertAction(viewmap.get(seqpos), r.getType(), respos));
					currentEdit.add(new InsertNormalEdit(viewmap.get(seqpos), respos, new Residue(r.getType())));
					for(int i =0; i < size();i++)
					{
						if(i!=viewmap.get(seqpos))
						{
							scorequeue.add(new InsertAction(i, r.getType(), end+1));
							currentEdit.add(new InsertNormalEdit(i, end+1, new Residue(r.getType())));
							
						}
					}
					scorequeue.add(new MoveBlocksAction(respos,true));
				}
			}
		return delpos;
		

	}
	
	public void insertResidueNormal(int seqpos, int respos, Residue r)
	{
		//This method should be used to insert a Residue under normal (i.e Non-sticky) AFTER the initial alignment's residues have been loaded in.
		//It will add/remove spaces at the of sequences as necessary
		Sequence targetseq = get(seqpos);
		targetseq.add(respos,r);
//		for(int i = respos; i <targetseq.size(); i ++)
//		{
//			changed.add(new ResiduePos(i, seqpos));
//		}
		changed.add(new ResiduePos(0,0));
		
		
		if(targetseq.size()<=longestSeq)
		{

			
		}
//		else if(targetseq.get(targetseq.size()-1).isBlank())
//		{
//			
//			targetseq.remove(targetseq.size()-1);
//		}
		else
		{

//			for(int i = 0; i < size(); i++)
//				if(i!=seqpos)
//			{
////				get(i).add(new Residue('-'));
//				changed.add(new ResiduePos(get(i).size()-1, i));
//			}
			changed.add(new ResiduePos(0,0));
			this.extend();
			Alignment.al.netBlock.blockVector.get(Alignment.al.netBlock.blockVector.size()-1).endres++;
//			columns.add(new Column());
//			columns.get(columns.size()-1).put(Residue.ResidueType.BLANK, new MutableInt(this.size()-1));
//			columns.get(columns.size()-1).increment(this.get(seqpos).get(longestSeq-1).getType());
			
		}
		
		
		currentEdit.add(new InsertNormalEdit(viewmap.get(seqpos), respos,  r));
		synchronized (scorequeue)
		{
		scorequeue.add(new InsertAction(viewmap.get(seqpos), r.getType(), respos));
		}
		
	}
	public void setStickyColumns(boolean recomputeScoresIfNeeded)
	{
		//ArrayList 
		urt.rushEditToQueue();

		
		sw = new StickyComputeTask(recomputeScoresIfNeeded);
		WorkDispatcher.submit(sw, sw.new StickyDoneTask(), "Sticky regions are being computed. These designate well-aligned regions of your alignment.");
//		sw.execute();

		
		
		
		
	}

//	public void protoSetStickyColumns(boolean recomputeScoresIfNeeded)
//	{
//		
//		urt.rushEditToQueue();
//		boolean recom = false;
//		if(sw!=null)
//		{
//			if((!sw.isDone()))
//		{
//				if(sw.recomputeEverything)
//				{
//					recom = true;
//				}
//				
//				sw.cancel(false);
//				
//				
//		}
//			else{
//				
//			}
//		}
//		else
//		{
//			//changed this...weird.
//			if(recomputeScoresIfNeeded)
//			{
//				recom = true;
//			}
//			
//		}
//		sw = new StickyWorker(recom);
//		sw.execute();

//		netBlock.generateRegionBlocks();
//		
//		
//	}
	
	class StickyComputeTask implements Runnable
	{
		
		int start, end;
		boolean cancelled;
		boolean recomputeEverything;
		boolean[] oldstickies;
		
		
		public StickyComputeTask(boolean recom)
		{
			
			cancelled = false;
		
			this.end = longestSeq-1;
			this.start = 0;
			recomputeEverything = recom;
			
			
			
			
		}
		@Override
		public void run() {
			try {
				System.out.println("STICKY done");

				// TODO Auto-generated method stub
				if(columns== null)
				{
					columns = new Vector<Column>();

					
				}
				
				if(columnIsSticky.size()>longestSeq)
				{
					for(int i = 0; i < columnIsSticky.size() - longestSeq; i ++)
					{
						columnIsSticky.remove(longestSeq + i);
					}
				}
				//stickyColumns.clear();
				if(stickiesSet==false)
				{
					columnIsSticky.clear();
					for(int i = start; i <=end; i++)
					{
						
							columnIsSticky.add(false);

					}
				}

//				System.out.println("stickiesset " + end);
				
				
				int matches = 0;
				int runningSticky = 0;
				//Set<Integer> oldstickies = new HashSet<Integer>();
				oldstickies = new boolean[longestSeq];
				for(int x = start; x <= end; x++)
				{
					oldstickies[x] = columnIsSticky.get(x);
					columnIsSticky.set(x, false);

					
					
					
					matches = 0;
					Column current = new Column();
					
					for(int y = 0; (y < Alignment.this.size()); y++)
					{
						Residue.ResidueType type = Alignment.this.get(y).get(x).getType();
						current.increment(type);
					}
					//current.check();
					if(x<columns.size())
					{
					columns.set(x, current);
					}
					else
					{
					columns.add(current);
					}
					//System.out.println(current);
					
					double matchRatio = (current.max + 0.0) / (Alignment.this.size() + 0.0);
					
					MutableInt numblanks = current.get(Residue.ResidueType.BLANK);
					if(numblanks==null)
						numblanks = new MutableInt(0);
					double gapRatio = (numblanks.value + 0.0) / (Alignment.this.size() + 0.0);
					
					if(matchRatio > minStickyMatch &&  gapRatio < maxStickyGap)
					{
						runningSticky ++;
					}
					else
					{
						if(runningSticky >= minStickySize)
						{
							for(int i = x-1; i >x-1-runningSticky; i--)
							{

								columnIsSticky.set(i, true);

							}
						}
						runningSticky = 0;
					}
				}
				if(runningSticky >= minStickySize)
				{
					for(int i = end; i >end-runningSticky; i--)
					{

						columnIsSticky.set(i, true);


					}
				}
				


				if(recomputeEverything==true && stickiesSet) //if stickies are set, and there is a change
				{
					recomputeEverything = false; 
				for(int i = 0; (i < longestSeq)&&recomputeEverything==false; i++)
				{
					if(columnIsSticky.get(i)!=oldstickies[i])
					{
						recomputeEverything = true;
					}
				}
				}
				
				if(recomputeEverything || scoreal.stickyblocks == null || scoreal.stickyblocks.size()==0) // the scoreal bit is only used when session loading.
				{
					System.out.println("Recomputing errthang");
					Vector<Integer> startblocks = new Vector<Integer>();
					Vector<Integer> endblocks = new Vector<Integer>();
					boolean last = columnIsSticky.get(0);
					startblocks.add(0);
					for(int i = 1; i < columnIsSticky.size(); i++)
					{
						if(columnIsSticky.get(i)!=last)
						{
							endblocks.add(i-1);
							startblocks.add(i);
							
						}
						
						last = columnIsSticky.get(i);
					}
					
					endblocks.add(columnIsSticky.size()-1);
					
					
//					Alignment.this.changed.add(new ResiduePos(0,0));
//					panel.topPanel.scoreLabel.setText("Recalculating score...");

					
					//we definitely dont want 2 consecutive redoeverythings
						if(!recomputeEverything) //this only triggers when session loading - we need to set sticky blocks, and transfer to scoreal, but not actually score
						{
							ArrayList<ScoreAction> tempal = new ArrayList<ScoreAction>();
							tempal.add(new RedoEverythingAction(startblocks, endblocks));
							savedRunEverything = new ActionSequence(tempal,scoreal.seqschangedset, null);
							
							
							savedRunEverything.editarr.get(0).run();
						}
						else if (scoreal.lastAction!=null && (scoreal.lastAction.editarr.get(0) instanceof RedoEverythingAction))
						{
							
						}
						else
						{
						synchronized(scorequeue)
						{
	
									scorequeue.add(new RedoEverythingAction(startblocks, endblocks));
									
								
						}
						synchronized (scoreal.scoreProcessingQueue)
						{
							scoreal.scoreProcessingQueue.add(new ActionSequence(scorequeue,scoreal.seqschangedset, urt.currentNode));
						}
						}
						
						
					
				}
				
				if(!stickiesSet)
				{
			stickiesSet = true;
				}
				netBlock.generateRegionBlocks();
				synchronized(Alignment.al.stickyLock)
				{
				Alignment.al.stickyLock.notify(); //when loading a session, this is waited for
				}
				

				

				
				
				
//				done();
				return ;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
			
		}
		
		class StickyDoneTask implements Runnable
		{
			
		public void run()
		{
				
				if(recomputeEverything)
				panel.topPanel.scoreLabel.setText("Recalculating score...");

						
					
					
					Alignment.this.changed.add(new ResiduePos(0,0));
					Alignment.this.panel.canvas.repaint();
				
			System.out.println("WORKER DONE");
			
			}
		}
			

		
		
		
		
	}
 
	
	
 public void prepare()
 {
     Residue.buildAmbiguityMap();
     Sequence.buildCodonTable();
	 viewmap = new Vector<Integer>();
	 inverseviewmap = new Vector<Integer>();
		for(int i = 0; i < this.size(); i++)
		{
			viewmap.add(i);
			inverseviewmap.add(i);
		}
		this.padBlanks();
		Residue.blank = new Residue('-');
		//sbm.buildFromScratch();
		netBlock = new ScoreBlock(0, this.longestSeq -1);
//		netBlock.computeSumOfPairs();
		
		
		scoreal =new ScoringAlignment();
		ct = new ColumnThread();
		ct.start();
//		this.setStickyColumns(); this needed to be moved elsewhere in RunEverything
		
		ubuntuReleasedFix = new RepeatingReleasedEventsFixer();
		ubuntuReleasedFix.install(); //uninstalled in static disposeCurrent method.

        IO.loadSettings();
     Residue.buildImageMaps();

 }
 
 public void alignSelectedRegion2(){return;}
 public void alignSelectedRegion()
 {
	 Runnable alignTask = new Runnable()
	 {

		@Override
		public void run() {
			tempAlignSet = new HashMap<Integer,Column>();
			System.out.println("ALIGNING!!");
			//startres = getResiduePosAtPoint(e.getPoint());
			int begin = 99999999;
			int end = -1 ;
			Set<Integer> seqset = new HashSet<Integer>();
			Iterator<ResiduePos> it = panel.canvas.viewport.selected.iterator();
			while(it.hasNext())
			{
				ResiduePos current = it.next();
				seqset.add(current.location[1]);
				if(current.location[0]<begin)
					begin = current.location[0];
				if(current.location[0]>end)
					end = current.location[0];
			}
			if(begin==end)
			{
				StickyBlock sb = netBlock.getRegion(begin);
				begin = sb.startres; 
				end = sb.endres;
			}
			Iterator<Integer> seqit = seqset.iterator();
//			int preblanks = 0;
//			int postblanks = 0;
//			while(seqit.hasNext())
//			{
//				int currentseq = seqit.next();
//				
//			}
			totalSequencesBeingAligned = seqset.size();
			Iterator<Integer> it2 = seqset.iterator();
			ArrayList<Column> profile = Sequence.getConsensusProfile(begin, end);
			Sequence cons = Sequence.getConsensus(profile);
			int progress = 0;
			while(it2.hasNext())
			{
				
				final int currprogress = progress;
				SwingUtilities.invokeLater(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						helpText.setText("IMPALE is busy snap-aligning the selected region.\nCurrently aligning sequence "+currprogress+" of "+totalSequencesBeingAligned +".");
					}});
				
				int nextint = it2.next();
				Sequence seq = Alignment.al.get(nextint);

//				 starti = end+1;

				if(alignToProfile)
					seq.alignToProfile(begin, end, profile);
				else
					seq.align(begin, end, cons);
				int usethis = viewmap.get(nextint);
				netBlock.seqChange(usethis);
				progress++;
				
				
			}
		}
		 
	 };
	 
		
		
		
		
		
		//System.out.println("BLOCK!!! " + current.startres + " " + current.endres);
	Runnable guiUpdateTask = new Runnable()
	{
		@Override
		public void run()
		{
		changed.add(new ResiduePos(0,0));
//		SwingUtilities.invokeLater
		panel.canvas.repaint();
		currentEdit.pushToTreeAndScore();
		if(!busyUndo)
			urt.redoNodes.clear();
		helpText.setText("You have aligned the selected region to the consensus. To align an entire region of a sequence, only select a single residue within it. Shortcut: Hold \"A\" and drag-select." );
		}
		
//		
		
	};

		helpText.setText("");
	WorkDispatcher.submit(alignTask, guiUpdateTask, "The optimal alignment is being computed. Please wait...");
		
	
 }

public void toggleSticky() {
//	int test = 0;
//	for(int i = 0; i < this.size(); i++)
//	{
//		for(int j = i+1; j<this.size(); j++)
//		{
//			test+=sbm.netBlock.scores[i][j];
//		}
//	}
//	
//	System.out.println("OTHERTOT " + test);

	
	if(stickyMode==true)
	{
		this.setStickyColumns(true);
		stickyMode = false;
		helpText.setText("You have disabled Sticky Mode. Edits will now proceed as per normal. Press SPACEBAR to toggle Sticky Mode.");
		
	}
	else
	{
		//sbm.buildFromScratch();
		helpText.setText("You have enabled Sticky Mode. Edits made between boldtext regions (poorly conserved areas) will not upset the rest of the alignment. Press SPACEBAR to toggle Sticky Mode.");
		this.setStickyColumns(true);
		stickyMode = true;
	}
	
	
	// TODO Auto-generated method stub
	
}
//------------------------------------------
//Symbol       Meaning      Nucleic Acid
//------------------------------------------
//A            A           Adenine
//C            C           Cytosine
//G            G           Guanine
//T            T           Thymine
//U            U           Uracil
//M          A or C
//R          A or G
//W          A or T
//S          C or G
//Y          C or T
//K          G or T
//V        A or C or G
//H        A or C or T
//D        A or G or T
//B        C or G or T
//X      G or A or T or C
//N      G or A or T or C

 
	
//	public boolean isSticky(QModelIndex index)
//	{
//		if(stickyColumns.contains(index.column()))
//			return true;
//		return false;
//	}
	
	public boolean shiftresright(int seqpos, int respos, int protectpos)
	{
	
		
//		seqpos = this.viewmap.get(seqpos);

				
				int end = 0;
				Residue r = new Residue(Residue.ResidueType.BLANK);
				if(stickyMode)
					end = getBlockEnd(respos);
				else
					end = longestSeq-1;
				Sequence targetseq = this.get(seqpos);
				targetseq.add(respos, r);
//				ScoreBlock block = sbm.getBlock(respos);
				boolean deleted = false;
				int delpos = 0;
				
				for(int i = protectpos+2; (i <= end+1)&&(!deleted); i ++ )
				{
					if(targetseq.get(i).isBlank())
					{
						targetseq.remove(i);
						delpos = i;
						
						deleted = true; //only serves to break out of loop.
					}
				}
//				deleted = false;
//				for(int i = respos; i < delpos; i++)
//				{
//					if(!targetseq.get(i).isBlank())
//					{
//						deleted = true;
//					}
//				}
				
				
				if (deleted == false)
				{
//				targetseq.add(respos, r);
				
				columns.add(end+1,new Column());
				columns.get(end+1).put(Residue.ResidueType.BLANK,new MutableInt(this.size()-1));
				columns.get(end+1).increment(this.get(seqpos).get(end+1).getType());
					for(int i = 0; i < this.size(); i++)
					{
						if(i!=seqpos)
						{
							this.get(i).addPlain(end+1,new Residue('-'));
						}
					}
					delpos = 0;
					
					shiftBlocksRight(respos);
				}
				 changed.add(new ResiduePos(0,0));
				 if(stickyMode)
					 currentEdit.add(new InsertStickyEdit( viewmap.get(seqpos), respos, delpos, r.getType()));
				 else
				 {
					 currentEdit.add(new InsertNormalEdit(viewmap.get(seqpos),respos, r));
					 if(deleted)
					 {
						 currentEdit.add(new DeleteNormalEdit(viewmap.get(seqpos),delpos, Residue.ResidueType.BLANK));
					 }
				 }
				 synchronized (scorequeue)
					{
						if(deleted)
						{
							scorequeue.add(new InsertAction(viewmap.get(seqpos), r.getType(), respos));
							scorequeue.add(new DeleteAction(viewmap.get(seqpos),delpos));
							
						}
						else
						{
							scorequeue.add(new InsertAction(viewmap.get(seqpos), r.getType(), respos));
							for(int i =0; i < size();i++)
							{
								if(i!=viewmap.get(seqpos))
								{
									scorequeue.add(new InsertAction(i, r.getType(), end+1));
									
								}
							}
							scorequeue.add(new MoveBlocksAction(respos,true));
						}
					}
				
				
			return true;
		}
	
	public boolean shiftresleft(int seqpos, int respos, int protectpos)
	{

		Sequence targetseq = this.get(seqpos);
		boolean everythingblank = true;
		//System.out.println(blockVector.);
		int end = -1;
		if(stickyMode)
			end = netBlock.blockVector.get(respos).endres;
		else
			end = longestSeq;
		
		for(int i = respos; everythingblank && i <= end; i++)
		{
			if(!targetseq.get(i).isBlank())
				everythingblank = false;
		}
		if(everythingblank)
			return false;

		
		//blankInsertPos = -1 by default
		if(wtfcounter > 0)
			wtfcounter = wtfcounter;
		wtfcounter++;

		Residue.ResidueType type = targetseq.get(respos).getType();
		
		
//		ScoreBlock block = sbm.getBlock(respos);
//
//			int blockPos = sbm.getBlockPos(respos);
		int start = 0;
		if(stickyMode)
		{
			start = getBlockStart(respos);
		}
		boolean blankfound = false;
		int blankDelPos =-1;
		for(int i = respos-1; !blankfound && i >= start; i--)
		{
			if(targetseq.get(i).isBlank())
			{
				blankfound = true;
				blankDelPos = i;
			}
			
		}
		
		if(blankfound==false)
			return false;

		targetseq.remove(blankDelPos);
		targetseq.add(protectpos, new Residue('-'));
		
//
		boolean empty = true;
		if(stickyMode)
		{
		for(int i = 0; i < this.size(); i ++)
		{
			if(!this.get(i).get(end).isBlank())
				empty = false;
		}
		}
		else
			empty=false;
		if(empty)
		{
			for(int i = 0; i < this.size(); i++)
			{
				this.get(i).removePlain(end);
			}
			columns.remove(end);
			shiftBlocksLeft(respos);
			
		}
		changed.add(new ResiduePos(0,0));
		if(stickyMode)
		{
			if(!empty)
				currentEdit.add(new DeleteStickyEdit(viewmap.get(seqpos), blankDelPos,  Residue.ResidueType.BLANK, protectpos+1));
			else
				currentEdit.add(new DeleteStickyEdit(viewmap.get(seqpos), blankDelPos,  Residue.ResidueType.BLANK, -99));

		}
		else
		{
			currentEdit.add(new DeleteNormalEdit(viewmap.get(seqpos), blankDelPos, Residue.ResidueType.BLANK));
			currentEdit.add(new InsertNormalEdit(viewmap.get(seqpos),protectpos, new Residue(Residue.ResidueType.BLANK)));
		}
		//		if(empty)
//		{
//			synchronized(scorequeue)
//			{
//			scorequeue.add(scoreal.new DeleteAction(viewmap.get(seqpos), respos));
//			if(blankInsertPos == -1 )
//				scorequeue.add(scoreal.new InsertAction(viewmap.get(seqpos), Residue.ResidueType.BLANK, end));
//			else
//				scorequeue.add(scoreal.new InsertAction(viewmap.get(seqpos), Residue.ResidueType.BLANK, blankInsertPos));
//			for(int i = 0; i < this.size(); i++)
//			{
//				scorequeue.add(scoreal.new DeleteAction(viewmap.get(i), end));
//			}
//				scorequeue.add(scoreal.new MoveBlocksAction(respos,false));
//			}
//		}
//		else
//		{
			synchronized(scorequeue)
			{
			scorequeue.add(new DeleteAction(viewmap.get(seqpos), blankDelPos));
			scorequeue.add(new InsertAction(viewmap.get(seqpos), Residue.ResidueType.BLANK, protectpos));
			if(empty)
			{
				for(int i = 0; i < this.size(); i++)
				{
					scorequeue.add(new DeleteAction(viewmap.get(i), end));
				}
				scorequeue.add(new MoveBlocksAction(respos,false));
			}
//			if(blankInsertPos == -1 )
//				scorequeue.add(scoreal.new InsertAction(viewmap.get(seqpos), Residue.ResidueType.BLANK, end));
//			else
//				scorequeue.add(scoreal.new InsertAction(viewmap.get(seqpos), Residue.ResidueType.BLANK, blankInsertPos));
//			
			}
			
//		}
		
		return true;
	}
		
	
	public void closeAllThreads()
	{
		scoreal.qw.cancel(true);
		if(this.sw!=null)
		
		ct.interrupt();
//		SwingUtilites
//		scoreal
	}
	
	public static void disposeCurrent()
	{
        IO.saveSettings();
		Alignment.al.ubuntuReleasedFix.remove();
		RunEverything.currentRe.al = null;
		RunEverything.currentRe.jf.dispose();
//		RunEverything.currentRe.jf.
		RunEverything.currentRe.jf = null;
		RunEverything.currentRe.pp = null;
		Alignment.al.panel.superframe.dispose();
		Alignment.al.closeAllThreads();
		Alignment.al = null;
		
//		menu.al = null;
//		closeAllThreads();
//		ct.interrupt();
	}
	
}
