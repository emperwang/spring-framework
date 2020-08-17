package com.wk.basedemo.SqlSessionMuliExec;

public class Starter {
	public static void main(String[] args) {
		SqlSessionNoThreadSafe noThreadSafe = new SqlSessionNoThreadSafe();
		noThreadSafe.multiThreadExec();
	}
}
