package com.github.obsproth.prismkey.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;

import com.github.obsproth.prismkey.ServiceElement;

//
// INternal data of service, hash, pass Names
//
//
//
class ServiceTable {
    private List<ServiceElement> contents;
    //
    // default contructor
    // USAGE:
    //   ServiceTable tbl = new ServiceTable();
    //   tbl.load("<any filename>");
    //   // any operations ...
    public ServiceTable() {
        this.contents = new ArrayList<ServiceElement>();
    }
    //
    // create table with loading file
    // USAGE:
    //   ServiceTable tbl = new ServiceTable("<any filename>");
    //   // any operations ...
    public ServiceTable(String fileName) throws IOException {
        this();
        this.load(fileName);
    }


    public void load(String fileName) throws IOException {
        File file = new File(fileName);
		if (file.exists() && file.isFile()) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str;
			while ((str = br.readLine()) != null) {
				if (!str.isEmpty()) {
					this.contents.add(ServiceElement.buildFromCSV(str));
				}
			}
			br.close();
		} else {
            throw new FileNotFoundException("Not found:"+fileName);
        }
    }

    public void save(String fileName) {
        PrintWriter pw;
		try {
			pw = new PrintWriter(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		for (ServiceElement element : this.contents) {
			pw.print(element.asCSV());
			pw.println();
		}
		pw.close();
    }
    //
    // データとして保存しているリストを取得する
    //
    public List<ServiceElement> getContents() {
        return this.contents;
    }

    // サービス名に基づいた要素を線形探索で取得
    //
    public ServiceElement get(String name) {
        for (ServiceElement element : this.contents) {
			if (element.getServiceName().equals(name)) {
                return element;
            }
		}
        return null;
    }

    public void add(String name, int length, String baseHash, int algoVersion, List<String> config) {
        this.contents.add(new ServiceElement(name, length, baseHash, algoVersion, config));
    }

    // indexに対応した要素を取得q
    //
    public ServiceElement get(int i) {
        return this.contents.get(i);
    }

    public int size() {
        return this.contents.size();
    }
}