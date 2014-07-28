package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JScrollBar;

public class PaintingViewport implements AdjustmentListener, ComponentListener{
	
	int startres, endres, startseq, endseq;
//	Alignment al;
	//Painting panel;
	int width;
	int height;
	int fontWidth;
	int fontHeight;
	int heightOffset;
	PaintingCanvas associatedcanvas;
	FontMetrics fm;
	public JScrollBar jsbh, jsbv;
	Set<ResiduePos> selected;
    int currentFont;

    public PaintingViewport( Dimension dim, FontMetrics fm, PaintingCanvas canvas)
	{
		int [] point =new int [100];
		selected = new TreeSet<ResiduePos> ();
		associatedcanvas = canvas;
		this.fm = fm;
		startres = 0;
		startseq = 0;


//		this.al = al;
		jsbh = new JScrollBar();
		jsbh.addAdjustmentListener(this);
		jsbh.setOrientation(JScrollBar.HORIZONTAL);
		jsbh.setMinimum(0);
		jsbv = new JScrollBar();
		jsbv.addAdjustmentListener(this);
		jsbv.setMinimum(0);
		updateDimensions(dim);
		

	}
	

	
	
	public void zoomToFontSize(int newfont)
    {
        ResiduePos firstSelected = null;
        if(selected.size()>0)
            firstSelected = selected.iterator().next();

        currentFont=newfont;
        Alignment.al.panel.canvas.font2 = new Font(Alignment.al.panel.canvas.font2.getFamily(), Alignment.al.panel.canvas.font2.getStyle(), newfont );
        Alignment.al.panel.canvas.viewport.fm = Alignment.al.panel.canvas.getFontMetrics(Alignment.al.panel.canvas.font2);
        Alignment.al.panel.canvas.viewport.componentResized(null);
        Alignment.al.panel.canvas.repaint();
        if(firstSelected!=null)
        {
            int goToX = Math.max(firstSelected.location[0]-width/2,0);
            int goToY = Math.max(firstSelected.location[1]-height/2,0);

            ResiduePos goTo = new ResiduePos(goToX,goToY);
            jsbh.setValue(goToX);
            jsbv.setValue(goToY);
        }
    }
	
	
	public void updateDimensions(Dimension dim)
	{
		
		fontHeight = fm.getHeight();
		fontWidth = 2 + fm.stringWidth("aa") - fm.charWidth('a');
		height = (int) dim.getHeight()/fontHeight;
		width = (int) (dim.getWidth()/ (fontWidth)) -1;
		if(height>Alignment.al.size())
			height = Alignment.al.size();
		if(width>Alignment.al.longestSeq)
			width = Alignment.al.longestSeq;
		//NB NB FUCKED
		
		endres = startres + width -1;
		endseq = startseq + height -1;
		jsbv.setMaximum(Alignment.al.size() -height + 10);
		jsbh.setMaximum(Alignment.al.longestSeq - width + 10);
		heightOffset = fontHeight - 3;
		if(Alignment.al.ct!=null &&width!=Alignment.al.ct.lastWidth)
		{
			Alignment.al.ct.lastWidth = width;
			boolean widthIsOdd = width%2==1;
			int usethis = width/2;
			if(widthIsOdd)
				usethis++;
			Alignment.al.ct.generateColumnOrder(width);
			
			//do a calibration run
			try{
				Alignment.al.panel.headers.tch.repaint();
					Alignment.al.ct.startCompute.set(startres);
					Alignment.al.ct.endCompute.set(endres);
					Alignment.al.ct.colChanged.set(true);
				
					Alignment.al.ct.delayCalibration.set(true);
//					Alignment.al.ct.stalledUpdate(0);
					synchronized(Alignment.al.ct.colSleeper)
					{
						Alignment.al.ct.colSleeper.notify();
					}
					
				}
				catch(NullPointerException err)
				{
					err.printStackTrace();
				}
		}
		
	}
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		// TODO Auto-generated method stub
//		System.out.println(e.getValue() + " / " + ((JScrollBar)(e.getSource())).getMaximum());
		if (((JScrollBar) e.getSource()).getOrientation()==JScrollBar.HORIZONTAL)
		{
			boolean scrollRight = (e.getValue()>startres)? true : false;
			startres = e.getValue();
			endres = startres+width -1;
			try{
			//Alignment.al.panel.headers.tch.repaint();
//				Alignment.al.ct.startCompute.set(startres);
//				Alignment.al.ct.endCompute.set(endres);
//				if(scrollRight)
//					Alignment.al.ct.seed.set(width-1);
//				else
//					Alignment.al.ct.seed.set(0);
//				Alignment.al.ct.colChanged.set(true);
//				synchronized(Alignment.al.ct.colSleeper)
//				{
//					Alignment.al.ct.colSleeper.notify();
//				}
				if(scrollRight)
				Alignment.al.ct.stalledUpdate(width-1);
				else
				Alignment.al.ct.stalledUpdate(0);
				
			}
			catch(NullPointerException err)
			{
				System.err.println("ct is not ready yet!");
			}
			
		}
		
		else
		{
			startseq = e.getValue();
			endseq= startseq+height-1;
			try{
			Alignment.al.panel.headers.rh.repaint();
			}
			catch(NullPointerException err)
			{
				System.err.println("headers.rh is not ready yet!");
			}
		}
		
		
		
		
		
		associatedcanvas.repaint();
		
	}



	@Override
	public void componentResized(ComponentEvent e) {
		updateDimensions(associatedcanvas.getSize());
		associatedcanvas.clean = true;
		associatedcanvas.bimg = null;
		associatedcanvas.repaint();
		// TODO Auto-generated method stub
		
	}



	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	public void scrollTo(ResiduePos goHerePos)
	{
		if(startres > goHerePos.location[0])
		{
			jsbh.setValue(goHerePos.location[0]);
		}
		else if(endres < goHerePos.location[0])
		{
			jsbh.setValue(goHerePos.location[0]-width+1);
		}
		 if(startseq > goHerePos.location[1])
		{
			jsbv.setValue(goHerePos.location[1]);
		}
		else if(endseq < goHerePos.location[1])
		{
			jsbv.setValue(goHerePos.location[1]-height+1);
		}
	}
	
	public void scrollTo(int leftmost, int rightmost, int topmost, int botmost)
	{
		ResiduePos goTo = new ResiduePos(startres, startseq);
		if(leftmost < startres||leftmost > endres)
			goTo.location[0]=leftmost;
		else if(rightmost > endres)
		{
			int leftspace = leftmost - startres;
			int moveThisMuch= Math.min(leftspace, rightmost-endres);
			goTo.location[0] = startres+moveThisMuch;
		}
		
		if(topmost < startseq || topmost > endseq)
			goTo.location[1]=topmost;
		else if(botmost > endseq)
		{
			int leftspace = topmost - startseq;
			int moveThisMuch= Math.min(leftspace, botmost-endseq);
			goTo.location[1] = startseq+moveThisMuch;
		}
		jsbh.setValue(goTo.location[0]);
		jsbv.setValue(goTo.location[1]);
	}

	
	//public scrollLeft

}
