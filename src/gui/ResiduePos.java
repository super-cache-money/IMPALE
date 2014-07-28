package gui;

import java.io.Serializable;
import java.util.Arrays;

public class ResiduePos implements Comparable, Serializable {
	public int [] location;
	
	public ResiduePos(int x, int y)
	{
		location = new int [2];
		location[0]=x;
		location[1]=y;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
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
		if (getClass() != obj.getClass())
			return false;
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
		ResiduePos r = (ResiduePos) o;
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
