package org.lerot.mycontact.gui.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.Border;

import org.lerot.mycontact.mcdb;

public abstract class jswPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	public static Border setborder()
	{
		return BorderFactory.createEmptyBorder(5, 5, 5, 5);
	}

	public static Border setcborder(String label)
	{
		return BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(label),
				BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	public static Border setLineBorder()
	{
		return BorderFactory.createLineBorder(Color.BLACK, 1);
	}

	public static Border setLineBorder(Color col, int w)
	{
		return BorderFactory.createLineBorder(col, w);
	}

	public static Border setLineBorder(int w)
	{
		return BorderFactory.createLineBorder(Color.BLACK, w);
	}

	private int borderOffset = 1;
	private String panelname;

	private int setHeight = 0, setWidth = 0;
	jswStyle styles;

	private String tag = "";
	private String marker = "";

	public jswPanel(String name)
	{
		panelname = name;
		setStyle(this.getClass().getSimpleName());
	}

	public jswPanel(int count)
	{
		String name = String.valueOf(count);
		panelname = name;
		setStyle(this.getClass().getSimpleName());
	}

	public void applyStyles(JComponent label)
	{
		applyStyles(label, styles);
	}

	public void applyStyles(JComponent label, jswStyle usestyles)
	{
		if (label == null) return;
		jswStyle styles = usestyles;
		Font sfont = styles.getFont();
		label.setBorder(styles.getBorder());
		if (label instanceof jswLabel)
		{
			JLabel innerlabel = ((jswLabel) label).getLabel();
			innerlabel.setFont(sfont);
			innerlabel.setForeground(styles.getColor("foregroundColor",
					Color.BLACK));
			innerlabel.setBackground(styles.getColor("backgroundColor",
					Color.white));
			// innerlabel.setForeground(Color.red);
		} else if (label instanceof jswHorizontalPanel)
		{
			label.setFont(sfont);
			label.setForeground(styles.getColor("foregroundColor", Color.BLACK));
			label.setBackground(styles.getColor("backgroundColor", new Color(0,
					0, 0, 0)));
		} else if (label instanceof JLabel)
		{
			label.setFont(sfont);
			label.setForeground(styles.getColor("foregroundColor", Color.BLACK));
			label.setBackground(styles.getColor("backgroundColor", Color.white));
		} else if (label instanceof jswButton)
		{
			label.setFont(sfont);
			label.setForeground(styles.getColor("foregroundColor", Color.BLACK));
			label.setBackground(styles.getColor("backgroundColor", Color.white));
		} else if (label instanceof jswToggleButton)
		{
			label.setFont(sfont);
			label.setForeground(styles.getColor("foregroundColor", Color.BLACK));
			label.setBackground(styles.getColor("backgroundColor", Color.white));
		} else if (label instanceof JButton)
		{
			label.setFont(sfont);
			label.setForeground(styles.getColor("foregroundColor", Color.BLACK));
			label.setBackground(styles.getColor("backgroundColor", Color.white));
		} else if (label instanceof jswContainer)
		{
			label.setFont(sfont);
			label.setForeground(styles.getColor("foregroundColor", Color.BLACK));
			label.setBackground(styles.getColor("backgroundColor", Color.white));
		} else if (label instanceof jswTextBox)
		{
			label.setFont(sfont);
			label.setForeground(styles.getColor("foregroundColor", Color.BLACK));

			label.setBackground(styles.getColor("backgroundColor", Color.white));
			JTextField innerlabel = ((jswTextBox) label).getTextField();
			innerlabel.setFont(sfont);
			innerlabel.setForeground(styles.getColor("foregroundColor",
					Color.BLACK));
			innerlabel.setBackground(Color.yellow);
			// innerlabel.setBackground(styles.getColor("backgroundColor",
			// Color.white));
			System.out.println(" setting " + innerlabel.getText());

		} else if (label instanceof JTextField)
		{
			label.setFont(sfont);
			label.setForeground(styles.getColor("foregroundColor", Color.BLACK));
			label.setBackground(Color.green);
			label.setPreferredSize(new Dimension(300, 20));
		} else if (label instanceof jswTable)
		{
			label.setFont(sfont);
			label.setForeground(styles.getColor("foregroundColor", Color.BLACK));
			label.setBackground(Color.green);

		} else if (label instanceof JComboBox)
		{
			label.setFont(sfont);
			label.setForeground(styles.getColor("foregroundColor", Color.BLACK));
			label.setBackground(new Color(0, 0, 0, 0));
			// label.setPreferredSize(new Dimension(300, 20));

		} else if (label instanceof JToggleButton)
		{
			label.setFont(sfont);
			label.setForeground(styles.getColor("foregroundColor", Color.BLACK));
			label.setBackground(styles.getColor("backgroundColor", Color.green));

		} else if (label instanceof jswCheckbox)
		{
			label.setFont(sfont);
			label.setForeground(styles.getColor("foregroundColor", Color.BLACK));
			label.setBackground(styles.getColor("backgroundColor", Color.green));

		} else
		{
			System.out.println(" not setting styles for "
					+ label.getClass().getSimpleName());
		}

	}

	public void applyStyles(jswStyle usestyles)
	{
		jswStyle styles = usestyles;
		Font sfont = styles.getFont();
		this.setFont(sfont);
		this.setBorder(styles.getBorder());
		this.setForeground(styles.getColor("foregroundColor", Color.BLACK));
		this.setBackground(styles.getColor("backgroundColor", new Color(0, 0,
				0, 0)));
	}

	public void applyStyles(String stylename)
	{
		jswStyle styles = mcdb.allstyles.getStyle(stylename);
		applyStyles(this, styles);
	}

	public String getPanelname()
	{
		return panelname;
	}

	public String getTag()
	{
		return tag;
	}

	public boolean isSelected()
	{
		return false;
	};

	public int jswGetHeight()
	{
		if (setHeight > 0) return setHeight;
		Dimension d = getPreferredSize();
		if (d.height > 0) return d.height;
		else
		{
			d = getMaximumSize();
			if (d.height > 0) return d.height;
			else
			{
				d = getMinimumSize();
				return d.height;
			}
		}
	}

	public int jswGetWidth()
	{
		if (setWidth > 0) return setWidth;
		Dimension d = getPreferredSize();
		if (d.width > 0) return d.width;
		else
		{
			d = getMaximumSize();
			if (d.width > 0) return d.width;
			else
			{
				d = getMinimumSize();
				return d.width;
			}
		}
	}

	public void setPanelname(String panelname)
	{
		this.panelname = panelname;
	}

	private void setStyle(String stylename)
	{
		styles = new jswStyle();
		styles.setDefaultStyle();
		jswStyle basestyle = mcdb.allstyles.getStyle(stylename);
		styles.overlay(basestyle);
	}

	public void setStyleParameter(String attribute, int value)
	{
		styles.putAttribute(attribute, value);
	}

	public void setStyleParameter(String attribute, String value)
	{
		styles.putAttribute(attribute, value);
	}

	public void setTag(String tag)
	{
		this.tag = tag;
	}

	public void paint(Graphics g)
	{
		// System.out.println(" freddy ");
		super.paint(g);
		// g.setColor(getBackground());
		// g.fillRect(0, 0, getSize().width, getSize().height);
		// g.setColor(getForeground());
		// Rectangle border = getBorderBounds();
		// g.drawRect(border.x, border.y, border.width, border.height);
	}

	protected Rectangle getBorderBounds()
	{
		int x = borderOffset;
		int y = borderOffset;
		int width = getSize().width - borderOffset * 2;
		int height = getSize().height - borderOffset * 2;
		Rectangle bounds = new Rectangle(x, y, width, height);
		return bounds;
	}

	int getBorderOffset()
	{
		return borderOffset;
	}

	public void setBorderOffset(int borderOffset)
	{
		this.borderOffset = borderOffset;
	}

	public String getMarker()
	{
		return marker;
	}

	public void setMarker(String marker)
	{
		this.marker = marker;
	}

}
