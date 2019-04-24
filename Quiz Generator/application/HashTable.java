package application;
////////////////////ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
//Title:           p3a Hash Table
//Files:           HashTable.java, HashTableTest.java
//Course:          CS400 spring 2019
//Author:          JunYu Wang
//Email:           junyu.wang@wisc.edu
//Lecturer's Name: Deppeler
//Due Date:        10pm Thursday 3/14
//
////////////////////PAIR PROGRAMMERS COMPLETE THIS SECTION ///////////////////
//
//Partner Name:    (name of your pair programming partner)
//Partner Email:   (email address of your programming partner)
//Partner Lecturer's Name: (name of your partner's lecturer)
//
//VERIFY THE FOLLOWING BY PLACING AN X NEXT TO EACH TRUE STATEMENT:
//___ Write-up states that pair programming is allowed for this assignment.
//___ We have both read and understand the course Pair Programming Policy.
//___ We have registered our team prior to the team registration deadline.
//
///////////////////////////// CREDIT OUTSIDE HELP /////////////////////////////
//
//Persons:         None
//Online Sources:  None
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////

//import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

// TODO: comment and complete your HashTableADT implementation
// DO ADD UNIMPLEMENTED PUBLIC METHODS FROM HashTableADT and DataStructureADT TO YOUR CLASS
// DO IMPLEMENT THE PUBLIC CONSTRUCTORS STARTED
// DO NOT ADD OTHER PUBLIC MEMBERS (fields or methods) TO YOUR CLASS
//
// TODO: implement all required methods
//
// TODO: describe the collision resolution scheme you have chosen
// identify your scheme as open addressing or bucket
//
// TODO: explain your hashing algorithm here 
// NOTE: you are not required to design your own algorithm for hashing,
//       since you do not know the type for K,
//       you must use the hashCode provided by the <K key> object
//       and one of the techniques presented in lecture
//
public class HashTable<K extends Comparable<K>, V> implements HashTableADT<K, V> {
	
	// TODO: ADD and comment DATA FIELD MEMBERS needed for your implementation
		
	//private List<Integer> indexList; // set a List to add and remove the index of elements
	private int numbers; // the number of the element in the table
	private double LoadFactorThreshold; // the threshold of resize
	private int Capacity; // the Capacity of the hashtable
	private hashPair[] hashtable;
	private int longestp;
	
	
	private class hashPair<K,V>{ // the inner class of the nodes
		private K k;
		private V v;
		hashPair(K key, V value) {
			k = key;
			v = value;
		}
		private K getK() {
			return k;
		}
		private V getV() {
			return v;
		}
		
		private void setK(K key) {
			this.k = key;
			
		}
		
	}
	
	
	
	
	/**
	 * Set the Capacity, threshold and initialize the array 
	 */
	public HashTable() {
		numbers = 0;
		LoadFactorThreshold = 0.75;
		Capacity = 37;
		hashtable = new hashPair[Capacity];
		longestp = 0;
	//	indexList = new ArrayList<Integer>(Capacity);
	}
	
	// TODO: comment and complete a constructor that accepts 
	// initial capacity and load factor threshold
        // threshold is the load factor that causes a resize and rehash
	public HashTable(int initialCapacity, double loadFactorThreshold) {
		this.numbers = 0;
		this.Capacity = initialCapacity;
		this.LoadFactorThreshold = loadFactorThreshold;
		hashtable = new hashPair[Capacity];
	//	indexList = new ArrayList<Integer>(Capacity);
		longestp = 0;
	}

	
	/**
	 * Set the oldsize and oldindexList, oldhashtable
	 * update the Capacity and indexList, hashtable
	 * They use the index stored in the oldindexlist and the oldhashtable to insert them in the new hashtable
	 */
	public void rehash() {
	//	System.out.println(" old index size is " + indexList.size());
//		List<Integer> oldindexList;
//		oldindexList = new ArrayList<Integer>(Capacity);
//		for(int i = 0; i < indexList.size(); i ++) {
//			oldindexList.add(indexList.get(i));  // copy the indexlist
//			
//		}
		int oldsize = Capacity;
		Capacity = (2 * Capacity) + 1;// renew the Capacity
		//indexList = new ArrayList<Integer>(Capacity);// renew the indexlist to record new index
		
		
		hashPair[] oldhashtable = hashtable;
		hashPair[] newHtablePairs = new hashPair[Capacity];
		hashtable = newHtablePairs;
		numbers = 0;
		
	//	System.out.println("index size is " + oldindexList.size());
		for(int i = 0; i < oldsize; i ++) {
			try {
				if(oldhashtable[i] == null) continue;
				else if(oldhashtable[i].k == null) continue;
				else {
					hashPair hPair = oldhashtable[i];
					K keyK = (K) hPair.getK();
					V valueV = (V) hPair.getV();
					insert(keyK, valueV);
				}
			} catch (Exception e) {
				System.out.println("fail to re insert");
			}
		}
		
		
	}
	/* (non-Javadoc)
	 * First we find the index of the element
	 * if the index is empty, we can insert it directly
	 * 
	 */
	@Override
	public void insert(K key, V value) throws IllegalNullKeyException {
		int tempp = 0; // store the probing steps
		if (key == null) throw new IllegalNullKeyException();
		int hashcode = key.hashCode();
		if(hashcode < 0) hashcode = -hashcode; // handle the negtive index
		int index = hashcode % Capacity;
		//System.out.println("insert1");
		if(hashtable[index] == null) { 
			hashtable[index] = new hashPair(key, value);
			//indexList.add(index);
			
		}
		else {
			while(true) {
				if(hashtable[index] == null || hashtable[index].k == null ) break;// if we find that next new space or the space has been deleted
				else {
					tempp ++;
//					if(hashtable[index].÷k.equals(key)) throw new DuplicateKeyException();
					index ++;
					if(index >= Capacity) index = 0;
				}
			}
			hashtable[index] = new hashPair(key, value);
			//indexList.add(index);
			
		}
		if(tempp > longestp) longestp = tempp;
		numbers ++;
		if(getLoadFactor() >= getLoadFactorThreshold()) rehash();
		
	}
	
	private int find_helper(K key) {
		int hashcode = key.hashCode();
		if(hashcode < 0) hashcode = -hashcode; // handle the negtive index
		int index = hashcode % Capacity;
		int begin = index;// when the index search for the whole table, it stops
		if(hashtable[index] == null) return -1;// not exist
		while(hashtable[index] != null) {// may exist, some element may in its position, and there is a collision
			if(hashtable[index].k != null && hashtable[index].k.equals(key)) {
				
				return index;
			}
			else {
				index ++;
				if(index >= Capacity) index = 0;
				if(index > longestp+1) return -1;
			}
		}
		
		return -1;
	}
	/* (non-Javadoc)
	 * F
	 */
	@Override
	public boolean remove(K key) throws IllegalNullKeyException {
		if(key == null) throw new IllegalNullKeyException();
		int index = find_helper(key);
		if(index == -1) return false;
		else {// find
			hashtable[index].k = null;
			numbers --;
		//	indexList.remove(new Integer(index)); // delete the specific element//
		}
		
		return true;
	}

	@Override
	public V get(K key) throws IllegalNullKeyException, KeyNotFoundException {
		if(key == null) { 
			throw new IllegalNullKeyException();
		}
		int index = find_helper(key);// you find the index of the element
		if(index == -1) throw new KeyNotFoundException();
		else {
			return (V) hashtable[index].v;
		}
		
	}

	@Override
	public int numKeys() {
		// TODO Auto-generated method stub
		return numbers;
	}

	@Override
	public double getLoadFactorThreshold() {// return LoadFactorThreshold
		// TODO Auto-generated method stub
		return LoadFactorThreshold;
	}

	@Override
	public double getLoadFactor() {// return the loadfactor
		// TODO Auto-generated method stub
		return (double)numbers / Capacity;
	}

	@Override
	public int getCapacity() {
		// TODO Auto-generated method stub
		return Capacity;
	}

	@Override
	public int getCollisionResolution() {
		// TODO Auto-generated method stub
		return 1;
	}

	



		
}
