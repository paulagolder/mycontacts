package org.lerot.mycontact.gui;

import org.lerot.gui.widgets.jswTabbedPanel;
import org.lerot.gui.widgets.jswVerticalLayout;

public class backupTabPanel extends jswTabbedPanel
{

	private static final long serialVersionUID = 1L;
	private exportBackupPanel makebackuppanel;
	importBackupPanel recoverbackuppanel ;

	public backupTabPanel()
	{
		setLayout(new jswVerticalLayout());
		makebackuppanel = new exportBackupPanel();
		addTab("Make Backup", makebackuppanel);
		makebackuppanel.setVisible(true);
		recoverbackuppanel = new importBackupPanel();
		addTab("Import Backup", recoverbackuppanel);
		
		setSelectedComponent(makebackuppanel);
	}



	public void showBackupPanel()
	{
		makebackuppanel.refresh();
		recoverbackuppanel.refresh();
		
	}

}
