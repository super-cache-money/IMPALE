package gui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;

public class Headers {
	
	PaintingViewport viewport;
	PaintingCanvas canvas;
//	Alignment al;
	RowHeader rh;
	TopColumnHeader tch;
	Font headerfont;
	
	public Headers (PaintingViewport viewport)
	{
		this.viewport = viewport;

		Font normal = viewport.fm.getFont();
		headerfont = new Font(normal.getName(), Font.BOLD, normal.getSize());
		canvas = viewport.associatedcanvas;
//		al = canvas.al;
		rh = new RowHeader();
		tch = new TopColumnHeader();
		
	}
	class TopColumnHeader extends JComponent
	{
		boolean clean;
		BufferedImage bimg;
		Graphics2D gbimg;
		int oldres;
		int smallinterval = 5;
		int bigingerval = 2;
		int lineheight = 5;
		int verticalindent = 16;
		public TopColumnHeader()
		{
			super();
			setOpaque(true);
			clean = true;
			bimg = null;
			setPreferredSize(new Dimension(100, 40));
		}
//		@Override
//		public void repaint()
//		{
//			System.out.println("lol");
//			super.repaint();
//		}
		
		@Override
		public void paintComponent(Graphics g2)
		{
			Graphics2D g = (Graphics2D) g2;
			g.setColor(Color.WHITE);
			g.setFont(Alignment.al.panel.canvas.font2);
			g.clearRect(0, 0, getWidth(), getHeight());
			int firstodd;
			boolean big = true;;
			if(viewport.startres%smallinterval==0)
			{
				firstodd = 0;
			}
			else
			{
				firstodd = (viewport.startres/5+1)*smallinterval - viewport.startres;
			}
			synchronized(Alignment.al.ct.paintLock)
			{
				//possibly could be edited out to speed things up
				g.fillRect(Headers.this.rh.getWidth(), (int) (0), viewport.width*viewport.fontWidth, this.getHeight());
			try{
			if(Alignment.al.ct.currentCountUpdate!=null)
			for(int i = 0; i < viewport.width; i++)
			{
				
					int currheight = 0;
					for(int usedi = 0; usedi < Alignment.al.usedResiduesArr.length; usedi++)
					{
						Residue.ResidueType res = Alignment.al.usedResiduesArr[usedi];
						g.setColor(new Residue(res).getColor());
//						if(al.ct.currentCountUpdate==null)
//							System.currentTimeMillis();
						double percent = (Alignment.al.ct.currentCountUpdate[i][usedi]+ 0.0)/Alignment.al.size();
						int colheight = (int) ((this.getHeight()+0.0)*percent);
						if(colheight>0)
							g.fillRect(Headers.this.rh.getWidth() + i*viewport.fontWidth, (int)(this.getHeight()+0.0) - colheight - currheight, viewport.fontWidth, colheight);
						currheight+=colheight;
						

					}
					
					if(Alignment.al.ct.completed.get())
					{
						
						g.setColor(Color.BLACK);
						g.drawString(Alignment.al.ct.consRes[i].toString(),Headers.this.rh.getWidth() + i*viewport.fontWidth ,24);
					}

				

			}
			
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				g.fillRect(Headers.this.rh.getWidth(), (int) ((this.getHeight()+0.0)), viewport.width*viewport.fontWidth, this.getHeight());

			}
			
			}
			g.setColor(Color.BLACK);
			//do scale
			for(int i = 0; i < viewport.width; i++)
			{		
				

				if((i-firstodd)%smallinterval==0)
				{
					
						int offset = (int) (viewport.fm.getStringBounds(viewport.startres+i + "", g).getWidth()/2);
						g.drawString(i+viewport.startres+"",Headers.this.rh.getWidth() + i*viewport.fontWidth + viewport.fontWidth/2 - 1 -offset , 11);
						g.fillRect(Headers.this.rh.getWidth() + i*viewport.fontWidth + viewport.fontWidth/2 - 1, 12 + verticalindent, 3, lineheight+2);

					
				}
				else
				{
					g.drawLine(Headers.this.rh.getWidth() + i*viewport.fontWidth + viewport.fontWidth/2, 14 + verticalindent,Headers.this.rh.getWidth() + i*viewport.fontWidth + viewport.fontWidth/2 , 14+verticalindent+lineheight);
					big = true;
				}
				

				
			}
			
			g.fillRect(Headers.this.rh.getWidth(), this.getHeight()-2, this.getWidth(), 2);
			
		}
		public void oldPaintComponent(Graphics g2)
		{
			Graphics2D g = (Graphics2D) g2;
			g.setFont(Alignment.al.panel.canvas.font2);
			g.clearRect(0, 0, getWidth(), getHeight());
			int firstodd;
			boolean big = true;;
			if(viewport.startres%smallinterval==0)
			{
				firstodd = 0;
			}
			else
			{
				firstodd = (viewport.startres/5+1)*smallinterval - viewport.startres;
			}
			
			for(int i = 0; i < viewport.width; i++)
			{
				try{
					
					Residue r = new Residue(Alignment.al.columns.get(i+viewport.startres).res);
					
					double percent = (Alignment.al.columns.get(i+viewport.startres).max + 0.0)/Alignment.al.size();
					
					int colheight = 0;
					if(!r.isBlank())
						colheight = (int) ((this.getHeight()+0.0)*percent);
					
					//System.out.print(percent + " ");
					g.setColor(r.getColor());
					Column currcol = Alignment.al.columns.get(i+viewport.startres);
					int currheight = colheight;
					
					//do most common res colour
					g.fillRect(Headers.this.rh.getWidth() + i*viewport.fontWidth, (int) ((this.getHeight()+0.0)-colheight), viewport.fontWidth, colheight);
					for(Entry<Residue.ResidueType,MutableInt> entry : Alignment.al.columns.get(i+viewport.startres).entrySet())
					{
						if(entry.getKey()!=currcol.res && entry.getKey()!=Residue.ResidueType.BLANK)
						{
							g.setColor(new Residue(entry.getKey()).getColor());
							percent = (entry.getValue().value+ 0.0)/Alignment.al.size();
							colheight = (int) ((this.getHeight()+0.0)*percent);
							
							//int colheight = (int) 
							
							g.fillRect(Headers.this.rh.getWidth() + i*viewport.fontWidth, (int)(this.getHeight()+0.0) - colheight - currheight, viewport.fontWidth, colheight);
							currheight+=colheight;
						}
							
					}
					//do blank colour
					MutableInt blackcount = Alignment.al.columns.get(i).get(Residue.ResidueType.BLANK);
					if(blackcount!=null)
					{
						g.setColor(new Residue(Residue.ResidueType.BLANK).getColor());
						percent = ((blackcount.value+0.0)/Alignment.al.size());
						colheight = (int) ((this.getHeight()+0.0)*percent);
						g.fillRect(Headers.this.rh.getWidth() + i*viewport.fontWidth, (int)(this.getHeight()+0.0) - colheight - currheight, viewport.fontWidth, colheight);
					}
					g.setColor(Color.BLACK);
					g.drawString(r.toString(),Headers.this.rh.getWidth() + i*viewport.fontWidth ,24);
				}
				catch(NullPointerException ex)
				{
					
				}
			}
			g.setColor(Color.BLACK);
			for(int i = 0; i < viewport.width; i++)
			{		
				

				if((i-firstodd)%smallinterval==0)
				{
					
						int offset = (int) (viewport.fm.getStringBounds(viewport.startres+i + "", g).getWidth()/2);
						g.drawString(i+viewport.startres+"",Headers.this.rh.getWidth() + i*viewport.fontWidth + viewport.fontWidth/2 - 1 -offset , 11);
						g.fillRect(Headers.this.rh.getWidth() + i*viewport.fontWidth + viewport.fontWidth/2 - 1, 12 + verticalindent, 3, lineheight+2);

					
				}
				else
				{
					g.drawLine(Headers.this.rh.getWidth() + i*viewport.fontWidth + viewport.fontWidth/2, 14 + verticalindent,Headers.this.rh.getWidth() + i*viewport.fontWidth + viewport.fontWidth/2 , 14+verticalindent+lineheight);
					big = true;
				}
				

				
			}
			g.fillRect(Headers.this.rh.getWidth(), this.getHeight()-2, this.getWidth(), 2);
			
			//g.drawLine(Headers.this.rh.getWidth(), this.getHeight() - 1, this.getWidth()-1, this.getHeight()-1);
		}
	}
	class RowHeader extends JComponent
	{
		boolean clean;
		BufferedImage bimg;
		Graphics2D gbimg;
		int oldseq;
		
		public RowHeader()
		{
			super();
			setOpaque(true);
			clean = true;
			bimg = null;
			int max =0;
			for(int i = 0; i < Alignment.al.size(); i++)
			{
				if(viewport.fm.stringWidth(Alignment.al.getUnderlying(i).name) > max);
					max = viewport.fm.stringWidth(Alignment.al.getUnderlying(i).name);
				
			}
			if(Alignment.al.longestName > 30)
				Alignment.al.longestName = 30;
			setPreferredSize(new Dimension(Alignment.al.longestName*viewport.fontWidth,100));
			
		}
		
		@Override
		public void paintComponent(Graphics g)
		{

		    
	    	 
	    	 g.setFont(Alignment.al.panel.canvas.font2);
	    	 g.clearRect(0, 0, this.getWidth(), this.getHeight());
				try{
					for(int i = 0; i < viewport.height; i++)
					{
						
						g.drawString(Alignment.al.get(viewport.startseq+i).name, 3, viewport.fontHeight*(i) + viewport.heightOffset);
						g.fillRect(this.getWidth()-2, 0, 2, this.getHeight());
					}
					}
					catch(NullPointerException e)
					{
						
					}
				
				
	    	 //g.drawImage(bimg, , dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer)
	    	 
		}
		
		public void redrawArea(int pos, int seq, int size)
		{
			for(int i = 0; i < size; i++)
			{
				
				gbimg.drawString(Alignment.al.get(seq+i).name, 0, viewport.fontHeight*(pos+i) + viewport.heightOffset);
			}
		}
	}

	class ColumnHeader extends JComponent
	{
		
	}
}
