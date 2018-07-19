package org.lerot.mycontact.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lerot.mycontact.mcdb;
import org.lerot.mycontact.gui.widgets.jswTabbedPanel;
import org.lerot.mycontact.gui.widgets.jswVerticalLayout;
import org.lerot.mycontact.gui.widgets.jswVerticalPanel;

public class ToolsPanel extends jswVerticalPanel implements ActionListener,
		ChangeListener
{

	private static final long serialVersionUID = 1L;

	ActionListener plistener = null;
	//attributeEditPanel attedpanel1;

	int othertabno;
	int toolstab;

	private importTabPanel importpanel;

	private ExportTabPanel exportpanel;

	private OtherTabPanel otherpanel;

	private manageTagsPanel manageTagspanel2;

	private jswTabbedPanel toolstabbedPane;

	private backupTabPanel backuppanel;

	private importBackupPanel importBackupPane;

	public ToolsPanel(ActionListener parentlistener)
	{
		plistener = parentlistener;

		makeToolsPanel();
	}

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		String action = evt.getActionCommand().toUpperCase();
		System.out.println("action in setuppanel " + action);
	}

	void makeToolsPanel()
	{
		toolstabbedPane = new jswTabbedPanel();
		toolstabbedPane.setLayout(new jswVerticalLayout());
		backuppanel = new backupTabPanel();
		toolstabbedPane.addTab("Backup", backuppanel);

		importpanel = new importTabPanel();
		toolstabbedPane.addTab("Import", importpanel);
		exportpanel = new ExportTabPanel();
		toolstabbedPane.addTab("Export", exportpanel);
		otherpanel = new OtherTabPanel();
		toolstabbedPane.addTab("Other", otherpanel);
		toolstabbedPane.setSelectedIndex(0);
		add(" FILLW ", toolstabbedPane);
	}

	public void showToolsPanel()
	{
		otherpanel.showOtherPanel();
		importpanel.showImportPanel();
		exportpanel.showExportPanel();
        backuppanel.showBackupPanel();

		repaint();
		mcdb.topgui.getContentPane().validate();
	}

	@Override
	public void stateChanged(ChangeEvent changeEvent)
	{
		JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
		int index = sourceTabbedPane.getSelectedIndex();
		othertabno = index;
		String seltab = sourceTabbedPane.getTitleAt(index);
		System.out.println("Tab changed to: " + seltab);

		//if (seltab.equals("Edit Attributes"))
		//{
		//	attedpanel1.showAttributeEditPanel();
		//	attedpanel1.setVisible(true);
		//	attedpanel1.validate();
		//}
		if (seltab.equals("Manage Tags"))
		{
			manageTagspanel2.refresh();
		}

		validate();
	}

	public void refresh()
	{
		showToolsPanel();

	}

}
