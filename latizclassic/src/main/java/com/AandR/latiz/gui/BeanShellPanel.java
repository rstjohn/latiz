package com.AandR.latiz.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.AandR.latiz.resources.Resources;

import bsh.Interpreter;
import bsh.util.JConsole;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class BeanShellPanel extends JPanel {

    private JDialog dialog;
    private JPanel rootPane;
    private Thread beanThread;

    public BeanShellPanel() {

        dialog = new JDialog((JDialog) null, "BeanShell Panel", false);
        dialog.setLocationRelativeTo(null);
        dialog.setAlwaysOnTop(true);
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                rootPane = (JPanel) dialog.getContentPane();
                dialog.setVisible(false);
                add(rootPane);
                revalidate();
                repaint();
            }
        });

        JConsole console = new JConsole();
        Interpreter interpreter = new Interpreter(console);
        beanThread = new Thread(interpreter);
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                if (!beanThread.isAlive()) {
                    beanThread.start();
                }
            }
        });

        JButton dialogButton = createTopButton("remove16.png", "Show in dialog");
        dialogButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showInDialog();
            }
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(dialogButton);

        rootPane = new JPanel(new BorderLayout());
        rootPane.add(topPanel, BorderLayout.NORTH);
        rootPane.add(console, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(rootPane, BorderLayout.CENTER);
    }

    private void showInDialog() {
        if (dialog.isVisible()) {
            rootPane = (JPanel) dialog.getContentPane();
            dialog.setVisible(false);
            add(rootPane);
            revalidate();
            repaint();
        } else {
            dialog.setContentPane(rootPane);
            dialog.pack();
            dialog.setVisible(true);
            remove(rootPane);
            repaint();
        }
    }

    private JButton createTopButton(String icon, String tooltip) {
        JButton button = new JButton(Resources.createIcon(icon));
        button.setPreferredSize(new Dimension(18, 18));
        button.setBackground(this.getBackground());
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusable(false);
        button.setToolTipText(tooltip);
        return button;
    }
}
