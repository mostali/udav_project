package mpu.str;

import mpf.test.ZNViewAno;
import mpu.X;
import mpu.core.ARR;

import java.util.function.Predicate;

@ZNViewAno
public class USTokenView {

	public static void main(String[] args) {

		//откусить токен спереди/сзади по первом или последнему делиметру
		//
		X.p("test 1:" + USToken.first("777-888-999", '-')); // 777
		X.p("test 2:" + USToken.firstGreedy("777-888-999", '-'));// 777-888

		X.p("test 3:" + USToken.last("777-888-999", '-')); // 999
		X.p("test 4:" + USToken.lastGreedy("777-888-999", '-')); // 888-999

		X.p("test 5:" + USToken.startWith("777-888-999", "777")); // '-888-999'
		X.p("test 6:" + USToken.endsWith("777-888-999", "999")); // '777-888-'


		//разделить строку на два токена по делиметру
		//
		X.p("test 7:" + ARR.as(USToken.two("777-888-999", "-")));// {"777","888-999"}
		X.p("test 8:" + ARR.as(USToken.twoGreedy("777-888-999", "-"))); // {"777-888","999"}

		//получаем токены по предикату
		//
		Predicate<Character> ISDIGIT = c -> Character.isDigit(c);

		//полуить первый/последний
		//
		X.p("test 9:" + USToken.first("7t", ISDIGIT)); //7
		X.p("test 10:" + USToken.last("t7", ISDIGIT)); //7

		//получить два токена - до и после предиакта
		//
		X.p("test 11:" + ARR.as(USToken.twoFirstPredicat("7t", ISDIGIT)));// ["7", "t"]
		X.p("test 12:" + ARR.as(USToken.twoLastPredicat("7t", ISDIGIT.negate())));// ["7", "t"]

	}
}
