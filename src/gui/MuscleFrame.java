package gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Font;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.border.BevelBorder;


public class MuscleFrame extends JFrame {
	public AtomicBoolean completedAlignment;
	public JPanel contentPane;
	public JTextField currentActionField;
	public JTextArea completedArea;
	JProgressBar progressBar;
	public JTextField memUsedField;
	public JTextField timeField;
	public JButton btnCancel;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MuscleFrame frame = new MuscleFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MuscleFrame() {
		completedAlignment = new AtomicBoolean(false);
//		try {
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//		} catch (ClassNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (InstantiationException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IllegalAccessException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (UnsupportedLookAndFeelException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
//NIMBUS FEEL
//		try {
//		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//		        if ("Nimbus".equals(info.getName())) {
//		            UIManager.setLookAndFeel(info.getClassName());
//		            break;
//		        }
//		    }
//		} catch (Exception e) {
//		    // If Nimbus is not available, you can set the GUI to another look and feel.
//		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 357, 439);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel currentProgress = new JPanel();
		
		JPanel progressHistory = new JPanel();
		
		JScrollPane historyScrollPane = new JScrollPane();
		
		JLabel lblCompletedActions = new JLabel("Completed Actions");
		GroupLayout gl_progressHistory = new GroupLayout(progressHistory);
		gl_progressHistory.setHorizontalGroup(
			gl_progressHistory.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_progressHistory.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_progressHistory.createParallelGroup(Alignment.LEADING)
						.addComponent(lblCompletedActions, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
						.addComponent(historyScrollPane, GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_progressHistory.setVerticalGroup(
			gl_progressHistory.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_progressHistory.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblCompletedActions, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(historyScrollPane, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		completedArea = new JTextArea();
		completedArea.setFont(new Font("Tahoma", Font.PLAIN, 11));
		historyScrollPane.setViewportView(completedArea);
		progressHistory.setLayout(gl_progressHistory);
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					if(completedAlignment.get()) //if we're done, launch editing in IMPALE.
					{
						MuscleFrame.this.dispose();
						RunEverything re = new RunEverything(new File(RunCommand.muscleOutpath));
						re.run();
					}
					else
					{
						  int response = JOptionPane.showConfirmDialog(null,"Are you sure you would like to cancel the muscle alignment?", "Confirm cancellation",JOptionPane.YES_NO_OPTION);
							 if(response == JOptionPane.YES_OPTION)
							{	
								 RunCommand.muscleCancelled.set(true);
								 try{
								RunCommand.process.destroy();
								 }
								 catch(Exception ex)
								 {
									 ex.printStackTrace();
									 System.err.println("There was a problem destroying the muscle process. It may still be running and consuming memory!");
								 }
								MuscleFrame.this.dispose();
								JOptionPane.showMessageDialog(null, "Your old alignment will now be reopened.");
								RunEverything re = new RunEverything(IO.lastFile);
								re.run();

							}
							 else
							 {
								 return;
							 }

					}
				
				//OH SHIT...
			}
		});
		
		JPanel panel = new JPanel();
		
		JLabel label = new JLabel("Memory Used");
		
		memUsedField = new JTextField();
		memUsedField.setColumns(10);
		
		JLabel label_1 = new JLabel("Time Elapsed");
		
		timeField = new JTextField();
		timeField.setColumns(10);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(label)
						.addComponent(label_1))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(timeField, GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
						.addComponent(memUsedField, GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGap(0, 91, Short.MAX_VALUE)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(20)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label)
						.addComponent(memUsedField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label_1)
						.addComponent(timeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(20, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(currentProgress, GroupLayout.PREFERRED_SIZE, 305, Short.MAX_VALUE)
						.addComponent(progressHistory, GroupLayout.PREFERRED_SIZE, 305, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(panel, GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnCancel)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(currentProgress, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(progressHistory, GroupLayout.PREFERRED_SIZE, 193, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnCancel)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		currentActionField = new JTextField();
		currentActionField.setColumns(10);
		
		
		JLabel lblCurrentAction = new JLabel("Current Action");
		
		progressBar = new JProgressBar();
		GroupLayout gl_currentProgress = new GroupLayout(currentProgress);
		gl_currentProgress.setHorizontalGroup(
			gl_currentProgress.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_currentProgress.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_currentProgress.createParallelGroup(Alignment.LEADING)
						.addComponent(currentActionField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
						.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
						.addComponent(lblCurrentAction, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_currentProgress.setVerticalGroup(
			gl_currentProgress.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_currentProgress.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblCurrentAction)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(currentActionField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(14, Short.MAX_VALUE))
		);
		currentProgress.setLayout(gl_currentProgress);
		contentPane.setLayout(gl_contentPane);
//		currentProgress
		Dimension d = progressHistory.getPreferredSize();
		d.width=100;
		progressHistory.setMinimumSize(d);
		d = currentProgress.getPreferredSize();
		d.width=100;
		currentProgress.setMinimumSize(d);
		completedArea.setText("");
		completedArea.setEditable(false);
		currentActionField.setEditable(false);
		memUsedField.setEditable(false);
		timeField.setEditable(false);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
	}
}
