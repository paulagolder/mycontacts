package org.lerot.mycontact.gui;

import org.lerot.gui.widgets.jswTabbedPanel;
import org.lerot.gui.widgets.jswVerticalLayout;

public class ExportTabPanel extends jswTabbedPanel
{

	private static final long serialVersionUID = 1L;
	private exportPanel exportpanel1;
	private labelprintPanel labelpanel2;

	public ExportTabPanel()
	{

		setLayout(new jswVerticalLayout());
		exportpanel1 = new exportPanel();
		addTab("Export Contacts", exportpanel1);
		labelpanel2 = new labelprintPanel();
		addTab("Print Labels", labelpanel2);
		setSelectedComponent(exportpanel1);

	}

	public void showExportPanel()
	{
		exportpanel1.refresh();
		labelpanel2.refresh();
	}
}
