package gui;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

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
public class SaveFileChooser extends javax.swing.JFrame/**
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

	
	public SaveFileChooser() {
		super();

		
		//initGUI();
	}
	
	public File getFile() {
		File f = null;
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

			{
				
				jFileChooser1 = new JFileChooser();
				int returnval = jFileChooser1.showSaveDialog(this);
				if(returnval == JFileChooser.APPROVE_OPTION)
				{
					f = jFileChooser1.getSelectedFile(); 
				}
				
				//getContentPane().add(jFileChooser1, BorderLayout.CENTER);
				jFileChooser1.setPreferredSize(new java.awt.Dimension(550, 332));
				
			}
			pack();
			this.setSize(621, 377);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
		this.dispose();
		return f;
	}

	


}