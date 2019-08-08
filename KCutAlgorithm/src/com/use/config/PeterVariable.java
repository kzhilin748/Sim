package com.use.config;

import java.io.InputStream;
import java.util.Scanner;

import com.use.scheduler.deploy.ASRG;
import com.use.scheduler.deploy.IPeterDeploy;
import com.use.utils.USESUtils;


public class PeterVariable implements IVariable {
	private static PeterVariable instance;
	private int VmNumber = 10;
	private int totalServiceNumber = 20;
	private int multipleCommunicationFactor = 1;
	private int multipleComputationFactor = 10;
	private int levelOneFork = 3;
	private int levelTwoFork = 2;
	private int totalDag = 10;
	private int rateFactor = 1000;
	private int interArrivalTimeSeed;
	private int interArrivalTimeMean;
	private int requestNumber;
	private IPeterDeploy deploy;
	private ASRG parallemMethod = null;
	private boolean usingRateFactor;
	private boolean balanceRequest;
	private boolean overwriteResult;
	private boolean cloneService;
	private boolean useRanking;
	private boolean regret;
	private int extraVMNumber;
	private boolean rankingQueue;

	public PeterVariable() throws Exception {
		instance = this;
		this.usingRateFactor = false;
		this.balanceRequest = false;
		this.overwriteResult = false;
		this.cloneService = false;
		this.useRanking = true;
		this.regret = false;
		this.extraVMNumber = 2;
		this.rankingQueue = true;
		this.interArrivalTimeSeed = 5;
		this.interArrivalTimeMean = 5;
		this.requestNumber = 1000;
		readConfig();
	}
	
	@Override
	public void readConfig() throws Exception {
		InputStream input = USESUtils.loadResource("args.conf");
		Scanner read = null;
		String delimiter = "=";
		if (input != null) {
			read = new Scanner(input);
		}
		while (read.hasNextLine()) {
			String str = read.nextLine().trim();
			if (str.split(delimiter).length <= 1) {
				continue;
			}
			String value = str.split(delimiter)[1].trim();
			if (str.startsWith("deployAlgorithm")) {
				deploy = Class.forName("com.use.scheduler.deploy." + value).asSubclass(IPeterDeploy.class).newInstance();
			}  
			else if (str.startsWith("parallemMethod")) {
				parallemMethod = Class.forName("com.use.scheduler.deploy." + value).asSubclass(ASRG.class).newInstance(); 
			}
			else if (str.startsWith("totalVM")) {
				VmNumber = Integer.parseInt(value);
			}
			else if (str.startsWith("totalService")) {
				totalServiceNumber = Integer.parseInt(value);
			}
			else if (str.startsWith("totalDag")) {
				totalDag = Integer.parseInt(value);
			}
			else if (str.startsWith("communicationFactor")) {
				multipleCommunicationFactor = Integer.parseInt(value);
			}
			else if (str.startsWith("computationFactor")) {
				multipleComputationFactor = Integer.parseInt(value);
			}
			else if (str.startsWith("levelOneFork")) {
				levelOneFork = Integer.parseInt(value);
			}
			else if (str.startsWith("levelTwoFork")) {
				levelTwoFork = Integer.parseInt(value);
			}
			else if (str.startsWith("rateFactor")) {
				rateFactor = Integer.parseInt(value);
			}
			else if (str.startsWith("usingRateFactor")) {
				usingRateFactor = Boolean.parseBoolean(value);
			}
			else if (str.startsWith("balanceRequest")) {
				balanceRequest = Boolean.parseBoolean(value);
			}
			else if (str.startsWith("overwriteResult")) {
				overwriteResult = Boolean.parseBoolean(value);
			}
			else if (str.startsWith("cloneService")) {
				cloneService = Boolean.parseBoolean(value);
			}
			else if (str.startsWith("useRanking")) {
				useRanking = Boolean.parseBoolean(value);
			}
			else if (str.startsWith("isRegret")) {
				regret = Boolean.parseBoolean(value);
			}
			else if (str.startsWith("extraVM")) {
				extraVMNumber = Integer.parseInt(value);
			}
			else if (str.startsWith("rankingQueue")) {
				rankingQueue = Boolean.parseBoolean(value);
			}
			else if (str.startsWith("interArrivalTimeMean")) {
				this.interArrivalTimeMean = Integer.parseInt(value);
			}
			else if (str.startsWith("interArrivalTimeSeed")) {
				this.interArrivalTimeSeed = Integer.parseInt(value);
			}
			else if (str.startsWith("requestNumber")) {
				this.requestNumber = Integer.parseInt(value);
			}
		}
		read.close();
	}

	public static PeterVariable getInstance() {
		return instance;
	}

	public int getVmNumber() {
		return this.VmNumber;
	}

	public int getTotalServiceNumber() {
		return this.totalServiceNumber;
	}

	public int getMultipleCommunicationFactor() {
		return this.multipleCommunicationFactor;
	}

	public int getMultipleComputationFactor() {
		return this.multipleComputationFactor;
	}

	public int getLevelOneFork() {
		return this.levelOneFork;
	}

	public int getLevelTwoFork() {
		return this.levelTwoFork;
	}

	public int getTotalDag() {
		return this.totalDag;
	}

	public IPeterDeploy getDeploy() {
		return deploy;
	}

	public ASRG getParallemMethod() {
		return parallemMethod;
	}

	public boolean isUsingRateFactor() {
		return usingRateFactor;
	}

	public boolean isBalanceRequest() {
		return balanceRequest;
	}

	public boolean isOverwriteResult() {
		return overwriteResult;
	}

	public int getRateFactor() {
		return rateFactor;
	}

	public boolean isCloneService() {
		return cloneService;
	}

	public boolean isUseRanking() {
		return useRanking;
	}

	public boolean isRegret() {
		return regret;
	}

	public boolean isExtraVM() {
		return extraVMNumber > 0;
	}
	
	public int getExtraVMNumber() {
		return extraVMNumber;
	}

	public boolean isRankingQueue() {
		return rankingQueue;
	}

	public int getInterArrivalTimeSeed() {
		return interArrivalTimeSeed;
	}

	public int getInterArrivalTimeMean() {
		return interArrivalTimeMean;
	}

	public int getRequestNumber() {
		return requestNumber;
	}

}
