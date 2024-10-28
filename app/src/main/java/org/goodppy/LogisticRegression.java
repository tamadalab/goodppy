package org.goodppy;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;

import com.opencsv.CSVReader;

/**
 * ロジスティック回帰分析を行うクラス
 */
public class LogisticRegression {
	/**
	 * 学習データのcsvファイルパス
	 */
	private static final String CSV_FILE_PATH = "./src/main/resource/trainingData.csv";

	/**
	 * リポジトリのURL
	 */
	private String repositoryUrl;

	/**
	 * コンストラクタ
	 * 
	 * @param repositoryUrl
	 */
	public LogisticRegression(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	/**
	 * ロジスティック回帰分析で生存確率を計算する
	 */
	public void analyze(double dependencyScore, String[] gitHubDataList) {
		List<Double> analyzeTargets = new ArrayList<Double>();
		analyzeTargets.add(dependencyScore);
		for (int i = 1; i < gitHubDataList.length; i++) {
			if (i == 4 || i == 5) {
				continue;
			}
			analyzeTargets.add(Double.valueOf(gitHubDataList[i]));
		}
		for (double a : analyzeTargets)
			System.out.print(a + " ");
		System.out.println();
		double[][] trainingData = getTrainingData();
		// double[][] standardizedData = standerdizeData(trainingData);
		double[] label = getLabel();
		// double[] weights = getEstimatedBeta(standardizedData, label);
		double[] weights = getEstimatedBeta(trainingData, label);
		for (double a : weights)
			System.out.print(a + " ");
		System.out.println();
		double linearCombination = 0.0;
		linearCombination += weights[0];
		for (int i = 1; i < analyzeTargets.size() + 1; i++) {
			linearCombination += weights[i] * analyzeTargets.get(i - 1);
		}
		linearCombination += weights[weights.length - 1];
		double probability = sigmoid(linearCombination);
		System.out.println("生存確率: " + probability);

		return;
	}

	/**
	 * 尤度関数の損失を計算する
	 * 
	 * @param trainingData 学習データ
	 * @param label        ラベル
	 * @param weights      重み
	 * @return 尤度関数の損失
	 */
	public double computeLoss(double[][] trainingData, double[] label, double[] weights) {
		double loss = 0.0;
		for (int i = 0; i < trainingData.length; i++) {
			double z = weights[0]; // 切片
			for (int j = 0; j < trainingData[i].length; j++) {
				z += trainingData[i][j] * weights[j + 1];
			}
			double predicted = sigmoid(z);
			loss += label[i] * Math.log(predicted) + (1 - label[i]) * Math.log(1 - predicted);
		}
		return -loss;
	}

	/**
	 * 最適化してパラメータを推定する
	 * 
	 * @param trainingData 学習データ
	 * @param label        ラベル
	 * @return 最適化されたパラメータ
	 */
	public double[] getEstimatedBeta(double[][] trainingData, double[] label) {
		double[] initialBeta = new double[trainingData[0].length + 1];
		double[] lowerBound = new double[trainingData[0].length + 1];
		double[] upperBound = new double[trainingData[0].length + 1];
		for (int i = 0; i < initialBeta.length; i++) {
			initialBeta[i] = 0.0;
			lowerBound[i] = -20.0;
			upperBound[i] = 20.0;
		}
		Integer numberParameters = initialBeta.length;

		MultivariateOptimizer optimizer = new BOBYQAOptimizer(numberParameters + 2);
		PointValuePair result = optimizer.optimize(
				new MaxEval(1000000),
				new ObjectiveFunction(params -> computeLoss(trainingData, label, params)),
				new InitialGuess(initialBeta),
				new SimpleBounds(lowerBound, upperBound),
				GoalType.MINIMIZE);

		return result.getPoint();
	}

	/**
	 * ラベルを取得する
	 * 
	 * @return ラベル
	 */
	public double[] getLabel() {
		try (CSVReader reader = new CSVReader(new FileReader(CSV_FILE_PATH))) {
			List<Double> labelList = new ArrayList<Double>();
			String[] line;
			reader.readNext(); // ヘッダを除外
			while ((line = reader.readNext()) != null) {
				labelList.add(Double.valueOf(line[line.length - 1]));
			}
			double[] label = new double[labelList.size()];
			for (int i = 0; i < labelList.size(); i++) {
				label[i] = labelList.get(i);
			}

			return label;
		} catch (Exception e) {
			e.printStackTrace();

			return new double[0];
		}
	}

	/**
	 * リポジトリのURLを取得する
	 * 
	 * @return リポジトリのURL
	 */
	public String getRepositoryUrl() {
		return this.repositoryUrl;
	}

	/**
	 * 学習データを取得する
	 * 
	 * @return 学習データ
	 */
	public double[][] getTrainingData() {
		try (CSVReader reader = new CSVReader(new FileReader(CSV_FILE_PATH))) {
			List<double[]> trainingDataList = new ArrayList<double[]>();
			String[] line;
			reader.readNext(); // ヘッダを除外
			while ((line = reader.readNext()) != null) {
				double[] trainingDataRow = new double[line.length - 1];
				for (int i = 0; i < line.length - 1; i++) {
					trainingDataRow[i] = Double.valueOf(line[i]);
				}
				trainingDataList.add(trainingDataRow);
			}
			double[][] trainingData = new double[trainingDataList.size()][];
			trainingDataList.toArray(trainingData);

			return trainingData;
		} catch (Exception e) {
			e.printStackTrace();

			return new double[0][0];
		}
	}

	/**
	 * シグモイド関数
	 * 
	 * @param z 指数
	 * @return シグモイド関数
	 */
	private static final double sigmoid(double z) {
		return 1.0 / (1.0 + Math.exp(-z));
	}
}
