package com.bootvuedemo.entity;

import lombok.Data;

import java.util.List;

@Data
public class Menu {
	private String icon;
	private String index;
	private String title;
	private boolean hidden;//表示是否在导航栏展示的标志
	private List<Menu> children;


}
