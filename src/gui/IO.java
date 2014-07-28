package gui;




import gui.UndoRedoTree.Node;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.biojava3.core.sequence.AccessionID;
import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava3.core.sequence.compound.AminoAcidCompound;
import org.biojava3.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava3.core.sequence.compound.DNACompoundSet;
import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.io.FastaReader;
import org.biojava3.core.sequence.io.FastaReaderHelper;
import org.biojava3.core.sequence.io.FastaWriterHelper;
import org.biojava3.core.sequence.io.GenericFastaHeaderParser;
import org.biojava3.core.sequence.io.DNASequenceCreator;
import org.biojava3.core.sequence.io.ProteinSequenceCreator;
import org.biojava3.core.sequence.template.AbstractSequence;


public class IO {
	static int numberOfSessionSaveFiles =2;
	static String lastOpenedMD5 = null;
	static int upsizeHeapThresholdKB = 2048;
	public static boolean lastSeqWasProtein = false;
    public static LinkedHashMap readFastaSequence(InputStream inStream) throws Exception {
    	//Formerly LinkedHashMap<String, DNASequence>
            LinkedHashMap output;
        FastaReader<DNASequence, NucleotideCompound> fastaReader = new FastaReader<DNASequence, NucleotideCompound>(
                inStream,
                new GenericFastaHeaderParser<DNASequence, NucleotideCompound>(),
                new DNASequenceCreator(AmbiguityDNACompoundSet.getDNACompoundSet()));
        try
        {
         output = fastaReader.process();
         lastSeqWasProtein=false;
         Sequence.isProtein = false;
        }
        catch(org.biojava3.core.exceptions.CompoundNotFoundError ex)
        {


//         lastSeqWasProtein=true;
//         
// 		FastaReader<ProteinSequence,AminoAcidCompound> fastaReaderAA = 
// 				new FastaReader<ProteinSequence,AminoAcidCompound>(
// 						inStream, 
// 						new GenericFastaHeaderParser<ProteinSequence,AminoAcidCompound>(), 
// 						new ProteinSequenceCreator(AminoAcidCompoundSet.getAminoAcidCompoundSet()));
// 			output = fastaReaderAA.process();

        	return null;
        }
        return output;
    }

    /**
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static LinkedHashMap readFastaSequence(
            File file) throws Exception {
        FileInputStream inStream = new FileInputStream(file);
        LinkedHashMap dnaSequences = readFastaSequence(inStream);
        inStream.close();
        if(dnaSequences==null)
        {
        dnaSequences = FastaReaderHelper.readFastaProteinSequence(file);

        lastSeqWasProtein = true;
        Sequence.isProtein = true;
        }
        System.out.println();
        return dnaSequences;
    }
    
    public static File lastFile = null; //this is the last alignment file opened, and the default file to be saved to.
    public static File tempfile = null; //input alignments are converted to a fasta file, placed here, and parsed by IMPALE.
    public static int lengthmult = 1;
    public static int widthmult = 1;
	//public File theFile;
	//public Alignment al;
	
	
//	public static Alignment openFasta2(File f)
//	{
//
//	}
    
    public static ArrayList<String> getRecentFiles()
    {
    	File jarfile = null;
		//			System.out.println(RunEverything.class.getProtectionDomain().getCodeSource().getLocation().getPath());
//		jarfile = new File(RunEverything.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		 String path=ClassLoader.getSystemClassLoader().getResource(".").getPath();
		 jarfile = new File(path);
		 String parent = null;
		try {
			parent = URLDecoder.decode(jarfile.getAbsolutePath(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				ObjectInputStream  ois = null;
				ArrayList<String> recentFiles = null;
//				JOptionPane.showMessageDialog(null,parent);
				try {
					ois = new ObjectInputStream(new FileInputStream(parent+ File.separator + "config"+File.separator+"recentfiles.ser"));
					recentFiles = (ArrayList<String>) ois.readObject();
					ois.close();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					recentFiles = new ArrayList<String>();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					
				}
				catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
				
				return recentFiles;
    }
    
    public static void saveRecentFiles(ArrayList<String> recentFiles)
    {
    	List<String> bannedNames = Arrays.asList("-.fas","UNSAVED");
    	Iterator<String> it = recentFiles.iterator();
    	while(it.hasNext())
    	{
    		String curr = it.next();
    		File f = new File(curr);
    		if(!f.exists())
    			it.remove();
    		if(bannedNames.contains(curr))
    			it.remove();
    	}
    	File jarfile = null;
		 String path=ClassLoader.getSystemClassLoader().getResource(".").getPath();

    	 jarfile = new File(path);
    	String parent = null;
		try {
			parent = URLDecoder.decode(jarfile.getAbsolutePath(),"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		File testd = new File(parent+ File.separator + "config");
		if(!testd.exists())
		{
			testd.mkdirs();
		}
		File testf = new File(parent+ File.separator + "config" + File.separator + "recentfiles.ser");
		if(testf.exists())
		{
			testf.delete();
		}
		try {
			testf.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ObjectOutputStream oos = null;
		try {
			
			oos = new ObjectOutputStream(new FileOutputStream(parent+ File.separator + "config" + File.separator + "recentfiles.ser"));
			oos.writeObject(recentFiles);
			oos.close();
			System.out.println("Succesfully written");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			// TODO Auto-generated catch block
			recentFiles =null;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try{
		Alignment.al.panel.menu.buildRecentFiles();
		}
		catch(NullPointerException ex)
		{
			System.out.println("Can't load recent file list yet...");
		}
    }
    public static void writeFastaOld(final boolean new1, final Runnable doOnEnd)
    {
//    	Alignment al 
    	Runnable mainTask = new Runnable()
    	{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			
    	Alignment.al.stripTrailingBlanks();
//    	String alstring = al.toString();
    	File theFile = null;
    	

    	if(new1)
    	{
//    		SaveFileChooser sfc = new SaveFileChooser();
    		theFile = AWTFileDialog.saveFile();
    		lastFile = theFile;
    		
    	}
    	else
    	{
    		theFile = lastFile;
    	}
    	ArrayList<String> recentFiles = getRecentFiles();

   //save to recent files
		if(!lastFile.getName().equals("-.fas"))
		{
		try {
			recentFiles.remove(URLDecoder.decode(lastFile.getAbsolutePath(), "UTF-8"));
			recentFiles.add(0,URLDecoder.decode(lastFile.getAbsolutePath(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}

	MessageDigest md=null;
	try {
		md = MessageDigest.getInstance("md5");
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    FileOutputStream fos = null;
    BufferedWriter out = null;
    		try {
		fos = new FileOutputStream(tempfile);
		out = new BufferedWriter(new OutputStreamWriter(fos));

//    if(lastSeqWasProtein)
//		List arr = new ArrayList();
//    	List<ProteinSequence> arr = new ArrayList<ProteinSequence>();
//    else
//    	List<DnaSequence> arr = new ArrayList<ProteinSequence>();

if(!lastSeqWasProtein)
{
    List<DNASequence> arr = new ArrayList<DNASequence>();
    FastaWriterHelper fwh = new FastaWriterHelper();
    
    
    DNASequenceCreator dsc = new DNASequenceCreator(AmbiguityDNACompoundSet.getDNACompoundSet());
//    org.biojava3.core.sequence.compound.
    int lastUpdate = -1;
	for(int i = 0; i < Alignment.al.size(); i++)
	{
		if(i*100/Alignment.al.size()!=lastUpdate)
		{
			lastUpdate= i*100/Alignment.al.size();
			final int currUpdate = lastUpdate;
			SwingUtilities.invokeLater(new Runnable()
			{

				@Override
				public void run() {
					
					Alignment.al.helpText.setText("Building save file... \n\nProgress: " + currUpdate + "%");
					// TODO Auto-generated method stub
					
				}
				
			});
		}
		String curr = Alignment.al.getUnderlying(i).toString();
		//arr.add();
//		System.out.println(al.getUnderlying(i).toString());
		DNASequence as = (DNASequence) dsc.getSequence(curr,0);
		as.setAccession(new AccessionID(Alignment.al.getUnderlying(i).name));
		arr.add(as);
		md.update(curr.getBytes());
//		alstring+=curr;


		
	
	}
	
	Collection<DNASequence> col = arr;
	SwingUtilities.invokeLater(new Runnable()
	{

		@Override
		public void run() {
			
			Alignment.al.helpText.setText("Writing save file...");
			// TODO Auto-generated method stub
			
		}
		
	});
	fwh.writeNucleotideSequence(fos, col);
	
	fos.flush();
}
else
{
	
    List<ProteinSequence> arr = new ArrayList<ProteinSequence>();
    FastaWriterHelper fwh = new FastaWriterHelper();
    
    
    ProteinSequenceCreator dsc = new ProteinSequenceCreator(org.biojava3.core.sequence.compound.AminoAcidCompoundSet.getAminoAcidCompoundSet());
	int lastUpdate = -1;
    for(int i = 0; i < Alignment.al.size(); i++)
	{
		lastUpdate= i*100/Alignment.al.size();
		final int currUpdate = lastUpdate;
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run() {
				
				Alignment.al.helpText.setText("Building save file... \n\nProgress: " + currUpdate + "%");
				// TODO Auto-generated method stub
				
			}
			
		});
		String curr = Alignment.al.getUnderlying(i).toString();
		//arr.add();
//		System.out.println(al.getUnderlying(i).toString());
		ProteinSequence as = (ProteinSequence) dsc.getSequence(curr,0);
		as.setAccession(new AccessionID(Alignment.al.getUnderlying(i).name));
		arr.add(as);
		md.update(curr.getBytes());
		
//		alstring+=curr;


		
	
	}
	
	Collection<ProteinSequence> col = arr;
	SwingUtilities.invokeLater(new Runnable()
	{

		@Override
		public void run() {
			
			Alignment.al.helpText.setText("Writing save file...");
			// TODO Auto-generated method stub
			
		}
		
	});
	fwh.writeProteinSequence(fos, col);
}

SwingUtilities.invokeLater(new Runnable()
{

	@Override
	public void run() {
		
		Alignment.al.helpText.setText("Saving session...");
		// TODO Auto-generated method stub
		
	}
	
});
	
	String hash = getMD5Hash(md);
	System.out.println("hash:" + hash);
	saveSession(Alignment.al,hash);
	 

//	Scanner sc = new 
	
//    ReadseqTools.convertToFormat(al.format, theFile, tempfile);
	
	if(Alignment.al.format!=8) //if its not already fasta
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run() {
				
				Alignment.al.helpText.setText("Converting back to original format...");
				// TODO Auto-generated method stub
				
			}
			
		});
    ReadseqTools.convertToFormat(Alignment.al.format, tempfile, theFile);
	}
	else
	{

		Files.deleteIfExists(theFile.toPath());
		Files.copy(tempfile.toPath(), theFile.toPath());
	}

		System.out.println("starting");
		
	
		
    
    		} catch (Exception e1) {
    			// TODO Auto-generated catch block
//    			JOptionPane.showMe
    			JOptionPane.showMessageDialog(null, "Something went wrong with the saving of your file. We understand if you want to stop using IMPALE.","Save Error", JOptionPane.ERROR_MESSAGE);
    			e1.printStackTrace();
    		}
    		
    		
    	saveRecentFiles(recentFiles);

	//System.out.println(al.columns);
	// TODO Auto-generated method stub
	
}
    		
    	};
    	
    	Runnable guiUpdate = new Runnable()
    	{

			@Override
			public void run() {
				
				if(IO.lastFile==null)
				{
	
					return;
				}
					
				Alignment.al.helpText.setText("This session has been successfully saved.");
				if(!IO.lastFile.getName().equals("-.fas"))
					RunEverything.currentRe.jf.setTitle(RunEverything.currentRe.titleBase+" : " + IO.lastFile.getName());
				else
					RunEverything.currentRe.jf.setTitle(RunEverything.currentRe.titleBase);
				if(doOnEnd!=null)
				doOnEnd.run();
				// TODO Auto-generated method stub
				
			}
    		
    	};
    	
    	WorkDispatcher.submit(mainTask, guiUpdate, "Saving Progress: ");
    }
	public static Alignment openFasta()
	{
	

//		String alstring = "";
		ArrayList<String> recentFiles = getRecentFiles();
		 String path=ClassLoader.getSystemClassLoader().getResource(".").getPath();
		 File jarfile = new File(path);
		 String parent = null;
		try {
			parent = URLDecoder.decode(jarfile.getAbsolutePath(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		
		Alignment al = new Alignment();
		LinkedHashMap inseqs = null;
//		tempfile = new File( "C:/temp" + File.separator +"temp_IMPALE.fas");
		
		tempfile = new File(parent + File.separator + "temp" + File.separator +"temp_IMPALE.fas");
		if(tempfile.exists())
			tempfile.delete();
		File f = tempfile;
		
//		System.out.println("last file" + lastFile);
//		String hash = getMD5Hash(lastFile.getAbsolutePath());
		
		if(!lastFile.getName().equals("-.fas"))
		{
		try {
			recentFiles.remove(URLDecoder.decode(lastFile.getAbsolutePath(), "UTF-8"));
			recentFiles.add(0,(URLDecoder.decode(lastFile.getAbsolutePath(), "UTF-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
		try {
			al.format = ReadseqTools.getAlignmentFileFormat(lastFile);
			ReadseqTools.saveAsFASTA(lastFile, tempfile);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		tempfile.deleteOnExit();
		try {
			System.out.println("PATH " + f.getCanonicalPath());
		  inseqs = readFastaSequence(f);
		  
		} catch (Exception e) {
			System.out.println("Something fucked up");
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		al.usedResiduesSet = new HashSet<Residue.ResidueType>();
		
//		String alstring = "";
		MessageDigest md=null;
		try {
			md = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(lastSeqWasProtein)
		{
			LinkedHashMap<String, ProteinSequence> protseqs = (LinkedHashMap<String, ProteinSequence>) inseqs;
			String strname;
			String strseq;
			Sequence.isProtein = true;
			int i = 0;
			for(int k = 0; k<lengthmult;k++)
			for(Entry<String,ProteinSequence> entry: protseqs.entrySet())
			{
//				al.scoreBackup.add(new Vector<Residue>());
				strseq="";
				for(int j = 0; j < widthmult; j++)
				strseq += entry.getValue().getSequenceAsString();
				strname = entry.getValue().getOriginalHeader();
//				alstring+=strseq;
				md.update(strseq.getBytes());
				for(int u = 0; u < strseq.length();u++)
				{
//					al.scoreBackup.get(al.scoreBackup.size()-1).add(new Residue(strseq.charAt(u)));
				}
			//	System.out.println(strname);
				al.add(new Sequence(strname, strseq,i));
				i++;
				
				
				//System.out.println(entry.getValue().getOriginalHeader() + "=" + entry.getValue().getSequenceAsString());
			}
		}
		else
		{
			LinkedHashMap <String, DNASequence> dnaseqs = (LinkedHashMap<String, DNASequence>) inseqs;
			String strname;
			String strseq;

			Sequence.isProtein = false;
			int i = 0;
			for(int k = 0; k<lengthmult;k++)
			for(Entry<String,DNASequence> entry: dnaseqs.entrySet())
			{
//				al.scoreBackup.add(new Vector<Residue>());
				strseq="";
				for(int j = 0; j < widthmult; j++)
				strseq += entry.getValue().getSequenceAsString();
				strname = entry.getValue().getOriginalHeader();
				for(int u = 0; u < strseq.length();u++)
				{
//					al.scoreBackup.get(al.scoreBackup.size()-1).add(new Residue(strseq.charAt(u)));
				}
//				alstring+=strseq;
				md.update(strseq.getBytes());
			//	System.out.println(strname);
				al.add(new Sequence(strname, strseq,i));
				i++;
				
				
				//System.out.println(entry.getValue().getOriginalHeader() + "=" + entry.getValue().getSequenceAsString());
			}
		}
//		String alstring = al.toString();
		String hash = getMD5Hash(md);
		System.out.println("hash " + hash);
//		tryToLoadSession(al,hash);
		lastOpenedMD5 = hash;
		al.usedResiduesSet.add(Residue.ResidueType.BLANK);
		Iterator<Residue.ResidueType> urit = al.usedResiduesSet.iterator();
		al.usedResiduesArr = new Residue.ResidueType [al.usedResiduesSet.size()];
		int currindex =0;
		while(urit.hasNext())
		{
			Residue.ResidueType currtype = urit.next();
			al.usedResiduesArr[currindex]=currtype;
			currindex++;
		}
		
		//just bump blank to the end of the arr
		int blankpos = -1;
		for(int i = 0; i < al.usedResiduesArr.length && blankpos < 0;i++)
		{
			if(al.usedResiduesArr[i]==Residue.ResidueType.BLANK)
				blankpos = i;
		}
		Residue.ResidueType temp = al.usedResiduesArr[al.usedResiduesArr.length-1];
		al.usedResiduesArr[al.usedResiduesArr.length-1] = Residue.ResidueType.BLANK;
		al.usedResiduesArr[blankpos] = temp;
		
		
		
//		JOptionPane.showMessageDialog(null, "WTF:"+parent+ File.separator + "/config/recentfiles.ser" );


		System.out.println("length" + al.size() + "width" + al.longestSeq);
//System.out.println("ARRAY SIZE " + al.size() + " " + al.get(0).size());
		//System.out.println(inseqs.keySet());

		//al.prepare();
		//System.out.println(al.columns);
//		al.panel.menu.buildRecentFiles();
		saveRecentFiles(recentFiles);
		return al;
     
	}
	
	public static String getMD5Hash(MessageDigest m)
	{
//		String str = "";
//		for(int i = 0; i < size();i++)
//		{
//			Sequence seq = this.getUnderlying(i);
//			for(Residue res: seq)
//			{
//				str+=res;
//				
//			}
//			str+="\n";
//		}
		
//		System.out.println("Here");
//		MessageDigest m = null;
//		try {
//			m = MessageDigest.getInstance("MD5");
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		m.reset();
//		m.update(str.getBytes());
		byte[] digest = m.digest();
		System.out.println("Here");
		BigInteger bigInt = new BigInteger(1,digest);
		String hashtext = bigInt.toString(16);
		// Now we need to zero pad it if you actually want the full 32 chars.
		while(hashtext.length() < 32 ){
		  hashtext = "0"+hashtext;
		}
		
		return hashtext;
	}
	public static void tryToLoadSession(Alignment al, String md5)
	{
//		String md5 = al.toString();
    	File jarfile = null;
		//			System.out.println(RunEverything.class.getProtectionDomain().getCodeSource().getLocation().getPath());
//		jarfile = new File(RunEverything.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		 String path=ClassLoader.getSystemClassLoader().getResource(".").getPath();
		 jarfile = new File(path);
		 String parent = null;
		try {
			parent = URLDecoder.decode(jarfile.getAbsolutePath(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 String pathToSaveFiles = parent+ File.separator + "config" + File.separator + "saved_sessions" + File.separator;
		 File [] listOfSessions = new File(pathToSaveFiles).listFiles();
		 
		 int foundCount = 0;
		 boolean found = false;
		 if(listOfSessions!=null)
		 for(int i = 0; i < listOfSessions.length; i++)
		 {
			 if(listOfSessions[i].getName().substring(0, listOfSessions[i].getName().length()-4).equals(md5))
			 {
				 foundCount++;

			 }
		 }

		 if(foundCount ==numberOfSessionSaveFiles)
			 found = true;
		 if(found)
		 {
			 
			 int response = JOptionPane.showConfirmDialog(null, "A saved editing session for this alignment has been found. Would you like to resume it?","Session Resumption", JOptionPane.YES_NO_OPTION);
			 if(response==JOptionPane.CANCEL_OPTION)
				{
					return;
				}
				else if(response == JOptionPane.NO_OPTION)
				{	
					found = false;

				}
				else if(response ==JOptionPane.YES_OPTION)
				{
					
				}
		 }
			

		 if(found)
		 {
			 System.out.println("found");
				ObjectInputStream  oisMap = null;
				ObjectInputStream oisURT = null;
				
				HashMap<String,Object> map = null;
				UndoRedoTree oldurt=al.urt;
				ScoreGraph oldscoreGraph = al.panel.topPanel.scoreGraph;
				BigInteger oldOldMaxScore = Alignment.al.netBlock.oldMaxScore;
				
				
				try {
					String pathToMapFile = parent+ File.separator + "config" + File.separator + "saved_sessions" + File.separator+md5+".map";
					String pathToURTFile = parent+ File.separator + "config" + File.separator + "saved_sessions" + File.separator+md5+".urt";
					oisMap = new ObjectInputStream(new FileInputStream(pathToMapFile));
					oisURT = new ObjectInputStream(new FileInputStream(pathToURTFile));
					map = (HashMap<String, Object>) oisMap.readObject();
					
					
					oisMap.close();
					al.urt = (UndoRedoTree) oisURT.readObject();
					oisURT.close();
					
//					al.panel.topPanel.remove(Alignment.al.panel.topPanel.chartPanel);
					al.panel.topPanel.chartPanel.remove(al.panel.topPanel.scoreGraph);
					al.panel.topPanel.scoreGraph = (ScoreGraph) map.get("chartPanel");
					if(al.panel.topPanel.scoreGraph.scoreHistory.size()>0)
					{
						Alignment.al.netBlock.oldMaxScore = (BigInteger) map.get("oldMaxScore");
						Alignment.al.netBlock.firstpublish=false;
						Alignment.al.panel.topPanel.scoreGraph.minScore= (BigInteger) map.get("minScore");
					}

                    Sequence refseq = (Sequence) map.get("referenceSequence");
                    if(refseq!=null)
                    {
                        al.referenceSequence=refseq;
                    }
					al.panel.topPanel.chartPanel.add(al.panel.topPanel.scoreGraph);
					al.panel.topPanel.revalidate();
					al.panel.topPanel.repaint();
					al.setStickyColumns(false);
					 //we are removing this - we don't want to recalc everything from the initial save state
					//this will be set to null if it isnt to be reinstated
					synchronized(Alignment.al.stickyLock)
					{
						try {
							Alignment.al.stickyLock.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					ActionSequence rea = al.savedRunEverything;
					System.out.println("Session loading sticky columns set!");
					ArrayList<ActionSequence> reasFound = new ArrayList<ActionSequence>();
					synchronized(al.scoreal.scoreProcessingQueue)
					{
						LinkedList<ActionSequence> savedQueue = (LinkedList<ActionSequence>) map.get("scoreProcessingQueue");
						//we need to roll back actions in the ScoringALignment, since its been generated from Alignment, which is ahead of its time
						Alignment.al.blockEdits = true;
						Vector<String> historyCopy = (Vector<String>) al.urt.treeHistory.clone();
						
						for(Node n : al.urt.treeHistory)
						{
							System.out.print(n.id+"-");
						}
						System.out.println("");
						
						int i = al.urt.treeHistory.size()-1; //we're starting on the latest node, and moving back one
						for(ActionSequence actions : savedQueue)
						{
							
							if(actions.editarr.size()==1 && actions.editarr.get(0) instanceof RedoEverythingAction)
							{
								reasFound.add(actions);
								
								continue;
								//do fuckall
							}
							
							i--;
							//backtrace steps till we reach where scoreal is
	
							//we need to find the node to go to
//							if(historyCopy.get(historyCopy.size()-savedQueue.size()+i).equalsIgnoreCase("new"))
//							al.urt.undo();
//							else if(historyCopy.get(historyCopy.size()-savedQueue.size()+i).equalsIgnoreCase("redo"))
//							al.urt.undo();
//							else if(historyCopy.get(historyCopy.size()-savedQueue.size()+i).equalsIgnoreCase("undo"))
//							al.urt.redo();
							
//							historyCopy.remove(historyCopy.size()-1);
			
						}
						Node targetNode = al.urt.treeHistory.get(i);
						Alignment.al.urt.goToNode(targetNode);
						
						//we need to empty all the scoreedits out of scoreProcessingQueue
						while(!al.scoreal.scoreProcessingQueue.isEmpty())
						{
						
						ActionSequence currActions = al.scoreal.scoreProcessingQueue.pop();
						//stop the editsInQ from going wonky
						Alignment.al.scoreal.pacmanCurrentUpdate.decrementAndGet();
						SwingUtilities.invokeLater(new Runnable(){

							@Override
							public void run() {
//
								Alignment.al.panel.topPanel.editsInQLabel.setText((Alignment.al.scoreal.pacmanCurrentUpdate.get())+"");
									Alignment.al.panel.topPanel.scoreProgressBar.setVisible(false);
									Alignment.al.panel.topPanel.scoreProgressBar.setValue(0);
								
								// TODO Auto-generated method stub
								
							}
							
						});
						
						
						for(ScoreAction currentAction : currActions.editarr)
						{
							currentAction.run();
						}
						
						}
						//end pasted shit
////						Alignment.al.debugTrace.add(0,new Vector(Alignment.al.scoreal.get(0).subList(90,135)));
//						
//						for(Vector<Residue.ResidueType> curr : Alignment.al.debugTrace)
//						{
//							System.out.println(""+curr);
//						}
						if(reasFound.size()>0)
						{
							for(ActionSequence redo : reasFound)
							{
								savedQueue.remove(redo);
								
							}
							
						savedQueue.add(rea);	
							 //this will get pushed onto the scoreProcessingQueue eventually, and will cause a recompute at the saved alignment point.
						}
						
						
						
						
						
						
						
						Alignment.al.blockEdits = false;
						
						

						oisURT = new ObjectInputStream(new FileInputStream(pathToURTFile));
						
						al.urt = (UndoRedoTree) oisURT.readObject();
						rea.node = al.urt.currentNode;//since its not set earlier (may have no effect if theres no rea)
						oisURT.close();
						
						
						synchronized(Alignment.al.scorequeue)
						{
	
									Alignment.al.scorequeue.add(new RedoEverythingAction(null, null));
								
						}

						Alignment.al.scoreal.scoreProcessingQueue.add(new ActionSequence(Alignment.al.scorequeue,Alignment.al.scoreal.seqschangedset, Alignment.al.urt.currentNode));
					
						
						
						//since we used urt to roll back a few edits in scoreal, lets restore it.
						
						for(ActionSequence curr : savedQueue)
						{
							Alignment.al.scoreal.pacmanCurrentUpdate.incrementAndGet();
							al.scoreal.scoreProcessingQueue.add(curr);
							
						}
					}
					
					SwingUtilities.invokeLater(new Runnable(){					
						@Override
					public void run() {
//						if(Alignment.al.panel.topPanel.editsInQLabel!=null)
						Alignment.al.panel.topPanel.editsInQLabel.setText((Alignment.al.scoreal.pacmanCurrentUpdate.get())+"");

						
						// TODO Auto-generated method stub
						
					}});

					
					
					
				} 

				catch(Exception ex)
				{
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "Unable to load saved session. It was probably created with an older version of IMPALE, which is why it is incompatible. \n The alignment will still be loaded fine, at least.");
					al.panel.topPanel.chartPanel.remove(al.panel.topPanel.scoreGraph);
					al.panel.topPanel.scoreGraph = oldscoreGraph;

					Alignment.al.netBlock.oldMaxScore = oldOldMaxScore;
					Alignment.al.netBlock.firstpublish=true;
						
					
					al.panel.topPanel.chartPanel.add(al.panel.topPanel.scoreGraph);
					al.panel.topPanel.revalidate();
					al.panel.topPanel.repaint();
					synchronized(Alignment.al.scoreal.scoreProcessingQueue)
					{
						Alignment.al.scoreal.scoreProcessingQueue.clear();
					}
					al.setStickyColumns(true);
				}
		 }
		 else
		 {
			 al.setStickyColumns(true);
		 }

		
	}
	public static void saveSettings()
    {
        HashMap<String, Object> settingsMap = new HashMap<String, Object>();
//        misc settings
        settingsMap.put("alignToProfile_boolean",Alignment.al.alignToProfile);
        settingsMap.put("disable_scoring_boolean",Alignment.al.scoreal.disable_scoring);
        settingsMap.put("deleteResidues_boolean", Alignment.al.deleteResidues);

//        select similar settings
        settingsMap.put("startingSimilarJump_double", SimilarEngine.startingSimilarJump);
        settingsMap.put("startingSimilarBound_double", SimilarEngine.startingSimilarBound);

//      sticky settings
        settingsMap.put("maxStickyGap_double",Alignment.al.maxStickyGap);
        settingsMap.put("maxStickyGap_double",Alignment.al.minStickyMatch);
        settingsMap.put("minStickySize_int",Alignment.al.minStickySize);

//      scoring settings
        settingsMap.put("scoreGapExtension_int",Sequence.scoreGapExtension);
        settingsMap.put("scoreGapOpen_int",Sequence.scoreGapOpen);
        settingsMap.put("scoreTransition_int",Sequence.scoreTransition);
        settingsMap.put("scoreTransversion_int",Sequence.scoreTransversion);
        settingsMap.put("scoreMatch_int",Sequence.scoreMatch);
        settingsMap.put("AASubstitution_int_2d",Residue.AAsubstitution);

        String path=ClassLoader.getSystemClassLoader().getResource(".").getPath();
        File jarfile = new File(path);
        String parent = null;
        try {
            parent = URLDecoder.decode(jarfile.getAbsolutePath(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        File settingsFile = new File(parent + File.separator + "config" + File.separator +"settings.map");
        if(settingsFile.exists())
            settingsFile.delete();

        System.out.println("Writing settings to file...");
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(settingsFile.getAbsolutePath()));
            oos.writeObject(settingsMap);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }





    }

    public static void loadSettings()
    {
        File jarfile = null;
        //			System.out.println(RunEverything.class.getProtectionDomain().getCodeSource().getLocation().getPath());
//		jarfile = new File(RunEverything.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String path=ClassLoader.getSystemClassLoader().getResource(".").getPath();
        jarfile = new File(path);
        String parent = null;
        try {
            parent = URLDecoder.decode(jarfile.getAbsolutePath(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String pathToSettings = parent+ File.separator + "config" + File.separator + "settings.map";
        ObjectInputStream  ois = null;
        HashMap<String,Object> settingsMap = null;
//				JOptionPane.showMessageDialog(null,parent);
        try {
            ois = new ObjectInputStream(new FileInputStream(pathToSettings));
            settingsMap = (HashMap<String,Object>) ois.readObject();
            ois.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            // TODO Auto-generated catch block
            return;
        } catch (IOException e1) {
            e1.printStackTrace();
            // TODO Auto-generated catch block
            return;
        }
        catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        }


//        misc settings
        if(settingsMap.containsKey("alignToProfile_boolean"))
        Alignment.al.alignToProfile = (Boolean)  settingsMap.get("alignToProfile_boolean");
if(settingsMap.containsKey("disable_scoring_boolean"))
        Alignment.al.scoreal.disable_scoring = (Boolean) settingsMap.get("disable_scoring_boolean");
if(settingsMap.containsKey("deleteResidues_boolean"))
        Alignment.al.deleteResidues= (Boolean)  settingsMap.get("deleteResidues_boolean");

//        select similar settings
if(settingsMap.containsKey("startingSimilarJump_double"))
         SimilarEngine.startingSimilarJump = (Double)  settingsMap.get("startingSimilarJump_double");
if(settingsMap.containsKey("startingSimilarBound_double"))
         SimilarEngine.startingSimilarBound = (Double)  settingsMap.get("startingSimilarBound_double");

//      sticky settings
if(settingsMap.containsKey("maxStickyGap_double"))
        Alignment.al.maxStickyGap = (Double)  settingsMap.get("maxStickyGap_double");
if(settingsMap.containsKey("maxStickyGap_double"))
        Alignment.al.minStickyMatch = (Double)  settingsMap.get("maxStickyGap_double");
if(settingsMap.containsKey("minStickySize_int"))
        Alignment.al.minStickySize = (Integer)  settingsMap.get("minStickySize_int");

//      scoring settings
if(settingsMap.containsKey("scoreGapExtension_int"))
        Sequence.scoreGapExtension = (Integer)  settingsMap.get("scoreGapExtension_int");
if(settingsMap.containsKey("scoreGapOpen_int"))
        Sequence.scoreGapOpen = (Integer)  settingsMap.get("scoreGapOpen_int");
if(settingsMap.containsKey("scoreTransition_int"))
        Sequence.scoreTransition = (Integer)  settingsMap.get("scoreTransition_int");
if(settingsMap.containsKey("scoreTransversion_int"))
        Sequence.scoreTransversion = (Integer)  settingsMap.get("scoreTransversion_int");
if(settingsMap.containsKey("scoreMatch_int"))
        Sequence.scoreMatch = (Integer)  settingsMap.get("scoreMatch_int");
if(settingsMap.containsKey("AASubstitution_int_2d"))
        Residue.AAsubstitution = (int[][])  settingsMap.get("AASubstitution_int_2d");

    }

	public static void saveSession(Alignment al, String md5)
	{Alignment.al.urt.rushEditToQueue();
		synchronized(Alignment.al.scoreal.scoreProcessingQueue)
		{
		
		long startTime = System.currentTimeMillis();
//		String md5 = al.toString();
    	File jarfile = null;
		//			System.out.println(RunEverything.class.getProtectionDomain().getCodeSource().getLocation().getPath());
//		jarfile = new File(RunEverything.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		 String path=ClassLoader.getSystemClassLoader().getResource(".").getPath();
		 jarfile = new File(path);
		 String parent = null;
		try {
			parent = URLDecoder.decode(jarfile.getAbsolutePath(),"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		 String pathToSaveFiles = parent+ File.separator + "config" + File.separator + "saved_sessions" + File.separator;
		 
		 File testd = new File(pathToSaveFiles);
			if(!testd.exists())
			{
				testd.mkdirs();
			}
			File mapfile = new File(pathToSaveFiles+md5+ ".map");
			if(mapfile.exists())
			{
				mapfile.delete();
			}
			try {
				mapfile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			File urtfile = new File(pathToSaveFiles+md5+ ".urt");
			if(urtfile.exists())
			{
				urtfile.delete();
			}
			try {
				urtfile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ObjectOutputStream oos = null;
			try {
				
				oos = new ObjectOutputStream(new FileOutputStream(mapfile.getAbsolutePath()));
				oos.writeObject(Alignment.al.getStateMap());
				oos.close();
				
				oos = new ObjectOutputStream(new FileOutputStream(urtfile.getAbsolutePath()));
				oos.writeObject(Alignment.al.urt);
				oos.close();
				
				
			
				System.out.println("Succesfully written");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				// TODO Auto-generated catch block
//				recentFiles =null;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		 
		 System.out.println("Session saving took " + (System.currentTimeMillis()-startTime));
		}

	}
	
	public static int[][] readSubstitutionMatrix(InputStream f)
	{
		Scanner sc = null;
		sc = new Scanner(f);
		
		ArrayList<Character> headermap = new ArrayList<Character>();
		boolean headerset = false;
		int [] [] outarr = null;
		int rowpos = 0;
		outerloop:
		while(sc.hasNext())
		{
			String line = sc.nextLine();
//			System.out.println(line); 
			Scanner linesc = new Scanner(line);
			linesc.useDelimiter(" ");
			int linepos =-1;
			boolean charstored = false;
			while(linesc.hasNext())
			{
				String word = linesc.next();
//				System.out.println(word);
				while(word.length()==0)
					word = linesc.next();
				if(word.charAt(0)=='#')
					break;
				if(!headerset)
				{
					headermap.add(word.charAt(0));
				}
				else if(linepos!=-1) //first char is the AA symbol
				{
					charstored=true;
					outarr[rowpos][linepos] = Integer.parseInt(word);
					
				}
				linepos++;

			}
			if(!headerset && headermap.size()>0)
			{
				headerset = true;
				outarr = new int[headermap.size()][headermap.size()];
			}
			if(charstored)
			rowpos++;


		}
		
		int [] [] orgarr = new int [Residue.ResidueType.BLANK.getInt()][Residue.ResidueType.BLANK.getInt()]; 
		for(int i = 0; i < orgarr.length;i++)
		for(int j = 0; j < orgarr.length;j++)
			orgarr[i][j] = -999;
		//outarr is reorganised according to the order of the header AA's
		for(int row = 0; row < outarr.length; row++)
		{
			for(int col = 0; col < outarr.length; col++)
			{
//				System.out.println(col);
				Residue rowres = new Residue(headermap.get(row));
				Residue colres = new Residue(headermap.get(col));
				orgarr[rowres.getType().getInt()][colres.getType().getInt()]=outarr[row][col];
			}
		}
		sc.close();
////		System.out.println("AAMATRIX-start");
//		for(int i = 0; i < orgarr.length;i++)
//		{System.out.println();
//		for(int j = 0; j < orgarr.length;j++)
//		{
////			System.out.print(orgarr[i][j] + " ");
//		}
//		}
////		System.out.println("AAMATRIX-end");
		return orgarr;

	}

    public static void translateDNA()
    {

        String path=ClassLoader.getSystemClassLoader().getResource(".").getPath();
        String jarDirPath = new File( path).getAbsolutePath();
        String outPath = jarDirPath+File.separator + "temp" + File.separator+"UNSAVED";
        lastFile=new File(outPath);


        Alignment.disposeCurrent();
        RunEverything re = new RunEverything(new File(outPath));
        re.run();
    }

    public static void writeFasta(final boolean new1, final Runnable doOnEnd)
    {
        writeFasta(new1,false,doOnEnd);
    }
	public static void writeFasta(final boolean new1, final boolean translate, final Runnable doOnEnd)
	{
		Alignment.al.stripTrailingBlanks();
		Runnable mainTask = new Runnable()
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		

//	String alstring = al.toString();
	File theFile = null;
	

	if(new1)
	{
//		SaveFileChooser sfc = new SaveFileChooser();
		theFile = AWTFileDialog.saveFile();
		lastFile = theFile;
		
	}
	else
	{
		theFile = lastFile;
	}
	ArrayList<String> recentFiles = getRecentFiles();

//save to recent files
	if(!lastFile.getName().equals("-.fas"))
	{
	try {
		recentFiles.remove(URLDecoder.decode(lastFile.getAbsolutePath(), "UTF-8"));
		recentFiles.add(0,URLDecoder.decode(lastFile.getAbsolutePath(), "UTF-8"));
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	}

MessageDigest md=null;
try {
	md = MessageDigest.getInstance("md5");
} catch (NoSuchAlgorithmException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
FileOutputStream fos = null;
BufferedWriter out = null;
		try {


//if(lastSeqWasProtein)
//	List arr = new ArrayList();
//	List<ProteinSequence> arr = new ArrayList<ProteinSequence>();
//else
//	List<DnaSequence> arr = new ArrayList<ProteinSequence>();

	FileWriter writer = new FileWriter(tempfile);
    long start = System.currentTimeMillis();
    int lastUpdate=-1;
    
    for (int i = 0; i < Alignment.al.size(); i++) 
    {
    	Sequence seq = Alignment.al.get(i);
    	
    	if(i*100/Alignment.al.size()!=lastUpdate)
		{
			lastUpdate= i*100/Alignment.al.size();
			final int currUpdate = lastUpdate;
			SwingUtilities.invokeLater(new Runnable()
			{

				@Override
				public void run() {
					
					Alignment.al.helpText.setText("Building save file... \n\nProgress: " + currUpdate + "%");
					// TODO Auto-generated method stub
					
				}
				
			});
		}
    	writer.write(">"+seq.name);
    	StringBuilder sb = new StringBuilder("");
        if(translate)
        {
            //TODO actually translate
        }
        else
    	for(int respos = 0; respos< Alignment.al.longestSeq; respos++)
    	{

    		if(respos%80==0)
    			sb.append("\n");
            else
            {
                sb.append(seq.get(respos).toString());
            }

	    		
    	}
    	final String currSeqStr = sb.toString();
    	md.update(currSeqStr.getBytes());
    	writer.write(currSeqStr);
    	writer.write("\n");
        
    }
    writer.flush();
    writer.close();
    long end = System.currentTimeMillis();
    System.out.println("SAVING TIME: " + (end - start) / 1000f + " seconds");

SwingUtilities.invokeLater(new Runnable()
{

@Override
public void run() {
	
	Alignment.al.helpText.setText("Saving session...");
	// TODO Auto-generated method stub
	
}

});

String hash = getMD5Hash(md);
System.out.println("hash:" + hash);
saveSession(Alignment.al,hash);
 

//Scanner sc = new 

//ReadseqTools.convertToFormat(al.format, theFile, tempfile);

if(Alignment.al.format!=8) //if its not already fasta
{
	SwingUtilities.invokeLater(new Runnable()
	{

		@Override
		public void run() {
			
			Alignment.al.helpText.setText("Converting back to original format...");
			// TODO Auto-generated method stub
			
		}
		
	});
ReadseqTools.convertToFormat(Alignment.al.format, tempfile, theFile);
}
else
{

	Files.deleteIfExists(theFile.toPath());
	Files.copy(tempfile.toPath(), theFile.toPath());
}

	System.out.println("starting");
	

	

		} 		catch(IOException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Something went wrong while writing to the fasta file. You may not have a saved file, but at least you have an apology.\n\nSorry.");
			
		}
		
		
	saveRecentFiles(recentFiles);

//System.out.println(al.columns);
// TODO Auto-generated method stub

}
		
	};
	
	Runnable guiUpdate = new Runnable()
	{

		@Override
		public void run() {
			
			if(IO.lastFile==null)
			{

				return;
			}
				
			Alignment.al.helpText.setText("This session has been successfully saved.");
			if(!IO.lastFile.getName().equals("-.fas"))
				RunEverything.currentRe.jf.setTitle(RunEverything.currentRe.titleBase+" : " + IO.lastFile.getName());
			else
				RunEverything.currentRe.jf.setTitle(RunEverything.currentRe.titleBase);
			if(doOnEnd!=null)
			doOnEnd.run();
			// TODO Auto-generated method stub
			
		}
		
	};
	
	WorkDispatcher.submit(mainTask, guiUpdate, "Saving Progress: ");
		
		
		

	
	
		
	}

//System.out.println("ARRAY SIZE " + al.size() + " " + al.get(0).size());
		//System.out.println(inseqs.keySet());




}


