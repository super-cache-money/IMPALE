package gui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class WestHeaderMouseAdapter extends MouseAdapter{
//	Alignment al;
	boolean resizing= false;
	public WestHeaderMouseAdapter()
	{
//		this.al = al;
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		if(Alignment.al.panel.headers.rh.getWidth() -e.getPoint().x <5)
		{
			Alignment.al.panel.movecursor = true;
			Alignment.al.panel.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
			
		}
		else if(Alignment.al.panel.movecursor)
		{
			Alignment.al.panel.movecursor = false;
			Alignment.al.panel.setCursor(Cursor.getDefaultCursor());
		}
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if(Alignment.al.panel.headers.rh.getWidth() -e.getPoint().x <5)
		{
		System.out.println("West header clicked " + e.getPoint());
		resizing = true;
		}
		
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		resizing = false;
	}
	
	@Override 
	public void mouseDragged(MouseEvent e)
	{
		
//		System.out.println("repainting")
if(e.getPoint().x<Alignment.al.panel.getWidth()-15&&resizing)
{
		Alignment.al.panel.headers.rh.setPreferredSize(new Dimension(e.getPoint().x,100));
		Alignment.al.panel.doLayout();
		Alignment.al.panel.repaint();
}
//		Alignment.al.panel.canvas.repaint();
//		Alignment.al.panel.headers.rh.repaint();
	}
}
