package gui;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.text.html.HTMLDocument.Iterator;
//not really a block, stores the distance matrices of everything
public class ScoreBlock{
	
	
	int startres, endres;
	long lastScore;
	boolean firstpublish = true;
	AtomicInteger completedWorkers;
	Object seqChangeLock;
//	Alignment al;
	
	int [] [] scores;
	boolean [] [] gapModifier;
	Vector <StickyBlock> regions;
	//stores whether a gap has been exteneded from this scoreblock to the one on its left. [i][j] -> i has extended gap when compared with j
	Long score;
	long time;
	
	public Boolean seqChange;
	public int seqChangeCurrent;
	public boolean sticky;
	public Boolean busy;
	BigInteger oldMaxScore;
//	StickyScoreWorker scw;
	Set<Integer> stickySeqsChanged;
	int labelwidth = 25;
	BigInteger oldscore = BigInteger.valueOf(-99999999);
	long startTime;
	StickyBlock[] newBlock;
	Vector<StickyBlock>blockVector;
	ScoreBlock(int startres, int endres)
	{
		oldMaxScore = BigInteger.valueOf(-999999999);
		gapModifier = new boolean [Alignment.al.size()][Alignment.al.size()];
		scores = new int [Alignment.al.size()][Alignment.al.size()];
		score = new Long(0);
//		this.al = al;
		this.startres = startres;
		this.endres = endres;
		
		seqChange = false;
	
		stickySeqsChanged = Collections.synchronizedSet(new HashSet<Integer>());
		busy = false;
		regions = new Vector<StickyBlock>();
		blockVector = new Vector<StickyBlock>();
		seqChangeLock = new Object();
	}
	
	public void set(int i, int j, int val)
	{
		int temp;
		if(j<i)
		{
			temp = i;
			i = j;
			j = temp;
		}
		this.scores[i][j] = val;
	}
	

	public void extend(int num)
	{
		endres +=num;
	}
	
	/*public void seqChange2(int underlyingseq)
	{
		synchronized(stickySeqsChanged)
		{
			stickySeqsChanged.add(underlyingseq);
		}
		if(scw!=null && !scw.isDone())
		{
			
			synchronized(stickySeqsChanged)
			{
			if(this.seqChangeCurrent==underlyingseq)
			{
				scw.cancel(false);
				while(!scw.isDone())
				{
					System.out.println("WAITING!!!FFS");
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				stickySeqsChanged.add(underlyingseq);
				scw = new StickyScoreWorker(stickySeqsChanged);
				scw.execute();
				
			}
			else
			{
				stickySeqsChanged.add(underlyingseq);
			}
				
				
			}
		}
		else
		{
			
			
		scw = new StickyScoreWorker(stickySeqsChanged);
		scw.execute();
		}
		

	}*/
	
	public void seqChange(int underlyingseq)
	{
		Alignment.al.scoreal.seqschangedset.add(underlyingseq);
//		stickySeqsChanged.add(underlyingseq);
//		if(scw ==null)
//		{
//			scw = new StickyScoreWorker(stickySeqsChanged);
//			scw.execute();
//		}
//		
//		synchronized(seqChangeLock)
//		{
//			seqChangeLock.notify();
//		}
		
	}
	
	public void publishScore(BigInteger score)
	{
		int curr = Alignment.al.scoreal.pacmanCurrentUpdate.decrementAndGet();
		if(curr==0)
		Alignment.al.panel.topPanel.scoreProgressBar.setVisible(false);
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				if(Alignment.al.panel.topPanel.editsInQLabel!=null)
				Alignment.al.panel.topPanel.editsInQLabel.setText((Alignment.al.scoreal.pacmanCurrentUpdate.get())+"");
				// TODO Auto-generated method stub
				
			}
			
		});
		if(Alignment.al.panel.topPanel.scoreGraph.startingScore==null)
			Alignment.al.panel.topPanel.scoreGraph.startingScore = score;
		String scoretxt = "Score: ";
		String sc = score +"";
		
		while(scoretxt.length() + sc.length()<labelwidth)
			scoretxt+=" ";
		Alignment.al.panel.topPanel.scoreLabel.setText(scoretxt+sc);
		
		
		if(score.compareTo(oldMaxScore)==1||firstpublish)
		
		{
			if(!firstpublish)
			{
			Alignment.al.helpText.setText("You have set a new alignment high score, exceeding the previous best by " + (score.subtract(oldMaxScore).toString()) + ". Note how the max score has turned red to indicate this.");
			
			//Alignment.al.panel.topPanel.chartPanel.minScore = (score+0.0)*0.90;
			}
			else
			{
				BigDecimal tmp = new BigDecimal(score);
//				tmp = tmp.multiply(new BigDecimal(myDouble));
				Alignment.al.panel.topPanel.scoreGraph.minScore = tmp.subtract(new BigDecimal(score.abs()).multiply(new BigDecimal(0.0001))).toBigInteger();
			}
			
			String maxtxt = "Max:   ";
			String c = score +"";
			while(maxtxt.length() + c.length()<labelwidth)
				maxtxt+=" ";
			oldMaxScore = score;
			Alignment.al.panel.topPanel.maxLabel.setForeground(Color.RED);
			Alignment.al.panel.topPanel.maxLabel.setText(maxtxt + c);
			

		}
		else
		{
			Alignment.al.panel.topPanel.maxLabel.setForeground(Color.BLACK);
		}
		
		if(!firstpublish)
		{
		String difftxt = "Diff:   ";
		String c = ""+(score.subtract(oldscore));
		while(difftxt.length()+c.length()<labelwidth)
			difftxt+=" ";
		Alignment.al.panel.topPanel.diffLabel.setText(difftxt+c);
		}
		firstpublish  = false;
		
		
		Alignment.al.panel.topPanel.scoreGraph.addScore(score);
		
		System.out.println("Min " + Alignment.al.panel.topPanel.scoreGraph.minScore + " " + score);
		oldscore = score;
	}
	
	public void generateRegionBlocks() 
	{
		
		newBlock = new StickyBlock [Alignment.al.longestSeq];
		blockVector.clear();
		//vec = new Vector<StickyBlock>();
//		Alignment.al.setStickyColumns();

		regions.clear();
		boolean last = Alignment.al.columnIsSticky.get(0); 

		

		StickyBlock sb = new StickyBlock(0,0);
		regions.add(sb);
		blockVector.add(sb);
		newBlock[0] = sb;
		for(int i = 1; i < Alignment.al.longestSeq; i++)
		{
			
			if(Alignment.al.columnIsSticky.get(i)==last)
			{
				sb.endres++;
				
			}
			else
			{
				sb = new StickyBlock(i,i);
				regions.add(sb);
				//newBlock[i] = false;
				last = Alignment.al.columnIsSticky.get(i);
			}
			newBlock[i] = sb;
			blockVector.add(sb);
			
		}
		

		
		
		
		//System.out.println("stickies " + )
	}
	
	public StickyBlock getRegion(int res)
	{
		for(int i = 0; i < regions.size(); i++)
		{
			if(regions.get(i).endres>=res)
				return regions.get(i);
				
				
		}
		return null;
	}
	

	
	
	/*public int pairwiseComparisonSticky(int seq1, int seq2)
	{
//		for(int i = 0; i < regions.size(); i++)
//		{
//			regions.get(i).increment(i, i, i); WTF REALLY NOW
//		}
		int score = 0;
		boolean gapOpen1 = false;
		boolean gapOpen2 = false;
		Sequence main = Alignment.al.getUnderlying(seq1);
		Sequence other = Alignment.al.getUnderlying(seq2);
		Residue.ResidueType thisres, otherres;
		int startpos = 0;
		int endpos = Alignment.al.longestSeq-1;
		//Clear out old stickyblock scores
		if(seq1>seq2)
		for(int i = 0; i < regions.size(); i++)
		{
			regions.get(i).scores[seq2][seq1] = 0;
		}
		else
		for(int i = 0; i < regions.size(); i++)
		{
			regions.get(i).scores[seq1][seq2] = 0;
		}
		
		
		
		for(int i = startpos; i<=endpos; i++)
		{
			
			thisres = main.get(i).getType();
			otherres = other.get(i).getType();
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
				score+=Residue.substitution[thisres.getInt()][otherres.getInt()];
			}

			}
			blockVector.get(i).increment(seq1, seq2, score);
			//int j = sb.score;
		}
		//System.out.println(score);
		return score;
	}*/

	/*public class StickyScoreWorker extends SwingWorker<Void, Void>
	{
		Set<Integer> seqs;
		int [] tempchanges;
		boolean cancelled = false;
		public StickyScoreWorker(Set<Integer> input)
		{
			seqs = input;
			tempchanges = new int [Alignment.al.size()];
		}
		@Override
		protected Void doInBackground()  {
			oldscore = score;
			try
			{
			while(true)
			{
				if(seqs.isEmpty())
				{
					ScoreBlock.this.publishScore();
					synchronized(seqChangeLock)
					{
						seqChangeLock.wait();
						
					}
				}
				System.out.println("LOLIES");
				int current = 0;
				int netScoreDiff = 0;
				boolean wait = false;
				
				synchronized (busy)
				{
					
					if(busy==true)
					{
						System.out.println("waiting");
						try {
							busy.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("resuming");
					}
						
				}
				
				
					
				synchronized (seqs)
				{
					java.util.Iterator<Integer> it = seqs.iterator();
					current = it.next();
					seqChangeCurrent = current; 

						seqs.remove(current);

				}
				int oldScore, diffScore, newScore;
				boolean cont = true;
				for(int i = 0; cont&&(i < Alignment.al.size()); i++)
				{
					if(seqs.contains(current))
					{
						cont = false;
						continue;
					}
					
					newScore = pairwiseComparisonSticky(i, current);
					oldScore = ScoreBlock.this.get(i,current);
					diffScore = newScore-oldScore;
			
					tempchanges[i] = diffScore;
					//ScoreBlock.this.setScore(i, current, newScore);
					if(i!=current)
					{
						netScoreDiff+=diffScore;
					}
					

						
					
					
					
				}
				if(cont){
				ScoreBlock.this.score+=netScoreDiff;

				for(int i = 0; i < Alignment.al.size(); i ++)
				{
					ScoreBlock.this.increment(i, current, tempchanges[i]);

				}
				

					
				
				}
				
			}
			
		}
		catch(Exception e)
		{	
			e.printStackTrace();
		}
			
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public void done()
		{

			JOptionPane.showMessageDialog(null, "LOLFUCKED");
			
				
		}
		
	}

	@Override
	public int get(int underlying1, int underlying2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void increment(int underlying1, int underlying2, int inc) {
		// TODO Auto-generated method stub
		
	}*/
	
	
	




}


