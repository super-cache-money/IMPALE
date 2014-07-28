package gui;

import javax.swing.*;
import javax.swing.table.TableModel;

public class SeqTable extends JTable{

	public SeqTable(TableModel model)
	{
		super(model);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setCellSelectionEnabled(true);
		for(int i = 0; i<getColumnCount(); i++)
		{
			getColumnModel().getColumn(i).setPreferredWidth(15);
		}
	}
	
	
}
