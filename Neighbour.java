
public class Neighbour {

	private char name;
	private int linkLength;
	private int secondLength;
	private int portN;
	
	public Neighbour(char aName, int aLength, int aPortN){
		
		this(aName, aLength, -1, aPortN);
	}
	
	public Neighbour(char aName, int aLength, int sLength, int aPortN){
		
		name = aName;
		linkLength = aLength;
		secondLength = sLength;
		portN = aPortN;
		
	}
	
	public char getName(){
		return name;
	}
	public int getLinkLength(){
		return linkLength;
	}
	public int getSecondLength(){
		return secondLength;
	}
	public int getPortNum(){
		return portN;
	}
}
