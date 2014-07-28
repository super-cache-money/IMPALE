package gui;

public class StickyBlock{
//stores one sticky or non-sticky block of the alignment
	int startres, endres;
//	Alignment al;
	
	int score;
	public StickyBlock(int start, int end)
	{
		this.startres = start;
		this.endres = end;
//		this.al = al;
		
		for(int i = 0; i < Alignment.al.size(); i++)
			for(int j = 0; j < Alignment.al.size(); j++)
		{
			
		}
		

		
	}
	
	@Override 
	public boolean equals(Object o)
	{
		Integer i = (Integer) o;
		if(i>endres)
			return false;
		if(i<startres)
			return false;
		return true;
	}
	
//	public int get(int i, int j)
//	{
//		if(i<j)
//			return scores[i][j];
//		else
//			return scores[j][i];
//	}
	
	public String toString()
	{
		return startres + ":" + endres + " ";
	}
	
	 
//	public void addScores()
//	{
//		for(int i = 0; i < Alignment.al.size(); i++)
//			for(int j = i + 1; j < Alignment.al.size(); j++)
//		{
//			score+= scores[i][j];
//		}
//	}
//	public void set(int i, int j, int val)
//	{
//		int temp;
//		if(j<i)
//		{
//			temp = i;
//			i = j;
//			j = temp;
//		}
//		this.scores[i][j] = val;
//	}

//	public void increment(int i, int j, int inc)
//	{
//		
//		if(i<j)
//		{
//			scores[i][j]+=inc;
//		}
//		else
//		{
//			scores[j][i]+=inc;
//		}
//			
//	}
	
	

}
