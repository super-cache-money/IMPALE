package gui;

import java.util.Arrays;

public class ShiftingRightResiduePos extends ResiduePos implements Comparable {
	public int [] location;
	public int trailingblanks = 0;
	public ShiftingRightResiduePos(int x, int y,int trailingblanks)
	{
		super(x,y);
		location = new int [2];
		location[0]=x;
		location[1]=y;
		this.trailingblanks = trailingblanks;
		
	}
	public ShiftingRightResiduePos(ResiduePos rp, int trailingblanks)
	{
		super(rp.location[0],rp.location[1]);
		location[0] = rp.location[0];
		location[1] = rp.location[1];
		this.trailingblanks = trailingblanks;
	}
	
	@Override
	public int hashCode() {
		final int prime = 29;
		int result = 1;
		result = prime * result + Arrays.hashCode(location);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
//		if (getClass() != obj.getClass())
//			return false;
		ResiduePos other = (ResiduePos) obj;
		if (!Arrays.equals(location, other.location))
			return false;
		return true;
	}
	
	

	public String toString()
	{
		return "x:" + location[0] + " y:" + location[1];
	}


	@Override
	public int compareTo(Object o) {
		ShiftingRightResiduePos r = (ShiftingRightResiduePos) o;
		if(this.trailingblanks < r.trailingblanks)
			return -1;
		if(this.trailingblanks > r.trailingblanks)
			return 1;
		if(this.location[1]>r.location[1])
			return 1;
		if(this.location[1]<r.location[1])
			return -1;
		if (this.location[0]>r.location[0])
			return 1;
		if (this.location[0]<r.location[0])
			return -1;

		// TODO Auto-generated method stub
		return 0;
	}
}
