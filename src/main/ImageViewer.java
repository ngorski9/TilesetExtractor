package main;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.PaintEvent;

public class ImageViewer extends Dialog {

	protected Object result;
	protected Shell shell;
	
	private Image image;
	private Slider xSlider;
	private Slider ySlider;
	private Canvas canvas;
	
	private final int scrollMultiplier = 4;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ImageViewer(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}
	
	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(Image i) {
		createContents();
		initialize(i);
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

	private void initialize(Image i) {
		image = i;
		int imageWidth = i.getBounds().width;
		int imageHeight = i.getBounds().height;
		int spaceWidth = canvas.getBounds().width;
		int spaceHeight = canvas.getBounds().height;
		
		if(imageWidth > spaceWidth) {
			xSlider.setMinimum(0);
			xSlider.setMaximum((imageWidth - spaceWidth) / scrollMultiplier + xSlider.getThumb());
			xSlider.setIncrement(1);
			xSlider.setEnabled(true);
		}
		else {
			xSlider.setVisible(false);
		}
		
		if(imageHeight > spaceHeight) {
			ySlider.setMinimum(0);
			ySlider.setMaximum((imageHeight - spaceHeight) / scrollMultiplier + ySlider.getThumb());
			ySlider.setIncrement(1);
			ySlider.setEnabled(true);
		}
		else {
			ySlider.setVisible(false);
		}
	}
	
	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 284);
		shell.setText(getText());
		
		Button closeButton = new Button(shell, SWT.NONE);
		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				shell.close();
			}
		});
		closeButton.setBounds(342, 217, 93, 29);
		closeButton.setText("OK");
		
		xSlider = new Slider(shell, SWT.NONE);
		xSlider.setThumb(0);
		xSlider.setBounds(10, 198, 408, 12);
		xSlider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				canvas.redraw();
			}
		});
		
		ySlider = new Slider(shell, SWT.VERTICAL);
		ySlider.setThumb(0);
		ySlider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				canvas.redraw();
			}
		});
		ySlider.setBounds(416, 10, 19, 185);
		
		canvas = new Canvas(shell, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(image, -xSlider.getSelection() * scrollMultiplier, -ySlider.getSelection() * scrollMultiplier);
			}
		});
		canvas.setBounds(10, 10, 408, 182);

	}
}
