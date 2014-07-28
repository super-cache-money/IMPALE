package gui;

//COMMENTED OUT LINE

public class PairwiseInfo implements Comparable{

	static int compareToSeq;
	public int seq;
//	public Block sb;
	int pairwiseScore;
	public PairwiseInfo(int seq, int score)
	{
		this.seq = seq;
//		this.sb = sb;
		pairwiseScore = score;
//		pairwiseScore = sb.get(compareToSeq, seq);
	}
	@Override
	public int compareTo(Object o) {
		if (o.getClass()!=this.getClass())
				return 9999999;
		PairwiseInfo other = (PairwiseInfo) o;
		return this.pairwiseScore - other.pairwiseScore;
		
		// TODO Auto-generated method stub
		
	}

}
