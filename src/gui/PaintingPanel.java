package gui;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.MenuBar;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.nio.file.Files;

import javax.swing.*;

import net.iharder.dnd.FileDrop;

public class PaintingPanel extends JPanel {
	public TopPanel topPanel;
	public PaintingCanvas canvas;
//	public Alignment al;
	public Headers headers;
	public ScoreBlock scoreBlock;
	public CanvasMouseAdapter cma;
	JFrame superframe;
	NorthHeaderMouseAdapter nhma ;
	WestHeaderMouseAdapter whma;
	public MainMenu menu;
	Cursor unavailableCursor, grabbingCursor;
	boolean movecursor = false;
	boolean stopcursor = false;
	boolean grabcursor = false;
	public PaintingPanel(JFrame superframe)
	{
		
		
		Alignment.al.panel = this;
		this.superframe = superframe;
		canvas = new PaintingCanvas();
		System.out.println("canvas.");
		cma = new CanvasMouseAdapter(this);
		canvas.addMouseListener(cma);
		canvas.addMouseMotionListener(cma);
		canvas.addMouseWheelListener(cma);
		setLayout(new BorderLayout());
		headers = new Headers(canvas.viewport);
		 nhma = new NorthHeaderMouseAdapter();
		 whma = new WestHeaderMouseAdapter();
		headers.tch.addMouseListener(nhma);
		headers.tch.addMouseMotionListener(nhma);
		headers.rh.addMouseMotionListener(whma);
		headers.rh.addMouseListener(whma);
		add(headers.rh, BorderLayout.WEST);
		add(headers.tch, BorderLayout.NORTH);
		add(canvas, BorderLayout.CENTER);
		add(canvas.viewport.jsbv,BorderLayout.EAST);
		add(canvas.viewport.jsbh, BorderLayout.SOUTH);
		canvas.addComponentListener(canvas.viewport);
		canvas.viewport.updateDimensions(canvas.getSize());
		canvas.repaint();
		unavailableCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(RunEverything.class.getResource("/unavailable.png")), new Point(0,0), "Unavailable");
		grabbingCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(RunEverything.class.getResource("/grabbing.png")), new Point(0,0), "Grabbing");
		new  FileDrop( this, new FileDrop.Listener()
	      {   public void  filesDropped( java.io.File[] files )
	          {   
	    	  		if(files.length > 1)
	    	  		{
	    	  			JOptionPane.showMessageDialog(null, "You may only drop in 1(one) single alignment file!");
	    	  			return;
	    	  		}
//	    	  		if(files[0].getName())
	    			int response = JOptionPane.NO_OPTION;
	    			//System.out.println(Sequence.getConsensus(0, al.longestSeq-1, al));
	    			if(!IO.lastFile.getName().equals("-.fas"))
	    			{
	    				response = JOptionPane.showConfirmDialog(null, "Would you like to save the changes before opening a new file?", "", JOptionPane.YES_NO_CANCEL_OPTION);
	    			}
	    			
	    			if(response==JOptionPane.CANCEL_OPTION)
	    			{
	    				
	    			}
	    			else if(response == JOptionPane.NO_OPTION)
	    			{File f = files[0];
	    				RunEverything re = new RunEverything(f);
	    				re.run();
	    				PaintingPanel.this.superframe.dispose();
	    			}
	    			else if(response ==JOptionPane.YES_OPTION)
	    			{
	    				final File f = files[0];
	    				IO.writeFasta(false, new Runnable(){

							@Override
							public void run() {
			    				RunEverything re = new RunEverything(f);
			    				re.run();
			    				PaintingPanel.this.superframe.dispose();
								// TODO Auto-generated method stub
								
							}});
	    			}

	    	  		
	          }   // end filesDropped
	      }); // end FileDrop.Listener
		
		

	      
	         
	}

}
