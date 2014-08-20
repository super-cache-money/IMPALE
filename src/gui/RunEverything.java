package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;



public class RunEverything implements Runnable{
String titleBase = "IMPALE 1.28";
static RunEverything currentRe;
Alignment al;
JFrame jf;
 PaintingPanel pp;
 MainMenu menu;
public RunEverything(File f) //file to load on start, if null, it uses the filechooser.
{
	
	currentRe = this;
	//this.al = null;
try{
//	OpenFileChooser chooser = new OpenFileChooser();
//	NativeOpenFileChooser nchooser = new NativeOpenFileChooser();
	if(f==null)
		f=AWTFileDialog.openFile();	
	if(f==null) //load -.fas
	{
		File jarfile = null;
		String path=ClassLoader.getSystemClassLoader().getResource(".").getPath();
	    System.out.println("path to jar:" + path);
		jarfile = new File( path);
		try {
			f=new File(URLDecoder.decode(jarfile.getAbsolutePath(), "UTF-8") + File.separator+"config"+File.separator+"-.fas");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		//	 f = chooser.getFileFromDialog(null);
	IO.lastFile = f;
	al = IO.openFasta();
//	String [] splitarr = filename.split(Pattern.quote(F));
}
catch(OutOfMemoryError e)
{
	e.printStackTrace();
	RunCommand.restartWithMoreMemory();
}

}
public RunEverything(Alignment al)
{
	this.al = al;
	currentRe = this;
}
	@Override
	public void run() {
		
		jf = new JFrame();
		
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
		if(!IO.lastFile.getName().equals("-.fas"))
			jf.setTitle(titleBase+" : " + IO.lastFile.getName());
		else
			jf.setTitle(titleBase);
		jf.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/icon.jpg")));  
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		pp = null;
		TopPanel topPanel = null;
		pp = new PaintingPanel( jf);
		jp.add(pp, BorderLayout.CENTER);
		jf.setPreferredSize(new Dimension(1024,500));	
//		jf.setExtendedState(jf.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		topPanel = new TopPanel();
		Alignment.al.prepare();
		
		
		pp.topPanel = topPanel;
		pp.cma.tp = topPanel;
		 menu = new MainMenu();
		jf.setJMenuBar(menu);
		jf.setMinimumSize(new Dimension(1024, 500));
		jp.add(topPanel, BorderLayout.NORTH);
		jf.setContentPane(jp);
		jf.pack();
		jf.setVisible(true);
		jf.revalidate();
		System.out.println(topPanel.getSize());
		System.out.println(pp.headers.rh.getSize());
		jf.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		

		jf.addWindowListener(new WindowAdapter() {
		        public void windowClosing(WindowEvent e) {
		          menu.tryToClose();
		        }
		      });
		if(!IO.lastFile.getName().equals("-.fas"))
			//note: this also sets stickyColumns
			 IO.tryToLoadSession(al, IO.lastOpenedMD5);
		
		
		// TODO Auto-generated method stub

	}
	
	public static void main (String []args)
	{
        OSTools.validateJVM();
		//		if(args[0]!=null)
//			IO.lengthmult = Integer.parseInt(args[0]);
//		if(args[1]!=null)
//			IO.widthmult = Integer.parseInt(args[1]);
		System.out.println("l " + IO.lengthmult + " w " + IO.widthmult);
	    try {
            // Set System L&F
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
//	    	 UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
    } 
    catch (Exception e) {
    	e.printStackTrace();
       // handle exception
    }
//    catch (ClassNotFoundException e) {
//       // handle exceptionx`
//    }
//    catch (InstantiationException e) {
//       // handle exception
//    }
//    catch (IllegalAccessException e) {
//       // handle exception
//    }
//			String path = RunEverything.class  
//			           .getProtectionDomain().getCodeSource().getLocation().toURI().getPath(); 
//		CodeSource codeSource = RunEverything.class.getProtectionDomain().getCodeSource();
		File jarfile = null;
        String path = "";
        try {
            path = ClassLoader.getSystemClassLoader().getResource(".").getPath();
        }
        catch (NullPointerException np)
        {
            np.printStackTrace(); //it is actually a problem
            System.exit(1);
        }

	    System.out.println("path to jar:" + path);
		jarfile = new File( path);
//		String parent = jarfile.getParent();
		RunEverything app = null;
		try {
			app = new RunEverything( new File(URLDecoder.decode(jarfile.getAbsolutePath(), "UTF-8") + File.separator+"config"+File.separator+"-.fas"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			SwingUtilities.invokeAndWait(app);
		}
		
		
     catch (Exception ex) {
    	 ex.printStackTrace();
    	 if(ex.getCause() instanceof java.lang.OutOfMemoryError)
    	 {
        
        
        app.jf.dispose();
        app.al = null;
        app.pp = null;
        app = null;
        
        System.gc();
        RunCommand.restartWithMoreMemory();
    	 }
    	 else
    		 JOptionPane.showMessageDialog(null, "YOLO");
    }

	}

}
