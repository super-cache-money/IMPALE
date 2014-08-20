package gui;

import javafx.beans.property.adapter.JavaBeanProperty;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainMenu extends JMenuBar{
    abstract class SettingsPanel extends JPanel{
        public SettingsPanel()
        {
            setBorder(new EmptyBorder(5, 5, 5, 5) );
        }
        abstract void captureSettings();
    }

//	Alignment al;
	JRadioButtonMenuItem [] zoomLevelRadioItems;
	public JMenuItem filler;
	public JMenu file, edit, options, recentFilesMenu, macrosMenu,zoomMenu;
	JMenuItem translateItem, settingsItem, alignmentInfoItem,  debuggingItem,similarSelectItem, alignItem,similarOptionItem, muscleItem,openItem, saveItem, saveAsItem,prefItem, undoItem, redoItem, recomputeItem, exitItem, blankItem, resItem, delItem, scoringOptionsItem, stickyOptionsItem, loadAminoAcidMatrix;
	JCheckBoxMenuItem enableScoringItem, enableResDeleteItem;
    JSlider retinaQualitySlider;
    //TODO implement enableScoringItem
	JRadioButtonMenuItem radioProfile, radioMax;
	ButtonGroup snapGroup,zoomGroup;
	int muscle_maxMemory = 4096; 
	int muscle_maxIterations = 8;
	boolean recentFilesSet = false;
	boolean macroSet = false;
    boolean scoringChanged = false;

    Dimension settingsWindowSize = new Dimension(470,350);

    String muscle_ClusteringMethod12  = "UPGMB";
	String muscle_ClusteringMethodOther = "UPGMB";
	
	int muscle_lambda = 24;
	public  JLabel busyLabel;
	public MainMenu()
	{
		super();
//		this.al = al;
		buildZoomMenu();
		Alignment.al.panel.menu = this;
		snapGroup = new ButtonGroup();
		
		radioProfile = new JRadioButtonMenuItem(toggleProfileAction);
		radioMax = new JRadioButtonMenuItem(toggleProfileAction);
		radioProfile.setText("Profile Snap-Aligning (Accurate)");
		radioMax.setText("Simple Snap-Aligning (Fast)");
		radioProfile.setToolTipText("Use the ratio of the presence of each residue when snap-aligning");
		radioMax.setToolTipText("Only use the most common residue as the consensus when snap-aligning");
		

		file = new JMenu("File");
		edit = new JMenu("Edit");
		similarOptionItem = new JMenuItem(similarSettingsAction);
		
		options = new JMenu("Options");
		openItem = new JMenuItem(openAction);
		saveItem = new JMenuItem(saveAction);
		saveAsItem = new JMenuItem(saveAsAction);
		prefItem = new JMenuItem("Preferences");
		recomputeItem = new JMenuItem(recomputeAction);
		undoItem = new UndoItem(undoAction);
		redoItem = new RedoItem(redoAction);
		exitItem = new JMenuItem(exitAction);
		similarSelectItem = new JMenuItem(similarSelectAction);
		//similarSelectItem.setToolT
		muscleItem = new JMenuItem(muscleAction);
		stickyOptionsItem = new JMenuItem(stickyThresholdAction);
		scoringOptionsItem = new JMenuItem(scoreSettingsAction);
		loadAminoAcidMatrix = new JMenuItem(loadAAMatrixAction);
		alignItem = new JMenuItem(snapAlignAction);
		resItem = new JMenuItem(Alignment.al.panel.canvas.insertNormalGapAction);
		delItem = new JMenuItem(Alignment.al.panel.canvas.deleteNormalAction);
		settingsItem = new JMenuItem(showSettingsAction);
		alignmentInfoItem = new JMenuItem(alignmentInfoAction);
		enableScoringItem = new JCheckBoxMenuItem(enableScoringAction);
		enableResDeleteItem = new JCheckBoxMenuItem(enableResDeleteAction);
        enableScoringItem.setSelected(true);
        this.add(file);
		this.add(edit);
		this.add(options);
		debuggingItem = new JMenuItem(debugAction);
		this.add(debuggingItem); 
		file.add(openItem);
		file.add(saveItem);
		file.add(saveAsItem);
		file.add(muscleAction);
		file.add(exitItem);
		
		edit.add(undoItem);
		edit.add(redoItem);
		edit.add(resItem);
		edit.add(delItem);
		edit.add(alignItem);
		edit.add(similarSelectItem);
		options.add(radioProfile);
		options.add(radioMax);
		options.addSeparator();
		options.add(enableScoringItem);
        options.add(enableResDeleteItem);
        options.addSeparator();
        options.add(zoomMenu);
        options.add(recomputeItem);
        options.add(alignmentInfoItem);
        options.add(settingsItem);
        if(OSTools.isRetina())
        {
            retinaQualitySlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 0);
            retinaQualitySlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    JSlider source = (JSlider)e.getSource();
                    if (!source.getValueIsAdjusting()) {
                        int newValue = (int)source.getValue();

                        float newRetinaMult = (1f+(newValue+0f)/100f);
                        System.out.println("retMult " + newRetinaMult );
                        //TODO maybe only change mult if it hasn't been changed already
                        OSTools.retinaMultiplier=newRetinaMult;
                        Residue.buildImageMaps();
                        Alignment.al.panel.canvas.viewport.componentResized(null);
//                        Alignment.al.panel.canvas.viewport.updateDimensions(Alignment.al.panel.canvas.getSize());

                    }
                }
            });
            options.add(retinaQualitySlider);
        }
		snapGroup.add(radioProfile);
		snapGroup.add(radioMax);
		snapGroup.setSelected(radioProfile.getModel(),true);
		
//		options.add(stickyOptionsItem);
//		options.add(scoringOptionsItem);
//		options.add(similarOptionItem);

		//redoItem.setEnabled(false);
		undoItem.setAccelerator(KeyStroke.getKeyStroke("control Z"));
		redoItem.setAccelerator(KeyStroke.getKeyStroke("control Y"));
		saveItem.setAccelerator(KeyStroke.getKeyStroke("control S"));
		saveAsItem.setAccelerator(KeyStroke.getKeyStroke("control shift S"));
		exitItem.setAccelerator(KeyStroke.getKeyStroke("control Q"));
		openItem.setAccelerator(KeyStroke.getKeyStroke("control O"));
		resItem.setAccelerator(KeyStroke.getKeyStroke('-'));
		delItem.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
		alignItem.setAccelerator(KeyStroke.getKeyStroke("A"));    //this
		similarSelectItem.setAccelerator(KeyStroke.getKeyStroke("S")); //and this may cause issues! These are actually handled by a KeyBoardListener, which appears to intercept presses first, luckily. This is just to add the hotkey description in the menu.
		openItem.setToolTipText("Closes the current file, and opens a new one.");
		undoItem.setToolTipText("Reverses the last action performed");
		redoItem.setToolTipText("Repeats the last action which was undone");
		exitItem.setToolTipText("Close the program");
		similarSelectItem.setToolTipText("Selects the same areas in similar sequences to those currently selected.");
		zoomMenu.setToolTipText("Set the zoom level");
		alignmentInfoItem.setToolTipText("Get details about the makeup of the alignment.");
		enableScoringItem.setToolTipText("Choose whether to enable scoring");
		scoringOptionsItem.setToolTipText("Set the gap penalities and substitution matrix parameters used when calculating the score.");
		stickyOptionsItem.setToolTipText("Set the criteria which determine which columns are sticky(i.e. emboldened).");
		resItem.setToolTipText("Inserts a residue at each point in the selected area. The type of insertion depends on whether sticky mode is enabled.");
		delItem.setToolTipText("Deletes each residue currently selected. The type of deletion depends on whether sticky mode is enabled");
		alignItem.setToolTipText("Snap-aligns the currently selected area to the consensus. If one residue for a given sequence is selected, the entire segment of the sequence falling within the region will be aligned.");
		muscleItem.setToolTipText("Aligns the set of sequences usign the MUSCLE algorithm");
		saveAsItem.setToolTipText("Choose where to save a copy of the current alignment");
		saveItem.setToolTipText("Save the current alignment to the current file");
		recomputeItem.setToolTipText("Recomputes the sticky regions and score of the alignment from scratch - useful if something is not functioning as it should. ");
		similarOptionItem.setToolTipText("Set the criteria which determine the strictness of the similar sequences detection");
		buildRecentFiles();
		buildMacros();
		enableResDeleteItem.setToolTipText("Allow deletion of non-blank residues in the alignment.");
		
		
		if(IO.lastFile.getName().equals("-.fas"))
		{	
			edit.setEnabled(false);
			saveAction.setEnabled(false);
			saveAsAction.setEnabled(false);
			muscleAction.setEnabled(false);
			alignmentInfoAction.setEnabled(false);
		}
		
	}

    Action translateAction = new MeatyAction("Translate", true,true) {
        @Override
        public void actionMeat(ActionEvent e) {

            Runnable actuallyTranslate = new Runnable() {
                @Override
                public void run() {
                    IO.translateDNA();
                }
            };
            int option = JOptionPane.showConfirmDialog(null, "Would you like to save like to perform a save, before sending the current alignment to Muscle?", "Save changes...", JOptionPane.YES_NO_OPTION);
            if(option==JOptionPane.YES_OPTION)
            {
                IO.writeFasta(false,actuallyTranslate);
            }
            else
            {
                actuallyTranslate.run();
            }
        }
    };
    Action showSettingsAction = new MeatyAction("Settings...",false,false) {
        @Override
        public void actionMeat(ActionEvent e) {
            showSettings();
        }
    };
	
	Action recomputeAction = new MeatyAction("Recompute Everything", false, false){

		@Override
		public void actionMeat(ActionEvent e) {
			//JOptionPane.showInputDialog("Due to the large size of your alignment, more memory is required. IMPALE will now restart. You need to make more of your System RAM available for IMPALE. On a 32bit System, you may only enter up to 4GB. Bear in mind that 3GB should be sufficient for almost all alignments. Please enter the amount of RAM to make available (in GB):",10);
			
			Alignment.al.changed.add(new ResiduePos(0,0));
			Alignment.al.panel.topPanel.scoreLabel.setText("Recalculating score...");
			Alignment.al.stickiesSet=false;
			Alignment.al.sw.recomputeEverything = true;
			Alignment.al.setStickyColumns(true);
		}
		
		
	};
	
	Action toggleProfileAction = new MeatyAction("", false,false){

		@Override
		public void actionMeat(ActionEvent e) {
			if(Alignment.al.alignToProfile)
				Alignment.al.alignToProfile = false;
			else
				Alignment.al.alignToProfile = true;
		
			return;
		}
		
		
	};

	Action muscleAction = new MeatyAction("Align using MUSCLE",false,false){

		@Override
		public void actionMeat(ActionEvent e) {
			
			JPanel musclePanel = new JPanel();
			musclePanel.setLayout(new BoxLayout(musclePanel, BoxLayout.Y_AXIS));

			
			JPanel maxIterationsPanel = new JPanel();
			maxIterationsPanel.setLayout(new BoxLayout(maxIterationsPanel, BoxLayout.X_AXIS));
			JSpinner maxIterationsSpinner = new JSpinner(new SpinnerNumberModel(muscle_maxIterations, 1, 100, 1));
			JLabel maxIterationsLabel = new JLabel();
			maxIterationsLabel.setToolTipText("The maximum number of MUSCLE iterations. More iterations could result in better alignments, but take more time to compute.");
			maxIterationsLabel.setText("Max Iterations");
			maxIterationsSpinner.setAlignmentX(RIGHT_ALIGNMENT);
			maxIterationsLabel.setAlignmentX(LEFT_ALIGNMENT);
			maxIterationsPanel.add(maxIterationsLabel);
			maxIterationsPanel.add(Box.createHorizontalGlue());
			maxIterationsPanel.add(maxIterationsSpinner);
			musclePanel.add(maxIterationsPanel);
			

			
			JPanel clusteringMethod12Panel = new JPanel();
			clusteringMethod12Panel.setLayout(new BoxLayout(clusteringMethod12Panel, BoxLayout.X_AXIS));
			String [] list = {"Neighbour-Joining", "UPGMB", "UPGMA" };
			JSpinner clusteringMethod12Spinner = new JSpinner(new SpinnerListModel(list));
			JLabel clusteringMethod12Label = new JLabel();
			clusteringMethod12Label.setToolTipText("The clustering method of the first two iterations of MUSCLE");
			clusteringMethod12Label.setText("Clustering Method(Iterations 1,2)");
			clusteringMethod12Spinner.setAlignmentX(RIGHT_ALIGNMENT);
			clusteringMethod12Label.setAlignmentX(LEFT_ALIGNMENT);
			clusteringMethod12Panel.add(clusteringMethod12Label);
			clusteringMethod12Panel.add(Box.createHorizontalGlue());
			clusteringMethod12Panel.add(clusteringMethod12Spinner);
			musclePanel.add(clusteringMethod12Panel);
			
			JPanel clusteringMethodOtherPanel = new JPanel();
			clusteringMethodOtherPanel.setLayout(new BoxLayout(clusteringMethodOtherPanel, BoxLayout.X_AXIS));
			String [] list2 = {"UPGMB", "UPGMA", "Neighbour-Joining"};
			JSpinner clusteringMethodOtherSpinner = new JSpinner(new SpinnerListModel(list2));
			JLabel clusteringMethodOtherLabel = new JLabel();
			clusteringMethodOtherLabel.setToolTipText("The clustering method used from the 3rd iteration onwards");
			clusteringMethodOtherLabel.setText("Clustering Method(Iterations 3+)");
			clusteringMethodOtherSpinner.setAlignmentX(RIGHT_ALIGNMENT);
			clusteringMethodOtherLabel.setAlignmentX(LEFT_ALIGNMENT);
			clusteringMethodOtherPanel.add(clusteringMethodOtherLabel);
			clusteringMethodOtherPanel.add(Box.createHorizontalGlue());
			clusteringMethodOtherPanel.add(clusteringMethodOtherSpinner);
			musclePanel.add(clusteringMethodOtherPanel);
			
			JPanel lambdaPanel = new JPanel();
			lambdaPanel.setLayout(new BoxLayout(lambdaPanel, BoxLayout.X_AXIS));
			JSpinner lambdaSpinner = new JSpinner(new SpinnerNumberModel(muscle_lambda, 0, 1000, 1));
			JLabel lambdaLabel = new JLabel();
			lambdaLabel.setToolTipText("The minimum diagonal length(lambda)");
			lambdaLabel.setText("Minimum Diagonal Length");
			lambdaSpinner.setAlignmentX(RIGHT_ALIGNMENT);
			lambdaLabel.setAlignmentX(LEFT_ALIGNMENT);
			lambdaPanel.add(lambdaLabel);
			lambdaPanel.add(Box.createHorizontalGlue());
			lambdaPanel.add(lambdaSpinner);
			musclePanel.add(lambdaPanel);
			
			JPanel gapOpenPanel = new JPanel();
			gapOpenPanel.setLayout(new BoxLayout(gapOpenPanel, BoxLayout.X_AXIS));
			JSpinner gapOpenSpinner = new JSpinner(new SpinnerNumberModel(Sequence.scoreGapOpen, -1000, 0, 1));
			JLabel gapOpenLabel = new JLabel();
			gapOpenLabel.setText("Gap Open Penalty");
			gapOpenLabel.setToolTipText("This is the penalty attributed to the opening of a new gap within the alignment");
			gapOpenSpinner.setAlignmentX(RIGHT_ALIGNMENT);
			gapOpenLabel.setAlignmentX(LEFT_ALIGNMENT);
			gapOpenPanel.add(gapOpenLabel);
			gapOpenPanel.add(Box.createHorizontalGlue());
			gapOpenPanel.add(gapOpenSpinner);
			musclePanel.add(gapOpenPanel);
			
			

		
			JPanel gapExtensionPanel = new JPanel();
			gapExtensionPanel.setLayout(new BoxLayout(gapExtensionPanel, BoxLayout.X_AXIS));
			JSpinner gapExtensionSpinner = new JSpinner(new SpinnerNumberModel(Sequence.scoreGapExtension, -1000, 0, 1));
			JLabel gapExtensionLabel = new JLabel();
			gapExtensionLabel.setToolTipText("This is the penalty attributed to the extension of an already open gap within the alignment.");
			gapExtensionLabel.setText("Gap Extension Penalty");
			gapExtensionSpinner.setAlignmentX(RIGHT_ALIGNMENT);
			gapExtensionLabel.setAlignmentX(LEFT_ALIGNMENT);
			gapExtensionPanel.add(gapExtensionLabel);
			gapExtensionPanel.add(Box.createHorizontalGlue());
			gapExtensionPanel.add(gapExtensionSpinner);
			musclePanel.add(gapExtensionPanel);
			
			
			JPanel maxMemoryPanel = new JPanel();
			maxMemoryPanel.setLayout(new BoxLayout(maxMemoryPanel, BoxLayout.X_AXIS));
			JSpinner maxMemorySpinner = new JSpinner(new SpinnerNumberModel(muscle_maxMemory, 100, 1000000, 1024));
			JLabel maxMemoryLabel = new JLabel();
			maxMemoryLabel.setToolTipText("The maximum amount of RAM (IN MB) able to be used by muscle. Allocate as much as you can afford.");
			maxMemoryLabel.setText("Max Memory(MB)");
			maxMemorySpinner.setAlignmentX(RIGHT_ALIGNMENT);
			maxMemoryLabel.setAlignmentX(LEFT_ALIGNMENT);
			maxMemoryPanel.add(maxMemoryLabel);
			maxMemoryPanel.add(Box.createHorizontalGlue());
			maxMemoryPanel.add(maxMemorySpinner);
			musclePanel.add(maxMemoryPanel);
			
			JPanel otherOptionsPanel = new JPanel();
			maxMemoryPanel.setLayout(new BoxLayout(maxMemoryPanel, BoxLayout.X_AXIS));
			JTextField otherOptionsText = new JTextField();
			JLabel otherOptionsLabel = new JLabel();
			otherOptionsLabel.setToolTipText("Any additional muscle commands you would like to add. EG: -param1 value1 -param2 valu2");
			otherOptionsLabel.setText("Other Commands");
			otherOptionsText.setAlignmentX(RIGHT_ALIGNMENT);
			otherOptionsLabel.setAlignmentX(LEFT_ALIGNMENT);
			otherOptionsPanel.add(otherOptionsLabel);
			otherOptionsPanel.add(Box.createHorizontalGlue());
			otherOptionsPanel.add(otherOptionsText);
			musclePanel.add(otherOptionsPanel);
			
			clusteringMethodOtherSpinner.setMaximumSize(clusteringMethod12Spinner.getPreferredSize());
			clusteringMethod12Spinner.setMaximumSize(clusteringMethod12Spinner.getPreferredSize());
			gapOpenSpinner.setMaximumSize(clusteringMethod12Spinner.getPreferredSize());
			gapExtensionSpinner.setMaximumSize(clusteringMethod12Spinner.getPreferredSize());
			lambdaSpinner.setMaximumSize(clusteringMethod12Spinner.getPreferredSize());
			maxIterationsSpinner.setMaximumSize(clusteringMethod12Spinner.getPreferredSize());
			maxMemorySpinner.setMaximumSize(clusteringMethod12Spinner.getPreferredSize());
			
			clusteringMethodOtherSpinner.setMinimumSize(clusteringMethod12Spinner.getPreferredSize());
			clusteringMethod12Spinner.setMinimumSize(clusteringMethod12Spinner.getPreferredSize());
			gapOpenSpinner.setMinimumSize(clusteringMethod12Spinner.getPreferredSize());
			gapExtensionSpinner.setMinimumSize(clusteringMethod12Spinner.getPreferredSize());
			lambdaSpinner.setMinimumSize(clusteringMethod12Spinner.getPreferredSize());
			maxIterationsSpinner.setMinimumSize(clusteringMethod12Spinner.getPreferredSize());
			maxMemorySpinner.setMinimumSize(clusteringMethod12Spinner.getPreferredSize());

			clusteringMethodOtherSpinner.setPreferredSize(clusteringMethod12Spinner.getPreferredSize());
			clusteringMethod12Spinner.setPreferredSize(clusteringMethod12Spinner.getPreferredSize());
			gapOpenSpinner.setPreferredSize(clusteringMethod12Spinner.getPreferredSize());
			gapExtensionSpinner.setPreferredSize(clusteringMethod12Spinner.getPreferredSize());
			lambdaSpinner.setPreferredSize(clusteringMethod12Spinner.getPreferredSize());
			maxIterationsSpinner.setPreferredSize(clusteringMethod12Spinner.getPreferredSize());
			maxMemorySpinner.setPreferredSize(clusteringMethod12Spinner.getPreferredSize());
			
			otherOptionsText.setPreferredSize(clusteringMethod12Spinner.getPreferredSize());
			otherOptionsText.setMinimumSize(clusteringMethod12Spinner.getPreferredSize());
			otherOptionsText.setMaximumSize(clusteringMethod12Spinner.getPreferredSize());
			

			//clusteringMethod12Spinner.setModel(new SpinnerListModel(list3));
			clusteringMethod12Spinner.getModel().setValue("UPGMB");
			
			JOptionPane.showMessageDialog(null, musclePanel, "MUSCLE Settings", JOptionPane.PLAIN_MESSAGE);
			String param = "-gapopen " + gapOpenSpinner.getValue() + " -gapextend " + gapExtensionSpinner.getValue() +  " -maxmb " + maxMemorySpinner.getValue() + " -maxiters " + maxIterationsSpinner.getValue() + " -cluster1 " + clusteringMethod12Spinner.getValue() + " -cluster2 " + clusteringMethodOtherSpinner.getValue() + " -diaglength " + lambdaSpinner.getValue() + " " + otherOptionsText.getText();
			if(IO.lastSeqWasProtein)
				param += " -seqtype protein";
			else
				param += " -seqtype DNA";
			RunCommand.alignInMuscle(param);

			
		}
		
	};
	public void buildZoomMenu()
	{
		zoomLevelRadioItems = new JRadioButtonMenuItem [19];
		zoomMenu = new JMenu("Set Zoom Level");
		zoomGroup = new ButtonGroup();
		for(int i = 18;i>3;i--)
		{
			final int currentI = i;
			Action currAction = new MeatyAction((100*i/12)+"%", false, false){
				
				@Override
				public void actionMeat(ActionEvent e) {
                    Alignment.al.panel.canvas.viewport.zoomToFontSize(currentI);
					Alignment.al.helpText.setText("To zoom in and out quickly, hold Control and scroll the mousewheel.");


					
				}
				
			};
			zoomLevelRadioItems[i] = new JRadioButtonMenuItem(currAction);
			zoomGroup.add(zoomLevelRadioItems[i]);
			zoomMenu.add(zoomLevelRadioItems[i]);
		}
//		zoomGroup.set
		zoomGroup.setSelected(zoomLevelRadioItems[12].getModel(), true);
	}

	
	Action enableScoringAction = new MeatyAction("Enable scoring",false,false){

		@Override
		public void actionMeat(ActionEvent e) {
			if(Alignment.al.scoreal.disable_scoring)
				Alignment.al.scoreal.disable_scoring=false;
			else
				Alignment.al.scoreal.disable_scoring=true;
		}
		
		
	};


    
	Action alignmentInfoAction = new MeatyAction("Get alignment info", false,false){

		@Override
		public void actionMeat(ActionEvent e) {
			String text = "";
			text+="Length: " + Alignment.al.longestSeq + "\n";
			text+="Sequences: " + Alignment.al.size() + "\n";
			text+="Number of Sticky Blocks: " + Alignment.al.scoreal.stickyblocks.size()+'\n';
			text+="Residue Counts\n";
			Map<Residue.ResidueType, MutableInt> residueMap = new HashMap<Residue.ResidueType,MutableInt>();
			ArrayList<String> typeNames = new ArrayList<String>();
			for(int i = 0; i < Alignment.al.usedResiduesArr.length;i++)
			{
				residueMap.put(Alignment.al.usedResiduesArr[i], new MutableInt(0));
				typeNames.add(Alignment.al.usedResiduesArr[i].toString());
			}
			
			
			for(int seq = 0; seq< Alignment.al.size();seq++)
			{
				Sequence currseq=Alignment.al.get(seq);
				for(int i = 0; i < Alignment.al.longestSeq; i++)
				{
//					Residue.ResidueType test = currseq.get(i);
					residueMap.get(currseq.get(i).getType()).value++;
				}
			}
			
			java.util.Collections.sort(typeNames);
			for(int i = 0; i < typeNames.size();i++)
			{
				text+="\t"+typeNames.get(i)+": " + residueMap.get(new Residue(typeNames.get(i).charAt(0)).getType()).value + "\n";
			}
			JTextArea tarea = new JTextArea(text);
			tarea.setEditable(false);
//			JOptionPane.showMessageDialog(null, tarea);
			JOptionPane.showMessageDialog(null, tarea, "Alignment Information", JOptionPane.INFORMATION_MESSAGE);
        }
		
		
	};
	
	Action saveAction = new MeatyAction("Save", false,false){

		@Override
		public void actionMeat(ActionEvent e) {
			if(!IO.lastFile.getName().equals("UNSAVED"))
                IO.writeFasta(false,null);
            else
                saveAsAction.actionPerformed(null);
		}
		
		
	};
	
	Action enableResDeleteAction = new MeatyAction("Allow residue deletion",false,false){
		@Override
		public void actionMeat(ActionEvent e)
		{
			if(Alignment.al.deleteResidues==false)
			{
				Alignment.al.deleteResidues = true;
				
			}
			else
				Alignment.al.deleteResidues = false;
		}
	};
	
	Action saveAsAction = new MeatyAction("Save as",false,false){
		
		@Override
		public void actionMeat(ActionEvent e) {
			System.out.println("EYO");
			// TODO Auto-generated method stub
			IO.writeFasta(true,null);
			
		}
		
		
		
	};
	Action openAction = new MeatyAction("Open",false,false){

		@Override
		public void actionMeat(ActionEvent e) {
			int response = JOptionPane.NO_OPTION;
			//System.out.println(Sequence.getConsensus(0, Alignment.al.longestSeq-1, al));
			if(!IO.lastFile.getName().equals("-.fas"))
			{
				response = JOptionPane.showConfirmDialog(null, "Would you like to save the changes before opening a new file?", "", JOptionPane.YES_NO_CANCEL_OPTION);
			}
			
			if(response==JOptionPane.CANCEL_OPTION)
			{
				
			}
			else if(response == JOptionPane.NO_OPTION)
			{File f = null;
			Alignment.disposeCurrent();

				RunEverything re = new RunEverything(f);
				re.run();
				

			}
			else if(response ==JOptionPane.YES_OPTION)
			{
				

//				al = null;
				IO.writeFasta(false, new Runnable(){

					@Override
					public void run() {
						Alignment.disposeCurrent();
						RunEverything re = new RunEverything((File) null);
						re.run();

						
					}});
				

				
			}


			
		}
		
	};
	
	Action exitAction = new MeatyAction("Exit", false,false){

		@Override
		public void actionMeat(ActionEvent e) {
			
			tryToClose();
			
		}
		
	};
	
	 
	
	Action undoAction = new MeatyAction("Undo",true,true){

		@Override
		public void actionMeat(ActionEvent e) {
			System.out.println("Undo pressed!");
//			JPanel jp = new JPanel();
//			jp.setLayout(new BorderLayout());
			
			Alignment.al.urt.undo();
			Alignment.al.panel.canvas.repaint();
			
//			JFrame jf = Alignment.al.panel.superframe;
//			PaintingPanel pp = new PaintingPanel(IO.openFasta(), jf);
//			jp.add(pp, BorderLayout.CENTER);
//			jf.setPreferredSize(new Dimension(1000,1000));	
//			TopPanel topPanel = new TopPanel(pp.al);
//			pp.topPanel = topPanel;
//			pp.cma.tp = topPanel;
//			MainMenu menu = new MainMenu(pp.al);
//			jf.setJMenuBar(menu);
//			jp.add(topPanel, BorderLayout.NORTH);
//			jf.setContentPane(jp);
//			jf.pack();
//			jf.setVisible(true);
//			jf.revalidate();
//			System.out.println(topPanel.getSize());
//			System.out.println(pp.headers.rh.getSize());
//			jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
//			al = pp.al;

			
		}
		
	};
	
//	Action otherSimilarAction = new AbstractAction("Select similar regions (")
	Action similarSelectAction = new MeatyAction("Select similar regions",false,false){

		@Override
		public void actionMeat(ActionEvent e) {

			SimilarEngine.selectSimilarMain(true);
			// TODO Auto-generated method stub
			
		}
		
	};
	
	Action snapAlignAction = new MeatyAction("Snap-Align selected region",true,false){

		@Override
		public void actionMeat(ActionEvent e) {
			if(Alignment.al.netBlock.busy==true)
			{
				JOptionPane.showMessageDialog(null, "Please wait for the score to be computed before editing the alignment.");
				return;
			}
			Alignment.al.alignSelectedRegion();
			
			// TODO Auto-generated method stub
			
		}
		
	};
	
	
	Action debugAction = new MeatyAction("Debug",false,false){

		@Override
		public void actionMeat(ActionEvent e) {
            System.out.println("RETINA? " + OSTools.isRetina());
            Residue.buildImageMaps();
            Image bi = Residue.imageMap.get(Residue.ResidueType.A);
            File outputfile = new File("saved2.png");
            try {
                ImageIO.write((BufferedImage) bi, "png", outputfile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

//
//            if(SimilarEngine.firstExtend)
//            {
//                boolean extendSuccess = SimilarEngine.firstExtendCache();
//                if(!extendSuccess)
//                    return;
//
//            }
//            else
//            {
//                System.out.println("similarScrolling...");
//            }
//            SimilarEngine.similarJump=0;
//            SimilarEngine.similarBound=0;
//            int oldSimilarFound = SimilarEngine.similarSeqsFound;
//            HashSet<Integer> similarSeqs = SimilarEngine.getSimilarSequences();
//            showSettings();
//		    MuscleFrame muscleProgressFrame = new MuscleFrame();
//		     muscleProgressFrame.setTitle("Muscle Alignment Progress");
//		     muscleProgressFrame.setVisible(true);
			
//			EnumMap<Residue.ResidueType, MutableInt> countmap= new EnumMap<Residue.ResidueType, MutableInt>(Residue.ResidueType.class);
//			for(int j=0;j<Alignment.al.panel.canvas.viewport.width;j++)
//			for(int k=0; k < 100; k++)
//			for(int i = 0; i < Alignment.al.size(); i++)
//			{
//				MutableInt entry = countmap.get(Alignment.al.get(j).get(i));
//				if(entry==null)
//					entry = new MutableInt(0);
//				entry.value++;
//			}
			
//			System.out.println("vp:" + Alignment.al.panel.canvas.viewport.width);
//			Alignment.al.panel.canvas.viewport.jsbh.setMaximum(Alignment.al.longestSeq+1 - Alignment.al.panel.canvas.viewport.width + 10);

//			OpenFileChooser chooser = new OpenFileChooser();
//			JOptionPane.showMessageDialog(null, Alignment.al.longestSeq);
//			File f = chooser.getFileFromDialog(null);
//			ReadseqTools.fixFasta(f);
//			int code = Integer.parseInt(JOptionPane.showInputDialog("Whats to code bro?"));
//			ReadseqTools.convertToFormat(code, f, new File("C:\\Users\\Public\\lel.txt"));
//buildRecentFiles();
//
//System.out.println("Sticky?" + Alignment.al.columnIsSticky.get(respos));
//System.out.println("Block0:" + Alignment.al.netBlock.regions.get(0).endres);
//System.out.println("blockvector:" + Alignment.al.netBlock.blockVector.get(respos));
//			System.out.println(Alignment.al.panel.headers.rh.getPreferredSize());
//			OpenFileChooser chooser = new OpenFileChooser();
//			File f = chooser.getFile();
//			int [] [] AAMat = IO.readSubstitutionMatrix(f);
//			System.out.println("comenow");
//			WorkDispatcher tryme = new WorkDispatcher(new Runnable(){@Override public void run(){System.out.println("running");}}, "just checking this eowt.");
//			tryme.execute();
//			Alignment.al.panel.topPanel.scoreProgressBar.setValue(Integer.parseInt(JOptionPane.showInputDialog("Number")));
//			Alignment.al.panel.topPanel.scoreProgressBar.setValue(50);
////			Alignment.al.panel.topPanel.scoreProgressBar.repaint();
////			Alignment.al.panel.topPanel.scoreProgressBar.pr
//			Alignment.al.panel.topPanel.scoreProgressBar.update(Alignment.al.panel.topPanel.scoreProgressBar.getGraphics());
////			Alignment.al.panel.topPanel.p
//			Alignment.al.panel.topPanel.scoreProgressBar.validate();
//			System.out.println(Alignment.al.alignToProfile);
//			System.out.println(""+Alignment.al.getMD5Hash());
//			for(int i = 94; i < 139;i++)
//			{
//				System.out.print(Alignment.al.scoreal.get(0).get(i));
//			}
			
			//THIS CAN BE USEFUL FOR DEBUGGING SCOREAL STATE
//			for(int i = 0; i < Alignment.al.debugTrace.size(); i++)
//			{
//				
//				System.out.println(""+Alignment.al.debugTrace.get(i));
//			}
//			System.out.println("");
			//END USEFULLNESS FOR DEBUGGING SCOREAL STATE
//Alignment.al.panel.canvas.has
//			AWTFileDialog.openFile();
//			Alignment.al.panel.topPanel.scoreGraph.printHistory();
//			Alignment.al.panel.canvas.setUpKeyboardMaps();
//			Alignment.al.panel.canvas.setI
//			String out = Alignment.al.toString();
//			ResiduePos selected = Alignment.al.panel.canvas.viewport.selected.iterator().next();
//			System.out.println(Alignment.al.netBlock.getRegion(selected.location[0]).startres + " to " + Alignment.al.netBlock.getRegion(selected.location[0]).endres);
			//-GGAGGAGTAAA-TGGCGCCGTTAAACGG-TGCCGT-AAT----T
			//-GGAGGAGTAAA-TGGCGCCGTTAAACGG-TGCCGT-AATAT--T
		}
	};
	
	Action redoAction = new MeatyAction("Redo",true,true){
		@Override
		public void actionMeat(ActionEvent e)
		{
			Alignment.al.urt.redo(null); //its the default redo (i.e. default path)
			Alignment.al.panel.canvas.repaint();
		}
		
	};
	
	Action loadAAMatrixAction = new MeatyAction("Load custom Protein scoring matrix...",false,false){
		@Override
		public void actionMeat(ActionEvent e)
		{
            scoringChanged = true;
//			OpenFileChooser ofc = new OpenFileChooser();
			try {
				IO.readSubstitutionMatrix(new FileInputStream(AWTFileDialog.openFile()));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	};
	
	Action defaultAAMatrixAction = new MeatyAction("Revert to default BLOSUM62 matrix",false,false){
	@Override
	public void actionMeat(ActionEvent e)
	{
		scoringChanged=true;
		IO.readSubstitutionMatrix(this.getClass().getResourceAsStream("/BLOSUM62.txt"));
	}
	
};
	Action similarSettingsAction = new MeatyAction("Similarity Detection Settings...",false,true){

		@Override
		public void actionMeat(ActionEvent e) {
			JPanel similarPanel = new JPanel();
			similarPanel.setLayout(new BoxLayout(similarPanel, BoxLayout.Y_AXIS));
			JLabel minthresh = new JLabel("Minimum pairwise similarity (%)");
			JLabel minjump = new JLabel("Minimum similar jump size(%)");
			JSpinner minthreshspinner = new JSpinner(new SpinnerNumberModel((int)(SimilarEngine.startingSimilarBound*100), 0, 100,1));
			JSpinner minjumpspinner = new JSpinner(new SpinnerNumberModel((int)(SimilarEngine.startingSimilarJump*100), 0, 100,1));
			minthreshspinner.setMaximumSize(minthreshspinner.getPreferredSize());
			minjumpspinner.setMaximumSize(minjumpspinner.getPreferredSize());
			
			JPanel panelJump = new JPanel();
			JPanel panelThresh = new JPanel();
			panelJump.setToolTipText("Set the minimum gap in scores (as a percentage) between the highest non-similar sequence, and the lowest similar sequence. A lower score means lower sensitivity.");
			panelThresh.setToolTipText("Set the minimum pairwise similarity score for a sequence to be declared permissably similar. 100% means identical to the sequence. 0% would exclude no sequences.");
			panelJump.setLayout(new BoxLayout(panelJump, BoxLayout.X_AXIS));
			panelThresh.setLayout(new BoxLayout(panelThresh, BoxLayout.X_AXIS));
			panelJump.add(minjump);
			panelJump.add(Box.createHorizontalGlue());
			panelJump.add(minjumpspinner);
			panelThresh.add(minthresh);
			panelThresh.add(Box.createHorizontalGlue());
			panelThresh.add(minthreshspinner);
			similarPanel.add(panelJump);
			similarPanel.add(panelThresh);
			JOptionPane.showMessageDialog(null, similarPanel, "Similarity Settings", JOptionPane.PLAIN_MESSAGE);
			SimilarEngine.startingSimilarJump = ((Integer)minjumpspinner.getValue() + 0.0)/100.0;
			SimilarEngine.startingSimilarBound = ((Integer)minthreshspinner.getValue() + 0.0)/100.0;
            Alignment.al.resetSelection();
			
		}
		
	};
	Action scoreSettingsAction = new MeatyAction("Scoring settings...",false,false){
		@Override
		public void actionMeat(ActionEvent e)
		{

			JPanel scoringPanel = new JPanel();
			scoringPanel.setLayout(new BoxLayout(scoringPanel, BoxLayout.Y_AXIS));
			JLabel transitionLabel = new JLabel("Transition Score(used with DNA)");
			JLabel transversionLabel = new JLabel("Transversion Score(used with DNA)");
			JLabel matchLabel = new JLabel("Match Score (used with DNA)");
			JLabel gapCreationLabel = new JLabel("Gap Creation (used with both DNA and Protein)");
			JLabel gapExtensionLabel = new JLabel("Gap Extension (used with both DNA and Protein)");
			transitionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			transversionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			matchLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			gapCreationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			gapExtensionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
	
			JSpinner transitionspinner = new JSpinner(new SpinnerNumberModel(Sequence.scoreTransition,-50, 50,1));
			JSpinner transversionspinner = new JSpinner(new SpinnerNumberModel(Sequence.scoreTransversion,-50, 50,1));
			JSpinner matchspinner = new JSpinner(new SpinnerNumberModel(Sequence.scoreMatch,-50, 50,1));
			JSpinner gapCreationspinner = new JSpinner(new SpinnerNumberModel(Sequence.scoreGapOpen,-50, 50,1));
			JSpinner gapExtensionspinner =  new JSpinner(new SpinnerNumberModel(Sequence.scoreGapExtension,-50, 50,1));
			gapExtensionspinner.setMaximumSize(gapExtensionspinner.getPreferredSize());
			gapCreationspinner.setMaximumSize(gapCreationspinner.getPreferredSize());
			matchspinner.setMaximumSize(matchspinner.getPreferredSize());
			transitionspinner.setMaximumSize(transitionspinner.getPreferredSize());
			transversionspinner.setMaximumSize(transversionspinner.getPreferredSize());
			transitionspinner.setAlignmentX(Component.RIGHT_ALIGNMENT);
			matchspinner.setAlignmentX(Component.RIGHT_ALIGNMENT);
			gapCreationspinner.setAlignmentX(Component.RIGHT_ALIGNMENT);
			gapExtensionspinner.setAlignmentX(Component.RIGHT_ALIGNMENT);
			transversionspinner.setAlignmentX(Component.RIGHT_ALIGNMENT);
			JPanel transitionpanel = new JPanel();
			transitionpanel.setLayout(new BoxLayout(transitionpanel, BoxLayout.X_AXIS));
			transitionpanel.add(transitionLabel);
			transitionpanel.add(Box.createHorizontalGlue());
			transitionpanel.add(transitionspinner);
			transitionpanel.setToolTipText("Set the score associated with a nucleotide transition (A to G, or C to T). It should be higher than that of a transversion, but lower than a match");
			scoringPanel.add(transitionpanel);
			JPanel transversionpanel = new JPanel();
			transversionpanel.setLayout(new BoxLayout(transversionpanel, BoxLayout.X_AXIS));
			transversionpanel.add(transversionLabel);
			transversionpanel.add(Box.createHorizontalGlue());
			transversionpanel.add(transversionspinner);
			transversionpanel.setToolTipText("Set the score associated with a nucleotide transversion. It should be lower than that of a transition.");
//			setToolTipText("Set the score associated with a nucleotide transition (A to G, or C to T). It should be higher than that of a transversion, but lower than a match");
			scoringPanel.add(transversionpanel);
			JPanel matchpanel = new JPanel();
			matchpanel.setLayout(new BoxLayout(matchpanel, BoxLayout.X_AXIS));
			matchpanel.add(matchLabel);
			matchpanel.add(Box.createHorizontalGlue());
			matchpanel.add(matchspinner);
			matchpanel.setToolTipText("Set the score associated with a Non-Gap nucleotide match. It should be higher than that of a transition or transversion");
			scoringPanel.add(matchpanel);
			JPanel gapCreationpanel = new JPanel();
			gapCreationpanel.setLayout(new BoxLayout(gapCreationpanel, BoxLayout.X_AXIS));
			gapCreationpanel.add(gapCreationLabel);
			gapCreationpanel.add(Box.createHorizontalGlue());
			gapCreationpanel.add(gapCreationspinner);
			scoringPanel.add(gapCreationpanel);
			gapCreationpanel.setToolTipText("Set the score associated with opening a new gap in each pairwise comparison. It should be negative, and less than or equal to the Gap Extension score");
			
			JPanel gapExtensionpanel = new JPanel();
			gapExtensionpanel.setLayout(new BoxLayout(gapExtensionpanel, BoxLayout.X_AXIS));
			gapExtensionpanel.add(gapExtensionLabel);
			gapExtensionpanel.add(Box.createHorizontalGlue());
			gapExtensionpanel.add(gapExtensionspinner);
			gapExtensionpanel.setToolTipText("Set the score associated with extending an already open gap in each pairwise comparison. It should be negative, but not as severe as a Gap Open penalty.");
			scoringPanel.add(gapExtensionpanel);
			scoringPanel.add(new JLabel(" "));
			JButton customAAMatrixButton =  new JButton(loadAAMatrixAction);
			JButton defaultAAMatrixButton = new JButton(defaultAAMatrixAction);
			scoringPanel.add(customAAMatrixButton);
			defaultAAMatrixButton.setPreferredSize(customAAMatrixButton.getPreferredSize());
			defaultAAMatrixButton.setMaximumSize(customAAMatrixButton.getMaximumSize());
			defaultAAMatrixButton.setMinimumSize(customAAMatrixButton.getMinimumSize());
			scoringPanel.add(defaultAAMatrixButton);
			JOptionPane.showMessageDialog(null, scoringPanel, "Scoring Settings", JOptionPane.PLAIN_MESSAGE);
			Residue.buildDNASubMatrix(Sequence.scoreMatch, Sequence.scoreTransition, Sequence.scoreTransversion);
			Sequence.scoreGapExtension = (Integer)(gapExtensionspinner.getValue());
			Sequence.scoreGapOpen = (Integer)gapCreationspinner.getValue();
			Sequence.scoreMatch = (Integer)matchspinner.getValue();
			Sequence.scoreTransition = (Integer)transitionspinner.getValue();
			Sequence.scoreTransversion = (Integer)transversionspinner.getValue();
			Residue.buildDNASubMatrix(Sequence.scoreMatch, Sequence.scoreTransition, Sequence.scoreTransversion);
			Alignment.al.changed.add(new ResiduePos(0,0));
			Alignment.al.panel.topPanel.scoreLabel.setText("Recalculating score...");
			Alignment.al.netBlock.oldMaxScore = BigInteger.valueOf(-999999999);;
			Alignment.al.panel.topPanel.maxLabel.setText("-");
			Alignment.al.stickiesSet = false;
			Alignment.al.sw.recomputeEverything =true;
			Alignment.al.setStickyColumns(true);
			
			
		
		}
	};
	Action stickyThresholdAction = new MeatyAction("Sticky Region settings..."){
		@Override
		public void actionMeat(ActionEvent e)
		{
			JLabel similarity = new JLabel("Minimum Residue Conservation (%)");
//			similarity.setLayout(new BoxLayout(similarity, BoxLayout.X_AXIS));
			JLabel gaps = new JLabel ("Maximum Gaps Allowed (%)");
			JLabel minrun = new JLabel ("Minimum sticky region width");
			JSpinner similarityspinner = new JSpinner(new SpinnerNumberModel((int) (Alignment.al.minStickyMatch*100),0,100,1));
			JSpinner gapsspinner = new JSpinner(new SpinnerNumberModel((int) (Alignment.al.maxStickyGap*100), 0, 100,1));
			JSpinner minrunspinner = new JSpinner(new SpinnerNumberModel(Alignment.al.minStickySize,1, 200,1));
			similarity.setAlignmentX(Component.LEFT_ALIGNMENT);
			similarityspinner.setAlignmentX(Component.RIGHT_ALIGNMENT);
			gaps.setAlignmentX(Component.LEFT_ALIGNMENT);
			minrun.setAlignmentX(Component.LEFT_ALIGNMENT);
			gapsspinner.setAlignmentX(Component.RIGHT_ALIGNMENT);
			minrunspinner.setAlignmentX(Component.RIGHT_ALIGNMENT);
			similarityspinner.setMaximumSize(similarityspinner.getPreferredSize());
			gapsspinner.setMaximumSize(gapsspinner.getPreferredSize());
			minrunspinner.setMaximumSize(minrunspinner.getPreferredSize());
			JPanel simpanel = new JPanel();
			simpanel.setLayout(new BoxLayout(simpanel, BoxLayout.X_AXIS));
			simpanel.add(similarity);
			simpanel.add(Box.createHorizontalGlue());
			simpanel.add(similarityspinner);
			simpanel.setToolTipText("Set the minimum conservation percentage (100% meaning nucleotides completely conserved throughout a column) in order for a column to be declared sticky. A lower score means more sticky columns.");
			JPanel gappanel = new JPanel();
			gappanel.setToolTipText("Set the maximum permissable percentage of gaps present within the column. A higher percentage means more sticky columns.");
			gappanel.setLayout(new BoxLayout(gappanel, BoxLayout.X_AXIS));
			gappanel.add(gaps);
			gappanel.add(Box.createHorizontalGlue());
			gappanel.add(gapsspinner);
			JPanel runpanel = new JPanel();
			runpanel.setLayout(new BoxLayout(runpanel, BoxLayout.X_AXIS));
			runpanel.add(minrun);
			runpanel.add(Box.createHorizontalGlue());
			runpanel.add(minrunspinner);
			runpanel.setToolTipText("Set the shortest number of qualifying columns (i.e. Columns which meet the other criteria) which may be classified as a Sticky Region. A lower count means more sticky regions");
			JPanel daddypanel = new JPanel();
			BoxLayout bl = new BoxLayout(daddypanel, BoxLayout.Y_AXIS);
			daddypanel.setLayout(bl);
			daddypanel.add(runpanel);
			daddypanel.add(gappanel);
			daddypanel.add(simpanel);
			
			JOptionPane.showMessageDialog(null, daddypanel, "Sticky Column Settings", JOptionPane.PLAIN_MESSAGE);
			Alignment.al.minStickyMatch = (((Integer)similarityspinner.getValue()) + 0.0 )/100.0;
			Alignment.al.maxStickyGap = (((Integer)gapsspinner.getValue()) + 0.0 )/100.0;
			Alignment.al.minStickySize = (Integer)minrunspinner.getValue();
			Alignment.al.setStickyColumns(true);
			//Alignment.al.panel.canvas.font2 = new Font(Alignment.al.panel.canvas.font2.getFamily(), Alignment.al.panel.canvas.font2.getStyle(), (int)spinner.getValue() );
			Alignment.al.panel.canvas.viewport.fm = Alignment.al.panel.canvas.getFontMetrics(Alignment.al.panel.canvas.font2);
			//Alignment.al.panel.canvas.viewport.updateDimensions(Alignment.al.panel.canvas.getSize());
			Alignment.al.panel.canvas.viewport.componentResized(null);
			Alignment.al.panel.canvas.repaint();
		}
		
	};
	
	public void tryToClose()
	{
		int response = JOptionPane.YES_OPTION;
		
		if(IO.lastFile.getName().equals("-.fas"))
			response = JOptionPane.NO_OPTION;
		if(!IO.lastFile.getName().equals("-.fas"))
			response = JOptionPane.showConfirmDialog(null, "Would you like to save before exiting?", "", JOptionPane.YES_NO_CANCEL_OPTION);
		if(response==JOptionPane.CANCEL_OPTION)
		{
			//this shit never happens
		}
		else if(response == JOptionPane.NO_OPTION)
		{
			System.exit(0);
		}
		else if(response ==JOptionPane.YES_OPTION)
		{
			IO.writeFasta(false, new Runnable(){

				@Override
				public void run() {
					JOptionPane.showMessageDialog(null,"Your session has been saved. Goodbye.");
				System.exit(0);
					// TODO Auto-generated method stub
					
				}});

		}
	}
	
	class UndoItem extends JMenuItem
	{
		public UndoItem(Action a)
		{
			super(a);
		}
		
		@Override
		public boolean isEnabled()
		{
			
			
			return true;
			
			//commented the next 4 lines out lol
//			if(Alignment.al.urt.currentNode.depth <= 0)
//				return false;
//			else
//				return true;
		}
		
		
	}
	
	class RedoItem extends JMenuItem
	{
		public RedoItem(Action a)
		{
			super(a);
		}
		
		@Override
		public boolean isEnabled()
		{
			if(Alignment.al.urt.redoNodes.isEmpty())
				return false;
			else
				return true;
		}
		
		
	}
	public void buildRecentFiles()
	{
		if(recentFilesSet)
		{
			file.remove(recentFilesMenu);
					
		}
		recentFilesMenu = new JMenu("Open a recent file...");
//		file.add(recentFilesMenu);
		file.insert(recentFilesMenu, 1);
		recentFilesSet=true;
		ArrayList<String>recentfilesarr=null;
		try {
			 File jarfile = null;
			 String path=ClassLoader.getSystemClassLoader().getResource(".").getPath();
			 jarfile = new File(path);
			 String parent = null;
				try {
					parent = URLDecoder.decode(jarfile.getAbsolutePath(),"UTF-8");
				} catch (UnsupportedEncodingException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				 
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(parent+File.separator + "config"+File.separator+"recentfiles.ser"));
		recentfilesarr = (ArrayList<String>) ois.readObject();
			ois.close();
				
			} catch (FileNotFoundException e) {
				return;
				// TODO Auto-generated catch block
//				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		for(final String filename: recentfilesarr)
		{
			System.out.println(filename);
			String [] splitarr = filename.split(Pattern.quote(File.separator));
			String display = "";
			if(splitarr.length>1)
			{
				if(splitarr.length>2)
				{
					display+="..";
				}
				display+= File.separator +splitarr[splitarr.length-2]+File.separator + splitarr[splitarr.length-1];
			}
			else
			{
				display=File.separator+splitarr[splitarr.length-1];
			}
			
			Action RecentFileOpenAction = new MeatyAction(display,false,false){

				@Override
				public void actionMeat(ActionEvent e) {
					int response = JOptionPane.NO_OPTION;
					//System.out.println(Sequence.getConsensus(0, Alignment.al.longestSeq-1, al));
					if(!IO.lastFile.getName().equals("-.fas"))
					{
						response = JOptionPane.showConfirmDialog(null, "Would you like to save the changes before opening a new file?", "", JOptionPane.YES_NO_CANCEL_OPTION);
					}
					
					if(response==JOptionPane.CANCEL_OPTION)
					{
						
					}
					else if(response == JOptionPane.NO_OPTION)
					{Alignment.disposeCurrent();
						RunEverything re = new RunEverything(new File(filename));
						re.run();
//						Alignment.al.panel.superframe.dispose();
					}
					else if(response ==JOptionPane.YES_OPTION)
					{
						
						IO.writeFasta(false, new Runnable(){

							@Override
							public void run() {
								Alignment.disposeCurrent();
								RunEverything re = new RunEverything(new File(filename));
								re.run();
								// TODO Auto-generated method stub
								
							}});

//						Alignment.al.panel.superframe.dispose();
					}

					// TODO Auto-generated method stub
					
				}
					//OpenFileChooser
					
					//Alignment.al.helpText.setText("You have aligned the selected region to the consensus. To align an entire region of a sequence, only select a single residue within it. Shortcut: Hold Alt and drag-select." );
					// TODO Auto-generated method stub
					
				
				
			};
			JMenuItem newone = new JMenuItem(RecentFileOpenAction);
			recentFilesMenu.add(newone);
			
		}
	}

    public void saveSimilarSeed()
    {

    }
	

	
	public void buildMacros()
	{
		if(macroSet)
		{
			this.remove(macrosMenu);
					
		}
		macrosMenu = new JMenu("Macros");
//		file.add(recentFilesMenu);
		this.add(macrosMenu, 3);
		macroSet=true;
		File jarfile = null;
		HashMap<String,String>macromap=null;
		boolean savedMacros = false;
		try {
			String path=ClassLoader.getSystemClassLoader().getResource(".").getPath();
			 jarfile = new File(path);
			 String parent = null;
				try {
					parent = URLDecoder.decode(jarfile.getAbsolutePath(),"UTF-8");
				} catch (UnsupportedEncodingException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				 
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(parent+File.separator + "config"+File.separator+"macros.ser"));
		macromap = (HashMap<String,String>) ois.readObject();
			savedMacros = true;
			ois.close();
				
			} catch (FileNotFoundException e) {
				
				// TODO Auto-generated catch block
//				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 String p=null;
		try {
			p = URLDecoder.decode(jarfile.getAbsolutePath(),"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		};
		final String parent = p;
		if(savedMacros)
		for(final Entry<String, String> entry: macromap.entrySet())
		{
			Action currentAction = new MeatyAction(entry.getKey(),false,false)
			{

				@Override
				public void actionMeat(ActionEvent arg0) {
					
					String [] splitarr = entry.getValue().split(Pattern.quote( File.separator));
					String display = entry.getKey();
					String cmd = entry.getValue();
					Pattern pat = Pattern.compile("impalealign.[^\\s]+");
					Matcher m = pat.matcher(cmd);
					String lastExt = "";
					int formatcode = 0;
					while(m.find())
					{
						String [] splitAgain =m.group(0).split(Pattern.quote("."));
						String ext = splitAgain[splitAgain.length-1];
						if(!lastExt.equals("") && !lastExt.equals(ext))
						{
							JOptionPane.showMessageDialog(null, "You cannot use different format extensions in your macro!");
							
						}
						lastExt=ext;
						
					}
					
					if(lastExt.equalsIgnoreCase("FASTA")||lastExt.equalsIgnoreCase("FAS"))
					{
						formatcode = 8;
					}
					else if(lastExt.equalsIgnoreCase("PHY")||lastExt.equalsIgnoreCase("PHYLIP"))
					{
						formatcode = 12;
					}
					else if(lastExt.equalsIgnoreCase("NEX")||lastExt.equalsIgnoreCase("NEXUS"))
					{
						formatcode = 17;
					}
					else if(lastExt.equalsIgnoreCase("aln"))
					{
						formatcode = 22;
					}
					else
					{
						formatcode = 8;
					}
					
					cmd = cmd.replaceAll("impalealign.[^\\s]+", (parent + File.separator + "temp" + File.separator +"cmd_IMPALE.fas").replace("\\", "\\\\"));
					int oldformat = Alignment.al.format;
					File oldLastFile = IO.lastFile;
					IO.lastFile = new File(parent + File.separator + "temp" + File.separator +"cmd_IMPALE.fas");
					Alignment.al.format=formatcode;
					IO.writeFasta(false,null);//this is fucked.
					IO.lastFile = oldLastFile;
					Alignment.al.format = oldformat;
					System.out.println(cmd);
					if(OSTools.isWindows())
					cmd = "CMD /c " + cmd;
					Runtime rt = Runtime.getRuntime();
					try {

						Process pr = rt.exec("CMD /c " + cmd);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						JOptionPane.showMessageDialog(null,"An error occured while running your command");
						
					}
					// TODO Auto-generated method stub
					
				}
				
			}; 

//			if(splitarr.length>1)
//			{
//				if(splitarr.length>2)
//				{
//					display+="..";
//				}
//				display+= File.separator +splitarr[splitarr.length-2]+File.separator + splitarr[splitarr.length-1];
//			}
//			else
//			{
//				display=File.separator+splitarr[splitarr.length-1];
//			}
			 
			
			
			JMenuItem newone = new JMenuItem(currentAction);
			macrosMenu.add(newone);
			
		}
		
		macrosMenu.addSeparator();
		Action newMacroAction = new MeatyAction("New Macro...",false,false)
		{

			@Override
			public void actionMeat(ActionEvent arg0) {
				JPanel newMacroPanel = new JPanel();
				newMacroPanel.setLayout(new BoxLayout(newMacroPanel,BoxLayout.Y_AXIS));
				JTextField textField = new JTextField();
				
				JTextArea textArea = new JTextArea();
				JScrollPane cmdScroll = new JScrollPane(textArea);
//				cmdScroll.add(textArea);
				textField.setAlignmentX(Component.RIGHT_ALIGNMENT);
				textField.setPreferredSize(new Dimension(500,textField.getPreferredSize().height));
				textField.setMaximumSize(new Dimension(500,textField.getPreferredSize().height));
				cmdScroll.setAlignmentX(Component.RIGHT_ALIGNMENT);
				cmdScroll.setPreferredSize(new Dimension(500,50));
//				newMacroPanel.add(textField);
//				newMacroPanel.add(textArea);
				JPanel panelName = new JPanel();
				JPanel panelcmd = new JPanel();
				JLabel nameLabel = new JLabel("Name:");
				JLabel cmdLabel = new JLabel("Command:  ");
				cmdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
				nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//				panelName.setToolTipText("Set the name of the new Macro");
//				panelcmd.setToolTipText("Set the minimum pairwise similarity score for a sequence to be declared permissably similar. 100% means identical to the sequence. 0% would exclude no sequences.");
				panelcmd.setLayout(new BoxLayout(panelcmd, BoxLayout.X_AXIS));
				panelName.setLayout(new BoxLayout(panelName, BoxLayout.X_AXIS));
				panelcmd.add(cmdLabel);
				panelcmd.add(Box.createHorizontalGlue());
				panelcmd.add(cmdScroll);
				panelName.add(nameLabel);
				panelName.add(Box.createHorizontalGlue());
				panelName.add(textField);
				newMacroPanel.add(panelName);
				newMacroPanel.add(panelcmd);
				JLabel blankLabel = new JLabel(" ");
				JLabel helpLabel  = new JLabel("Use impalealign.format in place of the current alignment");
				JLabel helpLabel2 = new JLabel("where \".format\" is a common alignment format extension.");
				newMacroPanel.add(blankLabel);
				newMacroPanel.add(helpLabel);
				newMacroPanel.add(helpLabel2);
				JOptionPane.showMessageDialog(null, newMacroPanel, "Add a new Macro...", JOptionPane.PLAIN_MESSAGE);
				String cmd = textArea.getText();
				String name = textField.getText();
				ObjectInputStream ois = null;
				HashMap<String,String> macromap = null;
				try {
					ois = new ObjectInputStream(new FileInputStream(parent+File.separator + "config"+File.separator+"macros.ser"));
					macromap = (HashMap<String,String>) ois.readObject();
					
					ois.close();
				}
				catch (FileNotFoundException e)
				{
					macromap = new HashMap<String,String>();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// TODO Auto-generated method stub
				
				macromap.put(name, cmd);
				ObjectOutputStream oos = null;
				try {
					
					oos = new ObjectOutputStream(new FileOutputStream(parent+File.separator + "config" + File.separator+"macros.ser"));
					oos.writeObject(macromap);
				}
				catch(IOException ex)
				{
					
				}
				
				buildMacros();
			}
			
		};
		JMenuItem newMacroItem = new JMenuItem(newMacroAction);
		macrosMenu.add(newMacroItem);
	}
	
	public void showSettings()
    {
        final JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Scoring", null, makeScoringSettings(),"Settings related to computation of the alignment score");
        tabbedPane.addTab("Sticky", null, makeStickySettings(), "Settings related to the designation of sticky blocks");
        tabbedPane.addTab("Selection", null, makeSimilarSettings(), "Settings related to the similar sequence selection expansion");
        final JFrame settingsFrame = new JFrame("Settings");
        JButton okButton = new JButton(new MeatyAction("OK",false,false) {
            @Override
            public void actionMeat(ActionEvent e) {
                for(Component settingsPanel: tabbedPane.getComponents())
                {
                    ((SettingsPanel)settingsPanel).captureSettings();
                }
                settingsFrame.dispose();
            }
        });
        JButton cancelButton = new JButton(new MeatyAction("Cancel",false,false) {
            @Override
            public void actionMeat(ActionEvent e) {
                settingsFrame.dispose();

            }
        });

        okButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        cancelButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        settingsFrame.setLayout(new BoxLayout(settingsFrame.getContentPane(),BoxLayout.Y_AXIS));
        JPanel buttonPanel = new JPanel();
        Dimension buttonSize = cancelButton.getPreferredSize();
        okButton.setPreferredSize(buttonSize);
        okButton.setMinimumSize(buttonSize);

        cancelButton.setMinimumSize(buttonSize);
        buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(okButton);
        buttonPanel.add(new JLabel(" "));
        buttonPanel.add(cancelButton);
        buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        buttonPanel.setMinimumSize(new Dimension(0,buttonPanel.getPreferredSize().height));
        settingsFrame.setMinimumSize(settingsWindowSize);
        settingsFrame.setPreferredSize(settingsWindowSize);
        settingsFrame.add(tabbedPane);

        settingsFrame.add(buttonPanel);

        settingsFrame.setVisible(true);
//        System.out.println("Saving settings...");
//        IO.saveSettings();
    }

    public SettingsPanel makeStickySettings()
    {
        JLabel similarity = new JLabel("Minimum Residue Conservation (%)");
//			similarity.setLayout(new BoxLayout(similarity, BoxLayout.X_AXIS));
        JLabel gaps = new JLabel ("Maximum Gaps Allowed (%)");
        JLabel minrun = new JLabel ("Minimum sticky region width");
        final JSpinner similarityspinner = new JSpinner(new SpinnerNumberModel((int) (Alignment.al.minStickyMatch*100),0,100,1));
        final JSpinner gapsspinner = new JSpinner(new SpinnerNumberModel((int) (Alignment.al.maxStickyGap*100), 0, 100,1));
        final JSpinner minrunspinner = new JSpinner(new SpinnerNumberModel(Alignment.al.minStickySize,1, 200,1));
        similarity.setAlignmentX(Component.LEFT_ALIGNMENT);
        similarityspinner.setAlignmentX(Component.RIGHT_ALIGNMENT);
        gaps.setAlignmentX(Component.LEFT_ALIGNMENT);
        minrun.setAlignmentX(Component.LEFT_ALIGNMENT);
        gapsspinner.setAlignmentX(Component.RIGHT_ALIGNMENT);
        minrunspinner.setAlignmentX(Component.RIGHT_ALIGNMENT);
        similarityspinner.setMaximumSize(similarityspinner.getPreferredSize());
        gapsspinner.setMaximumSize(gapsspinner.getPreferredSize());
        minrunspinner.setMaximumSize(minrunspinner.getPreferredSize());
        JPanel simpanel = new JPanel();
        simpanel.setLayout(new BoxLayout(simpanel, BoxLayout.X_AXIS));
        simpanel.add(similarity);
        simpanel.add(Box.createHorizontalGlue());
        simpanel.add(similarityspinner);
        simpanel.setToolTipText("Set the minimum conservation percentage (100% meaning nucleotides completely conserved throughout a column) in order for a column to be declared sticky. A lower score means more sticky columns.");
        JPanel gappanel = new JPanel();
        gappanel.setToolTipText("Set the maximum permissable percentage of gaps present within the column. A higher percentage means more sticky columns.");
        gappanel.setLayout(new BoxLayout(gappanel, BoxLayout.X_AXIS));
        gappanel.add(gaps);
        gappanel.add(Box.createHorizontalGlue());
        gappanel.add(gapsspinner);
        JPanel runpanel = new JPanel();
        runpanel.setLayout(new BoxLayout(runpanel, BoxLayout.X_AXIS));
        runpanel.add(minrun);
        runpanel.add(Box.createHorizontalGlue());
        runpanel.add(minrunspinner);
        runpanel.setToolTipText("Set the shortest number of qualifying columns (i.e. Columns which meet the other criteria) which may be classified as a Sticky Region. A lower count means more sticky regions");
        SettingsPanel daddypanel = new SettingsPanel() {
            @Override
            void captureSettings() {
                System.out.println("capturing sticky settings...");
                Alignment.al.minStickyMatch = (((Integer)similarityspinner.getValue()) + 0.0 )/100.0;
                Alignment.al.maxStickyGap = (((Integer)gapsspinner.getValue()) + 0.0 )/100.0;
                Alignment.al.minStickySize = (Integer)minrunspinner.getValue();
                Alignment.al.setStickyColumns(true);
                //Alignment.al.panel.canvas.font2 = new Font(Alignment.al.panel.canvas.font2.getFamily(), Alignment.al.panel.canvas.font2.getStyle(), (int)spinner.getValue() );
                Alignment.al.panel.canvas.viewport.fm = Alignment.al.panel.canvas.getFontMetrics(Alignment.al.panel.canvas.font2);
                //Alignment.al.panel.canvas.viewport.updateDimensions(Alignment.al.panel.canvas.getSize());
                Alignment.al.panel.canvas.viewport.componentResized(null);
                Alignment.al.panel.canvas.repaint();

            }
        };
        BoxLayout bl = new BoxLayout(daddypanel, BoxLayout.Y_AXIS);
        daddypanel.setLayout(bl);
        daddypanel.add(runpanel);
        daddypanel.add(gappanel);
        daddypanel.add(simpanel);

        return daddypanel;


    }

    public SettingsPanel makeScoringSettings()
    {
        scoringChanged=false;
        final JSpinner transitionspinner = new JSpinner(new SpinnerNumberModel(Sequence.scoreTransition,-50, 50,1));
        final JSpinner transversionspinner = new JSpinner(new SpinnerNumberModel(Sequence.scoreTransversion,-50, 50,1));
        final JSpinner matchspinner = new JSpinner(new SpinnerNumberModel(Sequence.scoreMatch,-50, 50,1));
        final JSpinner gapCreationspinner = new JSpinner(new SpinnerNumberModel(Sequence.scoreGapOpen,-50, 50,1));
        final JSpinner gapExtensionspinner =  new JSpinner(new SpinnerNumberModel(Sequence.scoreGapExtension,-50, 50,1));
        SettingsPanel scoringPanel = new SettingsPanel() {
            @Override
            void captureSettings() {
                System.out.println("capturing scoring settings...");

                if(Sequence.scoreGapExtension!=(Integer)gapExtensionspinner.getValue())
                {
                    scoringChanged=true;
                    Sequence.scoreGapExtension = (Integer)(gapExtensionspinner.getValue());
                }
                if(Sequence.scoreGapOpen!=(Integer)gapCreationspinner.getValue())
                {
                    scoringChanged=true;
                    Sequence.scoreGapOpen = (Integer)gapCreationspinner.getValue();
                }
                if(Sequence.scoreMatch!=(Integer)matchspinner.getValue())
                {
                    scoringChanged=true;
                    Sequence.scoreMatch = (Integer)matchspinner.getValue();
                }
                if(Sequence.scoreTransition!=(Integer)transitionspinner.getValue())
                {
                    scoringChanged=true;
                    Sequence.scoreTransition = (Integer)transitionspinner.getValue();
                }
                if(Sequence.scoreTransversion!=(Integer)transversionspinner.getValue())
                {
                    scoringChanged=true;
                    Sequence.scoreTransversion = (Integer)transversionspinner.getValue();
                }

                if(scoringChanged) {
                    if(Alignment.al.sw!=null) {
                        Residue.buildDNASubMatrix(Sequence.scoreMatch, Sequence.scoreTransition, Sequence.scoreTransversion);
                        Alignment.al.changed.add(new ResiduePos(0, 0));
                        Alignment.al.panel.topPanel.scoreLabel.setText("Recalculating score...");
                        Alignment.al.netBlock.oldMaxScore = BigInteger.valueOf(-999999999);

                        Alignment.al.panel.topPanel.maxLabel.setText("-");
                        Alignment.al.stickiesSet = false;
                        Alignment.al.sw.recomputeEverything = true;
                        Alignment.al.setStickyColumns(true);
                    }
                }

                IO.saveSettings();
            }
        };
        gapExtensionspinner.setMaximumSize(gapExtensionspinner.getPreferredSize());
        scoringPanel.setLayout(new BoxLayout(scoringPanel, BoxLayout.Y_AXIS));
        JLabel transitionLabel = new JLabel("Transition Score(used with DNA)");
        JLabel transversionLabel = new JLabel("Transversion Score(used with DNA)");
        JLabel matchLabel = new JLabel("Match Score (used with DNA)");
        JLabel gapCreationLabel = new JLabel("Gap Creation (used with both DNA and Protein)");
        JLabel gapExtensionLabel = new JLabel("Gap Extension (used with both DNA and Protein)");
        JButton customAAMatrixButton =  new JButton(loadAAMatrixAction);
        JButton defaultAAMatrixButton = new JButton(defaultAAMatrixAction);

        customAAMatrixButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        defaultAAMatrixButton.setAlignmentX(Component.RIGHT_ALIGNMENT);

        transitionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        transversionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        matchLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        gapCreationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        gapExtensionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        gapCreationspinner.setMaximumSize(gapCreationspinner.getPreferredSize());
        matchspinner.setMaximumSize(matchspinner.getPreferredSize());
        transitionspinner.setMaximumSize(transitionspinner.getPreferredSize());
        transversionspinner.setMaximumSize(transversionspinner.getPreferredSize());
        transitionspinner.setAlignmentX(Component.RIGHT_ALIGNMENT);
        matchspinner.setAlignmentX(Component.RIGHT_ALIGNMENT);
        gapCreationspinner.setAlignmentX(Component.RIGHT_ALIGNMENT);
        gapExtensionspinner.setAlignmentX(Component.RIGHT_ALIGNMENT);
        transversionspinner.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JPanel transitionpanel = new JPanel();
        transitionpanel.setLayout(new BoxLayout(transitionpanel, BoxLayout.X_AXIS));
        transitionpanel.add(transitionLabel);
        transitionpanel.add(Box.createHorizontalGlue());
        transitionpanel.add(transitionspinner);
        transitionpanel.setToolTipText("Set the score associated with a nucleotide transition (A to G, or C to T). It should be higher than that of a transversion, but lower than a match");
        scoringPanel.add(transitionpanel);
        JPanel transversionpanel = new JPanel();
        transversionpanel.setLayout(new BoxLayout(transversionpanel, BoxLayout.X_AXIS));
        transversionpanel.add(transversionLabel);
        transversionpanel.add(Box.createHorizontalGlue());
        transversionpanel.add(transversionspinner);
        transversionpanel.setToolTipText("Set the score associated with a nucleotide transversion. It should be lower than that of a transition.");
//			setToolTipText("Set the score associated with a nucleotide transition (A to G, or C to T). It should be higher than that of a transversion, but lower than a match");
        scoringPanel.add(transversionpanel);
        JPanel matchpanel = new JPanel();
        matchpanel.setLayout(new BoxLayout(matchpanel, BoxLayout.X_AXIS));
        matchpanel.add(matchLabel);
        matchpanel.add(Box.createHorizontalGlue());
        matchpanel.add(matchspinner);
        matchpanel.setToolTipText("Set the score associated with a Non-Gap nucleotide match. It should be higher than that of a transition or transversion");
        scoringPanel.add(matchpanel);
        JPanel gapCreationpanel = new JPanel();
        gapCreationpanel.setLayout(new BoxLayout(gapCreationpanel, BoxLayout.X_AXIS));
        gapCreationpanel.add(gapCreationLabel);
        gapCreationpanel.add(Box.createHorizontalGlue());
        gapCreationpanel.add(gapCreationspinner);
        scoringPanel.add(gapCreationpanel);
        gapCreationpanel.setToolTipText("Set the score associated with opening a new gap in each pairwise comparison. It should be negative, and less than or equal to the Gap Extension score");

        JPanel gapExtensionpanel = new JPanel();
        gapExtensionpanel.setLayout(new BoxLayout(gapExtensionpanel, BoxLayout.X_AXIS));
        gapExtensionpanel.add(gapExtensionLabel);
        gapExtensionpanel.add(Box.createHorizontalGlue());
        gapExtensionpanel.add(gapExtensionspinner);
        gapExtensionpanel.setToolTipText("Set the score associated with extending an already open gap in each pairwise comparison. It should be negative, but not as severe as a Gap Open penalty.");
        scoringPanel.add(gapExtensionpanel);
        scoringPanel.add(new JLabel(" "));

        JPanel customButtonPanel = new JPanel();
        JPanel defaultButtonPanel = new JPanel();

        customButtonPanel.setLayout(new BoxLayout(customButtonPanel,BoxLayout.X_AXIS));
        defaultButtonPanel.setLayout(new BoxLayout(defaultButtonPanel, BoxLayout.X_AXIS));
        customButtonPanel.add(Box.createHorizontalGlue());
        customButtonPanel.add(customAAMatrixButton);
        defaultButtonPanel.add(Box.createHorizontalGlue());
        defaultButtonPanel.add(defaultAAMatrixButton);

        scoringPanel.add(customButtonPanel);
        defaultAAMatrixButton.setPreferredSize(customAAMatrixButton.getPreferredSize());
        defaultAAMatrixButton.setMaximumSize(customAAMatrixButton.getMaximumSize());
        defaultAAMatrixButton.setMinimumSize(customAAMatrixButton.getMinimumSize());
        scoringPanel.add(defaultButtonPanel);
//       duplicate wtf Residue.buildDNASubMatrix(Sequence.scoreMatch, Sequence.scoreTransition, Sequence.scoreTransversion);
        return scoringPanel;
    }

    public SettingsPanel makeSimilarSettings()
    {

            final JSpinner minthreshspinner = new JSpinner(new SpinnerNumberModel((int)(SimilarEngine.startingSimilarBound*100), 0, 100,1));
            final JSpinner minjumpspinner = new JSpinner(new SpinnerNumberModel((int)(SimilarEngine.startingSimilarJump*100), 0, 100,1));
            SettingsPanel similarPanel = new SettingsPanel() {
                @Override
                void captureSettings() {
                    System.out.println("capturing similar settings...");

                    SimilarEngine.startingSimilarJump = ((Integer)minjumpspinner.getValue() + 0.0)/100.0;
                    SimilarEngine.startingSimilarBound = ((Integer)minthreshspinner.getValue() + 0.0)/100.0;

                }
            };
            similarPanel.setLayout(new BoxLayout(similarPanel, BoxLayout.Y_AXIS));
            JLabel minthresh = new JLabel("Minimum pairwise similarity (%)");
            JLabel minjump = new JLabel("Minimum similar jump size(%)");
            minthreshspinner.setMaximumSize(minthreshspinner.getPreferredSize());
            minjumpspinner.setMaximumSize(minjumpspinner.getPreferredSize());

            JPanel panelJump = new JPanel();
            JPanel panelThresh = new JPanel();
            panelJump.setToolTipText("Set the minimum gap in scores (as a percentage) between the highest non-similar sequence, and the lowest similar sequence. A lower score means lower sensitivity.");
            panelThresh.setToolTipText("Set the minimum pairwise similarity score for a sequence to be declared permissably similar. 100% means identical to the sequence. 0% would exclude no sequences.");
            panelJump.setLayout(new BoxLayout(panelJump, BoxLayout.X_AXIS));
            panelThresh.setLayout(new BoxLayout(panelThresh, BoxLayout.X_AXIS));
            panelJump.add(minjump);
            panelJump.add(Box.createHorizontalGlue());
            panelJump.add(minjumpspinner);
            panelThresh.add(minthresh);
            panelThresh.add(Box.createHorizontalGlue());
            panelThresh.add(minthreshspinner);
            similarPanel.add(panelJump);
            similarPanel.add(panelThresh);

    return similarPanel;


    }
	public void disarm()
	{
		if(filler==null)
		filler = new JMenuItem("Only scrolling is allowed during current action...");
		this.remove(file);
		this.remove(edit);
		this.remove(options);
		this.remove(macrosMenu);
		this.remove(debuggingItem);
		this.add(filler);
//		this.add(filler);
		this.setEnabled(false);
		
	}

	public void arm()
	{
		if(filler!=null)
		{
		this.remove(filler);
		filler=null;
		}
//		this.remove(filler);
//		this.remove(macrosMenu);
//		this.remove(debuggingItem);
//		this.validate();
		this.add(file);
		this.add(edit);
		this.add(options);		
		this.add(macrosMenu);
		this.add(debuggingItem);
		
		this.validate();
		this.repaint();
		this.setEnabled(true);
	}

	
	

	

}
