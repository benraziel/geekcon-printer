package geekconprinter;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

public class CameraTest {

    public static void main (String args[]){

    System.out.println("Hello, OpenCV");
    // Load the native library.
    System.loadLibrary("opencv_java249");

    VideoCapture camera = new VideoCapture(0);
    try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
    camera.open(0); //Useless
    if(!camera.isOpened()){
        System.out.println("Camera Error");
    }
    else{
        System.out.println("Camera OK?");
    }

    Mat frame = new Mat();

    //camera.grab();
    //System.out.println("Frame Grabbed");
    //camera.retrieve(frame);
    //System.out.println("Frame Decoded");

    camera.read(frame);
    System.out.println("Frame Obtained");

    /* No difference
    camera.release();
    */

    System.out.println("Captured Frame Width " + frame.width());

    Highgui.imwrite("camera.jpg", frame);
    System.out.println("OK");
    }
}