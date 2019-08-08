package com.use.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

import com.use.utils.USESUtils;
import com.use.workflow.grouping.IGrouping;
import com.use.workflow.ranking.IRanking;

public class DAGVariable implements IVariable {

	private static DAGVariable instance;
	private int numberOfWorkflow;
	private int randomSeed;
	private float communicationToComputationRatio;
	private int minComputationTime;
	private int maxComputationTime;
	private int maxInterArrivalTime;
	private int numberOfForkJoin;
	private int numberOfBranch;
	private int nodesForEachBranch;
	private int numberOfResource;
	private int numberOfExperiments;
	private int numberOfLevel;
	private int totalCPU;
	private int numberOfNodesPerLevel;
	private float fitnessWeight;
	private float EFTWeight;
	private float remainingTimeWeight;
	private String rankingMethod;
	private String groupingMethod;
	private double alpha;
	
	private IRanking ranking;
	private IGrouping grouping;
	
	
	public DAGVariable() throws Exception {
		instance = this;
		this.alpha=0.2;
		this.numberOfWorkflow = 1;
		this.randomSeed = 1;
		this.communicationToComputationRatio = 1;
		this.minComputationTime = 1;
		this.maxComputationTime = 30;
		this.maxInterArrivalTime = 30;
		this.numberOfForkJoin = 1;
		this.numberOfBranch = 2;
		this.nodesForEachBranch = 3;
		this.numberOfResource = 4;
		this.numberOfExperiments=1;
		this.EFTWeight = 1;
		this.numberOfLevel = 5;
		this.numberOfNodesPerLevel = 5;
		this.totalCPU=8;
		this.rankingMethod = "BottomAmountRank";
		this.groupingMethod = "PCHGrouping";
		readConfig();
		if (ranking == null) {
			ranking = Class.forName("com.use.workflow.ranking." + rankingMethod).asSubclass(IRanking.class).newInstance();
		}
		if (grouping == null) {
			grouping = Class.forName("com.use.workflow.grouping." + groupingMethod).asSubclass(IGrouping.class).newInstance();
		}
		
	}
	
	@Override
	public void readConfig() throws FileNotFoundException {
		File config = new File("resource/PCH.conf");
		InputStream input = USESUtils.loadResource("resource/DAG.conf");
		Scanner read = null;
		String delimiter = "=";

		if (!config.exists()) {
			if (input == null) {
				throw new FileNotFoundException("resource/DAG.conf not found");
			}
			else {
				read = new Scanner(input);
			}
		}
		if (read == null) {
			read = new Scanner(config);
		}
		while (read.hasNextLine()) {
			String str = read.nextLine();
			String value = str.split(delimiter)[1];
			if (str.startsWith("randomSeed")) {
				this.randomSeed = Integer.parseInt(value);
			}
			else if (str.startsWith("communicationToComputationRatio")) {
				this.communicationToComputationRatio = Float.parseFloat(value);
			}
			else if (str.startsWith("minComputationTime")) {
				this.minComputationTime = Integer.parseInt(value);
			}
			else if (str.startsWith("maxComputationTime")) {
				this.maxComputationTime = Integer.parseInt(value);
			}
			else if (str.startsWith("maxInterArrivalTime")) {
				this.maxInterArrivalTime = Integer.parseInt(value);
			}
			else if (str.startsWith("numberOfForkJoin")) {
				this.numberOfForkJoin = Integer.parseInt(value);
			}
			else if (str.startsWith("numberOfBranch")) {
				this.numberOfBranch = Integer.parseInt(value);
			}
			else if (str.startsWith("nodesForEachBranch")) {
				this.nodesForEachBranch = Integer.parseInt(value);
			}
			else if (str.startsWith("numberOfWorkflow")) {
				this.numberOfWorkflow = Integer.parseInt(value);
			}
			else if (str.startsWith("numberOfResource")) {
				this.totalCPU = Integer.parseInt(value);
			}
			else if (str.startsWith("numberOfExperiments")) {
				this.numberOfExperiments = Integer.parseInt(value);
			}
			else if (str.startsWith("rankingMethod")) {
				this.rankingMethod = value;
			}
			else if (str.startsWith("groupingMethod")) {
				this.groupingMethod = value;
			}
			else if (str.startsWith("fitnessWeight")) {
				this.fitnessWeight = Float.parseFloat(value);
			}
			else if (str.startsWith("EFTWeight")) {
				this.EFTWeight = Float.parseFloat(value);
			}
			else if (str.startsWith("remainingTimeWeight")) {
				this.remainingTimeWeight = Float.parseFloat(value);
			}
			else if (str.startsWith("numberOfLevel")) {
				this.numberOfLevel = Integer.parseInt(value);
			}
			else if (str.startsWith("numberOfNodesPerLevel")) {
				this.numberOfNodesPerLevel = Integer.parseInt(value);
			}
			
		}
		

	}

	public static DAGVariable getInstance() {
		return instance;
	}

	public int getRandomSeed() {
		return randomSeed;
	}

	public int getMinComputationTime() {
		return minComputationTime;
	}

	public int getMaxComputationTime() {
		return maxComputationTime;
	}

	public int getMaxInterArrivalTime() {
		return maxInterArrivalTime;
	}

	public int getNumberOfForkJoin() {
		return numberOfForkJoin;
	}

	public int getNumberOfBranch() {
		return numberOfBranch;
	}

	public int getNodesForEachBranch() {
		return nodesForEachBranch;
	}
	
	public int getNumberOfWorkflow() {
		return numberOfWorkflow;
	}
	
	public float getCommunicationToComputationRatio() {
		return communicationToComputationRatio;
	}

	public int getNumberOfResource() {
		return numberOfResource;
	}

	public int getNumberOfExperiments() {
		return numberOfExperiments;
	}

	public int getNumberOfLevel() {
		return numberOfLevel;
	}

	public int getNumberOfNodesPerLevel() {
		return numberOfNodesPerLevel;
	}

	public float getFitnessWeight() {
		return fitnessWeight;
	}

	public float getEFTWeight() {
		return EFTWeight;
	}

	public float getRemainingTime() {
		return remainingTimeWeight;
	}

	public IRanking getRanking() {
		return ranking;
	}

	public IGrouping getGrouping() {
		return grouping;
	}
	public int getRESNP(){
		return totalCPU;
	}
	public double getAlpha(){
		return alpha;
	}
	
}
