package gui;



import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.google.common.collect.TreeMultiset;

public class UndoRedoTree implements Serializable{
	Vector<Node> treeHistory;
	 int nextid = 0;
    private Node root;
    public Node currentNode;
    public Vector <Node> redoNodes;
   
    public Vector <TreeMultiset<Node>> depthNodes;
    public UndoRedoTree() {

        root = new Node(0, new Vector<Integer>(), new Vector<Integer>());
       
        treeHistory = new Vector<Node>();
        currentNode = root;
        root.children = new Vector<Node>();
        redoNodes = new Vector<Node>();
        treeHistory.add(root);
    }
    public void goToNode(Node target)
    {
    	
    	ArrayList<Node> mrcaToTarget = findNodeInDescendants(target,currentNode);
    	
    	
    	
    	
    	while(mrcaToTarget==null)
    	{
        	if(currentNode.equals(root))
        	{
        		JOptionPane.showMessageDialog(null, "Search for target node failed!");
        		return;
        	}
    		undo();
    		
    		mrcaToTarget = findNodeInDescendants(target, currentNode);
    	}

    	for(int i = 1; i<mrcaToTarget.size();i++)
    	{
    		Node n = mrcaToTarget.get(i);
    		goToNeighbourNode(n);
    	}
    	
    }
    
    public ArrayList<Node> findNodeInDescendants(Node target,Node search)
    {
    	boolean found;
    	for(int i = 0; i < search.children.size();i++)
    	{
    		Node child = search.children.get(i);
    		ArrayList<Node> trace = findNodeInDescendants(target,child);
    		if(trace!=null)
    		{
    			trace.add(0,search);
    			return trace;
    		}
    	}
    	
    	if(target.equals(search))
    	{
    		ArrayList<Node> trace = new ArrayList<Node>();
    		trace.add(search);
    		return trace;
    	}
    	
    	return null;
    }
	public void newEdit(EditStack e)
	{
		Node temp  = currentNode;
		Vector<Integer> copyPos = currentNode.tracePos;
		Vector<Integer> copyChild = currentNode.traceChild;
		if(currentNode.children.size() >0);
		{
			copyPos = (Vector<Integer>) currentNode.tracePos.clone();
			copyChild= (Vector<Integer>) currentNode.traceChild.clone();
			copyPos.add(currentNode.depth);
			copyChild.add(currentNode.children.size());
			
			
		}
		
		
		currentNode.children.add(new Node(currentNode.depth+1,copyPos, copyChild));
		currentNode = currentNode.children.get(currentNode.children.size()-1);
		temp.action = e;
		currentNode.parent = temp;
		treeHistory.add(currentNode);
		
	}
	
	public void goToNeighbourNode(Node targetNode)
	//this needs to be done
	{
		if(currentNode.equals(targetNode))
		{
			return;
		}
		int firstbranch = 0;
		boolean found = false;
		for(int i = 0; (i < Math.max(targetNode.traceChild.size(), currentNode.traceChild.size()))&&(found==false); i++)
		{
			
		}
		
		if(currentNode.parent!=null&& currentNode.parent.equals(targetNode))
		{
			undo();
			return;
		}
		for(Node child : currentNode.children)
		{
			
			if(child.equals(targetNode))
			{
				    redo(child);
				    return;
			}
		}
		
		JOptionPane.showMessageDialog(null, "Oh shit. Where's that node at?");
		
	}
	
	
	public void rushEditToQueue()
	{
		synchronized(Alignment.al.scorequeue)
		{
			if(Alignment.al.currentEdit.timertask!=null)
			if(!Alignment.al.currentEdit.timertask.isComplete.get())
			{
				System.out.println("Rushing the current task");
				Alignment.al.urt.newEdit(Alignment.al.currentEdit);

				synchronized (Alignment.al.scoreal.scoreProcessingQueue)
				{
					Alignment.al.scoreal.scoreProcessingQueue.add(new ActionSequence(Alignment.al.scorequeue,Alignment.al.scoreal.seqschangedset, Alignment.al.urt.currentNode));
				}
				Alignment.al.currentEdit.timertask.alreadyrun.set(true);
				Alignment.al.currentEdit = new EditStack();
				
			}
		

		
		}
	}
	public void undo()
	{
		rushEditToQueue();
		if(currentNode==root)
		{
			System.out.println("WTF nothing to undo");
			return;
		}
		redoNodes.add(currentNode);
		Alignment.al.busyUndo = true;
		Alignment.al.currentEdit.currentStartingSelected = currentNode.parent.action.currentEndingSelected;
		Alignment.al.currentEdit.currentEndingSelected = currentNode.parent.action.currentStartingSelected;
		currentNode.parent.action.run();

		currentNode.action = Alignment.al.currentEdit;
		Alignment.al.currentEdit = new EditStack();
		currentNode = currentNode.parent;
		int y = 1;
		synchronized(Alignment.al.scoreal.scoreProcessingQueue)
		{
			Alignment.al.scoreal.scoreProcessingQueue.add(new ActionSequence(Alignment.al.scorequeue, Alignment.al.scoreal.seqschangedset, currentNode));
		}
		Alignment.al.panel.menu.redoItem.setEnabled(true);
		Alignment.al.busyUndo = false;
		
		treeHistory.add(currentNode);
		
		
		int x = 1;
		
	}
	
	public void redo(Node childNode)
	{
		Node gotonode;
		if(childNode==null)
		{
			 gotonode = redoNodes.get(redoNodes.size()-1);
			 redoNodes.remove(redoNodes.size()-1);
		}
		else
		{
			gotonode = childNode;
			redoNodes = new Vector<Node>(); //because we obviously just broke it
		}
		gotonode.action.run();
//		gotonode.parent=currentNode;
		currentNode.action = Alignment.al.currentEdit;
		currentNode.action.currentStartingSelected = gotonode.action.currentEndingSelected;
		currentNode.action.currentEndingSelected = gotonode.action.currentStartingSelected;
		Alignment.al.currentEdit = new EditStack();
		currentNode = gotonode;
		synchronized(Alignment.al.scoreal.scoreProcessingQueue)
		{
			Alignment.al.scoreal.scoreProcessingQueue.add(new ActionSequence(Alignment.al.scorequeue, Alignment.al.scoreal.seqschangedset, currentNode));
		}
		
		
		
		treeHistory.add(currentNode);
	}
	
	

     class Node implements Comparable, Serializable {
        public Vector<Integer> traceChild; //this keeps a trace of which child to follow from the root, to get to this node
        public Vector<Integer> tracePos; //weird, tracks the depth of nodes followed from root to here, i think. point = ?
        public boolean alive;
        public boolean scored = false;
        public int depth;
        private Node parent;
        private Vector<Node> children;
        public int id;
        public BigInteger score;
        public EditStack action;
        public Node(int depth, Vector<Integer> tracePos, Vector<Integer> traceChild)
        {
        	this.depth=depth;
        	this.traceChild = traceChild;
        	this.tracePos = tracePos;
        	alive = true;
        	children = new Vector<Node>();
        	id = nextid;
        	nextid++;

        	
        }

		@Override
		public int compareTo(Object o) {
				Node other = (Node) o;
				if(this.scored==false)
					return -9999999;
				if(other.scored==false)
					return 99999999;
			
				return  (this.score.subtract(other.score).intValue());
		}

		public void updateScore(int score)
		{
			
		}
		
		@Override
		public boolean equals(Object o)
		{
			if(o instanceof Node)
			{
				Node other = (Node) o;
				if(other.id == this.id)
					return true;
			}
			return false;
		}
		
		
    }

}
