package gui;
//PLEASE BE HERE
//IF THIS IS HERE...

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;

import javax.swing.JOptionPane;
//nucleotideIndex['A'] = 0;
//nucleotideIndex['a'] = 0;
//nucleotideIndex['C'] = 1;
//nucleotideIndex['c'] = 1;
//nucleotideIndex['G'] = 2;
//nucleotideIndex['g'] = 2;
//nucleotideIndex['T'] = 3;
//nucleotideIndex['t'] = 3;
//nucleotideIndex['U'] = 4;
//nucleotideIndex['u'] = 4;
//nucleotideIndex['I'] = 5;
//nucleotideIndex['i'] = 5;
//nucleotideIndex['X'] = 6;
//nucleotideIndex['x'] = 6;
//nucleotideIndex['R'] = 7;
//nucleotideIndex['r'] = 7;
//nucleotideIndex['Y'] = 8;
//nucleotideIndex['y'] = 8;
//nucleotideIndex['N'] = 9;
//nucleotideIndex['n'] = 9;
public class Residue implements Comparable, Serializable {
    static RenderingHints hints;


	public static Residue blank;
	public static Color []  AAColorMap = {new Color(255,87,87), new Color(255,129,87), new Color(255,171,87), new Color(255,213,87), new Color(255,255,87), new Color(213,255,87), new Color(171,255,87), new Color(129,255,87), new Color(87,255,87), new Color(87,255,129), new Color(87,255,171), new Color(87,255,213), new Color(87,255,255), new Color(87,213,255), new Color(87,171,255), new Color(87,129,255), new Color(87,87,255), new Color(129,87,255), new Color(171,87,255), new Color(213,87,255), new Color(255,87,255), new Color(255,87,213), new Color(255,87,171), new Color(255,87,129)};
	public static Color [] AAInverseMap;
	static Color [] DNAinverses = new Color[18];
    private static float imageAccel = 1.0f;
    boolean unknownShown = false;
	static Color [] DNAcolors = new Color [18];
	  static int[][] DNAsubstitution = new int [17][17];
	  static int [] [] AAsubstitution;
    public static ArrayList<ArrayList<Integer>> ambiguityMap;
    public static EnumMap<ResidueType,Image> imageMap, imageSelectedMap, imageStickyMap,imageSelectedStickyMap;

    public String toTranslatedString() {
        return null;
    }

    //		  {
//		  	{ -1, -8, -8, -8, 1, 0, 0, 0, 0, 0, 1,0 }, // C
//		      { -8, 10, -8, -8, 1, 0, 0, 0, 0, 0, 1 }, // T
//		      { -8, -8, 10, -8, 1, 0, 0, 0, 0, 0, 1 }, // A
//		      { -8, -8, -8, 10, 1, 0, 0, 0, 0, 0, 1 }, // G
//		      { 1, 1, 1, 1, 10, 0, 0, 0, 0, 0, 1 }, // -
//		      { 1, 1, 1, 1, 1, 10, 0, 0, 0, 0, 1 }, // -
//		      { 1, 1, 1, 1, 1, 0, 10, 0, 0, 0, 1 }, // -
//		      { 1, 1, 1, 1, 1, 0, 0, 10, 0, 0, 1 }, // -
//		      { 1, 1, 1, 1, 1, 0, 0, 0, 10, 0, 1 }, // -
//		      { 1, 1, 1, 1, 1, 0, 0, 0, 0, 10, 1 }, // -
//		      { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, // -
//		  };
	static enum ResidueType {
	A, C, G, T, U, M, R, W, S, Y, K, V,H,D,B,X,N,L,I,F,P,Q,E,Z,ANY, BLANK;
	public String toString()
	{
		if (super.toString().equals("BLANK"))
			return "-";
		else if(super.toString().equals("ANY"))
			return "*";
		else
			return super.toString();
	}
	

	
	public int getInt()
	{
	
		
		switch(this)
		{
		case A:
			return 0;
		case C:
			return 1;
		case G:
			return 2;
		case T:
			return 3;
		case U:
			return 3;
		case M:
			return 4;
		case R:
			return 5;
		case W:
			return 6;
		case S:
			return 7;
		case Y:
			return 8;
		case K:
			return 9;
		case V:
			return 10;
		case H:
			return 11;
		case D:
			return 12;
		case B:
			return 13;
		case X:
			return 14;
		case N:
			return 15;
		case Q: 
			return 16;
		case E:
			return 17;
		case I:
			return 18;
		case L: 
			return 19;
		case F:
			return 20;
		case P:
			return 21;
		case Z:
			return 22;
		case ANY:
			return 23;
		case BLANK:
			return 24;
		default:
			return -1;
		
		}
		
	}
	}
	
	static int numResidues = ResidueType.values().length;
	
	private ResidueType res;
	//int [] location;
//	Color inverse, colour;
	
	public Residue.ResidueType getType()
	{
		return res;
	}
	
	public boolean isBlank()
	{
		if(res==Residue.ResidueType.BLANK)
			return true;
		return false;
	}
	public Residue(ResidueType type)
	{
		res = type;
//		colour= getColor();
//		inverse = new Color(Math.abs(colour.getRed() - 255),
//                Math.abs(colour.getGreen() - 255), Math.abs(colour.getBlue() - 255));
	}
	
	public boolean isOfType(Residue.ResidueType type)
	{
		if(res==type)
			return true;
		return false;
	}


	public Residue(char in)
	{
		in = Character.toUpperCase(in);
		//location = arr;
		//A, C, G, T, U, M, R, W, S, Y, K, V,H,D,B,X,N, BLANK;
		switch (in)
		{
		case 'A':
		res = ResidueType.A;
		break;
		
		case 'C':
		res = ResidueType.C;
		break;
		
		case 'G':
		res = ResidueType.G;
		break;
		
		case 'T':
		res = ResidueType.T;
		break;
		
		case 'U':
			res = ResidueType.U;
			break;
		case 'M':
			res = ResidueType.M;
			break;
		case 'R':
			res = ResidueType.R;
			break;
		case 'W':
			res = ResidueType.W;
			break;
		case 'S':
			res = ResidueType.S;
			break;
		case 'Y':
			res = ResidueType.Y;
			break;
		case 'K':
			res = ResidueType.K;
			break;
		case 'V':
			res = ResidueType.V;
			break;
		case 'H':
			res = ResidueType.H;
			break;
		case 'D':
			res = ResidueType.D;
			break;
		case 'B':
			res = ResidueType.B;
			break;
		case 'X':
			res = ResidueType.X;
			break;
		case 'Q':
			res = ResidueType.Q;
			break;
		case 'E':
			res = ResidueType.E;
			break;
		case 'I':
			res = ResidueType.I;
			break;
		case 'L':
			res = ResidueType.L;
			break;
		case 'F':
			res = ResidueType.F;
			break;
		case 'P':
			res = ResidueType.P;
			break;
		case 'Z':
			res = ResidueType.Z;
			break;
		case 'N':
			res = ResidueType.N;
			break;
		case '*':
			res = ResidueType.ANY;
			break;
		case '?':
			res = ResidueType.ANY;
			break;
			

		case '-':
		res = ResidueType.BLANK;
		break;
		

		
		
			
		default:
		{
			res = Residue.ResidueType.BLANK;
			if(!unknownShown)
			JOptionPane.showMessageDialog(null,"The sequence file contained an unknown residue: '" + in + "'. \nIt is recommended you correct this before proceeding with editing.");
			unknownShown = true;
			System.out.println("UNKNOWN" + in);
		} 
		}
//		colour= getColor();
//		inverse = new Color(Math.abs(colour.getRed() - 255),
//                Math.abs(colour.getGreen() - 255), Math.abs(colour.getBlue() - 255));
		
		
		
		
	}
	
	
	public Color getColor()
	{
//		if (res==ResidueType.A)
//		{
//			return Color.yellow;
//		}
//		else if(res==ResidueType.T)
//			return Color.cyan;
//		else if(res==ResidueType.C)
//			return Color.lightGray;
//		else if(res==ResidueType.G)
//			return Color.green;
//		else if(res == Residue.ResidueType.BLANK)
//			return Color.white;
//		else
//			return new Color(255, 0, 255);
		
		
		//this used to be it
		int resint = res.getInt();
		if(resint>=24)
			return  Color.white;
		if(Sequence.isProtein)
			return AAColorMap[res.getInt()];
		try{
		return DNAcolors[res.getInt()];
		}
		catch(Exception NullPointerException)
		{
			return AAColorMap[res.getInt()];
		}
	
	}
	
	public Color getInverse()
	{
		int resint = res.getInt();
		if(resint>=24)
			return  Color.BLACK;
		if(Sequence.isProtein)
			return AAInverseMap[res.getInt()];
		try{
		return DNAinverses[res.getInt()];
		}
		catch(Exception NullPointerException)
		{
			return AAInverseMap[res.getInt()];
		}
	}
	
	
	public String toString()
	{
		return res + "";
	}
	
	
	public boolean equals(Residue r)
	{
		if (r!=this)
			return false;
		return true;
	}


	public int scoreMatrix(Residue in)
	{
		
		if(Sequence.isProtein)
			return AAsubstitution[in.getType().getInt()][this.res.getInt()];
		return DNAsubstitution[in.getType().getInt()][this.res.getInt()];
	}
	@Override
	public int compareTo(Object o) {
		Residue r = (Residue) o;
		return (int) DNAsubstitution[r.getType().getInt()][this.res.getInt()];
		
		
		// TODO Auto-generated method stub
	}
	
	public static void buildDNASubMatrix(int match, int transition, int transversion)
	{

		//DEFINE base ACGT matrix
		DNAsubstitution[0][0] = match;
		DNAsubstitution[0][1] = transversion;
		DNAsubstitution[0][2] = transition;
		DNAsubstitution[0][3] = transversion;
		DNAsubstitution[1][2] = transversion;
		DNAsubstitution[1][3] = transition;
		DNAsubstitution[2][3] = transversion;
		DNAsubstitution[1][0] = transversion;
		DNAsubstitution[2][1] = transversion;
		DNAsubstitution[2][0] = transition;
		DNAsubstitution[3][0] = transversion;
		DNAsubstitution[3][1] = transition;
		DNAsubstitution[3][2] = transversion;
		for(int i = 0; i < 4; i ++)
		{
			DNAsubstitution[i][i] = match;
		}

		
		DNAcolors[0] = Color.cyan;
		DNAcolors[1] = Color.yellow;
		DNAcolors[2] = Color.green;
		DNAcolors[3] = new Color(255,182,193);
		DNAcolors[17] = Color.white;

		for(int i = 0; i <16; i++)
		{
			int r = 0;
			int g = 0;
			int b = 0;
			for(int j = 0; j < ambiguityMap.get(i).size(); j++)
			{
				r+=DNAcolors[ambiguityMap.get(i).get(j)].getRed();
				g+=DNAcolors[ambiguityMap.get(i).get(j)].getGreen();
				b+=DNAcolors[ambiguityMap.get(i).get(j)].getBlue();
			}
			r = r/ambiguityMap.get(i).size();
			g = g/ambiguityMap.get(i).size();
			b = b/ambiguityMap.get(i).size();
			
			DNAcolors[i] = new Color(r,g,b);
			
		}


        for(int i = 0; i < 16; i ++)
			for(int j = 0; j < 16;j ++)
			{
				int tot = 0;
				for(int x = 0; x < ambiguityMap.get(i).size(); x++)
					for(int y = 0; y < ambiguityMap.get(j).size(); y++)
					{
						try{
						tot+=DNAsubstitution[ambiguityMap.get(i).get(x)][ambiguityMap.get(j).get(y)];
						}
						catch(Exception e)
						{
							//System.out.println("WORKS");
							tot+=DNAsubstitution[ambiguityMap.get(j).get(y)][ambiguityMap.get(i).get(x)];
						}
					}
				tot = tot/(ambiguityMap.get(i).size()*ambiguityMap.get(j).size());
				DNAsubstitution[i][j] = tot;
//				System.out.println(i + "," + j + ":" + tot);
			}
		
		for(int i = 0; i < 16; i++)
		{
			DNAsubstitution[i][16] = Sequence.scoreGapOpen;
			DNAsubstitution[16][i] = Sequence.scoreGapOpen;
		}
		
		DNAinverses = new Color [DNAcolors.length];
		for(int i = 0; i < DNAcolors.length;i++)
		{
			try{
			DNAinverses[i]= new Color(Math.abs(DNAcolors[i].getRed() - 255),
	                Math.abs(DNAcolors[i].getGreen() - 255), Math.abs(DNAcolors[i].getBlue() - 255));
			}
			catch(NullPointerException ex)
			{
				
			}
		}
		
		
		AAInverseMap = new Color [AAColorMap.length];
		for(int i = 0; i < AAColorMap.length; i++)
		{
			AAInverseMap[i]= new Color(Math.abs(AAColorMap[i].getRed() - 255),
	                Math.abs(AAColorMap[i].getGreen() - 255), Math.abs(AAColorMap[i].getBlue() - 255));
		}
		
		
		
	}

    public static void buildAmbiguityMap()
    {
        ambiguityMap = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < 24; i++)
            ambiguityMap.add(new ArrayList<Integer>());
        ambiguityMap.get(0).add(0);
        ambiguityMap.get(1).add(1);
        ambiguityMap.get(2).add(2);
        ambiguityMap.get(3).add(3);
        ambiguityMap.get(4).add(0);
        ambiguityMap.get(4).add(1);
        ambiguityMap.get(5).add(0);
        ambiguityMap.get(5).add(2);
        ambiguityMap.get(6).add(0);
        ambiguityMap.get(6).add(3);
        ambiguityMap.get(7).add(1);
        ambiguityMap.get(7).add(2);
        ambiguityMap.get(8).add(1);
        ambiguityMap.get(8).add(3);
        ambiguityMap.get(9).add(2);
        ambiguityMap.get(9).add(3);
        ambiguityMap.get(10).add(0);
        ambiguityMap.get(10).add(1);
        ambiguityMap.get(10).add(2);
        ambiguityMap.get(11).add(0);
        ambiguityMap.get(11).add(1);
        ambiguityMap.get(11).add(3);
        ambiguityMap.get(12).add(0);
        ambiguityMap.get(12).add(2);
        ambiguityMap.get(12).add(3);
        ambiguityMap.get(13).add(1);
        ambiguityMap.get(13).add(2);
        ambiguityMap.get(13).add(3);
        ambiguityMap.get(14).add(0);
        ambiguityMap.get(14).add(1);
        ambiguityMap.get(14).add(2);
        ambiguityMap.get(14).add(3);
        ambiguityMap.set(15, ambiguityMap.get(14));
        ambiguityMap.set(23, ambiguityMap.get(14)); //it's any
    }
    
    public static void buildImageMaps()
    {

        hints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        imageMap = new EnumMap<ResidueType, Image>(ResidueType.class);
        imageSelectedMap = new EnumMap<ResidueType, Image>(ResidueType.class);
        imageStickyMap = new EnumMap<ResidueType, Image>(ResidueType.class);
        imageSelectedStickyMap = new EnumMap<ResidueType, Image>(ResidueType.class);
        int fontHeight = Alignment.al.panel.canvas.viewport.fontHeight;
        int fontWidth = Alignment.al.panel.canvas.viewport.fontWidth;
        int fontStartX = 1;
        int fontStartY = OSTools.retinaMultiplier*Alignment.al.panel.canvas.viewport.heightOffset;


        for(Residue.ResidueType type : Residue.ResidueType.values())
        {
                        Image curr = Alignment.al.panel.canvas.gfx_config.createCompatibleImage(OSTools.retinaMultiplier*fontWidth,OSTools.retinaMultiplier*fontHeight); 
            Graphics2D currG = (Graphics2D) curr.getGraphics();
            currG.setRenderingHints(hints);
            currG.setFont(Alignment.al.panel.canvas.font2.deriveFont(OSTools.retinaMultiplier*1f*Alignment.al.panel.canvas.font2.getSize()));
            //sticky
                Font oldfont = currG.getFont();
                currG.setFont(new Font(oldfont.getName(), oldfont.getStyle() + Font.BOLD, oldfont.getSize()));

            //selected
                currG.setColor(new Residue(type).getInverse());
                currG.fillRect(0,0, OSTools.retinaMultiplier*fontWidth, OSTools.retinaMultiplier*fontHeight);

            BufferedImage bi = Alignment.al.panel.canvas.gfx_config.createCompatibleImage(curr.getWidth(null),curr.getHeight(null));
            Graphics2D big = bi.createGraphics();
            big.setColor(currG.getColor());
            big.fillRect(0,0,curr.getWidth(null),curr.getHeight(null));
            currG.drawImage(bi,null,0,0);


            currG.setColor(Color.WHITE);
            oldfont = currG.getFont();
                currG.setFont(new Font(oldfont.getName(), oldfont.getStyle() + Font.ITALIC, oldfont.getSize()));


            //notSelected
//                currG.setColor(new Residue(type).getColor());
//                currG.fillRect(0,0, OSTools.retinaMultiplier*fontWidth, OSTools.retinaMultiplier*fontHeight);
//                currG.setColor(Color.BLACK);

            currG.drawString(new Residue(type).toString(), fontStartX, fontStartY);
            curr.setAccelerationPriority(imageAccel);

            imageSelectedStickyMap.put(type,curr);


        }

        for(Residue.ResidueType type : Residue.ResidueType.values())
        {
                        Image curr = Alignment.al.panel.canvas.gfx_config.createCompatibleImage(OSTools.retinaMultiplier*fontWidth,OSTools.retinaMultiplier*fontHeight); 
            Graphics2D currG = (Graphics2D) curr.getGraphics();
            currG.setRenderingHints(hints);
            currG.setFont(Alignment.al.panel.canvas.font2.deriveFont(OSTools.retinaMultiplier*1f*Alignment.al.panel.canvas.font2.getSize()));
            //sticky
            Font oldfont = currG.getFont();
            currG.setFont(new Font(oldfont.getName(), oldfont.getStyle() + Font.BOLD, oldfont.getSize()));

            //selected
//            currG.setColor(new Residue(type).getInverse());
//            currG.fillRect(0,0, OSTools.retinaMultiplier*fontWidth, OSTools.retinaMultiplier*fontHeight);
//            currG.setColor(Color.WHITE);
//            oldfont = currG.getFont();
//            currG.setFont(new Font(oldfont.getName(), oldfont.getStyle() + Font.ITALIC, oldfont.getSize()));


            //notSelected
                currG.setColor(new Residue(type).getColor());
                currG.fillRect(0,0, OSTools.retinaMultiplier*fontWidth, OSTools.retinaMultiplier*fontHeight);

            BufferedImage bi = Alignment.al.panel.canvas.gfx_config.createCompatibleImage(curr.getWidth(null),curr.getHeight(null));
            Graphics2D big = bi.createGraphics();
            big.setColor(currG.getColor());
            big.fillRect(0,0,curr.getWidth(null),curr.getHeight(null));
            currG.drawImage(bi,null,0,0);

            currG.setColor(Color.BLACK);

            currG.drawString(new Residue(type).toString(), fontStartX, fontStartY);
            curr.setAccelerationPriority(imageAccel);

            imageStickyMap.put(type,curr);


        }

        for(Residue.ResidueType type : Residue.ResidueType.values())
        {
                        Image curr = Alignment.al.panel.canvas.gfx_config.createCompatibleImage(OSTools.retinaMultiplier*fontWidth,OSTools.retinaMultiplier*fontHeight); 
            
            Graphics2D currG = (Graphics2D) curr.getGraphics();
            currG.setRenderingHints(hints);
            currG.setFont(Alignment.al.panel.canvas.font2.deriveFont(OSTools.retinaMultiplier*1f*Alignment.al.panel.canvas.font2.getSize()));
                        Font oldfont = currG.getFont();
//            //sticky
//            currG.setFont(new Font(oldfont.getName(), oldfont.getStyle() + Font.BOLD, oldfont.getSize()));

            //selected
            currG.setColor(new Residue(type).getInverse());
            currG.fillRect(0,0, OSTools.retinaMultiplier*fontWidth, OSTools.retinaMultiplier*fontHeight);

            BufferedImage bi = Alignment.al.panel.canvas.gfx_config.createCompatibleImage(curr.getWidth(null),curr.getHeight(null));
            Graphics2D big = bi.createGraphics();
            big.setColor(currG.getColor());
            big.fillRect(0,0,curr.getWidth(null),curr.getHeight(null));
            currG.drawImage(bi,null,0,0);

            currG.setColor(Color.WHITE);
            oldfont = currG.getFont();
            currG.setFont(new Font(oldfont.getName(), oldfont.getStyle() + Font.ITALIC, oldfont.getSize()));


            //notSelected
//                currG.setColor(new Residue(type).getColor());
//                currG.fillRect(0,0, OSTools.retinaMultiplier*fontWidth, OSTools.retinaMultiplier*fontHeight);
//                currG.setColor(Color.BLACK);

            currG.drawString(new Residue(type).toString(), fontStartX, fontStartY);
            curr.setAccelerationPriority(imageAccel);
            imageSelectedMap.put(type,curr);


        }

        for(Residue.ResidueType type : Residue.ResidueType.values())
        {
                        Image curr = Alignment.al.panel.canvas.gfx_config.createCompatibleImage(OSTools.retinaMultiplier*fontWidth,OSTools.retinaMultiplier*fontHeight); 
            Graphics2D currG = (Graphics2D) curr.getGraphics();
            currG.setRenderingHints(hints);
            currG.setFont(Alignment.al.panel.canvas.font2.deriveFont(OSTools.retinaMultiplier*1f*Alignment.al.panel.canvas.font2.getSize()));
            //sticky
//            Font oldfont = currG.getFont();
//            currG.setFont(new Font(oldfont.getName(), oldfont.getStyle() + Font.BOLD, oldfont.getSize()));

            //selected
//            currG.setColor(new Residue(type).getInverse());
//            currG.fillRect(0,0, OSTools.retinaMultiplier*fontWidth, OSTools.retinaMultiplier*fontHeight);
//            currG.setColor(Color.WHITE);
//            oldfont = currG.getFont();
//            currG.setFont(new Font(oldfont.getName(), oldfont.getStyle() + Font.ITALIC, oldfont.getSize()));


//            notSelected
            currG.setColor(new Residue(type).getColor());
            currG.fillRect(0,0, OSTools.retinaMultiplier*fontWidth, OSTools.retinaMultiplier*fontHeight);

            BufferedImage bi = Alignment.al.panel.canvas.gfx_config.createCompatibleImage(curr.getWidth(null),curr.getHeight(null));
            Graphics2D big = bi.createGraphics();
            big.setColor(currG.getColor());
            big.fillRect(0,0,curr.getWidth(null),curr.getHeight(null));
            currG.drawImage(bi,null,0,0);

            currG.setColor(Color.BLACK);
            currG.drawString(new Residue(type).toString(), fontStartX, fontStartY);
            curr.setAccelerationPriority(imageAccel);
            imageMap.put(type,curr);


        }
    }
    

}
