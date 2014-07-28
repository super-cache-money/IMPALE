package gui;

import java.io.Serializable;
import java.util.ArrayList;

class AlignAction extends ScoreAction implements Serializable
{
	ArrayList<Residue> currentsegment;
	int startres;
	
	int underlyingseq;
	public AlignAction(ArrayList<Residue> currentsegment, int startres, int underlyingseq)
	{
		
		//-GGAGGAGTAAA-TGGCGCCGTTAAACGG-TGCCGT-AAT----T
		//-GGAGGAGTAAA-TGGCGCCGTTAAACGG-TGCCGT-AATAT--T
		this.startres = startres;
		this.underlyingseq = underlyingseq;
		this.currentsegment = currentsegment;
	}
	@Override
	public void run() {

		for(int i = 0; i < currentsegment.size();i++)
		{
//			try{ //possible fix, think it may be too superficial though.
			Alignment.al.scoreal.get(underlyingseq).set(startres+i, currentsegment.get(i).getType() );
//			}
//			catch(ArrayIndexOutOfBoundsException ex)
//			{
//				ScoringAlignment.this.get(underlyingseq).add(currentsegment.get(i).getType());
//			}

		}
		// TODO Auto-generated method stub

	}


    public String show()
    {
        String out = startres + ": ";

        for(Residue r : currentsegment)
        {
            out+=""+r;
        }
        return out;
    }

}
