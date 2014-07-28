package gui;

public class InsertNormalEdit extends Edit {

//	int underlyingseqpos, respos;
	Residue r;
//	Alignment al;
	
	public InsertNormalEdit(int underlyingseqpos, int underlyingrespos, Residue r) {
		super();
		this.r = r;
		this.underlyingseqpos = underlyingseqpos;
		this.respos = underlyingrespos;
//		this.al = al;
	}
	

	@Override
	public void run() {
		
		Alignment.al.deleteResidueNormal(Alignment.al.inverseviewmap.get(underlyingseqpos), respos);
		
		// TODO Auto-generated method stub

	}
	
	@Override
	public int getUnderlyingSeq() {
		return underlyingseqpos;
		// TODO Auto-generated method stub
		
	}

}
