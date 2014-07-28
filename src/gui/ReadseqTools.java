package gui;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import iubio.readseq.BioseqFormats;
import iubio.readseq.BioseqWriterIface;
import iubio.readseq.Readseq;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import gui.Alignment.FileFormat;

/**
 *
 * @author Michael Golden
 */
public class ReadseqTools {
	


    public ReadseqTools() {
    }

    public static void convertToFormat(int formatCode, File inputFile, File outputFile) {
        try {
        	
            BioseqWriterIface seqwriter = BioseqFormats.newWriter(formatCode);
            seqwriter.setOutput(new FileWriter(outputFile));
            seqwriter.writeHeader();
            Readseq rd = new Readseq();
            rd.setInputObject(inputFile);
            if(rd.getFormat()==formatCode)
            {
            	Files.copy(inputFile.toPath(), outputFile.toPath());
            }
            	
            if (rd.isKnownFormat() && rd.readInit()) {
                rd.readTo(seqwriter);
            }
            seqwriter.writeTrailer();
            seqwriter.close();
        } 
        
        catch(FileNotFoundException ex)
        {
        	JOptionPane.showMessageDialog(null,"IMPALE does not have write access to the file you are attempting to write to:\n" + outputFile.getAbsolutePath()+"\n\nPlease save the file somewhere else, like in your default Documents folder.");
        }
        
        catch (IOException ex) {
        	
            Logger.getLogger(ReadseqTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(formatCode==8)
        {
        	fixFasta(outputFile);
        }
    }

    public static void saveAsFASTA(File inputFile, File outputFile) throws IOException {
    	
//    	Files.copy(inputFile.toPath(), outputFile.toPath());
//    	return;
    	if(isInFastaFormat(inputFile))
    	{
//    		JOptionPane.showMessageDialog(null,"Already Fasta");
    		outputFile.delete();
    		Files.copy(inputFile.toPath(), outputFile.toPath());
    		return;
    	}
    	JOptionPane.showMessageDialog(null, getFormatName(inputFile));
        BioseqWriterIface seqwriter = BioseqFormats.newWriter(8);
        try
        {
        seqwriter.setOutput(new FileWriter(outputFile));
        }
        catch(FileNotFoundException ex)
        {
        	JOptionPane.showMessageDialog(null,"IMPALE does not have write access to the folder its program files are stored in. Try moving it to My Documents!");
        }
        seqwriter.writeHeader();
        Readseq rd = new Readseq();
        rd.setInputObject(inputFile);
        if (rd.isKnownFormat() && rd.readInit()) {
            rd.readTo(seqwriter);
        }
        seqwriter.writeTrailer();
        seqwriter.close();
        
        fixFasta(outputFile);
        return;
    }
    
    

    public static boolean isInFastaFormat(File file) {
        try {
            Readseq rd = new Readseq();
            rd.setInputObject(file);
            if (rd.isKnownFormat() && rd.readInit()) {
                if (rd.getFormat() == 8) {
                    rd.close();
                    return true;
                }
            }
            rd.close();
        } catch (IOException ex) {
            Logger.getLogger(ReadseqTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static boolean isKnownFormat(File file) {
        try {
            Readseq rd = new Readseq();
            rd.setInput(file);
            if (rd.isKnownFormat() && rd.readInit()) {
                rd.close();
                return true;
            }
            rd.close();
        } catch (IOException ex) {
            Logger.getLogger(ReadseqTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static int getAlignmentFileFormat(File file)
    {
    	int formatCode = 8;
        try {
            Readseq rd = new Readseq();
            rd.setInput(file);
            if (rd.isKnownFormat() && rd.readInit()) {
                rd.close();
                formatCode = rd.getFormat();
//                switch(formatCode)
//                {
//                    case 8:
//                        return FileFormat.FASTA;
//                    case 11:
//                    case 12:
//                        return FileFormat.PHYLIP4;
//                    case 17:
//                        return FileFormat.NEXUS;
//                    case 22:
//                        return FileFormat.CLUSTAL;
//                    default:
//                        return FileFormat.UNKNOWN;
//                }
            }
            rd.close();
        } catch (IOException ex) {
            Logger.getLogger(ReadseqTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return formatCode;
    }

    public static String getFormatName(File file) {
        try {
            Readseq rd = new Readseq();
            rd.setInput(file);
            if (rd.isKnownFormat() && rd.readInit()) {
                rd.close();
                return rd.getFormatName();
            }
            rd.close();
        } catch (IOException ex) {
            Logger.getLogger(ReadseqTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static void fixFasta(File f)
    {
    	Scanner sc = null;
    	String content = "";
    	try {
    		sc = new Scanner(f);
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occured while opening/saving your alignment!");
		}
//        Pattern p = Pattern.compile("^(>.*?)\\s[\\d]+\\sbp$");
//        Matcher m = p.matcher(content);
//        StringBuffer s = new StringBuffer();
//        
//        while(m.find())
//        {
//        	m.appendReplacement(s, m.group(1));
//        }
        String output = "";
        while(sc.hasNext())
        {
        	String currentline = sc.nextLine();
        	boolean needscorrection = false;
        	String [] tokens = null;
        	if(currentline.length()>0 &&currentline.charAt(0)=='>')
        	{
        		tokens = currentline.split("\\s");
        	if(tokens.length>2 &&tokens[tokens.length-1].equals("bp") )
        	{
        		try{
        			int num = Integer.parseInt(tokens[tokens.length-2]);
        				needscorrection = true;
        		}
        		catch(Exception e)
        		{
        			
        		}
        	}
        	}
        	
        	if(needscorrection)
        	{
        		currentline = "";
        		for(int i = 0; i < tokens.length-2;i++)
        			currentline+=tokens[i];
        	}
        	
        	output+=currentline+"\n";
        }
//        System.out.println(output);
        try {
        	f.delete();
			PrintWriter out = new PrintWriter(f);
			out.println(output);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
////    	^>.*?[\d]+\sbp$
    }
}
