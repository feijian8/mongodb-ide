package fr.opensagres.mongodb.ide.ui.editors.database;

import org.eclipse.ui.PartInitException;

import fr.opensagres.mongodb.ide.core.model.Database;
import fr.opensagres.mongodb.ide.ui.editors.AbstractFormEditor;
import fr.opensagres.mongodb.ide.ui.internal.Trace;

public class DatabaseEditor extends AbstractFormEditor {

	public static final String ID = "fr.opensagres.mongodb.ide.ui.editors.database.DatabaseEditor";

	@Override
	protected void addPages() {
		try {
			super.addPage(new OverviewPage(this));
		} catch (PartInitException e) {
			Trace.trace(Trace.STRING_SEVERE,
					"Error while adding page in the editor ", e);
		}
	}

	@Override
	protected void createPages() {
		// create pages
		super.createPages();
		// modify the title of the editor with the name of the database.
		Database database = getDatabase();
		if (database != null) {
			super.setPartName(database.getName());
		}
	}

	public Database getDatabase() {
		return ((DatabaseEditorInput) getEditorInput()).getDatabase();
	}
}
