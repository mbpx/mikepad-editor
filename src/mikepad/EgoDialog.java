package mikepad;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class EgoDialog {
	private JDialog dialog;
	private JPanel panel;
	private JLabel link;

	public EgoDialog(JFrame window) {
		dialog = new JDialog(window, "Acerca de Mikepad");
		dialog.setResizable(false);
		panel = (JPanel) dialog.getContentPane();
		panel.setBorder(new EmptyBorder(3, 3, 3, 3));
		panel.setLayout(new GridLayout(0, 1));

		/*
		 * GridBagConstraints gbc = new GridBagConstraints(); gbc.gridwidth =
		 * GridBagConstraints.REMAINDER; gbc.fill = GridBagConstraints.HORIZONTAL;
		 */

		panel.add(new JLabel("Hecho por Miguel Bautista Pérez"));
		panel.add(new JLabel("Editor de código hecho en Java Swing"));
		panel.add(new JLabel("Basado en RSyntaxTextArea"));
		panel.add(new JSeparator(SwingConstants.HORIZONTAL));
		link = new JLabel(getStyledLink("Visitar repo de github"));
		panel.add(link);
		createActions();
		dialog.pack();
		dialog.setLocationRelativeTo(window);
		dialog.setVisible(true);
	}

	private static String getStyledLink(String text) {
		return "<html><a href=''>" + text + "</a></html>";
	}

	private void createActions() {
		link.setCursor(new Cursor(Cursor.HAND_CURSOR));

		link.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("https://github.com/admorsus"));
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}
