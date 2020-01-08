package org.lerot.gui.widgets;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;



public class jswStyles
{
	public Map<String, jswStyle> stylelist = new HashMap<String, jswStyle>();
	private String 	stylegroupname;
	public static jswStyles tablestyles;
	private static jswStyles allstyles;

	public jswStyles(String name)
	{
       stylegroupname = name;
	}

	public static jswStyles getStyles(String name)
	{
		if(name.contentEquals("tablestyles")) return tablestyles;
		else return allstyles;
	}

	
	public jswStyle getStyle(String name)
	{
		jswStyle astyle = null;
		if (stylelist.containsKey(name))
		{
			astyle = stylelist.get(name);
		}
		return astyle;
	}

	public jswStyle makeStyle(String name)
	{
		jswStyle newstyle = new jswStyle(name);
		stylelist.put(name, newstyle);
		return newstyle;
	}

	public   void copyStyle(String newname, String oldname)
	{
		jswStyle astyle = makeStyle(newname);
		jswStyle ostyle = getStyle(oldname);
		astyle.copyAll(ostyle);
	}

	
	
	public static void initiateStyles()
	{
		tablestyles = new jswStyles("tablestyles");
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

		jswStyle colstyle = tablestyles.makeStyle("col");
		colstyle.putAttribute("fontStyle", Font.PLAIN);
		colstyle.setHorizontalAlign("RIGHT");
		colstyle.putAttribute("minwidth", "true");
		
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

		allstyles = new jswStyles("allstyles");
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
		jswDropDownContactBoxStyles.putAttribute("backgroundColor", "#C0C0C0");
		jswDropDownContactBoxStyles.putAttribute("fontsize", "10");

		jswStyle jswScrollPaneStyles = allstyles
				.makeStyle("jswScrollPaneStyles");
		jswScrollPaneStyles.putAttribute("backgroundColor", "#C0C0C0");
		jswScrollPaneStyles.putAttribute("fontsize", "10");

		
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

	public static void importstyles(String name, jswStyles styles)
	{

		if(name.contentEquals("tablestyles")) tablestyles = styles;
		else allstyles = styles;
		
	}
}
