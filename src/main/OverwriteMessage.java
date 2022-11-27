package main;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class OverwriteMessage extends Dialog {

	protected Shell shell;

	private Label fileNameLabel;
	private boolean overwriteFile = false;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public OverwriteMessage(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public boolean open(String filename) {
		createContents(filename);
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return overwriteFile;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents(String filename) {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(335, 152);
		shell.setText(getText());
		
		fileNameLabel = new Label(shell, SWT.NONE);
		fileNameLabel.setAlignment(SWT.CENTER);
		fileNameLabel.setBounds(10, 10, 310, 17);
		fileNameLabel.setText(filename);
		
		Label existsLabel = new Label(shell, SWT.NONE);
		existsLabel.setAlignment(SWT.CENTER);
		existsLabel.setBounds(10, 33, 310, 17);
		existsLabel.setText("already exists. Overwrite?");
		
		Button yesButton = new Button(shell, SWT.NONE);
		yesButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				overwriteFile = true;
				shell.close();
			}
		});
		yesButton.setBounds(57, 67, 93, 29);
		yesButton.setText("Yes");
		
		Button noButton = new Button(shell, SWT.NONE);
		noButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
			overwriteFile = false;
			shell.close();
			}
		});
		noButton.setBounds(182, 67, 93, 29);
		noButton.setText("No");

	}
}
