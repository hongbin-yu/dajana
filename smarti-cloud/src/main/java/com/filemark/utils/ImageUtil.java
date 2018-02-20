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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.filemark.jcr.serviceImpl.JcrServicesImpl;

public class ImageUtil
{
	private final static Logger log = LoggerFactory.getLogger(ImageUtil.class);
	private static String HDDPIN = "18";
    /**
     * Takes a file, and resizes it to the given width and height, while keeping
     * original proportions. Note: It resizes a new file rather than resizing 
     * the original one. Resulting file is always written as a png file due to issues
     * with resizing jpeg files which results in color loss. See:
     * https://stackoverflow.com/a/19654452/49153 
     * for details, including the comments.
     * 
     */    
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
    
    public static int convert(String infile, String outfile,  int maxWidth,int maxHeight) {
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

    public static int pdf2jpg(String infile, String outfile) {
    	String s;
    	Process p;
    	int exit=1;
    	ProcessBuilder pb = new ProcessBuilder("/usr/bin/convert","-colorspace","sRGB", infile+"[0]","-scale","400x400","-background","white","-flatten",outfile);
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
	        	log.error("convert exit: " + exit);
	        	
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

		BufferedWriter writer = new BufferedWriter(new FileWriter("/var/lib/tomcat8/conf/doc2pdf.sh",true));

    	writer.write("cd "+outdir+" && lowriter --convert-to pdf:writer_pdf_Export --outdir "+outdir+" "+filename);
    	writer.newLine();
    	writer.close();     

        return exit;
    	
    }     

    public static int xls2pdf(String filename,String outdir) throws IOException, InterruptedException {
    	int exit=1;

		BufferedWriter writer = new BufferedWriter(new FileWriter("/var/lib/tomcat8/conf/doc2pdf.sh",true));

    	writer.write("cd "+outdir+" && libreoffice --headless --convert-to pdf --outdir "+outdir+" "+filename);
    	writer.newLine();
    	writer.close();     

        return exit;
    	
    }       
    public static int video2mp4(String filename,String device) throws IOException, InterruptedException {
    	int exit=1;
    	String output = device+filename+".mp4";
    	String icon = device+"/publish/icon400"+filename+".jpg";
		BufferedWriter writer = new BufferedWriter(new FileWriter("/var/lib/tomcat8/conf/video2mp4.sh",true));

    	writer.write("ffmpeg -i "+device+filename +" -nostats -nostdin -s 600X400 -c:v libx264 -preset ultrafast "+output);
    	writer.newLine();
    	writer.write("ffmpeg -ss 00:00:02 -i "+output +" -nostats -nostdin -s 400X300 -vframes 1 "+icon);
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
   
    public static int HDDOn() {
    	return gpio("write", HDDPIN,"1");
    }
    public static int HDDOff() {
    	return gpio("write", HDDPIN,"0");
    }    
}