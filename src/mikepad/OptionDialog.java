package mikepad;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextField;

/**
 * Clase que representa una ventana de selección de datos, a través de una
 * entrada de texto y una lista dinámica de resultados.
 * 
 * @author Miguel Bautista Pérez
 */
public abstract class OptionDialog {
	// Ventana
	protected JDialog window;
	protected Container container;
	protected JTextField textField;
	// Lista
	protected JList<String> entryList;
	protected DefaultListModel<String> listModel;

	/**
	 * Constructor por defecto
	 * 
	 * @param parent ventana raiz
	 */
	public OptionDialog(JFrame parent) {
		window = new JDialog(parent);
		window.setSize(200, 200);
		window.setResizable(true);
		window.setLocationRelativeTo(parent);
		container = window.getContentPane();
		container.setLayout(new BorderLayout());

		textField = new JTextField();
		listModel = new DefaultListModel<String>();
		entryList = new JList<String>(listModel);
		container.add(textField, BorderLayout.NORTH);
		container.add(entryList, BorderLayout.CENTER);

		registerEvents();
		loadContent();
		window.setVisible(true);
	}

	/**
	 * Refresca la lista de resultados con cada caracter que introduzcamos.
	 */
	public abstract void loadContent();

	/**
	 * Define lo que ocurre cuando se elija un item.
	 * 
	 * @param selectedItem
	 */
	public abstract void selectionPerformed(String selectedItem);

	/**
	 * Crea los escuchadores.
	 */
	private void registerEvents() {
		window.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				loadContent();

				if (e.getKeyChar() == '\n') {
					int listSize = listModel.getSize();
					if (listSize == 1) {
						selectionPerformed(listModel.getElementAt(0));
					} else if (listSize > 1) {
						entryList.setSelectedIndex(0);

						for (int i = 0; i < listSize; i++) {
							String entry = listModel.getElementAt(i).toLowerCase();

							if (textField.getText().toLowerCase().equals(entry)) {
								entryList.setSelectedIndex(i);
								break;
							}
						}
						entryList.requestFocusInWindow();
					}
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					entryList.setSelectedIndex(0);
					entryList.requestFocusInWindow();
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					close();
				}
			}
		});

		entryList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					selectionPerformed(entryList.getSelectedValue());
				}
			}
		});

		entryList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == '\n') {
					if (entryList.getSelectedIndex() >= 0) {
						selectionPerformed(entryList.getSelectedValue());
					}
				} else if (entryList.getSelectedIndex() <= 0 && e.getKeyCode() == KeyEvent.VK_UP) {
					textField.requestFocusInWindow();
				}
			}
		});
	}

	/**
	 * Sólo sale de la ventana de diálogo.
	 */
	public void close() {
		window.dispose();
	}

	/*
	 * Solo para propósitos de test.
	 */
	public static void main(String[] args) {
		new OptionDialog(null) {
			@Override
			public void selectionPerformed(String selectedItem) {
				System.out.println("Selection: " + selectedItem);
				window.dispose();
			}

			@Override
			public void loadContent() {
				for (int i = 0; i < 10; i++) {
					listModel.addElement(Integer.toString(i));
				}
			}
		};
	}
}
