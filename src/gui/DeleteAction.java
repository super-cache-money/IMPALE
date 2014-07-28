package gui;
import java.io.Serializable;


class DeleteAction extends ScoreAction implements Serializable
{

	int underlyingseq;
	int pos;
	
	public DeleteAction(int seq, int pos)
	{
		
		
		this.underlyingseq = seq;
		this.pos = pos;
		
	}
	@Override
	public void run() {
		Alignment.al.scoreal.get(underlyingseq).remove(pos);
		// TODO Auto-generated method stub
		
	}
	
}
