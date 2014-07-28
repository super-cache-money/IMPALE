package gui;

import java.util.Stack;

public class UndoRedoStacks{
	Stack<EditStack> undo;
	Stack<EditStack> redo;
//	Alignment al;
	public UndoRedoStacks()
	{
		
//		this.al = al;
		undo = new Stack<EditStack>();
		redo = new Stack<EditStack>();
	}
	
	public void undo()
	{
		Alignment.al.busyUndo = true;
		undo.pop().run();
//		Alignment.al.currentEdit.pushToRedo();
		Alignment.al.busyUndo = false;
		Alignment.al.panel.menu.redoItem.setEnabled(true);
	}
	
	public void redo()
	{
		
		redo.pop().run();
		
	}
	
	//25 Falmouth 
	class RedoStack extends Stack<EditStack>
	{
		public RedoStack()
		{
			super();
		}
		
		@Override 
		public void clear()
		{
			super.clear();
			Alignment.al.panel.menu.redoItem.setEnabled(false);
			
		}
		
	}
}
