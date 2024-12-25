package org.goodppy;

import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.opencsv.CSVReader;

/**
 * CSVの操作に関するクラス
 */
public class CsvController {
	/**
	 * csvファイルのファイルパスのString型
	 */
	private String csvFilePathString;

	/**
	 * 脆弱性のある依存関係を保持するもの
	 */
	private Multimap<String, List<String>> dependencies;

	/**
	 * 評価するリポジトリについて操作を行う
	 */
	private RepositoryController repositoryController;

	/**
	 * コンストラクタ
	 * @param repositoryUrl リポジトリのURL
	 */
	public CsvController(String repositoryUrl) {
		this.dependencies = ArrayListMultimap.create();
		this.repositoryController = new RepositoryController(repositoryUrl);
		this.csvFilePathString = new DependencyChecker(repositoryUrl).dependencyCheck(this.repositoryController.getLocalPath());

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
	 * Mapに依存関係に関する情報を追加する
	 * 
	 * @param dependencyName 脆弱性のあるライブラリ名
	 * @param dependencyData 脆弱性に関する情報
	 */
	public void putDependencies(String dependencyName, List<String> dependencyData) {
		this.dependencies.put(dependencyName, dependencyData);

		return;
	}

	/**
	 * csvファイルを読み込んで、脆弱性のある依存関係を返す
	 * 
	 * @return 脆弱性のある依存関係
	 */
	public Multimap<String, List<String>> readCsv() {
		Path csvFilePath = Paths.get(getCsvFilePathString());
		try (CSVReader csvReader = new CSVReader(new FileReader(csvFilePath.toFile()))) {
			csvReader.readNext();
			List<String[]> data = csvReader.readAll();
			data.forEach((line) -> {
				List<String> dependencyData = new ArrayList<String>();
				String dependencyName = line[2];// 脆弱性のあるライブラリ名
				dependencyData.add(line[12]);// 脆弱性の内容
				dependencyData.add(line[17]);// 脆弱性の深刻度
				dependencyData.add(line[18]);// 脆弱性のスコア
				putDependencies(dependencyName, dependencyData);
			});

			return this.dependencies;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ArrayListMultimap.create();
	}

/**
 * csvファイルを書き出す
 */
	public void writeCsv(){

		return;
	}
}
