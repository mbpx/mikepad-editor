package mikedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.drjekyll.fontchooser.FontDialog;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

public class MainWindow {

	// Componente de swing que representa una ventana
	private JFrame window;

	// Objeto que representa el contenido de la ventana
	private Container container;

	// Objeto que representa un fichero
	// (básicamente es la ruta a un fichero)
	private File editingFile;
	private File directoryFile;
	
	// Componentes del editor de texto
	private RSyntaxTextArea editor;
	private RTextScrollPane scrollEditor;
	
	// Componentes del menú
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu editMenu;
	private JMenu formatMenu;
	
	// Acciones
	private Action openAction;
	private Action saveAction;
	private Action saveAsAction;
	private Action undoAction;
	private Action redoAction;
	private Action fontChooseAction;
	private Action exitAction;
	private Action syntaxHighlightAction;
	
	// Panel de abajo
	private JPanel southPanel;
	private JLabel fileLabel;
	private JLabel pathLabel;
	
	// Selectores
	private JFileChooser fileChooser;
	
	// Constantes de la aplicación
	private final String app_name = "mikedit";

	// Constructor.
	public MainWindow() {

		createComponents();
		createActions();
		setKeyStrokes();
		placeComponents();
		window.setVisible(true);
		registerEvents();

	}

	/**
	 * Crea los objetos que serán representados como componentes de la ventana de la
	 * aplicación.
	 * 
	 * También incluiremos las configuraciones a nuestros componentes, con lo que
	 * este método contendrá una gran parte de la información visual de la app.
	 */
	private void createComponents() {
		// Crear la ventana
		window = new JFrame();
		container = window.getContentPane();

		// Editor
		editor = new RSyntaxTextArea();
		scrollEditor = new RTextScrollPane(editor);
		editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		editor.setCodeFoldingEnabled(true);
		
		// Barra de menu
		menuBar = new JMenuBar();
		menuBar.setBorderPainted(false);
		fileMenu = new JMenu("Archivo");
		editMenu = new JMenu("Editar");
		formatMenu = new JMenu("Formato");

		// Panel de abajo
		southPanel = new JPanel();
		pathLabel = new JLabel("nuevo documento");
		pathLabel.setForeground(Color.gray);
		fileLabel = new JLabel();
		fileLabel.setForeground(Color.darkGray);

		// Otros componentes
		fileChooser = new JFileChooser();
	}

	/**
	 * Crea las acciones, que son objetos cuyo contenido es simplemente una función,
	 * que será ejecutada cuando se active la acción por algún método.
	 */
	@SuppressWarnings("serial")
	private void createActions() {

		openAction = new AbstractAction("Abrir") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int retVal = fileChooser.showOpenDialog(window);
				if (retVal == JFileChooser.APPROVE_OPTION) {
					editingFile = fileChooser.getSelectedFile();
					try {
						FileReader reader = new FileReader(editingFile);
						editor.read(reader, "");

					} catch (IOException ioex) {
						ioex.printStackTrace();
					}
					refreshWindow();
				}
			}
		};
		
		openAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);

		saveAction = new AbstractAction("Guardar") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (editingFile != null) {
					try {
						FileWriter writer = new FileWriter(editingFile);
						editor.write(writer);
					} catch (IOException ioex) {
						ioex.printStackTrace();
					}
				} else {
					saveAsAction.actionPerformed(null);
				}
			}
		};

		saveAsAction = new AbstractAction("Guardar como") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int retVal = fileChooser.showSaveDialog(window);
				if (retVal == JFileChooser.APPROVE_OPTION) {
					editingFile = fileChooser.getSelectedFile();
					saveAction.actionPerformed(null);
					refreshWindow();
				}
			}
		};

		undoAction = RSyntaxTextArea.getAction(RSyntaxTextArea.UNDO_ACTION);

		redoAction = RSyntaxTextArea.getAction(RSyntaxTextArea.REDO_ACTION);
		
		fontChooseAction = new AbstractAction("Tipo de letra") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				FontDialog.showDialog(editor);
			}
		};
		
		exitAction = new AbstractAction("Salir") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		};
		
		syntaxHighlightAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				
			}
		};
	}

	/**
	 * Configura el mapa de acciones del objeto editor y establece una combinación
	 * de teclas para cada una de las acciones.
	 */
	private void setKeyStrokes() {
		openAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
		openAction.putValue(Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke("control O"));
		saveAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
		saveAction.putValue(Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke("control S"));
		saveAsAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
		saveAsAction.putValue(Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke("control shift S"));
	}

	/**
	 * Coloca los componentes en la ventana. En este método se define como se
	 * ordenan y colocan los componentes para representarse en la pantalla.
	 */
	private void placeComponents() {
		// Configurar la ventana
		window.setTitle(app_name);
		window.setSize(600, 600);
		window.setResizable(true);
		container.setLayout(new BorderLayout());

		// Barra de menú (desplegables)
		fileMenu.add(openAction);
		fileMenu.add(saveAction);
		fileMenu.add(saveAsAction);
		fileMenu.add(exitAction);
		editMenu.add(undoAction);
		editMenu.add(redoAction);
		editMenu.add(fontChooseAction);
		formatMenu.add(fontChooseAction);
		// Barra de menu
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(formatMenu);
		//menuBar.add(helpMenu);
		window.setJMenuBar(menuBar);

		// Editor
		container.add(scrollEditor, BorderLayout.CENTER);

		// Panel de abajo
		southPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		pathLabel.setBorder(new EmptyBorder(0, 3, 0, 0));
		southPanel.add(pathLabel);
		southPanel.add(fileLabel);
		container.add(southPanel, BorderLayout.SOUTH);
	}

	/**
	 * Método que obtiene la ruta absoluta del archivo editingFile y lo muestra a
	 * través de los dos labels inferiores. Este método muestra el archivo que se
	 * está editando, con lo que ha de ejecutarse cada vez que se elija un nuevo
	 * archivo a editar.
	 */
	private void refreshWindow() {
		String route = editingFile.getAbsolutePath();
		String fileName = "";

		for (int i = route.length() - 1; i > 0; i--) {
			if (route.charAt(i) == '/') {
				i++;
				fileName = route.substring(i);
				route = route.substring(0, i);
				break;
			}
		}

		directoryFile = new File(route);
		pathLabel.setText(route);
		fileLabel.setText(fileName);
		window.setTitle(editingFile.getName() + " — " + app_name);
	}

	/**
	 * Crea un ActionListener en cada uno de los objetos que pueden ser clicados. A
	 * este ActionListener le asociamos una accion pasándosela como argumento.
	 * Cuando el componente sea clicado se ejecutará el método actionPerformed de la
	 * acción.
	 */
	public void registerEvents() {

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		southPanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseEntered(MouseEvent arg0) {
				southPanel.setBackground(SystemColor.control);
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (editingFile != null) {
					openFileExplorer();
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				southPanel.setBackground(UIManager.getColor("Panel.background"));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		});
	}

	/**
	 * API desktop de java para abrir el explorador de archivos.
	 */
	private final Desktop desktop = Desktop.getDesktop();

	/**
	 * Método que abre un archivo en el explorador de archivos.
	 */
	private void openFileExplorer() {
		try {
			desktop.open(directoryFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método main. Tan solo llamará al constructor de nuestra clase ventana.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new MainWindow();
	}
}
