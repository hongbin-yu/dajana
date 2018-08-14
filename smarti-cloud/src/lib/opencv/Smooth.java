    package opencv;
    
    
    import static org.bytedeco.javacpp.opencv_core.*;
    import static org.bytedeco.javacpp.opencv_imgproc.*;
    import static org.bytedeco.javacpp.opencv_imgcodecs.*;

    public class Smoother {
    	
    public static void smooth(String filename) {
    	IplImage image = cvLoadImage(filename);
	    if (image != null) {
		    cvSmooth(image, image);
		    cvSaveImage(filename, image);
		    cvReleaseImage(image);
	    }
    }

    public static void main(String[] args) {
	    if (args.length > 0) {
	    smooth(args[0]);
	    }
    }
    }