package org.lerot.mycontact.gui.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

public class jswButtonandTextBox extends jswHorizontalPanel
{

	private static final long serialVersionUID = 1L;

	jswButton actionButton;
	String command;
	JTextField datafield;

	public jswButtonandTextBox(ActionListener apanel, String label)
	{
		command = label + " button";
		actionButton = new jswButton(apanel, label, command);
		actionButton.setPreferredSize(new Dimension(200, 30));
		add(actionButton);
		setPreferredSize(new Dimension(600, 40));
		datafield = new JTextField(" ");
		datafield.setPreferredSize(new Dimension(400, 30));
		datafield.setBorder(setLineBorder());
		datafield.setFont(new Font("SansSerif", Font.BOLD, 11));
		datafield.setForeground(Color.black);
		datafield.setBackground(Color.LIGHT_GRAY);
		add("FILLW", datafield);
	}

	public String getCommand()
	{
		return command;
	}

	public String getText()
	{
		return datafield.getText();
	}

	public boolean isCommand(String com)
	{
		return command.equals(com);
	}

	public void setText(String text)
	{
		datafield.setText(text);
	}

}
