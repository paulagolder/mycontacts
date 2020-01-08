package org.lerot.gui.widgets;




import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class jswTextArea extends jswVerticalPanel   implements ActionListener 
{

    private static final long serialVersionUID = 1L;
   // private final static String newline = "\n";

    public JTextArea textbox;



    public jswTextArea(String inLabel)
    {
        super(inLabel,true);
        textbox = new JTextArea("Enter text",20,30);
        JScrollPane scrollPane = new JScrollPane(textbox); 
        textbox.setEditable(true);
        textbox.setLineWrap(true);
        textbox.setWrapStyleWord(true);
        textbox.setEnabled(true);
        scrollPane.setEnabled(true);
        setEnabled(true);
        //setStyle("");
        add("FILLH", scrollPane);
        textbox.setPreferredSize(new Dimension(100, 100));
        setPreferredSize(new Dimension(100, 100));
    }

   

    @Override
	public void actionPerformed(ActionEvent evt) {
//        String text = textbox.getText();
//        textbox.append(text + newline);
//        textbox.selectAll();
//
//        //Make sure the new text is visible, even if there
//        //was a selection in the text area.
//        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    public String getText()
    {
        return textbox.getText();
    }

    @Override
	public boolean isSelected()
    {
        return false;
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

    public void setStyle(String string)
    {
        setEnabled(true);
        textbox.setOpaque(true);
        textbox.setBackground(Color.LIGHT_GRAY);
        textbox.setDisabledTextColor(Color.blue);
        textbox.setFont(new Font("SansSerif", Font.ITALIC, 10));
        setEnabled(false);
    }

    public void setText(String t)
    {
        textbox.setText(t);
    }





}

