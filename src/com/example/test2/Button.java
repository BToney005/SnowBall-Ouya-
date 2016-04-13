package com.example.test2;

public enum Button{
	O(0), U(1), Y(2), A(3), D__DOWN(4), D_LEFT(5), D_UP(6), D_RIGHT(7), L1(8), L2(9), L3(10), R1(11), R2(12), R3(13), HOME(14);
	final int code;
	Button( int code ){
		this.code = code;
	}
}
