/**
 * CS540-1 Assignment 1
 * Sek Cheong (sucheong@wisc.edu)
 * 
 * A kNN classification algorithm implementation.
 * 
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class KNN {

	/**
	 * In this method, you should implement the kNN algorithm. You can add other
	 * methods in this class, or create a new class to facilitate your work. If
	 * you create other classes, DO NOT FORGET to include those java files when
	 * preparing your code for hand in.
	 * 
	 * Also, Please DO NOT MODIFY the parameters or return values of this
	 * method, or any other provided code. Again, create your own methods or
	 * classes as you need them.
	 * 
	 * @param trainingData An Item array of training data
	 * @param testData An Item array of test data
	 * @param k	The number of neighbors to use for classification
	 * @return The object KNNResult contains classification accuracy, category assignment, etc.
	 */
	public KNNResult classify(Item[] trainingData, Item[] testData, int k) {
		KNNResult r = new KNNResult();
		r.categoryAssignment = new String[testData.length];
		r.nearestNeighbors = new String[testData.length][k];
		CategoryCounter cc = new CategoryCounter();
		Neighbor[] neighbors;

		for (int i = 0; i < testData.length; i++) {
			//System.out.printf("test[%d]=%s %s\n", i, testData[i].category, testData[i].name);
			neighbors = findKNN(trainingData, testData[i], k);
			for (int j = 0; j < k; j++) {
				r.nearestNeighbors[i][j] = neighbors[j].target.name;
				cc.vote(neighbors[j].target.category);
			}
			r.categoryAssignment[i] = cc.topCategory();
			//System.out.printf("test[%d].category=%s\n", i, r.categoryAssignment[i]);
			cc.clear();
		}

		int correct = 0;
		for (int i = 0; i < testData.length; i++) {
			if (r.categoryAssignment[i].compareTo(testData[i].category) == 0) {
				correct++;
			}
		}
		r.accuracy = (double) correct / (double) testData.length;
		return r;
	}

	
	/**
	 * Class to keep track of one of the neighboring points of the test point
	 */
	private class Neighbor implements Comparable<Neighbor> {
		public Item target;
		public double distance;

		public Neighbor(Item test, Item training) {
			distance = distance(test, training);
			target = training;
		}

		public int compareTo(Neighbor other) {
			if (this.distance > other.distance)	{return 1;}
			if (this.distance < other.distance) {return -1;}
			// System.out.printf("tie : %s %s %f - %s %s %f",this.target.category,
			int pSelf = getCatPriority(this.target.category);
			int pOther = getCatPriority(other.target.category);
			if (pSelf > pOther) {
				return 1;
			}
			if (pSelf < pOther) {
				return -1;
			}
			return 0;
		}
	}

	/*** 
	 * Calculates the Euclidean distance between p and q
	 * @param p point in feature space
	 * @param q point in feature space
	 * @return the distance between p and q
	 */
	private double distance(Item p, Item q) {
		double d = 0;
		for (int i = 0; i < p.features.length; i++) {
			d = d + Math.pow((p.features[i] - q.features[i]), 2);
		}
		d = Math.sqrt(d);
		return d;
	}

	/***
	 * Finds the nearest K neighbors of a given test point
	 * @param trainingData the set of training points
	 * @param test  the test point
	 * @param k the number of neighboring points
	 * @return an array of K nearest neighbors
	 */
	private Neighbor[] findKNN(Item[] trainingData, Item test, int k) {
		List<Neighbor> links = new ArrayList<Neighbor>();

		for (int i = 0; i < trainingData.length; i++) {
			links.add(new Neighbor(test, trainingData[i]));
		}

		Collections.sort(links);
		Neighbor[] knn = new Neighbor[k];

		for (int i = 0; i < k; i++) {
			knn[i] = links.get(i);
			// System.out.printf("knn[%d]= %s %s %f\n", i, knn[i].target.category, knn[i].target.name, knn[i].distance);
		}

		return knn;
	}

	private int getCatPriority(String cat) {
		if (cat.compareTo("nation") == 0) {
			return 0;
		}
		if (cat.compareTo("machine") == 0) {
			return 1;
		}
		if (cat.compareTo("fruit") == 0) {
			return 2;
		}
		return 0;
	}

	private String priorityToCat(int priority) {
		switch (priority) {
		case 0:
			return "nation";
		case 1:
			return "machine";
		case 2:
			return "fruit";
		}
		return null;
	}

	private class CategoryCounter {
		private int _counts[] = new int[3];

		public CategoryCounter() {
			this.clear();
		}

		public void vote(String category) {
			int index = getCatPriority(category);
			_counts[index]++;
		}

		public void clear() {
			for (int i = 0; i < _counts.length; i++) {
				_counts[i] = 0;
			}
		}

		public String topCategory() {
			int top = 0;
			int maxCount = _counts[0];
			for (int i = 0; i < _counts.length; i++) {
				if (_counts[i] > maxCount) {
					maxCount = _counts[i];
					top = i;
				}
			}
			return priorityToCat(top);
		}                                    

	}

}
