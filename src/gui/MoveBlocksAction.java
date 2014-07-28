package gui;

import java.io.Serializable;

class MoveBlocksAction extends ScoreAction implements Serializable
{
	int respos;
	boolean  extend;
	
	public MoveBlocksAction(int respos, boolean extend)
	{
		//extend is whether or not the block is extended(rather than contracted)
		this.respos  = respos;
		
		this.extend = extend;
		
	}
	
	@Override
	public void run()
	{
		int currentblock = Alignment.al.scoreal.getBlockPosAtPos(respos);
		if(extend)
		{
			Alignment.al.scoreal.stickyblocks.get(currentblock).endres++;
			
			for(int i = currentblock+1; i < Alignment.al.scoreal.stickyblocks.size(); i++)
			{
				Alignment.al.scoreal.stickyblocks.get(i).startres += 1;
				Alignment.al.scoreal.stickyblocks.get(i).endres +=1;
			}
		}
		else if(!extend)
		{
			Alignment.al.scoreal.stickyblocks.get(currentblock).endres--;
			for(int i = currentblock; i < Alignment.al.scoreal.stickyblocks.size();i++)
			{
				Alignment.al.scoreal.stickyblocks.get(i).startres --;
				Alignment.al.scoreal.stickyblocks.get(i).endres --;
				
			}
		}
		
	}
}