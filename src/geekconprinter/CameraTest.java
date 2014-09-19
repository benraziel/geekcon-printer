package geekconprinter;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.*;

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
	
	    Highgui.imwrite("camera.jpg", getThresholdedImage(frame));
	    System.out.println("OK");
    }
    
    private static Mat getThresholdedImage(Mat cameraFrame)
    {
    	Mat result = new Mat(cameraFrame.size(), CvType.CV_8UC1);
    	Mat imageHSV = cameraFrame.clone();
    	
    	Imgproc.cvtColor(cameraFrame, imageHSV, Imgproc.COLOR_RGB2HSV);
    	Core.inRange(imageHSV, new Scalar(0.0), new Scalar(100.0), result);
    	
    	return result;
    }
    
//    IplImage* GetThresholdedImage(IplImage* img)
//    {
//        // Convert the image into an HSV image
//        IplImage* imgHSV = cvCreateImage(cvGetSize(img), 8, 3);
//        cvCvtColor(img, imgHSV, CV_BGR2HSV);
//        IplImage* imgThreshed = cvCreateImage(cvGetSize(img), 8, 1);
//        cvInRangeS(imgHSV, cvScalar(20, 100, 100), cvScalar(30, 255, 255), imgThreshed);
//        cvReleaseImage(&imgHSV);
//        return imgThreshed;
//    }
} // end class