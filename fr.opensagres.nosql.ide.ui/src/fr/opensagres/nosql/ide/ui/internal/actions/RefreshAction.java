package fr.opensagres.nosql.ide.ui.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import fr.opensagres.nosql.ide.core.model.TreeContainerNode;
import fr.opensagres.nosql.ide.ui.internal.ImageResources;
import fr.opensagres.nosql.ide.ui.internal.Messages;

public class RefreshAction extends Action {

	private final TreeViewer viewer;

	public RefreshAction(TreeViewer viewer) {
		super.setText(Messages.RefreshAction_text);
		super.setToolTipText(Messages.RefreshAction_toolTipText);
		super.setImageDescriptor(ImageResources
				.getImageDescriptor(ImageResources.IMG_REFRESH_16));
		this.viewer = viewer;
	}

	public void run() {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		if (!selection.isEmpty()) {
			// Remove child nodes
			Object element = selection.getFirstElement();
			if (element instanceof TreeContainerNode) {
				((TreeContainerNode) element).clearNodes(true);
			}
		}
		// refresh viewer
		viewer.refresh();
	}

}
