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
		X.p("test 1:" + TKN.first("777-888-999", '-')); // 777
		X.p("test 2:" + TKN.firstGreedy("777-888-999", '-'));// 777-888

		X.p("test 3:" + TKN.last("777-888-999", '-')); // 999
		X.p("test 4:" + TKN.lastGreedy("777-888-999", '-')); // 888-999

		X.p("test 5:" + TKN.startWith("777-888-999", "777")); // '-888-999'
		X.p("test 6:" + TKN.endsWith("777-888-999", "999")); // '777-888-'


		//разделить строку на два токена по делиметру
		//
		X.p("test 7:" + ARR.as(TKN.two("777-888-999", "-")));// {"777","888-999"}
		X.p("test 8:" + ARR.as(TKN.twoGreedy("777-888-999", "-"))); // {"777-888","999"}

		//получаем токены по предикату
		//
		Predicate<Character> ISDIGIT = c -> Character.isDigit(c);

		//полуить первый/последний
		//
		X.p("test 9:" + TKN.first("7t", ISDIGIT)); //7
		X.p("test 10:" + TKN.last("t7", ISDIGIT)); //7

		//получить два токена - до и после предиакта
		//
		X.p("test 11:" + ARR.as(TKN.twoFirstPredicat("7t", ISDIGIT)));// ["7", "t"]
		X.p("test 12:" + ARR.as(TKN.twoLastPredicat("7t", ISDIGIT.negate())));// ["7", "t"]

	}
}
