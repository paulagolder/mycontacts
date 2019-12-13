package org.lerot.mycontact.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;
import org.lerot.mycontact.gui.widgets.jswDropPane;

public class zTestDragNDropFiles {

    public static void main(String[] args) {
        new zTestDragNDropFiles();
    }

    public zTestDragNDropFiles() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            //catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException  ex) {
                } catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new jswDropPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

   
}

