package gui;

import javax.swing.table.AbstractTableModel;


public class SeqTableModel extends AbstractTableModel
{

//	Alignment al;
	
	public SeqTableModel(Alignment al)
	{
		super();
//		this.al = al;
		
	}
	
	@Override
	public int getRowCount() {
		return Alignment.al.size();
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getColumnName(int columnIndex)
	{
		return columnIndex + 1 + "";
	}
	
	public String getRowName(int rowIndex)
	{
		return Alignment.al.get(rowIndex).name;
	}
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return Alignment.al.longestSeq;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return Alignment.al.get(rowIndex).get(columnIndex);
	}
	
	@Override
	public void setValueAt(Object o, int rowIndex, int columnIndex)
	{
		Alignment.al.get(rowIndex).set(columnIndex, (Residue) o);
	}
	
	
}
