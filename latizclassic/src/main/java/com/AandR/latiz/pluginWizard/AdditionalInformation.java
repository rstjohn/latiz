package com.AandR.latiz.pluginWizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Date;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * @author Dr. Richard St. John
 * @version $Revision$, $Date$
 */
public class AdditionalInformation extends WizardPanel {

    private JCheckBox checkRestrictedAccess;
    private JTextArea areaKeyword, areaComments;
    private JTextField fieldAuthor, fieldDate;

    public AdditionalInformation(Properties props) {
        initialize();
        setLayout(new BorderLayout());
        add(createPanel());
    }

    private void initialize() {
        fieldAuthor = new JTextField(System.getProperty("user.name"), 15);
        fieldDate = new JTextField(new Date().toString());
        areaKeyword = new JTextArea(2, 15);
        areaComments = new JTextArea(2, 15);
        checkRestrictedAccess = new JCheckBox("Restricted Access?", false);
        checkRestrictedAccess.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private Component createPanel() {
        JPanel keywordsLabelPanel = new JPanel(new BorderLayout());
        keywordsLabelPanel.add(createLabel("Keywords:"), BorderLayout.NORTH);
        JPanel keywordPanel = new JPanel(new BorderLayout());
        keywordPanel.add(keywordsLabelPanel, BorderLayout.WEST);
        keywordPanel.add(new JScrollPane(areaKeyword), BorderLayout.CENTER);

        JPanel commentsLabelPanel = new JPanel(new BorderLayout());
        commentsLabelPanel.add(createLabel("Comments:"), BorderLayout.NORTH);
        JPanel commentsPanel = new JPanel(new BorderLayout());
        commentsPanel.add(commentsLabelPanel, BorderLayout.WEST);
        commentsPanel.add(new JScrollPane(areaComments), BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.add(keywordPanel);
        centerPanel.add(commentsPanel);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(checkRestrictedAccess, BorderLayout.SOUTH);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private JLabel createLabel(String label) {
        JLabel jlabel = new JLabel(label);
        jlabel.setPreferredSize(new Dimension(80, 22));
        return jlabel;
    }

    public JTextField getFieldAuthor() {
        return fieldAuthor;
    }

    public JTextField getFieldDate() {
        return fieldDate;
    }

    public JTextArea getAreaKeyword() {
        return areaKeyword;
    }

    public JTextArea getAreaComments() {
        return areaComments;
    }

    public JCheckBox getCheckRestrictedAccess() {
        return checkRestrictedAccess;
    }

    public String getMessageLabel() {
        return "The information defined on this page is used to catalogue plugins. <BR><BR>The Latiz plugin "
                + "search feature uses the keywords to find plugins.";
    }

    public String getMessageTitle() {
        return "Additional Information Page";
    }
}
