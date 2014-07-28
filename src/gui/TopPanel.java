package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
public class TopPanel extends JPanel {
//Alignment al;
ScoreBlock scoreBlock;
JButton btnGlobalSort;
JButton btnLocalSort;
JButton btnInitialSort;
JLabel progressFiller;
public boolean lsort, gsort;
JLabel scoreLabel;
JLabel maxLabel, diffLabel;
JPanel scorePanel;
JPanel stickyPanel;
JPanel busyPanel;
HelpPanel helpPanel;
JToggleButton btnStickyMode;
JRadioButton radioLocal, radioGlobal, radioDefault;
ButtonGroup sortGroup;
Dimension stickyButtonDim;
JTextArea helpArea;
ScoreGraph scoreGraph;
JPanel chartPanel;
JLabel stickyButtonLabel;
public ImageIcon busyTrueIcon, busyFalseIcon;
public  JLabel busyLabel;
public JLabel editsInQLabel;
public JProgressBar scoreProgressBar;
public JPanel progPanel;
Dimension  progBarSize;

	public TopPanel()
	{
		super();
		progressFiller = new JLabel("FUCK");
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		btnStickyMode = new JToggleButton(Alignment.al.panel.canvas.switchStickyAction);
		btnStickyMode.setLayout(new BoxLayout(btnStickyMode, BoxLayout.Y_AXIS));
		stickyButtonLabel = new JLabel("Disable");
		busyTrueIcon = new ImageIcon(getClass().getResource("/busytrue.gif"));
		busyFalseIcon = new ImageIcon(getClass().getResource("/busyfalse.gif"));
		System.out.println("busyicon " + busyFalseIcon);
		busyLabel = new JLabel();
		busyLabel.setIcon(busyTrueIcon);
		busyLabel.setMaximumSize(busyLabel.getPreferredSize());
		busyLabel.setMinimumSize(busyLabel.getPreferredSize());
		System.out.println(busyLabel.getPreferredSize() + " prefsize");
		JLabel l1 = new JLabel("Sticky");
		JLabel l2 = new JLabel("Mode");
		l1.setAlignmentX(CENTER_ALIGNMENT);
		l2.setAlignmentX(CENTER_ALIGNMENT);
		stickyButtonLabel.setAlignmentX(CENTER_ALIGNMENT);
		btnStickyMode.add(stickyButtonLabel);
		btnStickyMode.add(l1);
		btnStickyMode.add(l2);
		
		System.out.println("NB" + btnStickyMode.getPreferredSize());
		btnStickyMode.setFocusable(false);
		btnStickyMode.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		btnStickyMode.setToolTipText("Toggle Sticky Mode. In sticky mode, edits made to a non-bold section of the alignment (i.e. a poorly conserved region) do not affect the rest of the alignment. Shortcut: SPACEBAR");
		
		stickyButtonDim = new Dimension(btnStickyMode.getPreferredSize().width, btnStickyMode.getPreferredSize().height);
//		btnStickyMode.setPreferredSize(btnStickyMode.getPreferredSize());
//		btnStickyMode.setMaximumSize(btnStickyMode.stickyButtonDim);
//		btnStickyMode.setMinimumSize(btnStickyMode.getPreferredSize());
		//btnStickyMode.setText("Enable Sticky Mode");
		//btnStickyMode.setText("Enable Sticky Mode");
		stickyButtonLabel.setText("Enable");
		btnStickyMode.revalidate();
		btnStickyMode.setPreferredSize(stickyButtonDim);
		btnStickyMode.setMinimumSize(stickyButtonDim);
		btnStickyMode.setMaximumSize(stickyButtonDim);
		radioLocal = new JRadioButton(localSortAction);
		radioGlobal = new JRadioButton(globalSortAction);
		radioDefault = new JRadioButton(initialSortAction);
		sortGroup = new ButtonGroup();
		sortGroup.add(radioLocal);
		sortGroup.add(radioGlobal);
		sortGroup.add(radioDefault);
		sortGroup.setSelected(radioDefault.getModel(), true);

		scorePanel = new JPanel();
		BoxLayout scoreLayout = new BoxLayout(scorePanel, BoxLayout.Y_AXIS);
		scorePanel.setLayout(scoreLayout);
		lsort = false;
		gsort = false;
		scorePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
		//radioPanel.add(Box.createHorizontalGlue());
		radioLocal.setAlignmentX(LEFT_ALIGNMENT);
		radioGlobal.setAlignmentX(LEFT_ALIGNMENT);
		radioDefault.setAlignmentX(LEFT_ALIGNMENT);
		Font ft = radioLocal.getFont();
		ft = new Font(ft.getName(), ft.getStyle(), 10);
		radioLocal.setFont(ft);
		radioLocal.setToolTipText("Reorder the sequences by decreasing order of similarity to the selected sequence within the current non-bold block.");
		radioGlobal.setFont(ft);
		radioGlobal.setToolTipText("Reorder the sequences by decreasing order of similarity to the selected sequence over the entire length of the sequence.");
		radioDefault.setFont(ft);
		radioDefault.setToolTipText("Reorder the sequences to match the ordering of the initially loaded file");
		//radioLocal.setFont()
		radioPanel.add(radioLocal);
		radioPanel.add(radioGlobal);
		radioPanel.add(radioDefault);
		radioPanel.setMaximumSize(radioPanel.getPreferredSize());
		radioPanel.setMinimumSize(radioPanel.getPreferredSize());
		//radioPanel.add(Box.createHorizontalGlue());
		//radioPanel.add(empty1.add(Box.createHorizontalStrut(10)));
		//Dimension dim = radioPanel.getPreferredSize();
		stickyPanel = new JPanel ();
//		empty1.setMaximumSize(dim);
//		empty1.setMinimumSize(dim);
//		empty1.setPreferredSize(dim);
		stickyPanel.setLayout(new BoxLayout(stickyPanel, BoxLayout.X_AXIS));
		//empty1.add(Box.createHorizontalGlue());
		stickyPanel.add(btnStickyMode);
		//empty1.add(Box.createHorizontalGlue());
		stickyPanel.setAlignmentX(LEFT_ALIGNMENT);
;
		busyPanel = new JPanel();
		busyPanel.setPreferredSize(new Dimension(70,70));
		busyPanel.setLayout(new BoxLayout(busyPanel, BoxLayout.Y_AXIS));
		busyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		progPanel = new JPanel();
		progPanel.setPreferredSize(new Dimension(50,21));
		progPanel.setMinimumSize(progPanel.getPreferredSize());
		progPanel.setMaximumSize(progPanel.getPreferredSize());
		scoreProgressBar = new JProgressBar(0,100);
		scoreProgressBar.setValue(0);
		scoreProgressBar.setPreferredSize(new Dimension(46,16));
		
		 editsInQLabel =new JLabel("0"){
			 @Override
			 public void setText(String text)
			 {
				 super.setText(text);
				 this.setMaximumSize(this.getPreferredSize());
			 }
		 };
		 editsInQLabel.setToolTipText("The number of edits yet to be scored");
		 scoreProgressBar.setToolTipText("Current edit scoring progress");
		 busyLabel.setToolTipText("Indicates whether scoring is occuring");
//		editsInQLabel.setForeground(Color.RED);
		 editsInQLabel.setAlignmentX(CENTER_ALIGNMENT);
//		editsInQLabel.setBounds(0,0,(int)editsInQLabel.getPreferredSize().getWidth(), (int)editsInQLabel.getPreferredSize().getHeight());
//		busyLPane.add(new JLabel("FUUU"),new Integer(0));
		progressFiller.setBounds(0,0,(int)progressFiller.getPreferredSize().getWidth(), (int)progressFiller.getPreferredSize().getHeight());
//		progPanel.add(progressFiller, new Integer(0)); 
//		scoreProgressBar.setBounds(0,0,progPanel.getWidth(), progPanel.getHeight());
		 
		 
		progPanel.setAlignmentX(CENTER_ALIGNMENT);
		busyPanel.setAlignmentX(CENTER_ALIGNMENT);
		busyPanel.add(busyLabel);
		progressFiller.setAlignmentX(CENTER_ALIGNMENT);
		busyPanel.add(editsInQLabel);
		busyPanel.add(progPanel);
		progBarSize = new Dimension(scoreProgressBar.getSize().width,scoreProgressBar.getSize().height);
//		scoreProgressBar.setPreferredSize(new Dimensio);
//		scoreProgressBar.setValue(90);
		
		editsInQLabel.setText("-");
		
		helpPanel = new HelpPanel();
		helpPanel.setToolTipText("The help panel. It displays runtime hints to maximise your utility of this program.");
		//helpPanel.add(helpArea);
		//radioPanel.setMaximumSize(dim);
		radioPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
//		JButton butty = new JButton("A");
//		butty.setAlignmentX(Component.CENTER_ALIGNMENT);
//		busyPanel.add(butty);
		radioLocal.setFocusable(false);
		radioGlobal.setFocusable(false);
		radioDefault.setFocusable(false);
		scoreLabel = new JLabel();
		maxLabel = new JLabel();
		diffLabel = new JLabel();
		Font f = new Font("monospaced", maxLabel.getFont().getStyle(),  maxLabel.getFont().getSize());
		maxLabel.setFont(f);
		diffLabel.setFont(f);
		scoreLabel.setFont(f);
		
		//localSort.setAction
		scorePanel.add(maxLabel);
		maxLabel.setToolTipText("The highest score achieved since the alignment was loaded. If the colour is red, a new maximum has been achieved.");
		scorePanel.add(scoreLabel);
		scoreLabel.setToolTipText("The current Sum-Of-Pairs score achieved, as determined by the scoring settings in the options menu.");
		scorePanel.add(diffLabel);
		diffLabel.setToolTipText("The change in score induced by the last edit");
		
		scoreLabel.setText("Loading score...");
		this.setPreferredSize(new Dimension(50,70));
		scoreGraph = new ScoreGraph();
		chartPanel = new JPanel();
		chartPanel.add(scoreGraph);
		
		this.add(Box.createHorizontalGlue());
		this.add(stickyPanel);
		this.add(Box.createHorizontalGlue());
		this.add(busyPanel);
		this.add(Box.createHorizontalGlue());		
		this.add(scorePanel);
		this.add(chartPanel);
//		this.add(scoreGraph);
		this.add(Box.createHorizontalGlue());
		
//		this.add(radioLocal);
//		this.add(radioGlobal);
//		this.add(radioDefault);

		//this.add(Box.createHorizontalGlue());
//		this.add(radioLocal);
//		this.add(radioGlobal);
//		this.add(radioDefault);
		this.add(radioPanel);
		this.add(Box.createHorizontalGlue());
		this.add(helpPanel);
		this.add(Box.createHorizontalGlue());

		
		helpPanel.FreezeDimension();
//		scoreProgressBar.setBounds(0,0,66,29);
//		scoreProgressBar.setBounds(0, 0, progPanel.getWidth(), progPanel.getHeight());
		progPanel.add(scoreProgressBar);
		
//		progressFiller.set
		
		
		//this.setMinimumSize(new Dimension(100,100));
		
		Alignment.al.helpText = helpPanel.helpText;
		Alignment.al.helpText.setText("This is the quick help panel. Keep an eye on it for hints as you explore the program! Browse the Help menu or hover your mouse over item labels for further details.");
		
	}
	
	public Action globalSortAction = new MeatyAction ("Sort by global",false,true) {

		@Override
		public void actionMeat(ActionEvent e) {
			
			if(gsort)
			{
				gsort = false;
				
				//btnGlobalSort.setSelected(false);
			}
			else
			{
				Alignment.al.helpText.setText("Left-Click on a residue in the alignment below to sort the sequences based on similarity to that sequence, over its entire length, in descending order.");
				gsort = true;
				lsort = false;
				//btnGlobalSort.setSelected(true);
			}
			Alignment.al.resetSelection();
			Alignment.al.panel.canvas.requestFocus();
			
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public Action initialSortAction = new MeatyAction ("Sort by initial order",false,true) {

		
		@Override
		public void actionMeat(ActionEvent e) {
			Alignment.al.helpText.setText("You have rearranged the sequences to order them as they were initially loaded");
			for(int i = 0; i < Alignment.al.viewmap.size(); i++)
			{
				Alignment.al.viewmap.set(i,i);
				Alignment.al.inverseviewmap.set(i,i);
			}
			Alignment.al.panel.canvas.requestFocus();
			Alignment.al.changed.add(new ResiduePos(0,0));
			Alignment.al.panel.canvas.repaint();
			Alignment.al.panel.headers.rh.repaint();
			lsort = false;
			gsort = false;
			// TODO Auto-generated method stub
			Alignment.al.resetSelection();
		}
		
	};
	
	public Action localSortAction = new MeatyAction ("Sort by local",false,true) {

		@Override
		public void actionMeat(ActionEvent e) {
			Alignment.al.helpText.setText("Left-Click on a residue in the alignment below to sort the sequences based on similarity to that sequence, within the selected block, in descending order.");
			if(lsort)
			{
			lsort = false;

			}
			else
			{
			gsort = false;
			lsort = true;
			}
			
			
			
			// TODO Auto-generated method stub
			Alignment.al.resetSelection();
		}
		
	};
	
	class HelpPanel extends JPanel{
		public JTextArea helpText;
		public Dimension initialDim;
		public HelpPanel()
		{
			super();
			helpText = new JTextArea();
			helpText.setFocusable(false);
			helpText.setFont(new Font("Dialog", Font.PLAIN, 12));
			//helpText.setText("                                                                                                           \n\n\n");
			helpText.setText("This is the quick help panel. \nKeep an eye on it for hints as you explore the program!\nBrowse Help or hover over items for further details.");
			this.add(helpText);
			
			helpText.setOpaque(false);
			helpText.setEditable(false);
			initialDim = super.getPreferredSize();
			//TitledBorder tit = BorderFactory.createTitledBorder("Help");
			//
			Border tit = BorderFactory.createSoftBevelBorder(BevelBorder.LOWERED);
			this.setBorder(tit);
			
		}
		public void FreezeDimension()
		{
			initialDim = new Dimension(super.getPreferredSize().width, super.getPreferredSize().height);
			helpText.setFont(new Font(helpText.getFont().getName(), helpText.getFont().getStyle(), 11));
			helpText.setPreferredSize(new Dimension(initialDim.width - 7, initialDim.height-2));
			helpText.setLineWrap(true);
			helpText.setWrapStyleWord(true);
		}
		
		public Dimension getPreferredSize()
		{
			return initialDim;
		}
		
		public Dimension getMaximumSize()
		{
			return initialDim;
		}
		
		public Dimension getMaxmimuSize()
		{
			return initialDim;
		}
	}
}
