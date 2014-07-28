package gui;

import gui.Residue.ResidueType;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public class Column extends EnumMap<Residue.ResidueType, MutableInt>
{
	/**
	 * 
	 */
//	static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
//        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
//            new Comparator<Map.Entry<K,V>>() {
//                @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
//                    int res = e1.getValue().compareTo(e2.getValue());
//                    return res != 0 ? res : 1; // Special fix to preserve items with equal values
//                }
//            }
//        );
//        sortedEntries.addAll(map.entrySet());
//        return sortedEntries;
//    }
	public int blanks, max;
	public Residue.ResidueType res;
	//Alignment al;
	private static final long serialVersionUID = 1L;
	public Column()
	{
		super(Residue.ResidueType.class);	
//		this.al = al;
		max = 0;
		blanks = 0;
		Residue.ResidueType [] types = Residue.ResidueType.values();
//		for(int i = 0; i < types.length; i++)
//		{
//			this.put(types[i], new AtomicInteger(0));
//		}
		
		//this.fi
		
	}
	
//	@Override
//	public Integer put(Residue.ResidueType key, Integer value)
//	{
//		
//		Integer ret = super.put(key, value);
//		
//		//ret.
////		if(max<value.intValue())
////			max = value.intValue();
////		if(key==Residue.ResidueType.BLANK)
////			blanks = value.get();
//		
//		return ret;
//			
//	}
//	

	
	public void increment(Residue.ResidueType r)
	{
		
//		int curr;

		MutableInt curr = super.get(r);
		if(curr ==null)
		{
			curr = new MutableInt();
			this.put(r, curr);
		}
		
		else
			curr.increment();
		if(curr.value>max)
		{
			this.res = r;
			max = curr.value;
		}
		
		
		
	
	}
	
	public void decrement(Residue.ResidueType r)
	{
		
		MutableInt curr = super.get(r);
		if(curr==null)
			System.out.println("whoa hold up");
		curr.decerement();

//			this.put(r, new MutableInt());
			
		
		if(res==r)
		{

			max = curr.value;
			if(max*2>Alignment.al.size())
			{
				check();
			}
			else
			{
				check();
			}
			
		}
		else
		{
		
		}
		
//		if((maxres.getType() ==r ))
//		{
//			it.next();
//			Entry<Residue.ResidueType, Integer> next = it.next();
//			if(next.getValue().intValue()<=curr)
//			{
//				ss.iterator().next().setValue(curr);
//				it = ss.iterator();
//
//			}
//			else
//			{
//				check();
//			}
//			
//		}
//		else
//		{
//			check();
//		}

		

	}
	
//	@Override
//	public MutableInt get(Object o)
//	{
//		MutableInt ret = super.get(o);
//		
//		if(ret==null)
//		{
//			ret = new MutableInt(0);
//			//super.put((Residue.ResidueType) o, ret);
//		}
//		return ret;
//
//	}

	public void check()
	{
		
		//returns {max count, blank count}
		for(Entry<Residue.ResidueType, MutableInt> entry : this.entrySet())
		{
			if(entry.getValue().value>max)
			{
				max = entry.getValue().value;
				res = entry.getKey();
			}
		}
		

		//return arr;

	}
	
	public String toString()
	{
		return "max:" + max + " blanks:" + blanks +super.toString();
	}
	
}
