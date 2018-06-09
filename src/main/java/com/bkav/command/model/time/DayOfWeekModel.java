package com.bkav.command.model.time;

import java.time.DayOfWeek;

import com.bkav.command.model.ParseStringModel;

public class DayOfWeekModel extends ParseStringModel<DayOfWeek> {

	public DayOfWeekModel() {
		super();
		this.modelName = "DAY_OF_WEEK";
	}

	/***
	 * _exx
	 */
	@Override
	protected DayOfWeek createData(String word) {
		try {
			return DayOfWeek.of((Integer.parseInt(word.replaceFirst("_e(\\d)", "$1")) - 1));			
		} catch(Exception ex) {
			return null;
		}
	}
}
