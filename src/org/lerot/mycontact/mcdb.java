package org.lerot.mycontact;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.lerot.mywidgets.jswContainer;
import org.lerot.mywidgets.jswHorizontalPanel;
import org.lerot.mywidgets.jswLabel;
import org.lerot.mywidgets.jswPanel;
import org.lerot.mywidgets.jswPushButtonset;
import org.lerot.mywidgets.jswStyle;
import org.lerot.mywidgets.jswStyles;
import org.lerot.mywidgets.jswVerticalLayout;
import org.lerot.mywidgets.jswVerticalPanel;
import org.lerot.mycontact.gui.ToolsPanel;
import org.lerot.mycontact.gui.browsePanel;
import org.lerot.mycontact.gui.editPanel;
import org.lerot.mycontact.gui.selectorBox;
import org.lerot.mycontact.gui.widgets.jswDropDownContactBox;
import org.lerot.mycontact.gui.searchPanel;

//import org.lerot.mycontact.forms.certificateeditpanel;
//import org.lerot.mycontact.forms.documentTemplate;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DesertBluer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.List;

public class mcdb extends JFrame implements ActionListener
{

	public class selbox
	{

	}

	public static JFrame mframe;
	public mcDataSource currentcon;
	public static jswStyles panelstyles;
	public static jswStyles tablestyles;
	public static Component browserpanel;
	public static String certificatepath;

	private static final long serialVersionUID = 1L;
	public static boolean started = false;
	public static boolean showborders;
	public static String temppath;
	public static mcdb topgui;
	static String version = "V 22.0";
	public static selectorBox selbox;
	public static String letterfolder;
	public static String docsfolder;
	public static Map<String, Map<String, String>> labeltemplates = null;

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

		mframe = new mcdb(900, 700);
		mframe.setVisible(true);

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
		Dimension actual = new Dimension();
		actual.width = 900;
		actual.height = 700;
		mframe.setSize(actual);

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - mframe.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - mframe.getHeight()) / 2);
		mframe.setLocation(x, y);

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
	public static mcDataTypes alldatatypes;
	public static mcAttributeTypes myattributetypes;
	public Properties props;
	public String propsfile;
	private String dbtitle;
	public String dbsource;
	public String docs;
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
			docsfolder = "C:/Users/" + user + "/Documents/correspondance";
		} else
		{
			dotcontacts = "/home/" + user + "/.mccontacts/";
			desktop = "/home/" + user + "/Desktop/";
			letterfolder = desktop + "Labels and Letters/";
			docsfolder = "/home/" + user + "/Documents/correspondance";
		}
		java.net.URL jstatIconURL = ClassLoader.getSystemClassLoader()
				.getResource("mccontacts.png");

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
		budir = props.getProperty("backupdirectory", dotcontacts + "/backup");
		docs = props.getProperty("docs", "Documents/correspondance/");
		currentcon = new mcDataSource(dotcontacts + dbsource);

		topgui = this;
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});

		initiateStyles();

		bigpanel = new jswVerticalPanel("bigpanel", true);
		bigpanel.setBorder(BorderFactory.createLineBorder(Color.blue));
		bigpanel.setName("bigpanel");
		bigpanel.setPreferredSize(new Dimension(800, 500));
		bigpanel.setSize(new Dimension(800, 500));
		bigpanel.setMinimumSize(new Dimension(800, 500));
		getContentPane().add(bigpanel);
		jswHorizontalPanel optionBar = new jswHorizontalPanel();
		buttonset = new jswPushButtonset(this, "mode", false, false);
		buttonset.setBorder(jswStyle.makeLineBorder(Color.red, 1));
		buttonset.addNewOption("Browse");
		buttonset.addNewOption("Search");
		buttonset.addNewOption("Edit");
		buttonset.addNewOption("Tools");
		buttonset.setSelected("Browse");
		optionBar.add(" ", buttonset);
		optionBar.setBorder(jswStyle.makeLineBorder(Color.pink, 1));
		bigpanel.add(" ", optionBar);
		jswHorizontalPanel sourceBar = new jswHorizontalPanel();
		title = new jswLabel(dbtitle);
		sourceBar.add(title);
		source = new jswLabel(dbsource);
		sourceBar.add(source);
		sourceBar.setBorder(jswStyle.makeLineBorder(Color.GREEN, 1));
		bigpanel.add(" height=40 ", sourceBar);
		selbox = new selectorBox(this, this);
		bigpanel.add("FILLW", selbox);
		mainpanel = new jswVerticalPanel("mainpanel", false);
		bigpanel.add(" FILLH ", mainpanel);
		bigpanel.setBorder(jswStyle.makeLineBorder(Color.GRAY, 3));
		abrowsepanel = new browsePanel();
		mainpanel.add(" FILLH ", abrowsepanel);
		asearchpanel = new searchPanel();
		asearchpanel.makesearchPanel(selbox, this);
		aneditpanel = new editPanel();
		selbox.setEnabled(true);
		startup();
		toolspanel = new ToolsPanel(this);
		selbox.setTaglist();
		selbox.refreshAllContacts("1");
		refreshView();
		initDragAndDrop();
		mcdb.started = true;
	}

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		String action = evt.getActionCommand().toUpperCase();
		System.out.println(" mcdb action :" + action);
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
		refreshView();
	}

	public void startup()
	{
		System.out.println(os + " " + userhome);
		System.out.println("opening :" + dbsource);
		Map<String, String> mychecks = currentcon.checkmcdb();
		if (mychecks.get("Valid").equalsIgnoreCase("yes"))
			System.out.println("Checks ok");
		else
			System.out.println("Checks NOT ok");
		String ncon = mychecks.get("No of Contacts");
		dbtitle = mychecks.get("Title");
		System.out.println("title " + dbtitle);
		title.setText(dbtitle);
		source.setText(" (" + dbsource + ")");
		System.out.println("No of contacts = " + ncon);
		mcDataTypes.loadTypes();
		mcAttributeTypes.loadAttributeTypes();
		selbox.setBrowseFilter("all");
		mode = "BROWSE";
		mcLetter.getTemplates(dotcontacts);
		labeltemplates = mcPDF.readTemplates();
	}

	public void refreshView()
	{
		buttonset.setSelected(mode);
		if (mode.equals("EDIT"))
		{
			selbox.setVisible(true);
			selbox.navVisible(true);
			selbox.filterboxVisible(false);
			mainpanel.removeAll();
			aneditpanel.makeEditPanel();
			mainpanel.add(aneditpanel);
			aneditpanel.repaint();
		} else if (mode.equals("SEARCH"))
		{
			selbox.setVisible(true);
			selbox.navVisible(false);
			mainpanel.removeAll();
			asearchpanel.makesearchPanel(selbox, this);
			mainpanel.add(" FILLH ", asearchpanel);
			asearchpanel.repaint();
		} else if (mode.equals("BROWSE"))
		{
			selbox.setVisible(true);
			selbox.navVisible(true);
			selbox.filterboxVisible(true);
			mainpanel.removeAll();
			abrowsepanel.removeAll();
			abrowsepanel.makeBrowsePanel();
			mainpanel.add("FILLW", abrowsepanel);
			abrowsepanel.repaint();
		} else if (mode.equals("TOOLS"))
		{
			selbox.setVisible(false);
			mainpanel.removeAll();
			mainpanel.add(" FILLW ", toolspanel);
			toolspanel.repaint();
		}

		mainpanel.repaint();
		if (mframe != null) mframe.repaint();
		getContentPane().repaint();
		getContentPane().validate();
	}

	public static void initiateStyles()
	{
		tablestyles = jswStyles.getDefaultStyles();
		tablestyles.name = "table";
		jswStyle tablestyle = tablestyles.makeStyle("table");
		tablestyle.putAttribute("background", "red");
		jswStyle rowstyle = tablestyles.makeStyle("row");
		rowstyle.putAttribute("height", "10");
		jswStyle col0style = tablestyles.makeStyle("col_0");
		col0style.putAttribute("fontStyle", Font.BOLD);
		col0style.setHorizontalAlign("RIGHT");
		col0style.putAttribute("minwidth", "true");
		jswStyle col1style = tablestyles.makeStyle("col_1");
		col1style.putAttribute("fontStyle", Font.BOLD);
		col1style.setHorizontalAlign("RIGHT");
		jswStyle col2style = tablestyles.makeStyle("col_2");
		col2style.putAttribute("horizontalAlignment", "RIGHT");
		col2style.putAttribute("minwidth", "true");

		panelstyles = jswStyles.getDefaultStyles();
		panelstyles.name = "panel";
		jswStyle jswWidgetStyles = panelstyles.makeStyle("jswWidget");
		jswWidgetStyles.putAttribute("backgroundColor", "#e0dcdf");
		jswWidgetStyles.putAttribute("boxbackgroundColor", "GREEN");
		jswWidgetStyles.putAttribute("foregroundColor", "Black");
		jswWidgetStyles.putAttribute("borderWidth", "0");
		jswWidgetStyles.putAttribute("fontsize", "14");
		jswWidgetStyles.putAttribute("borderColor", "blue");

		jswStyle jswLabelStyles = panelstyles.makeStyle("jswLabel");

		jswStyle jswButtonStyles = panelstyles.makeStyle("jswButton");
		jswButtonStyles.putAttribute("fontsize", "10");

		jswStyle jswToggleButtonStyles = panelstyles
				.makeStyle("jswToggleButton");
		jswToggleButtonStyles.putAttribute("foregroundColor", "Red");

		jswStyle jswTextBoxStyles = panelstyles.makeStyle("jswTextBox");

		jswStyle jswTextFieldStyles = panelstyles.makeStyle("jswTextField");
		// jswTextFieldStyles.putAttribute("backgroundColor", "#e0dcdf");

		jswStyle jswDropDownBoxStyles = panelstyles.makeStyle("jswDropDownBox");
		// jswDropDownBoxStyles.putAttribute("backgroundColor","#C0C0C0");

		jswStyle jswhpStyles = panelstyles.makeStyle("jswContainer");
		jswhpStyles.putAttribute("backgroundColor", "#C0C0C0");

		jswStyle jswDropDownContactBoxStyles = panelstyles
				.makeStyle("jswDropDownContactBox");
		jswDropDownContactBoxStyles.putAttribute("backgroundColor", "#C0C0C0");
		jswDropDownContactBoxStyles.putAttribute("fontsize", "10");

		jswStyle jswScrollPaneStyles = panelstyles
				.makeStyle("jswScrollPaneStyles");
		jswScrollPaneStyles.putAttribute("backgroundColor", "#C0C0C0");
		jswScrollPaneStyles.putAttribute("fontsize", "10");

		jswStyle jswBorderStyle = panelstyles.makeStyle("borderstyle");
		jswBorderStyle.putAttribute("borderWidth", "1");
		// jswBorderStyle.putAttribute("borderColor", "#C0C0C0");
		jswBorderStyle.putAttribute("borderColor", "black");

		jswStyle hpanelStyle = panelstyles.makeStyle("hpanelstyle");
		hpanelStyle.putAttribute("borderWidth", "2");
		hpanelStyle.putAttribute("borderColor", "blue");
		hpanelStyle.putAttribute("height", "100");

		jswStyle pbStyle = panelstyles.makeStyle("jswPushButton");
		pbStyle.putAttribute("backgroundColor", "#C0C0C0");
		pbStyle.putAttribute("fontsize", "10");

		pbStyle.putAttribute("foregroundColor", "black");
		jswStyle greenfont = panelstyles.makeStyle("greenfont");
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

	private void initDragAndDrop()
	{
		this.setDropTarget(new DropTarget()
		{
			private static final long serialVersionUID = 1L;

			// mcContact selcontact = mcdb.selbox.getSelcontact();
			@Override
			public synchronized void drop(DropTargetDropEvent dtde)
			{
				try
				{
					System.out.println("File dropped 487 ");
					mcContact selcontact = mcdb.selbox.getSelcontact();
					File directory = new File(mcdb.docsfolder + File.separator
							+ selcontact.getID());
					if (!directory.exists())
					{
						directory.mkdir();
					}
					Transferable transfer = dtde.getTransferable();
					if (transfer.isDataFlavorSupported(
							DataFlavor.javaFileListFlavor))
					{
						dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
						List objects = (List) transfer
								.getTransferData(DataFlavor.javaFileListFlavor);
						for (Object object : objects)
						{

							if (object instanceof File)
							{
								File source = (File) object;
								// File dest = new
								// File(System.getProperty("user.home")+File.separator+"source.getName());"
								File dest = new File(mcdb.docsfolder
										+ File.separator + selcontact.getID()
										+ File.separator + source.getName());

								Files.copy(Paths.get(source.getAbsolutePath()),
										Paths.get(dest.getAbsolutePath()),
										StandardCopyOption.REPLACE_EXISTING);
								SimpleDateFormat sdf = new SimpleDateFormat(
										"YYYY-MM-dd");
								String date = sdf.format(source.lastModified());
								selcontact.addCorrespondance(source.getName(),
										date, "gunge", dest);

								System.out.println("File copied from 509 "
										+ source.getAbsolutePath() + " to "
										+ dest.getAbsolutePath());
							}
						}
					} else if (transfer
							.isDataFlavorSupported(DataFlavor.stringFlavor))
					{
						dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
						String type = (String) transfer
								.getTransferData(DataFlavor.stringFlavor);
						System.out
								.println("Data flavor not supported: " + type);
					} else
					{
						System.out.println("Data flavor not supported.");
					}
				} catch (UnsupportedFlavorException ex)
				{
					System.err.println("UFException " + ex.getMessage());
				} catch (IOException ex)
				{
					System.err.println("IOException " + ex.getMessage());
				} catch (Exception ex)
				{
					System.err.println("Exception " + ex.getMessage());
				} finally
				{
					dtde.dropComplete(true);
				}
			}
		});
	}

}
