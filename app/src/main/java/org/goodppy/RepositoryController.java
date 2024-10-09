package org.goodppy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * 評価するリポジトリについて操作を行うクラス
 */
public class RepositoryController {

	/**
	 * クローン先のディレクトリのパス
	 */
	private Path localPath;

	/**
	 * リポジトリのオーナー名
	 */
	private String owner;

	/**
	 * リポジトリ名
	 */
	private String repositoryName;

	/**
	 * リポジトリのURL
	 */
	private String repositoryUrl;

	/**
	 * コンストラクタ
	 * 
	 * @param repositoryUrl リポジトリのURL
	 */
	public RepositoryController(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
		this.owner = makeUrlParts()[makeUrlParts().length - 2];
		this.repositoryName = makeUrlParts()[makeUrlParts().length - 1].replace(".git", "");
		this.localPath = Paths.get("./repositories/" + ownerAndRepositoryName()); // ./repositories/owner/repositoryName

		return;
	}

	/**
	 * リポジトリを削除する
	 * 
	 * @param directory ディレクトリ
	 */
	private static void deleteDirectory(File directory) {
		try {
			if (directory.isDirectory()) {
				File[] files = directory.listFiles();
				if (files != null) {
					for (File file : files) {
						deleteDirectory(file);
					}
				}
			}
			Files.delete(directory.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * クローン先のディレクトリのパスを取得する
	 * 
	 * @return クローン先のディレクトリのパス
	 */
	public Path getLocalPath() {
		return this.localPath;
	}

	/**
	 * リポジトリのオーナー名を取得する
	 * 
	 * @return リポジトリのオーナー名
	 */
	public String getOwner() {
		return this.owner;
	}

	/**
	 * リポジトリ名を取得する
	 * 
	 * @return リポジトリ名
	 */
	public String getRepositoryName() {
		return this.repositoryName;
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
	 * リポジトリをクローンする
	 */
	public void gitClone() {
		try {
			if (getLocalPath().toFile().exists()) {
				System.out.println("This repository is already clone.");
				System.out.println("This repository is deleting now...");
				deleteDirectory(getLocalPath().toFile());
				System.out.println("This repository has been deleted.");
			}
			System.out.println("Repository is being cloned...");
			Git.cloneRepository()
					.setURI(getRepositoryUrl())
					.setDirectory(getLocalPath().toFile())
					.call();
			System.out.println("Repository has been cloned.");
		} catch (GitAPIException e) {
			e.printStackTrace();
			System.out.println("Repository has been failed to clone.");
		}

		return;
	}

	/**
	 * リポジトリのURLを「/」で区切る
	 * 
	 * @return リポジトリのURLのパース結果
	 */
	public String[] makeUrlParts() {
		String[] urlParts = getRepositoryUrl().split("/");

		return urlParts;
	}

	/**
	 * オーナー名とリポジトリ名のファイルパスを出力する
	 * @return owner/repositoryName/
	 */
	public String ownerAndRepositoryName() {
		return getOwner() + "/" + getRepositoryName() + "/";
	}

	// public void setLocalPath(String localPath) {
	// this.localPath = localPath;

	// return;
	// }

	// public void setOwner(String owner) {
	// this.owner = owner;

	// return;
	// }

	// public void setRepositoryName(String repositoryName) {
	// this.repositoryName = repositoryName;

	// return;
	// }

	// public void setRepositoryUrl(String repositoryUrl) {
	// this.repositoryUrl = repositoryUrl;

	// return;
	// }
}
