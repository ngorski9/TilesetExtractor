package main;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

public class Start {

	protected Shell shell;
	private Text tileWidthText;
	private Text tileHeightText;
	private Text setWidthText;

	private Label filePathLabel;
	private Label backgroundPathLabel;

	private Button fileOpenButton;
	private Button viewImageButton;
	private Button viewBackgroundButton;
	private Button saveTilesetButton;
	private Button previewTilesetButton;
	private Button activateButton;
	
	private Image rawInputImage;
	private Image inputImage;
	private Image backgroundImage;
	private Image outputImage;
	
	private Display display;
		
	// what stage in the progam's execution cycle we are:
	// 0 - nothing loaded
	// 1 - input image selected, but tileset not created
	// 2 - tileset created
	private int stage = 0;
	private boolean has_background = false;
	
	JFileChooser chooser = new JFileChooser();
	ImageViewer iv;
	ErrorPopup ep;
	OverwriteMessage om;
	private Button openBackgroundButton;
	
	// launch the application
	public static void main(String[] args) {
		try {
			Start window = new Start();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// open the window and set properties of various widgets
	public void open() {
		display = Display.getDefault();
		createContents();
		chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		
		// create unopened instances of the various other windows used by the application
		iv = new ImageViewer(shell, SWT.APPLICATION_MODAL | SWT.TITLE | SWT.BORDER | SWT.CLOSE);
		ep = new ErrorPopup(shell, SWT.APPLICATION_MODAL | SWT.TITLE | SWT.BORDER);
		om = new OverwriteMessage(shell, SWT.APPLICATION_MODAL | SWT.TITLE | SWT.BORDER);
		
		// run the window
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public File openImage() {
		shell.setVisible(false);
		chooser.setDialogTitle("Open image");
		int result = chooser.showOpenDialog(null);
		shell.setVisible(true);
		if (result == JFileChooser.APPROVE_OPTION) {
		    return chooser.getSelectedFile();
		} 
		else {
			return null;
		}
	}
	
	// This function is called when the open background button is pressed
	// it loads the background file, and then
	public void onBackgroundOpenButtonPress() {
		File inputFile = openImage();
		if (inputFile != null) {

		    try {
				backgroundImage = new Image(display, inputFile.getPath());
		    }
		    catch(Exception e) {
		    	ep.open("ERROR: invalid image", "", "");
		    	return;
		    }
		    
		    String path = inputFile.getPath();
		    path = abbreviateFilePath(path);
		    backgroundPathLabel.setText(path);
		    
		    viewBackgroundButton.setEnabled(true);
		    processBackground();
		    has_background = true;
		}
	}
	
	// This function is called when the open image button is pressed
	public void onFileOpenButtonPress() {
		File inputFile = openImage();
		if (inputFile != null) {

		    try {
				rawInputImage = new Image(display, inputFile.getPath());
		    }
		    catch(Exception e) {
		    	ep.open("ERROR: invalid image", "", "");
		    	return;
		    }
		    
		    inputImage = rawInputImage;
		    
		    String path = inputFile.getPath();
		    path = abbreviateFilePath(path);
		    filePathLabel.setText(path);
		    
		    //update the interface to reflect the new stage:
		    if(stage == 2) {
				saveTilesetButton.setEnabled(false);
				previewTilesetButton.setEnabled(false);
				activateButton.setEnabled(false);
		    }
		    
		    if(has_background) {
		    	processBackground();
		    }
		    
		    tileWidthText.setEnabled(true);
		    tileHeightText.setEnabled(true);
		    setWidthText.setEnabled(true);
		    viewImageButton.setEnabled(true);
		    activateButton.setEnabled(true);
		    openBackgroundButton.setEnabled(true);
		    stage = 1;
		}
	}

	// add ellipses to the filepath to only show the root folder and the final file name
	public String abbreviateFilePath(String path) {
		char[] pathArray = path.toCharArray();
	    boolean found = false;
	    for(int i = pathArray.length - 1; i > 0; i--) {
	    	if (pathArray[i] == '/'){
	    		if(!found) {
	    			found = true;
	    		}
	    		else {
	    			path = "/.../" + path.substring(i+1);
	    			break;
	    		}
	    	}
	    }
	    return path;
	}
	
	// read in all of the background colors, then replace all background colors
	// present in the original image with one given color. This a tile which has
	// a transparent background from being picked up multiple times
	public void processBackground() {
		ArrayList<Integer> backgroundColors = new ArrayList<Integer>();
		ImageData backgroundData = backgroundImage.getImageData();
		for(int i = 0; i < backgroundData.width; i++) {
			for(int j = 0; j < backgroundData.height; j++) {
				int color = backgroundData.getPixel(i, j);
				if(!backgroundColors.contains(color)) {
					backgroundColors.add(color);
				}
			}
		}
		
		ImageData inData = rawInputImage.getImageData();
		int front = backgroundColors.get(0);
		for(int i = 0; i < inData.width; i++) {
			for(int j = 0; j < inData.height; j++) {
				int color = inData.getPixel(i, j);
				if(backgroundColors.contains(color)) {
					inData.setPixel(i,j,front);
				}
			}
		}
		inputImage = new Image(display, inData);
	}
	
	public void onActivateButtonPress() {
		
		int tileWidth = fetchPositiveIntFromText(tileWidthText, "tile width");
		int tileHeight = fetchPositiveIntFromText(tileHeightText, "tile height");
		int setWidth = fetchPositiveIntFromText(setWidthText, "tileset width");
		if( tileWidth < 0 || tileHeight < 0 || setWidth < 0 ) {
			return;
		}
		
		// derive image data as desired
		ImageData inData = inputImage.getImageData();
		ImageData outData = new ImageData(tileWidth * setWidth, tileHeight * 1000, inData.depth, inData.palette);
		
		int inputWidth = inData.width / tileWidth;
		int inputHeight = inData.height / tileHeight;
		
		int tileCount = 0;
		boolean match = false;
		
		// scroll through the input tileset
		for(int inTileY = 0; inTileY < inputHeight; inTileY++) {
			for(int inTileX = 0; inTileX < inputWidth; inTileX++) {
				
				// scroll through the tiles in the output set
				for(int outTilePos = 0; outTilePos < tileCount; outTilePos++) {
					
					match = true;
					
					// check for matching pixels
					for(int pixelY = 0; pixelY < tileHeight; pixelY++) {
						for(int pixelX = 0; pixelX < tileWidth; pixelX++) {
							if( inData.getPixel(inTileX * tileWidth + pixelX, inTileY * tileHeight + pixelY)
							 != outData.getPixel((outTilePos % setWidth) * tileWidth + pixelX, (outTilePos / setWidth) * tileHeight + pixelY)) {
								match = false;
								break;
							}
						}
						if(!match) {
							break;
						}
					}
					
					if(match) {
						break;
					}
					
				}
				
				// if there is not a match, write the new tile to the set
				if(!match) {
					//scroll through pixels:
					for(int pixelY = 0; pixelY < tileHeight; pixelY++) {
						for(int pixelX = 0; pixelX < tileWidth; pixelX++) {
							outData.setPixel( (tileCount % setWidth) * tileWidth + pixelX, (tileCount / setWidth) * tileHeight + pixelY, 
									          inData.getPixel(inTileX * tileWidth + pixelX, inTileY * tileHeight + pixelY));
						}
					}
					tileCount++;
				}
				
			}
		}
		
		outData.height = (tileCount / setWidth) * tileHeight;	
		outputImage = new Image(display, outData);
		iv.open(new Image(display, outData));
		stage = 2;
		previewTilesetButton.setEnabled(true);
		saveTilesetButton.setEnabled(true);
	}
	
	public int fetchPositiveIntFromText(Text text, String name) {
		try {
			int ret = Integer.parseInt(text.getText());
			if(ret <= 0) {
				ep.open("ERROR: " + name + " must be a", "positive integer", "");
				return -1;
			}
			return ret;
		}
		catch(Exception e) {
			ep.open("ERROR: " + name + " must be a", "positive integer", "");
			return -1;
		}
	}
	
	public void onViewImageButtonPress() {
		iv.open(inputImage);
	}
	
	public void onViewBackgroundButtonPress() {
		iv.open(backgroundImage);
	}
	
	public void onPreviewTilesetButtonPress() {
		iv.open(outputImage);
	}
	
	public void onSaveTilesetButtonPress() {
		chooser.setDialogTitle("Save file");
		int out = chooser.showSaveDialog(null);
		if ( out == JFileChooser.APPROVE_OPTION ) {
			File f = chooser.getSelectedFile();
			if(f.exists() && !om.open(abbreviateFilePath(f.getPath()))) {
				System.out.println("don't save");
				return;
			}
			
			ImageLoader saver = new ImageLoader();
			saver.data = new ImageData[] { outputImage.getImageData() };
			saver.save(f.getAbsolutePath(), SWT.IMAGE_PNG);
		}
	}
	
	/**
	 * Create contents of the window. This is all machine generated.
	 */
	protected void createContents() {
		
		shell = new Shell();
		shell.setSize(429, 247);
		shell.setText("Tileset Splitter");
		shell.setLayout(null);
		
		fileOpenButton = new Button(shell, SWT.NONE);
		fileOpenButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				onFileOpenButtonPress();
			}
		});
		
		fileOpenButton.setBounds(10, 10, 93, 29);
		fileOpenButton.setText("Open image");
		
		filePathLabel = new Label(shell, SWT.NONE);
		filePathLabel.setBounds(109, 16, 205, 17);
		
		tileWidthText = new Text(shell, SWT.BORDER);
		tileWidthText.setText("16");
		tileWidthText.setEnabled(false);
		tileWidthText.setBounds(119, 102, 81, 29);
		
		tileHeightText = new Text(shell, SWT.BORDER);
		tileHeightText.setText("16");
		tileHeightText.setEnabled(false);
		tileHeightText.setBounds(332, 102, 81, 29);
		
		setWidthText = new Text(shell, SWT.BORDER);
		setWidthText.setText("16");
		setWidthText.setEnabled(false);
		setWidthText.setBounds(119, 137, 81, 29);
		
		activateButton = new Button(shell, SWT.NONE);
		activateButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				onActivateButtonPress();
			}
		});
		activateButton.setEnabled(false);
		activateButton.setBounds(10, 178, 93, 29);
		activateButton.setText("Go!");
		
		Label tileWidthLabel = new Label(shell, SWT.NONE);
		tileWidthLabel.setBounds(16, 107, 70, 17);
		tileWidthLabel.setText("Tile Width");
		
		Label tileHeightLabel = new Label(shell, SWT.NONE);
		tileHeightLabel.setBounds(245, 107, 70, 17);
		tileHeightLabel.setText("Tile Height");
		
		Label tilesetWidthLabel2 = new Label(shell, SWT.NONE);
		tilesetWidthLabel2.setBounds(16, 155, 81, 17);
		tilesetWidthLabel2.setText("(in tiles)");
		
		Label tilesetWidthLabel1 = new Label(shell, SWT.NONE);
		tilesetWidthLabel1.setText("Tileset Width");
		tilesetWidthLabel1.setBounds(16, 137, 100, 17);
		
		viewImageButton = new Button(shell, SWT.NONE);
		viewImageButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				onViewImageButtonPress();
			}
		});
		viewImageButton.setEnabled(false);
		viewImageButton.setBounds(320, 10, 93, 29);
		viewImageButton.setText("View Image");
		
		saveTilesetButton = new Button(shell, SWT.NONE);
		saveTilesetButton.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				onSaveTilesetButtonPress();
			}
		});
		saveTilesetButton.setEnabled(false);
		saveTilesetButton.setBounds(320, 178, 93, 29);
		saveTilesetButton.setText("Save Tileset");
		
		previewTilesetButton = new Button(shell, SWT.NONE);
		previewTilesetButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				onPreviewTilesetButtonPress();
			}
		});
		previewTilesetButton.setEnabled(false);
		previewTilesetButton.setBounds(158, 178, 115, 29);
		previewTilesetButton.setText("Preview Tileset");

		openBackgroundButton = new Button(shell, SWT.NONE);
		openBackgroundButton.setEnabled(false);
		openBackgroundButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				onBackgroundOpenButtonPress();
			}
		});
		openBackgroundButton.setBounds(10, 56, 93, 29);
		openBackgroundButton.setText("open bg");
		
		viewBackgroundButton = new Button(shell, SWT.NONE);
		viewBackgroundButton.setEnabled(false);
		viewBackgroundButton.setBounds(320, 56, 93, 29);
		viewBackgroundButton.setText("view bg");
		viewBackgroundButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				onViewBackgroundButtonPress();
			}
		});
		
		backgroundPathLabel = new Label(shell, SWT.NONE);
		backgroundPathLabel.setBounds(109, 62, 205, 17);
		
	}
}
