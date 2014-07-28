package gui;

import java.awt.event.ActionEvent;

/**
 * Created by arjun on 2014/06/28.
 */
public abstract class MeatyAction extends javax.swing.AbstractAction {
    //is this bad practice? lolz

    boolean changesAlignment = false;
    boolean changesSelection = false;

    public MeatyAction()
    {
        super();
    }
    public MeatyAction(String str)
    {
        super(str);
    }

    public MeatyAction(String str, Boolean changesAlignment, Boolean changesSelection)
    {
        super(str);
        if(changesAlignment!=null)
            this.changesAlignment=changesAlignment;
        if(changesSelection!=null)
            this.changesSelection=changesSelection;

    }

    public void actionPerformed(ActionEvent e)
    {

        if(changesAlignment||changesSelection)
            SimilarEngine.firstExtend=true;
        if(changesAlignment && Alignment.al.blockEdits)
        {
            return;
        }
        else
        {
            actionMeat(e);
        }

    }

    public abstract void actionMeat(ActionEvent e);

}
