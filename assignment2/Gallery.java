package pa2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.restfb.BinaryAttachment;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.json.JsonObject;

import pa2.ImageFileList;
import pa2.SwingBrowser;



public class Gallery {
	// specify the path that contains images
	private static final String IMAGE_PATH = "./images/";

	// indicate the index of the image that is displayed
	private static int imageIndex = -1;
	// name of the image files under `IMAGE_PATH`
	private static List<String> imageFiles = new ArrayList<String> ();

	// FB-related variable
	private static String userToken = null;

	// GUI-components
	private static JButton loadButton;
	private static JButton resetButton;
	private static JButton leftButton;
	private static JButton rightButton;
	private static JButton shareButton;
	private static JButton processButton;
	private static JButton connectButton;
	private static JFrame frame;
	private static JLabel label;

	// layout-related variables
	final static boolean RIGHT_TO_LEFT = false;

	// getters
	public JFrame getFrame() {
		return frame;
	}

	public List<String> getImageFiles(){
		return imageFiles;
	}

	public static void readImagesFromPath() {

		// TODO

		// clear 'imageFiles'
		imageFiles.clear();

		// Create a `File` object with the specified path name `IMAGE_PATH`
		File folder = new File(IMAGE_PATH);

		// Use the `listFiles()` method to obtain an array of `File` objects
		File[] fileList = folder.listFiles();

		// Assign the image filenames to `imageFiles`
		String[] fileName = new String[fileList.length];
		for(int i=0; i < fileList.length; i++){

			// get the name of the file on the fileList
			fileName[i] = fileList[i].getName();

			// add the name to imageFiles
			imageFiles.add(fileName[i]);

		}
	}

	public static String getImageSuffix(String imageName) {

		// TODO

		// Split the name of the file by the dot sign 
		String[] parts = imageName.split("\\.");

		// get the image type (suffix after the last dot) and put it into imageType
		String imageType = parts[parts.length - 1];

		return imageType;

	}

	public static BaseImage createImageObj(String imageName) {

		//TODO

		// get the image type with getImageSuffix(String imageName) method
		String imageType = getImageSuffix(imageName);

		// create a BaseImage object under curImage
		BaseImage curImage = null;

		// assign curImage to the appropriate object according to the type of the image
		if(imageType.equals("png"))
			curImage = new PNGImage(IMAGE_PATH + imageName);
		else if (imageType.equals("jpg"))
			curImage = new JPEGImage(IMAGE_PATH + imageName);

		return curImage;
	}

	public static void addComponentsToPane(Container pane) {
		if (RIGHT_TO_LEFT) {
			pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// first row
		loadButton = new JButton("Load");
		loadButton.setName("loadButton");
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 1;
		c.gridy = 0;
		pane.add(loadButton, c);

		resetButton = new JButton("Reset");
		resetButton.setName("resetButton");
		c.gridx = 4;
		c.gridy = 0;
		pane.add(resetButton, c);

		// display list on the left
		c.gridx = 0;
		c.gridy = 1;
		ImageFileList imageFileList = new ImageFileList(imageFiles);
		imageFileList.setName("imageFileList");
		imageFileList.setPreferredSize(new Dimension(100,100));
		pane.add(imageFileList, c);

		// display image in the middle
		leftButton = new JButton("L");
		leftButton.setName("leftButton");
		leftButton.setEnabled(false);
		c.gridx = 1;
		c.gridy = 2;
		pane.add(leftButton, c);

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;       //aligned with button 2
		c.gridwidth = 2;   //2 columns wide
		c.gridy = 1;       //third row
		c.ipadx = 25;
		c.ipady = 25;
		ImageIcon icon = new ImageIcon("");
		label = new JLabel(icon);
		label.setName("label");
		label.setPreferredSize(new Dimension(400, 300));
		pane.add(label, c);

		// reset specs
		c.fill = GridBagConstraints.NONE;
		c.ipadx = 0;
		c.ipady = 0;
		rightButton = new JButton("R");
		rightButton.setName("rightButton");
		rightButton.setEnabled(false);
		c.gridx = 4;
		c.gridy = 2;
		pane.add(rightButton, c);

		// display buttons on the right
		shareButton = new JButton("Share FB");
		shareButton.setName("shareButton");
		shareButton.setEnabled(false);
		c.gridx = 5;
		c.gridy = 4;
		pane.add(shareButton, c);

		connectButton = new JButton("Connect FB");
		connectButton.setName("connectButton");
		c.gridx = 5;
		c.gridy = 5;
		pane.add(connectButton, c);

		processButton = new JButton("Process");
		processButton.setName("processButton");
		processButton.setEnabled(false);
		c.gridx = 5;
		c.gridy = 1;
		pane.add(processButton, c);

		// action listeners
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				readImagesFromPath();
				// update List component
				imageFileList.updateListModel(imageFiles);
				if(imageFiles.size() > 0) {
					imageIndex = 0;
					label.setIcon(new ImageIcon(IMAGE_PATH + imageFiles.get(imageIndex)));
					rightButton.setEnabled(true);
					processButton.setEnabled(true);
				}
			}
		});

		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				imageFiles.clear();
				imageIndex = -1;
				imageFileList.updateListModel(imageFiles);
				label.setIcon(new ImageIcon(""));
				leftButton.setEnabled(false);
				rightButton.setEnabled(false);
				processButton.setEnabled(false);
				shareButton.setEnabled(false);
			}
		});

		leftButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				//TODO

				// left button and right button are always enabled (unless the below exceptions)
				leftButton.setEnabled(true);
				rightButton.setEnabled(true);
				//left button is disabled when image index == 0
				if (imageIndex == 0)
					leftButton.setEnabled(false);
				// right button is disabled when image index == imageFiles.size()-1
				if (imageIndex == imageFiles.size() - 1)
					rightButton.setEnabled(false);

				if (imageIndex > 0){

					// decrease image index by 1
					imageIndex = imageIndex - 1;

					// update the selected index of imageFileList with the updated image index 
					imageFileList.setListSelectedIndex(imageIndex);

					// update the icon that is displayed
					label.setIcon(new ImageIcon(IMAGE_PATH + imageFiles.get(imageIndex)));

				}
			}
		});

		rightButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				//TODO

				// left button and right button are always enabled (unless the below exceptions)				
				leftButton.setEnabled(true);
				rightButton.setEnabled(true);
				//left button is disabled when image index == 0
				if (imageIndex == 0)
					leftButton.setEnabled(false);
				// right button is disabled when image index == imageFiles.size()-1
				if (imageIndex == imageFiles.size() - 1)
					rightButton.setEnabled(false);

				if (imageIndex < imageFiles.size()){

					// increase image index by 1
					imageIndex = imageIndex + 1;

					// update the selected index of imageFileList with the updated image index
					imageFileList.setListSelectedIndex(imageIndex);

					// update the icon that is displayed
					label.setIcon(new ImageIcon(IMAGE_PATH + imageFiles.get(imageIndex)));

				}

			}
		});

		shareButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				/* TODO (Bonus)
				 * NO IMPLEMENTATION
				 * 
				 * Share the image displayed on the screen to Facebook
				 * 
				 * This task can be done in 3 steps: 
				 *	1. Convert `icon` to a byte Array. Get the icon and convert it to BufferedImage first, 
				 *	and then convert the BufferedImage to a byte Array. 
				 *	// Get the image that is currently displayed on the screen 
				 *	Icon icon = label.getIcon();
				 *	// Convert `icon` from `Icon` type to `BufferedImage` type
				 *	// Convert `BufferedImage` to a `byte` Array
				 *
				 *	2. Create a `FacebookClient` object
				 *  // Create a `FacebookClient` with the `userToken`
				 *  
				 *	3. Publish the image to Facebook with `publish` method 
				 *	// Publish the image as a `byte` Array to Facebook with the `FacebookClient` object and the `publish` method. 
				 *	// A String `shareMsg` is added together with the photo. 
				 * 
				 */

				String imageName = imageFiles.get(imageIndex);
				String imageType = getImageSuffix(imageName);
				String shareMsg = "share "+imageType+" image";

				Icon icon = label.getIcon();

			}

		});

		processButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String imageName = imageFiles.get(imageIndex);
				Object[] possibilities = {"contrast enhancement", "gradient image", 
						"edge filtering (H)", "edge filtering (V)"};
				String imageProcessOption = (String)JOptionPane.showInputDialog( pane,
						"Choose process option:\n",
						"Image processing options",
						JOptionPane.PLAIN_MESSAGE,
						icon,
						possibilities,
						"contrast enhancement");

				// TODO

				BaseImage curImage = createImageObj(imageName);

				if (imageProcessOption == null){
					// the image is not processed
				}
				else {
					// Call image processing methods based on the value `imageProcessOption`
					if (imageProcessOption.equals("contrast enhancement"))
						curImage.contrastEnhancement();
					if (imageProcessOption.equals(possibilities[1]))
						BaseImage.gradient();
					if (imageProcessOption.equals(possibilities[2]))
						BaseImage.edgeFiltering(0);
					if (imageProcessOption.equals(possibilities[3]))
						BaseImage.edgeFiltering(1);
					//the image shown is the processed image based on the method selected
					label.setIcon(new ImageIcon(BaseImage.getProcessedImage()));
				}
			}

		});

		imageFileList.getJList().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				System.out.println("list selected index "+imageFileList.getJList().getSelectedIndex());
				if(e.getValueIsAdjusting() == false) {
					if(!imageFiles.isEmpty() && imageFileList.getJList().getSelectedIndex() >= 0) {
						imageIndex = imageFileList.getJList().getSelectedIndex();
						label.setIcon(new ImageIcon(IMAGE_PATH + imageFiles.get(imageIndex)));

						// TODO

						// left button and right button are always enabled (unless the below exceptions)				
						leftButton.setEnabled(true);
						rightButton.setEnabled(true);
						//left button is disabled when image index == 0
						if (imageIndex == 0)
							leftButton.setEnabled(false);
						// right button is disabled when image index == imageFiles.size()-1
						if (imageIndex == imageFiles.size() - 1)
							rightButton.setEnabled(false);
					}
				}
			}
		});

		connectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// connect to facebook
				System.out.println("connect to facebook");

				SwingBrowser browser = new SwingBrowser();
				browser.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				browser.setVisible(true);

				SwingWorker<String, String> worker = new SwingWorker<String, String>()
						{
					@Override
					protected String doInBackground() throws Exception
					{
						//do something useful in the background thread
						browser.fetchAccessToken();
						while(true) {
							if(browser.getUserToken() != null) {
								userToken = browser.getUserToken();
								System.out.println("user token in Gallery2: "+userToken);
								shareButton.setEnabled(true);
								break;
							}
						}
						return userToken;
					}

					@Override
					protected void done()
					{
						// userToken = browser.getUserToken();
					}
						};
						worker.execute();
			}
		});

	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	public Gallery() {
		//Create and set up the window.
		frame = new JFrame("LiteGallery");
		frame.setName("main frame");
		frame.setSize(900, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Set up the content pane.
		addComponentsToPane(frame.getContentPane());

		//Display the window.
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Gallery g = new Gallery();
			}
		});
	}

}