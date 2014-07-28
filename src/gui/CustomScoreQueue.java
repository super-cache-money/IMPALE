package gui;

import java.util.ArrayList;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class CustomScoreQueue extends ArrayList<ScoreAction>{
	
	
	@Override
	public boolean add(ScoreAction in)
	{
		if(this.size()==0)
		{
			Alignment.al.scoreal.pacmanCurrentUpdate.incrementAndGet();
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {
//					if(Alignment.al.panel.topPanel.editsInQLabel!=null)
					Alignment.al.panel.topPanel.editsInQLabel.setText((Alignment.al.scoreal.pacmanCurrentUpdate.get())+"");
						Alignment.al.panel.topPanel.scoreProgressBar.setVisible(true);
						Alignment.al.panel.topPanel.scoreProgressBar.setValue(0);
					
					// TODO Auto-generated method stub
					
				}
				
			});
		}
			
//		Alignment.al.panel.topPanel.scoreProgressBar=new JProgressBar(0,100);
//		Alignment.al.panel.topPanel.scoreProgressBar.setVisible(true);
//		Alignment.al.panel.topPanel.scoreProgressBar.setValue(0);
		
//		Alignment.al.panel.topPanel.scoreProgressBar.setBackground(Color.YELLOW);
		return super.add(in);
	}
	
	public CustomScoreQueue()
	{
		super();
	}

}
