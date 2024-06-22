package com.app.amnm_uc.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
import android.widget.DatePicker;
import android.widget.EditText;

import com.app.amnm_uc.b_health2.ActivityDoctorSlots;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public  class DatePickerFragment extends DialogFragment
implements DatePickerDialog.OnDateSetListener {

	EditText editText;
	public DatePickerFragment(EditText editText) {
		// TODO Auto-generated constructor stub
		this.editText = editText;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();

		//set calendar date otherwise current date will be set
		String dateStr = editText.getText().toString().trim();
		if(!TextUtils.isEmpty(dateStr)){
			try {
				Date date = new SimpleDateFormat(DATA.OLC_DATE_FMT).parse(dateStr);
				c.setTime(date);
			}catch (Exception e){ e.printStackTrace();}
		}

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), DatePickerDialog.THEME_HOLO_LIGHT, this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {

		month = month +1;

		//DATA.isDateSelected = true;
		//DATA.date = year + "-" + month + "-" + day;
		DATA.date =  month + "/" + day + "/" + year;

		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d/yyyy");
			Date d = simpleDateFormat.parse(DATA.date);
			//SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
			//SimpleDateFormat sdf = new SimpleDateFormat(sharedPrefsHelper.get("date_formate",Constants.DATE_FORMATE_DISPLAY));
			SimpleDateFormat sdf = new SimpleDateFormat(DATA.OLC_DATE_FMT, Locale.ENGLISH);
			DATA.date = sdf.format(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		editText.setText(DATA.date);
		if(getActivity() instanceof ActivityDoctorSlots){
			((ActivityDoctorSlots) getActivity()).getDoctorsSlots();
		}

	}
}