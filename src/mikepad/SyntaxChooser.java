package mikepad;

import java.lang.reflect.Field;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/**
 * OptionDialog que permite escoger entre todos los tipos de coloreado de
 * syntaxis disponibles en RSyntaxTextArea.
 * 
 * @author Miguel Bautista Pérez
 */
public class SyntaxChooser extends OptionDialog {

	private RSyntaxTextArea editor;
	private static SyntaxConstants scObject;
	private static Field[] scFields;

	static {
		scObject = new SyntaxConstants() {
		};
		scFields = scObject.getClass().getFields();
	}

	public SyntaxChooser(RSyntaxTextArea editor) {
		this((JFrame) SwingUtilities.getRoot(editor));
		this.window.setTitle("Elije un lenguaje");
		this.editor = editor;
	}

	public SyntaxChooser(JFrame parent) {
		super(parent);
	}

	@Override
	public void loadContent() {
		listModel.clear();

		for (Field f : scFields) {
			if (f.getType().equals(String.class)) {

				try {
					String mime = (String) f.get(scObject);
					String lang = mime.substring(1 + mime.lastIndexOf("/"));
					String name = f.getName().substring(1 + f.getName().lastIndexOf("_"));
					String input = textField.getText().toLowerCase();

					if (lang.equals("cpp")) {
						lang = "c++";
					}

					if (lang.contains(input) || name.toLowerCase().contains(input)) {
						String entry = formatSyntaxNames(lang);
						listModel.addElement(entry);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String formatSyntaxNames(String lang) {
		return lang.substring(0, 1).toUpperCase() + lang.substring(1);
	}

	@Override
	public void selectionPerformed(String selectedItem) {
		String selectedMimeType = "text/" + selectedItem.toLowerCase();
		editor.setSyntaxEditingStyle(selectedMimeType);
		close();
	}

	/**
	 * Obtiene el lenguaje de programación a partir de un mimeType.
	 * 
	 * @param mimeType
	 * @return programming language
	 */
	private static String getLangFromMime(String mime) {
		String lang = mime;
		if (mime.contains("/x-")) {
			lang = mime.substring(3 + mime.lastIndexOf("/x-"));
		} else if (mime.contains("/")) {
			lang = mime.substring(1 + mime.lastIndexOf("/"));
		}

		if (lang.equals("sh")) {
			lang = "unix";
		}
		return lang;
	}

	/**
	 * Obtiene un identificador de sintaxis válido a partir de un mimeType. En caso
	 * de no compatibilidad devuelve "text/plain".
	 * 
	 * @param mimeType (puede ser o no válido)
	 * @return mimeType (válido)
	 */
	public static String getSyntax(String mime) {
		if (mime != null) {
			String selectedLang = getLangFromMime(mime);

			for (Field f : scFields) {
				if (f.getType().equals(String.class)) {

					try {
						String mimeType = (String) f.get(scObject);
						String lang = mimeType.substring(1 + mimeType.lastIndexOf("/"));

						String name = (f.getName().substring(1 + f.getName().lastIndexOf("_"))).toLowerCase();

						if (lang.equals(selectedLang) || name.equals(selectedLang)) {
							return mimeType;
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return "text/plain";
	}

	// Solo para pruebas.
	public static void main(String[] args) {
		new SyntaxChooser(new RSyntaxTextArea());
	}

}
