package gui;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class RunCommand {
	static MuscleFrame muscleProgressFrame;
	static Process process;
	static AtomicBoolean muscleCancelled = new AtomicBoolean(false);
	public static void alignInMuscle(final String params)
	{
		muscleCancelled.set(false);
		Runnable dispatch = new Runnable()
		{

			@Override
			public void run() {
				Alignment.disposeCurrent();

				muscleDispatch(params);
				// TODO Auto-generated method stub
				
			}
			
		};
		
		
		int option = JOptionPane.showConfirmDialog(null, "Would you like to save like to perform a save, before sending the current alignment to Muscle?", "Save changes...", JOptionPane.YES_NO_OPTION);
		if(option==JOptionPane.YES_OPTION)
		{
			IO.writeFasta(false,dispatch);
		}
		else
		{
			dispatch.run();
		}
		
		

		
	}
	
	public static void restartWithMoreMemory()
	{
    	String os = System.getProperty("os.name").toLowerCase();
		int mem = Integer.parseInt(JOptionPane.showInputDialog("Due to the large size of your alignment, you need to make more RAM available. \nCurrently, IMPALE is allocated " + (int) (Runtime.getRuntime().totalMemory()/1024/1024) + "MB\nOn a 32bit System, you may only enter up to 4GB. \n Bear in mind that 1GB should be sufficient for almost all alignments. \nIMPALE will now restart. If you get a Virtual Machine error, try to allocate less RAM \nPlease enter the amount of RAM to make available (in MB):",1024));
    	CodeSource codeSource = RunEverything.class.getProtectionDomain().getCodeSource();
    	File jarFile = null;
//		String path=ClassLoader.getSystemClassLoader().getResource(".").getPath();
    	String path = RunEverything.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		jarFile = new File(path);
		 String parent = null;
		try {
			parent = URLDecoder.decode(jarFile.getAbsolutePath(),"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		 
		String relaunchcmd ="";
    	if(os.contains("windows"))
    	{

    	
		relaunchcmd = "CMD /C \"javaw -Xmx" + mem + "m -jar \""+ parent  +"\"\"";
		System.out.println(relaunchcmd);
    	}
    	else if(os.contains("mac"))
    	{
    		relaunchcmd = "/Library/Internet\\ Plug-Ins/JavaAppletPlugin.plugin/Contents/Home/bin/javaw -Xmx" + mem + "m -jar \""+ parent + "\"";
    	}
    	
    	try {
			Process process = Runtime.getRuntime().exec(relaunchcmd);
			
	            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

	            // Read command standard output
	            String s;
	            String errors = "";
	            //System.out.println("Standard output: ");
	            //System.out.println("Standard error: ");
	            System.out.println("Relaunch LOG:");
	            try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            if(stdError.ready())
	            while ((s = stdError.readLine()) != null) {
	                errors+=s+"\n";
	                System.out.println("WAITING");
	            }
	            if(errors!="")
	            {
	            JOptionPane.showMessageDialog(null, "You have probably attempted to allocate too much RAM. Please try again!\n Java error message:\n" + errors );
	            restartWithMoreMemory();
	            }
	            System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
	}
	
	public static String getPath() {
        String path = RunEverything.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = path;
        try {
            decodedPath = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        String absolutePath = decodedPath.substring(0, decodedPath.lastIndexOf("/"))+"\\";
        return absolutePath;
    }
  //  private static final String CMD =         "CMD /C \\muscle.exe -in C:\\Users\\arjun\\Documents\\test.fas";
	static JLabel output;
	static String muscleOutpath;
	static String jarDirPath;
	static String cmd;
	static List<String> cmdList;
	public static String quotify(String in)
	{
		return "'"+in+"'";
	}
	public static List<String> getMuscleCommandWin(String params)
	{
		File muscleExec = null;
		 
		try {
			muscleExec=new File(URLDecoder.decode(jarDirPath, "UTF-8") + File.separator+"dependencies"+File.separator+"muscle_win_32.exe");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		cmd = "CMD /C \""+ quotify(muscleExec.getAbsolutePath()) + " -in " + quotify(IO.tempfile.getAbsolutePath()) +" -out " + quotify(muscleOutpath) + " " + params + "\"";
		System.out.println(cmd);
		cmdList = new ArrayList<String>();
		cmdList.add(muscleExec.getAbsolutePath());
		cmdList.add("-in");
		cmdList.add(IO.tempfile.getAbsolutePath());
		cmdList.add("-out");
		cmdList.add(muscleOutpath);
		return cmdList;
		 
	}
	
	public static List<String> getMuscleCommandMac(String params)
	{
        
        File muscleExec = null;
		
		try {
			muscleExec=new File(URLDecoder.decode(jarDirPath, "UTF-8") + File.separator+"dependencies"+File.separator+"muscle_mac_32");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		cmd = "CMD /C \""+ quotify(muscleExec.getAbsolutePath()) + " -in " + quotify(IO.tempfile.getAbsolutePath()) +" -out " + quotify(muscleOutpath) + " " + params + "\"";
		System.out.println(cmd);
		return null;
	}
	
	public static List<String> getMuscleCommandLin(String params)
	{
       
        File muscleExec = null;
        
		try {
			
			muscleExec=new File(URLDecoder.decode(jarDirPath, "UTF-8") + File.separator+"dependencies"+File.separator+"muscle_lin_32");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cmd = muscleExec.getAbsolutePath() + " -in " + quotify(IO.tempfile.getAbsolutePath()) +" -out " + (muscleOutpath) + " " + params;
		System.out.println(cmd);
		 cmdList = new ArrayList<String>();
		cmdList.add(muscleExec.getAbsolutePath());
		cmdList.add("-in");
		cmdList.add(IO.tempfile.getAbsolutePath());
		cmdList.add("-out");
		cmdList.add(muscleOutpath);
//		cmdList.addAll(Arrays.asList(params.split(" ")));
		
		return cmdList;
		
	}
	
	public static void actionCompletedUpdate(String desc)
	{
		if(muscleProgressFrame.currentActionField.getText().equals(desc))
		{
			
		}
		else
		{
			if(muscleProgressFrame.completedArea.getText().length()>2)
				muscleProgressFrame.completedArea.setText(muscleProgressFrame.completedArea.getText()+"\n"+muscleProgressFrame.currentActionField.getText());
			else
				muscleProgressFrame.completedArea.setText(muscleProgressFrame.currentActionField.getText());
			muscleProgressFrame.currentActionField.setText(desc);
		}
	}
	public static void muscleDispatch(String params)
	{
    	output = new JLabel();
    	output.setText("Launching Muscle...");
        output.setPreferredSize(new Dimension(400, 25));
        output.setMinimumSize(new Dimension(400,25));
		muscleProgressFrame = new MuscleFrame();
//	     muscleProgressFrame.setTitle("Muscle Alignment Progress");
	     muscleProgressFrame.setVisible(true);

        File jardir = null;
        File muscleExec = null;
		String path=ClassLoader.getSystemClassLoader().getResource(".").getPath();
	    System.out.println("path to jar:" + path);
		jarDirPath = new File( path).getAbsolutePath();
		muscleOutpath = jarDirPath+File.separator + "temp" + File.separator+"UNSAVED";
		
		if(OSTools.isWindows())
			cmdList = getMuscleCommandWin(params);
		else if(OSTools.isMac())
			cmdList = getMuscleCommandMac(params);
		else if(OSTools.isUnix())
			cmdList = getMuscleCommandLin(params);
		else
		{
			JOptionPane.showMessageDialog(null, "Muscle alignment is not supported on your operating system. Supported OS's are:\n\nMac\nWindows\nLinux");
			return;
		}
		
		
		
		SwingWorker<Void, String> ProcessWorker = new SwingWorker<Void, String>(){
			String mem = "";
			String time = "";
			String iter = "";
			String percent = "";
			String desc = "";
			@Override
			protected Void doInBackground()  {
				try{
				
	            process = new ProcessBuilder(cmdList).start();

	           // output.setEditable(false);
	            System.out.println("Done");
	            // Get input streams
	            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
	            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

	            // Read command standard output
	            String s = null;
	            //System.out.println("Standard output: ");
	            //System.out.println("Standard error: ");
	            System.out.println(cmd);
	            System.out.println("Muscle log:");
	            char [] buffread = new char [15];
	            
	            int count = 0;
		            while(true)
		            {
		            	
		            try{
		            	if((s = stdError.readLine())==null)
		            	{
		            		break;
		            	}
		            }
		            catch(Exception ex)
		            {
		            	
		            	if((!new File(muscleOutpath).exists()) && muscleCancelled.get()==false)
		            	{
		            		throw ex;
		            	}
		            	else
		            	{
		            		ex.printStackTrace();
		            		System.err.println("Anomaly in process reading, but it shouldnt be a problem.");
		            	}
		            }
		                publish(s);
		                count+=s.length();
		                
//		                System.out.println(count);
	
		                
		            }
	            

	            process.waitFor();
		            		//probably don't need this
//	            while((s = stdInput.readLine()) != null)
//	            {
//	            	System.out.println(s);
//	            }
	            
	            actionCompletedUpdate("Done! Press \"Finish\" to begin editing.");
	            muscleProgressFrame.btnCancel.setText("Finish");
	            muscleProgressFrame.completedAlignment.set(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					JOptionPane.showMessageDialog(null,"There was a problem aligning in MUSCLE. Sorry.");
				}
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			protected void done()
			{
				output.setText("Muscle Alignment Completed!");
				
	            //IO.openFasH
			}
			@Override
			protected void process(List<String> outputlines)
			{
				String lastLine = outputlines.get(outputlines.size()-1);
                System.out.println(lastLine);
				String[] tokens = lastLine.split("\\s+");
				if(tokens.length<6)
					return;
				time = tokens[0];
				mem = tokens[1]+" "+tokens[2];
				iter = tokens [4];
				percent = tokens[5].substring(0,tokens[5].indexOf("."));
				desc = "Iteration " + iter+": ";
				for(int i = 6; i < tokens.length; i++)
					desc+=tokens[i]+" ";
				
			    SwingUtilities.invokeLater(new Runnable(){

					@Override
					public void run() {
						muscleProgressFrame.memUsedField.setText(mem);
						muscleProgressFrame.progressBar.setValue(Integer.parseInt(percent));
						actionCompletedUpdate(desc);
						
						// TODO Auto-generated method stub
						
					}});
			     
//				output.setText(outputlines.get(outputlines.size()-1));
			}
			
     	
     };
     ProcessWorker.execute();
     //JOptionPane.showMe

//     muscleProgressFrame = new MuscleFrame();
////     muscleProgressFrame.setTitle("Muscle Alignment Progress");
//     muscleProgressFrame.setVisible(true);
//     muscleProgressFrame.setPreferredSize(new Dimension(500,200));
//     muscleProgressFrame.setLayout(new BorderLayout());
//     muscleProgressFrame
//     muscleProgressFrame.setLayout(new BoxLayout(muscleProgressFrame,BoxLayout.Y_AXIS));
     
//     JOptionPane.showMessageDialog(null, output, "Muscle Progress", JOptionPane.PLAIN_MESSAGE);

     
     	


			
		

	}

}
