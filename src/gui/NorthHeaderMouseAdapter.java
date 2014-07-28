package gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NorthHeaderMouseAdapter extends MouseAdapter{
	
	boolean dragging = false;
//	Alignment al;
	//Headers.TopColumnHeader tch = Alignment.al.panel.headers.tch;
	int startselect;
	int lastres;
	public NorthHeaderMouseAdapter()
	{
		super();
//		this.al = al;
		
	}
	public int getRes(int pixel)
	{
		pixel -= Alignment.al.panel.headers.rh.getWidth();
		int ret = pixel/Alignment.al.panel.canvas.viewport.fontWidth + Alignment.al.panel.canvas.viewport.startres;
		return ret;
		
		
	}
	@Override
	public void mousePressed(MouseEvent e)
	{
		System.out.println("HEADER PRESSED " + e.getPoint());
		if(!e.isControlDown())
		{
			Alignment.al.panel.canvas.viewport.selected.clear();
			Alignment.al.changed.add(new ResiduePos(0,0));
		}
		startselect = getRes(e.getX());
		lastres = startselect;
		for(int i = 0; i < Alignment.al.size(); i++)
		{
			Alignment.al.panel.canvas.viewport.selected.add(new ResiduePos(startselect, i));
			
		}
		Alignment.al.panel.canvas.redrawArea(startselect-Alignment.al.panel.canvas.viewport.startres, 0, startselect, Alignment.al.panel.canvas.viewport.startseq, 1, Alignment.al.panel.canvas.viewport.height) ;
		Alignment.al.panel.canvas.repaint();
		Alignment.al.panel.cma.edgetimer = Alignment.al.panel.cma.new EdgeTimerTask();
		Alignment.al.panel.cma.timer.scheduleAtFixedRate(Alignment.al.panel.cma.edgetimer, 100, 50);
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		Alignment.al.panel.nhma.dragging = false;
		Alignment.al.panel.cma.edgetimer.cancel();
	}
	@Override
	public void mouseDragged(MouseEvent e)
	{
		Alignment.al.panel.nhma.dragging = true;
		int newres = getRes(e.getX());
//		System.out.println(newres);
		if(newres >startselect)
		{
			if(lastres<startselect)
			{
//				System.out.println("LOOL");
				for(int j =lastres; j <startselect; j++)
				{
//					System.out.println("removed " + j);
					for(int i = 0; i < Alignment.al.size(); i++)
					{
						Alignment.al.panel.canvas.viewport.selected.remove(new ResiduePos(j, i));
						
					}
				}
				
				
				Alignment.al.panel.canvas.redrawArea(lastres-Alignment.al.panel.canvas.viewport.startres, 0, lastres, Alignment.al.panel.canvas.viewport.startseq, startselect - lastres, Alignment.al.panel.canvas.viewport.height) ;
				lastres = startselect;
			}
			if(newres> lastres)
			{
				for(int j =lastres+1; j <=newres; j++)
				{
					for(int i = 0; i < Alignment.al.size(); i++)
					{
						Alignment.al.panel.canvas.viewport.selected.add(new ResiduePos(j, i));
						
					}
				}
				Alignment.al.panel.canvas.redrawArea(lastres-Alignment.al.panel.canvas.viewport.startres+1, 0, lastres+1, Alignment.al.panel.canvas.viewport.startseq, newres-lastres, Alignment.al.panel.canvas.viewport.height) ;
				
			}
			else if(lastres> newres)
			{
				for(int j =lastres; j >newres; j--)
				{
					for(int i = 0; i < Alignment.al.size(); i++)
					{
						Alignment.al.panel.canvas.viewport.selected.remove(new ResiduePos(j, i));
						
					}
				}
				
				Alignment.al.panel.canvas.redrawArea(newres-Alignment.al.panel.canvas.viewport.startres+1, 0, newres+1, Alignment.al.panel.canvas.viewport.startseq, lastres-newres, Alignment.al.panel.canvas.viewport.height) ;
				
			}
		}
		else
		{
			if(lastres>startselect)
			{
//				System.out.println("LOOL");
				for(int j =lastres; j >startselect; j--)
				{
//					System.out.println("removed " + j);
					for(int i = 0; i < Alignment.al.size(); i++)
					{
						Alignment.al.panel.canvas.viewport.selected.remove(new ResiduePos(j, i));
						
					}
				}
				
				
				Alignment.al.panel.canvas.redrawArea(startselect-Alignment.al.panel.canvas.viewport.startres+1, 0, startselect+1, Alignment.al.panel.canvas.viewport.startseq, lastres-startselect, Alignment.al.panel.canvas.viewport.height) ;
				lastres = startselect;
			}
			if(newres< lastres)
			{
				for(int j =lastres-1; j >=newres; j--)
				{
					for(int i = 0; i < Alignment.al.size(); i++)
					{
						Alignment.al.panel.canvas.viewport.selected.add(new ResiduePos(j, i));
						
					}
				}
				Alignment.al.panel.canvas.redrawArea(newres-Alignment.al.panel.canvas.viewport.startres, 0, newres, Alignment.al.panel.canvas.viewport.startseq, lastres-newres, Alignment.al.panel.canvas.viewport.height) ;
			}
			else if(lastres<newres)
			{
				for(int j = lastres; j<newres; j++)
				{
					for(int i =0; i < Alignment.al.size(); i++)
					{
						Alignment.al.panel.canvas.viewport.selected.remove(new ResiduePos(j, i));
					}
				}
				Alignment.al.panel.canvas.redrawArea(lastres-Alignment.al.panel.canvas.viewport.startres, 0, lastres, Alignment.al.panel.canvas.viewport.startseq, newres-lastres, Alignment.al.panel.canvas.viewport.height) ;
			}
			
		}
		lastres = newres;
		Alignment.al.panel.canvas.repaint();
		Alignment.al.panel.cma.lastdragMouseEvent = e;
	}
	
	

}
