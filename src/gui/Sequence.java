package gui;



import gui.Residue.ResidueType;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;


public class Sequence extends ArrayList<Residue>{
	
	public static HashMap<String,String> codonTable = new HashMap<String,String>();
	public String name;
	int seqnum;
	static int scoreGapExtension =-1;
	static int scoreGapOpen = -2;
	static int scoreTransition = -1;
	static int scoreTransversion = -2;
	static int scoreMatch = 1;

	public static boolean isProtein;

	
//	Alignment al;
//	ArrayList<Residue> seq;

	public static Sequence getConsensus(ArrayList<Column> in)
	{
		Sequence cons = new Sequence("Consensus", -5);
		for(Column posmap : in)
		{
			
			cons.add(new Residue(posmap.res));
		}
		
		return cons;
		
	}
	
	public static ArrayList<Column> getConsensusProfile(int startres, int endres)
	{
		ArrayList<Column> profarr = new ArrayList<Column>();
		
		for(int i = startres; i <=endres;i++)
		{
			
			Column newmap = new Column();
//			HashMap<Residue.ResidueType, MutableInt> newmap = new HashMap<Residue.ResidueType,MutableInt>();
			for(Residue.ResidueType rest : Alignment.al.usedResiduesArr)
			{
				newmap.put(rest, new MutableInt(0));
			}
			for(Sequence seq : Alignment.al)
			{
				MutableInt currMut = newmap.get(seq.get(i).getType());
				currMut.value++;
				if(currMut.value>=newmap.max)
				{
					newmap.max=currMut.value;
					newmap.res=seq.get(i).getType();
				}
			}
			profarr.add(newmap);
		}
		
		return profarr;
	}

    public int getPairwiseScoreWith(Sequence otherSeq, int fromRes, int toRes)
    {
        int tot = 0;
        boolean thisWasBlank = false;
        boolean otherWasBlank = false;
        Residue thisRes, otherRes;
        for(int i = fromRes; i <=toRes; i++)
        {
            thisRes = this.get(i);
            otherRes = otherSeq.get(i);
            int blanks = ((thisRes.isBlank()) ? 1 : 0) + ((otherRes.isBlank()) ? 1 : 0);
            if(blanks==2)
                continue;
            else if(blanks==1)
            {
                if(thisRes.isBlank())
                {
                    if(thisWasBlank)
                        tot+=Sequence.scoreGapExtension;
                    else
                    {
                        thisWasBlank=true;
                        tot+=Sequence.scoreGapOpen;
                    }

                }
                else
                {
                    if(otherWasBlank)
                    {
                        tot+=Sequence.scoreGapExtension;
                    }
                    else
                    {
                        otherWasBlank=true;
                        tot+=Sequence.scoreGapOpen;
                    }

                }
            }
            else //no blanks
                tot+=thisRes.scoreMatrix(otherRes);

        }

        return tot;
    }

	public Sequence removeGapsInInterval(int startres, int endres)
	{
		Sequence out = new Sequence("seqAlign", 999999);
		for(int i = startres; i <= endres; i++)
		{
		 if(!this.get(i).isBlank())
			 out.add(this.get(i));
		}
		
		return out;
	}
	public Sequence (String name, int seqnum)
	{
		super();
		this.seqnum = seqnum;
		this.name = name;
//		this.al = al;
		
	}
	
	
	
	public Sequence (String name, String seq, int seqnum)
	{
		super();
		this.seqnum = seqnum;
		this.name = name;
//		this.al = al;
		for(int i = 0; i < seq.length(); i++)
		{
			Residue currentres = new Residue(seq.charAt(i));
			add(currentres);
			Alignment.al.usedResiduesSet.add(currentres.getType());
		}
	}
	
	

	
	public void addPlain(int pos, Residue r)
	{
		super.add(pos,r);
	}
	

	@Override
	public Residue remove(int pos)
	{
		
//		for(int i = pos; i < Alignment.al.longestSeq; i++)
//		{
//			Column current = Alignment.al.columns.get(i);
//			current.decrement(this.get(i).getType());
//			try{
//			current.increment(this.get(i+1).getType());
//			}
//			catch(ArrayIndexOutOfBoundsException e)
//			{
//				//System.out.println("Reached end");
//			}
//		}
		try{
			return super.remove(pos);
		}
		catch(IndexOutOfBoundsException ex)
		{
			System.err.println("Removing failed for " +name);
			return new Residue(Residue.ResidueType.BLANK);
		}
	}


	@Override
	public String toString()
	{
//		System.out.println("Starting seq");
//		String out = name +": ";
		String out = "";
		for (int i = 0; i < size(); i++)
			out+=get(i);
//		StringBuilder sb = new StringBuilder(out);
//		for (int i = 0; i < size(); i++)
//			sb.append(get(i));
//		
//		
////		System.out.println("ending seq");
//		return sb.toString();
		return out;
		
	}

    public String translatePiece(int start, int end)
    {
        StringBuilder sb = new StringBuilder();
        String codon = "";
        for(int i = start; i < end; i=i+3)
        {
            codon = "" + this.get(i) + this.get(i+1) + this.get(i+2);
            sb.append(translateCodon(codon));
        }

        return sb.toString();
    }

    private char translateCodon(String codon)
    {
        return '?';
    }


    @Override
	public Residue get(int index)
	{
		try{
			return super.get(index);
		}
		catch(IndexOutOfBoundsException e)
		{
			return new Residue('-');
		}
	}
	
//	public int pairwiseSubset(Sequence other, int startpos, int endpos)
//	{
//		int score = 0;
//		boolean gapOpen1 = false;
//		boolean gapOpen2 = false;
//		Residue.ResidueType thisres, otherres;
//		for(int i = startpos; i<=endpos; i++)
//		{
//			
//			thisres = get(i).getType();
//			otherres = other.get(i).getType();
//			if(thisres!=Residue.ResidueType.BLANK || otherres!=Residue.ResidueType.BLANK)
//			{
//			//check equality
//			if((thisres==otherres))
//			{
//				if(thisres!=Residue.ResidueType.BLANK)
//				score+= scoreMatch;
//			}
//			else
//			{
//				score+=scoreMismatch;
//			}
//			
//			//Gap for this res
//			
//			if(thisres==Residue.ResidueType.BLANK)
//			{
//				if(gapOpen1)
//				{
//					score+=scoreGapExtension;
//				}
//				else
//				{
//					score+=scoreGapOpen;
//					gapOpen1 = true;
//				}
//			}
//			else if(gapOpen1)
//			{
//				gapOpen1=false;
//			}
//			
//			//Gap for other res
//			if(otherres==Residue.ResidueType.BLANK)
//			{
//				if(gapOpen2)
//				{
//					score+=scoreGapExtension;
//				}
//				else
//				{
//					score+=scoreGapOpen;
//					gapOpen2 = true;
//				}
//			}
//			else if(gapOpen2)
//			{
//				gapOpen2=false;
//			}
//			}
//		}
//		//System.out.println(score);
//		return score;
//	}
	
	public double alignToProfile(int startres, int endres, ArrayList<Column> profile)
	{
		Sequence cons;
		ArrayList<Residue> backup = new ArrayList<Residue>();
//		ArrayList<Column> profile = Sequence.getConsensusProfile(startres, endres);
		Sequence in = getConsensus(profile);
		if(in.sizeInclBlanks() == endres-startres+1)
		{
			 cons = in;
		}
		else
		{
		cons = new Sequence("cons", -1);
		for(int i = startres; i <= endres; i++)
		{
			cons.add(in.get(i));
			
		}
		}
		for(int i = startres; i <=endres; i++)
		{
			backup.add(new Residue(this.get(i).getType()));
		}
		
		Sequence crop = this.removeGapsInInterval(startres, endres);
		int [][][][] traverse = new int [cons.sizeInclBlanks()+1][crop.sizeInclBlanks()+1][2][3];
		double [][][] matrix = new double [cons.sizeInclBlanks()+1][crop.sizeInclBlanks()+1][4];
		//[cons][crop][type] where type : 0 = max where last res's were taken, 1 = max with gap in crop; 
		//type = 2 corresponds with whether a true gap was taken in reference to the consensus (0-false, 1-true)
		matrix[0][0][1] = 0;
		matrix[0][0][0]= 0; 
		
		traverse[0][0][0][0] = -999999;
		
		int starti = startres-1;
		double startBlankBonus = 0;
		while(starti<Alignment.al.longestSeq&&starti>=0&&startBlankBonus < 1&&this.get(starti).isBlank())
		{
			Column col = Alignment.al.tempAlignSet.get(starti);
			if(col==null)
			{
				col = Sequence.getConsensusProfile(starti, starti).get(0);
				Alignment.al.tempAlignSet.put(starti, col);
			}
			int blankcount = col.get(Residue.ResidueType.BLANK).value;
//			if(blankcount ==al.size())
			startBlankBonus+=1-(blankcount+0.0)/Alignment.al.size();
			starti--;
		}
		startBlankBonus=Math.min(startBlankBonus,1);
		matrix[0][0][2] = startBlankBonus;
//		for(int i = 0; i < cons.size() ; i++)
//		{
//			int [] from = {i,0,1};
//			traverse[i+1][0][1] = from;
//			matrix[i+1][0][1] = scoreGapExtension*(i) + scoreGapOpen;
//			matrix[i+1][0][0] = -99999;
//		}
		
		int c = 0;
		
		boolean first = true;
		//set top row;
		while ( c<cons.sizeInclBlanks())
		{
			double profileGapFraction = 0;
			
			if((profile.get(c).get(Residue.ResidueType.BLANK)!=null))
			{
				profileGapFraction = (profile.get(c).get(Residue.ResidueType.BLANK).value+0.0)/(Alignment.al.size()+0.0);
			}
			int [] from = {c,0,1};
			traverse[c+1][0][1] = from;
		

			matrix[c+1][0][1] = matrix[c][0][1] + matrix[c][0][2]*scoreGapExtension*(1-profileGapFraction) + (1-matrix[c][0][2])*scoreGapOpen*(1-profileGapFraction);
			matrix[c+1][0][2] = Math.min(matrix[c][0][2] + 1-profileGapFraction, 1);
			
//			if(cons.get(c).isBlank())
//			{
//				matrix[c+1][0][1] = matrix[c][0][1]+scoreGapExtension*(1-profileGapFraction);
//				matrix[c+1][0][2] = matrix[c][0][2];
//			}
//			else if(!first)
//			{
//				matrix[c+1][0][1] = matrix[c][0][1]+scoreGapExtension*(1-profileGapFraction);
//				matrix[c+1][0][2] = 1;
//			}
//			else
//			{
//				matrix[c+1][0][1] = matrix[c][0][1] + scoreGapOpen*(1-profileGapFraction);
//				first = false;
//				matrix[c+1][0][2] = 1;
//			}

			matrix[c+1][0][0] = -99999;
			c++;
			
		}
		
		double m, g, val1, val2, val3;
		System.out.println("QWERTY");
		for(int j = 1; j < crop.sizeInclBlanks()+1; j++)
		
		{
			for(int i = j; i < cons.sizeInclBlanks()+1; i++)
			{
				//calculate m;
				
				val1 = matrix[i-1][j-1][0];
				val2 = matrix[i-1][j-1][1];
				Column currcol = profile.get(i -1);
				//if(currcol.res!=Residue.ResidueType.BLANK)
				for(Entry<ResidueType, MutableInt> entry : currcol.entrySet())
				{
					if(entry.getKey()==ResidueType.BLANK)
					{
						val1+=(Sequence.scoreGapOpen*(entry.getValue().value+0.0)/(0.0+Alignment.al.size()));
						val2+=(Sequence.scoreGapOpen*(entry.getValue().value+0.0)/(0.0+Alignment.al.size()));
					}
					else
					{
						val1+=(crop.get(j-1).scoreMatrix(new Residue(entry.getKey()))*(entry.getValue().value+0.0))/(0.0+Alignment.al.size());
						val2+=(crop.get(j-1).scoreMatrix(new Residue(entry.getKey()))*(entry.getValue().value+0.0))/(0.0+Alignment.al.size());
					}
					
					//System.out.println(crop.get(j-1).scoreMatrix(new Residue(entry.getKey()))*(entry.getValue().doubleValue())/(0.0+Alignment.al.size()));
						
				}
				
				
				

				if(val1>val2)
				{
					matrix[i][j][0] = val1;
					int [] arr = {i-1,j-1,0};
					traverse[i][j][0] = arr;
				}
				else
				{
					matrix[i][j][0] = val2;
					int [] arr = {i-1,j-1,1};
					traverse[i][j][0] = arr;
				}
				
				if(i!=j)
				{
					double profileGapFraction = 0;
					if((profile.get(i-1).get(Residue.ResidueType.BLANK)!=null))
					{
						profileGapFraction = (profile.get(i-1).get(Residue.ResidueType.BLANK).value+0.0)/(Alignment.al.size()+0.0);
					}
					 
					
					
					val1 = matrix[i-1][j][0] + Sequence.scoreGapOpen*(1-profileGapFraction);
					val2 = matrix[i-1][j][1] + matrix[i-1][j][2]*Sequence.scoreGapExtension*(1-profileGapFraction) + (1-matrix[i-1][j][2])*Sequence.scoreGapOpen*(1-profileGapFraction);;
//					if(matrix[i-1][j][2] == 1)
//					{
//						val2 = matrix[i-1][j][1] + Sequence.scoreGapExtension*(1-profileGapFraction);
//					}
//					else  
//					{
//						val2 = matrix[i-1][j][1] + Sequence.scoreGapOpen*(1-profileGapFraction);
//					}

					if(val1>val2)
					{
						matrix[i][j][1] = val1;
						int [] arr = {i-1, j, 0};
						traverse[i][j][1] = arr;
						
						matrix[i][j][2] = 1-profileGapFraction;
					}
					else
					{
						matrix[i][j][1] = val2;
						int [] arr = {i-1, j, 1};
						traverse[i][j][1] = arr;
						
						matrix[i][j][2] = Math.min(1-profileGapFraction+matrix[i-1][j][2], 1);
					}
					
					//this right here incentivises putting gaps at the end of the aligned bit, because of the effect it has on the gap extension penalties of the following bit.
					
					if(i==cons.sizeInclBlanks())
					{
						int endi = endres+1;
						double totalScoreImpact = 0;
						double gapTakenPercent = 0; //running total of GapTaken wrt the consensus, excluding the gaptaken percent of the final aligned blank
						while(endi < Alignment.al.longestSeq && this.get(endi).isBlank())
						{
							Column col = Alignment.al.tempAlignSet.get(endi);
							if(col==null)
							{
								col = getConsensusProfile(endi,endi).get(0);
								Alignment.al.tempAlignSet.put(endi,col); //this thing is a cached column storage, implemented to speed up aligning multiple seqs.
							}
							
							double colgaps = (col.get(Residue.ResidueType.BLANK).value+0.0)/Alignment.al.size();
							double currGapTaken = Math.min(1, gapTakenPercent+matrix[i][j][2]);
							double currImpact = Math.max(0, currGapTaken-gapTakenPercent);
							totalScoreImpact+=currImpact*Sequence.scoreGapExtension*(1-colgaps) - currImpact*Sequence.scoreGapOpen*(1-colgaps);
							gapTakenPercent+=(1-colgaps);
							if(currImpact==0)
							{
								break;
							}
							endi++;
						}
//						System.out.println("totImpact "+i+","+j +":"+ totalScoreImpact);
						matrix[i][j][1]+=totalScoreImpact;
						
					}
					
					
					
					
//					if(cons.get(i-1).isBlank())
//					{
////						val1= matrix[i-1][j][0];
////						val2=matrix[i-1][j][1];
//						
//							if(val1>val2)
//							{
//								matrix[i][j][2] = 0;
//							}
//							else
//							{
//
//								matrix[i][j][2]=matrix[i-1][j][2];
//							}
//						
//						
//					}
					
 
					

				}
				else
				{
					matrix[i][j][1] = -99999;
				}  
				
//				System.out.print(matrix[i][j][0] + "," + matrix[i][j][1] + ",");
				//System.out.print (" " + matrix[i][j][0] + "|" + matrix [i][j][1]);
			}
//			System.out.println();
			//System.out.println("");
		}
		
		//THIS SHIT PRINTS OUT ALL THE TRACES ETC
//		System.out.println("matrix\n\n");
//		for(int j = 0; j < matrix[0].length;j++)
//		
//		{
//			for(int i = 0; i < matrix.length; i++)
//			{
//				System.out.print (",|," + matrix[i][j][0] + "," + matrix [i][j][1]);
//			}
//			System.out.println();
//		}
//		
//		System.out.println("now the trace\n\n");
//		
//		for(int j = 0; j < traverse[0].length;j++)
//		{
//			
//				for(int i = 0; i < traverse.length; i++)
//			{
//				int [] curr = traverse[i][j][0];
//				System.out.print(""+curr[0] + ","+curr[1]+","+curr[2]+",|,");
//			}
//			System.out.println("");
//			
//				for(int i = 0; i < traverse.length; i++)
//			{
//				int [] curr = traverse[i][j][1];
//				System.out.print(""+curr[0] + ","+curr[1]+","+curr[2]+",|,");
//			}
//			System.out.println("");   
//			
//			
//		}
		System.out.println("\n\n");
		
		ArrayList<Residue> aligned = new ArrayList<Residue>();
		
		//for(int i = 0; i < )
		int max = 0;
		int maxsize = crop.size();
		int type = 1;
		
		if(matrix[cons.sizeInclBlanks()][maxsize][0] > matrix[cons.sizeInclBlanks()][maxsize][1])
			type = 0;
//		for(int i = 0; i < crop.size()+1; i ++)
//		{
//			if(matrix[cons.size()][i][0] >max)
//			{
//				max = matrix[cons.size()][i][0];
//				maxsize = i;
//				type = 0;
//			}
//			
//			if(matrix[cons.size()][i][1] > max)
//			{
//				max = matrix[cons.size()][i][1];
//				maxsize = i;
//				type = 1;
//			}
//		}
		
		if(type==0)
		{
			aligned.add(crop.get(crop.sizeInclBlanks()-1));
		}
		else
		{
			aligned.add(new Residue('-'));
		}
		int [] trace = traverse[cons.sizeInclBlanks()][maxsize][type];

		while(trace[1]!=0)
		{
			//System.out.println(trace[0] + " " + trace[1] + " " + trace[2]);
			if(trace[2]==0)
			{
				aligned.add(crop.get(trace[1]-1));
			}
			else
			{
				aligned.add(new Residue('-'));
			}
			int new0 = traverse[trace[0]][trace[1]][trace[2]][0];
			int new1 = traverse[trace[0]][trace[1]][trace[2]][1];
			int new2 = traverse[trace[0]][trace[1]][trace[2]][2];
			trace[0] = new0;
			trace[1] = new1;
			trace[2] = new2;

		}
		
		for(int i = 0; i < trace[0]; i ++)
		{
			aligned.add(new Residue('-'));
		}
		ArrayList<Residue> changedseq = new ArrayList<Residue>();
		for(int i = 0; i < endres - startres + 1; i++)
		{
			try
			{
				changedseq.add(aligned.get(aligned.size()-1-i));
				this.set(startres + i, aligned.get(aligned.size()-1 -i));
			}
			catch(Exception e)
			{
				this.set(startres + i, new Residue('-'));
				changedseq.add(new Residue('-'));
			}
		}
		
		Alignment.al.currentEdit.add(new AlignEdit(backup, changedseq, startres, this.seqnum));
		return matrix[cons.sizeInclBlanks()][maxsize][type];
		
	}
	
	public double align(int startres, int endres, Sequence in)
	{
		Sequence cons;
		ArrayList<Residue> backup = new ArrayList<Residue>();
		if(in.sizeInclBlanks() == endres-startres+1)
		{
			 cons = in;
		}
		else
		{
		cons = new Sequence("cons", -1);
		for(int i = startres; i <= endres; i++)
		{
			cons.add(in.get(i));
			
		}
		}
		for(int i = startres; i <=endres; i++)
		{
			backup.add(new Residue(this.get(i).getType()));
		}
		System.out.println(cons);
		Sequence crop = this.removeGapsInInterval(startres, endres);
		int [][][][] traverse = new int [cons.sizeInclBlanks()+1][crop.sizeInclBlanks()+1][2][3];
		int [][][] matrix = new int [cons.sizeInclBlanks()+1][crop.sizeInclBlanks()+1][4];
		//[cons][crop][type] where type : 0 = max where last res's were taken, 1 = max with gap in crop; 
		//type = 2 corresponds with whether a true gap was taken in reference to the consensus (0-false, 1-true)
		matrix[0][0][1] = 0;
		matrix[0][0][0]= 0; 
		matrix[0][0][2] = 0;
		traverse[0][0][0][0] = -55555;
//		for(int i = 0; i < cons.size() ; i++)
//		{
//			int [] from = {i,0,1};
//			traverse[i+1][0][1] = from;
//			matrix[i+1][0][1] = scoreGapExtension*(i) + scoreGapOpen;
//			matrix[i+1][0][0] = -99999;
//		}
		
		int c = 0;
		
		boolean first = true;
		//set top row;
		while ( c<cons.sizeInclBlanks())
		{
			int [] from = {c,0,1};
			traverse[c+1][0][1] = from;
		
			if(cons.get(c).isBlank())
			{
				matrix[c+1][0][1] = matrix[c][0][1];
				matrix[c+1][0][2] = matrix[c][0][2];
			}
			else if(!first)
			{
				matrix[c+1][0][1] = matrix[c][0][1]+scoreGapExtension;
				matrix[c+1][0][2] = 1;
			}
			else
			{
				matrix[c+1][0][1] = matrix[c][0][1] + scoreGapOpen;
				first = false;
				matrix[c+1][0][2] = 1;
			}

			matrix[c+1][0][0] = -99999;
			c++;
			
		}

		int m, g, val1, val2, val3;
		for(int j = 1; j < crop.sizeInclBlanks()+1; j++)
		
		{
			for(int i = j; i < cons.sizeInclBlanks()+1; i++)
			{
				//calculate m;
				if(cons.get(i-1).isBlank())
				{
					int count =2;
					//int []temp = traverse[i-1][j-1][1];
					int [] temp = {i-1, j-1, 1};
					try
					{
						while(temp[2]==1 && cons.get(i-count).isBlank())
								{
									temp = traverse[temp[0]][temp[1]][temp[2]];
									count++;
								}
						
						
					
					if(temp[2]==0 && cons.get(i-count).isBlank())
					{
						val2 = matrix[i-1][j-1][1] + Sequence.scoreGapOpen;
						
					}
					else
					{
						val2 = matrix[i-1][j-1][1] + Sequence.scoreGapOpen;
					}
					}
					catch (ArrayIndexOutOfBoundsException ex) 
					{
						val2 = matrix[i-1][j-1][1] + Sequence.scoreGapOpen;
					}
					val1 = matrix[i-1][j-1][0] + Sequence.scoreGapOpen;
					
					
				}
				else
				{
					val1 = matrix[i-1][j-1][0] + crop.get(j-1).scoreMatrix(cons.get(i-1));
					val2 = matrix[i-1][j-1][1] + crop.get(j-1).scoreMatrix(cons.get(i-1));
				}
				

				if(val1>val2)
				{
					matrix[i][j][0] = val1;
					int [] arr = {i-1,j-1,0};
					traverse[i][j][0] = arr;
				}
				else
				{
					matrix[i][j][0] = val2;
					int [] arr = {i-1,j-1,1};
					traverse[i][j][0] = arr;
				}
				
				if(i!=j)
				{
					if(cons.get(i-1).isBlank())
					{
						val1= matrix[i-1][j][0];
						val2=matrix[i-1][j][1];
						
							if(val1>val2)
							{
								matrix[i][j][1] = val1;
								int [] arr = {i-1, j, 0};
								traverse[i][j][1] = arr;
								matrix[i][j][2] = 0;
							}
							else
							{
								matrix[i][j][1] = val2;
								int [] arr = {i-1, j, 1};
								traverse[i][j][1] = arr;
								matrix[i][j][2]=matrix[i-1][j][2];
							}
						
						
					}
					else
					{
						val1 = matrix[i-1][j][0] + Sequence.scoreGapOpen;

						if(matrix[i-1][j][2] == 1)
						{
							val2 = matrix[i-1][j][1] + Sequence.scoreGapExtension;
						}
						else
						{
							val2 = matrix[i-1][j][1] + Sequence.scoreGapOpen;
						}
						
						if(val1>val2)
						{
							matrix[i][j][1] = val1;
							int [] arr = {i-1, j, 0};
							traverse[i][j][1] = arr;
						}
						else
						{
							matrix[i][j][1] = val2;
							int [] arr = {i-1, j, 1};
							traverse[i][j][1] = arr;
						}
						
						matrix[i][j][2] = 1;
					}

				}
				else
				{
					matrix[i][j][1] = -99999;
				}  
				
				
			}

		}

		for(int i = 0; i <=crop.sizeInclBlanks();i++)
		
		{
			for(int j = 0; j <= cons.sizeInclBlanks(); j++)
			{
				try{
				System.out.print (matrix[j][i][0] + "," + matrix [j][i][1] + ",");
				}
				catch(NullPointerException e)
				{
					
				}
			}
			System.out.println("");
		}
		
		ArrayList<Residue> aligned = new ArrayList<Residue>();
		
		//for(int i = 0; i < )
		int max = 0;
		int maxsize = crop.sizeInclBlanks();
		int type = 1;
		
		if(matrix[cons.sizeInclBlanks()][maxsize][0] > matrix[cons.sizeInclBlanks()][maxsize][1])
			type = 0;
//		for(int i = 0; i < crop.size()+1; i ++)
//		{
//			if(matrix[cons.size()][i][0] >max)
//			{
//				max = matrix[cons.size()][i][0];
//				maxsize = i;
//				type = 0;
//			}
//			
//			if(matrix[cons.size()][i][1] > max)
//			{
//				max = matrix[cons.size()][i][1];
//				maxsize = i;
//				type = 1;
//			}
//		}
		
		if(type==0)
		{
			aligned.add(crop.get(crop.sizeInclBlanks()-1));
		}
		else
		{
			aligned.add(new Residue('-'));
		}
		int [] trace = traverse[cons.sizeInclBlanks()][maxsize][type];

		while(trace[1]!=0)
		{
			//System.out.println(trace[0] + " " + trace[1] + " " + trace[2]);
			if(trace[2]==0)
			{
				aligned.add(crop.get(trace[1]-1));
			}
			else
			{
				aligned.add(new Residue('-'));
			}
			int new0 = traverse[trace[0]][trace[1]][trace[2]][0];
			int new1 = traverse[trace[0]][trace[1]][trace[2]][1];
			int new2 = traverse[trace[0]][trace[1]][trace[2]][2];
			trace[0] = new0;
			trace[1] = new1;
			trace[2] = new2;

		}
		
		for(int i = 0; i < trace[0]; i ++)
		{
			aligned.add(new Residue('-'));
		}
		ArrayList<Residue> changedseq = new ArrayList<Residue>();
		for(int i = 0; i < endres - startres + 1; i++)
		{
			try
			{
				changedseq.add(aligned.get(aligned.size()-1-i));
				this.set(startres + i, aligned.get(aligned.size()-1 -i));
			}
			catch(Exception e)
			{
				this.set(startres + i, new Residue('-'));
				changedseq.add(new Residue('-'));
			}
		}
		
		Alignment.al.currentEdit.add(new AlignEdit( backup, changedseq, startres, this.seqnum));
		return matrix[cons.sizeInclBlanks()][maxsize][type];
		
	}
	
//	public int alignToConsensus2(int startres, int endres)
//	{
//
//		Sequence newcons = new Sequence("consensus", 9999999, al);
//		
//		int count;
//		Sequence newseq = new Sequence("conalign", 999999999,al);
//		int blanks = 0;
//		
//		for(int i = startres; i<= endres; i++)
//		{
//			newcons.add(consensus.get(i));
//			if(!this.get(i).isBlank())
//			{
//				blanks++;
//			}
//			else
//			{
//				newseq.add(this.get(i));
//				
//			}
//			
//		}
//		System.out.println("BLANKS!!! " + blanks);
//		int lastblank = -1;
//		int finalmax = 0;
//		for(int i = 0; i < blanks; i++)
//		{
//			Residue blank = new Residue('-');
//			int maxscore = -1000000;
//			int current = 0;
//			int blankpos = 0;
//			for(int j = lastblank+1;j <= newseq.size(); j++)
//			{
//				newseq.add(j, blank);
//				//OPTIMISE STARTPOS TODO
//				current = newseq.pairwiseSubset(newcons, 0, newseq.size()-1);
//				if(current>maxscore)
//				{
//					maxscore = current;
//					blankpos = j;
//				}
//				newseq.remove(j);
//			}
//			newseq.add(blankpos, blank);
//			System.out.println("NEWSEQ: "+ newseq);
//			finalmax = maxscore;
//		}
//		
//		for(int i = 0; i <newseq.size(); i++)
//		{
//			
//			this.set((i+startres),newseq.get(i));
//		}
//		
//		return finalmax;
//		
//		
//			
//		
//	}
	public void removePlain(int end) {
		super.remove(end);
		// TODO Auto-generated method stub
		
	}
	
	@Override public int size()
	{
//		boolean carryon = true;
		int end = super.size();
		
		while(end > 1)
		{
			if(this.get(end-1).getType() == Residue.ResidueType.BLANK)
			{
				end--;
			}
			else
			{
				break;
			}
		}
		
		return end;
	}
	
	public int sizeInclBlanks()
	{
		return super.size();
	}


    protected static void buildCodonTable ()
    {
        codonTable.put ("TTT", "F");
        codonTable.put ("TTC", "F");
        codonTable.put ("TTA", "L");
        codonTable.put ("TTG", "L");
        codonTable.put ("TCT", "S");
        codonTable.put ("TCC", "S");
        codonTable.put ("TCA", "S");
        codonTable.put ("TCG", "S");
        codonTable.put ("TAT", "Y");
        codonTable.put ("TAC", "Y");
        //            TAA end
        //            TAG end
        codonTable.put ("TGT", "C");
        codonTable.put ("TGC", "C");
        //            TGA end
        codonTable.put ("TGG", "W");
        codonTable.put ("CTT", "L");
        codonTable.put ("CTC", "L");
        codonTable.put ("CTA", "L");
        codonTable.put ("CTG", "L");
        codonTable.put ("CCT", "P");
        codonTable.put ("CCC", "P");
        codonTable.put ("CCA", "P");
        codonTable.put ("CCG", "P");
        codonTable.put ("CAT", "H");
        codonTable.put ("CAC", "H");
        codonTable.put ("CAA", "Q");
        codonTable.put ("CAG", "Q");
        codonTable.put ("CGT", "R");
        codonTable.put ("CGC", "R");
        codonTable.put ("CGA", "R");
        codonTable.put ("CGG", "R");
        codonTable.put ("ATT", "I");
        codonTable.put ("ATC", "I");
        codonTable.put ("ATA", "I");
        codonTable.put ("ATG", "M");
        codonTable.put ("ACT", "T");
        codonTable.put ("ACC", "T");
        codonTable.put ("ACA", "T");
        codonTable.put ("ACG", "T");
        codonTable.put ("AAT", "N");
        codonTable.put ("AAC", "N");
        codonTable.put ("AAA", "K");
        codonTable.put ("AAG", "K");
        codonTable.put ("AGT", "S");
        codonTable.put ("AGC", "S");
        codonTable.put ("AGA", "R");
        codonTable.put ("AGG", "R");
        codonTable.put ("GTT", "V");
        codonTable.put ("GTC", "V");
        codonTable.put ("GTA", "V");
        codonTable.put ("GTG", "V");
        codonTable.put ("GCT", "A");
        codonTable.put ("GCC", "A");
        codonTable.put ("GCA", "A");
        codonTable.put ("GCG", "A");
        codonTable.put ("GAT", "D");
        codonTable.put ("GAC", "D");
        codonTable.put ("GAA", "E");
        codonTable.put ("GAG", "E");
        codonTable.put ("GGT", "G");
        codonTable.put ("GGC", "G");
        codonTable.put ("GGA", "G");
        codonTable.put ("GGG", "G");
        //TODO add U
        for(int i = 4; i < 16; i++)
        {
            HashSet<Integer> hs = new HashSet<Integer>();
            for(int j = 0; j < Residue.ambiguityMap.get(i).size();j++)
            {
                
            }
        }



    }

	

}
