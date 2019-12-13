package org.lerot.mycontact.gui.widgets;

import java.awt.Color;

public class jswTable extends jswPanel
{
	private static final long serialVersionUID = 1L;

	jswStyles tablestyles = null;
	

	public jswTable(String name, jswStyles styles)
	{
		super(name);
		tablestyles = styles;
		jswStyle tablestyle = tablestyles.getStyle("table");
		setLayout(new jswTableLayout());
		Color bcolor = tablestyle.getColor("backgroundColor", Color.BLUE);
		setBackground(bcolor);
		int borderwidth = tablestyle.getIntegerStyle("borderWidth", 0);
		if (borderwidth > 0)
		{
			Color bordercolor = tablestyle
					.getColor("bordercolor", Color.yellow);
			this.setBorder(setLineBorder(bordercolor, borderwidth));

		} else
		{

		}
		setName(name);
	}

	public void addCell(jswPanel cont, int row, int col)
	{
		addCell(cont, null, row, col);
	}
	
	public void addCell(Integer anint, int row, int col)
	{
		jswLabel alabel = new jswLabel(anint);
		addCell(alabel,row,col);	
	}
	
	public void addCell(String astring, int row, int col)
	{
		jswLabel alabel = new jswLabel(astring);
		addCell(alabel,row,col);
	}

	public void addCell(jswPanel cont, String setting, int row, int col)
	{

		jswStyle cellstyle = getCellStyle(row, col);
		jswCell acell = new jswCell(row, col);
		acell.applyStyles(cellstyle);

		String settings = cellstyle.getStringStyle("horizontalAlign");
		if (setting != null) settings += " " + setting + " ";
		add(acell);
		jswStyle cellcontentstyle = getCellContentStyle(row, col);
		acell.applyStyles(cont, cellcontentstyle);
		cont.setBackground(new Color(0, 0, 0, 0));
		cont.setBorder(null);
		acell.add(settings, cont);
	}

	private jswStyle getCellContentStyle(int row, int col)
	{
		jswStyle cellstyle = new jswStyle();
		// cellstyle.overlay(tablestyles.getStyle("tablecontent"));
		cellstyle.overlay(tablestyles.getStyle("cellcontent"));
		cellstyle.overlay(tablestyles.getStyle("colcontent_" + col));
		cellstyle.overlay(tablestyles.getStyle("rowcontent_" + row));
		cellstyle.overlay(tablestyles
				.getStyle("cellcontent_" + col + "_" + row));
		return cellstyle;
	}

	private jswStyle getCellStyle(int row, int col)
	{
		jswStyle cellstyle = new jswStyle();
		// cellstyle.overlay(tablestyles.getStyle("table"));
		cellstyle.overlay(tablestyles.getStyle("cell"));
		cellstyle.overlay(tablestyles.getStyle("col_" + col));
		cellstyle.overlay(tablestyles.getStyle("row_" + row));
		cellstyle.overlay(tablestyles.getStyle("cell_" + col + "_" + row));
		return cellstyle;
	}

	public jswStyle getColStyle(int col)
	{
		jswStyle colstyle = new jswStyle();
		colstyle.overlay(tablestyles.getStyle("col_" + col));
		return colstyle;
	}

	@Override
	public void removeAll()
	{
		super.removeAll();
		
	}

	

}
