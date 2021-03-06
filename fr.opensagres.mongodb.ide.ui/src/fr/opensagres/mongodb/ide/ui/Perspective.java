package fr.opensagres.mongodb.ide.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import fr.opensagres.mongodb.ide.ui.views.ServerExplorer;
import fr.opensagres.mongodb.ide.ui.views.ShellCommandsView;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);
		layout.setFixed(true);

		String editorArea = layout.getEditorArea();
		IFolderLayout bottomFolder = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.80f, editorArea);
		IFolderLayout leftFolder = layout.createFolder("left",
				IPageLayout.LEFT, 0.22f, editorArea);
//		IFolderLayout centerFolder = layout.createFolder("center",
//				IPageLayout.LEFT, 0.68f, editorArea);
//		IFolderLayout rightFolder = layout.createFolder("right",
//				IPageLayout.LEFT, 0.10f, editorArea);

		leftFolder.addView(ServerExplorer.ID);
		bottomFolder.addView(ShellCommandsView.ID);

	}

}
