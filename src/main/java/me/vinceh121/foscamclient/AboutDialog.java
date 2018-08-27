package me.vinceh121.foscamclient;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class AboutDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblFoscamClient;
	private JLabel labelLicense;
	private JTextArea txtrLicenses;

	public AboutDialog() {
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setResizable(false);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{62, 316, 1, 0};
		gridBagLayout.rowHeights = new int[]{48, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		lblFoscamClient = new JLabel("Foscam Client");
		lblFoscamClient.setHorizontalAlignment(SwingConstants.CENTER);
		lblFoscamClient.setFont(new Font("Dialog", Font.BOLD, 40));
		GridBagConstraints gbc_lblFoscamClient = new GridBagConstraints();
		gbc_lblFoscamClient.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblFoscamClient.insets = new Insets(0, 0, 5, 5);
		gbc_lblFoscamClient.gridx = 1;
		gbc_lblFoscamClient.gridy = 0;
		getContentPane().add(lblFoscamClient, gbc_lblFoscamClient);
		
		labelLicense = new JLabel("");
		labelLicense.setIcon(new ImageIcon(AboutDialog.class.getResource("/me/vinceh121/foscamclient/gplv3-with-text-136x68.png")));
		GridBagConstraints gbc_labelLicense = new GridBagConstraints();
		gbc_labelLicense.insets = new Insets(0, 0, 5, 5);
		gbc_labelLicense.gridx = 1;
		gbc_labelLicense.gridy = 1;
		getContentPane().add(labelLicense, gbc_labelLicense);
		
		txtrLicenses = new JTextArea();
		txtrLicenses.setEditable(false);
		txtrLicenses.setText("Libraries:\n - vlcj, GLP V3\n - jna, LGPL V2.1 - Apache Software License V 2.0\n - slf4j, MIT license\n - httpclient, Apache Software License V 2.0\n - httpcore, Apache Software License V 2.0\n - commons-logging, Apache Software License V 2.0\n - commons-codec, Apache Software License V 2.0");
		GridBagConstraints gbc_txtrLicenses = new GridBagConstraints();
		gbc_txtrLicenses.insets = new Insets(0, 0, 0, 5);
		gbc_txtrLicenses.fill = GridBagConstraints.BOTH;
		gbc_txtrLicenses.gridx = 1;
		gbc_txtrLicenses.gridy = 2;
		getContentPane().add(txtrLicenses, gbc_txtrLicenses);
	}
}
