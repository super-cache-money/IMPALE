package gui;

import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.MenuBar;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

public class WorkDispatcher extends SwingWorker  {

	
	InputMap savedInputMap;
	JMenuBar savedMenu;
	ActionMap savedActionMap;
	static WorkDispatcher currentDispatcher;
	boolean blockme = false;
	Runnable threadedTask = null;
	Runnable doneTask = null;
	AtomicBoolean busy;
	String progressMessage;
	public WorkDispatcher(Runnable threadedTask, Runnable doneTask, String progressMessage)
	{
		super();
		this.progressMessage = progressMessage;
		busy = new AtomicBoolean(true);
		Alignment.al.helpText.setText(progressMessage);
		if(currentDispatcher!=null && !(currentDispatcher.isDone()))
			blockme = true;
		else
		{
			blockme = false;
			currentDispatcher=this;
//			blockInput();
		}
		
		this.threadedTask = threadedTask;
		this.doneTask = doneTask;
		
		
//		Cursor.
		
	}
	
	static void submit(Runnable threadedTask, Runnable doneTask, String messages)
	{
		if(currentDispatcher!=null && !(currentDispatcher.isDone()))
			return;
		currentDispatcher=new WorkDispatcher(threadedTask,doneTask,messages);
		currentDispatcher.blockInput();
		currentDispatcher.execute();
		
	}
	

	@Override
	protected Void doInBackground() throws Exception {
		Thread.currentThread().setPriority((Thread.MAX_PRIORITY+Thread.MIN_PRIORITY)/2);
		if(blockme)
			return null ;
		try{
		threadedTask.run();
//		try {
//		Thread.sleep(5000);
//	} catch (InterruptedException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void done()
	{
		
		unBlockInput();
		Alignment.al.helpText.setText("");
		doneTask.run();
		busy.set(false);
		
//		this.i
	}
	
	public void blockInput()
	{
//		JPanel glassPanel = new JPanel(new GridLayout(0, 1));
//		JLabel nothing = new JLabel();
//		glassPanel.add(nothing);
//		glassPanel.addMouseListener(new MouseAdapter() {});
//        glassPanel.addMouseMotionListener(new MouseMotionAdapter() {});
//        glassPanel.addKeyListener(new KeyAdapter() {});
//        glassPanel.setOpaque(false);
//        CanvasMouseAdapter cma = Alignment.al.panel.cma;
        Alignment.al.panel.canvas.removeMouseListener(Alignment.al.panel.cma);
        Alignment.al.panel.canvas.removeMouseMotionListener(Alignment.al.panel.cma);
        
//        Alignment.al.panel.canvas.removeKeyListener(Alignment.al.panel.canvas);
        InputMap actualInputMap = Alignment.al.panel.canvas.getInputMap();
        if(savedInputMap==null)
        {
        	savedInputMap = new InputMap();
        	KeyStroke [] strokes = actualInputMap.keys();
        	for(KeyStroke stroke : strokes)
        	{
        		savedInputMap.put(stroke, actualInputMap.get(stroke));
        	}
        	
        }
     	KeyStroke [] strokes = actualInputMap.keys();
    	for(KeyStroke stroke : strokes) //for some reason, they can never be added back properly if removed.
    	{    		
    		actualInputMap.put(stroke, "none");
    	}
//        actualInputMap.clear();
//        savedInputMap = Alignment.al.panel.canvas.getInputMap();
        savedMenu = RunEverything.currentRe.menu;
//        savedActionMap = Alignment.al.panel.canvas.getActionMap();
//        Alignment.al.panel.canvas.setActionMap(new ActionMap());
//        Alignment.al.panel.canvas.setInputMap(JComponent.WHEN_FOCUSED,new InputMap());
//        Alignment.al.panel.menu.setEnabled(false);
        Alignment.al.panel.menu.disarm();
//        RunEverything.currentRe.jf.remove(Alignment.al.panel.menu);
//        RunEverything.currentRe.jf.setJMenuBar(new JMenuBar());
//        Alignment.al.panel.menu.setVisible(false);
//        Cursor.get;
//        Alignment.al.panel.superframe.setCu
//        Alignment.al.panel.canvas.removeMouseWheelListener(l)
//        Alignment.al.panel.superframe

        Alignment.al.panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Alignment.al.panel.superframe.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

	}
	
	public void unBlockInput()
	{
//		Alignment.al.panel.superframe.setCursor(Cursor.getDefaultCursor());
		Alignment.al.panel.superframe.setCursor(Cursor.getDefaultCursor());
		Alignment.al.panel.setCursor(Cursor.getDefaultCursor());
        Alignment.al.panel.canvas.addMouseListener(Alignment.al.panel.cma);
        Alignment.al.panel.canvas.addMouseMotionListener(Alignment.al.panel.cma);
//        Alignment.al.panel.canvas.setInputMap(JComponent.WHEN_FOCUSED,savedInputMap);
        InputMap actualInputMap = Alignment.al.panel.canvas.getInputMap();
        KeyStroke [] keys = savedInputMap.keys();
        for(KeyStroke key : keys)
        {
        	
        	actualInputMap.put(key, savedInputMap.get(key));
        }
//        Alignment.al.panel.menu.setEnabled(true);
        Alignment.al.panel.menu.arm();
//        MainMenu mb = null;
//        RunEverything.currentRe.jf.setJMenuBar(savedMenu);
        
//        Alignment.al.panel.menu.setVisible(true);
//        Alignment.al.panel.canvas.setActionMap(savedActionMap);
//		Alignment.al.panel.
		
	}
	

//	protected void this.

}
