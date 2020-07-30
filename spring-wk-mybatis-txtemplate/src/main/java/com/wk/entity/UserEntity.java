package com.wk.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
	private int id;
	private String name;
	private int age;
	private String address;

	public UserEntity(String name, int age, String address) {
		this.name = name;
		this.age = age;
		this.address = address;
	}

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
