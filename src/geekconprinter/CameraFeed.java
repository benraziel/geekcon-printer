package geekconprinter;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class CameraFeed {
	private VideoCapture videoCapture;
	private Mat maskY, maskX;
    public CameraFeed()
    {
	    System.out.println("Hello, OpenCV");
	    // Load the native library.
	    System.loadLibrary("opencv_java249");
	
	    videoCapture = new VideoCapture(0);
	    try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    videoCapture.open(0); //Useless
	    if(!videoCapture.isOpened()){
	        System.out.println("Camera Error");
	    }
	    else{
	        System.out.println("Camera OK?");
	    }   
	    
	    maskY = Highgui.imread("./src/resources/mask_y.png");
    	Imgproc.cvtColor(maskY, maskY, Imgproc.COLOR_RGB2GRAY);
    	
    	maskX = Highgui.imread("./src/resources/mask_x.png");
    	Imgproc.cvtColor(maskX, maskX, Imgproc.COLOR_RGB2GRAY);
    }
    
    public BufferedImage captureFrame()
    {
	    Mat frame = new Mat();
	    videoCapture.read(frame);
	    
	    return toBufferedImage(frame);
    }

    public Mat captureExtruderImage()
    {
	    Mat frame = new Mat();
	    videoCapture.read(frame);
	    
	    Mat threshImageX = getThresholdedImageX(frame);
	    Mat erodedX = erodeImage(threshImageX);
	    
	    Mat threshImageY = getThresholdedImageY(frame);
	    Mat erodedY = erodeImage(threshImageY);
	    
	    return erodedX;
    }
    
    public Point2D getExtruderLocation()
    {
    	Mat frame = new Mat();
	    videoCapture.read(frame);
	    Imgproc.medianBlur(frame, frame, 11);
	    Mat threshImageX = getThresholdedImageX(frame);
	    Mat erodedX = erodeImage(threshImageX);
	    
	    Mat threshImageY = getThresholdedImageY(frame);
	    Mat erodedY = erodeImage(threshImageY);
	    
	    double x = getExtruderPosition(erodedX).x;
	    double y = getExtruderPosition(erodedY).y;
	    
	    return  new Point2D(x, y);
    }
    
    public Mat captureExtruderImageRaw()
    {
	    Mat frame = new Mat();
	    videoCapture.read(frame);
	    
	   return frame;
    }
    
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
	
	    Mat frame = new Mat();
	    camera.read(frame);
	    
	    //Mat frame = Highgui.imread("./camera_pink.jpg"); 
	    System.out.println("Frame Obtained");
	
	    camera.release();
	    System.out.println("Captured Frame Width " + frame.width());
	
	    // apply thresholding on the image, to get a background mask
	    //Mat threshImage = getThresholdedImageX(frame);
	    //Mat dilated = dilateMask(threshImage);
	    
	    // dilate the mask
	    
	    Highgui.imwrite("from_camera.jpg", frame);
	    System.out.println("OK");
    }
    
    private Mat getThresholdedImageX(Mat cameraFrame)
    {
    	Mat result = new Mat(cameraFrame.size(), CvType.CV_8UC1);
    	Mat imageHSV = cameraFrame.clone();
    	imageHSV.setTo(new Scalar(0.0, 0.0, 0.0));
    	cameraFrame.copyTo(imageHSV, maskX);
    	
    	Imgproc.cvtColor(imageHSV, imageHSV, Imgproc.COLOR_RGB2HSV);
    	
    	// pink
    	//Core.inRange(imageHSV, new Scalar(130.0, 40.0, 210.0), new Scalar(170.0, 70.0, 255.0), result);
    	
    	// green
    	Core.inRange(imageHSV, new Scalar(20.0, 100.0, 210.0), new Scalar(120.0, 255.0, 255.0), result);
    	
    	
    	// white
    	//Core.inRange(imageHSV, new Scalar(0.0, 0.0, 230.0), new Scalar(180.0, 255.0, 255.0), result);
    	
    	return result;
    }
    
    private Mat getThresholdedImageY(Mat cameraFrame)
    {
    	Mat result = new Mat(cameraFrame.size(), CvType.CV_8UC1);
    	Mat imageHSV = cameraFrame.clone();
    	imageHSV.setTo(new Scalar(0.0, 0.0, 0.0));
    	cameraFrame.copyTo(imageHSV, maskY);
    	
    	Imgproc.cvtColor(imageHSV, imageHSV, Imgproc.COLOR_RGB2HSV);
    	
    	// green
    	Core.inRange(imageHSV, new Scalar(20.0, 100.0, 210.0), new Scalar(120.0, 255.0, 255.0), result);
    	
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
    	Imgproc.erode(inputImage, result, Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(15,15)));
    	
    	return result;
    }
    
    public Point2D getExtruderPosition(Mat thresholdImage)
    {	
    	Moments m = Imgproc.moments(thresholdImage);
    	double area = m.get_m00();
    	
    	double posX = m.get_m10() / area;
    	double posY = m.get_m01() / area;
    	
    	return new Point2D(posX, posY);
    }

    public BufferedImage toBufferedImage(Mat m){
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte [] b = new byte[bufferSize];
        m.get(0,0,b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);  
        return image;
    }
} // end class