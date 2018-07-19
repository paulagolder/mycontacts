package org.lerot.mycontact;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.lerot.mycontact.gui.ToolsPanel;
import org.lerot.mycontact.gui.browsePanel;
import org.lerot.mycontact.gui.editPanel;
import org.lerot.mycontact.gui.searchPanel;
import org.lerot.mycontact.gui.widgets.jswContainer;
import org.lerot.mycontact.gui.widgets.jswDropDownContactBox;
import org.lerot.mycontact.gui.widgets.jswHorizontalPanel;
import org.lerot.mycontact.gui.widgets.jswLabel;
import org.lerot.mycontact.gui.widgets.jswPanel;
import org.lerot.mycontact.gui.widgets.jswPushButtonset;
import org.lerot.mycontact.gui.widgets.jswStyle;
import org.lerot.mycontact.gui.widgets.jswStyles;
import org.lerot.mycontact.gui.widgets.jswVerticalLayout;
import org.lerot.mycontact.gui.widgets.jswVerticalPanel;

//import org.lerot.mycontact.forms.certificateeditpanel;
//import org.lerot.mycontact.forms.documentTemplate;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DesertBluer;

public class mcdb extends JFrame implements ActionListener
{

	public mcDataSource currentcon;
	public static jswStyles allstyles;
	public static Component browserpanel;
	public static String certificatepath;
	
	private static final long serialVersionUID = 1L;
	public static boolean showborders;
	public static String temppath;
	public static mcdb topgui;
	static String version = "V 12.0(64)";
	public static mcSelectorBox selbox;
	public static String letterfolder;
	

	public static void main(String[] args)
	{
		try
		{
			PlasticLookAndFeel.setPlasticTheme(new DesertBluer());
			UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
		} catch (Exception e)
		{
		}
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);

		JFrame mframe = new mcdb(800, 400);
		mframe.addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		mframe.getContentPane().setLayout(
				new BoxLayout(mframe.getContentPane(), BoxLayout.X_AXIS));
		mframe.setLocation(50, 50);
		mframe.pack();
		mframe.setVisible(true);
	}

	public browsePanel abrowsepanel;
	public editPanel aneditpanel;
	public searchPanel asearchpanel;
	jswVerticalPanel bigpanel;
	jswPushButtonset buttonset;
	public jswDropDownContactBox contactselect;
	public String edattributename;
	public Font footnotefont;
	public Font headingfont;
	public Font promptfont;

	public jswPanel mainpanel;
	public String mode = "main";
	private String os;
	private String osversion;
	public jswStyles tablestyles;
	private ToolsPanel toolspanel;
	public String user;
	String userdir;
	private String userhome;
	public String username;
	public String view = "main";
	public String dotcontacts;
	private ImageIcon jstatIcon;
	public mcImports imported;
	public String desktop;
	private mcDataTypes alldatatypes;
	public mcAttributeTypes attributetypes;
	public Properties props;
	public String propsfile;
	private String dbtitle;
	public String dbsource;
	private jswLabel title;
	private jswLabel source;
	public String budir;
	
	public mcdb(int w, int h)
	{
		super("MyContacts " + version);
		userdir = System.getProperty("user.dir");
		userhome = System.getProperty("user.home");
		user = System.getProperty("user.name");
		osversion = System.getProperty("os.version");
		os = System.getProperty("os.name");
		if (os.startsWith("Windows"))
		{
			dotcontacts = "C:/Users/" + user + "/.mccontacts/";
			letterfolder = "C:/Users/" + user + "/Documents/correspondance";
		} else
		{
			dotcontacts = "/home/" + user + "/.mccontacts/";
			letterfolder = "/home/" + user + "/Documents/correspondance";
			desktop = "/home/" + user + "/Desktop/";
		}
		java.net.URL jstatIconURL = ClassLoader.getSystemClassLoader().getResource("mccontacts.png");
		
		if (jstatIconURL != null)
		{
			jstatIcon = new ImageIcon(jstatIconURL);
			Image jstatIconImage = jstatIcon.getImage();
			this.setIconImage(jstatIconImage);
			new Dimension(jstatIcon.getIconWidth() + 2,
					jstatIcon.getIconHeight() + 2);
		} else
			System.out.println("no icon");
		System.out.println("user :" + user);
		System.out.println("user directory :" + userdir);
		System.out.println("operating system :" + os + "(" + osversion + ")");
		propsfile = dotcontacts + "properties.xml";
		props = readProperties(propsfile);
		dbsource = props.getProperty("database", "mcdb.sqlite");
		budir = props.getProperty("backupdirectory", dotcontacts+"/backup");
		
		currentcon = new mcDataSource(dotcontacts + dbsource);
		mcDataObject.setConnection(currentcon);
	
		topgui = this;

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		promptfont = new Font("SansSerif", Font.ITALIC, 9);
		initiateStyles();
		bigpanel = new jswVerticalPanel();
		getContentPane().add(bigpanel);
		jswHorizontalPanel optionBar = new jswHorizontalPanel();
		buttonset = new jswPushButtonset(this, "mode", false, false);
		buttonset.addNewOption("Browse");
		buttonset.addNewOption("Search");
		buttonset.addNewOption("Edit");
		buttonset.addNewOption("Tools");
		buttonset.setSelected("Browse");
		optionBar.add(buttonset);
		bigpanel.add(optionBar);
		jswHorizontalPanel sourceBar = new jswHorizontalPanel();
		title= new jswLabel(dbtitle);
		sourceBar.add(title);
		source= new jswLabel(dbsource);
		sourceBar.add(source);
		bigpanel.add(sourceBar);

		selbox = new mcSelectorBox(this, this);
		bigpanel.add("FILLW", selbox);
		mainpanel = new jswContainer("fred1");
		mainpanel.setLayout(new jswVerticalLayout());
		bigpanel.add(" FILLH ", mainpanel);
		bigpanel.setBorder(jswPanel.setLineBorder(Color.red, 3));
		mode = "BROWSE";
		abrowsepanel = new browsePanel();
		mainpanel.add("FILLW", abrowsepanel);
		asearchpanel = new searchPanel();
		asearchpanel.makesearchPanel(selbox, this);
		abrowsepanel = new browsePanel();
		aneditpanel = new editPanel();
		toolspanel = new ToolsPanel(this);
		// aneditpanel.showEditPanel();
		selbox.setEnabled(true);
		startup();
	}

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		String action = evt.getActionCommand().toUpperCase();
		// System.out.println(" mcdb action :" + action);
		if (action.startsWith("MODE:"))
		{
			String vstr = action.substring(5);
			mode = vstr;
			if (mode.equalsIgnoreCase("tools"))
			{

			}

		} else if (action.startsWith("VIEW:"))
		{
			if (!mode.equalsIgnoreCase("EDIT")) mode = "BROWSE";
		} else
			System.out.println("action  " + action + " unrecognised in main ");
		refresh();
	}

	public void startup()
	{
		System.out.println(os + " " + userhome);
	
		System.out.println("opening :"+ dbsource);
		Map<String, String> mychecks = currentcon.checkmcdb();
	
		mychecks.get("Valid").equalsIgnoreCase("yes");
		mychecks.get("No of Contacts");
		dbtitle = mychecks.get("Title");
		System.out.println("title "+ dbtitle);
		title.setText(dbtitle);
		source.setText(" ("+dbsource+")");
		alldatatypes = new mcDataTypes();
		alldatatypes.loadTypes();
		attributetypes = new mcAttributeTypes();
		attributetypes.dbloadTypeList();
		selbox.setAllContacts();
		selbox.clearGroupfilter();
		selbox.addtoGroupfilter("person");
		selbox.addtoGroupfilter("group");
		selbox.filterContacts();
		selbox.setSearchterm("");
		selbox.setBrowseStatus("BROWSE");
		mcLetter.getTemplates(dotcontacts);
		refresh();
	}

	public void refresh()
	{
		buttonset.setSelected(mode);
		selbox.update();
		if (mode.equals("EDIT"))
		{
			selbox.setVisible(true);
			selbox.navVisible(true);
			mainpanel.removeAll();
			// aneditpanel = new editPanel();
			aneditpanel.showEditPanel();
			mainpanel.add(aneditpanel);
		} else if (mode.equals("SEARCH"))
		{
			selbox.setVisible(true);
			selbox.navVisible(false);
			mainpanel.removeAll();
			// asearchpanel = new searchPanel();
			asearchpanel.makesearchPanel(selbox, this);
			mainpanel.add(" FILLH ", asearchpanel);
		} else if (mode.equals("BROWSE"))
		{
			selbox.setVisible(true);
			selbox.navVisible(true);
			mainpanel.removeAll();
			// abrowsepanel = new browsePanel();
			abrowsepanel.showBrowsePanel();
			mainpanel.add("FILLW", abrowsepanel);
		} else if (mode.equals("TOOLS"))
		{
			selbox.setVisible(false);
			mainpanel.removeAll();
			toolspanel.refresh();
			mainpanel.add(" FILLW ", toolspanel);
		}
		Dimension d = this.getMinimumSize();
		Rectangle fred = this.getBounds();
		fred.width = d.width;
		fred.height = d.height + 40;
		Rectangle actual = mcdb.topgui.getBounds();
		if (fred.width > actual.width) actual.width = fred.width;
		if (fred.height > actual.height) actual.height = fred.height;
		mcdb.topgui.setBounds(actual);
		getContentPane().repaint();
		getContentPane().validate();

	}

	public void initiateStyles()
	{
		tablestyles = new jswStyles();
		jswStyle cellstyle = tablestyles.makeStyle("cell");
		cellstyle.putAttribute("backgroundColor", "#C0C0C0");
		cellstyle.putAttribute("foregroundColor", "Blue");
		cellstyle.putAttribute("borderWidth", "1");
		cellstyle.putAttribute("borderColor", "white");
		cellstyle.setHorizontalAlign("LEFT");
		cellstyle.putAttribute("fontsize", "14");

		jswStyle cellcstyle = tablestyles.makeStyle("cellcontent");
		cellcstyle.putAttribute("backgroundColor", "transparent");
		cellcstyle.putAttribute("foregroundColor", "Red");
		cellcstyle.setHorizontalAlign("LEFT");
		cellcstyle.putAttribute("fontsize", "11");

		jswStyle col0style = tablestyles.makeStyle("col_0");
		col0style.putAttribute("fontStyle", Font.BOLD);
		col0style.setHorizontalAlign("RIGHT");
		col0style.putAttribute("minwidth", "true");

		jswStyle col1style = tablestyles.makeStyle("col_1");
		col1style.putAttribute("fontStyle", Font.BOLD);
		col1style.setHorizontalAlign("RIGHT");
		col1style.putAttribute("minwidth", "true");

		jswStyle tablestyle = tablestyles.makeStyle("table");
		tablestyle.putAttribute("backgroundColor", "White");
		tablestyle.putAttribute("foregroundColor", "Green");
		tablestyle.putAttribute("borderWidth", "2");
		tablestyle.putAttribute("borderColor", "blue");

		jswStyle col2style = tablestyles.makeStyle("col_2");
		col2style.putAttribute("horizontalAlignment", "RIGHT");
		col2style.putAttribute("minwidth", "true");

		allstyles = new jswStyles();
		jswStyle jswLabelStyles = allstyles.makeStyle("jswLabel");
		jswLabelStyles.putAttribute("backgroundColor", "#C0C0C0");
		jswLabelStyles.putAttribute("foregroundColor", "Black");
		jswLabelStyles.putAttribute("borderWidth", "1");
		jswLabelStyles.putAttribute("fontsize", "14");
		jswLabelStyles.putAttribute("borderColor", "#C0C0C0");

		jswStyle jswButtonStyles = allstyles.makeStyle("jswButton");
		jswButtonStyles.putAttribute("foregroundColor", "Blue");
		jswButtonStyles.putAttribute("fontsize", "10");

		jswStyle jswToggleButtonStyles = allstyles.makeStyle("jswToggleButton");
		jswToggleButtonStyles.putAttribute("foregroundColor", "Red");

		jswStyle jswTextBoxStyles = allstyles.makeStyle("jswTextBox");
		jswTextBoxStyles.putAttribute("backgroundColor", "#e0dcdf");
		jswTextBoxStyles.putAttribute("fontsize", "14");

		jswStyle jswDropDownBoxStyles = allstyles.makeStyle("jswDropDownBox");
		jswDropDownBoxStyles.putAttribute("backgroundColor", "yellow");
		jswDropDownBoxStyles.putAttribute("foregroundColor", "black");
		jswDropDownBoxStyles.putAttribute("fontsize", "14");

		jswStyle jswhpStyles = allstyles.makeStyle("jswHorizontalPanel");
		jswhpStyles.putAttribute("backgroundColor", "yellow");

		jswStyle jswDropDownContactBoxStyles = allstyles
				.makeStyle("jswDropDownContactBox");
		jswDropDownContactBoxStyles.putAttribute("backgroundColor", "blue");
		jswDropDownContactBoxStyles.putAttribute("fontsize", "10");

		jswStyle jswBorderStyle = allstyles.makeStyle("borderstyle");
		jswBorderStyle.putAttribute("borderWidth", "1");
		// jswBorderStyle.putAttribute("borderColor", "#C0C0C0");
		jswBorderStyle.putAttribute("borderColor", "black");

		jswStyle hpanelStyle = allstyles.makeStyle("hpanelstyle");
		hpanelStyle.putAttribute("borderWidth", "2");
		hpanelStyle.putAttribute("borderColor", "red");
		hpanelStyle.putAttribute("height", "100");
		jswStyle pbStyle = allstyles.makeStyle("jswPushButton");
		pbStyle.putAttribute("backgroundColor", "#C0C0C0");
		pbStyle.putAttribute("fontsize", "10");
		pbStyle.putAttribute("foregroundColor", "black");
		jswStyle greenfont = allstyles.makeStyle("greenfont");
		greenfont.putAttribute("foregroundColor", "green");
	}

	public java.util.Properties readProperties(String propsfile)
	{
		Properties prop = new Properties();
		try
		{
			prop.loadFromXML(new FileInputStream(propsfile));
			return prop;
		} catch (InvalidPropertiesFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
