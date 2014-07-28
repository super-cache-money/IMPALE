package gui;

import java.io.Serializable;
import java.util.Vector;

class RedoEverythingAction extends ScoreAction implements Serializable
{
	
	//When run, it clears all the scoring stickyblocks. It does not actually recompute all scoring.
Vector<Integer> blockstartres;
Vector<Integer> blockendres;
boolean skipScoreRecompute = false; 
//this is only used when loading a session, and setting scoreal stickyblocks before restoring scoreal
//to appropriate state before edits which havent been scored (but have been saved)
	public RedoEverythingAction(Vector<Integer> blockstartres, Vector<Integer> blockendres)
	{
		this.blockstartres = blockstartres;
		this.blockendres = blockendres;

	}
	
	@Override
	public void run() {
		if(blockstartres!=null && blockendres !=null)
		{
		System.out.println("updating scoring stickyblocks");
		Alignment.al.scoreal.stickyblocks.clear();
		for(int i = 0; i < blockstartres.size(); i++)
		{
			Alignment.al.scoreal.stickyblocks.add(Alignment.al.scoreal.new StickyScoreBlock(blockstartres.get(i),blockendres.get(i)));
			
		}
		}
		// TODO Auto-generated method stub
		
	}

}