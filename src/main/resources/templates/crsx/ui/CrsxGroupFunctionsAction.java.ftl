package ${package}.ui.outline;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.ui.editor.outline.IOutlineTreeProvider;
import org.eclipse.xtext.ui.editor.outline.actions.IOutlineContribution;
import org.eclipse.xtext.ui.editor.outline.impl.OutlinePage;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

public class CrsxGroupFunctionsAction extends Action implements IOutlineContribution{

    private Action action;
    
    private OutlinePage outlinePage;
    
    /**
     * Initialize action if it was not done yet
     * 
     * @param should groping be activated after action initialization
     * @return this action initialized
     */
    protected Action getAction(boolean checked){
        if(action == null){
            action = this;
            configureAction(action,checked);
        }
        return action;
    }
    
    /**
     * Set grouping action arguments
     * 
     * @param action Action to be configured
     * @param checked wheter it should be checked by default
     */
    protected void configureAction(Action action, boolean checked){
        action.setText("Group");
        action.setToolTipText("Group rules by function sorts");
        ImageDescriptor img = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_UP);
        action.setImageDescriptor(img);
        action.setEnabled(true);
        action.setChecked(checked);
    }
    
    @Override
    public void initialize(IPreferenceStoreAccess access) {
        
    }
    
    /**
     * Perform action
     * 
     * This will turn grouping on/off
     */
    @Override
    public void run(){
        IOutlineTreeProvider treeProvider = outlinePage.getTreeProvider();
        if( treeProvider instanceof CrsxOutlineTreeProvider){
            CrsxOutlineTreeProvider crsxOutlineTreeProvider = (CrsxOutlineTreeProvider) treeProvider;
            boolean grouping = !crsxOutlineTreeProvider.getGrouping();
            crsxOutlineTreeProvider.setGrouping(grouping);
            this.setChecked(grouping);
            outlinePage.scheduleRefresh();
        }
    }

    /**
     * Register action to the outline view
     */
    @Override
    public void register(OutlinePage outlinePage) {
        IToolBarManager toolbarManager = outlinePage.getSite().getActionBars().getToolBarManager();
        
        boolean grouping = false;
        
        IOutlineTreeProvider treeProvider = outlinePage.getTreeProvider();
        if( treeProvider instanceof CrsxOutlineTreeProvider){
            grouping = ((CrsxOutlineTreeProvider) treeProvider).getGrouping();
        }
        
        toolbarManager.add(getAction(grouping));
        outlinePage.getSite().getActionBars().updateActionBars();
        this.outlinePage = outlinePage;
    }

    @Override
    public void deregister(OutlinePage outlinePage) {
        this.outlinePage = null;
    }

}
