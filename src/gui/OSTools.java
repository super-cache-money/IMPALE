package gui;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;

public class OSTools {
	public static float retinaMultiplier = (isRetina()?1.2f:1f);
	private static String OS = System.getProperty("os.name").toLowerCase();
 
	public static void main(String[] args) {
 
		System.out.println(OS);

 

	}

    public static void listSystemProperties() {

            Properties systemProperties = System.getProperties();
            SortedMap sortedSystemProperties = new TreeMap(systemProperties);
            Set keySet = sortedSystemProperties.keySet();
            Iterator iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String propertyName = (String) iterator.next();
                String propertyValue = systemProperties.getProperty(propertyName);
                System.out.println(propertyName + ": " + propertyValue);

        }
    }

    public static String[] getJREVersion()
    {
        String discard, major, minor, update, build;

        String[] javaVersionElements = System.getProperty("java.runtime.version").split("\\.|_|-b");

        discard = javaVersionElements[0];
        major   = javaVersionElements[1];
        minor   = javaVersionElements[2];
        update  = javaVersionElements[3];
        build   = javaVersionElements[4];

        return javaVersionElements;
    }
	
	public static String getOSString()
	{
		if (isWindows()) {
			return("Windows");
		} else if (isMac()) {
			return("Mac");
		} else if (isUnix()) {
			return("Linux");
		} else {
			return("Unsupported");
		}
	}

    public static void validateJVM(){
        try {
            String vendor = System.getProperty("java.vm.vendor").toLowerCase();
            if (vendor.contains("apple") || vendor.contains("open") || vendor.contains("sun")) {
                JOptionPane.showMessageDialog(null, "You have the wrong type of java installed - yours is from " + vendor + ". Please visit www.java.com to download the correct version.");
                System.exit(1);
            }
            String[] versionElements = getJREVersion();
            if (Integer.parseInt(versionElements[1]) < 7) {
                JOptionPane.showMessageDialog(null, "Your java version is outdated! Please download the latest version from www.java.com");
                System.exit(1);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }




    }
    public static boolean isRetina() {
        String []  versionElements = getJREVersion();
        //other OS and JVM specific checks...

            if(Integer.parseInt(versionElements[1])==7)
                if(Integer.parseInt(versionElements[2])==0)
                    if(Integer.parseInt(versionElements[3])<40)
                        return false;

        if(true) {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            final GraphicsDevice device = env.getDefaultScreenDevice();

            try {
                Field field = device.getClass().getDeclaredField("scale");

                if (field != null) {
                    field.setAccessible(true);
                    Object scale = field.get(device);

                    if (scale instanceof Integer && ((Integer)scale).intValue() == 2) {
                        return true;
                    }
                }
            } catch (Exception ignore) {}
        }
        //...
        return false;
    }
	public static boolean isWindows() {
 
		return (OS.indexOf("win") >= 0);
 
	}
 
	public static boolean isMac() {
 
		return (OS.indexOf("mac") >= 0);
 
	}
 
	public static boolean isUnix() {
 
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
 
	}
 
	public static boolean isSolaris() {
 
		return (OS.indexOf("sunos") >= 0);
 
	}
 
}