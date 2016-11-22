package com.badmanners;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class AddressBook extends JFrame
{
	private JPanel panel;
	private JList<AddressEntry> listOfAddresses;
	private JTextField textFieldName;
	private JTextField textFieldAddress;
	private JButton buttonAdd;
	private JButton buttonEdit;
	private JButton buttonRemove;
	private JButton buttonLoad;
	private JButton buttonSearch;
	private JButton buttonSave;
	private JTextField textFieldSearch;
	private JButton buttonExit;

	private DefaultListModel<AddressEntry> addressModel;

	public AddressBook()
	{
		super("Адресная книга");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setContentPane(panel);
		setBounds(600, 200, 600, 400);

		addressModel = new DefaultListModel<>();
		listOfAddresses.setModel(addressModel);

		buttonAdd.addActionListener(e -> {
			if(textFieldName.getText().isEmpty() || textFieldAddress.getText().isEmpty())
				return;
			addressModel.addElement(
					new AddressEntry(textFieldName.getText(), textFieldAddress.getText()));
			listOfAddresses.setSelectedIndex(listOfAddresses.getLastVisibleIndex());
		});
		buttonRemove.addActionListener(e -> addressModel.remove(listOfAddresses.getSelectedIndex()));
		buttonSearch.addActionListener(e -> {
			if(textFieldSearch.getText().isEmpty())
				return;
			List<Integer> indices = new ArrayList<>();
			for(int i = 0; i < addressModel.size(); i++)
				if(addressModel.get(i)
							   .getAddress()
							   .equalsIgnoreCase(
									   textFieldSearch.getText()) || addressModel.get(i)
																				 .getName()
																				 .equalsIgnoreCase(
																						 textFieldSearch
																								 .getText()))
					indices.add(i);
			listOfAddresses.setSelectedIndices(
					indices.stream().mapToInt(i -> i).toArray());
		});
		listOfAddresses.addListSelectionListener(e -> {
			if(listOfAddresses.getSelectedValue() == null)
				return;
			textFieldName.setText(
					listOfAddresses.getSelectedValue().getName());
			textFieldAddress.setText(
					listOfAddresses.getSelectedValue().getAddress());
		});
		buttonEdit.addActionListener(e -> {
			listOfAddresses.getSelectedValue().setName(textFieldName.getText());
			listOfAddresses.getSelectedValue().setAddress(textFieldAddress.getText());
			listOfAddresses.repaint();
		});
		buttonExit.addActionListener(e -> System.exit(0));
		buttonLoad.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser("./");
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setFileFilter(new FileNameExtensionFilter("json", "json"));
			int result = fileChooser.showOpenDialog(this);
			JSONArray array;
			if(result == JFileChooser.APPROVE_OPTION)
				try
				{
					array = new JSONArray(new String(
							Files.readAllBytes(fileChooser.getSelectedFile().toPath())));
					addressModel.clear();
					for(int i = 0; i < array.length(); i++)
						addressModel.addElement(
								new AddressEntry(array.getJSONObject(i).getString("name"),
												 array.getJSONObject(i)
													  .getString("address")));
				} catch(IOException e1)
				{
					e1.printStackTrace();
				}
		});
		buttonSave.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser("./");
			fileChooser.setSelectedFile(new File("book.json"));
			fileChooser.setFileFilter(new FileNameExtensionFilter("json", "json"));
			JSONArray array = new JSONArray();
			for(int i = 0; i < addressModel.size(); i++)
				array.put(new JSONObject().put("name", addressModel.get(i).getName())
										  .put("address",
											   addressModel.get(i).getAddress()));
			int result = fileChooser.showSaveDialog(this);
			if(result == JFileChooser.APPROVE_OPTION)
				try
				{
					Files.write(fileChooser.getSelectedFile().toPath(),
								array.toString().getBytes());
				} catch(IOException e1)
				{
					e1.printStackTrace();
				}
		});
	}
}
