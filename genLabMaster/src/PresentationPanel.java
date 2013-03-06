import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.Color;
import java.io.*;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.colorchooser.*;
import javax.swing.BorderFactory;
import javax.swing.border.*;

import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

import java.net.URL;
//import javax.media.CannotRealizeException;
//import javax.media.Manager;
//import javax.media.NoPlayerException;
//import javax.media.Player;
//import javax.media.bean.playerbean.MediaPlayer;

import java.io.IOException;
import java.net.MalformedURLException;

class PresentationPanel extends JPanel {


//	boolean xhair, eotMsg, dot;
	int centerxpoint, centerypoint;
	boolean dot = false;
	boolean word = false;
	boolean picture = false;
	boolean video = false;
	String theWord = "";
	String thePicture = "";
	String fontFace;
	int fontSize;
	Vector thisTrial;

	int hVal = 0, vVal = 0, horiz = 0, vert = 0;
	
	Dimension d2;
	boolean leaveDisplayOn = false;

	//JPanel videoPan;

    //    MediaPlayer mediaPlayer, mediaPlayer1;
//        Player player, mediaPlayer2;
        Component videoComponent;
        vlcjPlayer videoPlayer;
        Vector videoPanVector;
        Vector mediaPlayerVector;
        Vector videoNameVector;
    //    Vector realizedPlayerVector;
        int videoPanCtr = 0;


	public PresentationPanel(){

		setBackground(Color.white);
		setForeground(Color.black);
		d2 = new Dimension(780, 450);
		setSize(d2);
		setPreferredSize(d2);
		setBorder(BorderFactory.createLineBorder(Color.black));
                setLayout(null);
		float paneWidth = (float)(getSize().getWidth());
		float paneHeight = (float)(getSize().getHeight());
		float centery = (paneHeight) / 2;
		centerypoint = Math.round(centery);
		float centerx = (paneWidth ) / 2;
		centerxpoint = Math.round(centerx);
		thisTrial = new Vector();
		this.setVisible(true);
	}

        public void prepVideo(String filename)
        {
    //        JPanel tempPan;
          //  int w = 117;
           // int h = 80;
         //   tempPan = new JPanel(new BorderLayout());

        //    tempPan.setBackground(Color.red);
       //     videoPanVector.addElement(tempPan);
       //     this.add((JPanel)videoPanVector.lastElement());
            URL mediaURL = null;
            try {
        //       mediaURL = new URL("file:/C:/Users/reberle/Documents/Fig21_06_07/Fig21_06_07/" + filename);
                mediaURL = new URL("file:/" + filename);
            }
            catch (MalformedURLException e) { }


//    try
          //  {
         // create a player to play the media specified in the URL

    //        mediaPlayer = new MediaPlayer();
            
      //      mediaPlayer.setMediaLocation(new java.lang.String("file:/" + filename));
             //  mediaPlayer.setMediaLocation(new java.lang.String("file:/C:/Users/reberle/Documents/Fig21_06_07/Fig21_06_07/" + filename));

       //     mediaPlayer.realize();
        
   //         mediaPlayer.prefetch();
    //        mediaPlayerVector.addElement(mediaPlayer);
 
        }


	public void paintComponent(Graphics g) {

		Graphics2D g2 = (Graphics2D)g;
		super.paintComponent(g2);

		Font thisFont = new Font (fontFace, Font.BOLD, fontSize);
		g2.setFont(thisFont);
		FontMetrics metrics = g2.getFontMetrics(thisFont);

		if (word) {
			int strw = metrics.stringWidth(theWord);
			int strh = metrics.getHeight();
			int leading = metrics.getLeading();
			if (hVal == -1) {  //center
				hVal = (int)((d2.getWidth() / 2) - (strw / 2));
				vVal = (int)((d2.getHeight() / 2) + (strh / 2));
			}
			else if (hVal == -2) { //random
				Random rand = new Random();

//				int hValTemp = rand.nextInt(horiz - strw);
//				int vValTemp = rand.nextInt(vert - strh);
//
//				hVal = (int)((d2.getWidth() / 2) - (horiz / 2) + hValTemp);
//				vVal = (int)((d2.getHeight() / 2) + (vert / 2) - vValTemp);

                                int hValTemp = rand.nextInt(horiz);
				int vValTemp = rand.nextInt(vert);

				hVal = (int)((d2.getWidth() / 2) - (horiz / 2) + hValTemp - strw/2);
				vVal = (int)((d2.getHeight() / 2) + (vert / 2) - vValTemp - strh/2);

			}
			else {
				vVal += strh;
			}


			if (leaveDisplayOn) {

				if (thisTrial.size() > 0) {

					for (int i = 0; i < thisTrial.size(); i++) {

						PresPanelDisplay p = (PresPanelDisplay)thisTrial.elementAt(i);
						if (p.pp_itemDisplayed.equals("kqlkql")) {
							g2.drawImage(p.pp_theImage, p.pp_hVal, p.pp_vVal, null);
						}
						else {
							g2.drawString(p.pp_itemDisplayed, p.pp_hVal, p.pp_vVal);
						}
					}

				}

				PresPanelDisplay presPanelDisplay = new PresPanelDisplay();
				Image blankImg = Toolkit.getDefaultToolkit().getImage("blank.jpg");

				presPanelDisplay.buildPresPanelDisplay(theWord, blankImg, hVal, vVal);
				thisTrial.addElement(presPanelDisplay);

			}
			g2.drawString(theWord, hVal, vVal);
			word = false;
		}
		else if (picture) {
			Image img = Toolkit.getDefaultToolkit().getImage(thePicture);
			try {
  			  MediaTracker tracker = new MediaTracker(this);
  			  tracker.addImage(img, 0);
                          tracker.waitForID(0);
			} catch (Exception e) {
				e.printStackTrace();
			}

			int width = img.getWidth(this);
			int height = img.getHeight(this);


			if (hVal == -1) {  //center
				System.out.println("Drawing centered");
				hVal = (int)((d2.getWidth() / 2) - (width / 2));
				vVal = (int)((d2.getHeight() / 2) - (height / 2));
			}
			else if (hVal == -2) {  //random
				Random rand = new Random();

                                int hValTemp = rand.nextInt(horiz);
				int vValTemp = rand.nextInt(vert);

				hVal = (int)((d2.getWidth() / 2) - (horiz / 2) + hValTemp - width/2);
				vVal = (int)((d2.getHeight() / 2) + (vert / 2) - vValTemp - height/2);
			}
//			BufferedImage bi = new BufferedImage(width, height,
//			BufferedImage.TYPE_INT_RGB);
//			Graphics2D biContext = bi.createGraphics();

			if (leaveDisplayOn) {

				if (thisTrial.size() > 0) {

					for (int i = 0; i < thisTrial.size(); i++) {

						PresPanelDisplay p = (PresPanelDisplay)thisTrial.elementAt(i);
						if (p.pp_itemDisplayed.equals("kqlkql")) {
							g2.drawImage(p.pp_theImage, p.pp_hVal, p.pp_vVal, null);


						}
						else {
							g2.drawString(p.pp_itemDisplayed, p.pp_hVal, p.pp_vVal);
						}
					}

				}

				PresPanelDisplay presPanelDisplay = new PresPanelDisplay();
				presPanelDisplay.buildPresPanelDisplay("kqlkql", img, hVal, vVal);
				thisTrial.addElement(presPanelDisplay);
			}

			g2.drawImage(img, hVal, vVal, null);
			picture = false;
		}


	}

		public void clearVector() {
			thisTrial.removeAllElements();

		}

                public void clearMediaPlayer()  {
                    if (video == true) {
  //                      ((MediaPlayer)mediaPlayerVector.elementAt(videoPanCtr)).stop();
  //                     this.remove(((MediaPlayer)mediaPlayerVector.elementAt(videoPanCtr)));
                   //     ((MediaPlayer)mediaPlayerVector.elementAt(videoPanCtr-1)).close();
                        video = false;
                    }
                }


		public void drawDot(){

			dot = true;
			repaint();
		}
		public void drawWord(String theWord, String fontFace, int fontSize, int hVal, int vVal, int horiz, int vert, boolean leaveDisplayOn){

			word = true;
			this.theWord = theWord;
			this.fontFace = fontFace;
			this.fontSize = fontSize;
			this.hVal = hVal;
			this.vVal = vVal;
			this.horiz = horiz;
			this.vert = vert;
			this.leaveDisplayOn = leaveDisplayOn;
			repaint();
	}
		public void drawPicture(String thePicture, int hVal, int vVal, int horiz, int vert, boolean leaveDisplayOn){

			picture = true;
			this.thePicture = thePicture;
			this.hVal = hVal;
			this.vVal = vVal;
			this.horiz = horiz;
			this.vert = vert;
			this.leaveDisplayOn = leaveDisplayOn;
			repaint();
	}

	public void showVideo(String thePicture, int hVal, int vVal, int horiz, int vert, boolean leaveDisplayOn)
{
			String mrl = "";
			video = true;
			this.hVal = hVal;
			this.vVal = vVal;
			this.horiz = horiz;
			this.vert = vert;
			this.leaveDisplayOn = leaveDisplayOn;
			mrl = this.thePicture;

                        int tempIndex = videoNameVector.indexOf(thePicture);
                        if (tempIndex >=0) {
                            videoPanCtr = tempIndex;
                        }

         //               videoComponent = ((MediaPlayer)mediaPlayerVector.elementAt(videoPanCtr)).getVisualComponent();
         //               Dimension size = videoComponent.getPreferredSize();
		//	int width = (int)size.getWidth();
		//  int height = (int)size.getHeight();

                        int height = 200;
                        int width = 200;

                        this.setFocusable(true);
          
			if (hVal == -1) {  //center
				hVal = (int)((d2.getWidth() / 2) - (width / 2));
				vVal = (int)((d2.getHeight() / 2) - (height / 2));
                            //    vVal += 20;
			}
			else if (hVal == -2) {  //random
				Random rand = new Random();

                                int hValTemp = rand.nextInt(horiz);
				int vValTemp = rand.nextInt(vert);

				hVal = (int)((d2.getWidth() / 2) - (horiz / 2) + hValTemp - width/2);
				vVal = (int)((d2.getHeight() / 2) + (vert / 2) - vValTemp - height/2);
			}
             
			// pjc
		//	videoPlayer = new vlcjPlayer(width, height, hVal, vVal);
		//	this.add(videoPlayer);
		//	this.setVisible(true);
		//	videoPlayer.setVisible(true);
		//	videoPlayer.play(mrl);
				

//((MediaPlayer)mediaPlayerVector.elementAt(videoPanCtr)).setBounds(hVal,vVal,width,height);
 
//                        this.add((MediaPlayer)mediaPlayerVector.elementAt(videoPanCtr));
//                        ((MediaPlayer)mediaPlayerVector.elementAt(videoPanCtr)).start();

		}

		public void eraseAll(){
                    repaint();
                    clearMediaPlayer();
	}


}

class PresPanelDisplay {
	String pp_itemDisplayed = "";
	Image pp_theImage;
	int pp_hVal = 0;
	int pp_vVal = 0;

	PresPanelDisplay buildPresPanelDisplay(String pp_itemDisplayed, Image pp_theImage, int pp_hVal, int pp_vVal){

		this.pp_itemDisplayed = pp_itemDisplayed;
		this.pp_theImage = pp_theImage;
		this.pp_hVal = pp_hVal;
		this.pp_vVal = pp_vVal;

		return this;
	}

}

class vlcjPlayer extends JPanel {
	private EmbeddedMediaPlayer player;
	private Canvas canvas;
	
	public vlcjPlayer(int width, int height, int xcoord, int ycoord) {
		canvas = new Canvas();
		repaint();
		
		canvas.setSize(width, height);
		canvas.setLocation(xcoord, ycoord);
		
		NativeLibrary.addSearchPath("libvlc", "<libvlc-path>");
		
		MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
		player = mediaPlayerFactory.newEmbeddedMediaPlayer();
		
		CanvasVideoSurface videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
	
		player.setVideoSurface(videoSurface);
		canvas.setVisible(true);
	}
	public void play(String mrl){
		this.setVisible(true);
		player.playMedia(mrl);
	}
}





