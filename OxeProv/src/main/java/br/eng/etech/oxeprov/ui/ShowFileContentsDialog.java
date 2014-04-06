package br.eng.etech.oxeprov.ui;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JScrollPane;

public class ShowFileContentsDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextField txtFilename;
	private JTextField txtPath;
	private JTextArea txtCommands;

	/**
	 * Create the dialog.
	 */
	public ShowFileContentsDialog(String data, String filename, String path) {
		setTitle("Commands Contents");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblFilename = new JLabel("Filename:");
		GridBagConstraints gbc_lblFilename = new GridBagConstraints();
		gbc_lblFilename.insets = new Insets(0, 0, 5, 5);
		gbc_lblFilename.anchor = GridBagConstraints.EAST;
		gbc_lblFilename.gridx = 0;
		gbc_lblFilename.gridy = 0;
		panel.add(lblFilename, gbc_lblFilename);
		
		txtFilename = new JTextField();
		txtFilename.setEditable(false);
		lblFilename.setLabelFor(txtFilename);
		GridBagConstraints gbc_txtFilename = new GridBagConstraints();
		gbc_txtFilename.insets = new Insets(0, 0, 5, 0);
		gbc_txtFilename.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFilename.gridx = 1;
		gbc_txtFilename.gridy = 0;
		panel.add(txtFilename, gbc_txtFilename);
		txtFilename.setColumns(10);
		
		JLabel lblPath = new JLabel("Path:");
		GridBagConstraints gbc_lblPath = new GridBagConstraints();
		gbc_lblPath.anchor = GridBagConstraints.EAST;
		gbc_lblPath.insets = new Insets(0, 0, 0, 5);
		gbc_lblPath.gridx = 0;
		gbc_lblPath.gridy = 1;
		panel.add(lblPath, gbc_lblPath);
		
		txtPath = new JTextField();
		txtPath.setEditable(false);
		lblPath.setLabelFor(txtPath);
		GridBagConstraints gbc_txtPath = new GridBagConstraints();
		gbc_txtPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPath.gridx = 1;
		gbc_txtPath.gridy = 1;
		panel.add(txtPath, gbc_txtPath);
		txtPath.setColumns(10);
		
		txtCommands = new JTextArea();
		txtCommands.setFont(new Font("Courier New", Font.PLAIN, 12));
		txtCommands.setEditable(false);

		JScrollPane scrollpane = new JScrollPane(txtCommands);
		getContentPane().add(scrollpane, BorderLayout.CENTER);
		
		txtFilename.setText(filename);
		txtPath.setText(path);
		txtCommands.setText(data);
	}

}
