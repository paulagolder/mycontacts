package org.lerot.mycontact.gui.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

import org.lerot.mycontact.mcdb;

public class jswTextField extends jswPanel
{

	private static final long serialVersionUID = 1L;
	public JTextField textbox;

	public jswTextField(String prompt)
	{
		super("TF:");
		setLayout(new jswHorizontalLayout());
		textbox = new JTextField();
	    if(prompt==null || prompt.isEmpty())
	    {
	    	prompt=" ";
	    }
		jswTextPrompt tp7 = new jswTextPrompt(prompt, textbox);
		tp7.setForeground( Color.GREEN );
		tp7.setFont(mcdb.topgui.promptfont);
		tp7.changeAlpha(0.5f);
		tp7.changeStyle(Font.ITALIC);
		tp7.setVisible(true);
		add("FILLW", textbox);
		textbox.setVisible(true);
	}
	
	public jswTextField()
	{
		this(null);	
	}

	@Override
	public void addFocusListener(FocusListener fl)
	{
		textbox.addFocusListener(fl);
	}

	@Override
	public void addKeyListener(KeyListener kl)
	{
		textbox.addKeyListener(kl);
	}

	public void clear()
	{
		textbox.setText("");
	}

	public int getInteger()
	{
		return Integer.parseInt(textbox.getText());
	}

	public String getText()
	{
		return textbox.getText();
	}

	public JTextField getTextField()
	{
		return textbox;
	}

	@Override
	public boolean isSelected()
	{
		return false;
	}

	public void setEditable(boolean e)
	{
		textbox.setEditable(e);
	}

	@Override
	public void setEnabled(boolean e)
	{
		textbox.setEnabled(e);
	}

	public void setSelected(boolean b)
	{
		;
	}
	
	@Override
	public void setSize(int w, int h)
	{
		textbox.setSize(w,h);
	}

	public void setText(int t)
	{
		textbox.setText(Integer.toString(t));
	}

	public void setText(String t)
	{
		textbox.setText(t);
	}

	@Override
	public void setVisible(boolean e)
	{
		textbox.setVisible(e);
	}

	@Override
	public Dimension getMinimumSize()
	{
		String text = textbox.getText();
		int width = text.length() * 10;
		return new Dimension(width, 0);
	}
}
