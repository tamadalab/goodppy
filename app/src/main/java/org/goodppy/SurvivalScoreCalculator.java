package org.goodppy;

import java.util.ArrayList;
import java.util.List;

/**
 * 生存率の計算を行う
 */
public class SurvivalScoreCalculator {
	/**
	 * 依存関係のスコアにかかる重み
	 */
	private static final double W1 = 0.30;

	/**
	 * 最終コミット時間と現在時刻との差にかかる重み
	 */
	private static final double W2 = 0.25;

	/**
	 * Issueの解決度にかかる重み
	 */
	private static final double W3 = 0.20;

	/**
	 * 貢献者数にかかる重み
	 */
	private static final double W4 = 0.10;

	/**
	 * スター数にかかる重み
	 */
	private static final double W5 = 0.10;

	/**
	 * フォーク数にかかる重み
	 */
	private static final double W6 = 0.05;

	/**
	 * 依存関係のスコアの減衰速度
	 */
	private static final double A1 = 0.15;

	/**
	 * 最終コミット時間と現在時刻との差の減衰速度
	 */
	private static final double A2 = 1e-7;

	/**
	 * 貢献者数用のスケール
	 */
	private static final double B4 = 0.01;

	/**
	 * スター数用のスケール
	 */
	private static final double B5 = 0.001;

	/**
	 * フォーク数用のスケール
	 */
	private static final double B6 = 0.01;

	/**
	 * コンストラクタ
	 */
	public SurvivalScoreCalculator() {
	}

	public void calculateSurvivalRate(String[] gitHubDataList, Double dependencyScore) {
		List<Double> gitHubData = new ArrayList<Double>();
		for (int i = 1; i < gitHubDataList.length; i++) {
			if (i == 4 || i == 5) {
				continue;
			}
			gitHubData.add(Double.valueOf(gitHubDataList[i]));
		}

		Double dependencyScoreTerm = W1 * Math.exp(-A1 * dependencyScore);
		Double commitTimeDiffTerm = W2 * Math.exp(-A2 * gitHubData.get(4));
		Double ratioOfClosedIssuesTerm = W3 * gitHubData.get(3);
		Double contributorsTerm = W4 * Math.log(1 + B4 * gitHubData.get(2));
		Double starsTerm = W5 * Math.log(1 + B5 * gitHubData.get(0));
		Double forksTerm = W6 * Math.log(1 + B6 * gitHubData.get(1));

		Double survivalScore = 1 - Math.exp(
				-1.5 * (dependencyScoreTerm + commitTimeDiffTerm + ratioOfClosedIssuesTerm + contributorsTerm
						+ starsTerm + forksTerm));

		System.out.printf("Survival Score: %.4f%n", survivalScore);

		return;
	}

	public int checkWeight() {
		double W_all = W1 + W2 + W3 + W4 + W5 + W6;
		if (W_all != 1.0) {
			System.out.println("The total weight is not \"1\".");

			return -1;
		}

		return 0;
	}
}
