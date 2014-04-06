package br.eng.etech.oxeprov.ui;

import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Font;

public class CommandDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea txtResponse;
	private JTextArea txtCommand;

	/**
	 * Create the dialog.
	 */
	public CommandDialog(String command) {
		setTitle("OXE Command");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);

		JLabel lblCommand = new JLabel("Command:");
		GridBagConstraints gbc_lblCommand = new GridBagConstraints();
		gbc_lblCommand.insets = new Insets(0, 0, 0, 5);
		gbc_lblCommand.anchor = GridBagConstraints.EAST;
		gbc_lblCommand.gridx = 0;
		gbc_lblCommand.gridy = 0;
		panel.add(lblCommand, gbc_lblCommand);
		
		txtCommand = new JTextArea();
		lblCommand.setLabelFor(txtCommand);
		txtCommand.setText("<dynamic>");
		txtCommand.setEditable(false);
		txtCommand.setColumns(10);
		GridBagConstraints gbc_txtCommand = new GridBagConstraints();
		gbc_txtCommand.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtCommand.gridx = 1;
		gbc_txtCommand.gridy = 0;
		panel.add(txtCommand, gbc_txtCommand);
		
		txtResponse = new JTextArea();
		txtResponse.setFont(new Font("Courier New", Font.PLAIN, 12));

		JScrollPane scrollPane = new JScrollPane(txtResponse);
		scrollPane.setEnabled(false);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		txtCommand.setText(command);
	}

	public String getResponse() {
		return txtResponse.getText();
	}

}
