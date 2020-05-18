package com.wk.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
	private int id;
	private String name;
	private int age;
	private String address;

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", name='" + name + '\'' +
				", age=" + age +
				", address='" + address + '\'' +
				'}';
	}
}
