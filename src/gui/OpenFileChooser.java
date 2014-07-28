package gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class OpenFileChooser extends javax.swing.JFrame/**
	 * 
	 */

{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFileChooser jFileChooser1;
	public File selected;
	private JPanel jPanelMain;
	/**
	* Auto-generated main method to display this JFrame
	*/

	
	public OpenFileChooser() {
		super();

		
		//initGUI();
	}
	
	public File getFileFromDialog(String fileToOpen) {

		

		

//		if(recentFiles==null)
//		{
//			recentFiles = new ArrayList<String>();
//			recentFiles.add("would be empty");
//		}
//		String [] arr=  (String[]) recentFiles.toArray(new String [recentFiles.size()]);
		
//		JList<String> filelist = new JList<String>(arr);
		
//		jPanelMain = new JPanel(new FlowLayout());
//		this.add(jPanelMain);
////		filelist.setPreferredSize(new Dimension(100,100));
//		filelist.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
//		filelist.setLayoutOrientation(JList.HORIZONTAL_WRAP);
//		filelist.setVisibleRowCount(-1);
//		JScrollPane listScroller = new JScrollPane(filelist);
//		listScroller.setPreferredSize(new Dimension(250, 80));
//		listScroller.setMinimumSize(new Dimension(250, 80));
//		jPanelMain.add(listScroller);
//		this.setVisible(true);
		
		File f = null;
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

			{
//				JPanel extraPanel = new JPanel();
				
				jFileChooser1 = new JFileChooser();
				int returnval = jFileChooser1.showOpenDialog(jPanelMain);
				if(fileToOpen!=null)
				{
					f = new File(fileToOpen);
				}
				else if(returnval == JFileChooser.APPROVE_OPTION)
				{
					f = jFileChooser1.getSelectedFile(); 
				}
				
				//getContentPane().add(jFileChooser1, BorderLayout.CENTER);
				jFileChooser1.setPreferredSize(new java.awt.Dimension(550, 332));
//				jPanelMain.add(this);
			}
			pack();

			
			
//			this.setSize(621, 377);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
		
		ObjectOutputStream  oos = null;
	
		


		
		this.dispose();
		return f;
	}

	


}