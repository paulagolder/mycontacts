package org.lerot.mycontact.gui;

import org.lerot.mywidgets.jswTabbedPanel;
import org.lerot.mywidgets.jswVerticalLayout;

public class backupTabPanel extends jswTabbedPanel
{

	private static final long serialVersionUID = 1L;
	private exportBackupPanel makebackuppanel;
	importBackupPanel recoverbackuppanel ;

	public backupTabPanel()
	{
		super("backuptabpanel");
		//setLayout(new jswVerticalLayout());
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
