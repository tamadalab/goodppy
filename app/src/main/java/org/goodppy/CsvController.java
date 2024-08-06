package org.goodppy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CSVの操作に関するクラス
 */
public class CsvController {
	/**
	 * csvファイルのファイルパスのString型
	 */
	private String csvFilePathString;

	/**
	 * 評価するリポジトリについて操作を行う
	 */
	private RepositoryController repositoryController;

	/**
	 * コンストラクタ
	 */
	public CsvController() {
		this.csvFilePathString = "./dependency_reports/"
				+ this.repositoryController.ownerAndRepositoryName()
				+ "/dependency-check-report.csv";

		return;
	}

	/**
	 * csvファイルのファイルパスのString型を取得する
	 * 
	 * @return csvファイルのファイルパスのString型
	 */
	public String getCsvFilePathString() {
		return this.csvFilePathString;
	}

	/**
	 * CSVを読み込む
	 */
	public Map<String, List<String>> readCsv() {
		Path csvFilePath = Paths.get(getCsvFilePathString());
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFilePath.toFile()))) {
			String line;
			String dependencyName;
			List<String> data;
			List<String> dependencyData = new ArrayList<String>();
			Map<String, List<String>> dependencies = new HashMap<>();
			while ((line = bufferedReader.readLine()) != null) {
				data = new ArrayList<String>(Arrays.asList(line.split(",")));
				dependencyName = data.get(2);
				dependencyData.add(data.get(12));
				dependencyData.add(data.get(17));
				dependencyData.add(data.get(18));
				dependencies.put(dependencyName, dependencyData);
			}

			return dependencies;
		} catch (IOException e) {
			e.printStackTrace();

			return null;
		}
	}
}
