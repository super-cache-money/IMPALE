package gui;

import java.util.*;

/**
 * Created by arjun on 2014/06/27.
 */
public class SimilarEngine {


    static boolean firstExtend = true;
    static int startRes = -1;
    static int endRes = -1;
    static int seq = -1;
    static int similarSeqsFound;
    static double similarBound = 0.9;
    static double similarJump = 0.05;
    static double startingSimilarJump = 0.05;
    static double startingSimilarBound = 0.9;
    static int maxscrolls = 30;


/*
      increasing/decreasing sets whether selection will be increased/decreased if there are no similar seqs.
*/
    public static void selectSimilarMain(boolean increasing)
    {
        if(firstExtend)
        {
            boolean extendSuccess = firstExtendCache();
            if(!extendSuccess)
                return;

        }
        else
        {
            System.out.println("similarScrolling...");
        }
        int oldSimilarFound = similarSeqsFound;
        HashSet<Integer> similarSeqs = getSimilarSequences();
        while(oldSimilarFound==similarSeqsFound)
        {
            if(increasing)
            {
                SimilarEngine.similarBound = SimilarEngine.similarBound-SimilarEngine.startingSimilarBound/maxscrolls;
                SimilarEngine.similarJump=SimilarEngine.similarJump-SimilarEngine.startingSimilarJump/maxscrolls;
            }
            else
            {
                SimilarEngine.similarBound = SimilarEngine.similarBound+SimilarEngine.startingSimilarBound/maxscrolls;
                SimilarEngine.similarJump=SimilarEngine.similarJump+SimilarEngine.startingSimilarJump/maxscrolls;
            }

            similarSeqs = getSimilarSequences();
            System.out.println("old " + oldSimilarFound + " new " + similarSeqsFound);
            if(increasing)
            {
                if(oldSimilarFound==Alignment.al.size())
                {
                    break;
                }
            }
            if(!increasing)
            {

                if(oldSimilarFound<=1 && similarSeqsFound<=1)
                {
                    break;
                }

            }
        }
        Alignment.al.panel.canvas.viewport.selected.clear();
        for(Integer seq: similarSeqs)
        {
            for(int i = startRes; i <= endRes; i++)
            {
                Alignment.al.panel.canvas.viewport.selected.add(new ResiduePos(i,seq));
            }
        }
        Alignment.al.helpText.setText("You have extended your selection to similar sequences. This is useful for co-editing similar sequences. Hold \"S\" and scroll to alter sensitivity. Sequences selected: " + similarSeqsFound);
        Alignment.al.changed.add(new ResiduePos(0,0));
        Alignment.al.panel.canvas.repaint();
    }

/*
uses startRes, endRes, and seq fields.
 */
    public static HashSet<Integer> getSimilarSequences()
    {

        similarSeqsFound = 0;
        Alignment.al.urt.rushEditToQueue();
        if(!Alignment.al.scoreal.upToDate.get())
        {
            Alignment.al.helpText.setText("IMPALE is busy scoring the alignment! Wait for the loading indicator over to the right to stop, before trying this again.");
            return new HashSet<Integer>();
        }
        PairwiseInfo.compareToSeq = seq;
        Vector<PairwiseInfo> similarities = new Vector<PairwiseInfo>();




        for(int i = 0; i < Alignment.al.size(); i++)
        {
            int tot = Alignment.al.get(seq).getPairwiseScoreWith(Alignment.al.get(i),startRes,endRes);
//			System.out.println("tot" + i + " " + tot);
            similarities.add(new PairwiseInfo(i, tot));
        }

        Collections.sort(similarities);
        HashSet<Integer> returnSet = new HashSet<Integer>();
        //System.out.println(similarities);
        for(int i = 0 ; i < similarities.size(); i++)
        {
//			System.out.println(similarities.get(i).pairwiseScore);
        }

        int range = similarities.get(similarities.size()-1).pairwiseScore - similarities.get(0).pairwiseScore;
        double minbound = similarities.get(similarities.size()-1).pairwiseScore - (range + 0.0) * (1-similarBound);
        double minjump = (range + 0.0)*similarJump;
        System.out.println("minbound:" + minbound + " minjump:" + minjump);
        boolean jumpMet = false;

        for(int i = 1; i < similarities.size(); i ++)
        {
            if(i==similarities.size()-1)
                i=i;
            if(!jumpMet)
            {
                if(similarities.get(i).pairwiseScore>=minbound)
                {
                    if((similarities.get(i).pairwiseScore-similarities.get(i-1).pairwiseScore)>= minjump)
                    {
                        jumpMet = true;
                        returnSet.add((similarities.get(i).seq));

                    }//returns the viewmap sequence numbers so that selection may be painting on screen.

                }

            }
            else
            {

                returnSet.add((similarities.get(i).seq));
            }
        }

        if(similarities.get(0).pairwiseScore>=minbound && (returnSet.size()==Alignment.al.size()-1)) {
            returnSet.add(similarities.get(0).seq);
        }
        returnSet.add(seq);
        similarSeqsFound = returnSet.size();

        return returnSet;


    }

    //returns false if there is more than one sequence in the selection, or something fucks up
    public static boolean firstExtendCache()
    {

        similarSeqsFound=1;
        Iterator<ResiduePos> it = Alignment.al.panel.canvas.viewport.selected.iterator();
        similarJump=startingSimilarJump;
        similarBound=startingSimilarBound;
        if(!it.hasNext())
        {
            Alignment.al.helpText.setText("You haven't selected anything.");
            return false;
        }


        ResiduePos first = it.next();
        int newStartRes=first.location[0];
        int newSeq = first.location[1];
        int newEndRes = -1;
        while(it.hasNext())
        {
            ResiduePos curr = it.next();
            if(curr.location[1]!=newSeq)
            {
                Alignment.al.helpText.setText("You can't extend a selection of more than one sequence");
                return false;
            }
            newEndRes = curr.location[0];
        }
        firstExtend =false;
        endRes = newEndRes;
        startRes = newStartRes;
        seq = newSeq;
        return true;
    }


}

