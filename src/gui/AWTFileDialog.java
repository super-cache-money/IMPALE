package gui;

import java.awt.FileDialog;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class AWTFileDialog {
	
	public static File openFile()
	{
		JFrame dFrame = new JFrame();
		FileDialog fd = new FileDialog(dFrame, "Open alignment...", FileDialog.LOAD);
		fd.setVisible(true);
		
		while ( fd.getFiles()== null || fd.getFiles().length!=1)
		{
		  int response = JOptionPane.showConfirmDialog(null,"Would you like to continue trying to select a file?", "Retry file selection",JOptionPane.YES_NO_OPTION);
			 if(response == JOptionPane.NO_OPTION)
			{	
				return null;

			}
		  fd.dispose();
		  fd = new FileDialog(dFrame, "Choose a file", FileDialog.LOAD);
			fd.setVisible(true);
		  
		}
		File outFile = fd.getFiles()[0];
		
		 System.out.println("You chose " + outFile.getAbsolutePath());
		dFrame.dispose();
		return  outFile;
	}
	
	public static File saveFile()
	{
		JFrame dFrame = new JFrame();
		FileDialog fd = new FileDialog(dFrame, "Save alignment as...", FileDialog.SAVE);
		fd.setVisible(true);
		
		while ( fd.getFiles()== null || fd.getFiles().length!=1)
		{
		  int response = JOptionPane.showConfirmDialog(null,"Would you like to continue trying to select a file?", "Retry file selection",JOptionPane.YES_NO_OPTION);
			 if(response == JOptionPane.NO_OPTION)
			{	
				return null;

			}
		  fd.dispose();
		  fd = new FileDialog(dFrame, "Choose a file", FileDialog.LOAD);
			fd.setVisible(true);
		  
		}
		File outFile = fd.getFiles()[0];
		
		 System.out.println("You chose " + outFile.getAbsolutePath());
		dFrame.dispose();
		return  outFile;
	}

}
