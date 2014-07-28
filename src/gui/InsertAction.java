package gui;

import java.io.Serializable;

class InsertAction extends ScoreAction implements Serializable
{

	int underlyingseq;
	Residue.ResidueType res;
	int pos;
	
	public InsertAction(int useq, Residue.ResidueType res, int pos)
	{
		
		
		underlyingseq = useq;
		this.res = res;
		this.pos = pos;
	}
	
	
	@Override
	public void run() {
//		ScoringAlignment.this.get(0).a
//		ScoringAlignment.this.get(underlyingseq).
		Alignment.al.scoreal.get(underlyingseq).add(pos,res);
		if(Alignment.al.scoreal.get(underlyingseq).size()>Alignment.al.scoreal.longestupperbound)
			Alignment.al.scoreal.longestupperbound=Alignment.al.scoreal.get(underlyingseq).size();
		// TODO Auto-generated method stub
		
	}
	
}
