package org.lerot.gui.widgets;


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.border.Border;

public class jswTableLayout extends jswLayout
{

	private class rowcol
	{
		public boolean minwidth;
		protected double position;
		protected double size;

		@Override
		public String toString()
		{
			String out = "minwidth=" + minwidth + " position=" + position
					+ " size=" + size;
			return out;
		}
	}

	public static final int CENTER = 0;
	private static final int DEFAULT_HGAP = 0;
	private static final int DEFAULT_VGAP = 0;
	private Vector<rowcol> columns;
	private int hgap;
	int maxcol = 0;
	int maxrow = 0;

	private Vector<rowcol> rows;
	private String marker;

	public jswTableLayout()
	{
		this(DEFAULT_HGAP, DEFAULT_VGAP);
	}

	public jswTableLayout(int hgap, int vgap)
	{
		this.hgap = hgap;
		rows = new Vector<rowcol>();
		columns = new Vector<rowcol>();
	}

	public void changed()
	{
	}

	@Override
	public void layoutContainer(Container parent)
	{
		if (parent instanceof jswPanel)
		{
			marker = ((jswPanel) parent).getMarker();
			
		}
		int ncomponents = parent.getComponentCount();
		if (ncomponents == 0)
		{
			this.hgap = 0;
		}
		Insets insets = parent.getInsets();
		Dimension parentSize = parent.getParent().getSize();
		int usableWidth = parentSize.width - insets.left - insets.right - 2;
		// paul fix replace by border width?
		// - (fixedwidth);

		int availableHeight = parentSize.height - insets.top - insets.bottom;
		setRowCols((jswTable) parent);
		scaleRows(availableHeight);
		scaleColumns(usableWidth);
		for (int i = 0; i < ncomponents; i++)
		{
			Component comp = parent.getComponent(i);
			// getSettings(comp);

			if (comp instanceof jswCell)
			{
				jswCell cell = (jswCell) comp;
				int ncol = cell.col;
				int nrow = cell.row;
				int colspan = cell.colspan;
				rowcol rowsettings = rows.get(nrow);
				int y = (int) rowsettings.position + insets.top;
				int height = (int) rowsettings.size; // +insets.top+insets.bottom;
				rowcol colsettings = columns.get(ncol);
				int x = (int) colsettings.position + insets.left;
				int width = 0;
				if (colspan > 1)
				{
					for (int c = 0; c < colspan; c++)
					{
						width += (int) columns.get(ncol + c).size;
					}

				} else
					width = (int) colsettings.size;
				comp.setBounds((x), (y), (width), (height));
			}
		}
	}

	public Dimension maximumLayoutSize(Container parent)
	{
		return minimumLayoutSize(parent);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent)
	{
		int ncomponents = parent.getComponentCount();
		if (ncomponents == 0)
		{
			this.hgap = 0;
		}
		setRowCols((jswTable) parent);
		double height = 0;
		for (rowcol r : rows)
		{
			height = height + r.size;
		}
		double width = 0;
		for (rowcol c : columns)
		{
			if (c != null) width = width + c.size;
		}
		int bwidth = 0;
		int bheight = 0;
		if (parent instanceof jswTable)
		{
			Border aborder = ((jswTable) parent).getBorder();
			if (aborder != null)
			{
				Insets insts = aborder.getBorderInsets(parent);
				bheight = insts.bottom + insts.top;
				bwidth = insts.left + insts.right;
			}
		}
		// System.out.format(" tablesize %d %d %n", (int) width, (int) height);
		return new Dimension((int) width + bwidth, (int) height + bheight);
	}

	@Override
	public Dimension preferredLayoutSize(Container parent)
	{
		return minimumLayoutSize(parent);
	}

	private void scaleColumns(int targetwidth)
	{
		int cwidth = 0;
		int cfixed = 0;

		for (int ncol = 0; ncol < maxcol; ncol++)
		{
			rowcol cset = columns.get(ncol);
			cwidth += cset.size;
			if (cset.minwidth) cfixed += cset.size;
		}
		double desiredwidth = cwidth;
		double fixedwidth = cfixed;
		double scale = 1;
		if (targetwidth > desiredwidth)
		{
			scale = (targetwidth - fixedwidth) / (desiredwidth - fixedwidth);
			int cpos = 0;
			for (int ncol = 0; ncol < maxcol; ncol++)
			{
				rowcol cset = columns.get(ncol);
				if (!cset.minwidth) cset.size = cset.size * scale;
				cset.position = cpos;
				cpos += cset.size;
				columns.set(ncol, cset);
			}
		} else
		{
			scale = targetwidth / desiredwidth;
			int cpos = 0;
			for (int ncol = 0; ncol < maxcol; ncol++)
			{
				rowcol cset = columns.get(ncol);
				cset.size = cset.size * scale;
				cset.position = cpos;
				cpos += cset.size;
				columns.set(ncol, cset);
			}
		}

	}

	private void scaleRows(int targetheight)
	{

		int rpos = 0;
		for (int nrow = 0; nrow < maxrow; nrow++)
		{
			rowcol rset = rows.get(nrow);
			if (rset != null)
			{
				rset.position = rpos;
				rows.set(nrow, rset);
				rpos += rset.size;
			}
		}
		/*
		 * if (targetheight < 1) return; if (rpos < 1) return;
		 * 
		 * float scale = 1.0f; if (rpos < targetheight) scale = targetheight /
		 * rpos; rpos = 0; for (int nrow = 0; nrow < maxrow; nrow++) { rowcol
		 * rset = rows.get(nrow); if (rset != null) { rset.size = rset.size *
		 * scale; rset.position = rpos; rpos += rset.size; rows.set(nrow, rset);
		 * } }
		 */
	}

	public void setRowCols(jswTable parent)
	{
		rows.clear();
		columns.clear();
		maxrow = 0;
		maxcol = 0;
		int ncomponents = parent.getComponentCount();
		for (int i = 0; i < ncomponents; i++)
		{
			Component comp = parent.getComponent(i);
			if (comp instanceof jswCell)
			{
				jswCell cell = (jswCell) comp;

				int ncol = cell.col;
				int nrow = cell.row;
				if (ncol > maxcol) maxcol = ncol;
				if (nrow > maxrow) maxrow = nrow;
			}
		}
		maxcol++;
		maxrow++;
		rows.setSize(maxrow);
		columns.setSize(maxcol);
		for (int nrow = 0; nrow < maxrow; nrow++)
		{
			rowcol rset = new rowcol();
			rset.size = 0;
			rset.position = 0;
			rows.set(nrow, rset);
		}
		for (int ncol = 0; ncol < maxcol; ncol++)
		{
			rowcol cset = new rowcol();
			cset.size = 0;
			cset.position = 0;
			jswStyle colstyle = parent.getColStyle(ncol);
			if (colstyle != null)
			{
				int width = colstyle.getIntegerStyle("width", 0);
			//	System.out.println(" tablestyle coll="+ncol+" width ="+width);
				cset.minwidth = colstyle.getBooleanStyle("minwidth", false);
				if (width > 0)
				{
					cset.size = width;
					cset.minwidth = true;
				}
				;
			} else
			{
				cset.minwidth = false;
			}
			columns.set(ncol, cset);
			//
		}
		for (int i = 0; i < ncomponents; i++)
		{
			Component comp = parent.getComponent(i);
			if (comp instanceof jswCell)
			{
				jswCell cell = (jswCell) comp;
				// settings s = getSettings(comp);
				Dimension min = null;
				if(cell.getComponentCount()==0) continue;
				Component cellcontent = cell.getComponent(0);
				if (cellcontent instanceof jswPanel)
				{
					jswPanel jccomp = (jswPanel) cellcontent;
					min = jccomp.getMinimumSize();

				} else
				{
					JComponent jccomp = (JComponent) cellcontent;
					min = jccomp.getMinimumSize();
					System.out.println(" not jswpanel ="
							+ comp.getClass().getName());
				}
				int ncol = cell.col;
				int nrow = cell.row;
				int colspan = cell.colspan;
				int width = cell.width;
				if (width > min.width)
				{
					min.width = width;
					System.out.println(" Setting width =" + width);
				}
				if (ncol > -1 && nrow > -1)
				{

					rowcol rset = rows.get(nrow);
					if (rset.size < min.height)
					{
						rset.size = min.height;
						rows.set(nrow, rset);
					}

					if (colspan > 1)
					{

						double sharedwidth = min.width / colspan;
						for (int c = ncol; c < ncol + colspan; c++)
						{
							rowcol cset = columns.get(c);
							if (cset.size < sharedwidth)
							{
								cset.size = sharedwidth;
								columns.set(c, cset);
							}
						}
					} else
					{
						rowcol cset = columns.get(ncol);
						if (cset.size < min.width)
						{
							cset.size = min.width;
							columns.set(ncol, cset);
						}
					}

				}
			}
		}
		for (int ncol = 0; ncol < maxcol; ncol++)
		{
			rowcol cset = columns.get(ncol);
			jswStyle colstyle = parent.getColStyle(ncol);
		//	System.out.println(" colstyle    "+colstyle);
			cset.minwidth = false;
			if (colstyle != null)
			{
				int width = colstyle.getIntegerStyle("width", 0);
				cset.minwidth = colstyle.getBooleanStyle("minwidth", false);
				if (width > 0)
				{
					cset.minwidth = true;
					if (width > cset.size)
					{
						cset.size = width;
					}
				}
			} 
			columns.set(ncol, cset);
			//System.out.println(" tablestyle  coll="+ncol+ " "+cset);
		}

	}

}
