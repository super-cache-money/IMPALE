package gui;

public class SequencePairwise {
	int seq1, seq2;

	
	public SequencePairwise(int seq1, int seq2)
	{
		if(seq1>seq2)
		{
		this.seq1 = seq2;
		this.seq2 = seq1;
		}
		else{
			this.seq2 = seq2;
			this.seq1 = seq1;
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if(seq1<seq2)
		{
		result = prime * result + seq1;
		result = prime * result + seq2;
		}
		else
		{
			result = prime*result + seq2;
			result = prime*result + seq1;
		}
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
		SequencePairwise other = (SequencePairwise) obj;
		if (seq1 ==  other.seq1 && seq2== other.seq2 )
			return true;
		if (seq1 == other.seq2 && seq2 == other.seq1 )
			return true;
		return false;
	}
	
	

}
