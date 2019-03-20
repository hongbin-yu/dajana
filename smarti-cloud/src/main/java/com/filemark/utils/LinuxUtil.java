package com.filemark.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.json.stream.JsonGenerator;
import javax.swing.JPanel;
/*
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;*/





















import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.filemark.jcr.model.Asset;
import com.filemark.jcr.model.SmartiNode;



public class LinuxUtil
{
	private final static Logger log = LoggerFactory.getLogger(LinuxUtil.class);
	private static String HDDPIN = "18";
	public static boolean video = false;
	public static String HOST = "http://192.168.0.134:9200";
	public static String INDEX = "yuhongyun";
	public static String TYPE = "assets";
	public static ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    /**
     * Takes a file, and resizes it to the given width and height, while keeping
     * original proportions. Note: It resizes a new file rather than resizing 
     * the original one. Resulting file is always written as a png file due to issues
     * with resizing jpeg files which results in color loss. See:
     * https://stackoverflow.com/a/19654452/49153 
     * for details, including the comments.
     * 
     */
	
	static {
        String osName = System.getProperty("os.name");
        String opencvpath = System.getProperty("user.dir");
        final String osArch = System.getProperty("os.arch");
        int bitness = Integer.parseInt(System.getProperty("sun.arch.data.model"));
        log.debug("OS:"+osName);
        log.debug("arch:"+osArch);
        log.debug("opencvpath:"+opencvpath);
        log.debug("bitness:"+bitness);

	}
	

	public static int opencvResize(String src,String des,int width,int hight) {
    	String s;
    	Process p;
    	int exit=1;
    	String shellCommand = "/opt/opencv-3.3.0/build/bin/resize_image "+src+" "+des+" "+width+" "+hight;
    	ProcessBuilder pb = new ProcessBuilder("/opt/opencv-3.3.0/build/bin/resize_image",src,des,""+width,""+hight);
    	pb.redirectErrorStream(true);
	    try {	
	        p = pb.start();//Runtime.getRuntime().exec(shellCommand);
	        BufferedReader br = new BufferedReader(
	            new InputStreamReader(p.getInputStream()));
	        while ((s = br.readLine()) != null) {
	            log.debug("line: " + s);
	        }
	        p.waitFor();
	        exit = p.exitValue();
	        if(exit !=0) {
	        	br = new BufferedReader(
	                    new InputStreamReader(p.getErrorStream()));
	                while ((s = br.readLine()) != null) {
	                    log.debug("line: " + s);
	                }
	        	log.error(shellCommand);
	        	log.error("resize image exit: " + exit);
	        	
	        }
	        p.destroy();
	    } catch (IOException e) {
			log.error("resize_image :"+e.getMessage());;
	    } catch (InterruptedException e) {
			log.error("resize_image :"+e.getMessage());;
		}
	        return exit;
	    	
	    }



	
	public static int opencvRotate(String src,String des,int angle) {
    	String s;
    	Process p;
    	int exit=1;
    	String shellCommand = "/opt/opencv-3.3.0/build/bin/rotate_image "+src+" "+des+" "+angle;
    	ProcessBuilder pb = new ProcessBuilder("/opt/opencv-3.3.0/build/bin/rotate_image",src,des,""+angle);
    	pb.redirectErrorStream(true);
	    try {	
	        p = pb.start();//Runtime.getRuntime().exec(shellCommand);
	        BufferedReader br = new BufferedReader(
	            new InputStreamReader(p.getInputStream()));
	        while ((s = br.readLine()) != null) {
	            log.debug("line: " + s);
	        }
	        p.waitFor();
	        exit = p.exitValue();
	        if(exit !=0) {
	        	br = new BufferedReader(
	                    new InputStreamReader(p.getErrorStream()));
	                while ((s = br.readLine()) != null) {
	                    log.debug("line: " + s);
	                }
	        	log.error(shellCommand);
	        	log.error("rotate image exit: " + exit);
	        	
	        }
	        p.destroy();
	    } catch (IOException e) {
			log.error("rotate_image :"+e.getMessage());;
	    } catch (InterruptedException e) {
			log.error("rotate_image :"+e.getMessage());;
		}
	        return exit;
	    	
	    }
	
    public static File resize(File file, int width, int height) throws Exception
    {
        Image img = Toolkit.getDefaultToolkit().getImage( file.getAbsolutePath() );
        loadCompletely(img);
        BufferedImage bm = toBufferedImage(img);
        bm = resize(bm, width, height);

        StringBuilder sb = new StringBuilder();
        sb.append( bm.hashCode() ).append(".png");
        String filename = sb.toString(); 

        File result = new File( filename );
        ImageIO.write(bm, "png", result);

        return result;
    }

    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        bimage.getGraphics().drawImage(img, 0, 0 , null);
        bimage.getGraphics().dispose();

        return bimage;
    }

    public static BufferedImage resize(BufferedImage image, int areaWidth, int areaHeight)
    {
        float scaleX = (float) areaWidth / image.getWidth();
        float scaleY = (float) areaHeight / image.getHeight();
        float scale = Math.min(scaleX, scaleY);
        int w = Math.round(image.getWidth() * scale);
        int h = Math.round(image.getHeight() * scale);

        int type = image.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;

        boolean scaleDown = scale < 1;

        if (scaleDown) {
            // multi-pass bilinear div 2
            int currentW = image.getWidth();
            int currentH = image.getHeight();
            BufferedImage resized = image;
            while (currentW > w || currentH > h) {
                currentW = Math.max(w, currentW / 2);
                currentH = Math.max(h, currentH / 2);

                BufferedImage temp = new BufferedImage(currentW, currentH, type);
                Graphics2D g2 = temp.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(resized, 0, 0, currentW, currentH, null);
                g2.dispose();
                resized = temp;
            }
            return resized;
        } else {
            Object hint = scale > 2 ? RenderingHints.VALUE_INTERPOLATION_BICUBIC : RenderingHints.VALUE_INTERPOLATION_BILINEAR;

            BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resized.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(image, 0, 0, w, h, null);
            g2.dispose();
            return resized;
        }
    }


    /**
     * Since some methods like toolkit.getImage() are asynchronous, this
     * method should be called to load them completely.
     */
    public static void loadCompletely (Image img)
    {
        MediaTracker tracker = new MediaTracker(new JPanel());
        tracker.addImage(img, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static String geoip(String ip) {
    	String s;
    	String city="";
    	Process p;
    	int exit = 0;
    	String shellCommand = "geoiplookup -f /usr/share/GeoIP/GeoLiteCity.dat " +ip;
    	ProcessBuilder pb = new ProcessBuilder("geoiplookup","-f","/usr/share/GeoIP/GeoLiteCity.dat",ip);
    	pb.redirectErrorStream(true);
	    try {	
	        p = pb.start();//Runtime.getRuntime().exec(shellCommand);
	        BufferedReader br = new BufferedReader(
	            new InputStreamReader(p.getInputStream()));
	        while ((s = br.readLine()) != null) {
	        	city +=s+"<br/>";
	            log.debug("line: " + s);
	        }
	        p.waitFor();
	        exit = p.exitValue();
	        if(exit !=0) {
	        	br = new BufferedReader(
	                    new InputStreamReader(p.getErrorStream()));
	                while ((s = br.readLine()) != null) {
	    	        	city +=s+"<br/>";
	                    log.debug("line: " + s);
	                }
	        	log.error(shellCommand);
	        	log.error("video exit: " + exit);
	        	
	        }
	        p.destroy();
	    } catch (IOException e) {
	    	city = e.getMessage();
			log.error("city :"+e.getMessage());;
	    } catch (InterruptedException e) {
	    	city = e.getMessage();
			log.error("city :"+e.getMessage());;
		}
	    return city;


    }  
    //atp-get install speedtest-cli
    public static String speedtest() {
    	String s;
    	String city="";
    	Process p;
    	int exit = 0;
    	String shellCommand = "speedtest-cli";
    	ProcessBuilder pb = new ProcessBuilder("speedtest-cli");
    	pb.redirectErrorStream(true);
	    try {	
	        p = pb.start();//Runtime.getRuntime().exec(shellCommand);
	        BufferedReader br = new BufferedReader(
	            new InputStreamReader(p.getInputStream()));
	        while ((s = br.readLine()) != null) {
	        	city +=s+"<br/>";
	            log.debug("line: " + s);
	        }
	        p.waitFor();
	        exit = p.exitValue();
	        if(exit !=0) {
	        	br = new BufferedReader(
	                    new InputStreamReader(p.getErrorStream()));
	                while ((s = br.readLine()) != null) {
	    	        	city +=s+"<br/>";
	                    log.debug("line: " + s);
	                }
	        	log.error(shellCommand);
	        	log.error("video exit: " + exit);
	        	
	        }
	        p.destroy();
	    } catch (IOException e) {
	    	city = e.getMessage();
			log.error("city :"+e.getMessage());;
	    } catch (InterruptedException e) {
	    	city = e.getMessage();
			log.error("city :"+e.getMessage());;
		}
	    return city;


    }      
    /*
	sudo apt-get update
	sudo apt-get upgrade
	wget http://lilnetwork.com/download/raspberrypi/mjpg-streamer.tar.gz
	tar xvzf mjpg-streamer.tar.gz
	sudo apt-get install libjpeg8-dev
	sudo apt-get install imagemagick
	cd mjpg-streamer/mjpg-streamer
	make
	./mjpg_streamer -i "./input_uvc.so" -o "./output_http.so -w ./www"
     */
    public static String video(int maxWidth,int maxHeight) {
    	String s;
    	Process p;
    	String exit="";

    	if(maxWidth>=720) maxWidth=720;
    	else if(maxWidth>=450) maxWidth=450;
    	else maxWidth=300;
    	String shellCommand = "/home/device/dajana/smarti-cloud/streamer"+maxWidth+".sh";
    	ProcessBuilder pb = new ProcessBuilder("/home/device/dajana/smarti-cloud/streamer"+maxWidth+".sh");
    	pb.redirectErrorStream(true);
	    try {
	    	closevideo();
	        p = pb.start();//Runtime.getRuntime().exec(shellCommand);
	        BufferedReader br = new BufferedReader(
	            new InputStreamReader(p.getInputStream()));
	        while ((s = br.readLine()) != null) {
	            log.debug("line: " + s);
	            exit+=s+"<br/>";
	        }
	        p.waitFor();

	        if(p.exitValue()>0) {
	        	br = new BufferedReader(
	                    new InputStreamReader(p.getErrorStream()));
	                while ((s = br.readLine()) != null) {
	                    log.debug("line: " + s);
	    	            exit+=s+"<br/>";
	                }
	        	log.error(shellCommand);
	        	log.error("video exit: " + exit);
	        	
	        }
	        p.destroy();
	    } catch (IOException e) {
			log.error("video :"+e.getMessage());;
	    } catch (InterruptedException e) {
			log.error("video :"+e.getMessage());;
		}
	        return exit;


    }

    public static int closevideo() {
    	String s;
    	Process p;
    	int exit=1;
    	ProcessBuilder pb = new ProcessBuilder("killall","mjpg_streamer");
    	pb.redirectErrorStream(true);
	    try {	
	        p = pb.start();//Runtime.getRuntime().exec(shellCommand);
	        BufferedReader br = new BufferedReader(
	            new InputStreamReader(p.getInputStream()));
	        while ((s = br.readLine()) != null) {
	            log.debug("line: " + s);
	        }
	        p.waitFor();
	        exit = p.exitValue();
	        if(exit !=0) {
	        	br = new BufferedReader(
	                    new InputStreamReader(p.getErrorStream()));
	                while ((s = br.readLine()) != null) {
	                    log.debug("line: " + s);
	                }

	        	log.error("video exit: " + exit);
	        	
	        }
	        p.destroy();
	    } catch (IOException e) {
			log.error("video :"+e.getMessage());;
	    } catch (InterruptedException e) {
			log.error("video :"+e.getMessage());;
		}
	        return exit;


    }
    //fswebcam -r 1280x720 --no-banner image3.jpg
    public static int fswebcam(String outfile,  int maxWidth,int maxHeight) {
    	String s;
    	Process p;
    	int exit=1;
    	String shellCommand = "/usr/bin/fswebcam -r "+maxWidth+"x"+maxHeight+" "+outfile;
    	ProcessBuilder pb = new ProcessBuilder("/usr/bin/fswebcam","-r",maxWidth+"x"+maxHeight,outfile);
    	pb.redirectErrorStream(true);
	    try {	
	        p = pb.start();//Runtime.getRuntime().exec(shellCommand);
	        BufferedReader br = new BufferedReader(
	            new InputStreamReader(p.getInputStream()));
	        while ((s = br.readLine()) != null) {
	            log.debug("line: " + s);
	        }
	        p.waitFor();
	        exit = p.exitValue();
	        if(exit !=0) {
	        	br = new BufferedReader(
	                    new InputStreamReader(p.getErrorStream()));
	                while ((s = br.readLine()) != null) {
	                    log.debug("line: " + s);
	                }
	        	log.error(shellCommand);
	        	log.error("convert exit: " + exit);
	        	
	        }
	        p.destroy();
	    } catch (IOException e) {
			log.error("webcam :"+e.getMessage());;
	    } catch (InterruptedException e) {
			log.error("webcam :"+e.getMessage());;
		}
	        return exit;
    
    }
    
    public static int convert(String infile, String outfile,  int maxWidth,int maxHeight) {
    	
    	//if(opencvResize(infile,outfile,maxWidth,maxHeight)==0) return 0;
    	String s;
    	Process p;
    	int exit=1;
    	String shellCommand = "/usr/bin/convert "+infile+" -resize "+maxWidth+"x"+maxHeight+"\\> "+outfile;
    	ProcessBuilder pb = new ProcessBuilder("/usr/bin/convert",infile,"-resize",maxWidth+"x"+maxHeight,outfile);
    	pb.redirectErrorStream(true);
	    try {	
	        p = pb.start();//Runtime.getRuntime().exec(shellCommand);
	        BufferedReader br = new BufferedReader(
	            new InputStreamReader(p.getInputStream()));
	        while ((s = br.readLine()) != null) {
	            log.debug("line: " + s);
	        }
	        p.waitFor();
	        exit = p.exitValue();
	        if(exit !=0) {
	        	br = new BufferedReader(
	                    new InputStreamReader(p.getErrorStream()));
	                while ((s = br.readLine()) != null) {
	                    log.debug("line: " + s);
	                }
	        	log.error(shellCommand);
	        	log.error("convert exit: " + pb.toString());
	        	
	        }
	        p.destroy();
	    } catch (IOException e) {
			log.error("pdf2jpg :"+e.getMessage());;
	    } catch (InterruptedException e) {
			log.error("pdf2jpg :"+e.getMessage());;
		}
	        return exit;
	    	
	    }
    
    public static int rotate(String infile, String outfile,  int angle) {
    	String s;
    	Process p;
    	int exit=1;
    	String shellCommand = "/usr/bin/convert "+infile+" -rotate "+angle+" "+outfile;
    	ProcessBuilder pb = new ProcessBuilder("/usr/bin/convert",infile,"-rotate",""+angle,outfile);
    	pb.redirectErrorStream(true);
        try {
	        p = pb.start();//Runtime.getRuntime().exec(shellCommand);
	
	        //p = Runtime.getRuntime().exec(shellCommand);
	        BufferedReader br = new BufferedReader(
	            new InputStreamReader(p.getInputStream()));
	        while ((s = br.readLine()) != null)
	            log.debug("line: " + s);
	        p.waitFor();
	        exit = p.exitValue();
	        if(exit !=0) {
	        	br = new BufferedReader(
	                    new InputStreamReader(p.getErrorStream()));
	                while ((s = br.readLine()) != null) {
	                    log.debug("line: " + s);
	                }
	        	log.error(shellCommand);
	        	log.error("convert exit: " + exit);
	        	
	        }
	        p.destroy();
        } catch (IOException e) {
			log.error("rotate :"+e.getMessage());;
        } catch (InterruptedException e) {
			log.error("rotate :"+e.getMessage());;
		}
        return exit;
    	
    }
    
    public static int autoRotate(String infile, String outfile) throws IOException, InterruptedException {
    	String s;
    	Process p;
    	int exit=1;
    	ProcessBuilder pb = new ProcessBuilder("/usr/bin/convert",infile,"-auto-orient",outfile);
    	pb.redirectErrorStream(true);
    	try {
	        p = pb.start();//Runtime.getRuntime().exec(shellCommand);
	
	        BufferedReader br = new BufferedReader(
	            new InputStreamReader(p.getInputStream()));
	        while ((s = br.readLine()) != null)
	            log.debug("line: " + s);
	        p.waitFor();
	        exit = p.exitValue();
	        if(exit !=0) {
	        	br = new BufferedReader(
	                    new InputStreamReader(p.getErrorStream()));
	                while ((s = br.readLine()) != null) {
	                    log.debug("line: " + s);
	                }
	
	        	log.error("convert exit: " + exit);
	        	
	        }
	        p.destroy();
        } catch (IOException e) {
			log.error("autorotate :"+e.getMessage());;
        } catch (InterruptedException e) {
			log.error("autorotate :"+e.getMessage());;
		}
        return exit;
    	
    }     

    public static int pdf2jpg(String infile,Integer n,String wxh, String outfile) {
    	String s;
    	Process p;
    	int exit=1;
    	ProcessBuilder pb = new ProcessBuilder("/usr/bin/convert","-colorspace","sRGB","-density","200",infile+"["+n+"]","-scale",wxh,"-background","white","-flatten",outfile);
    	pb.redirectErrorStream(true);
        try {
	        p = pb.start();//Runtime.getRuntime().exec(shellCommand);
	
	        BufferedReader br = new BufferedReader(
	            new InputStreamReader(p.getInputStream()));
	        while ((s = br.readLine()) != null)
	            log.debug("line: " + s);
	        p.waitFor();
	        exit = p.exitValue();
	        if(exit !=0) {
	        	br = new BufferedReader(
	                    new InputStreamReader(p.getErrorStream()));
	                while ((s = br.readLine()) != null) {
	                    log.debug("line: " + s);
	                }
	
	        	log.error("convert exit: " + exit);
	        	
	        }
	        p.destroy();
        } catch (IOException e) {
			log.error("pdf2jpg :"+e.getMessage());;
        } catch (InterruptedException e) {
			log.error("pdf2jpg :"+e.getMessage());;
		}
        return exit;
    	
    }

    public static int video2jpg(String infile,String wxh, String outfile) {
    	String s;
    	Process p;
    	int exit=1;
    	ProcessBuilder pb = new ProcessBuilder("ffmpeg","-ss", "00:00:02", "-i",infile, "-nostats","-nostdin","-s", wxh, "-vframes","1",outfile);
    	pb.redirectErrorStream(true);
        try {
	        p = pb.start();//Runtime.getRuntime().exec(shellCommand);
	
	        BufferedReader br = new BufferedReader(
	            new InputStreamReader(p.getInputStream()));
	        while ((s = br.readLine()) != null)
	            log.debug("line: " + s);
	        p.waitFor();
	        exit = p.exitValue();
	        if(exit !=0) {
	        	br = new BufferedReader(
	                    new InputStreamReader(p.getErrorStream()));
	                while ((s = br.readLine()) != null) {
	                    log.debug("line: " + s);
	                }
	
	        	log.error("convert exit: " + exit);
	        	
	        }
	        p.destroy();
        } catch (IOException e) {
			log.error("video2jpg :"+e.getMessage());;
        } catch (InterruptedException e) {
			log.error("video2jpg :"+e.getMessage());;
		}
        return exit;
    	
    }

    public static int amr2wav(String infile,String rate, String outfile) {
    	String s;
    	Process p;
    	int exit=1;
    	ProcessBuilder pb = new ProcessBuilder("ffmpeg","-i", infile, "-ar", rate, outfile);
    	pb.redirectErrorStream(true);
        try {
	        p = pb.start();//Runtime.getRuntime().exec(shellCommand);
	
	        BufferedReader br = new BufferedReader(
	            new InputStreamReader(p.getInputStream()));
	        while ((s = br.readLine()) != null)
	            log.debug("line: " + s);
	        p.waitFor();
	        exit = p.exitValue();
	        if(exit !=0) {
	        	br = new BufferedReader(
	                    new InputStreamReader(p.getErrorStream()));
	                while ((s = br.readLine()) != null) {
	                    log.debug("line: " + s);
	                }
	
	        	log.error("convert exit: " + exit);
	        	
	        }
	        p.destroy();
        } catch (IOException e) {
			log.error("amr2mp3 :"+e.getMessage());;
        } catch (InterruptedException e) {
			log.error("amr2mp3 :"+e.getMessage());;
		}
        return exit;
    	
    }
    
    public static String oreintation(String infile) {
    	String s;
    	Process p;
    	int exit=0;
    	String orientation="";
    	ProcessBuilder pb = new ProcessBuilder("/usr/bin/identify","-format","%[EXIF:orientation]",infile);
    	pb.redirectErrorStream(true);
        try {
	        p = pb.start();//Runtime.getRuntime().exec(shellCommand);
	
	        BufferedReader br = new BufferedReader(
	            new InputStreamReader(p.getInputStream()));
	        while ((s = br.readLine()) != null)
	            orientation+=s;
	        p.waitFor();
	        exit = p.exitValue();
	        if(exit !=0) {
	        	br = new BufferedReader(
	                    new InputStreamReader(p.getErrorStream()));
	                while ((s = br.readLine()) != null) {
	                    log.debug("line: " + s);
	                }
	
	        	log.error("convert exit: " + exit);
	        	
	        }
	        p.destroy();
        } catch (IOException e) {
        	log.error("orientation :"+e.getMessage());
		}catch (InterruptedException e) {
			log.error("orientation :"+e.getMessage());
		}
        return orientation;
    	
    }

    public static String getWidthxHeight(String infile) {
    	String s;
    	Process p;
    	int exit=0;
    	String orientation="";
    	ProcessBuilder pb = new ProcessBuilder("identify","-format","%wx%h",infile);
    	pb.redirectErrorStream(true);
    	
        try {
			p = pb.start();
	        BufferedReader br = new BufferedReader(
	                new InputStreamReader(p.getInputStream()));
	            while ((s = br.readLine()) != null)
	                orientation+=s;
	            p.waitFor();
	            exit = p.exitValue();
	            if(exit !=0) {
	            	br = new BufferedReader(
	                        new InputStreamReader(p.getErrorStream()));
	                    while ((s = br.readLine()) != null) {
	                        log.debug("line: " + s);
	                    }

	            	log.error("convert exit: " + exit);
	            	
	            }
	            p.destroy();
        } catch (IOException e) {
        	log.error("get width :"+e.getMessage());
		}catch (InterruptedException e) {
			log.error("get height :"+e.getMessage());
		}


        return orientation;
    	
    }
    
    public String getAddress(String gps) {
       	ProcessBuilder pb = new ProcessBuilder("curl","XGET","http://maps.google.com/maps/api/geocode/json?sensor=false&latlng="+gps);

    	
    	return "";
    }
    
    public static String getPosition(String infile) {
    	String s;
    	Process p;
    	int exit=0;
    	String position="";
    	ProcessBuilder pb = new ProcessBuilder("exiftool","-c","%.6f","-gpsposition",infile);
    	pb.redirectErrorStream(true);
    	
        try {
			p = pb.start();
	        BufferedReader br = new BufferedReader(
	                new InputStreamReader(p.getInputStream()));
	            while ((s = br.readLine()) != null)
	            	position+=s;
	            p.waitFor();
	            exit = p.exitValue();
	            if(exit !=0) {
	            	br = new BufferedReader(
	                        new InputStreamReader(p.getErrorStream()));
	                    while ((s = br.readLine()) != null) {
	                        log.debug("line: " + s);
	                    }

	            	log.error("convert exit: " + exit);
	            	
	            }
	            p.destroy();
        } catch (IOException e) {
        	log.error("get width :"+e.getMessage());
		}catch (InterruptedException e) {
			log.error("get height :"+e.getMessage());
		}
        if(position.indexOf("GPS Position")>=0) {
            String pos[] = position.split(": ");
            if(pos.length>1) {
               return pos[1];  	
            }
     	
        }

        return position;
    	
    }    
    public static int opencvLimit(String folder,String ext, int maxWidth) {

    	int exit=1;
    	File directory = new File(folder);
    	if(directory.isDirectory()) {
    		for(File file:directory.listFiles()) {
    			if(file.getName().endsWith(ext)) {
    				opencvResize(file.getAbsolutePath(),file.getAbsolutePath(),maxWidth,maxWidth);
    			}
    		}
    		exit = 0;
    	}
        
        return exit;
    	
    }         
    public static int limit(String folder,String ext, int maxWidth) {
    	String s;
    	Process p;
    	int exit=1;
    	ProcessBuilder pb = new ProcessBuilder("/usr/bin/convert",folder+"/*."+ext+"["+maxWidth+"x>]","-resize",""+maxWidth+"x"+maxWidth,"-set","filename:base","%[base]","%[filename:base]."+ext);
    	pb.redirectErrorStream(true);
        try {    	
	        p = pb.start();//Runtime.getRuntime().exec(shellCommand);
	
	
	        BufferedReader br = new BufferedReader(
	            new InputStreamReader(p.getInputStream()));
	        while ((s = br.readLine()) != null)
	            log.debug("line: " + s);
	        p.waitFor();
	        exit = p.exitValue();
	        if(exit !=0) {
	        	br = new BufferedReader(
	                    new InputStreamReader(p.getErrorStream()));
	                while ((s = br.readLine()) != null) {
	                    log.debug("line: " + s);
	                }
	        	log.error("convert exit: " + pb.toString());
	        	
	        }
	        p.destroy();
        } catch (IOException e) {
        	log.error("limit size :"+e.getMessage());
		}catch (InterruptedException e) {
			log.error("limit size :"+e.getMessage());
		}
        
        return exit;
    	
    }     
 
    
    public static int gpio(String action,String pin, String value) {
    	String s;
    	Process p;
    	int exit=0;    	
    	ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/gpio","-g",action,pin,value);
    	pb.redirectErrorStream(true);
    	
        try {
			p = pb.start();
	        BufferedReader br = new BufferedReader(
	                new InputStreamReader(p.getInputStream()));
	            while ((s = br.readLine()) != null)
	                log.debug("line: " + s);
	            p.waitFor();
	            exit = p.exitValue();
	            if(exit !=0) {
	            	br = new BufferedReader(
	                        new InputStreamReader(p.getErrorStream()));
	                    while ((s = br.readLine()) != null) {
	                        log.debug("line: " + s);
	                    }
	            	log.error("convert exit: " + exit);
	            	
	            }
	            p.destroy();
        } catch (IOException e) {
			log.error("gpio :"+e.getMessage());;
        } catch (InterruptedException e) {
			log.error("gpio :"+e.getMessage());;
		}



        return exit;
    }

    public static int doc2pdf(String filename,String outdir) throws IOException, InterruptedException {
    	int exit=1;

		BufferedWriter writer = new BufferedWriter(new FileWriter("/home/device/workingDir/doc2pdf.sh",true));

    	writer.write("cd "+outdir+" && lowriter --convert-to pdf:writer_pdf_Export --outdir "+outdir+" "+filename);
    	writer.newLine();
    	writer.close();     

        return exit;
    	
    }     

    public static int xls2pdf(String filename,String outdir) throws IOException, InterruptedException {
    	int exit=1;

		BufferedWriter writer = new BufferedWriter(new FileWriter("/home/device/workingDir/doc2pdf.sh",true));

    	writer.write("cd "+outdir+" && libreoffice --headless --convert-to pdf --outdir "+outdir+" "+filename);
    	writer.newLine();
    	writer.close();     

        return exit;
    	
    }       
    public static int video2mp4(String filename,String resolution) throws IOException, InterruptedException {
    	int exit=1;
    	String output = filename+".mp4";
    	//String webmOutput = device+filename+".webm";
    	//String icon = device+"/publish/icon400"+filename+".jpg";
		BufferedWriter writer = new BufferedWriter(new FileWriter("/home/device/workingDir/video2mp4.sh",true));
    	//writer.write("ffmpeg -ss 00:00:02 -i "+device+filename +" -nostats -nostdin -s 400X400 -vframes 1 "+icon);
    	//writer.newLine();

		writer.write("ffmpeg -i "+filename +" -nostats -nostdin -s "+resolution+" -c:v libx264 -preset ultrafast -movflags +faststart "+output);
    	//writer.write("ffmpeg -i "+device+filename +" -nostats -nostdin -s 600X400 -c:v libx264 -preset ultrafast -movflags +faststart "+output);
    	//writer.newLine();
		//writer.write("ffmpeg -i "+output+" -c:v libvpx -b:v 1M -c:a libvorbis "+webmOutput);
    	writer.newLine();
    	writer.close();    

        return exit;
    	
    }    
    public static int doc2html(String filename,String outdir) throws IOException, InterruptedException {
    	String s;
    	Process p;
    	int exit=0;
    	ProcessBuilder pb = new ProcessBuilder("lowriter","--convert-to html:XHTML file:UTF8",outdir,filename);
    	pb.redirectErrorStream(true);
        p = pb.start();//Runtime.getRuntime().exec(shellCommand);


        BufferedReader br = new BufferedReader(
            new InputStreamReader(p.getInputStream()));
        while ((s = br.readLine()) != null)
            log.debug("line: " + s);
        p.waitFor();
        exit = p.exitValue();
        if(exit !=0) {
        	br = new BufferedReader(
                    new InputStreamReader(p.getErrorStream()));
                while ((s = br.readLine()) != null) {
                    log.debug("line: " + s);
                }
        	log.error("convert exit: " + exit);
        	
        }
        p.destroy();
        return exit;
    	
    }     
    public static void gpioMode(String mode) {
    	gpio("mode", HDDPIN,mode);
    }
    public static int HDDOn() {
    	return gpio("write", HDDPIN,"1");
    }
    public static int HDDOff() {
    	return gpio("write", HDDPIN,"0");
    }
    
    public static String HDDSleep() throws IOException, InterruptedException {
    	ProcessBuilder pb = new ProcessBuilder("hdparm","-Y","/dev/sda");
  	
        return execute(pb);
    }
    public static String put(String host,String index,String content) throws IOException, InterruptedException {

    	ProcessBuilder pb = new ProcessBuilder("curl","XPUT",index,content);
        return execute(pb);
    }
    
    public static String update(SmartiNode node) throws IOException, InterruptedException {
		String json = "{\"doc\" :"+ow.writeValueAsString(node)+", \"doc_as_upsert\" : true}";
		return xpost(LinuxUtil.HOST+"/"+LinuxUtil.INDEX+"/"+LinuxUtil.TYPE+"/_update",json);
    }
    
    public static String updateProperty(String uid,String name,String value) {
    	try {
    		String json = "{ \"doc\" : {\""+name+"\":\""+value+"\" }, \"doc_as_upsert\" : true }";
			return xpost(LinuxUtil.HOST+"/"+LinuxUtil.INDEX+"/"+LinuxUtil.TYPE+"/_update",json);

		} catch (IOException | InterruptedException e) {
			log.error("updateProperty"+e.getMessage());
		}
    	return null;
    } 
 
    public static String updateProperty(String uid,String name,long value) {

    	try {
    		String json = " { \"doc\" : { \""+name+"\":"+value+" }, \"doc_as_upsert\" : true }";

			return xpost(LinuxUtil.HOST+"/"+LinuxUtil.INDEX+"/"+LinuxUtil.TYPE+"/_update",json);
		} catch (IOException | InterruptedException e) {
			log.error("updateProperty"+e.getMessage());
		}
    	return null;
    } 
    
    public static String xpost(String action,String json) throws IOException, InterruptedException {
		json = new String(json.getBytes("ISO-8859-1"),"UTF-8");
    	log.debug("update:"+json);
/*		URL url = new URL(action);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");

    	conn.setReadTimeout(10000);
    	conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
    	conn.addRequestProperty("User-Agent", "Mozilla");
    	conn.addRequestProperty("Referer", "dajana.net");
    	
		// Send post request
		conn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		
    	String contentType = conn.getContentType();
    	byte b[] = new byte[1024];
    	InputStream is = conn.getInputStream(); 
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		while(is.read(b)>0) {
			out.write(b);
		}
		out.close();        	

		String result = new String(out.toByteArray(),"uft-8");

		log.debug(result);*/
    	ProcessBuilder pb = new ProcessBuilder("curl","-XPOST",action,"-d",json);
    	
    	return execute(pb);

		//return result;
    }
    
    public static String add(SmartiNode node) throws IOException, InterruptedException {
		String json = ow.writeValueAsString(node);
  		return xpost(LinuxUtil.HOST+"/"+LinuxUtil.INDEX+"/"+LinuxUtil.TYPE+"/"+node.getUid(),json);

    } 
    
    public static String search(String username, String path, String query) throws IOException, InterruptedException {
    	String q = "{ \"query\" : {\"match\":  { \"_all\" : \""+query+"\"} }, \"filter\": {\"term\" : { \"createdby\" : \""+username+"\"} } }";
   			
    	ProcessBuilder pb = new ProcessBuilder("curl","-XGET","-H", "Content-Type: text/plain; charset=UTF-8", "--data-binary", LinuxUtil.HOST+"/"+LinuxUtil.INDEX+"/"+LinuxUtil.TYPE+"/_search?"+query);
    	
    	return execute(pb);
    } 
    
    public static String query(String query) throws IOException, InterruptedException {
    	ProcessBuilder pb = new ProcessBuilder("curl","-XPOST",LinuxUtil.HOST+"/"+LinuxUtil.INDEX+"/"+LinuxUtil.TYPE+"/_search","-d",query);
    	
    	return execute(pb);
    } 
 
    public static String get(String uid) throws IOException, InterruptedException {
    	ProcessBuilder pb = new ProcessBuilder("curl","-XGET",LinuxUtil.HOST+"/"+LinuxUtil.INDEX+"/"+LinuxUtil.TYPE+"/"+uid);
    	return execute(pb);
    }
    
    public static String delete(String uid) throws IOException, InterruptedException {
    	ProcessBuilder pb = new ProcessBuilder("curl","XDELETE",LinuxUtil.HOST+"/"+LinuxUtil.INDEX+"/"+LinuxUtil.TYPE+"/"+uid);
    	return execute(pb);
    }      
    
    public static String update_by_query(String index,String json) throws IOException, InterruptedException {
    	ProcessBuilder pb = new ProcessBuilder("curl","XPOST",index,"/_update_by_query",json);
    	
    	return execute(pb);    	
    }
    
    private static String execute(ProcessBuilder pb) throws IOException, InterruptedException {
    	String s;
    	Process p;
    	String exit="";
    	//pb.redirectErrorStream(true);
        p = pb.start();


        BufferedReader br = new BufferedReader(
            new InputStreamReader(p.getInputStream(), "UTF-8"));
        while ((s = br.readLine()) != null) {
            //log.debug("line: " + s);
            exit +=s;
        }
        p.waitFor();

        if(p.exitValue() !=0) {
        	br = new BufferedReader(
                    new InputStreamReader(p.getErrorStream()));
                while ((s = br.readLine()) != null) {
                    //log.debug("line: " + s);
                    exit +=s;
                }
        	//log.error("convert exit: " + exit);
        	
        }
        p.destroy();    	    	
    	return exit;   	
    }
}
