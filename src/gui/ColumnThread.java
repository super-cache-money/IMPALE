package gui;

import gui.ScoringAlignment;
import gui.SequencePairwise;

import gui.ScoringAlignment.SetBusyIcon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

//This is how this works: first, a column order is generated. Residues are counted in bundles, with each bundle counted like a bead is dropped in four-in-a-row. 
//The order of bundle dropping is determined by generateColumnOrder(), based on some random pretty formula. There are min(100,al.size()) bundles per row. 
//The first al.size()%100 bundles have an extra size of 1, to accomodate the remainder. The paints occur a fixed amount of times, irrespective of the number of cols displayable in the window.
//Everytime the window is resized, the column order needs to be regenerated. 


public class ColumnThread extends Thread
{

	private final ScheduledExecutorService scheduler =
		     Executors.newScheduledThreadPool(1);
	ScheduledFuture currentColDelayedTask;
	boolean schedulerWaiting = false;
	boolean stopPainting = false;
	Object schedulerLock = new Object();
	int numAniPaints = 30; //paints this many times
	int desiredAnimationTime = 500;
	int num_percentiles; // min(100,al.size())
	int num_percentiles_sq;
	int stalledDelayMs = 100;
	AtomicBoolean delayCalibration;
	int zeroAnimationTime = -1;
	int framewaitms = 7;
	int currentseed = 0;
	int lastWidth = 0;
	 int [] animationOrder = null;
	 AtomicBoolean colChanged;
	 AtomicBoolean busy;
	 AtomicInteger seed;
	 Object colSleeper = new Object();
	 Object paintLock = new Object();
	 Object aniCompLock = new Object();
	 int [][] currentCountUpdate = null;
	 Residue.ResidueType [] consRes = null;
	 AtomicBoolean completed;
    boolean editSinceCountdown = false;
	//Alignment al;
	AtomicInteger startCompute = null;
	AtomicInteger endCompute = null;
	
	public ColumnThread()
	{
		
		colChanged = new AtomicBoolean(false);
		busy = new AtomicBoolean(true);
		completed = new AtomicBoolean(false);
		startCompute = new AtomicInteger(0);
		endCompute = new AtomicInteger(0);
		delayCalibration = new AtomicBoolean(false);
		seed = new AtomicInteger(0);
	}
	@Override
	public void run() {
		foreverloop:
		while(true)
		{
			synchronized(colSleeper)
			{
			if(colChanged.get())
			{
				
			}
			else
			{
				try {
					colSleeper.wait();
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			}
			long starttime = System.currentTimeMillis();
			completed.set(false); 
			int start = startCompute.get();
			int end = endCompute.get();
			if(Alignment.al.panel==null)
				return;
			PaintingViewport vp = Alignment.al.panel.canvas.viewport;
//			ArrayList<EnumMap<Residue.ResidueType, MutableInt>> colcounts = new ArrayList<EnumMap<Residue.ResidueType, MutableInt>>();
			EnumMap<Residue.ResidueType, MutableInt> [] colcounts = new EnumMap [end-start+1];
			 consRes = new Residue.ResidueType [end-start+1];
			 int [] consCount = new int [end-start+1];
			
			for(int i = 0; i <= end-start; i++)
			{
				colcounts[i]=new EnumMap<Residue.ResidueType,MutableInt>(Residue.ResidueType.class);
				
//				colcounts.add();
			}
			
			
			colChanged.set(false);
			
				for(int col = 0; col <= end - start; col++)
				for(int i = 0; i < Alignment.al.usedResiduesArr.length; i ++)
				{
					colcounts[col].put(Alignment.al.usedResiduesArr[i], new MutableInt(0));

				}

				
				

			synchronized (aniCompLock) 
			{
				int [] currPercentile = new int[end-start+1];
				if(animationOrder==null)
				{
					try {
						aniCompLock.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue foreverloop;
				}
				
				int paintfreq = (animationOrder.length)/numAniPaints;
				if(paintfreq==0)
					return;
				int extraOneIndex = Alignment.al.size()%num_percentiles;
				int eachcount = Alignment.al.size()/num_percentiles;
//				boolean isOdd = (end-start+1)%2==1;
				int seedOffsetStart = end-start-seed.get();
				int seedOffsetEnd = seedOffsetStart+end-start;
				int aniIndexActual = 0;
				maincolloop:
				for (int aniIndex = 0; aniIndex < animationOrder.length; aniIndex++) 
				{
					
					if (colChanged.get()) {
						synchronized (paintLock) {
							currentCountUpdate = null;
						}
						continue foreverloop;

					}
					
					if(Thread.interrupted())
					{
						System.out.println("closing column thread");
						return;
					}
					
					
					int putativeI = animationOrder[aniIndex];
					if(putativeI<seedOffsetStart || putativeI>seedOffsetEnd)
						continue maincolloop;
					
					int i = putativeI-seedOffsetStart;	
					int percentile = currPercentile[i];
					currPercentile[i]++;
					int from = percentile*eachcount+Math.min(percentile, extraOneIndex);
					int to = from+eachcount;

					if( percentile < extraOneIndex)
						to++;
					
						
//					int coliters = (currcol*2+start==end&&isOdd)? 1:2;
					for(int seq = from; seq < to; seq++)
//						for(int i=currcol*2; i < currcol*2+coliters;i++)
					{
							if(i>=end-start+1)
							{
								completed.set(true);
								continue foreverloop;
							}
					Residue.ResidueType currentres = Alignment.al.get(seq).get(vp.startres + i).getType();
					MutableInt currcount = colcounts[i].get(currentres);
					currcount.value++;
					if (consCount[i] <= currcount.value) {
						consCount[i] = currcount.value;
						consRes[i] = currentres;
					}
					
					if(aniIndexActual%paintfreq==0)
					{
						try {
						if(!delayCalibration.get())
							Thread.sleep(framewaitms);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						synchronized(paintLock)
						{
							if(stopPainting)
							{
								completed.set(true);
								continue foreverloop;
							}
						}
						pushColourUpdate(colcounts,false);
					}
					}
					aniIndexActual++;
				}
				
				
				int timeTaken = (int) (System.currentTimeMillis()-starttime);
				
				if(delayCalibration.get())
				{
					delayCalibration.set(false);
					int diff = timeTaken-desiredAnimationTime;
					framewaitms-=diff/numAniPaints/10;
					if(framewaitms<0)
						framewaitms = 0;
				}
				else
				{
				int diff = timeTaken-desiredAnimationTime;
				if(diff>100)
				{
					if(framewaitms>0)
						framewaitms--;
					if(diff>200)
					{
//						int diff2 = timeTaken-desiredAnimationTime;
						framewaitms-=diff/numAniPaints/2;
					}
				}

				else if(diff<-100)
				{
					framewaitms++;
				}
				}
				
				if(framewaitms<0)
					framewaitms=0;
					
				System.out.println("time for ani " + (timeTaken) +" , " + framewaitms +"ms");
				
		}
				

				

//				
				
			
				
				if(currentCountUpdate!=null)
				{
					pushColourUpdate(colcounts,true);

				}
			
			}
		// TODO Auto-generated method stub
		
	}
	
	public void pushColourUpdate(EnumMap<Residue.ResidueType,MutableInt> [] colcounts, boolean doConsensus)
	{
		synchronized(paintLock)
		{
			currentCountUpdate = new int [colcounts.length][Alignment.al.usedResiduesArr.length];
			for(int i = 0; i < colcounts.length; i++)
				for(int j = 0; j < Alignment.al.usedResiduesArr.length; j++)
			{
				currentCountUpdate[i][j]=colcounts[i].get(Alignment.al.usedResiduesArr[j]).get();
				
			}
			
			if(doConsensus)
			{
				completed.set(true);
			}
		}
		
		SwingUtilities.invokeLater(new Runnable(){public void run() {Alignment.al.panel.headers.tch.repaint();}});
		
	}

    public void stalledUpdate(final int seed)
    {

        synchronized (schedulerLock)
        {
            synchronized(Alignment.al.ct.paintLock)
            {
                stopPainting = true;
                Alignment.al.ct.currentCountUpdate=null;

            }
            Alignment.al.panel.headers.tch.repaint();

            if(currentColDelayedTask==null ||currentColDelayedTask.isDone())
            {
                editSinceCountdown = false;
                Runnable currentTask = new Runnable() {
                    @Override
                    public void run() {
                        synchronized (schedulerLock) {
                            if(editSinceCountdown)
                            {
                                editSinceCountdown=false;
                                synchronized(Alignment.al.ct.paintLock)
                                {
                                    stopPainting = true;
                                    Alignment.al.ct.currentCountUpdate=null;

                                }

                                currentColDelayedTask = scheduler.schedule(this,stalledDelayMs, java.util.concurrent.TimeUnit.MILLISECONDS);
                            }
                            else {
                                stopPainting = false;
                                Alignment.al.ct.startCompute.set(startCompute.get());
                                Alignment.al.ct.endCompute.set(endCompute.get());
                                Alignment.al.ct.seed.set(seed);
                                Alignment.al.ct.colChanged.set(true);
                                synchronized (Alignment.al.ct.colSleeper) {
                                    Alignment.al.ct.colSleeper.notify();
                                }
                            }

                        }
                    }


                };
                synchronized(Alignment.al.ct.paintLock)
                {
                    stopPainting = true;
                    Alignment.al.ct.currentCountUpdate=null;

                }
                currentColDelayedTask = scheduler.schedule(currentTask, stalledDelayMs, java.util.concurrent.TimeUnit.MILLISECONDS);

            }
            else
            {
                editSinceCountdown=true;
                //do nothing really
            }
        }
    }
	
	public void stalledUpdateOld(final int seed)
	{
		Runnable currentTask = new Runnable(){
		
			@Override
			public void run() {
				if(Thread.interrupted())
					return;
				stopPainting = false;
				synchronized(schedulerLock)
				{
				SwingUtilities.invokeLater(new Runnable(){public void run()
				{
					synchronized(schedulerLock)
					{
						
					
					int startres = startCompute.get();
					int endres = endCompute.get();
					Alignment.al.ct.startCompute.set(startres);
					Alignment.al.ct.endCompute.set(endres);

						Alignment.al.ct.seed.set(seed);
					Alignment.al.ct.colChanged.set(true);
					synchronized(Alignment.al.ct.colSleeper)
					{
						Alignment.al.ct.colSleeper.notify();
					}
					}
				
				
				}});
				}
			}
						
		};
		synchronized(schedulerLock)
		{

			synchronized(Alignment.al.ct.paintLock)
			{
				stopPainting = true;
				Alignment.al.ct.currentCountUpdate=null;
				
			}
			Alignment.al.panel.headers.tch.repaint();
		if(currentColDelayedTask==null ||currentColDelayedTask.isDone())
		{
			currentColDelayedTask = scheduler.schedule(currentTask, stalledDelayMs, java.util.concurrent.TimeUnit.MILLISECONDS);

		}
			else
		{
			currentColDelayedTask.cancel(true);
			currentColDelayedTask = scheduler.schedule(currentTask, stalledDelayMs, java.util.concurrent.TimeUnit.MILLISECONDS);
		}
		}
	}
	
	
	public void generateColumnOrder(int width)
	{
		num_percentiles = Math.min(100,Alignment.al.size());
		num_percentiles_sq = num_percentiles*num_percentiles;
		
		int seed = (width-1)/2;
		long startTime = System.currentTimeMillis();
//		int seed = width/2;
		int outarrCurr = 0;
		int [] outarr = new int[(2*width-1)*num_percentiles];
		int [] rowsAdded = new int [width];
//		for(int i = 0; i < width; i++)
//			remainingRes[i] = al.size();
		ColumnComparator colComp = new ColumnComparator();
		PriorityQueue<ColAniScore> colQ = new PriorityQueue<ColAniScore>(width, colComp);
		for(int i = 0; i < width/2 + width%2; i++)
		{
			colQ.add(new ColAniScore(i,getAniScore(i,seed,num_percentiles*2,width)));
		}
		
		while(!colQ.isEmpty())
		{
			ColAniScore currCol = colQ.poll();
			if(currCol.col*2==width-1)//its an odd end basically
			{
				outarr[outarrCurr]=currCol.col*2;
				outarrCurr++;
			}
			else
			{
				int coldbl = currCol.col*2;
				outarr[outarrCurr]=coldbl;
				outarrCurr++;
				
				outarr[outarrCurr]=width-1+width-1-coldbl;
				outarrCurr++;
				outarr[outarrCurr]=coldbl+1;
				outarrCurr++;
				if(coldbl+1!=width-1)
				{
					outarr[outarrCurr]=width-1+width-1-coldbl-1;
					outarrCurr++;
				}
				
			}
			rowsAdded[currCol.col]++;
//			outarr[outarrCurr]= currCol.col;
//			outarr[outarrCurr][1] = rowsAdded[currCol.col];
//			outarrCurr++;
			
			if(rowsAdded[currCol.col]<num_percentiles)
			{
				colQ.add(new ColAniScore(currCol.col,getAniScore(currCol.col,seed,num_percentiles*2-rowsAdded[currCol.col],width)));
			}
		}
		
	synchronized(aniCompLock)
	{
		animationOrder = outarr;
		aniCompLock.notify();
	}
		
		long endTime = System.currentTimeMillis();
		
		System.out.println( "it took " + (endTime - startTime) + "ms");
	
	}
	
	public void OldGenerateColumnOrder(int width, int seed)
	{
		
		seed = 0;
		long startTime = System.currentTimeMillis();
//		int seed = width/2;
		int outarrCurr = 0;
		int [][] outarr = new int[width*num_percentiles][2];
		int [] rowsAdded = new int [width];
//		for(int i = 0; i < width; i++)
//			remainingRes[i] = al.size();
		ColumnComparator colComp = new ColumnComparator();
		PriorityQueue<ColAniScore> colQ = new PriorityQueue<ColAniScore>(width, colComp);
		for(int i = 0; i < width; i++)
		{
			colQ.add(new ColAniScore(i,getAniScore(i,seed,num_percentiles*2,width)));
		}
		
		while(!colQ.isEmpty())
		{
			ColAniScore currCol = colQ.poll();
			outarr[outarrCurr][0]= currCol.col;
			outarr[outarrCurr][1] = rowsAdded[currCol.col];
			outarrCurr++;
			rowsAdded[currCol.col]++;
			if(rowsAdded[currCol.col]<num_percentiles)
			{
				colQ.add(new ColAniScore(currCol.col,getAniScore(currCol.col,seed,num_percentiles-rowsAdded[currCol.col],width)));
			}
		}
		
	synchronized(aniCompLock)
	{
//		animationOrder = outarr;
		aniCompLock.notify();
	}
		
		long endTime = System.currentTimeMillis();
		
		System.out.println( "it took " + (endTime - startTime) + "ms");
	}
	
	public int getAniScore(int col, int seed, int remainingRows, int width)
	{
		double seedratio = Math.abs(col-seed+0.0)/width;
		double rowratio = (remainingRows+0.0)/(num_percentiles*2);
//		return (int)( remainingRows - (col-seed)*(col-seed));
//		return (int) (Math.cos(Math.PI*(col-seed)/seed)*10*remainingRows*remainingRows*remainingRows/al.size());
		//return  ((int) seedratio/(1+rowratio));
		//return (int) ((Math.pow(rowratio,0.5)-seedratio)*100);
		return (int) (num_percentiles_sq*rowratio/Math.pow(1+seedratio,3));
//		return (int) (rowratio*1000);
//		return remainingRows;
//		return (int) (seedratio*100);
		
	}
	class ColumnComparator implements Comparator
	{
//		int [] remainingRes;
//		int seed;
//		public ColumnComparator(int [] remres, int seed)
//		{
//			remainingRes = remres;
//			this.seed = seed;
//		}
//		
		
		@Override
		public int compare(Object o1, Object o2) {
			ColAniScore num1 = (ColAniScore) o1;
			ColAniScore num2 = (ColAniScore) o2;
			
			return num2.score -num1.score;
			
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class ColAniScore
	{
		int score, col;
		
		public ColAniScore(int col, int score)
		{
			this.score = score;
			this.col = col;
		}
	}
	
	

	
	
}