package main;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class ErrorPopup extends Dialog {

	protected Object result;
	protected Shell shell;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ErrorPopup(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(String first, String second, String third) {
		createContents(first, second, third);
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents(String first, String second, String third) {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(335, 152);
		shell.setText(getText());
		
		Label textRow1 = new Label(shell, SWT.NONE);
		textRow1.setAlignment(SWT.CENTER);
		textRow1.setBounds(20, 10, 300, 17);
		textRow1.setText(first);
		
		Label textRow2 = new Label(shell, SWT.NONE);
		textRow2.setAlignment(SWT.CENTER);
		textRow2.setBounds(20, 33, 300, 17);
		textRow2.setText(second);
		
		Label textRow3 = new Label(shell, SWT.NONE);
		textRow3.setAlignment(SWT.CENTER);
		textRow3.setBounds(20, 56, 300, 17);
		textRow3.setText(third);
		
		Button closeButton = new Button(shell, SWT.NONE);
		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				shell.close();
			}
		});
		closeButton.setBounds(106, 79, 93, 29);
		closeButton.setText("OK");

	}
}
