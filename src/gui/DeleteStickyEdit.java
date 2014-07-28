package gui;

public class DeleteStickyEdit extends Edit {

//	int underlyingseqpos, respos;
	int gapInsertPos;
//	Alignment al;
	boolean undo;
	Residue.ResidueType resDeleted;
	int blankInsertpos;
	public DeleteStickyEdit(int underlyingseqpos, int respos,  Residue.ResidueType resDeleted, int blankInsertPos) 
	{
		super();
		this.underlyingseqpos = underlyingseqpos;
		this.respos = respos;
		//this.gapInsertPos = gapInsertPos;
//		this.al = al;
		this.resDeleted = resDeleted;
		this.blankInsertpos = blankInsertPos;
		
	}
	
	
	@Override
	public void run() {

		Alignment.al.insertResidueSticky(Alignment.al.inverseviewmap.get(underlyingseqpos), respos,blankInsertpos, new Residue(resDeleted ));

	}
	
	@Override
	public int getUnderlyingSeq() {
		return underlyingseqpos;
		// TODO Auto-generated method stub
		
	}

}
