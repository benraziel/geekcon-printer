package geekconprinter;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.core.*;

public class CameraTest {

    public static void main (String args[]){

	    System.out.println("Hello, OpenCV");
	    // Load the native library.
	    System.loadLibrary("opencv_java249");
	
	    VideoCapture camera = new VideoCapture(0);
	    try {
			Thread.sleep(100);
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
	
	    //Mat frame = new Mat();
	    //camera.read(frame);
	    
	    Mat frame = Highgui.imread("./camera_pink.jpg"); 
	    System.out.println("Frame Obtained");
	
	    camera.release();
	    System.out.println("Captured Frame Width " + frame.width());
	
	    // apply thresholding on the image, to get a background mask
	    Mat threshImage = getThresholdedImage(frame);
	    //Mat dilated = dilateMask(threshImage);
	    
	    // dilate the mask
	    
	    Highgui.imwrite("from_camera.jpg", erodeImage(threshImage));
	    System.out.println("OK");
    }
    
    private static Mat getThresholdedImage(Mat cameraFrame)
    {
    	Mat result = new Mat(cameraFrame.size(), CvType.CV_8UC1);
    	Mat imageHSV = cameraFrame.clone();
    	
    	Imgproc.cvtColor(cameraFrame, imageHSV, Imgproc.COLOR_RGB2HSV);
    	
    	// pink
    	Core.inRange(imageHSV, new Scalar(130.0, 40.0, 210.0), new Scalar(170.0, 70.0, 255.0), result);
    	
    	// white
    	//Core.inRange(imageHSV, new Scalar(0.0, 0.0, 230.0), new Scalar(180.0, 255.0, 255.0), result);
    	
    	return result;
    }
    
    private static Mat dilateMask(Mat inputMask)
    {
    	Mat result = inputMask.clone();
    	Imgproc.dilate(inputMask, result, Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(5,5)));
    	
    	return result;
    }

    private static Mat erodeImage(Mat inputImage)
    {
    	Mat result = inputImage.clone();
    	Imgproc.erode(inputImage, result, Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(5,5)));
    	
    	return result;
    }
    
    private static Point2D getExtruderPosition(Mat cameraFrame)
    {
    	Mat thresholdImage = getThresholdedImage(cameraFrame);
    	
    	Moments m = Imgproc.moments(thresholdImage);
    	double area = m.get_m00();
    	
    	double posX = m.get_m10() / area;
    	double posY = m.get_m01() / area;
    	
    	return new Point2D(posX, posY);
    }
//    
// cvMoments(imgYellowThresh, moments, 1);
//
// // The actual moment values
// double moment10 = cvGetSpatialMoment(moments, 1, 0);
// double moment01 = cvGetSpatialMoment(moments, 0, 1);
//
// double area = cvGetCentralMoment(moments, 0, 0);

    
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