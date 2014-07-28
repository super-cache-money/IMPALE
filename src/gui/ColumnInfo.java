package gui;

public class ColumnInfo implements Comparable{
	int count;
	Residue.ResidueType res;
	
	public ColumnInfo(Residue.ResidueType res)
	{
		this.res = res;
	}
	
	@Override
	public boolean equals(Object o)
	{
		ColumnInfo other = (ColumnInfo) o;
		if(other.res.equals(this.res))
			return true;
		return false;
	}
	
	@Override
	public int compareTo(Object o)
	{
		ColumnInfo other= (ColumnInfo) o;
		return this.count-other.count;
	}
	

}
