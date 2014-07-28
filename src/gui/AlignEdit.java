package gui;

import java.util.ArrayList;

public class AlignEdit extends Edit{
	ArrayList<Residue> oldsegment, currentsegment;
//	int respos; its the starting pos
	//Alignment al;
//	int underlyingseqpos;
	public AlignEdit(  ArrayList<Residue> oldsegment, ArrayList<Residue> currentsegment, int startres, int underlyingseq)
	{
		super();
		//this.al = al;
		this.oldsegment = oldsegment;
		this.respos = startres;
		this.underlyingseqpos = underlyingseq;
		this.currentsegment = currentsegment;
		synchronized(Alignment.al.scorequeue)
		{
			Alignment.al.scorequeue.add(new AlignAction(currentsegment,  startres, underlyingseq));
		}
	}
	@Override
	public void run() {
		System.out.println("WAT" + oldsegment.size());
		
		ArrayList<Residue> backup = new ArrayList<Residue>();
		
		Sequence curr = Alignment.al.getUnderlying(underlyingseqpos);
		for(int i = 0; i < oldsegment.size(); i++)
		{
//			System.out.println(oldsegment.get(i));
			backup.add(new Residue(curr.get(i+respos).getType()));
			curr.set(respos+i, oldsegment.get(i));
			
		}
		Alignment.al.currentEdit.push(new AlignEdit( currentsegment, oldsegment, respos, underlyingseqpos));
		Alignment.al.changed.add(new ResiduePos(0,0));
		Alignment.al.panel.canvas.repaint();
		// TODO Auto-generated method stub
		
	}
	@Override
	public int getUnderlyingSeq() {
		// TODO Auto-generated method stub
		return underlyingseqpos;
	}
	
	

}
