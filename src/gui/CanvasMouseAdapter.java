package gui;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public class CanvasMouseAdapter extends MouseAdapter{
	boolean firstClick;
	boolean dontPermitResDrag = false;
	ResiduePos draggingLastRes;
	int resdragdir = 0;
	ResiduePos resSave = null;
	boolean aligning = false;
	boolean cleartext = true;
	boolean resdrag = false;
	boolean selectionBlacklisted = false;
	MouseEvent lastdragMouseEvent;
	EdgeTimerTask edgetimer;
	ResiduePos lastresdragpos = null;
	Timer timer;
	TreeSet<ResiduePos> selectedShift;
	TreeSet<ShiftingLeftResiduePos> selectedCopy;
	TreeSet<ShiftingRightResiduePos> selectedCopyright;
	boolean firstDrag = true;
	public CanvasMouseAdapter(PaintingPanel panel)
	{
		this.canvas = panel.canvas;
		timer = new Timer();
		edgetimer = new EdgeTimerTask();
		this.viewport = canvas.viewport;
		firstClick = true;
		this.tp = panel.topPanel;
		dragging =false;
		findingSimilar = false;
		this.pp = panel;
		lastdragMouseEvent = null;

	}

	PaintingPanel pp;
	TopPanel tp;
	PaintingCanvas canvas;
	boolean dragging;
	Set<Residue> dragselect;
	int startx, starty, prevx,prevy;
	ResiduePos startres, lastres;
	PaintingViewport viewport;
	Sequence align;
	boolean findingSimilar;
	Set<ResiduePos> ctrlResidues;
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		//		e.get
		if(e.isControlDown() && dragging == false)
		{
			int notches = e.getWheelRotation();
			int current = canvas.font2.getSize() - notches;
			if(current<4)
			{
				Alignment.al.helpText.setText("You cannot zoom out any more!");


			}
			else if(current>18)
			{
				Alignment.al.helpText.setText("Maybe you should get your eyes checked");
			}
			else
			{
                canvas.viewport.zoomToFontSize(current);
			}



        }
		else if(canvas.isKeyPressed.get("s"))
		{


                int notches = e.getWheelRotation();

            if(SimilarEngine.firstExtend ==true)
			{
                if(notches>0)
                {
//                    SimilarEngine.firstExtendCache();

                    //just to start at the right point
//                    SimilarEngine.similarBound = SimilarEngine.similarBound+SimilarEngine.startingSimilarBound/SimilarEngine.maxscrolls;
//                    SimilarEngine.similarJump=SimilarEngine.similarJump+SimilarEngine.startingSimilarJump/SimilarEngine.maxscrolls;
                }
                else
                {
                    //why is the user scrolling this way?
                    return;
                }
            }

            //shreeya 074 421 9318
            int oldsize = viewport.selected.size();
            int newsize = oldsize;


            //			viewport.selected = selectedShift;
			outerloop:
				while(notches!=0)
				{
					if(notches>0)
					{
                        SimilarEngine.selectSimilarMain(true);
						notches--;
					}
					if(notches<0)
					{
                       SimilarEngine.selectSimilarMain(false);
						notches++;
					}
				}
			//			System.out.println(Alignment.al.similarBound +" jump "+Alignment.al.similarJump);
			//			for(int i = 0; i < notches; i++)
			//			{
			//				Alignment.al.similarBound = Alignment.al.similarBound*Math.pow((5.0)/6.0,notches);
			//				Alignment.al.similarJump = Alignment.al.similarJump*Math.pow((5.0)/6.0,notches);
			//				pp.menu.similarSelectAction.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,""));
			//			
			//			}
			//
			////			System.out.println("scrollin "+notches + ":" + Alignment.al.similarBound + Alignment.al.similarJump);
			//			Alignment.al.similarBound = Alignment.al.similarBound*Math.pow((5.0)/6.0,notches);
			//			Alignment.al.similarJump = Alignment.al.similarJump*Math.pow((5.0)/6.0,notches);
			//			pp.menu.similarSelectAction.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,""));
		}
		else
		{
			if(e.isShiftDown())
			{
				int notches = e.getWheelRotation();
				int newpos = viewport.jsbh.getValue() + notches;
				if(newpos < 0)
					newpos = 0;
				else if (newpos>=Alignment.al.longestSeq-viewport.width+1)
					newpos = Alignment.al.longestSeq - viewport.width;

				viewport.jsbh.setValue(newpos);

				
			}
			else
			{
				int notches = e.getWheelRotation();
				int newpos = viewport.jsbv.getValue() + notches;
				if(newpos < 0)
					newpos = 0;
				else if (newpos>=Alignment.al.size()-viewport.height+1)
					newpos = Alignment.al.size() - viewport.height;

				viewport.jsbv.setValue(newpos);
//				Alignment.al.changed.add(new ResiduePos(0,0));
//				canvas.repaint();
			}
			Alignment.al.changed.add(new ResiduePos(0,0));
			canvas.repaint();
		}
	}
	//    public void mouseWheelMoved(MouseWheelEvent e) {
	//        String message;
	//        int notches = e.getWheelRotation();
	//        if (notches < 0) {
	//            message = "Mouse wheel moved UP "
	//                         + -notches + " notch(es)" + newline;
	//        } else {
	//            message = "Mouse wheel moved DOWN "
	//                         + notches + " notch(es)" + newline;
	//        }
	//        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
	//            message += "    Scroll type: WHEEL_UNIT_SCROLL" + newline;
	//            message += "    Scroll amount: " + e.getScrollAmount()
	//                    + " unit increments per notch" + newline;
	//            message += "    Units to scroll: " + e.getUnitsToScroll()
	//                    + " unit increments" + newline;
	//            message += "    Vertical unit increment: "
	//                + scrollPane.getVerticalScrollBar().getUnitIncrement(1)
	//                + " pixels" + newline;
	//        } else { //scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL
	//            message += "    Scroll type: WHEEL_BLOCK_SCROLL" + newline;
	//            message += "    Vertical block increment: "
	//                + scrollPane.getVerticalScrollBar().getBlockIncrement(1)
	//                + " pixels" + newline;
	//        }
	//        saySomething(message, e);
	//     }
	//     ...
	// }
	@Override
	public void mousePressed(MouseEvent e)
	{
        SimilarEngine.firstExtend =true;
		//System.out.println(e.getModifiersExText(e.getButton()));
		if(e.isPopupTrigger())
		{
			popUp(e);
			return;
		}
		if (!((e.getModifiersEx() & (MouseEvent.BUTTON1_DOWN_MASK)) == MouseEvent.BUTTON1_DOWN_MASK)) {
			return;
		}

		//		System.out.println("CLICKED HERE:" + e.getPoint());
		edgetimer = new EdgeTimerTask();
		timer.scheduleAtFixedRate(edgetimer, 100, 50);
		canvas.mousePressedSinceLastKey=true;
		if(tp.lsort)
		{

			startres = getResiduePosAtPoint(e.getPoint());
			//			ScoreBlock block = Alignment.al.sb.getBlock(startres.location[0]);
			//			ScoreBlock block = new ScoreBlock(Alignment.al.getBlockStart(startres.location[0]), Alignment.al.getBlockEnd(startres.location[0]), Alignment.al);
			Alignment.al.urt.rushEditToQueue();
			if(Alignment.al.scoreal.upToDate.get())
			{
				ScoringAlignment.StickyScoreBlock block = Alignment.al.scoreal.getBlockAt(startres.location[0]);

				Alignment.al.reorderAccordingTo(Alignment.al.viewmap.get(startres.location[1]), block.scores);
				tp.lsort = false;
			}
			else
			{
				//				JOptionPane.showMessageDialog( null, "Hello");
				Alignment.al.helpText.setText("IMPALE is busy scoring the alignment! Wait for the loading indicator over to the right to stop, before trying this again.");
				cleartext = false;
			}


		}
		else if(tp.gsort)
		{
			startres = getResiduePosAtPoint(e.getPoint());
			Alignment.al.urt.rushEditToQueue();
			if(Alignment.al.scoreal.upToDate.get())
			{
				Alignment.al.reorderAccordingTo(Alignment.al.viewmap.get(startres.location[1]), Alignment.al.scoreal.netscore);
				tp.gsort = false;
			}
			else
			{
				cleartext = false;
				Alignment.al.helpText.setText("IMPALE is busy scoring the alignment! Wait for the loading indicator over to the right to stop, before trying this again.");
			}

		}

		if(canvas.isKeyPressed.get("s"))
		{
			findingSimilar = true;
			startres = getResiduePosAtPoint(e.getPoint());
			Alignment.al.helpText.setText("Any similar sequences within this block will have their residues added to the selection once you release the mouse button. To change sensitivity, hold \"S\" and scroll with mousewheel.");

		}


		if(canvas.isKeyPressed.get("a"))
		{
			viewport.selected.clear();
			Alignment.al.helpText.setText("Select a region to be aligned to the consensus. If you select a single residue of a sequence, the entire region of the sequence within the encapsulating block will be aligned.");
			aligning = true;

		}
		startres = getResiduePosAtPoint(e.getPoint());
		draggingLastRes = getResiduePosAtPoint(e.getPoint());
		if(viewport.selected.contains(startres))
		{
			resdrag = true;
			lastresdragpos=startres;
			if(Alignment.al.stickyMode)
			{

			}
			resSave =startres;
			dragging = false;
		}
		else
		{

			if(e.isControlDown())
			{
				ctrlResidues = new HashSet<ResiduePos>();
				ctrlResidues.addAll(viewport.selected);
				//			ctrlResidues = (Set<ResiduePos>) ((TreeSet<ResiduePos>) viewport.selected).clone();
			}
			else
			{
				viewport.selected.clear();
				ctrlResidues = new HashSet<ResiduePos>();

			}
			dragging = false;
			viewport.selected.add(startres);
			canvas.redrawArea(0, 0, viewport.startres, viewport.startseq, viewport.width, viewport.height);
			canvas.repaint();
			lastres = startres;
			draggingLastRes = new ResiduePos(startres.location[0],startres.location[1]);

			//viewport.selected.add(startres);


			//		startx = e.getX();
			//		starty = e.getY();
			//		prevx = startx;
			//		prevy = starty;
		}

	}
	public void toggle(ResiduePos rp)
	{
		if(ctrlResidues.contains(rp))
			return;
		if(!viewport.selected.remove(rp))
			viewport.selected.add(rp);
	}

	public void selectDrag(ResiduePos newres)
	{

		if(newres.location[0]-draggingLastRes.location[0] < 0)
		{
			int nopoint = 5;
		}
		int dminx = Math.min(newres.location[0], startres.location[0]);
		int dminy = Math.min(newres.location[1], startres.location[1]);
		int dmaxx = Math.max(newres.location[0], startres.location[0]);
		int dmaxy = Math.max(newres.location[1], startres.location[1]);
		if(newres.location[0]>draggingLastRes.location[0])
		{
			//			viewport.selected.
		}
		else if(newres.location[0]>draggingLastRes.location[0])
		{

		}

		Rectangle newrect = new Rectangle(dminx,dminy,dmaxx-dminx+1,dmaxy-dminy+1);
		dminx = Math.min(draggingLastRes.location[0], startres.location[0]);
		dminy = Math.min(draggingLastRes.location[1], startres.location[1]);
		dmaxx = Math.max(draggingLastRes.location[0], startres.location[0]);
		dmaxy = Math.max(draggingLastRes.location[1], startres.location[1]);
		Rectangle oldrect = new Rectangle(dminx,dminy,dmaxx-dminx+1,dmaxy-dminy+1);

		Rectangle intersectRect = oldrect.intersection(newrect);
		Rectangle oldsub = subtractRectangles(oldrect, intersectRect);
		Rectangle newsub = subtractRectangles(newrect, intersectRect);

		for(int x= oldsub.x; x < oldsub.width+oldsub.x; x++)
			for(int y =oldsub.y; y < oldsub.height+oldsub.y;y++)
			{
				toggle(new ResiduePos(x,y));
			}
		for(int x= newsub.x; x < newsub.width+newsub.x; x++)
			for(int y =newsub.y; y < newsub.height+newsub.y;y++)
			{
				toggle(new ResiduePos(x,y));
			}
		//Alignment.al.panel.canvas.redrawArea(lastres-Alignment.al.panel.canvas.viewport.startres, 0, lastres, Alignment.al.panel.canvas.viewport.startseq, startselect - lastres, Alignment.al.panel.canvas.viewport.height) ;

		canvas.redrawArea(newsub.x-Alignment.al.panel.canvas.viewport.startres ,newsub.y - Alignment.al.panel.canvas.viewport.startseq,newsub.x,newsub.y,newsub.width,newsub.height);
		//		canvas.redrawArea(getXofRes(oldsub.x),getYofSeq(oldsub.y),oldsub.x,oldsub.y,oldsub.width,oldsub.height);
		canvas.redrawArea(oldsub.x-Alignment.al.panel.canvas.viewport.startres ,oldsub.y - Alignment.al.panel.canvas.viewport.startseq,oldsub.x,oldsub.y,oldsub.width,oldsub.height);
		draggingLastRes = new ResiduePos(newres.location[0],newres.location[1]);
	}
	@Override 
	public void mouseDragged(MouseEvent e)
	{

		ResiduePos newres = getResiduePosAtPoint(e.getPoint());
		int b1 = MouseEvent.BUTTON1_DOWN_MASK;
		int b2 = MouseEvent.BUTTON2_DOWN_MASK;
		if (!((e.getModifiersEx() & (b1)) == b1)) {
			return;
		}
		dragging = true;
		if (resdrag)//(resdrag)
		{
			//			System.out.println("resdraggin");
			resDrag(e);

		}
		else if (!newres.equals(draggingLastRes))
		{
			//			rect.
			//		System.out.println("dragging");
			//		viewport.selected = new HashSet<ResiduePos>();

			int minx = -99999;


			if(newres.location[0]>=Alignment.al.longestSeq)
			{
				newres.location[0]=Alignment.al.longestSeq-1;
				//			Alignment.al.helpText.setText("You may only drag-select residues downwards and to the right. For more flexibility expanding your selection, hold shift and use the arrow keys.");
			}

			if(newres.location[1]>=Alignment.al.size())
			{
				newres.location[1]=Alignment.al.size() -1;
				//			Alignment.al.helpText.setText("You may only drag-select residues downwards and to the right. For more flexibility expanding your selection, hold shift and use the arrow keys.");
			}

			if(newres.location[1] < 0)
				newres.location[1] = 0;
			if (newres.location[0]< 0)
				newres.location[0] = 0;

			if(newres.location[0]!=draggingLastRes.location[0])
			{
				selectDrag(new ResiduePos(newres.location[0],draggingLastRes.location[1]));
			}
			if(newres.location[1]!=draggingLastRes.location[1])
			{
				selectDrag(new ResiduePos(draggingLastRes.location[0],newres.location[1]));
			}


			//		for(int x = minx; x <=maxx;x++)
			//		for(int y = miny; y<= maxy; y++)
			//		{
			//			viewport.selected.add(new ResiduePos(x,y));
			//		}
			viewport.selected.addAll(ctrlResidues);
			//		Alignment.al.changed.add(new ResiduePos(0,0));

			canvas.repaint();

		}
		lastdragMouseEvent = e;
	}

	//	@Override 
	//	public void mouseDragged(MouseEvent e)
	//	{
	//		dragging = true;
	//		ResiduePos newres = getResiduePosAtPoint(e.getPoint());
	//		TreeSet<ResiduePos> oldselected = (TreeSet<ResiduePos>) viewport.selected.clone();
	//		viewport.selected.clear();
	//		int minx = -99999;
	//		if(newres.location[0]>startres.location[0])
	//			minx = startres.location[0];
	//		else
	//			minx = newres.location[0];
	//		
	//		int miny = -99999;
	//		if(newres.location[1]>startres.location[1])
	//			miny = startres.location[1];
	//		else
	//			miny = newres.location[1];
	//		
	//		int maxy = -99999;
	//		if(newres.location[1]<startres.location[1])
	//			maxy = startres.location[1];
	//		else
	//			maxy = newres.location[1];
	//		
	//		int maxx = -99999;
	//		if(newres.location[0]<startres.location[0])
	//			maxx = startres.location[0];
	//		else
	//			maxx = newres.location[0];
	//		
	//		for(int x = minx; x <=maxx;x++)
	//			for(int y = miny; y<= maxy; y++)
	//			{
	//				viewport.selected.add(new ResiduePos(x,y));
	//			}
	//		
	//		viewport.selected.addAll(ctrlResidues);
	//		TreeSet<ResiduePos> oldselected2 = (TreeSet<ResiduePos>) viewport.selected.clone();
	//		oldselected.removeAll(viewport.selected);
	//		oldselected2.removeAll(oldselected);
	//		
	//		oldselected.addAll(oldselected2);
	//		
	//		for(ResiduePos rp : oldselected)
	//		{
	//			redrawArea(rp,rp);
	//		}
	//		
	//		canvas.repaint();
	//		
	//		
	//		
	//		
	//	}


	//	@Override
	//	public void mouseDragged(MouseEvent e)
	//	{
	//		
	//		dragging = true;
	//		ResiduePos newres = getResiduePosAtPoint(e.getPoint());
	//		if(newres.location[0]<startres.location[0])
	//		{
	//			newres.location[0]=startres.location[0];
	//			Alignment.al.helpText.setText("You may only drag-select residues downwards and to the right. For more flexibility expanding your selection, hold shift and use the arrow keys.");
	//		}
	//		
	//		if(newres.location[1]<startres.location[1])
	//		{
	//			newres.location[1]=startres.location[1];
	//			Alignment.al.helpText.setText("You may only drag-select residues downwards and to the right. For more flexibility expanding your selection, hold shift and use the arrow keys.");
	//		}
	//		
	//		if(newres.location[0]>=Alignment.al.longestSeq)
	//		{
	//			newres.location[0]=Alignment.al.longestSeq-1;
	//			Alignment.al.helpText.setText("You may only drag-select residues downwards and to the right. For more flexibility expanding your selection, hold shift and use the arrow keys.");
	//		}
	//		
	//		if(newres.location[1]>=Alignment.al.size())
	//		{
	//			newres.location[1]=Alignment.al.size() -1;
	//			Alignment.al.helpText.setText("You may only drag-select residues downwards and to the right. For more flexibility expanding your selection, hold shift and use the arrow keys.");
	//		}
	//		
	////		if(newres.location[1] == viewport.endseq && newres.location[1]<Alignment.al.size()-1)
	////		{
	////			viewport.jsbv.setValue(viewport.jsbv.getValue()+1);
	////			newres.location[1]++;
	////		}
	////		if(newres.location[0] == viewport.endres && newres.location[1]<Alignment.al.longestSeq)
	////		{
	////			viewport.jsbh.setValue(viewport.jsbh.getValue()+1);
	////			newres.location[0]++;
	////		}
	//		
	//		if (lastres.equals(newres))
	//		{
	//			//System.out.println("new residue");
	//		}
	//		else if (lastres.location[0]>newres.location[0] && newres.location[0] > startres.location[0])
	//		{
	//			for(int i = startres.location[1]; i <= lastres.location[1]; i++)
	//			{
	//				for(int j = newres.location[0] + 1; j <= lastres.location[0]; j ++)
	//				{
	//				viewport.selected.remove(new ResiduePos(j,i));
	//				}
	//				
	//			}
	//			ResiduePos s = new ResiduePos(newres.location[0] + 1, startres.location[1]);
	//		
	//			redrawArea(s, lastres);
	//			//redrawArea();
	//		}
	//		else if  (lastres.location[0]>newres.location[0] && newres.location[0] < startres.location[0])
	//		{
	//			//System.out.println(viewport.selected);
	//			for(int i = startres.location[1]; i <= newres.location[1]; i++)
	//				for(int j = lastres.location[0]+1; j <=newres.location[0]; j ++)
	//			{
	//				
	//				viewport.selected.add(new ResiduePos (j,i));
	//				
	//			}
	//			ResiduePos s = new ResiduePos(lastres.location[0]+1, startres.location[1]);
	//			//System.out.println(startres + " " + newres);
	//			redrawArea(s, newres);
	//			//viewport.selected.
	//		}
	//		else if (lastres.location[0]<newres.location[0])
	//		{
	//			//System.out.println(viewport.selected);
	//			for(int i = startres.location[1]; i <= newres.location[1]; i++)
	//				for(int j = lastres.location[0]+1; j <=newres.location[0]; j ++)
	//			{
	//				
	//				viewport.selected.add(new ResiduePos (j,i));
	//				
	//			}
	//			ResiduePos s = new ResiduePos(lastres.location[0]+1, startres.location[1]);
	//			//System.out.println(startres + " " + newres);
	//			redrawArea(s, newres);
	//			//viewport.selected.
	//		} 
	//		if (lastres.location[1]>newres.location[1])
	//		{
	//			for(int i = startres.location[0]; i <= lastres.location[0] ; i++)
	//				for(int j = newres.location[1] + 1; j <= lastres.location[1]; j ++)
	//			{
	//				viewport.selected.remove(new ResiduePos(i,j));
	//			}
	//			ResiduePos s = new ResiduePos(startres.location[0], newres.location[1]+1);
	//			redrawArea(s, lastres);
	//		}
	//		else if (lastres.location[1]<newres.location[1])
	//		{
	//			for(int i = startres.location[0]; i <= newres.location[0] ; i++)
	//				for(int j = lastres.location[1]+ 1; j <= newres.location[1]; j++)
	//			{
	//				viewport.selected.add(new ResiduePos(i,j));
	//			}
	//			ResiduePos s = new ResiduePos(startres.location[0], lastres.location[1]+1);
	//			redrawArea(s, newres);
	//		}
	//		
	//			
	//				
	//		lastres = newres;
	//		prevx = e.getX();
	//		prevy = e.getY();
	//		
	//	}


	public void redrawArea(ResiduePos start, ResiduePos end)
	{
		int startrespos = start.location[0] - viewport.startres;
		int startseqpos = start.location[1] -viewport.startseq;
		canvas.redrawArea(startrespos, startseqpos, start.location[0], start.location[1], end.location[0]-start.location[0] + 1, end.location[1] - start.location[1] + 1);
		canvas.repaint();

	}
	@Override
	public void mouseMoved(MouseEvent e)
	{
		if(pp.movecursor)
		{
			pp.movecursor = false;
			pp.setCursor(Cursor.getDefaultCursor());
		}
	}
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if(viewport.selected instanceof HashSet)
		{
			TreeSet<ResiduePos> treecopy = new TreeSet<ResiduePos>();
			treecopy.addAll(viewport.selected);
			viewport.selected = treecopy;
		}
		firstDrag = true;
		dontPermitResDrag = false;
		selectionBlacklisted = false;
		pp.stopcursor = false;
		pp.grabcursor = false;
		pp.setCursor(Cursor.getDefaultCursor());
		resdragdir = 0;
		selectedCopy = null;
		//		dragging = false;
		if(e.isPopupTrigger())
		{
			popUp(e); //Right click
			return;
		}
		canvas.mousePressedSinceLastKey=true;
		Point p = e.getPoint();
		ResiduePos pos = this.getResiduePosAtPoint(p);
		System.out.println("ENDED");
		edgetimer.cancel();
		if(resdrag)
		{
			Iterator<ResiduePos> it = viewport.selected.iterator();
			while(it.hasNext())
			{
				ResiduePos rp = it.next();
				Alignment.al.currentEdit.currentEndingSelected.add(new ResiduePos(rp.location[0],Alignment.al.viewmap.get(rp.location[1])));

			}

			Alignment.al.ct.stalledUpdate(pos.location[0]-Alignment.al.panel.canvas.viewport.startres);
		}
		if(resdrag&&!dragging)
		{
			System.out.println("Reached");
			viewport.selected.clear();
			ctrlResidues = new HashSet<ResiduePos>();


			dragging = false;
			viewport.selected.add(startres);
			canvas.redrawArea(0, 0, viewport.startres, viewport.startseq, viewport.width, viewport.height);
			canvas.repaint();
			lastres = startres;

		}
		resdrag = false;
		if (dragging ==false)
		{

			//System.out.println(getResiduePosAtPoint(p));
			int seq = viewport.startseq + p.y/viewport.fontHeight;
			int res = viewport.startres + p.x/viewport.fontWidth;
			applySelection(res, seq, 1, 1, p);
			canvas.repaint();
		}
		if(aligning)
		{
			aligning = false;
			//			if(Alignment.al.netBlock.busy==true) not necessary since scoring has been separated. 
			//			{
			//				JOptionPane.showMessageDialog(null, "Please wait for the score to be computed before editing the alignment.");
			//				return;
			//			}
			//			else 
			Alignment.al.alignSelectedRegion();

			dragging = false;
		}
		if(findingSimilar)
		{

			cleartext = false;
			System.out.println("findingSIMILAR");
			//ScoreBlock sb = Alignment.al.sbm.getBlock(pos.location[0]);
			SimilarEngine.selectSimilarMain(true);
			//			HashMap<Integer, Vector<Integer>> map = new HashMap<Integer, Vector<Integer>>();
			//			TreeSet<ResiduePos> selectedcopy = (TreeSet<ResiduePos>) viewport.selected.clone();
			//			for(ResiduePos entry : selectedcopy)
			//			{
			//				Vector<Integer> similarseqs;
			//				if(!map.containsKey(entry.location[1]))
			//				{
			//					similarseqs = Alignment.al.getSimilarSequences(entry.location[1], Alignment.al.netBlock.blockVector.get(entry.location[0]));
			//					map.put(entry.location[1], similarseqs );
			//				}
			//				else
			//				{
			//					similarseqs = map.get(entry.location[1]);
			//				}
			//				
			//				for(int i = 0; i < similarseqs.size(); i++)
			//				{
			//					viewport.selected.add(new ResiduePos(entry.location[0],similarseqs.get(i)));
			//				}
			//			
			//					
			//			}


		}
		if(cleartext)
			Alignment.al.helpText.setText("");
		else
			cleartext = true;
		dragging = false;

	}
	public ResiduePos getResiduePosAtPoint(Point p)
	{
		int seq = viewport.startseq + p.y/viewport.fontHeight;
		int res = viewport.startres + p.x/viewport.fontWidth;
		return new ResiduePos(res, seq);

	}

	public int getYofSeq(int s)
	{
		if(s>viewport.startseq)
		{
			return (s-viewport.startseq)*viewport.fontHeight;

		}
		return 9999;
	}

	public int getXofRes(int s)
	{
		if(s>viewport.startres)
		{
			return (s-viewport.startseq)*viewport.fontWidth;
		}
		return 9999;
	}

	public Rectangle subtractRectangles(Rectangle fromRect, Rectangle subRect)
	{
		int minx = fromRect.x-subRect.x;
		int miny = fromRect.y-subRect.y;
		int maxy = miny+fromRect.height - subRect.height;
		int maxx = minx + fromRect.width - subRect.width;

		int startx = 99999; int starty = 99999;int endx = 99999; int endy = 99999;
		if(minx!=0)
		{
			startx = fromRect.x;
			endx = subRect.x;

		}
		else if(maxx!=0)
		{
			startx = subRect.x + subRect.width;
			endx = fromRect.x + fromRect.width;
		}
		else if(maxy!=0 || miny!=0)
		{
			startx = fromRect.x;
			endx = startx + fromRect.width;
		}
		else
		{
			startx =0;
			starty =0;
			endx = 0;
			endy = 0;

		}
		if(miny!=0)
		{
			starty = fromRect.y;
			endy = subRect.y;

		}
		else if(maxy!=0)
		{
			starty = subRect.y + subRect.height;
			endy = fromRect.y+fromRect.height;
		}
		else if(maxx!=0 || minx!=0)
		{
			starty = fromRect.y;
			endy = starty + fromRect.height;
		}

		return new Rectangle(startx,starty,endx-startx, endy-starty);



	}
	public void applySelection(int x, int y, int width, int height, Point p)
	{
		for(int i = 0; i < height; i ++)
		{
			for(int j = 0; j < width; j++)
			{
				//viewport.selectedCells.add(new Area(rect));
				viewport.selected.add(new ResiduePos(x+j, y+i));




			}
		}

		//		System.out.println(width + " AND " + height);
		//		Graphics2D gfx = (Graphics2D) canvas.bimg.getGraphics();
		//		Shape oldclip = gfx.getClip();
		//		//Rectangle rect = new Rectangle(getXofRes(x),);
		//		gfx.setClip(getXofRes(x), getYofSeq(y), width*viewport.fontWidth, height*viewport.fontHeight);
		//		gfx.setColor(Color.BLACK);
		//		gfx.fillRect(0, 0, 9999, 9999);
		//		gfx.setClip(oldclip);
		//canvas.repaint();
		Alignment.al.helpText.setText("");
		if(firstClick)
		{
			firstClick = false;
			Alignment.al.helpText.setText("Note the bold-text areas of the alignment. These respresent well conserved areas (Sticky Blocks). If you only want to edit the blocks in between, maybe you should try Sticky Mode.");
		}
		redrawArea(startres, getResiduePosAtPoint(p));
		//System.out.println(x + " " + y + " ")

		//canvas.redrawArea(getResidueAtPoint(p).location[0]-viewport.startres, getResidueAtPoint(p).location[1] - viewport.startseq,x, y, width, height);
	}

	class EdgeTimerTask extends TimerTask
	{

		@Override
		public void run() {


			if(dragging || Alignment.al.panel.nhma.dragging)
			{
				Point p = MouseInfo.getPointerInfo().getLocation().getLocation();
				if (Alignment.al.panel.nhma.dragging)
					p = new Point(p.x-Alignment.al.panel.headers.rh.getWidth(), viewport.startseq+1);
				else
					p = new Point(p.x-Alignment.al.panel.headers.rh.getWidth(), p.y - Alignment.al.panel.headers.tch.getHeight() - Alignment.al.panel.topPanel.getHeight() - Alignment.al.panel.menu.getHeight());
				ResiduePos newres = getResiduePosAtPoint(p);
				if(newres.location[0]>=Alignment.al.longestSeq)
				{
					newres.location[0]=Alignment.al.longestSeq-1;
					//					Alignment.al.helpText.setText("You may only drag-select residues downwards and to the right. For more flexibility expanding your selection, hold shift and use the arrow keys.");
				}

				if(newres.location[1]>=Alignment.al.size())
				{
					newres.location[1]=Alignment.al.size() -1;
					//					Alignment.al.helpText.setText("You may only drag-select residues downwards and to the right. For more flexibility expanding your selection, hold shift and use the arrow keys.");
				}

				if(newres.location[1] < 0)
					newres.location[1] = 0;

				if (newres.location[0]< 0)
					newres.location[0] = 0;

				boolean repaint = false;

				if(newres.location[1] >= viewport.endseq && viewport.endseq<Alignment.al.size()-1)
				{

					newres.location[1]++;
					repaint = true;
					Runnable r = new Runnable(){

						@Override
						public void run() {

							viewport.jsbv.setValue(viewport.jsbv.getValue()+1);
							//viewport.jsbh.setValue(viewport.jsbh.getValue()+1);
							// TODO Auto-generated method stub

						}

					};
					Alignment.al.changed.add(new ResiduePos(0,0));
					SwingUtilities.invokeLater(r);
				}
				if(newres.location[0] >= viewport.endres && viewport.endres<Alignment.al.longestSeq)
				{
					Runnable r = new Runnable(){

						@Override
						public void run() {


							viewport.jsbh.setValue(viewport.jsbh.getValue()+1);
							// TODO Auto-generated method stub

						}

					};

					newres.location[0]++;
					repaint = true;
					Alignment.al.changed.add(new ResiduePos(0,0));
					SwingUtilities.invokeLater(r);
				}

				if(newres.location[1] <= viewport.startseq && viewport.startseq > 0)
				{
					Runnable r = new Runnable(){

						@Override
						public void run() {


							viewport.jsbv.setValue(viewport.jsbv.getValue()-1);
							// TODO Auto-generated method stub

						}

					};

					newres.location[1]--;
					Alignment.al.changed.add(new ResiduePos(0,0));
					repaint = true;
					SwingUtilities.invokeLater(r);
				}
				if(newres.location[0] <= viewport.startres && viewport.startres > 0)
				{
					Runnable r = new Runnable(){

						@Override
						public void run() {

							Alignment.al.changed.add(new ResiduePos(0,0));
							viewport.jsbh.setValue(viewport.jsbh.getValue()-1);

							// TODO Auto-generated method stub

						}

					};

					newres.location[0]--;

					repaint =true;
					SwingUtilities.invokeLater(r);

				}
				if(repaint)
				{


					synchronized(Alignment.al.autoscrollLock)
					{
						if(Alignment.al.autoscrollrepaint)
						{

						}
						else
						{
							Alignment.al.autoscrollrepaint = true;

						}
					}


				}


				//				System.out.println("running");
				if(dragging)
					SwingUtilities.invokeLater(new Runnable()
					{

						@Override
						public void run() {
							mouseDragged(lastdragMouseEvent);
							// TODO Auto-generated method stub

						}

					});
				else
					SwingUtilities.invokeLater(new Runnable()
					{

						@Override
						public void run() {
							Alignment.al.panel.nhma.mouseDragged(lastdragMouseEvent);
							// TODO Auto-generated method stub

						}

					});


			}
			// TODO Auto-generated method stub

		}

	}

	class PopUpMenu extends JPopupMenu
	{
		public PopUpMenu()
		{
			super();
			this.add(new JMenuItem(canvas.insertNormalGapAction));
			this.add(new JMenuItem(canvas.deleteNormalAction));
			this.add(new JMenuItem(Alignment.al.panel.menu.snapAlignAction));
			this.add(new JMenuItem(Alignment.al.panel.menu.similarSelectAction));
		}
	}
	public void popUp(MouseEvent e)
	{
		if(e.isPopupTrigger())
		{
			PopUpMenu menu = new PopUpMenu();
			menu.show(e.getComponent(), e.getX(), e.getY());
			return;
		}
	}

	public void resDrag(MouseEvent e)
	{
		HashMap <Integer, Integer> rowTrailingBlanks = new HashMap<Integer,Integer>();
		int trailingblanksfound = 0;
		if(dontPermitResDrag ==true)
		{
			return;
		}

		ResiduePos currentpos = getResiduePosAtPoint(e.getPoint());
		ResiduePos backuppos = new ResiduePos(currentpos.location[0],currentpos.location[1]);

		if(currentpos.location[0]!=lastresdragpos.location[0])
			System.out.println("YOLO");
		try{
			if(pp.grabcursor == false&& pp.stopcursor ==false)
			{
				pp.grabcursor = true;
				pp.setCursor(pp.grabbingCursor);
			}
			if(currentpos.location[0]!=lastresdragpos.location[0] || selectedCopy==null)
			{

				//Check if enough blanks
				Iterator<ResiduePos> bCheckIter = viewport.selected.iterator();
				int trailingblankcount = 0;
				int mintrailingblanks = 999999;
				int lastrow = -1;
				boolean notEnoughBlanks = false;
				int start = 0;
				int minblanks = 999999;
				while(bCheckIter.hasNext())
				{
					ResiduePos rp = bCheckIter.next();
					if(firstDrag)
						Alignment.al.currentEdit.currentStartingSelected.add(new ResiduePos(rp.location[0],Alignment.al.viewmap.get(rp.location[1])));
					//adjust rp with diff
					//				rp.location[0];
					boolean newrow = false;
					if(lastrow==-1)
					{

						if(Alignment.al.stickyMode)
						{
							start = Alignment.al.getBlockStart(rp.location[0]);
						}
					}
					if(lastrow !=rp.location[1])
					{
						rowTrailingBlanks.put(lastrow,trailingblankcount);
						if(trailingblankcount <mintrailingblanks && lastrow!=-1)
						{
							mintrailingblanks = trailingblankcount;
						}
						trailingblankcount = 0;
						lastrow = rp.location[1];
						if(lastresdragpos.location[0]-currentpos.location[0]>0)
						{
							//						System.out.println("Checking " + rp.location[0] + diff + "," + rp.location[1]);

							int blanksfound = 0;
							for(int i = rp.location[0]-1; blanksfound<lastresdragpos.location[0]-currentpos.location[0] && i >= start; i--)
							{
								if(Alignment.al.get(rp.location[1]).get(i).isBlank())
								{
									blanksfound++;

								}

							}

							if(blanksfound< minblanks)
							{
								minblanks = blanksfound;


							}


						}
					}

					if(Alignment.al.get(rp.location[1]).get(rp.location[0]).isBlank())
					{
						trailingblankcount++;
					}
					else
					{
						trailingblankcount=0;
					}

					if(!bCheckIter.hasNext())
					{
						rowTrailingBlanks.put(lastrow,trailingblankcount);
						if(trailingblankcount <mintrailingblanks && lastrow!=-1)
						{
							mintrailingblanks = trailingblankcount;
						}
					}
				}
				firstDrag = false;
				trailingblanksfound = mintrailingblanks; 

				int shortfall = lastresdragpos.location[0]-currentpos.location[0] -minblanks;
				if(minblanks == 0)
				{
					Alignment.al.helpText.setText("You cannot drag further in that direction.");
					selectionBlacklisted = true;
				}

				else if(shortfall>0)
				{

					currentpos.location[0]+=shortfall;
				}
			}

			//if first time dragging or if there was a change in dragging direction
			if (selectedCopy==null || ((currentpos.location[0]-lastresdragpos.location[0])!=0 && Math.abs((currentpos.location[0]-lastresdragpos.location[0])/Math.abs(currentpos.location[0]-lastresdragpos.location[0])-resdragdir)==2))

			{
				//deep copy time!
				selectedCopy = new TreeSet<ShiftingLeftResiduePos>();
				selectedCopyright = new TreeSet<ShiftingRightResiduePos>();
				boolean multiplestickies = false;
				ResiduePos lastone = null;

				Iterator<ResiduePos> copyiter = canvas.viewport.selected.iterator();
				while(copyiter.hasNext())
				{
					ResiduePos nextone = copyiter.next();
					if(lastone!=null && Alignment.al.columnIsSticky.get(nextone.location[0]) != Alignment.al.columnIsSticky.get(lastone.location[0]))
					{
						multiplestickies = true;
					}

					//				if(lastrowpos!=null &&  )
					lastone=nextone;
					selectedCopyright.add(new ShiftingRightResiduePos(nextone.location[0],nextone.location[1],rowTrailingBlanks.get(nextone.location[1])));
					selectedCopy.add(new ShiftingLeftResiduePos(nextone.location[0],nextone.location[1],rowTrailingBlanks.get(nextone.location[1])));
				}
				if(multiplestickies && Alignment.al.stickyMode)
				{
					dontPermitResDrag=true;
					selectionBlacklisted = true;
					Alignment.al.helpText.setText("Your selection falls in two different sticky regions. In order to grab and drag it, disable sticky mode.");
				}
				if(ctrlResidues.size()>0)
				{
					dontPermitResDrag=true;
					selectionBlacklisted=true;
					Alignment.al.helpText.setText("You can only grab and move contiguous selections!");
				}

				//test for if there is space to be dragged or not
				{

				}

				//selectedCopy = (TreeSet<ResiduePos>) canvas.viewport.selected.clone();



			}
			//		if(selectionBlacklisted)
			//		{
			//			dontPermitResDrag = true;
			//			pp.setCursor(pp.unavailableCursor);
			//			pp.stopcursor = true;
			//			pp.grabcursor = false;
			//			selectionBlacklisted = false;
			//			return;
			//		}





			if(selectionBlacklisted)
			{
				pp.setCursor(pp.unavailableCursor);
				pp.stopcursor = true;
				pp.grabcursor = false;
				selectionBlacklisted = false;
				return;
			}
			if(currentpos.location[0]==lastresdragpos.location[0])
			{
				return;
			}

			//remove trailing blanks from viewport+selectedCopy cells if shifting left
			//if(trailingblanksfound >0 && lastresdragpos.location[0]-currentpos.location[0] > 0)
			if(trailingblanksfound > 0)
			{
				Iterator<ResiduePos> vpit = viewport.selected.iterator();
				int posstart = vpit.next().location[0];
				int curr = 9999999;
				int prev = -1;
				do
				{
					prev = curr;
					curr = vpit.next().location[0];
				}
				while(vpit.hasNext() && curr>posstart);

				if(curr>posstart && !vpit.hasNext())
					prev=curr;
				vpit = viewport.selected.iterator();
				ArrayList<ResiduePos> toBeRemoved = new ArrayList<ResiduePos>();
				while(vpit.hasNext())
				{
					ResiduePos entry = vpit.next();
					if(entry.location[0]> prev - trailingblanksfound)
					{
						toBeRemoved.add(entry);
					}
				}
				for(ResiduePos entry : toBeRemoved)
				{
					viewport.selected.remove(entry);
				}

				Iterator<ShiftingLeftResiduePos> scit = selectedCopy.iterator();
				ArrayList<ShiftingLeftResiduePos> toBeRemoved2 = new ArrayList<ShiftingLeftResiduePos>();
				posstart = scit.next().location[0];
				curr = 9999999;
				prev = -1;
				do
				{
					prev = curr;
					curr = scit.next().location[0];
				}
				while(scit.hasNext()&&curr>posstart);
				if(curr>posstart && !scit.hasNext())
					prev=curr;
				scit = selectedCopy.iterator();

				while(scit.hasNext())
				{
					ShiftingLeftResiduePos entry = scit.next();
					if(entry.location[0]> prev - trailingblanksfound)
					{
						toBeRemoved2.add(entry);
					}
				}

				for(ShiftingLeftResiduePos entry : toBeRemoved2)
				{
					selectedCopy.remove(entry);
					//				selectedCopyright.remove((ResiduePos)entry);
				}
				//			
				Iterator<ShiftingRightResiduePos> rscit = selectedCopyright.iterator();
				ArrayList<ShiftingRightResiduePos> RtoBeRemoved = new ArrayList<ShiftingRightResiduePos>();
				posstart = rscit.next().location[0];
				curr = 9999999;
				prev = -1;
				do
				{
					prev = curr;
					curr = rscit.next().location[0];
				}
				while(rscit.hasNext()&&curr>posstart);
				if(curr>posstart && !rscit.hasNext())
					prev=curr;
				rscit = selectedCopyright.iterator();

				while(rscit.hasNext())
				{
					ShiftingRightResiduePos entry = rscit.next();
					if(entry.location[0]> prev - trailingblanksfound)
					{
						RtoBeRemoved.add(entry);
					}
				}

				for(ShiftingRightResiduePos entry : RtoBeRemoved)
				{
					selectedCopyright.remove(entry);
				}


				trailingblanksfound = 0;
			}

			Iterator<ShiftingLeftResiduePos> copyitertemp = selectedCopy.iterator();
			Iterator<ResiduePos> itertemp = viewport.selected.iterator();
			int diff = itertemp.next().location[0]-copyitertemp.next().location[0];

			System.out.println("diff " + diff);
			boolean successful = true;

			Iterator iter = null;
			if(currentpos.location[0] - lastresdragpos.location[0] >0)
				iter = selectedCopyright.iterator();
			else
				iter = selectedCopy.iterator();

			//		int min = 9999999;
			Set<Integer> seqset = new HashSet<Integer>();
			int lastres = 0;
			//		if(al.netBlock.busy==true) //not needed since scoring separation 
			//			JOptionPane.showMessageDialog(null, "Please wait for the score to be computed before editing the alignment.");
			//			return;
			//		}

			ResiduePos iter_curr =  null;
			ResiduePos rowstart = null;
			if(iter.hasNext())
			{
				iter_curr = (ResiduePos)iter.next();
				rowstart = iter_curr;
				seqset.add(iter_curr.location[1]);
			}
			outerloop:
				while(true)
				{
					boolean newrow = false;


					innerloop:
						while(!newrow)
						{
							ResiduePos rp = null;
							if(iter.hasNext())
							{
								rp = (ResiduePos) iter.next();

								seqset.add(rp.location[1]);
								if(rp.location[1]!=iter_curr.location[1])
								{
									//proctect until iter_curr
									newrow = true;

									for(int i =0; i < currentpos.location[0] - lastresdragpos.location[0];i++)
									{
										Alignment.al.shiftresright(rowstart.location[1], rowstart.location[0], iter_curr.location[0]+diff+i);
										//						Alignment.al.insertResidueNormal(rowstart.location[1], rowstart.location[0] + i,new Residue(Residue.ResidueType.BLANK));

									}
									for(int i =0; i < lastresdragpos.location[0]-currentpos.location[0];i++)
									{
										//						System.out.println("Res to drag:" + (rowstart.location[0]+diff - i) +",protect:"+ iter_curr.location[0]);
										boolean result = Alignment.al.shiftresleft(rowstart.location[1], rowstart.location[0]+diff - i, iter_curr.location[0]+diff-i);
										//						Alignment.al.insertResidueNormal(rowstart.location[1], rowstart.location[0] + i,new Residue(Residue.ResidueType.BLANK));
										if(result == false)
										{
											successful = false;
										}
									}


									rowstart = rp;
									//					System.out.println("reached end of row");

								}

								iter_curr = rp;
							}
							if(!iter.hasNext())
							{
								for(int i =0; i < currentpos.location[0] - lastresdragpos.location[0];i++)
								{
									Alignment.al.shiftresright(rowstart.location[1], rowstart.location[0], iter_curr.location[0]+diff+i);
									//						Alignment.al.insertResidueNormal(rowstart.location[1], rowstart.location[0] + i,new Residue(Residue.ResidueType.BLANK));
								}
								for(int i =0; i < lastresdragpos.location[0]-currentpos.location[0];i++)
								{
									//						System.out.println("Res to drag:" + (rowstart.location[0]+diff - i) +",protect:"+ iter_curr.location[0]);
									boolean result = Alignment.al.shiftresleft(rowstart.location[1], rowstart.location[0]+diff - i, iter_curr.location[0]+diff-i);
									//						Alignment.al.insertResidueNormal(rowstart.location[1], rowstart.location[0] + i,new Residue(Residue.ResidueType.BLANK));
									if(result == false)
									{
										successful = false;
									}
								}
								break innerloop;

							}


						}

					if(!iter.hasNext())
					{
						break outerloop;
					}




					//			Alignment.al.shiftresright(rp.location[1], lastres);


				}

			System.out.println("Seqset" + seqset);
			Iterator<Integer> iter2 = seqset.iterator();

			//ScoreBlock block = al.sbm.getBlock(lastres);
			while(iter2.hasNext())
			{
				int current = iter2.next();

				Alignment.al.netBlock.seqChange(Alignment.al.viewmap.get(current));

			}

			for(int i = Alignment.al.columnIsSticky.size(); i <= Alignment.al.longestSeq; i ++)
			{
				Alignment.al.columnIsSticky.add(false);
			}


			Alignment.al.changed.add(new ResiduePos(0,0));
			viewport.jsbh.setMaximum(Alignment.al.longestSeq - viewport.width + 10);
			Alignment.al.currentEdit.pushToTreeAndScore();
			//		if(!al.busyUndo) dont think this actually gets called in the case of undo
			Alignment.al.urt.redoNodes.clear();

			//al.setStickyColumns();
			canvas.repaint();
			Iterator<ResiduePos> iter3 = canvas.viewport.selected.iterator();
			successful = true;
			if(successful)
			{
				while(iter3.hasNext())
				{
					ResiduePos nextone = iter3.next();
					nextone.location[0]=nextone.location[0]+(currentpos.location[0] - lastresdragpos.location[0]);
				}
				if(pp.stopcursor)
				{
					Alignment.al.helpText.setText("");
					pp.stopcursor=false;
					pp.grabcursor=false;
					pp.setCursor(pp.unavailableCursor);
				}
			}
			else
			{	
				Alignment.al.helpText.setText("You can't move the selection any further in that direction!");
				pp.setCursor(pp.unavailableCursor);
				pp.stopcursor = true;
				pp.grabcursor = false;
			}

			Alignment.al.changed.add(new ResiduePos(0,0));
			//		canvas.redrawArea(0, 0, viewport.startres, viewport.startseq, viewport.width, viewport.height);
			canvas.repaint();
			if(Math.abs(currentpos.location[0]-lastresdragpos.location[0])==0)
				resdragdir = resdragdir;

			else
				resdragdir = (currentpos.location[0]-lastresdragpos.location[0])/Math.abs(currentpos.location[0]-lastresdragpos.location[0]);
			if(!pp.stopcursor)
				lastresdragpos = currentpos;
		}
		finally
		{
			currentpos = backuppos;
		}
	}





}
