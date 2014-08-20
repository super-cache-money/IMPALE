package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;


public class PaintingCanvas extends JComponent{

	private static final long serialVersionUID = 1L;
	public PaintingViewport viewport;
	InputMap savedMap = null;
	Font font2 =new Font("Monospaced", Font.PLAIN, 12);
	Font fontSelected;
//	Alignment al;
	public boolean clean;
	public Image bimg;
	int oldresbuffered, oldseqbuffered;
	Graphics2D bufferg;
	
	//CanvasMouseAdapter mousey;
	InputMap im;
	ActionMap am;
	HashMap<String,Boolean> isKeyPressed;
	boolean mousePressedSinceLastKey = false;
    GraphicsConfiguration gfx_config;

    private int repaintCount = 0;

    public PaintingCanvas()
	{
		super();
        setDoubleBuffered(false);
       gfx_config  = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getDefaultScreenDevice().
                getDefaultConfiguration();
		savedMap = new InputMap();
//		savedMap.
		isKeyPressed = new HashMap<String, Boolean>()
				{
			@Override 
			public Boolean get(Object key)
			{
				if(!(key instanceof String))
				{
					return null;
				}
				String str = (String) key;
				Boolean ret = super.get(key);
				if(ret==null)
					ret = false;
				return ret;
			}
				};

		setOpaque(true);
		//font2 = new Font("Monospaced", Font.PLAIN, 12);
		fontSelected = new Font("Monospaced", Font.BOLD, 10);
		viewport = new PaintingViewport( getSize(), getFontMetrics(font2), this);
		
		clean = true;
		

		setUpKeyboardMaps();

		
		//System.out.println("SIZE " + getPreferredSize());
		
	}
	
	public void setUpKeyboardMaps()
	{
				savedMap = this.getInputMap(JComponent.WHEN_FOCUSED);
				
		am = this.getActionMap();

		//The following maps allow the user to interact with the canvas via keyboard.
		savedMap.put(KeyStroke.getKeyStroke('-'), "insertGap");
		am.put("insertGap", insertNormalGapAction);
		
		savedMap.put(KeyStroke.getKeyStroke("DELETE"), "deleteRes");
		am.put("deleteRes", deleteNormalAction);
		
		savedMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "backspaceRes");
		am.put("backspaceRes", deleteNormalAction);
		
		savedMap.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
		am.put("moveRight", moveRightAction);
		
		savedMap.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
		am.put("moveLeft", moveLeftAction);
		
		savedMap.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
		am.put("moveDown", moveDownAction);
		
		savedMap.put(KeyStroke.getKeyStroke("ESCAPE"), "clearSelection");
		am.put("clearSelection", clearSelectionAction);
		
		savedMap.put(KeyStroke.getKeyStroke("UP"), "moveUp");
		am.put("moveUp", moveUpAction);
		
		savedMap.put(KeyStroke.getKeyStroke("SPACE"), "switchStickyMode");
		am.put("switchStickyMode", switchStickyAction);
		
		savedMap.put(KeyStroke.getKeyStroke("shift RIGHT"), "addMoveRight");
		am.put("addMoveRight", addMoveRightAction);
		
		savedMap.put(KeyStroke.getKeyStroke("shift LEFT"), "addMoveLeft");
		am.put("addMoveLeft", addMoveLeftAction);
		
		savedMap.put(KeyStroke.getKeyStroke("shift DOWN"), "addMoveDown");
		am.put("addMoveDown", addMoveDownAction);
		
		savedMap.put(KeyStroke.getKeyStroke("shift UP"), "addMoveUp");
		am.put("addMoveUp", addMoveUpAction);
		
		savedMap.put(KeyStroke.getKeyStroke("released SHIFT"),"releasedShift");
		am.put("releasedShift", shiftReleasedAction);
		
		savedMap.put(KeyStroke.getKeyStroke("A"), "keyPressA");
		am.put("keyPressA", keyPressAAction);
		
		savedMap.put(KeyStroke.getKeyStroke("released A"), "keyReleaseA");
		am.put("keyReleaseA", keyReleaseAAction);
		
		savedMap.put(KeyStroke.getKeyStroke("S"), "keyPressS");
		am.put("keyPressS", keyPressSAction);
		
		savedMap.put(KeyStroke.getKeyStroke("released S"), "keyReleaseS");
		am.put("keyReleaseS", keyReleaseSAction);

		InputMap im= new InputMap(){
			@Override 
			public Object get(KeyStroke keystroke)
			{
				return PaintingCanvas.this.savedMap.get( keystroke);
			}
		};
		
//		this.setInputMap(WHEN_FOCUSED, savedMap);
	}
	//public void 
	public void paintComponent(Graphics g2)
	{
        Graphics2D g = (Graphics2D) g2;
        g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        long starttime = System.currentTimeMillis();
		synchronized(Alignment.al.autoscrollLock)
		{
			Alignment.al.autoscrollrepaint = true;
		}
		//super.paintComponent(g);
		g = (Graphics2D)g;
		//viewport.updateDimensions(getSize());
		//super.paintComponent(g);
		//g.setClip(0,0,(viewport.width - 15)*viewport.fontWidth, viewport.height*viewport.fontHeight);
		Color kirtanya = getBackground();
			g.setColor(kirtanya);
			updateBuffer();
			//RescaleOp op = new RescaleOp(1.3f, 0, null);
			//bimg = op.filter(bimg, null);
			g.drawImage(bimg,0,0,(int)(bimg.getWidth(null)/OSTools.retinaMultiplier), (int) (bimg.getHeight(null)/OSTools.retinaMultiplier),this);
			//System.out.println("PRINTED CLIP!" + g.getClipBounds() + " Component size " + getSize());
			//g.drawImage
        System.out.println("Time taken: " + (System.currentTimeMillis()-starttime));


	}

	@Override public void repaint()
	{
//        System.out.println("Repainting..." + repaintCount);
//        repaintCount++;

		super.repaint();

	}
	public void  updateBuffer()
	{
		//super.paintComponent(g);
		//System.out.println("WHAT");

		Color kirtanya = getBackground();
		if(bimg==null)
		{//System.out.println("WIDTH" + viewport.width);
			try{
//			bimg = new BufferedImage(viewport.width*viewport.fontWidth, viewport.height*viewport.fontHeight, BufferedImage.TYPE_INT_ARGB);

                if(OSTools.isWindows())
                {
                    bimg = gfx_config.createCompatibleImage((int) (OSTools.retinaMultiplier * viewport.width * viewport.fontWidth), (int) (OSTools.retinaMultiplier * viewport.height * viewport.fontHeight));

                }
                else {
                    bimg = gfx_config.createCompatibleVolatileImage((int) (OSTools.retinaMultiplier * viewport.width * viewport.fontWidth), (int) (OSTools.retinaMultiplier * viewport.height * viewport.fontHeight));
                }
                bimg.setAccelerationPriority(1);
			bufferg =   (Graphics2D) bimg.getGraphics();
//                bufferg.setRenderingHint(
//                        RenderingHints.KEY_ANTIALIASING,
//                        RenderingHints.VALUE_ANTIALIAS_ON);
//



			}
			catch(IllegalArgumentException lol)
			{
				System.out.println("error....who gives a shit lol");
				return;
			}

			//System.out.println("WIDTH" + viewport.width);
			//System.out.println(bufferg.getClip());

			//bufferg.setClip(0,0,(viewport.width - 3)*viewport.fontWidth, viewport.height*viewport.fontHeight);
		}

		//Graphics2D g =  (Graphics2D) bimg.getGraphics();
		if(clean)
		{
			bufferg.setColor(kirtanya);
			bufferg.fillRect(0, 0, bimg.getWidth(null), bimg.getHeight(null));

			redrawArea(0,0,viewport.startres, viewport.startseq, viewport.width, viewport.height);
			clean = false;
		}

		else
		{

			int xdiff = oldresbuffered - viewport.startres;
			int ydiff = oldseqbuffered - viewport.startseq;
			if(xdiff > 0) //scroll left
			{
				bufferg.copyArea(0, 0, (int)(OSTools.retinaMultiplier*(viewport.width-xdiff)*viewport.fontWidth), bimg.getHeight(null), (int) (OSTools.retinaMultiplier*xdiff*viewport.fontWidth), 0);
				redrawArea(0,0, viewport.startres,viewport.startseq,xdiff, viewport.height);
			}
			else if(xdiff < 0) //scroll right
			{
				//bufferg.setClip();
				bufferg.copyArea((int)(-OSTools.retinaMultiplier*(xdiff)*viewport.fontWidth), 0, (int)(OSTools.retinaMultiplier*(viewport.width+xdiff)*viewport.fontWidth), bimg.getHeight(null), (int) (OSTools.retinaMultiplier*xdiff*viewport.fontWidth), 0);
				redrawArea(viewport.width +xdiff, 0, viewport.endres +  xdiff + 1, viewport.startseq, -xdiff, viewport.height);
			}
			if(ydiff > 0)//scroll up
			{
				bufferg.copyArea(0, 0, bimg.getWidth(null), (int)(OSTools.retinaMultiplier*(viewport.height - ydiff)*viewport.fontHeight), 0, (int) (OSTools.retinaMultiplier*ydiff*viewport.fontHeight));
				redrawArea(0, 0, viewport.startres, viewport.startseq, viewport.width, ydiff);
			}
			else if (ydiff < 0) //scroll down
			{
				bufferg.copyArea(0, (int)(-OSTools.retinaMultiplier*ydiff*viewport.fontHeight), bimg.getWidth(null), (int)(OSTools.retinaMultiplier*(viewport.height + ydiff)*viewport.fontHeight), 0, (int) (OSTools.retinaMultiplier*ydiff*viewport.fontHeight));
				redrawArea(0, viewport.height+ydiff, viewport.startres, viewport.endseq + 1 +ydiff, viewport.width, -ydiff);
			}
			if(!Alignment.al.changed.isEmpty())
			{//redraws changed residues
//				for(int y = viewport.startseq; y < viewport.startseq + viewport.height; y ++)
//				{
//					for(int x = viewport.startres; x < viewport.startres + viewport.width; x++)
//					{
//						if(Alignment.al.changed.contains(new ResiduePos(x,y)))
//								{
//									redrawArea(x - viewport.startres, y - viewport.startseq, x,y,1,1);
//								}
//					}
//				}
				this.redrawArea(0, 0, viewport.startres, viewport.startseq, viewport.width, viewport.height);
				Alignment.al.changed.clear();
			}

		}
//
        oldresbuffered = viewport.startres;
		oldseqbuffered = viewport.startseq;
	}

	public void redrawArea(int startrespos, int startseqpos, int startres, int startseq, int width, int height)
	{
		if(Alignment.al.columnIsSticky==null || Alignment.al.columnIsSticky.size()==0)
			return;
		Font oldfont;
		ResiduePos rp = new ResiduePos(0,0);
		for(int i = 0; i < height; i++ )
		{
			for(int j = 0; j < width; j++)
			{
				rp.location[0] = startres+j;
				rp.location[1] = startseq+i;
				final Residue r = Alignment.al.get(startseq+i).get(startres+j);
				bufferg.setFont(font2.deriveFont(OSTools.retinaMultiplier*1f*font2.getSize()));
                Image resImg;
				if(Alignment.al.columnIsSticky.get(startres+ j) && viewport.selected.contains(rp))
                {
                    resImg = Residue.imageSelectedStickyMap.get(r.getType());
                }
                else if(viewport.selected.contains(rp))
				{
                    resImg = Residue.imageSelectedMap.get(r.getType());

				}
                else if(Alignment.al.columnIsSticky.get(startres+ j))
                {
                    resImg = Residue.imageStickyMap.get(r.getType());

                }
				else
				{
                    resImg = Residue.imageMap.get(r.getType());

				}
                    bufferg.drawImage(resImg,(int)(OSTools.retinaMultiplier*viewport.fontWidth*(startrespos+j)), (int)(OSTools.retinaMultiplier*viewport.fontHeight*(startseqpos+i)),null);
				//There is a height offset
				//also, each string shifted 1 to the right
//				bufferg.drawString(r.toString(), OSTools.retinaMultiplier*viewport.fontWidth*(startrespos+j) + 1, OSTools.retinaMultiplier*viewport.fontHeight*(startseqpos+i) +  OSTools.retinaMultiplier*viewport.heightOffset);
				
				
			}
		}
	}
	
//	public void invertResidue(Residue r)
//	{
//		//DOESNT WORK AT ALL LOL
//		Color col;
//		for(int i = 0; i < viewport.fontHeight; i++)
//			for(int j = 0; j < viewport.fontWidth; i++)
//			{
//				int pixel = bimg.getRGB(j, i);
//				col = new Color (pixel, true);
//				col = new Color(Math.abs(col.getRed() - 255),
//                        Math.abs(col.getGreen() - 255), Math.abs(col.getBlue() - 255));
//				//bimg.setRGB(y, y, rgb);
//			}
//	}
	
	
	@Override
	public void setPreferredSize(Dimension d)
	{
		System.out.println("Resizing");
		super.setPreferredSize(d);
		viewport.updateDimensions(d);
	}
	
	Action deleteNormalAction = new MeatyAction("Delete",true,false) {
		@Override
		public void actionMeat(ActionEvent e)
		{
            if(!Alignment.al.deleteResidues) {
                Iterator<ResiduePos> prelimIter = (viewport.selected.iterator());
                while (prelimIter.hasNext()) {
                    ResiduePos curr = prelimIter.next();
                    if(!Alignment.al.get(curr.location[1]).get(curr.location[0]).isBlank())
                    {
                        Alignment.al.helpText.setText("You have attempted to delete a non-blank residue. \nThis is currently not allowed. To change this, enable \"Allow residue deletion\" from the Options menu.");
                        return;
                    }
                }
            }


			ResiduePos lastrespos = null;
			Iterator<ResiduePos> iter = ((TreeSet<ResiduePos>)viewport.selected).descendingIterator();
			int min = 9999999;
			Set<Integer> seqset = new HashSet<Integer>();
			int lastres = 0;
			if(Alignment.al.netBlock.busy==true)
			{
				JOptionPane.showMessageDialog(null, "Please wait for the score to be computed before editing the alignment.");
				return;
			}


			while(iter.hasNext())
			{
				ResiduePos rp =  iter.next();
				Alignment.al.currentEdit.currentStartingSelected.add(new ResiduePos(rp.location[0],Alignment.al.viewmap.get(rp.location[1])));
				Alignment.al.currentEdit.currentEndingSelected.add(new ResiduePos(rp.location[0],Alignment.al.viewmap.get(rp.location[1])));

				lastrespos = rp;
				seqset.add(rp.location[1]);
				lastres = rp.location[0];
				if(rp.location[0]<min)
					min=rp.location[0];

				if(!Alignment.al.stickyMode)
				Alignment.al.deleteResidueNormal(rp.location[1], rp.location[0]);
				else
				{
					Alignment.al.deleteResidueSticky(rp.location[1], rp.location[0], -1);
					
				}
				
			}
			
			Iterator<Integer> iter2 = seqset.iterator();
			for(int i = Alignment.al.columnIsSticky.size(); i <= Alignment.al.longestSeq; i ++)
			{
				Alignment.al.columnIsSticky.add(false);
			}
			//ScoreBlock block = Alignment.al.sbm.getBlock(lastres);
			while(iter2.hasNext())
			{
				int current = iter2.next();
				

			Alignment.al.netBlock.seqChange(Alignment.al.viewmap.get(current));

			}

			viewport.jsbh.setMaximum(Alignment.al.longestSeq - viewport.width + 10);
			//Alignment.al.setStickyColumns();
			repaint();
			Alignment.al.currentEdit.pushToTreeAndScore();
			Alignment.al.urt.redoNodes.clear();
			//Alignment.al.currentEdit = new EditStack(al);
			Alignment.al.ct.stalledUpdate(lastrespos.location[0]-Alignment.al.panel.canvas.viewport.startres);
		}
	};
	Action insertNormalGapAction = new MeatyAction("Insert Gap",true,false) {
		@Override
		public void actionMeat(ActionEvent e) {
			
			ResiduePos firstrespos= null;
			//System.out.println("LOLNB " + Alignment.al.sbm.getBlock(3).startres + " " + Alignment.al.sbm.getBlock(3).endres);
			Iterator<ResiduePos> iter = viewport.selected.iterator();
//			int min = 9999999;
			Set<Integer> seqset = new HashSet<Integer>();
			int lastres = 0;
//			if(Alignment.al.netBlock.busy==true) //not needed since scoring separation 
//				JOptionPane.showMessageDialog(null, "Please wait for the score to be computed before editing the alignment.");
//				return;
//			}
			while(iter.hasNext())
			{
			
				ResiduePos rp = iter.next();
				Alignment.al.currentEdit.currentStartingSelected.add(new ResiduePos(rp.location[0],Alignment.al.viewmap.get(rp.location[1])));
				Alignment.al.currentEdit.currentEndingSelected.add(new ResiduePos(rp.location[0],Alignment.al.viewmap.get(rp.location[1])));

				if(firstrespos==null)
					firstrespos = rp;
				seqset.add(rp.location[1]);
				lastres = rp.location[0];
//				if(rp.location[0]<min)
//					min=rp.location[0];
				if(Alignment.al.stickyMode)
				{
					Alignment.al.insertResidueSticky(rp.location[1], rp.location[0], -1,new Residue('-')); 
				}
				else
				{
				Alignment.al.insertResidueNormal(rp.location[1], rp.location[0], new Residue('-'));
				}
				
				
			}
			System.out.println("Seqset" + seqset);
			Iterator<Integer> iter2 = seqset.iterator();
			
			//ScoreBlock block = Alignment.al.sbm.getBlock(lastres);
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
//			if(!Alignment.al.busyUndo) dont think this actually gets called in the case of undo
			Alignment.al.urt.redoNodes.clear();
			
			//Alignment.al.setStickyColumns();
			repaint();
			Alignment.al.ct.stalledUpdate(firstrespos.location[0]-Alignment.al.panel.canvas.viewport.startres);

			
			
		}
	};
	
	Action moveRightAction = new MeatyAction("",false,true) {

		@Override
		public void actionMeat(ActionEvent e) {
			boolean add = false;
            SimilarEngine.firstExtend=true;
			Iterator<ResiduePos> iter = viewport.selected.iterator();
			while(iter.hasNext())
			{
				ResiduePos current = iter.next();
				Alignment.al.changed.add(new ResiduePos(current.location[0], current.location[1]));
				if(current.location[0]<Alignment.al.longestSeq-1)
				{

					current.location[0]++;
//					else
//					{
//						viewport.selected.add(new ResiduePos(current.location[0], current.location[1]))
//					}
					if(current.location[0]>viewport.endres)
					{
						viewport.jsbh.setValue(viewport.jsbh.getValue()+1);
					}
				}
					
				Alignment.al.changed.add(current);
				
				
			}
			repaint();
			// TODO Auto-generated method stub
			
		}
	
		
	};
	
	Action addMoveRightAction = new MeatyAction("",false,true) {

		@Override
		public void actionMeat(ActionEvent e) {
            SimilarEngine.firstExtend=true;
			boolean add = false;
			Set<ResiduePos> copyset = (Set<ResiduePos>) ((TreeSet<ResiduePos>)viewport.selected).clone();
			Iterator<ResiduePos> iter = copyset.iterator();
			while(iter.hasNext())
			{
				ResiduePos current = iter.next();
				Alignment.al.changed.add(new ResiduePos(current.location[0], current.location[1]));
				if(current.location[0]<Alignment.al.longestSeq-1)
				{

					viewport.selected.add(new ResiduePos(current.location[0]+1, current.location[1]));
//					else
//					{
//						viewport.selected.add(new ResiduePos(current.location[0], current.location[1]))
//					}
					if(current.location[0]>viewport.endres)
					{
						viewport.jsbh.setValue(viewport.jsbh.getValue()+1);
					}
				}
					
				Alignment.al.changed.add(current);
				
				
			}
			repaint();
			// TODO Auto-generated method stub
			
		}
	
		
	};
	Action moveLeftAction = new MeatyAction("",false,true) {

		@Override
		public void actionMeat(ActionEvent e) {
			System.out.println("moving left");
            SimilarEngine.firstExtend=true;
			Iterator<ResiduePos> iter = viewport.selected.iterator();
			while(iter.hasNext())
			{
				ResiduePos current = iter.next();
				Alignment.al.changed.add(new ResiduePos(current.location[0], current.location[1]));
				//System.out.println(current.location[0] + " " + current.location[1]);
				if(current.location[0]>0)
				{
					current.location[0]--;
					if(current.location[0]<viewport.startres)
					{
						viewport.jsbh.setValue(viewport.jsbh.getValue()-1);
					}
				}
					
				Alignment.al.changed.add(current);
				
				
			}
			repaint();
			// TODO Auto-generated method stub
			
		}
	
		
	};
	
	Action addMoveLeftAction = new MeatyAction("",false,true) {

		@Override
		public void actionMeat(ActionEvent e) {
            SimilarEngine.firstExtend=true;
			Set<ResiduePos> copyset = (Set<ResiduePos>) ((TreeSet<ResiduePos>)viewport.selected).clone();
			Iterator<ResiduePos> iter = copyset.iterator();
			while(iter.hasNext())
			{
				ResiduePos current = iter.next();
				Alignment.al.changed.add(new ResiduePos(current.location[0], current.location[1]));
				if(current.location[0]>0)
				{
					viewport.selected.add(new ResiduePos(current.location[0]-1, current.location[1]));
					if(current.location[0]<viewport.startres)
					{
						viewport.jsbh.setValue(viewport.jsbh.getValue()-1);
					}
				}
					
				Alignment.al.changed.add(current);
				
				
			}
			repaint();
			// TODO Auto-generated method stub
			
		}
	
		
	};
	
	 
	
	Action moveUpAction = new MeatyAction("",false,true) {

		@Override
		public void actionMeat(ActionEvent e) {
            SimilarEngine.firstExtend=true;
			Iterator<ResiduePos> iter = viewport.selected.iterator();
			while(iter.hasNext())
			{
				ResiduePos current = iter.next();
				Alignment.al.changed.add(new ResiduePos(current.location[0], current.location[1]));
				if(current.location[1]>0)
				{
					current.location[1]--;
					if(current.location[1]<viewport.startseq)
					{
						viewport.jsbv.setValue(viewport.jsbv.getValue()-1);
					}
				}
					
				Alignment.al.changed.add(current);
				
				
			}
			repaint();
			// TODO Auto-generated method stub
			
		}
	
		
	};
	
	Action addMoveUpAction = new MeatyAction("",false,true) {

		@Override
		public void actionMeat(ActionEvent e) {
            SimilarEngine.firstExtend=true;
			Set<ResiduePos> copyset = (Set<ResiduePos>) ((TreeSet<ResiduePos>)viewport.selected).clone();
			Iterator<ResiduePos> iter = copyset.iterator();
			while(iter.hasNext())
			{
				ResiduePos current = iter.next();
				Alignment.al.changed.add(new ResiduePos(current.location[0], current.location[1]));
				if(current.location[1]>0)
				{
					viewport.selected.add(new ResiduePos(current.location[0], current.location[1]-1));
					
					if(current.location[1]<viewport.startseq)
					{
						viewport.jsbv.setValue(viewport.jsbv.getValue()-1);
					}
				}
					
				Alignment.al.changed.add(current);
				
				
			}
			repaint();
			// TODO Auto-generated method stub
			
		}
	
		
	};
	
	Action moveDownAction = new MeatyAction("",false,true) {

		@Override
		public void actionMeat(ActionEvent e) {
            SimilarEngine.firstExtend=true;
			Set<ResiduePos> copyset = (Set<ResiduePos>) ((TreeSet<ResiduePos>)viewport.selected).clone();
			Iterator<ResiduePos> iter = copyset.iterator();
			while(iter.hasNext())
			{
				ResiduePos current = iter.next();
				Alignment.al.changed.add(new ResiduePos(current.location[0], current.location[1]));
				if(current.location[1]<Alignment.al.size()-1)
				{
					current.location[1]++;
					if(current.location[1]>viewport.endseq)
					{
						viewport.jsbv.setValue(viewport.jsbv.getValue()+1);
					}
				}
					
				Alignment.al.changed.add(current);
				
				
			}
			repaint();
			// TODO Auto-generated method stub
			
		}
	
		
	};
	
	Action addMoveDownAction = new MeatyAction("",false,true) {

		@Override
		public void actionMeat(ActionEvent e) {
            SimilarEngine.firstExtend=true;
			Iterator<ResiduePos> iter = viewport.selected.iterator();
			Set <ResiduePos> cs = (Set<ResiduePos>) ((TreeSet<ResiduePos>)viewport.selected).clone();
			Iterator<ResiduePos>iter2 = cs.iterator();
			while(iter2.hasNext())
			{

                ResiduePos current = iter2.next();
				Alignment.al.changed.add(new ResiduePos(current.location[0], current.location[1]));
				if(current.location[1]<Alignment.al.size()-1)
				{
					
					viewport.selected.add(new ResiduePos(current.location[0], current.location[1]+1));
					
					if(current.location[1]>viewport.endseq)
					{
						viewport.jsbv.setValue(viewport.jsbv.getValue()+1);
					}
				}
					
				Alignment.al.changed.add(current);
				
				
			}
			repaint();
			// TODO Auto-generated method stub
			
		}
	
		
	};
	
	Action switchStickyAction = new MeatyAction(){

		
		@Override
		public void actionMeat(ActionEvent e) {
			Alignment.al.toggleSticky();
			
			if(Alignment.al.stickyMode)
			{
			Alignment.al.panel.topPanel.stickyButtonLabel.setText("Disable");
			Alignment.al.panel.topPanel.btnStickyMode.setSelected(true);
			}
			else
			{
			Alignment.al.panel.topPanel.stickyButtonLabel.setText("Enable");
			Alignment.al.panel.topPanel.btnStickyMode.setSelected(false);
			}
			
			System.out.println(Alignment.al.panel.topPanel.btnStickyMode.getSize());
			Alignment.al.panel.topPanel.btnStickyMode.setPreferredSize(Alignment.al.panel.topPanel.stickyButtonDim);
			Alignment.al.panel.topPanel.btnStickyMode.setMinimumSize(Alignment.al.panel.topPanel.stickyButtonDim);
			Alignment.al.panel.topPanel.btnStickyMode.setMaximumSize(Alignment.al.panel.topPanel.stickyButtonDim);
			// TODO Auto-generated method stub
			
		}
		
	};
	
	Action clearSelectionAction = new MeatyAction("",false,true){

		
		@Override
		public void actionMeat(ActionEvent e) {
			ResiduePos one = viewport.selected.iterator().next();
			viewport.selected.clear();
			viewport.selected.add(one);
			Alignment.al.changed.add(one);
			repaint();

			
			// TODO Auto-generated method stub
			
		}
		
	};
	
	Action shiftReleasedAction = new MeatyAction("",false,false){

		
		@Override
		public void actionMeat(ActionEvent e) {
//			if(firstExtend==false) MOVED TO S KEY RELEASE
//			{
//			firstExtend = true;
//			Alignment.al.similarBound = defaultBound;
//			Alignment.al.similarJump = defaultjump;
//			System.out.println("endedshift");
//			}
			// TODO Auto-generated method stub
			
		}
		
	};
	
	Action keyPressAAction = new MeatyAction("",true,false)
	{

		@Override
		public void actionMeat(ActionEvent e) {
//			System.out.println("A PRESSED Y'ALL");
			if(isKeyPressed.get("a"))
				return;
			isKeyPressed.put("a", true);
			mousePressedSinceLastKey=false;
			// TODO Auto-generated method stub
			
		}
		
	};
	Action keyReleaseAAction = new MeatyAction("",true,false)
	{

		@Override
		public void actionMeat(ActionEvent e) {
			System.out.println("A released");
			isKeyPressed.put("a", false);
			if(mousePressedSinceLastKey==false)
			{
				Alignment.al.alignSelectedRegion();

			}
			// TODO Auto-generated method stub
			
			
		}
		
	};
	Action keyPressSAction = new MeatyAction()
    {

        @Override
        public void actionMeat(ActionEvent e) {
            if(isKeyPressed.get("s"))
                return;
            isKeyPressed.put("s", true);
            mousePressedSinceLastKey=false;
            // TODO Auto-generated method stub

        }

    };
    Action keyReleaseSAction = new MeatyAction()
    {


        @Override
        public void actionMeat(ActionEvent e) {
            isKeyPressed.put("s", false);

            // TODO Auto-generated method stub
            if(SimilarEngine.firstExtend ==true)
            {
                SimilarEngine.selectSimilarMain(true);
            }
            Alignment.al.panel.cma.findingSimilar=false;
        }

    };







}
