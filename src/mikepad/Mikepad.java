package mikepad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * Editor de código.
 * 
 * @author Miguel Bautista Pérez
 */
public class Mikepad {

	// Ventana
	private JFrame window;
	private Container container;

	// Editor
	private EditingFile editingFile;
	private RSyntaxTextArea editor;
	private RTextScrollPane scrollEditor;

	// Acciones
	private Action openAction;
	private Action saveAction;
	private Action saveAsAction;
	private Action runAction;

	// Barra de menú
	private JMenuBar menuBar;
	private JMenu fileMenu = new JMenu("Archivo");
	private JMenu editMenu = new JMenu("Editar");
	private JMenu formatMenu = new JMenu("Formato");
	private JMenu runMenu = new JMenu("Ejecutar");

	// Panel inferior
	private JPanel southPanel;
	private JLabel fileLabel;
	private JLabel pathLabel;

	// Selectores
	private final JFileChooser fileChooser = new JFileChooser();
	private final Desktop desktop = Desktop.getDesktop();

	// Constantes de la aplicación
	private final String app_name = "Mikepad";
	private final String run_script_path = "src/scripts/RunScript.sh";

	// Constructor.
	public Mikepad() {

		createComponents();
		createActions();
		placeComponents();
		window.setVisible(true);
		registerEvents();

	}

	/**
	 * Crea los objetos que serán representados como componentes de la app.
	 */
	private void createComponents() {
		// Crear la ventana
		window = new JFrame();
		container = window.getContentPane();

		// Editor
		editor = new RSyntaxTextArea();
		scrollEditor = new RTextScrollPane(editor);
		editor.setCodeFoldingEnabled(true);
		menuBar = new JMenuBar();
		menuBar.setBorderPainted(false);

		// Panel de abajo
		southPanel = new JPanel();
		pathLabel = new JLabel("nuevo documento");
		pathLabel.setForeground(Color.gray);
		fileLabel = new JLabel();
		fileLabel.setForeground(Color.darkGray);

	}

	private void setKeyStroke(Action action, String keyStroke) {
		action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(keyStroke));
	}

	/**
	 * Crea las acciones, que son objetos cuyo contenido es simplemente una función,
	 * que será ejecutada cuando se active la acción de algún modo.
	 */
	@SuppressWarnings("serial")
	private void createActions() {

		// File menu
		openAction = new AbstractAction("Abrir") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int retVal = fileChooser.showOpenDialog(window);
				if (retVal == JFileChooser.APPROVE_OPTION) {
					editingFile = new EditingFile(fileChooser.getSelectedFile());
					try {
						FileReader reader = new FileReader(editingFile.getFile());
						editor.read(reader, "");
						reader.close();
					} catch (IOException ioex) {
						ioex.printStackTrace();
					}
					refreshWindow();
				}
			}
		};
		setKeyStroke(openAction, "control O");
		fileMenu.add(openAction);

		saveAction = new AbstractAction("Guardar") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (editingFile != null) {
					try {
						FileWriter writer = new FileWriter(editingFile.getFile());
						editor.write(writer);
						writer.close();
					} catch (IOException ioex) {
						ioex.printStackTrace();
					}
				} else {
					saveAsAction.actionPerformed(null);
				}
			}
		};
		setKeyStroke(saveAction, "control S");
		fileMenu.add(saveAction);

		saveAsAction = new AbstractAction("Guardar como") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int retVal = fileChooser.showSaveDialog(window);
				if (retVal == JFileChooser.APPROVE_OPTION) {
					editingFile = new EditingFile(fileChooser.getSelectedFile());
					saveAction.actionPerformed(null);
					refreshWindow();
				}
			}
		};
		setKeyStroke(saveAsAction, "control shift S");
		fileMenu.add(saveAsAction);
		fileMenu.addSeparator();

		fileMenu.add(new AbstractAction("Acerca de") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new EgoDialog(window);
			}
		});

		fileMenu.add(new AbstractAction("Salir") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		// Edit menu
		editMenu.add(RSyntaxTextArea.getAction(RSyntaxTextArea.UNDO_ACTION));
		editMenu.add(RSyntaxTextArea.getAction(RSyntaxTextArea.REDO_ACTION));
		editMenu.addSeparator();
		editMenu.add(RSyntaxTextArea.getAction(RSyntaxTextArea.CUT_ACTION));
		editMenu.add(RSyntaxTextArea.getAction(RSyntaxTextArea.COPY_ACTION));
		editMenu.add(RSyntaxTextArea.getAction(RSyntaxTextArea.PASTE_ACTION));
		editMenu.add(RSyntaxTextArea.getAction(RSyntaxTextArea.DELETE_ACTION));
		editMenu.addSeparator();
		editMenu.add(RSyntaxTextArea.getAction(RSyntaxTextArea.SELECT_ALL_ACTION));

		menuBar.add(editor.getPopupMenu());

		// Format menu
		formatMenu.add(new AbstractAction("Tipo de letra") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				FontDialog.showDialog(editor);
			}
		});

		formatMenu.add(new AbstractAction("Resaltado de syntaxis") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				@SuppressWarnings("unused")
				SyntaxChooser syntaxChooser = new SyntaxChooser(editor);
			}
		});

		// Run Menu
		Action openTerminalAction = new AbstractAction("Mostrar en terminal") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					System.out.println(editingFile.getDirectory());
					ProcessBuilder builder = new ProcessBuilder("x-terminal-emulator");
					String path;
					if (editingFile != null) {
						path = editingFile.getDirectory();
					} else {
						path = System.getProperty("user.home");
					}
					builder.directory(new File(path));
					builder.start();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		setKeyStroke(openTerminalAction, "control T");
		runMenu.add(openTerminalAction);

		runAction = new AbstractAction("Ejecutar") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					PrintWriter writer = new PrintWriter(run_script_path, "UTF-8");
					writer.println("chmod +x " + editingFile.getPath());
					writer.println(editingFile.getPath());
					writer.println("read -rn1 && exit");
					writer.close();
					String[] args = new String[] { "x-terminal-emulator", "-e", "bash", "-rcfile", run_script_path };
					ProcessBuilder builder = new ProcessBuilder(args);
					builder.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		runAction.setEnabled(false);
		setKeyStroke(runAction, "control R");
		runMenu.add(runAction);
	}

	/**
	 * Método que se ejecuta al terminar las acciones de Abrir y Guardar como, cuya
	 * función es recofigurar algunos componentes de la UI.
	 */
	private void refreshWindow() {
		runAction.setEnabled((editingFile != null));
		String syntax = SyntaxChooser.getSyntax(editingFile.getMimeType());
		editor.setSyntaxEditingStyle(syntax);
		System.out.println(editingFile.getMimeType());
		System.out.println(syntax);
		pathLabel.setText(editingFile.getDirectory());
		fileLabel.setText(editingFile.getFileName());
		window.setTitle(editingFile.getFileName() + " — " + app_name);
	}

	/**
	 * Coloca los componentes en la ventana. En este método se define como se
	 * ordenan y colocan los componentes para representarse en la pantalla.
	 */
	private void placeComponents() {
		// Configurar la ventana
		window.setTitle(app_name);
		window.setSize(600, 600);
		window.setLocationRelativeTo(null);
		window.setResizable(true);

		// Barra de menu
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(formatMenu);
		menuBar.add(runMenu);
		window.setJMenuBar(menuBar);

		// Editor
		container.setLayout(new BorderLayout());
		container.add(scrollEditor, BorderLayout.CENTER);

		// Panel de abajo
		southPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		pathLabel.setBorder(new EmptyBorder(0, 3, 0, 0));
		southPanel.add(pathLabel);
		southPanel.add(fileLabel);
		container.add(southPanel, BorderLayout.SOUTH);
	}

	/**
	 * Creamos los escuchadores de los eventos que tendrá la app (los que no hayamos
	 * creado a través de acciones).
	 */
	public void registerEvents() {

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		southPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				southPanel.setBackground(SystemColor.control);
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				openFileExplorer();
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				southPanel.setBackground(UIManager.getColor("Panel.background"));
			}
		});
	}

	/**
	 * Método que abre un archivo en el explorador de archivos.
	 */
	private void openFileExplorer() {
		try {
			if (editingFile != null) {
				desktop.open(new File(editingFile.getDirectory()));
			}
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
		new Mikepad();
	}
}
